import mysql.connector
import numpy as np
import pandas as pd
import argparse
import pprint
from scipy.spatial.transform import Rotation as R
from scipy.signal import argrelextrema
from datetime import datetime

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
    parser.add_argument("--threshold-extraction", type=float, default=0.1,
                        help="Threshold for filtering extreme points")
    parser.add_argument("--threshold-recognition", type=float, default=0.2,
                        help="Threshold for gesture recognition in DB")
    parser.add_argument("--suffix", type=str, required=False,
                        help="Optional: Suffix for new gesture name. If not provided, data will not be stored in the database.")

    return parser.parse_args()


# MySQL connection helper
def connect_db(host, user, password, database):
    try:
        return mysql.connector.connect(host=host, user=user, password=password, database=database)
    except mysql.connector.Error as err:
        print(f"❌ Database Connection Error: {err}")
        return None


# Fetch quaternion data for a specific `gesture_id`, filtering by `hand` if provided
def fetch_quaternion_data(conn, gesture_id, hand):
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

    query += " ORDER BY dl.timestamp ASC;"

    cursor = conn.cursor(dictionary=True)
    cursor.execute(query, params)
    data = cursor.fetchall()

    df = pd.DataFrame(data) if data else None
    if df is not None and not df.empty:
        print("\n✅ First 6 retrieved quaternions:")
        pprint.pprint(df.head(6).to_dict(orient="records"))  # Display first 6 samples

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


# Store extracted quaternion data under new gesture
def store_extracted_datalines(conn, df_extremes, new_gesture_id):
    cursor = conn.cursor()

    for _, row in df_extremes.iterrows():
        # cursor.execute(
        #     "INSERT INTO DataLine (hand, position, timestamp, gesture_id) VALUES (%s, %s, %s, %s);",
        #     (row['hand'], row['position'], row['timestamp'], new_gesture_id)
        # )
        cursor.execute(
            "INSERT INTO DataLine (hand, position, timestamp, gesture_id) VALUES (%s, %s, %s, %s);",
            (row['hand'], row['position'], datetime.utcfromtimestamp(row['timestamp']).strftime('%Y-%m-%d %H:%M:%S.%f'),
             new_gesture_id)
        )
        new_dataline_id = cursor.lastrowid

        cursor.execute(
            "INSERT INTO FingerDataLine (accX, accY, accZ, quatA, quatX, quatY, quatZ, id) VALUES (0, 0, 0, %s, %s, %s, %s, %s);",
            (row['qw'], row['qx'], row['qy'], row['qz'], new_dataline_id)
        )

    conn.commit()


# Main function
if __name__ == "__main__":
    args = parse_arguments()
    conn = connect_db(args.host, args.user, args.password, args.database)
    if not conn:
        exit(1)

    df_quaternions = fetch_quaternion_data(conn, args.gesture_id, args.hand)
    if df_quaternions is not None:
        df_with_velocity = compute_angular_velocity(df_quaternions)
        df_extremes = find_rotation_extremes(df_with_velocity, order=3, threshold=args.threshold_extraction)

        if args.suffix:
            # new_gesture_id = create_new_gesture(conn, df_quaternions.iloc[0]['userAlias'],
            #                                     df_quaternions.iloc[0]['user_id'], args.suffix,
            #                                     args.threshold_recognition)
            new_gesture_id = create_new_gesture(conn, df_quaternions.iloc[0]['userAlias'],
                                                int(df_quaternions.iloc[0]['user_id']), args.suffix,
                                                args.threshold_recognition)
            store_extracted_datalines(conn, df_extremes, new_gesture_id)
            print(f"✅ Stored {len(df_extremes)} extracted points under new gesture ID: {new_gesture_id}")

    conn.close()
