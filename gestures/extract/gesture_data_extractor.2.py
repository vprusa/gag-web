import argparse
import mysql.connector
import pandas as pd
import numpy as np
from datetime import datetime, timedelta, timezone
from scipy.spatial.transform import Rotation as R
from tabulate import tabulate
from scipy.signal import argrelextrema
import math
from pprint import pprint

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

def detect_rotation_extremes_datalines(df, df_extremes, angle_threshold_deg=10.0, include_start=False, include_end=False, align=False):
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
        if align.startswith(':'):
            semiresult = pd.concat(df_extremes).sort_values(by=['position', 'timestamp']).reset_index(drop=True)
        else:
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
                        sampled = group.reset_index(drop=True).iloc[[i for i in range(len(group)) if (i % math.ceil(len(group) / nth) == 0)]]
                        trimmed.append(sampled)
                elif align.startswith('xnth-top:'):
                    nth = int(align.split(':')[1])
                    grouped = semiresult.groupby('position')
                    min_size = min(len(group) for _, group in grouped)
                    for pos_value, group in grouped:
                        trimmed_group = group.reset_index(drop=True).iloc[:min_size]
                        sampled = trimmed_group.iloc[
                            [i for i in range(len(trimmed_group)) if i % max(1, math.ceil(min_size / nth)) == 0]]
                        trimmed.append(sampled)
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

              # Insert into WristDataLine if position is 5
        if row['position'] == 5:
            cursor.execute(
                "INSERT INTO WristDataLine (magX, magY, magZ, id) VALUES (%s, %s, %s, %s);",
                (0, 0, 0, new_dataline_id)
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


#Compute angular velocity
def compute_angular_velocity(df):
    df = df.copy()
    df['timestamp'] = pd.to_datetime(df['timestamp']).astype(np.int64) / 1e9  # Convert to seconds

    results = []

    for position in df['position'].unique():
        group = df[df['position'] == position].copy()

        if len(group) < 2:
            print(f"⚠️ Not enough samples for position {position} (skipping)")
            continue

        quaternions = group[['qx', 'qy', 'qz', 'qw']].values
        timestamps = group['timestamp'].values

        rotations = R.from_quat(quaternions)
        angular_velocities = [0]

        for i in range(1, len(quaternions)):
            delta_rotation = rotations[i - 1].inv() * rotations[i]
            angle = 2 * np.arccos(np.clip(delta_rotation.as_quat()[-1], -1.0, 1.0))
            dt = timestamps[i] - timestamps[i - 1]
            angular_velocity = angle / dt if dt > 0 else 0
            angular_velocities.append(angular_velocity)

        group['angular_velocity'] = angular_velocities
        results.append(group)

    return pd.concat(results, ignore_index=True) if results else pd.DataFrame()


def store_extreme_rotation_points_with_velocities(df_with_velocity,
                                                  filename='extreme_rotation_points_with_velocities.csv'):
    # Ensure that the DataFrame has necessary columns before saving
    if 'angular_velocity' not in df_with_velocity.columns:
        print("❌ DataFrame does not contain 'angular_velocity' column!")
        return

    # Select relevant columns including angular velocity
    columns_to_save = ['position', 'timestamp', 'hand', 'angular_velocity', 'qx', 'qy', 'qz', 'qw']

    # Filter only rows with extreme rotation points (i.e., angular velocity is above a threshold or calculated as extreme)
    extreme_df = df_with_velocity[
        df_with_velocity['angular_velocity'].notna()]  # Filter rows with non-NaN angular velocity

    # Save to CSV
    extreme_df[columns_to_save].to_csv(filename, index=False)

    print(f"✅ Extreme rotation points with velocities saved to '{filename}'")


def find_rotation_extremes(df, order=3, threshold=0.1, align=None):
    df = df.copy()
    results = []
    stats = []

    # Collect extreme data
    for position in df['position'].unique():
        group = df[df['position'] == position].copy()

        if len(group) < 2:
            print(f"⚠️ Not enough samples for position {position} (skipping)")
            continue

        angular_velocity = group['angular_velocity'].values
        extreme_indices = argrelextrema(angular_velocity, np.greater, order=order)[0]
        filtered_extremes = [idx for idx in extreme_indices if angular_velocity[idx] >= threshold]

        if len(filtered_extremes) == 0:
            print(f"⚠️ No extreme points detected for position {position}")
        else:
            # Extract extreme data
            extreme_data = group.iloc[filtered_extremes]
            mean_angular_velocity = extreme_data['angular_velocity'].mean()
            std_angular_velocity = extreme_data['angular_velocity'].std()
            min_angular_velocity = extreme_data['angular_velocity'].min()
            max_angular_velocity = extreme_data['angular_velocity'].max()

            # Store statistics for later use
            stats.append({
                'Position': position,
                'Number of Extreme Points': len(filtered_extremes),
                'Mean Angular Velocity': mean_angular_velocity,
                'Std Dev Angular Velocity': std_angular_velocity,
                'Min Angular Velocity': min_angular_velocity,
                'Max Angular Velocity': max_angular_velocity
            })

            # Append the extreme data to the results list
            results.append(extreme_data)

    # Combine all results into a single DataFrame
    df_results = pd.concat(results, ignore_index=True) if results else pd.DataFrame()

    # Apply alignment to the result data (if 'align' is provided)
    if align is not None and not df_results.empty:
        # Find the minimum number of extreme points in any position group
        min_group_size = min(df_results.groupby('position').size())

        # Trim extreme points based on the align parameter
        aligned_results = []
        for position in df_results['position'].unique():
            group = df_results[df_results['position'] == position].copy()

            # Trim the group based on alignment type
            if align == 'top':
                group = group.head(min_group_size)  # Trim from the top
            elif align == 'bottom':
                group = group.tail(min_group_size)  # Trim from the bottom
            elif align == 0:
                group = group.head(0)  # Exclude all data for this position
            else:
                group = group.head(int(align))
            aligned_results.append(group)

        # Combine aligned results into a single DataFrame
        df_results = pd.concat(aligned_results, ignore_index=True)

    # Create a DataFrame for the statistics and print it as a table
    if stats:
        stats_df = pd.DataFrame(stats)
        print("\n📊 Statistics for Extracted Extreme Points:")
        print(stats_df.to_string(index=False))

    return df_results

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

    df_with_velocity = compute_angular_velocity(df_quaternions)
    # Assuming df_with_velocity is already computed and contains angular_velocity
    store_extreme_rotation_points_with_velocities(df_with_velocity)
    print(f"✅ df_with_velocity size: {len(df_with_velocity)} samples ")
    df_extremes = find_rotation_extremes(df_with_velocity, order=3, threshold=args.threshold_extraction,
                                         align=args.align)

    threshold = estimate_threshold(df_quaternions, min_required=min_points) if args.threshold_extraction is None else args.threshold_extraction * 180
    print(f" threshold: {threshold}")
    df_final = detect_rotation_extremes_datalines(
        df_quaternions,
        df_extremes,
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
    #tests()
    main()

