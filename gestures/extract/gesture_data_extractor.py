import mysql.connector
import numpy as np
import pandas as pd
import argparse
from scipy.spatial.transform import Rotation as R
from scipy.signal import argrelextrema
from datetime import datetime
from pprint import pprint


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

    return parser.parse_args()


# MySQL connection helper
def connect_db(host, user, password, database):
    try:
        return mysql.connector.connect(host=host, user=user, password=password, database=database)
    except mysql.connector.Error as err:
        print(f"❌ Database Connection Error: {err}")
        return None


# Fetch quaternion data for a specific `gesture_id`, filtering by `hand` and `position` if provided
def fetch_quaternion_data(conn, gesture_id, hand, positions):
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
        print("\n✅ First 6 retrieved quaternions:")
        pprint(df.head(6).to_dict(orient="records"))  # Display first 6 samples

    return df


# Compute angular velocity
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


# Find extreme rotation changes
def find_rotation_extremes(df, order=3, threshold=0.1):
    df = df.copy()
    results = []

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

        results.append(group.iloc[filtered_extremes])

    return pd.concat(results, ignore_index=True) if results else pd.DataFrame()


# Add start and end quaternions if requested
# def add_start_end_quaternions(df, df_extremes, start, end):
#     result = []
# 
#     if start:
#         start_rows = df.groupby('position').first().reset_index()
#         result.append(start_rows)
# 
#     result.append(df_extremes)
# 
#     if end:
#         end_rows = df.groupby('position').last().reset_index()
#         result.append(end_rows)
# 
#     return pd.concat(result, ignore_index=True)
# def add_start_end_quaternions(df, df_extremes, start, end):
#     """
#     Adds first and/or last quaternions for each position to the final dataset.
#
#     :param df: Original DataFrame with all quaternions
#     :param df_extremes: Extracted extreme rotation quaternions
#     :param start: Boolean, if True, add first quaternions
#     :param end: Boolean, if True, add last quaternions
#     :return: Combined DataFrame with added start/end quaternions
#     """
#     result = []
#
#     # Ensure data is sorted by timestamp before grouping
#     df_sorted = df.sort_values(by="timestamp")
#
#     if start:
#         if 'position' in df_sorted.columns:
#             start_rows = df_sorted.groupby('position', as_index=False).first()
#             print(f"✅ Added {len(start_rows)} start quaternions.")
#             result.append(start_rows)
#         else:
#             print("⚠️ No 'position' column found, cannot add start quaternions.")
#
#     if not df_extremes.empty:
#         result.append(df_extremes)
#     else:
#         print("⚠️ No extreme points detected, skipping extraction data.")
#
#     if end:
#         if 'position' in df_sorted.columns:
#             end_rows = df_sorted.groupby('position', as_index=False).last()
#             from pprint import pprint
#             pprint (df_sorted)
#             print(f"✅ Added {len(end_rows)} end quaternions.")
#             result.append(end_rows)
#         else:
#             print("⚠️ No 'position' column found, cannot add end quaternions.")
#
#     if result:
#         return pd.concat(result, ignore_index=True)
#     else:
#         print("⚠️ No data available to concatenate, returning empty DataFrame.")
#         return pd.DataFrame()
# def add_start_end_quaternions(df, df_extremes, start, end):
#     """
#     Adds first and/or last quaternions for each position to the final dataset while maintaining order.
#
#     :param df: Original DataFrame with all quaternions
#     :param df_extremes: Extracted extreme rotation quaternions
#     :param start: Boolean, if True, add first quaternions
#     :param end: Boolean, if True, add last quaternions
#     :return: Combined DataFrame with added start/end quaternions
#     """
#     result = []
#
#     # Ensure data is sorted by timestamp before extracting start/end quaternions
#     df_sorted = df.sort_values(by="timestamp").copy()
#
#     if start:
#         start_rows = df_sorted.loc[df_sorted.groupby("position").head(1).index]  # First row per position
#         print(f"✅ Added {len(start_rows)} start quaternions.")
#         result.append(start_rows)
#
#     if not df_extremes.empty:
#         result.append(df_extremes)
#     else:
#         print("⚠️ No extreme points detected, skipping extraction data.")
#
#     if end:
#         pprint(df_sorted)
#         end_rows = df_sorted.loc[df_sorted.groupby("position").tail(1).index]  # Last row per position
#         print(f"✅ Added {len(end_rows)} end quaternions.")
#         result.append(end_rows)
#
#     if result:
#         return pd.concat(result, ignore_index=True)
#     else:
#         print("⚠️ No data available to concatenate, returning empty DataFrame.")
#         return pd.DataFrame()

# def add_start_end_quaternions(df, df_extremes, start, end):
#     """
#     Adds first and/or last quaternions for each position to the final dataset.
#
#     :param df: Original DataFrame with all quaternions
#     :param df_extremes: Extracted extreme rotation quaternions
#     :param start: Boolean, if True, add first quaternions
#     :param end: Boolean, if True, add last quaternions
#     :return: Combined DataFrame with added start/end quaternions
#     """
#     result = []
#     pos_dl = list(df.groupby("position", group_keys=False))
#     pprint(pos_dl)
#     pos_dl_sorted = list(pos_dl.apply(lambda x: x.sort_values(by="timestamp")))
#     pprint(pos_dl_sorted)
#     if start:
#         start_rows = pos_dl_sorted.head(1)
#         print(f"✅ Added {len(start_rows)} start quaternions.")
#         result.append(start_rows)
#
#     if not df_extremes.empty:
#         result.append(df_extremes)
#     else:
#         print("⚠️ No extreme points detected, skipping extraction data.")
#
#     if end:
#         end_rows = pos_dl_sorted.tail(1)
#         print(f"✅ Added {len(end_rows)} end quaternions.")
#         result.append(end_rows)
#
#     if result:
#         return pd.concat(result, ignore_index=True)
#     else:
#         print("⚠️ No data available to concatenate, returning empty DataFrame.")
#         return pd.DataFrame()

# def add_start_end_quaternions(df, df_extremes, start, end):
#     """
#     Adds the first and/or last quaternion from each sensor position before/after extracted data.
#
#     :param df: Original DataFrame with quaternions
#     :param df_extremes: DataFrame with extracted extreme rotation points
#     :param start: Boolean flag to include the first quaternion per position
#     :param end: Boolean flag to include the last quaternion per position
#     :return: Concatenated DataFrame with preserved timestamp order
#     """
#     result = []
#     pprint(df)
#     # Ensure df is sorted by timestamp before extracting head/tail
#
#     if start:
#         df_grouped = df.groupby('position', group_keys=False)
#         df_sorted = df_grouped.sort_values(by=['timestamp'], ascending=True).
#
#         result.append(start_rows)
#
#     result.append(df_extremes)
#
#     if end:
#         end_rows = df_sorted.groupby('position', group_keys=False).apply(lambda x: x.tail(1))
#         result.append(end_rows)
#
#     # Concatenate and restore order by timestamp
#     df_final = pd.concat(result, ignore_index=True).sort_values(by=['timestamp'], ascending=True)
#
#     return df_final


# def add_start_end_quaternions(df, df_extremes, start, end):
#     """
#     Adds the first and/or last quaternion from each sensor position before/after extracted data.
#     This function manually groups and sorts data to ensure correct order.
#
#     :param df: Original DataFrame with quaternions
#     :param df_extremes: DataFrame with extracted extreme rotation points
#     :param start: Boolean flag to include the first quaternion per position
#     :param end: Boolean flag to include the last quaternion per position
#     :return: Concatenated DataFrame with preserved timestamp order
#     """
#     result = []
#
#     # Ensure all timestamps are converted to `datetime` format
#     if df['timestamp'].dtype in ['float64', 'int64']:
#         df['timestamp'] = pd.to_datetime(df['timestamp'], unit='s', errors='coerce')  # Convert Unix timestamps
#     elif df['timestamp'].dtype == 'object':  # If stored as a string
#         df['timestamp'] = pd.to_datetime(df['timestamp'], errors='coerce')
#
#     # Handle NaN timestamps (remove rows with missing timestamps)
#     df = df.dropna(subset=['timestamp'])
#
#     # Convert timestamps to numeric values for sorting
#     df['timestamp'] = df['timestamp'].astype(np.int64)  # Convert to nanoseconds
#
#     # Sort manually by looping over each `position`
#     sorted_rows = []
#     first_rows = []
#     last_rows = []
#
#     unique_positions = df['position'].unique()
#
#     for pos in unique_positions:
#         group = df[df['position'] == pos].copy()
#         group = group.sort_values(by=['timestamp'], ascending=True)
#
#         if start and not group.empty:
#             first_rows.append(group.iloc[0])  # Select first row for this position
#
#         sorted_rows.append(group)  # Store sorted group
#
#         if end and not group.empty:
#             last_rows.append(group.iloc[-1])  # Select last row for this position
#
#     # Concatenate manually collected rows
#     if start:
#         result.extend(first_rows)
#
#     result.extend(sorted_rows)
#
#     if end:
#         result.extend(last_rows)
#
#     # Convert result back to DataFrame
#     df_final = pd.DataFrame(result).sort_values(by=['timestamp'], ascending=True)
#
#     return df_final

# def add_start_end_quaternions(df, df_extremes, start, end):
#     """
#     Adds the first and/or last quaternion from each sensor position before/after extracted data.
#     This function manually groups and sorts data to ensure correct order.
#
#     :param df: Original DataFrame with quaternions
#     :param df_extremes: DataFrame with extracted extreme rotation points
#     :param start: Boolean flag to include the first quaternion per position
#     :param end: Boolean flag to include the last quaternion per position
#     :return: Concatenated DataFrame with preserved timestamp order
#     """
#     result = []
#
#     # Ensure 'timestamp' column exists before processing
#     if 'timestamp' not in df.columns:
#         raise KeyError("❌ The dataframe is missing the 'timestamp' column.")
#
#     # Ensure all timestamps are converted to `datetime` format
#     if df['timestamp'].dtypes in ['float64', 'int64']:  # If Unix timestamp format
#         df['timestamp'] = pd.to_datetime(df['timestamp'], unit='s', errors='coerce')
#     elif df['timestamp'].dtypes == 'object':  # If stored as a string
#         df['timestamp'] = pd.to_datetime(df['timestamp'], errors='coerce')
#
#     # Handle NaN timestamps (remove rows with missing timestamps)
#     df = df.dropna(subset=['timestamp'])
#
#     # Sort manually by looping over each `position`
#     sorted_rows = []
#     first_rows = []
#     last_rows = []
#
#     unique_positions = df['position'].unique()
#
#     for pos in unique_positions:
#         group = df[df['position'] == pos].copy()
#         group = group.sort_values(by=['timestamp'], ascending=True)
#
#         if start and not group.empty:
#             first_rows.append(group.iloc[0])  # Select first row for this position
#
#         sorted_rows.append(group)  # Store sorted group
#
#         if end and not group.empty:
#             last_rows.append(group.iloc[-1])  # Select last row for this position
#
#     # Concatenate manually collected rows
#     if start:
#         result.extend(first_rows)
#
#     result.extend(df_extremes)
#
#     if end:
#         result.extend(last_rows)
#
#     # Convert result back to DataFrame
#     df_final = pd.concat(result, ignore_index=True).sort_values(by=['timestamp'], ascending=True)
#
#     return df_final


def add_start_end_quaternions(df, df_extremes, start, end):
    """
    Adds the first and/or last quaternion from each sensor position before/after extracted data.
    This function manually groups and sorts data to ensure correct order.

    :param df: Original DataFrame with quaternions
    :param df_extremes: DataFrame with extracted extreme rotation points
    :param start: Boolean flag to include the first quaternion per position
    :param end: Boolean flag to include the last quaternion per position
    :return: Concatenated DataFrame with preserved timestamp order
    """
    # Ensure timestamps are in consistent datetime format
    if df['timestamp'].dtype in ['float64', 'int64']:
        df['timestamp'] = pd.to_datetime(df['timestamp'], unit='s', errors='coerce')
    elif df['timestamp'].dtype == 'object':
        df['timestamp'] = pd.to_datetime(df['timestamp'], errors='coerce')

    # Remove any NaN timestamps
    df = df.dropna(subset=['timestamp'])

    # Convert timestamps to Unix format for sorting
    df['timestamp'] = df['timestamp'].astype(np.int64) // 10**9  # Convert nanoseconds to seconds

    # Store manually sorted groups
    sorted_rows = []
    first_rows = []
    last_rows = []

    # **Manual grouping and sorting**
    unique_positions = df['position'].unique()
    grouped_data = {pos: df[df['position'] == pos].sort_values(by=['timestamp']).to_dict(orient="records") for pos in unique_positions}

    for pos, records2 in grouped_data.items():
        # pprint("records2")
        # pprint(records2)
        # records = records2.sort(by=['timestamp'])
        records = records2
        # pprint("  ")
        # pprint("records")
        # pprint(records)
        if not records:
            continue  # Skip empty groups

        if start:
            first_rows.append(records[0])  # First quaternion per position

        sorted_rows.extend(records)  # Keep full list sorted

        if end:
            last_rows.append(records[-1])  # Last quaternion per position

    # Combine results while maintaining order
    final_result = []
    if start:
        final_result.extend(first_rows)
    df_extremes_sorted = df_extremes.sort_values(by=['timestamp']).to_dict(orient="records")
    final_result.extend(df_extremes_sorted)  # Add extracted extreme points
    # final_result.extend(sorted_rows)

    if end:
        pprint(last_rows)
        final_result.extend(last_rows)

    # Convert back to DataFrame and restore original timestamp format
    df_final = pd.DataFrame(final_result)
    df_final['timestamp'] = pd.to_datetime(df_final['timestamp'], unit='s')  # Restore datetime format
    # pprint("")
    # pprint("df_final")
    # pprint(df_final)
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

    return cursor.lastrowid  # Return the newly inserted gesture's ID

def store_extracted_datalines(conn, df_extremes, new_gesture_id):
    """
    Stores extracted extreme rotation points into the database under the new gesture.

    :param conn: MySQL connection
    :param df_extremes: DataFrame containing extracted quaternion data
    :param new_gesture_id: ID of the new gesture
    """
    cursor = conn.cursor()

    for _, row in df_extremes.iterrows():
        # Ensure timestamp is in the correct format
        if isinstance(row['timestamp'], (int, float)):  # If Unix timestamp, convert to datetime
            formatted_timestamp = datetime.utcfromtimestamp(row['timestamp']).strftime('%Y-%m-%d %H:%M:%S.%f')
        elif isinstance(row['timestamp'], str):  # If it's a string, parse it correctly
            formatted_timestamp = datetime.strptime(row['timestamp'], '%Y-%m-%d %H:%M:%S.%f').strftime('%Y-%m-%d %H:%M:%S.%f')
        else:  # Assume it's already a `datetime` object
            formatted_timestamp = row['timestamp'].strftime('%Y-%m-%d %H:%M:%S.%f')

        # Insert into DataLine table
        cursor.execute(
            "INSERT INTO DataLine (hand, position, timestamp, gesture_id) VALUES (%s, %s, %s, %s);",
            (row['hand'], row['position'], formatted_timestamp, new_gesture_id)
        )
        new_dataline_id = cursor.lastrowid

        # Insert into FingerDataLine table
        cursor.execute(
            "INSERT INTO FingerDataLine (accX, accY, accZ, quatA, quatX, quatY, quatZ, id) VALUES (0, 0, 0, %s, %s, %s, %s, %s);",
            (row['qw'], row['qx'], row['qy'], row['qz'], new_dataline_id)
        )

    conn.commit()


# Main function
if __name__ == "__main__":
    args = parse_arguments()

    print("\n📌 **Command-line arguments:**")
    pprint(vars(args))

    conn = connect_db(args.host, args.user, args.password, args.database)
    if not conn:
        exit(1)

    df_quaternions = fetch_quaternion_data(conn, args.gesture_id, args.hand, args.position)
    if df_quaternions is not None:
        df_with_velocity = compute_angular_velocity(df_quaternions)
        df_extremes = find_rotation_extremes(df_with_velocity, order=3, threshold=args.threshold_extraction)

        # Add start and end quaternions if requested
        df_final = add_start_end_quaternions(df_quaternions, df_extremes, args.start, args.end)

        # Save to CSV for local analysis
        df_final.to_csv("extreme_rotation_points.csv", index=False)
        print(f"✅ Extreme rotation points saved to 'extreme_rotation_points.csv' ({len(df_final)} samples)")

        if args.suffix:
            new_gesture_id = create_new_gesture(conn, df_quaternions.iloc[0]['userAlias'],
                                                int(df_quaternions.iloc[0]['user_id']), args.suffix,
                                                args.threshold_recognition)
            store_extracted_datalines(conn, df_final, new_gesture_id)
            print(f"✅ Stored {len(df_final)} extracted points under new gesture ID: {new_gesture_id}")

    conn.close()
