import mysql.connector
import numpy as np
import pandas as pd
import argparse
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix, classification_report
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
from datetime import datetime


# Command-line argument parser
def parse_arguments():
    parser = argparse.ArgumentParser(description="Generate confusion matrix for gesture classification.")

    parser.add_argument("--host", type=str, required=True, help="MySQL server host")
    parser.add_argument("--user", type=str, required=True, help="MySQL username")
    parser.add_argument("--password", type=str, required=True, help="MySQL password")
    parser.add_argument("--database", type=str, required=True, help="MySQL database name")
    parser.add_argument("--gesture_ids", type=int, nargs='+', required=True, help="List of gesture IDs to analyze")
    parser.add_argument("--positions", type=int, nargs='+', required=True,
                        help="List of position indices (0-5) to process")
    parser.add_argument("--output_prefix", type=str, default="confusion_matrix",
                        help="Output file prefix for confusion matrices")

    return parser.parse_args()


# Connect to MySQL database
def connect_db(host, user, password, database):
    try:
        return mysql.connector.connect(host=host, user=user, password=password, database=database)
    except mysql.connector.Error as err:
        print(f"❌ Database Connection Error: {err}")
        return None


# Fetch gesture data for a specific position
def fetch_gesture_data(conn, gesture_ids, position):
    query = """
    SELECT 
        dl.id, dl.position, dl.timestamp,
        fdl.quatA AS qw, fdl.quatX AS qx, fdl.quatY AS qy, fdl.quatZ AS qz,
        dl.gesture_id
    FROM FingerDataLine fdl
    JOIN DataLine dl ON fdl.id = dl.id
    WHERE dl.gesture_id IN ({}) AND dl.position = %s
    ORDER BY dl.timestamp ASC;
    """.format(', '.join(['%s'] * len(gesture_ids)))

    cursor = conn.cursor(dictionary=True)
    cursor.execute(query, gesture_ids + [position])
    data = cursor.fetchall()

    df = pd.DataFrame(data) if data else None

    if df is not None:
        print("\n📊 Retrieved Quaternion Data (Aligned Columns):")
        print(df[['position', 'qw', 'qx', 'qy', 'qz']].to_string(index=False))

    return df


# Compute angular velocity
def compute_angular_velocity(df):
    df = df.copy()
    df['timestamp'] = pd.to_datetime(df['timestamp']).astype(np.int64) / 1e9  # Convert to seconds

    results = []
    for position in df['position'].unique():
        group = df[df['position'] == position].copy()

        if len(group) < 2:
            continue

        quaternions = group[['qx', 'qy', 'qz', 'qw']].values
        timestamps = group['timestamp'].values

        angular_velocities = [0]
        for i in range(1, len(quaternions)):
            delta_rotation = np.linalg.norm(quaternions[i] - quaternions[i - 1])
            dt = timestamps[i] - timestamps[i - 1]
            angular_velocity = delta_rotation / dt if dt > 0 else 0
            angular_velocities.append(angular_velocity)

        group['angular_velocity'] = angular_velocities
        results.append(group)

    return pd.concat(results, ignore_index=True) if results else pd.DataFrame()


# Train a simple classifier and create confusion matrix
def generate_confusion_matrix(df, output_file):
    df = df.copy()
    features = df[['angular_velocity', 'position']]
    labels = df['gesture_id']

    X_train, X_test, y_train, y_test = train_test_split(features, labels, test_size=0.3, random_state=42)

    classifier = KNeighborsClassifier(n_neighbors=3)
    classifier.fit(X_train, y_train)
    y_pred = classifier.predict(X_test)

    cm = confusion_matrix(y_test, y_pred, labels=np.unique(labels))

    plt.figure(figsize=(8, 6))
    sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=np.unique(labels), yticklabels=np.unique(labels))
    plt.xlabel("Predicted Label")
    plt.ylabel("True Label")
    plt.title("Confusion Matrix")
    plt.savefig(output_file)
    plt.close()

    print(f"✅ Saved confusion matrix: {output_file}")

    cm_df = pd.DataFrame(cm, index=np.unique(labels), columns=np.unique(labels))
    cm_df.to_csv(output_file.replace('.png', '.csv'))



# Main function
if __name__ == "__main__":
    args = parse_arguments()

    print("\n🔧 Running script with arguments:")
    print(args)

    # unit_test_confusion_matrix()

    conn = connect_db(args.host, args.user, args.password, args.database)

    if not conn:
        exit(1)

    gesturesStr = '_'.join(map(str, args.gesture_ids))

    for position in args.positions:
        print(f"\n📡 Processing position: {position}")
        df_gestures = fetch_gesture_data(conn, args.gesture_ids, position)
        if df_gestures is not None:
            df_with_velocity = compute_angular_velocity(df_gestures)
            output_file = f"{args.output_prefix}_gestures_{gesturesStr}_position_{position}.png"
            generate_confusion_matrix(df_with_velocity, output_file)

    conn.close()
