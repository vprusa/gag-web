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
    ORDER BY dl.gesture_id, dl.timestamp ASC
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
        # df_pos = df[df['position'] == pos]
        df_pos = df[df['position'] == pos].sort_values(by=["gesture_id", "id"])

        gesture_quats = {}
        for gid, group in df_pos.groupby('gesture_id'):
            if len(group) != args.rows:
                print(f"⚠️ Skipping gesture {gid} (expected {args.rows} rows, got {len(group)})")
                continue
            gesture_quats[gid] = group[['qw', 'qx', 'qy', 'qz']].to_numpy()

        if len(gesture_quats) < 2:
            print("❌ Not enough valid gestures for comparison.")
            continue

        gesture_ids = sorted(gesture_quats.keys())
        row_max_diffs = []

        for row_idx in range(args.rows):
            diffs = []
            for i in range(len(gesture_ids)):
                for j in range(i + 1, len(gesture_ids)):
                    g1 = gesture_quats[gesture_ids[i]][row_idx]
                    g2 = gesture_quats[gesture_ids[j]][row_idx]
                    r1 = R.from_quat([g1[1], g1[2], g1[3], g1[0]])
                    r2 = R.from_quat([g2[1], g2[2], g2[3], g2[0]])
                    angle_diff = (r1.inv() * r2).magnitude()
                    diffs.append(angle_diff)
            max_diff = max(diffs)
            row_max_diffs.append(max_diff)
            print(f"Row {row_idx}: max angular difference = {max_diff:.6f}")

        global_max = max(row_max_diffs)
        print(f"\n🔥 Position {pos} - Maximum row difference = {global_max:.6f}")
        max_thresholds.append(global_max)

    if max_thresholds:
        print(f"\n🧠 Global max threshold across all positions = {max(max_thresholds):.6f}")

if __name__ == "__main__":
    main()
