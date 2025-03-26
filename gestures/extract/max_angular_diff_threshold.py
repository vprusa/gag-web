import argparse
import mysql.connector
import numpy as np
from scipy.spatial.transform import Rotation as R
import pandas as pd


def connect_db(host, user, password, database):
    return mysql.connector.connect(
        host=host,
        user=user,
        password=password,
        database=database
    )


def fetch_gesture_data(conn, gestures, positions):
    query = f"""
    SELECT dl.id, dl.gesture_id, dl.position, fdl.quatA as qw, fdl.quatX as qx, fdl.quatY as qy, fdl.quatZ as qz
    FROM FingerDataLine fdl
    JOIN DataLine dl ON fdl.id = dl.id
    WHERE dl.gesture_id IN ({','.join(['%s'] * len(gestures))})
    AND dl.position IN ({','.join(['%s'] * len(positions))})
    ORDER BY dl.gesture_id, dl.id ASC
    """
    cursor = conn.cursor(dictionary=True)
    cursor.execute(query, gestures + positions)
    return pd.DataFrame(cursor.fetchall())


def main():
    parser = argparse.ArgumentParser(description="Calculate max angular difference across aligned gesture datalines.")
    parser.add_argument("--host", type=str, required=True)
    parser.add_argument("--user", type=str, required=True)
    parser.add_argument("--password", type=str, required=True)
    parser.add_argument("--database", type=str, required=True)
    parser.add_argument("--gestures", type=int, nargs='+', required=True)
    parser.add_argument("--positions", type=int, nargs='+', required=True)
    parser.add_argument("--rows", type=int, required=True)
    args = parser.parse_args()

    conn = connect_db(args.host, args.user, args.password, args.database)
    df = fetch_gesture_data(conn, args.gestures, args.positions)
    conn.close()

    if df.empty:
        print("❌ No data loaded.")
        return

    print("\n📦 Loaded Gesture Data:")
    print(df)

    max_thresholds = []

    for pos in args.positions:
        print(f"\n📍 Position {pos}")
        df_pos = df[df['position'] == pos]

        # Grouping gestures and storing their quaternions per gesture ID
        gesture_quats = {}
        for gid, group in df_pos.groupby('gesture_id', sort=False):
            if len(group) != args.rows:
                print(f"⚠️ Skipping gesture {gid} (expected {args.rows} rows, got {len(group)})")
                continue
            gesture_quats[gid] = group[['qw', 'qx', 'qy', 'qz']].to_numpy()

        if len(gesture_quats) < 2:
            print("❌ Not enough valid gestures for comparison.")
            continue

        gesture_ids = list(gesture_quats.keys())
        row_max_diffs = []
        gesture_total_diffs = {gid: 0.0 for gid in gesture_ids}

        # Iterate over each row index (same index across all gestures)
        for row_idx in range(args.rows):
            max_angle = -1
            max_pair = (None, None)

            # Compare same index quaternions across all gesture pairs
            for i in range(len(gesture_ids)):
                for j in range(i + 1, len(gesture_ids)):
                    g1 = gesture_quats[gesture_ids[i]][row_idx]
                    g2 = gesture_quats[gesture_ids[j]][row_idx]
                    r1 = R.from_quat([g1[1], g1[2], g1[3], g1[0]])
                    r2 = R.from_quat([g2[1], g2[2], g2[3], g2[0]])
                    angle_diff = (r1.inv() * r2).magnitude()

                    # Keep track of total difference for each gesture
                    gesture_total_diffs[gesture_ids[i]] += angle_diff
                    gesture_total_diffs[gesture_ids[j]] += angle_diff

                    # Track max diff and pair for this row
                    if angle_diff > max_angle:
                        max_angle = angle_diff
                        max_pair = (gesture_ids[i], gesture_ids[j])

            row_max_diffs.append(max_angle)
            print(f"Row {row_idx}: max angular difference = {max_angle:.6f} (between gestures {max_pair[0]} and {max_pair[1]})")

        global_max = max(row_max_diffs)
        print(f"\n🔥 Position {pos} - Maximum row difference = {global_max:.6f}")
        max_thresholds.append(global_max)

        # Identify gesture with highest total angular difference
        worst_gesture = max(gesture_total_diffs.items(), key=lambda x: x[1])
        print(f"\n🔎 Gesture with highest overall angular difference: {worst_gesture[0]} (sum = {worst_gesture[1]:.6f})")

    if max_thresholds:
        print(f"\n🧠 Global max threshold across all positions = {max(max_thresholds):.6f}")


if __name__ == "__main__":
    main()
