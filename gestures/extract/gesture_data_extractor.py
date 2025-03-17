import mysql.connector
import numpy as np
import pandas as pd
import argparse
import pprint
from scipy.spatial.transform import Rotation as R
from scipy.signal import argrelextrema


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
    parser.add_argument("--add", action="store_true", help="If used, creates a new gesture and stores extracted data")

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


# Compute angular velocity for `hand` if provided, otherwise process all hands
def compute_angular_velocity(df):
    df = df.copy()  # Prevent modifying the original DataFrame
    df['timestamp'] = pd.to_datetime(df['timestamp']).astype(np.int64) / 1e9  # Convert to seconds

    results = []

    # Process per position (ignoring hand during filtering)
    for position in df['position'].unique():
        group = df[df['position'] == position].copy()

        if len(group) < 2:
            print(f"⚠️ Not enough samples for position {position} (skipping)")
            continue  # Skip groups with only one sample

        quaternions = group[['qx', 'qy', 'qz', 'qw']].values
        timestamps = group['timestamp'].values

        # Compute angular velocities
        rotations = R.from_quat(quaternions)
        angular_velocities = [0]  # First velocity is zero

        for i in range(1, len(quaternions)):
            delta_rotation = rotations[i - 1].inv() * rotations[i]
            angle = 2 * np.arccos(np.clip(delta_rotation.as_quat()[-1], -1.0, 1.0))
            dt = timestamps[i] - timestamps[i - 1]
            angular_velocity = angle / dt if dt > 0 else 0
            angular_velocities.append(angular_velocity)

        group['angular_velocity'] = angular_velocities
        results.append(group)

    return pd.concat(results, ignore_index=True) if results else pd.DataFrame()


# Find extreme rotation changes (ignoring hand)
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

        # Apply threshold filter
        filtered_extremes = [idx for idx in extreme_indices if angular_velocity[idx] >= threshold]

        if len(filtered_extremes) == 0:
            print(f"⚠️ No extreme points detected for position {position}")

        results.append(group.iloc[filtered_extremes])

    return pd.concat(results, ignore_index=True) if results else pd.DataFrame()


# Main function
if __name__ == "__main__":
    args = parse_arguments()

    # Connect to MySQL
    conn = connect_db(args.host, args.user, args.password, args.database)
    if not conn:
        exit(1)

    # Fetch quaternion data
    df_quaternions = fetch_quaternion_data(conn, args.gesture_id, args.hand)

    if df_quaternions is not None:
        print(f"✅ Retrieved {len(df_quaternions)} quaternion samples for gesture ID {args.gesture_id}")

        # Compute angular velocity per position (ignoring hand during filtering)
        df_with_velocity = compute_angular_velocity(df_quaternions)

        # Print velocity statistics
        if not df_with_velocity.empty:
            print("\n📊 **Statistics of Computed Angular Velocities:**")
            print(f"🔹 Total samples: {len(df_with_velocity)}")
            print(f"🔹 Mean angular velocity: {df_with_velocity['angular_velocity'].mean():.5f} rad/s")
            print(f"🔹 Min angular velocity: {df_with_velocity['angular_velocity'].min():.5f} rad/s")
            print(f"🔹 Max angular velocity: {df_with_velocity['angular_velocity'].max():.5f} rad/s")
            print(f"🔹 Standard deviation: {df_with_velocity['angular_velocity'].std():.5f} rad/s")

        # Find extreme rotation changes (ignoring hand)
        df_extremes = find_rotation_extremes(df_with_velocity, order=3, threshold=args.threshold_extraction)

        # Save to CSV for local analysis
        df_extremes.to_csv("extreme_rotation_points.csv", index=False)
        print(f"✅ Extreme rotation points saved to 'extreme_rotation_points.csv' ({len(df_extremes)} samples)")

    # Close database connection
    conn.close()
