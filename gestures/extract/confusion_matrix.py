import mysql.connector
import numpy as np
import pandas as pd
import argparse
import seaborn as sns
import matplotlib.pyplot as plt
import os
from sklearn.metrics import confusion_matrix


# Command-line argument parser
def parse_arguments():
    parser = argparse.ArgumentParser(
        description="Generate quaternion confusion matrices based on referential gestures.")

    parser.add_argument("--host", type=str, required=True)
    parser.add_argument("--user", type=str, required=True)
    parser.add_argument("--password", type=str, required=True)
    parser.add_argument("--database", type=str, required=True)
    parser.add_argument("--gestures", type=int, nargs='+', required=True)
    parser.add_argument("--positions", type=int, nargs='+', required=True)
    parser.add_argument("--ref-gestures", type=int, nargs='+', required=True)
    parser.add_argument("--output_prefix", type=str, default="quaternion_plot_out")
    parser.add_argument("--calc-threshold", action="store_true")
    parser.add_argument("--threshold", type=float)

    return parser.parse_args()


# Connect to MySQL database
def connect_db(host, user, password, database):
    return mysql.connector.connect(host=host, user=user, password=password, database=database)


# Fetch gesture data
def fetch_gesture_data(conn, gesture_ids, position):
    query = f"""
    SELECT dl.id, dl.position, dl.timestamp,
           fdl.quatA AS qw, fdl.quatX AS qx, fdl.quatY AS qy, fdl.quatZ AS qz,
           dl.gesture_id
    FROM FingerDataLine fdl
    JOIN DataLine dl ON fdl.id = dl.id
    WHERE dl.gesture_id IN ({', '.join(['%s'] * len(gesture_ids))}) AND dl.position = %s
    ORDER BY dl.timestamp ASC;
    """
    cursor = conn.cursor(dictionary=True)
    cursor.execute(query, gesture_ids + [position])
    return pd.DataFrame(cursor.fetchall())


# Calculate average gesture and threshold
def compute_avg_reference_and_threshold(df, override_threshold=None):
    grouped = df.groupby('gesture_id')
    min_len = grouped.size().min()
    aligned = [g.iloc[:min_len][['qw', 'qx', 'qy', 'qz']].to_numpy() for _, g in grouped]
    avg_gesture = np.mean(aligned, axis=0)

    if override_threshold is not None:
        return avg_gesture, override_threshold

    diffs = []
    for i in range(min_len):
        per_row_diffs = [np.linalg.norm(g[i] - avg_gesture[i]) for g in aligned]
        diffs.append(np.mean(per_row_diffs))

    return avg_gesture, np.mean(diffs)


# Categorize input gestures based on threshold
def categorize_by_threshold(input_df, avg_ref, threshold):
    categories = []
    grouped = input_df.groupby('gesture_id')
    min_len = min(len(g) for _, g in grouped)
    for gesture_id, g in grouped:
        for i in range(min_len):
            quat = g.iloc[i][['qw', 'qx', 'qy', 'qz']].to_numpy()
            ref_quat = avg_ref[i]
            dist = np.linalg.norm(quat - ref_quat)
            match = int(dist <= threshold)
            categories.append({
                'gesture_id': gesture_id,
                'index': i,
                'matched': match,
                'qw': quat[0], 'qx': quat[1], 'qy': quat[2], 'qz': quat[3]
            })
    return pd.DataFrame(categories)


# Generate confusion matrices and plots
def generate_confusion_matrices(df, ref_ids, input_ids, position, output_prefix):
    for i in df['index'].unique():
        subset = df[df['index'] == i]
        for axis in ['qw', 'qx', 'qy', 'qz']:
            y_true = subset['matched']
            y_pred = (subset[axis] > 0).astype(int)
            cm = confusion_matrix(y_true, y_pred, labels=[0, 1])

            out_path = os.path.join(
                f"{output_prefix}_ref_gestures_{'_'.join(map(str, ref_ids))}_in_gestures_{'_'.join(map(str, input_ids))}",
                f"pos_{position}"
            )
            os.makedirs(out_path, exist_ok=True)
            filename = f"quat_{subset['gesture_id'].iloc[0]}.idx_{i}.axis_{axis}.png"
            filepath = os.path.join(out_path, filename)

            plt.figure(figsize=(4, 3))
            sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=[0, 1], yticklabels=[0, 1])
            plt.title(f"Confusion Matrix (Index {i}, Axis {axis})")
            plt.xlabel("Predicted")
            plt.ylabel("Actual")
            plt.tight_layout()
            plt.savefig(filepath)
            plt.close()
            print(f"✅ Saved: {filepath}")


# Main execution
if __name__ == "__main__":
    args = parse_arguments()
    conn = connect_db(args.host, args.user, args.password, args.database)

    for pos in args.positions:
        print(f"\n📍 Processing position {pos}")
        ref_df = fetch_gesture_data(conn, args.ref_gestures, pos)
        if ref_df.empty:
            print("❌ No referential gesture data.")
            continue

        avg_ref, threshold = compute_avg_reference_and_threshold(ref_df,
                                                                 args.threshold if args.calc_threshold else args.threshold)
        print(f"📐 Using threshold: {threshold:.6f}")

        input_df = fetch_gesture_data(conn, args.gestures, pos)
        if input_df.empty:
            print("❌ No input gesture data.")
            continue

        categorized_df = categorize_by_threshold(input_df, avg_ref, threshold)
        generate_confusion_matrices(categorized_df, args.ref_gestures, args.gestures, pos, args.output_prefix)

    conn.close()
