import mysql.connector
import numpy as np
import pandas as pd
import argparse
from datetime import datetime
from sklearn.metrics import confusion_matrix
import matplotlib.pyplot as plt
import seaborn as sns

# Command-line argument parser
def parse_arguments():
    parser = argparse.ArgumentParser(description="Classify gestures by similarity to a referential gesture.")

    parser.add_argument("--host", type=str, required=True, help="MySQL server host")
    parser.add_argument("--user", type=str, required=True, help="MySQL username")
    parser.add_argument("--password", type=str, required=True, help="MySQL password")
    parser.add_argument("--database", type=str, required=True, help="MySQL database name")
    parser.add_argument("--ref-gesture", type=int, required=True, help="Referential gesture ID")
    parser.add_argument("--gestures", type=int, nargs='+', required=True, help="Gesture IDs to compare against reference")
    parser.add_argument("--class-threshold", type=float, default=0.01, help="Classification threshold (angular distance)")

    return parser.parse_args()

# Fetch gesture data (assumes all data for one gesture and position)
def fetch_gesture_data(conn, gesture_id):
    query = """
    SELECT dl.id, dl.timestamp, fdl.quatA AS qw, fdl.quatX AS qx, fdl.quatY AS qy, fdl.quatZ AS qz
    FROM FingerDataLine fdl
    JOIN DataLine dl ON fdl.id = dl.id
    WHERE dl.gesture_id = %s
    ORDER BY dl.timestamp ASC;
    """
    cursor = conn.cursor(dictionary=True)
    cursor.execute(query, (gesture_id,))
    data = cursor.fetchall()
    return pd.DataFrame(data) if data else None

# Compute angle between two quaternions
def quaternion_distance(q1, q2):
    q1 = q1 / np.linalg.norm(q1)
    q2 = q2 / np.linalg.norm(q2)
    dot_product = np.abs(np.dot(q1, q2))
    angle = 2 * np.arccos(np.clip(dot_product, -1.0, 1.0))
    return angle

# Classification logic
def classify_gesture_against_reference(ref_df, test_df, threshold):
    if len(ref_df) != len(test_df):
        print("❌ Gesture skipped due to mismatched dataline count.")
        return None  # Invalid due to mismatch

    for i in range(len(ref_df)):
        ref_q = ref_df.loc[i, ['qw', 'qx', 'qy', 'qz']].to_numpy()
        test_q = test_df.loc[i, ['qw', 'qx', 'qy', 'qz']].to_numpy()
        angle = quaternion_distance(ref_q, test_q)
        print(f"   ▸ Row {i}: angle = {angle:.6f} rad")
        if angle > threshold:
            return False  # Mismatch found
    return True

# Main function
if __name__ == "__main__":
    args = parse_arguments()

    print("\n🔧 Running script with arguments:")
    print(args)

    conn = mysql.connector.connect(host=args.host, user=args.user, password=args.password, database=args.database)
    if not conn:
        exit(1)

    print(f"\n📌 Loading reference gesture: {args.ref_gesture}")
    ref_df = fetch_gesture_data(conn, args.ref_gesture)
    if ref_df is None or ref_df.empty:
        print("❌ Could not load referential gesture data.")
        conn.close()
        exit(1)

    print("\n📋 Evaluating gestures:")
    y_true = []
    y_pred = []

    for gesture_id in args.gestures:
        print(f"\n🔎 Gesture {gesture_id}")
        test_df = fetch_gesture_data(conn, gesture_id)
        if test_df is None or test_df.empty:
            print("⚠️ Skipped: No data.")
            continue

        result = classify_gesture_against_reference(ref_df, test_df, args.class_threshold)
        if result is None:
            continue  # Skip mismatched length

        match = int(result)
        print("✅ MATCH" if match else "❌ NOT MATCHED")

        y_true.append(1)  # Assume all gestures are supposed to match
        y_pred.append(match)

    if y_true:
        print("\n📊 Confusion Matrix:")
        cm = confusion_matrix(y_true, y_pred, labels=[0, 1])
        df_cm = pd.DataFrame(cm, index=["True Not Match", "True Match"], columns=["Pred Not Match", "Pred Match"])
        print(df_cm)

        # Optional visualization
        plt.figure(figsize=(6, 4))
        sns.heatmap(df_cm, annot=True, fmt="d", cmap="Blues")
        plt.title("Confusion Matrix")
        plt.xlabel("Predicted")
        plt.ylabel("Actual")
        plt.tight_layout()
        plt.show()

    conn.close()
