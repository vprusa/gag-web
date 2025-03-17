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
    parser.add_argument("--output", type=str, default="confusion_matrix.png", help="Output file name for the confusion matrix image")
    
    return parser.parse_args()

# Connect to MySQL database
def connect_db(host, user, password, database):
    try:
        return mysql.connector.connect(host=host, user=user, password=password, database=database)
    except mysql.connector.Error as err:
        print(f"❌ Database Connection Error: {err}")
        return None

# Fetch gesture data
def fetch_gesture_data(conn, gesture_ids):
    query = """
    SELECT 
        dl.id, dl.position, dl.timestamp, 
        fdl.quatA AS qw, fdl.quatX AS qx, fdl.quatY AS qy, fdl.quatZ AS qz,
        dl.gesture_id
    FROM FingerDataLine fdl
    JOIN DataLine dl ON fdl.id = dl.id
    WHERE dl.gesture_id IN (%s)
    ORDER BY dl.timestamp ASC;
    """ % (', '.join(['%s'] * len(gesture_ids)))
    
    cursor = conn.cursor(dictionary=True)
    cursor.execute(query, gesture_ids)
    data = cursor.fetchall()
    
    return pd.DataFrame(data) if data else None

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
    # plt.show()
    plt.savefig("confusion_matrix.png")
    plt.close()

    print("Classification Report:")
    print(classification_report(y_test, y_pred))
    
    cm_df = pd.DataFrame(cm, index=np.unique(labels), columns=np.unique(labels))
    cm_df.to_csv("confusion_matrix.csv")

# Main function
if __name__ == "__main__":
    args = parse_arguments()
    conn = connect_db(args.host, args.user, args.password, args.database)
    
    if not conn:
        exit(1)
    
    df_gestures = fetch_gesture_data(conn, args.gesture_ids)
    if df_gestures is not None:
        df_with_velocity = compute_angular_velocity(df_gestures)
        generate_confusion_matrix(df_with_velocity, args.output)
    
    conn.close()

