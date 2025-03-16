import mysql.connector
import numpy as np
import pandas as pd
import argparse
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
    parser.add_argument("--threshold", type=float, default=0.1, help="Angular velocity threshold for filtering extreme points")
    parser.add_argument("--add", action="store_true", help="If used, creates a new gesture and stores extracted data")
    
    return parser.parse_args()


# MySQL Connection Helper
def connect_db(host, user, password, database):
    try:
        return mysql.connector.connect(host=host, user=user, password=password, database=database)
    except mysql.connector.Error as err:
        print(f"Database Connection Error: {err}")
        return None


# Fetch quaternion data from MySQL
def fetch_quaternion_data(conn, gesture_id):
    query = """
    SELECT 
        dl.id,
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
    ORDER BY 
        dl.timestamp ASC;
    """
    cursor = conn.cursor(dictionary=True)
    cursor.execute(query, (gesture_id,))
    data = cursor.fetchall()
    return pd.DataFrame(data) if data else None


# Compute angular velocity from quaternion sequence
def compute_angular_velocity(df):
    quaternions = df[['qx', 'qy', 'qz', 'qw']].values
    timestamps = pd.to_datetime(df['timestamp']).astype(np.int64) / 1e9  # Convert to seconds

    rotations = R.from_quat(quaternions)
    angular_velocities = [0]  # First velocity is zero

    for i in range(1, len(quaternions)):
        delta_rotation = rotations[i - 1].inv() * rotations[i]
        angle = 2 * np.arccos(np.clip(delta_rotation.as_quat()[-1], -1.0, 1.0))
        dt = timestamps[i] - timestamps[i - 1]
        angular_velocity = angle / dt if dt > 0 else 0
        angular_velocities.append(angular_velocity)

    df['angular_velocity'] = angular_velocities
    return df


# Detect extreme rotation changes (peaks in angular velocity) with threshold filtering
def find_rotation_extremes(df, order=3, threshold=0.1):
    angular_velocity = df['angular_velocity'].values
    extreme_indices = argrelextrema(angular_velocity, np.greater, order=order)[0]

    # Apply threshold filter
    filtered_extremes = [idx for idx in extreme_indices if angular_velocity[idx] >= threshold]
    
    return df.iloc[filtered_extremes]


# Create a new gesture and return its ID
def create_new_gesture(conn, old_gesture_name, user_id):
    new_gesture_name = f"{old_gesture_name}-gen-1"
    
    query = """
    INSERT INTO Gesture (dateCreated, delay, exec, isActive, isFiltered, shouldMatch, userAlias, user_id)
    VALUES (NOW(), 1, NULL, 0, 0, 0.2, %s, %s);
    """
    
    cursor = conn.cursor()
    cursor.execute(query, (new_gesture_name, user_id))
    conn.commit()
    
    return cursor.lastrowid  # Return the newly inserted gesture's ID


# Insert extracted extreme rotation points into the database
def store_extracted_datalines(conn, df_extremes, new_gesture_id):
    cursor = conn.cursor()

    for _, row in df_extremes.iterrows():
        # Insert into DataLine table
        cursor.execute(
            "INSERT INTO DataLine (hand, position, timestamp, gesture_id) VALUES (NULL, NULL, %s, %s);",
            (row['timestamp'], new_gesture_id)
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
    # Parse command-line arguments
    args = parse_arguments()

    # Connect to MySQL
    conn = connect_db(args.host, args.user, args.password, args.database)
    if not conn:
        exit(1)

    # Fetch quaternion data from MySQL
    df_quaternions = fetch_quaternion_data(conn, args.gesture_id)

    if df_quaternions is not None:
        # Compute angular velocity
        df_with_velocity = compute_angular_velocity(df_quaternions)

        # Find extreme rotation changes with threshold filtering
        df_extremes = find_rotation_extremes(df_with_velocity, order=3, threshold=args.threshold)

        # Save to CSV for local analysis
        df_extremes.to_csv("extreme_rotation_points.csv", index=False)
        print("Extreme rotation points saved to 'extreme_rotation_points.csv'")
        print(df_extremes.head())  # Print first few rows for quick check

        # If --add flag is used, create a new gesture and store the extracted data
        if args.add:
            print("Creating new gesture and storing extracted data...")
            
            new_gesture_id = create_new_gesture(conn, df_quaternions.iloc[0]['userAlias'], df_quaternions.iloc[0]['user_id'])
            
            store_extracted_datalines(conn, df_extremes, new_gesture_id)
            print(f"Extracted data stored under new gesture ID: {new_gesture_id}")

    # Close database connection
    conn.close()
