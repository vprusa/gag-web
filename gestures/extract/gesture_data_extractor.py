import mysql.connector
import numpy as np
import pandas as pd
import argparse
from scipy.spatial.transform import Rotation as R
from scipy.signal import argrelextrema
from datetime import datetime
from pprint import pprint
from tabulate import tabulate

# Command-line argument parser
def parse_arguments():
    parser = argparse.ArgumentParser(description="Extract and analyze quaternion data from MySQL.")

    parser.add_argument("--host", type=str, required=True, help="MySQL server host")
    parser.add_argument("--user", type=str, required=True, help="MySQL username")
    parser.add_argument("--password", type=str, required=True, help="MySQL password")
    parser.add_argument("--database", type=str, required=True, help="MySQL database name")
    parser.add_argument("--gesture_id", type=int, required=True, help="Gesture ID to filter data")
    parser.add_argument("--hand", type=int, required=False,
                        help="Optional: Hand to process (e.g., 0 for left, 1 for right). If not provided, all hands are processed.")
    parser.add_argument("--position", type=int, nargs="+", default=[0, 1, 2, 3, 4, 5],
                        help="List of sensor positions to use (default: all positions).")
    parser.add_argument("--threshold-extraction", type=float, default=0.1,
                        help="Threshold for filtering extreme points")
    parser.add_argument("--threshold-recognition", type=float, default=0.2,
                        help="Threshold for gesture recognition in DB")
    parser.add_argument("--suffix", type=str, required=False,
                        help="Optional: Suffix for new gesture name. If not provided, data will not be stored in the database.")
    parser.add_argument("--start", action="store_true",
                        help="Include the first quaternion for each position in the result.")
    parser.add_argument("--end", action="store_true",
                        help="Include the last quaternion for each position in the result.")
    parser.add_argument("--align", type=str, required=False,
                        help="align extracted data to the shortest per position.")

    return parser.parse_args()

# MySQL connection helper
def connect_db(host, user, password, database):
    try:
        return mysql.connector.connect(host=host, user=user, password=password, database=database)
    except mysql.connector.Error as err:
        print(f"❌ Database Connection Error: {err}")
        return None

# Fetch quaternion data for a specific `gesture_id`, filtering by `hand` and `position` if provided
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
        # pprint(df.head(num_to_show).to_dict(orient="records"))
        columns_to_display = ['gesture_id', 'position', 'timestamp', 'hand', 'angular_velocity', 'qx', 'qy', 'qz', 'qw']
        print(tabulate(df.head(num_to_show), headers=columns_to_display, tablefmt="pretty"))

    return df

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

# Compute angular velocity
# def compute_angular_velocity(df, align=None):
#     df = df.copy()
#     df['timestamp'] = pd.to_datetime(df['timestamp']).astype(np.int64) / 1e9  # Convert to seconds
#
#     results = []
#     min_group_size = min(df.groupby('position').size())  # Find the minimum group size across positions
#
#     for position in df['position'].unique():
#         group = df[df['position'] == position].copy()
#
#         if len(group) < 2:
#             print(f"⚠️ Not enough samples for position {position} (skipping)")
#             continue
#
#         # Align the group if 'align' is provided
#         if align == 'top':
#             group = group.head(min_group_size)  # Trim from the top
#         elif align == 'bottom':
#             group = group.tail(min_group_size)  # Trim from the bottom
#
#         quaternions = group[['qx', 'qy', 'qz', 'qw']].values
#         timestamps = group['timestamp'].values
#
#         rotations = R.from_quat(quaternions)
#         angular_velocities = [0]
#
#         for i in range(1, len(quaternions)):
#             delta_rotation = rotations[i - 1].inv() * rotations[i]
#             angle = 2 * np.arccos(np.clip(delta_rotation.as_quat()[-1], -1.0, 1.0))
#             dt = timestamps[i] - timestamps[i - 1]
#             angular_velocity = angle / dt if dt > 0 else 0
#             angular_velocities.append(angular_velocity)
#
#         group['angular_velocity'] = angular_velocities
#         results.append(group)
#
#     return pd.concat(results, ignore_index=True) if results else pd.DataFrame()


# Find extreme rotation changes
# def find_rotation_extremes(df, order=3, threshold=0.1):
#     df = df.copy()
#     results = []
#
#     for position in df['position'].unique():
#         group = df[df['position'] == position].copy()
#
#         if len(group) < 2:
#             print(f"⚠️ Not enough samples for position {position} (skipping)")
#             continue
#
#         angular_velocity = group['angular_velocity'].values
#         extreme_indices = argrelextrema(angular_velocity, np.greater, order=order)[0]
#         filtered_extremes = [idx for idx in extreme_indices if angular_velocity[idx] >= threshold]
#
#         if len(filtered_extremes) == 0:
#             print(f"⚠️ No extreme points detected for position {position}")
#
#         results.append(group.iloc[filtered_extremes])
#
#     return pd.concat(results, ignore_index=True) if results else pd.DataFrame()

# def find_rotation_extremes(df, order=3, threshold=0.1):
#     df = df.copy()
#     results = []
#     stats = []
#
#     for position in df['position'].unique():
#         group = df[df['position'] == position].copy()
#
#         if len(group) < 2:
#             print(f"⚠️ Not enough samples for position {position} (skipping)")
#             continue
#
#         angular_velocity = group['angular_velocity'].values
#         extreme_indices = argrelextrema(angular_velocity, np.greater, order=order)[0]
#         filtered_extremes = [idx for idx in extreme_indices if angular_velocity[idx] >= threshold]
#
#         if len(filtered_extremes) == 0:
#             print(f"⚠️ No extreme points detected for position {position}")
#         else:
#             # Extract extreme data
#             extreme_data = group.iloc[filtered_extremes]
#             mean_angular_velocity = extreme_data['angular_velocity'].mean()
#             std_angular_velocity = extreme_data['angular_velocity'].std()
#             min_angular_velocity = extreme_data['angular_velocity'].min()
#             max_angular_velocity = extreme_data['angular_velocity'].max()
#
#             # Store statistics for later use
#             stats.append({
#                 'Position': position,
#                 'Number of Extreme Points': len(filtered_extremes),
#                 'Mean Angular Velocity': mean_angular_velocity,
#                 'Std Dev Angular Velocity': std_angular_velocity,
#                 'Min Angular Velocity': min_angular_velocity,
#                 'Max Angular Velocity': max_angular_velocity
#             })
#
#             # Append the extreme data to the results list
#             results.append(extreme_data)
#
#     # Combine all results into a single DataFrame and return
#     df_results = pd.concat(results, ignore_index=True) if results else pd.DataFrame()
#
#     # Create a DataFrame for the statistics and print it as a table
#     if stats:
#         stats_df = pd.DataFrame(stats)
#         print("\n📊 Statistics for Extracted Extreme Points:")
#         print(stats_df.to_string(index=False))
#
#     return df_results

#
# def find_rotation_extremes(df, order=3, threshold=0.1, align=None):
#     df = df.copy()
#     results = []
#     stats = []
#
#     # Find the minimum group size across all positions
#     min_group_size = min(df.groupby('position').size())  # Find the smallest group size
#
#     for position in df['position'].unique():
#         group = df[df['position'] == position].copy()
#
#         if len(group) < 2:
#             print(f"⚠️ Not enough samples for position {position} (skipping)")
#             continue
#
#         # Align the group if 'align' is provided
#         if align == 'top':
#             group = group.head(min_group_size)  # Trim from the top
#         elif align == 'bottom':
#             group = group.tail(min_group_size)  # Trim from the bottom
#
#         angular_velocity = group['angular_velocity'].values
#         extreme_indices = argrelextrema(angular_velocity, np.greater, order=order)[0]
#         filtered_extremes = [idx for idx in extreme_indices if angular_velocity[idx] >= threshold]
#
#         if len(filtered_extremes) == 0:
#             print(f"⚠️ No extreme points detected for position {position}")
#         else:
#             # Extract extreme data
#             extreme_data = group.iloc[filtered_extremes]
#             mean_angular_velocity = extreme_data['angular_velocity'].mean()
#             std_angular_velocity = extreme_data['angular_velocity'].std()
#             min_angular_velocity = extreme_data['angular_velocity'].min()
#             max_angular_velocity = extreme_data['angular_velocity'].max()
#
#             # Store statistics for later use
#             stats.append({
#                 'Position': position,
#                 'Number of Extreme Points': len(filtered_extremes),
#                 'Mean Angular Velocity': mean_angular_velocity,
#                 'Std Dev Angular Velocity': std_angular_velocity,
#                 'Min Angular Velocity': min_angular_velocity,
#                 'Max Angular Velocity': max_angular_velocity
#             })
#
#             # Append the extreme data to the results list
#             results.append(extreme_data)
#
#     # Combine all results into a single DataFrame and return
#     df_results = pd.concat(results, ignore_index=True) if results else pd.DataFrame()
#
#     # Create a DataFrame for the statistics and print it as a table
#     if stats:
#         stats_df = pd.DataFrame(stats)
#         print("\n📊 Statistics for Extracted Extreme Points:")
#         print(stats_df.to_string(index=False))
#
#     return df_results
import pandas as pd
import numpy as np
from scipy.signal import argrelextrema

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


def add_start_end_quaternions(df, df_extremes, start, end):
    if df['timestamp'].dtype in ['float64', 'int64']:
        df['timestamp'] = pd.to_datetime(df['timestamp'], unit='s', errors='coerce')
    elif df['timestamp'].dtype == 'object':
        df['timestamp'] = pd.to_datetime(df['timestamp'], errors='coerce')

    df = df.dropna(subset=['timestamp'])
    df['timestamp'] = df['timestamp'].astype(np.int64) // 10**9

    sorted_rows = []
    first_rows = []
    last_rows = []

    unique_positions = df['position'].unique()
    grouped_data = {pos: df[df['position'] == pos].sort_values(by=['timestamp']).to_dict(orient="records") for pos in unique_positions}

    for pos, records in grouped_data.items():
        if not records:
            continue

        if start:
            first_rows.append(records[0])

        sorted_rows.extend(records)

        if end:
            last_rows.append(records[-1])

    final_result = []
    if start:
        final_result.extend(first_rows)
    df_extremes_sorted = df_extremes.sort_values(by=['timestamp']).to_dict(orient="records")
    final_result.extend(df_extremes_sorted)

    if end:
        final_result.extend(last_rows)

    df_final = pd.DataFrame(final_result)
    df_final['timestamp'] = pd.to_datetime(df_final['timestamp'], unit='s')
    return df_final

# Create a new gesture and return its ID
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

# Store extracted quaternions
def store_extracted_datalines(conn, df_extremes, new_gesture_id):
    cursor = conn.cursor()
    for _, row in df_extremes.iterrows():
        if isinstance(row['timestamp'], (int, float)):
            formatted_timestamp = datetime.utcfromtimestamp(row['timestamp']).strftime('%Y-%m-%d %H:%M:%S.%f')
        elif isinstance(row['timestamp'], str):
            formatted_timestamp = datetime.strptime(row['timestamp'], '%Y-%m-%d %H:%M:%S.%f').strftime('%Y-%m-%d %H:%M:%S.%f')
        else:
            formatted_timestamp = row['timestamp'].strftime('%Y-%m-%d %H:%M:%S.%f')

        cursor.execute(
            "INSERT INTO DataLine (hand, position, timestamp, gesture_id) VALUES (%s, %s, %s, %s);",
            (row['hand'], row['position'], formatted_timestamp, new_gesture_id)
        )
        new_datalign_id = cursor.lastrowid

        cursor.execute(
            "INSERT INTO FingerDataLine (accX, accY, accZ, quatA, quatX, quatY, quatZ, id) VALUES (0, 0, 0, %s, %s, %s, %s, %s);",
            (row['qw'], row['qx'], row['qy'], row['qz'], new_datalign_id)
        )

    conn.commit()


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


def print_extreme_rotation_points(df_extremes):
    if df_extremes.empty:
        print("⚠️ No extreme rotation points found.")
        return

    # Select the columns to display in the table
    columns_to_display = ['position', 'timestamp', 'hand', 'angular_velocity', 'qx', 'qy', 'qz', 'qw']

    # Ensure all necessary columns exist in the DataFrame
    missing_columns = [col for col in columns_to_display if col not in df_extremes.columns]
    if missing_columns:
        print(f"❌ Missing columns in the DataFrame: {', '.join(missing_columns)}")
        return

    # Prepare the DataFrame for display
    table_data = df_extremes[columns_to_display].values.tolist()

    # Print the table using tabulate
    print("\n📊 Extreme Rotation Points (with Velocities):")
    print(tabulate(table_data, headers=columns_to_display, tablefmt="pretty"))

# Main function
if __name__ == "__main__":
    args = parse_arguments()

    print("\n📌 **Command-line arguments:**")
    pprint(vars(args))

    conn = connect_db(args.host, args.user, args.password, args.database)
    if not conn:
        exit(1)

    num_to_show = len(args.position)

    df_quaternions = fetch_quaternion_data(conn, args.gesture_id, args.hand, args.position, num_to_show=num_to_show)

    if df_quaternions is not None:
        pprint(args.align)
        df_with_velocity = compute_angular_velocity(df_quaternions)
        # Assuming df_with_velocity is already computed and contains angular_velocity
        store_extreme_rotation_points_with_velocities(df_with_velocity)
        print(f"✅ df_with_velocity size: {len(df_with_velocity)} samples ")
        df_extremes = find_rotation_extremes(df_with_velocity, order=3, threshold=args.threshold_extraction, align=args.align)

        if not df_extremes.empty:
            print(f"\n✅ Extracted extreme rotation points:")
            print_extreme_rotation_points(df_extremes)
            # pprint(df_extremes.to_dict(orient="records"))
        else:
            print("\n⚠️ No extreme rotation points found.")

        df_final = add_start_end_quaternions(df_quaternions, df_extremes, args.start, args.end)
        df_final.to_csv("extreme_rotation_points.csv", index=False)
        print("Extremes:")
        columns_to_display = [ 'id', 'position', 'timestamp', 'qx', 'qy', 'qz', 'qw']
        print(tabulate(df_final, headers=columns_to_display, tablefmt="pretty"))

        print(f"✅ Extreme rotation points saved to 'extreme_rotation_points.csv' ({len(df_final)} samples)")

        if args.suffix:
            new_gesture_id = create_new_gesture(conn, df_quaternions.iloc[0]['userAlias'],
                                                int(df_quaternions.iloc[0]['user_id']), args.suffix,
                                                args.threshold_recognition)
            store_extracted_datalines(conn, df_final, new_gesture_id)
            print(f"✅ Stored {len(df_final)} extracted points under new gesture ID: {new_gesture_id}")

    conn.close()
