import argparse
import mysql.connector
import pandas as pd
import numpy as np
from datetime import datetime, timedelta, timezone
from scipy.spatial.transform import Rotation as R
from tabulate import tabulate


def parse_arguments():
    parser = argparse.ArgumentParser(description="Extract and analyze quaternion data from MySQL.")

    parser.add_argument("--host", type=str, required=True, help="MySQL server host")
    parser.add_argument("--user", type=str, required=True, help="MySQL username")
    parser.add_argument("--password", type=str, required=True, help="MySQL password")
    parser.add_argument("--database", type=str, required=True, help="MySQL database name")
    parser.add_argument("--gesture_id", type=int, required=True, help="Gesture ID to filter data")
    parser.add_argument("--hand", type=int, required=False)
    parser.add_argument("--position", type=int, nargs="+", default=[0, 1, 2, 3, 4, 5],
                        help="List of sensor positions to use")
    parser.add_argument("--threshold-extraction", type=float, default=0.1)
    parser.add_argument("--threshold-recognition", type=float, default=0.2)
    parser.add_argument("--suffix", type=str, required=False)
    parser.add_argument("--start", action="store_true")
    parser.add_argument("--end", action="store_true")
    parser.add_argument("-v", action="store_true", help="Verbose: print all extracted datalines")
    parser.add_argument("--align", type=str, help="Trim per-position data using format top:n or bottom:n")
    parser.add_argument("--min-points", type=int, default=5, help="Minimum points per sensor to aim for when estimating threshold")

    return parser.parse_args()


def connect_db(host, user, password, database):
    try:
        return mysql.connector.connect(host=host, user=user, password=password, database=database)
    except mysql.connector.Error as err:
        print(f"❌ Database Connection Error: {err}")
        return None


def fetch_quaternion_data(conn, gesture_id, hand, positions, num_to_show=6):
    query = """
    SELECT 
        dl.id,
        dl.hand,
        dl.position,
        dl.timestamp, 
        fdl.quatA AS qw, 
        fdl.quatX AS qx, 
        fdl.quatY AS qy, 
        fdl.quatZ AS qz,
        g.id AS gesture_id,
        g.user_id,
        g.userAlias
    FROM 
        FingerDataLine fdl
    JOIN 
        DataLine dl ON fdl.id = dl.id
    JOIN
        Gesture g ON dl.gesture_id = g.id
    WHERE dl.gesture_id = %s
    """

    params = [gesture_id]

    if hand is not None:
        query += " AND dl.hand = %s"
        params.append(hand)

    if positions:
        placeholders = ", ".join(["%s"] * len(positions))
        query += f" AND dl.position IN ({placeholders})"
        params.extend(positions)

    query += " ORDER BY dl.timestamp ASC;"

    cursor = conn.cursor(dictionary=True)
    cursor.execute(query, params)
    data = cursor.fetchall()

    df = pd.DataFrame(data) if data else None
    if df is not None and not df.empty:
        print(f"\n✅ First {num_to_show} retrieved quaternions:")
        print(tabulate(df.head(num_to_show), headers="keys", tablefmt="pretty"))

    return df


def detect_rotation_extremes_datalines(df, angle_threshold_deg=10.0, include_start=False, include_end=False, align=False):
    if df is None or df.empty:
        return pd.DataFrame()

    angle_threshold_rad = np.radians(angle_threshold_deg)
    all_extremes = []

    # Step 1: detect extremes per position
    for pos in df['position'].unique():
        df_pos = df[df['position'] == pos].reset_index()
        quats = df_pos[['qx', 'qy', 'qz', 'qw']].values
        n = len(quats)
        extremes = []

        if n > 1:
            rot = R.from_quat(quats)
            for i in range(1, n):
                dq = rot[i - 1].inv() * rot[i]
                if np.linalg.norm(dq.as_rotvec()) > angle_threshold_rad:
                    extremes.append(i)

        if extremes:
            all_extremes.append(df_pos.loc[sorted(set(extremes))])

    if not all_extremes:
        return pd.DataFrame()

    # Step 2: combine extremes
    semiresult = pd.concat(all_extremes).sort_values(by=['position', 'timestamp']).reset_index(drop=True)

    # Step 3: apply --align to extremes
    if isinstance(align, str):
        trimmed = []
        for pos in semiresult['position'].unique():
            df_pos = semiresult[semiresult['position'] == pos]
            if align.startswith('top:'):
                n = int(align.split(':')[1])
                trimmed.append(df_pos.head(n))
            elif align.startswith('bottom:'):
                n = int(align.split(':')[1])
                trimmed.append(df_pos.tail(n))
            elif align.startswith('middle:'):
                n = int(align.split(':')[1])
                mid = len(df_pos) // 2
                half = n // 2
                start = max(mid - half, 0)
                trimmed.append(df_pos.iloc[start:start + n])
            elif align.startswith('nth:'):
                nth = int(align.split(':')[1])
                grouped = semiresult.groupby('position')
                for pos_value, group in grouped:
                    sampled = group.reset_index(drop=True).iloc[[i for i in range(len(group)) if i % nth == 0]]
                    trimmed.append(sampled)
            elif align.startswith('xnth:'):
                nth = int(align.split(':')[1])
                grouped = semiresult.groupby('position')
                for pos_value, group in grouped:
                    sampled = group.reset_index(drop=True).iloc[[i for i in range(len(group)) if i % (len(group) / nth) == 0]]
                    trimmed.append(sampled)
                print("not implemented")

        semiresult = pd.concat(trimmed).sort_values(by=['position', 'timestamp']).reset_index(drop=True)

    # Step 4: add --start and --end if requested
    result = []
    for pos in df['position'].unique():
        df_pos = df[df['position'] == pos].sort_values(by='timestamp').reset_index()
        df_ext = semiresult[semiresult['position'] == pos]
        rows = []

        if include_start and not df_pos.empty:
            rows.append(df_pos.iloc[0])
        rows += [r for _, r in df_ext.iterrows()]
        if include_end and not df_pos.empty:
            rows.append(df_pos.iloc[-1])

        result.append(pd.DataFrame(rows))

    return pd.concat(result).drop_duplicates().sort_values(by=['position', 'timestamp']).reset_index(drop=True)



def create_new_gesture(conn, old_gesture_name, user_id, suffix, threshold_recognition):
    new_gesture_name = f"{old_gesture_name}-{suffix}"
    query = """
    INSERT INTO Gesture (dateCreated, delay, exec, isActive, isFiltered, shouldMatch, userAlias, user_id)
    VALUES (NOW(), 1, NULL, 1, 1, %s, %s, %s);
    """
    cursor = conn.cursor()
    cursor.execute(query, (threshold_recognition, new_gesture_name, user_id))
    conn.commit()
    return cursor.lastrowid


def store_extracted_datalines(conn, df_extremes, new_gesture_id):
    cursor = conn.cursor()
    df_extremes = df_extremes.sort_values(by='timestamp')  # ensure chronological insert order
    for _, row in df_extremes.iterrows():
        formatted_timestamp = row['timestamp']
        if isinstance(formatted_timestamp, (int, float)):
            formatted_timestamp = datetime.utcfromtimestamp(formatted_timestamp).strftime('%Y-%m-%d %H:%M:%S.%f')
        elif isinstance(formatted_timestamp, str):
            formatted_timestamp = datetime.strptime(formatted_timestamp, '%Y-%m-%d %H:%M:%S.%f').strftime(
                '%Y-%m-%d %H:%M:%S.%f')
        else:
            formatted_timestamp = row['timestamp'].strftime('%Y-%m-%d %H:%M:%S.%f')

        cursor.execute(
            "INSERT INTO DataLine (hand, position, timestamp, gesture_id) VALUES (%s, %s, %s, %s);",
            (row['hand'], row['position'], formatted_timestamp, new_gesture_id)
        )
        new_dataline_id = cursor.lastrowid

        cursor.execute(
            "INSERT INTO FingerDataLine (accX, accY, accZ, quatA, quatX, quatY, quatZ, id) VALUES (0, 0, 0, %s, %s, %s, %s, %s);",
            (row['qw'], row['qx'], row['qy'], row['qz'], new_dataline_id)
        )

    conn.commit()


def build_test_df(quaternions_by_position):
    rows = []
    timestamp = datetime.now(timezone.utc)
    idx = 0
    for pos, quats in quaternions_by_position.items():
        for q in quats:
            rows.append({
                'position': pos,
                'timestamp': timestamp + timedelta(milliseconds=idx * 10),
                'qx': q[0], 'qy': q[1], 'qz': q[2], 'qw': q[3],
                'hand': 0
            })
            idx += 1
    return pd.DataFrame(rows)


def tests():
    print("\nRunning tests...")
    q0 = [0, 0, 0, 1]
    q1 = [np.sin(np.pi / 4), 0, 0, np.cos(np.pi / 4)]
    q2 = [0, np.sin(np.pi / 4), 0, np.cos(np.pi / 4)]

    def run_test_case(name, df, expected_len, include_start=False, include_end=False, align=False):
        try:
            result = detect_rotation_extremes_datalines(df, angle_threshold_deg=10.0,
                                                        include_start=include_start,
                                                        include_end=include_end,
                                                        align=align)
            actual_len = len(result)
            print(f"\nTest: {name}\nExpected: {expected_len}, Got: {actual_len}")
            if actual_len != expected_len:
                print("❌ Mismatch! Result:", result[['position', 'timestamp', 'qx', 'qy', 'qz', 'qw']])
            else:
                print("✅ Passed.")
        except Exception as e:
            print(f"💥 Exception in test {name}: {e}")

    run_test_case("1 line 1 pos", build_test_df({0: [q0]}), 1, include_start=True)
    run_test_case("2 lines 1 pos, second change", build_test_df({0: [q0, q1]}), 1)
    run_test_case("2 lines 1 pos, first change", build_test_df({0: [q1, q0]}), 1)
    run_test_case("3 lines, change in middle", build_test_df({0: [q0, q1, q0]}), 2)
    run_test_case("5 lines, two changes", build_test_df({0: [q0, q1, q1, q2, q2]}), 2)
    run_test_case("5 lines, two changes + start/end", build_test_df({0: [q0, q1, q1, q2, q2]}), 4, include_start=True,
                  include_end=True)
    run_test_case("2 positions, 1 change each", build_test_df({0: [q0, q1], 1: [q0, q1]}), 2)
    run_test_case("2 pos, 3 lines each, middle change", build_test_df({0: [q0, q1, q0], 1: [q0, q1, q0]}), 4)


def estimate_threshold(df, min_required=5, start=None, end=None, tol=0.1):
    print(f"🔍 Estimating threshold for at least {min_required} extremes per sensor...")
        # Estimate sensible bounds from max quaternion differences across positions
    if start is None or end is None:
        max_diff = 0
        for pos in df['position'].unique():
            df_pos = df[df['position'] == pos].sort_values(by='timestamp')
            quats = df_pos[['qx', 'qy', 'qz', 'qw']].values
            if len(quats) < 2:
                continue
            rot = R.from_quat(quats)
            for i in range(1, len(rot)):
                dq = rot[i - 1].inv() * rot[i]
                angle = np.degrees(np.linalg.norm(dq.as_rotvec()))
                max_diff = max(max_diff, angle)
        start = max_diff
        end = 0.1
    low, high = end, start
    while high - low > tol:
        mid = (high + low) / 2.0
        print(f"  Trying threshold: {round(mid, 3)}°")
        result = detect_rotation_extremes_datalines(df, angle_threshold_deg=mid)
        if result.empty or 'position' not in result.columns:
            break
        counts = result['position'].value_counts()
        if all(counts[pos] >= min_required for pos in df['position'].unique()):
            high = mid
        else:
            low = mid
            final = round(high, 3)
    print(f"🎯 Estimated threshold: {final}°")
    return final

def main():
    args = parse_arguments()
    conn = connect_db(args.host, args.user, args.password, args.database)
    if not conn:
        return

    df_quaternions = fetch_quaternion_data(conn, args.gesture_id, args.hand, args.position)
    if df_quaternions is None or df_quaternions.empty:
        print("❌ No data found for given gesture.")
        return
    min_points = args.min_points
    threshold = estimate_threshold(df_quaternions, min_required=min_points) if args.threshold_extraction is None else args.threshold_extraction * 180
    print(f" threshold: {threshold}")
    df_final = detect_rotation_extremes_datalines(
        df_quaternions,
        angle_threshold_deg=threshold,
        include_start=args.start,
        include_end=args.end,
        align=args.align
    )

    print(f"✅ Extracted {len(df_final)} important quaternion datalines.")
    if args.v and not df_final.empty:
        print("📋 Extracted Datalines:")
        print(tabulate(df_final[['position', 'timestamp', 'qx', 'qy', 'qz', 'qw']], headers="keys", tablefmt="pretty"))

    if args.suffix:
        new_gesture_id = create_new_gesture(conn, df_quaternions.iloc[0]['userAlias'],
                                            int(df_quaternions.iloc[0]['user_id']), args.suffix,
                                            args.threshold_recognition)
        store_extracted_datalines(conn, df_final, new_gesture_id)
        print(f"✅ Stored {len(df_final)} extracted points under new gesture ID: {new_gesture_id}")

    conn.close()


if __name__ == "__main__":
    tests()
    main()

