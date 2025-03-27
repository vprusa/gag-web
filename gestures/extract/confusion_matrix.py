import mysql.connector
import numpy as np
import pandas as pd
import argparse
import seaborn as sns
import matplotlib.pyplot as plt
import matplotlib.image as mpimg
import os
from sklearn.metrics import confusion_matrix, classification_report
from scipy.spatial.transform import Rotation as R
import sys
from io import StringIO
import warnings
from sklearn.metrics import classification_report

# Suppress the specific UndefinedMetricWarning
# warnings.filterwarnings("ignore", category=UndefinedMetricWarning)

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
    parser.add_argument("--actual-matches", type=int, nargs='+', help="List of expected matches (1 or 0) for each input gesture")
    parser.add_argument("--save-ref-gesture", type=str, help="Name of new gesture to save averaged referential data")
    return parser.parse_args()


def connect_db(host, user, password, database):
    return mysql.connector.connect(host=host, user=user, password=password, database=database)


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
        print(f"  Gesture {idx}:")
        print(quat_set)
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


# def categorize_by_angular_distance(input_df, avg_ref, threshold):
#     categories = []
#     grouped = input_df.groupby('gesture_id')
#     min_len = min(len(g) for _, g in grouped)
#     print("\n🔍 Debug Info: categorize_by_angular_distance")
#     print(f"Using threshold: {threshold:.6f}")
#
#     for gesture_id, g in grouped:
#         print(f"\nGesture ID: {gesture_id}")
#         for i in range(min_len):
#             quat = g.iloc[i][['qw', 'qx', 'qy', 'qz']].to_numpy()
#             ref_quat = avg_ref[i]
#             q1 = R.from_quat([quat[1], quat[2], quat[3], quat[0]])
#             q2 = R.from_quat([ref_quat[1], ref_quat[2], ref_quat[3], ref_quat[0]])
#             confusion_matrix.pyangle = q1.inv() * q2
#             angle_diff = angle.magnitude()
#             match = int(angle_diff <= threshold)
#             print(f"  Index {i}: angle_diff = {angle_diff:.6f}, match = {match}")
#             categories.append({
#                 'gesture_id': gesture_id,
#                 'index': i,
#                 'angle_diff': angle_diff,
#                 'matched': match
#             })
#
#     categorized_df = pd.DataFrame(categories)
#     print("\n📋 Angular Difference Categorization Result:")
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

            # Correct the reference to use the quaternion rotation comparison
            angle_diff = (q1.inv() * q2).magnitude()
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


def assign_actual_matches(categorized_df, gesture_ids, actual_matches):
    mapping = dict(zip(gesture_ids, actual_matches))
    categorized_df['actual_match'] = categorized_df['gesture_id'].map(mapping).fillna(0).astype(int)
    return categorized_df


def generate_angular_confusion_matrix(df, ref_ids, input_ids, position, output_prefix, actual_matches, args):
    actual_str = ''.join(map(str, actual_matches)) if actual_matches else 'unknown'
    dir_prefix = f"out_ref_gestures_{'_'.join(map(str, ref_ids))}_in_gestures_{'_'.join(map(str, input_ids))}_actual_matches_{actual_str}"
    out_path = os.path.join(dir_prefix, f"pos_{position}")
    os.makedirs(out_path, exist_ok=True)

    old_stdout = sys.stdout
    sys.stdout = mystdout = StringIO()

    print("\n📊 Classification Report (Global):")
    y_true = df['actual_match']
    y_pred = df['matched']
    cm = confusion_matrix(y_true, y_pred, labels=[0, 1])
    print(classification_report(y_true, y_pred, labels=[0, 1]))

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

    for idx in df['index'].unique():
        print(f"\n📊 Classification Report (Index {idx}):")
        subset = df[df['index'] == idx]
        y_true = subset['actual_match']
        y_pred = subset['matched']
        cm = confusion_matrix(y_true, y_pred, labels=[0, 1])
        print(classification_report(y_true, y_pred, labels=[0, 1]))

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

    sys.stdout = old_stdout
    log_content = mystdout.getvalue()

    with open(os.path.join(out_path, "program_args.txt"), "w") as f:
        f.write("Command-line arguments:\n")
        for k, v in vars(args).items():
            f.write(f"{k}: {v}\n")

    with open(os.path.join(out_path, "program.log"), "w") as f:
        f.write(log_content)

    print(log_content)





def store_ref_gesture(conn, name, avg_quats, ref_df, position, threshold):
    if avg_quats is None or avg_quats.shape[0] == 0:
        print("⚠️ No averaged data to store.")
        return

    first_ref_gesture_id = ref_df['gesture_id'].unique()[0]
    timestamps = ref_df[ref_df['gesture_id'] == first_ref_gesture_id]['timestamp'].values[:len(avg_quats)]

    cursor = conn.cursor()
    cursor.execute("""
        INSERT INTO Gesture (dateCreated, delay, exec, isActive, isFiltered, shouldMatch, userAlias, user_id)
        VALUES (NOW(), 1, NULL, 1, 1, %s, %s, 1)
    """, (threshold, name))
    gesture_id = cursor.lastrowid

    for i, quat in enumerate(avg_quats):
        ts = timestamps[i] if i < len(timestamps) else datetime.utcnow()
        # Ensure ts is Python datetime
        if isinstance(ts, np.datetime64):
            ts = pd.to_datetime(ts).to_pydatetime()

        cursor.execute(
            "INSERT INTO DataLine (hand, position, timestamp, gesture_id) VALUES (%s, %s, %s, %s)",
            (None, position, ts, gesture_id)
        )
        dataline_id = cursor.lastrowid

        cursor.execute(
            "INSERT INTO FingerDataLine (accX, accY, accZ, quatA, quatX, quatY, quatZ, id) VALUES (0, 0, 0, %s, %s, %s, %s, %s)",
            (quat[0], quat[1], quat[2], quat[3], dataline_id)
        )

    conn.commit()
    print(f"✅ Saved new referential gesture '{name}' with ID: {gesture_id}")
#
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
#         avg_ref, threshold = compute_avg_reference_and_threshold(
#             ref_df,
#             override_threshold=args.threshold if args.calc_threshold else args.threshold,
#             calc_max=args.calc_max,
#             use_angular=args.angular_diff
#         )
#         print(f"📐 Using threshold: {threshold:.6f}")
#
#         input_df = fetch_gesture_data(conn, args.gestures, pos)
#         if input_df.empty:
#             print("❌ No input gesture data.")
#             continue
#
#         print("\n📦 Input gesture datalines:")
#         for gid in input_df['gesture_id'].unique():
#             print(f"\nGesture ID {gid}:")
#             print(input_df[input_df['gesture_id'] == gid][['qw', 'qx', 'qy', 'qz']])
#
#         categorized_df = categorize_by_angular_distance(input_df, avg_ref, threshold)
#
#         if args.actual_matches:
#             categorized_df = assign_actual_matches(categorized_df, args.gestures, args.actual_matches)
#         else:
#             print("⚠️ --actual-matches not provided. Defaulting all to match (1).")
#             categorized_df['actual_match'] = 1
#
#         if args.actual_matches:
#             categorized_df = assign_actual_matches(categorized_df, args.gestures, args.actual_matches)
#         else:
#             print("⚠️ --actual-matches not provided. Defaulting all to match (1).")
#             categorized_df['actual_match'] = categorized_df['matched']  # fallback: same as old behavior
#
#         if args.actual_matches:
#             categorized_df = assign_actual_matches(categorized_df, args.gestures, args.actual_matches)
#         else:
#             print("⚠️ --actual-matches not provided. Defaulting all to match (1).")
#             categorized_df['actual_match'] = categorized_df['matched']  # fallback: same as old behavior
#
#         generate_angular_confusion_matrix(
#             categorized_df,
#             args.ref_gestures,
#             args.gestures,
#             pos,
#             args.output_prefix,
#             args.actual_matches if args.actual_matches else categorized_df['actual_match'].tolist(),
#             args
#         )
#
#         # Additional categorization: gesture-level match if all datalines match
#         gesture_summary = categorized_df.groupby('gesture_id')['matched'].agg(lambda x: int(all(x))).reset_index()
#         if args.actual_matches:
#             gesture_summary['actual_match'] = pd.Series(args.actual_matches, index=gesture_summary.index)
#         else:
#             gesture_summary['actual_match'] = gesture_summary['matched']  # fallback
#
#         print("\n📊 Gesture-Level Classification Report:")
#         from sklearn.metrics import classification_report, confusion_matrix
#
#         y_true_gest = gesture_summary['actual_match']
#         y_pred_gest = gesture_summary['matched']
#         print(classification_report(y_true_gest, y_pred_gest, labels=[0, 1]))
#
#         # Optionally, save gesture-level matrix
#         gesture_cm = confusion_matrix(y_true_gest, y_pred_gest, labels=[0, 1])
#         gesture_out_dir_path = os.path.join(
#             f"out_ref_gestures_{'_'.join(map(str, args.ref_gestures))}_in_gestures_{'_'.join(map(str, args.gestures))}_actual_matches_{''.join(map(str, args.actual_matches if args.actual_matches else gesture_summary['actual_match'].tolist()))}",
#             f"pos_{pos}"
#         )
#         gesture_out_path = os.path.join(
#             gesture_out_dir_path,
#             "gesture_level_confusion.png"
#         )
#         os.makedirs(os.path.dirname(gesture_out_path), exist_ok=True)
#         plt.figure(figsize=(4, 3))
#         sns.heatmap(gesture_cm, annot=True, fmt='d', cmap='Blues', xticklabels=[0, 1], yticklabels=[0, 1])
#         plt.title("Gesture-Level Confusion Matrix\nTL: TN | TR: FP\nBL: FN | BR: TP")
#         plt.xlabel("Predicted")
#         plt.ylabel("Actual")
#         plt.tight_layout()
#         plt.savefig(gesture_out_path)
#         plt.close()
#         print(f"✅ Saved gesture-level confusion matrix: {gesture_out_path}")
#
#         # Optional: Save averaged referential gesture to DB
#         if args.save_ref_gesture:
#             store_ref_gesture(conn, args.save_ref_gesture, avg_ref, ref_df, pos, threshold)
#
#     conn.close()
#
#     print( f"nautilus {gesture_out_dir_path} & disown" )

# if __name__ == "__main__":
#     args = parse_arguments()
#     conn = connect_db(args.host, args.user, args.password, args.database)
#
#     # Initialize an empty list to hold the aggregated categorized data
#     aggregated_df = []
#
#     for pos in args.positions:
#         print(f"\n📍 Processing position {pos}")
#         ref_df = fetch_gesture_data(conn, args.ref_gestures, pos)
#         if ref_df.empty:
#             print("❌ No referential gesture data.")
#             continue
#
#         avg_ref, threshold = compute_avg_reference_and_threshold(
#             ref_df,
#             override_threshold=args.threshold if args.calc_threshold else args.threshold,
#             calc_max=args.calc_max,
#             use_angular=args.angular_diff
#         )
#         print(f"📐 Using threshold: {threshold:.6f}")
#
#         input_df = fetch_gesture_data(conn, args.gestures, pos)
#         if input_df.empty:
#             print("❌ No input gesture data.")
#             continue
#
#         print("\n📦 Input gesture datalines:")
#         for gid in input_df['gesture_id'].unique():
#             print(f"\nGesture ID {gid}:")
#             print(input_df[input_df['gesture_id'] == gid][['qw', 'qx', 'qy', 'qz']])
#
#         categorized_df = categorize_by_angular_distance(input_df, avg_ref, threshold)
#
#         if args.actual_matches:
#             categorized_df = assign_actual_matches(categorized_df, args.gestures, args.actual_matches)
#         else:
#             print("⚠️ --actual-matches not provided. Defaulting all to match (1).")
#             categorized_df['actual_match'] = 1
#
#         # Append categorized_df to the aggregated list
#         aggregated_df.append(categorized_df)
#
#         generate_angular_confusion_matrix(
#             categorized_df,
#             args.ref_gestures,
#             args.gestures,
#             pos,
#             args.output_prefix,
#             args.actual_matches if args.actual_matches else categorized_df['actual_match'].tolist(),
#             args
#         )
#
#         # Additional categorization: gesture-level match if all datalines match
#         gesture_summary = categorized_df.groupby('gesture_id')['matched'].agg(lambda x: int(all(x))).reset_index()
#         if args.actual_matches:
#             gesture_summary['actual_match'] = pd.Series(args.actual_matches, index=gesture_summary.index)
#         else:
#             gesture_summary['actual_match'] = gesture_summary['matched']  # fallback
#
#         print("\n📊 Gesture-Level Classification Report:")
#         from sklearn.metrics import classification_report, confusion_matrix
#
#         y_true_gest = gesture_summary['actual_match']
#         y_pred_gest = gesture_summary['matched']
#         print(classification_report(y_true_gest, y_pred_gest, labels=[0, 1]))
#
#         # Optionally, save gesture-level matrix
#         gesture_cm = confusion_matrix(y_true_gest, y_pred_gest, labels=[0, 1])
#         gesture_out_dir_path = os.path.join(
#             f"out_ref_gestures_{'_'.join(map(str, args.ref_gestures))}_in_gestures_{'_'.join(map(str, args.gestures))}_actual_matches_{''.join(map(str, args.actual_matches if args.actual_matches else gesture_summary['actual_match'].tolist()))}",
#             f"pos_{pos}"
#         )
#         gesture_out_path = os.path.join(
#             gesture_out_dir_path,
#             "gesture_level_confusion.png"
#         )
#         os.makedirs(os.path.dirname(gesture_out_path), exist_ok=True)
#         plt.figure(figsize=(4, 3))
#         sns.heatmap(gesture_cm, annot=True, fmt='d', cmap='Blues', xticklabels=[0, 1], yticklabels=[0, 1])
#         plt.title("Gesture-Level Confusion Matrix\nTL: TN | TR: FP\nBL: FN | BR: TP")
#         plt.xlabel("Predicted")
#         plt.ylabel("Actual")
#         plt.tight_layout()
#         plt.savefig(gesture_out_path)
#         plt.close()
#         print(f"✅ Saved gesture-level confusion matrix: {gesture_out_path}")
#
#         # Optional: Save averaged referential gesture to DB
#         if args.save_ref_gesture:
#             store_ref_gesture(conn, args.save_ref_gesture, avg_ref, ref_df, pos, threshold)
#
#     conn.close()
#
#     # After the loop ends, aggregate the results into a single DataFrame
#     aggregated_df = pd.concat(aggregated_df, ignore_index=True)
#
#     # Generate the overall confusion matrix for all positions combined
#     print("\n📊 Global Classification Report (Aggregated Across All Positions):")
#     y_true = aggregated_df['actual_match']
#     y_pred = aggregated_df['matched']
#     cm = confusion_matrix(y_true, y_pred, labels=[0, 1])
#     print(classification_report(y_true, y_pred, labels=[0, 1]))
#
#     # Save the global confusion matrix
#     global_out_path = os.path.join(args.output_prefix, "global_angular_diff_confusion.png")
#     os.makedirs(os.path.dirname(global_out_path), exist_ok=True)
#     plt.figure(figsize=(4, 3))
#     sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=[0, 1], yticklabels=[0, 1])
#     plt.title("Global Angular Difference Confusion Matrix (All Positions)")
#     plt.xlabel("Predicted")
#     plt.ylabel("Actual")
#     plt.tight_layout()
#     plt.savefig(global_out_path)
#     plt.close()
#     print(f"✅ Saved global angular difference confusion matrix: {global_out_path}")
#
#     print( f"nautilus {gesture_out_dir_path} & disown" )
#

if __name__ == "__main__":
    args = parse_arguments()
    conn = connect_db(args.host, args.user, args.password, args.database)

    # Initialize an empty list to hold the aggregated categorized data
    aggregated_df = []

    # Initialize a dictionary to store match summaries
    match_summary = {}

    # Initialize a list to store all gestures' match flags
    all_gesture_match_info = []

    for pos in args.positions:
        print(f"\n📍 Processing position {pos}")
        ref_df = fetch_gesture_data(conn, args.ref_gestures, pos)
        if ref_df.empty:
            print("❌ No referential gesture data.")
            continue

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

        print("\n📦 Input gesture datalines:")
        for gid in input_df['gesture_id'].unique():
            print(f"\nGesture ID {gid}:")
            print(input_df[input_df['gesture_id'] == gid][['qw', 'qx', 'qy', 'qz']])

        categorized_df = categorize_by_angular_distance(input_df, avg_ref, threshold)

        if args.actual_matches:
            categorized_df = assign_actual_matches(categorized_df, args.gestures, args.actual_matches)
        else:
            print("⚠️ --actual-matches not provided. Defaulting all to match (1).")
            categorized_df['actual_match'] = 1

        # Append categorized_df to the aggregated list
        aggregated_df.append(categorized_df)

        # Update match_summary with the match count for each gesture and position
        for gesture_id in categorized_df['gesture_id'].unique():
            # Get the match count for each gesture at this position
            total_matches = categorized_df[categorized_df['gesture_id'] == gesture_id]['matched'].sum()
            total_datalines = categorized_df[categorized_df['gesture_id'] == gesture_id].shape[0]

            # Initialize the entry for the gesture if not already present
            if gesture_id not in match_summary:
                match_summary[gesture_id] = {}

            match_summary[gesture_id][pos] = {
                'matches': total_matches,
                'total_datalines': total_datalines,
                'match_percentage': total_matches / total_datalines * 100 if total_datalines > 0 else 0
            }

        generate_angular_confusion_matrix(
            categorized_df,
            args.ref_gestures,
            args.gestures,
            pos,
            args.output_prefix,
            args.actual_matches if args.actual_matches else categorized_df['actual_match'].tolist(),
            args
        )

    conn.close()

    # After the loop ends, aggregate the results into a single DataFrame
    aggregated_df = pd.concat(aggregated_df, ignore_index=True)

    # Create a dictionary to store match info (match and expected flags) for each gesture
    gesture_match_flags = {}
    for idx, gesture_id in enumerate(args.gestures):
        # Get the expected match flag from --actual-matches
        expected_match = args.actual_matches[idx] if idx < len(
            args.actual_matches) else 1  # Default to 1 if not provided

        # Check if the gesture has 100% match in all positions
        all_positions_match = all(
            match_summary.get(gesture_id, {}).get(pos, {}).get('match_percentage', 0) == 100 for pos in args.positions)
        match_flag = 1 if all_positions_match else 0

        # Add to the gesture_match_flags dictionary
        gesture_match_flags[gesture_id] = {
            'match': match_flag,
            'expected': expected_match
        }

        # Add to all_gesture_match_info for the final confusion matrix
        all_gesture_match_info.append({
            'gesture_id': gesture_id,
            'match': match_flag,
            'expected': expected_match
        })

    # Print match summary for each gesture per position
    print("\n📋 Match Summary Per Gesture Per Position:")
    for gesture_id, positions in match_summary.items():
        print(f"\nGesture ID {gesture_id}:")
        for pos, data in positions.items():
            print(
                f"  Position {pos}: Matches: {data['matches']}/{data['total_datalines']} ({data['match_percentage']:.2f}%)")

    # Print match info for each gesture
    print("\n📋 Match Info for Each Gesture:")
    for gesture_id, flags in gesture_match_flags.items():
        print(f"Gesture ID {gesture_id}: Match: {flags['match']}, Expected: {flags['expected']}")

    # Now generate the final confusion matrix based on all_gesture_match_info
    y_true = [gesture['expected'] for gesture in all_gesture_match_info]
    y_pred = [gesture['match'] for gesture in all_gesture_match_info]

    final_cm = confusion_matrix(y_true, y_pred, labels=[0, 1])
    print(classification_report(y_true, y_pred, labels=[0, 1]))
    gesture_out_dir_path = os.path.join(
        f"out_ref_gestures_{'_'.join(map(str, args.ref_gestures))}_in_gestures_{'_'.join(map(str, args.gestures))}_actual_matches_{''.join(map(str, args.actual_matches if args.actual_matches else gesture_summary['actual_match'].tolist()))}",
        f"pos_{pos}"
    )
    gesture_out_path = os.path.join(
        gesture_out_dir_path,
        "gesture_level_confusion.png"
    )
    # Save the final confusion matrix
    final_out_path = os.path.join(
        gesture_out_dir_path,
        "final_all_matched_confusion.png"
    )
    os.makedirs(os.path.dirname(final_out_path), exist_ok=True)
    plt.figure(figsize=(4, 3))
    sns.heatmap(final_cm, annot=True, fmt='d', cmap='Blues', xticklabels=[0, 1], yticklabels=[0, 1])
    plt.title("Final Confusion Matrix: \nAll Datelines and Positions\nTL: TN | TR: FP\nBL: FN | BR: TP")
    plt.xlabel("Predicted")
    plt.ylabel("Actual")
    plt.tight_layout()
    plt.savefig(final_out_path)
    plt.close()
    print(f"✅ Saved final confusion matrix: {final_out_path}")

    print(f"nautilus {gesture_out_dir_path} & disown")
