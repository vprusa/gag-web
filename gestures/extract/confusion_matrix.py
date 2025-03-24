import mysql.connector
import numpy as np
import pandas as pd
import argparse
import seaborn as sns
import matplotlib.pyplot as plt
import matplotlib.image as mpimg
import os
from sklearn.metrics import confusion_matrix
from scipy.spatial.transform import Rotation as R

# Command-line argument parser

def parse_arguments():
    parser = argparse.ArgumentParser(description="Generate quaternion confusion matrices based on referential gestures.")

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
    parser.add_argument("--angular-diff", action="store_true")
    parser.add_argument("--calc-max", action="store_true")

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
# def compute_avg_reference_and_threshold(df, override_threshold=None):
#     grouped = df.groupby('gesture_id')
#     min_len = grouped.size().min()
#     aligned = [g.iloc[:min_len][['qw', 'qx', 'qy', 'qz']].to_numpy() for _, g in grouped]
#     avg_gesture = np.mean(aligned, axis=0)
# 
#     if override_threshold is not None:
#         return avg_gesture, override_threshold
# 
#     diffs = []
#     for i in range(min_len):
#         per_row_diffs = [np.linalg.norm(g[i] - avg_gesture[i]) for g in aligned]
#         diffs.append(np.mean(per_row_diffs))
# 
#     return avg_gesture, np.mean(diffs)


# def compute_avg_reference_and_threshold(df, override_threshold=None, calc_max=False):
#     grouped = df.groupby('gesture_id')
#     min_len = grouped.size().min()
#     aligned = [g.iloc[:min_len][['qw', 'qx', 'qy', 'qz']].to_numpy() for _, g in grouped]
#     avg_gesture = np.mean(aligned, axis=0)
#
#     if override_threshold is not None:
#         return avg_gesture, override_threshold
#
#     diffs = []
#     for i in range(min_len):
#         per_row_diffs = [np.linalg.norm(g[i] - avg_gesture[i]) for g in aligned]
#         stat = max(per_row_diffs) if calc_max else np.mean(per_row_diffs)
#         diffs.append(stat)
#
#     return avg_gesture, max(diffs) if calc_max else np.mean(diffs)

# def compute_avg_reference_and_threshold(df, override_threshold=None, calc_max=False):
#     grouped = df.groupby('gesture_id')
#     min_len = grouped.size().min()
#     aligned = [g.iloc[:min_len][['qw', 'qx', 'qy', 'qz']].to_numpy() for _, g in grouped]
#     avg_gesture = np.mean(aligned, axis=0)
#
#     print("\n🔍 Debug Info: compute_avg_reference_and_threshold")
#     print(f"Override threshold: {override_threshold}")
#     print(f"Using max calculation: {calc_max}")
#     print("Aligned Quaternions (per gesture):")
#     for idx, quat_set in enumerate(aligned):
#         print(f"  Gesture {idx}:\n{quat_set}")
#     print("\nAveraged Gesture:")
#     print(avg_gesture)
#
#     if override_threshold is not None:
#         return avg_gesture, override_threshold
#
#     diffs = []
#     for i in range(min_len):
#         per_row_diffs = [np.linalg.norm(g[i] - avg_gesture[i]) for g in aligned]
#         print(f"Row {i} diffs: {per_row_diffs}")
#         stat = max(per_row_diffs) if calc_max else np.mean(per_row_diffs)
#         print(f"Row {i} {'max' if calc_max else 'avg'} diff: {stat:.6f}")
#         diffs.append(stat)
#
#     final_threshold = max(diffs) if calc_max else np.mean(diffs)
#     print(f"\n✅ Final computed threshold: {final_threshold:.6f}")
#
#     return avg_gesture, final_threshold
def compute_avg_reference_and_threshold(df, override_threshold=None, calc_max=False, use_angular=False):
    grouped = df.groupby('gesture_id')
    min_len = grouped.size().min()
    aligned = [g.iloc[:min_len][['qw', 'qx', 'qy', 'qz']].to_numpy() for _, g in grouped]
    avg_gesture = np.mean(aligned, axis=0)

    print("\n🔍 Debug Info: compute_avg_reference_and_threshold")
    print(f"Override threshold: {override_threshold}")
    print(f"Using max calculation: {calc_max}")
    print(f"Using angular difference: {use_angular}")
    print("Aligned Quaternions (per gesture):")
    for idx, quat_set in enumerate(aligned):
        print(f"  Gesture {idx}:\n{quat_set}")
    print("\nAveraged Gesture:")
    print(avg_gesture)

    if override_threshold is not None:
        return avg_gesture, override_threshold

    diffs = []
    for i in range(min_len):
        if use_angular:
            ref = avg_gesture[i]
            row_diffs = []
            for g in aligned:
                q1 = R.from_quat([g[i][1], g[i][2], g[i][3], g[i][0]])
                q2 = R.from_quat([ref[1], ref[2], ref[3], ref[0]])
                angle_diff = (q1.inv() * q2).magnitude()
                row_diffs.append(angle_diff)
            print(f"Row {i} angular diffs: {row_diffs}")
        else:
            row_diffs = [np.linalg.norm(g[i] - avg_gesture[i]) for g in aligned]
            print(f"Row {i} euclidean diffs: {row_diffs}")
        stat = max(row_diffs) if calc_max else np.mean(row_diffs)
        print(f"Row {i} {'max' if calc_max else 'avg'} diff: {stat:.6f}")
        diffs.append(stat)

    final_threshold = max(diffs) if calc_max else np.mean(diffs)
    print(f"\n✅ Final computed threshold: {final_threshold:.6f}")
    return avg_gesture, final_threshold

# Categorize input gestures based on angular difference at full dataline level
# def categorize_by_angular_distance(input_df, avg_ref, threshold):
#     categories = []
#     grouped = input_df.groupby('gesture_id')
#     min_len = min(len(g) for _, g in grouped)
#     for gesture_id, g in grouped:
#         total_angle = 0
#         for i in range(min_len):
#             quat = g.iloc[i][['qw', 'qx', 'qy', 'qz']].to_numpy()
#             ref_quat = avg_ref[i]
#             q1 = R.from_quat([quat[1], quat[2], quat[3], quat[0]])
#             q2 = R.from_quat([ref_quat[1], ref_quat[2], ref_quat[3], ref_quat[0]])
#             angle = q1.inv() * q2
#             total_angle += angle.magnitude()
#         avg_angle = total_angle / min_len
#         match = int(avg_angle <= threshold)
#         categories.append({'gesture_id': gesture_id, 'matched': match})
#     return pd.DataFrame(categories)

# Categorize input gestures based on angular difference at full dataline level
# def categorize_by_angular_distance(input_df, avg_ref, threshold):
#     categories = []
#     grouped = input_df.groupby('gesture_id')
#     min_len = min(len(g) for _, g in grouped)
#     for gesture_id, g in grouped:
#         for i in range(min_len):
#             quat = g.iloc[i][['qw', 'qx', 'qy', 'qz']].to_numpy()
#             ref_quat = avg_ref[i]
#             q1 = R.from_quat([quat[1], quat[2], quat[3], quat[0]])
#             q2 = R.from_quat([ref_quat[1], ref_quat[2], ref_quat[3], ref_quat[0]])
#             angle = q1.inv() * q2
#             angle_diff = angle.magnitude()
#             match = int(angle_diff <= threshold)
#             categories.append({
#                 'gesture_id': gesture_id,
#                 'index': i,
#                 'angle_diff': angle_diff,
#                 'matched': match
#             })
#     categorized_df = pd.DataFrame(categories)
#     print("\n📋 Angular Difference Categorization:")
#     print(categorized_df.to_string(index=False))
#     return categorized_df

def categorize_by_angular_distance(input_df, avg_ref, threshold):
    categories = []
    grouped = input_df.groupby('gesture_id')
    min_len = min(len(g) for _, g in grouped)
    print("\n🔍 Debug Info: categorize_by_angular_distance")
    print(f"Using threshold: {threshold:.6f}")

    for gesture_id, g in grouped:
        print(f"\nGesture ID: {gesture_id}")
        for i in range(min_len):
            quat = g.iloc[i][['qw', 'qx', 'qy', 'qz']].to_numpy()
            ref_quat = avg_ref[i]
            q1 = R.from_quat([quat[1], quat[2], quat[3], quat[0]])
            q2 = R.from_quat([ref_quat[1], ref_quat[2], ref_quat[3], ref_quat[0]])
            angle = q1.inv() * q2
            angle_diff = angle.magnitude()
            match = int(angle_diff <= threshold)
            print(f"  Index {i}: angle_diff = {angle_diff:.6f}, match = {match}")
            categories.append({
                'gesture_id': gesture_id,
                'index': i,
                'angle_diff': angle_diff,
                'matched': match
            })

    categorized_df = pd.DataFrame(categories)
    print("\n📋 Angular Difference Categorization Result:")
    print(categorized_df.to_string(index=False))
    return categorized_df
# Generate single confusion matrix based on angular threshold classification
# def generate_angular_confusion_matrix(df, ref_ids, input_ids, position, output_prefix):
#     y_true = df['matched']
#     y_pred = df['matched']  # for now assume ideal classification (replace with real prediction logic if available)
#     cm = confusion_matrix(y_true, y_pred, labels=[0, 1])
#
#     out_path = os.path.join(
#         f"{output_prefix}_ref_gestures_{'_'.join(map(str, ref_ids))}_in_gestures_{'_'.join(map(str, input_ids))}",
#         f"pos_{position}"
#     )
#     os.makedirs(out_path, exist_ok=True)
#     file_path = os.path.join(out_path, f"angular_diff_confusion.png")
#
#     plt.figure(figsize=(4, 3))
#     sns.heatmap(cm, annot=True, fmt='d', cmap='Purples', xticklabels=[0, 1], yticklabels=[0, 1])
#     plt.title("Angular Difference Confusion Matrix")
#     plt.xlabel("Predicted")
#     plt.ylabel("Actual")
#     plt.tight_layout()
#     plt.savefig(file_path)
#     plt.close()
#     print(f"✅ Saved angular difference confusion matrix: {file_path}")

def generate_angular_confusion_matrix(df, ref_ids, input_ids, position, output_prefix):
    out_path = os.path.join(
        f"{output_prefix}_ref_gestures_{'_'.join(map(str, ref_ids))}_in_gestures_{'_'.join(map(str, input_ids))}",
        f"pos_{position}"
    )
    os.makedirs(out_path, exist_ok=True)

    # Global confusion matrix
    y_true = df['matched']
    y_pred = df['matched']  # Placeholder for prediction logic
    cm = confusion_matrix(y_true, y_pred, labels=[0, 1])

    file_path = os.path.join(out_path, f"angular_diff_confusion.png")
    plt.figure(figsize=(4, 3))
    sns.heatmap(cm, annot=True, fmt='d', cmap='Purples', xticklabels=[0, 1], yticklabels=[0, 1])
    plt.title("Angular Difference Confusion Matrix (Global)")
    plt.xlabel("Predicted")
    plt.ylabel("Actual")
    plt.tight_layout()
    plt.savefig(file_path)
    plt.close()
    print(f"✅ Saved global angular difference confusion matrix: {file_path}")

    # Per-dataline confusion matrices
    for idx in df['index'].unique():
        subset = df[df['index'] == idx]
        y_true = subset['matched']
        y_pred = subset['matched']  # Again, assumed perfect matching for placeholder
        cm = confusion_matrix(y_true, y_pred, labels=[0, 1])

        file_path = os.path.join(out_path, f"angular_diff_confusion.idx_{idx}.png")
        plt.figure(figsize=(4, 3))
        sns.heatmap(cm, annot=True, fmt='d', cmap='Purples', xticklabels=[0, 1], yticklabels=[0, 1])
        plt.title(f"Angular Diff Confusion Matrix (Index {idx})")
        plt.xlabel("Predicted")
        plt.ylabel("Actual")
        plt.tight_layout()
        plt.savefig(file_path)
        plt.close()
        print(f"✅ Saved angular diff matrix for index {idx}: {file_path}")

# # Main execution
# if __name__ == "__main__":
#     args = parse_arguments()
#     conn = connect_db(args.host, args.user, args.password, args.database)
#
#     for pos in args.positions:
#         print(f"\n📍 Processing position {pos}")
#         ref_df = fetch_gesture_data(conn, args.ref_gestures, pos)
#         if ref_df.empty:
#             print("❌ No referential gesture data.")
#             continue
#
#         avg_ref, threshold = compute_avg_reference_and_threshold(ref_df, args.threshold if args.calc_threshold else args.threshold)
#         print(f"📐 Using threshold: {threshold:.6f}")
#
#         input_df = fetch_gesture_data(conn, args.gestures, pos)
#         if input_df.empty:
#             print("❌ No input gesture data.")
#             continue
#
#         # categorized_df = categorize_by_threshold(input_df, avg_ref, threshold)
#         # generate_confusion_matrices(categorized_df, args.ref_gestures, args.gestures, pos, args.output_prefix)
#
#         if args.angular_diff:
#             angular_df = categorize_by_angular_distance(input_df, avg_ref, threshold)
#             generate_angular_confusion_matrix(angular_df, args.ref_gestures, args.gestures, pos, args.output_prefix)
#
#     conn.close()


if __name__ == "__main__":
    args = parse_arguments()
    conn = connect_db(args.host, args.user, args.password, args.database)

    for pos in args.positions:
        print(f"\n📍 Processing position {pos}")
        ref_df = fetch_gesture_data(conn, args.ref_gestures, pos)
        if ref_df.empty:
            print("❌ No referential gesture data.")
            continue

        # avg_ref, threshold = compute_avg_reference_and_threshold(
        #     ref_df,
        #     override_threshold=args.threshold if args.calc_threshold else args.threshold,
        #     calc_max=args.calc_max
        # )
        avg_ref, threshold = compute_avg_reference_and_threshold(
            ref_df,
            override_threshold=args.threshold if args.calc_threshold else args.threshold,
            calc_max=args.calc_max,
            use_angular=args.angular_diff
        )

        print(f"📐 Using threshold: {threshold:.6f}")

        input_df = fetch_gesture_data(conn, args.gestures, pos)
        if input_df.empty:
            print("❌ No input gesture data.")
            continue

        # categorized_df = categorize_by_threshold(input_df, avg_ref, threshold)
        # generate_confusion_matrices(categorized_df, args.ref_gestures, args.gestures, pos, args.output_prefix)

        if args.angular_diff:
            angular_df = categorize_by_angular_distance(input_df, avg_ref, threshold)
            generate_angular_confusion_matrix(angular_df, args.ref_gestures, args.gestures, pos, args.output_prefix)

    conn.close()
