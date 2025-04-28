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
from pprint import pprint
from datetime import datetime
from itertools import combinations


def parse_arguments():
    parser = argparse.ArgumentParser(description="Generate quaternion confusion matrices based on referential gestures.")
    parser.add_argument("--host", type=str, required=True)
    parser.add_argument("--user", type=str, required=True)
    parser.add_argument("--password", type=str, required=True)
    parser.add_argument("--database", type=str, required=True)
    parser.add_argument("--gestures", type=int, nargs='+', required=False)
    parser.add_argument("--positions", type=int, nargs='+', required=True)
    parser.add_argument("--ref-gestures", type=int, nargs='+', required=True)
    parser.add_argument("--output_prefix", type=str, default="quaternion_plot_out")
    parser.add_argument("--calc-all-thresholds", action="store_true", help="Use all threshold calculations")
    parser.add_argument("--row-max-col-max", action="store_true", help="Row max, Column max calculation")
    parser.add_argument("--row-max-col-avg", action="store_true", help="Row max, Column avg calculation")
    parser.add_argument("--row-avg-col-max", action="store_true", help="Row avg, Column max calculation")
    parser.add_argument("--row-avg-col-avg", action="store_true", help="Row avg, Column avg calculation")
    parser.add_argument("--use-max-threshold", action="store_true", help="Use maximum global threshold, average otherwise")
    parser.add_argument("--gen-cm", default=False, action="store_true", help="Enables confusion matrix generation")
    parser.add_argument("--threshold", type=float)
    parser.add_argument("--angular-diff", action="store_true")
    parser.add_argument("-v", default=False, action="store_true")
    parser.add_argument("--actual-matches", type=int, nargs='+', help="List of expected matches (1 or 0) for each input gesture")
    parser.add_argument("--save-ref-gesture", type=str, help="Name of new gesture to save averaged referential data")
    parser.add_argument("--calc-all", action="store_true", help="Enable brute-force gesture coverage evaluation")
    return parser.parse_args()


def connect_db(host, user, password, database):
    return mysql.connector.connect(host=host, user=user, password=password, database=database)

def fetch_gesture_data(conn, gesture_ids, positions):
    query = f"""
    SELECT dl.id, dl.position, dl.timestamp,
           fdl.quatA AS qw, fdl.quatX AS qx, fdl.quatY AS qy, fdl.quatZ AS qz,
           dl.gesture_id, g.shouldMatch
    FROM FingerDataLine fdl
    JOIN DataLine dl ON fdl.id = dl.id
    LEFT JOIN Gesture g on dl.gesture_id = g.id 
    WHERE dl.gesture_id IN ({', '.join(['%s'] * len(gesture_ids))}) AND 
            dl.position IN ({', '.join(['%s'] * len(positions))})
    ORDER BY dl.timestamp ASC;
    """
    cursor = conn.cursor(dictionary=True)
    cursor.execute(query, gesture_ids + positions)
    return pd.DataFrame(cursor.fetchall())

def compute_avg_reference_and_threshold(df, override_threshold=None, calc_max=False, use_angular=False):
    grouped = df.groupby('gesture_id')

    # Handle single gesture edge case
    if len(grouped) == 1:
        gesture_id = next(iter(grouped.groups))
        threshold_from_data = df[df['gesture_id'] == gesture_id]['shouldMatch'].iloc[0]
        single_gesture_quats = df[df['gesture_id'] == gesture_id][['qw', 'qx', 'qy', 'qz']].to_numpy()
        return single_gesture_quats, threshold_from_data

    min_len = grouped.size().min()
    aligned = [g.iloc[:min_len][['qw', 'qx', 'qy', 'qz']].to_numpy() for _, g in grouped]
    avg_gesture = np.mean(aligned, axis=0)

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
        else:
            row_diffs = [np.linalg.norm(g[i] - avg_gesture[i]) for g in aligned]
        stat = max(row_diffs) if calc_max else np.mean(row_diffs)
        diffs.append(stat)

    final_threshold = max(diffs) if calc_max else np.mean(diffs)
    return avg_gesture, final_threshold

def categorize_by_angular_distance(input_df, avg_ref, threshold):
    categories = []
    grouped = input_df.groupby('gesture_id')
    min_len = min(len(g) for _, g in grouped)

    for gesture_id, g in grouped:
        for i in range(min_len):
            quat = g.iloc[i][['qw', 'qx', 'qy', 'qz']].to_numpy()
            ref_quat = avg_ref[i]
            q1 = R.from_quat([quat[1], quat[2], quat[3], quat[0]])
            q2 = R.from_quat([ref_quat[1], ref_quat[2], ref_quat[3], ref_quat[0]])
            angle_diff = (q1.inv() * q2).magnitude()
            match = int(angle_diff <= threshold)
            categories.append({
                'gesture_id': gesture_id,
                'index': i,
                'angle_diff': angle_diff,
                'matched': match
            })

    return pd.DataFrame(categories)


def store_ref_gesture(conn, name, avg_quats, ref_df, threshold):
    if avg_quats is None or avg_quats.shape[0] == 0:
        print("⚠️ No averaged data to store.")
        return

    first_ref_gesture_id = ref_df['gesture_id'].unique()[0]
    first_gesture_df = ref_df[ref_df['gesture_id'] == first_ref_gesture_id].sort_values(by='timestamp')
    timestamps_df = first_gesture_df[['position', 'timestamp']].reset_index(drop=True)

    cursor = conn.cursor()
    cursor.execute("""
        INSERT INTO Gesture (dateCreated, delay, exec, isActive, isFiltered, shouldMatch, userAlias, user_id)
        VALUES (NOW(), 1, NULL, 1, 1, %s, %s, 1)
    """, (float(threshold), name))
    gesture_id = cursor.lastrowid

    for i, quat in enumerate(avg_quats):
        if i < len(timestamps_df):
            ts = timestamps_df.iloc[i]['timestamp']
            position = timestamps_df.iloc[i]['position']
        else:
            ts = datetime.utcnow()
            position = 0

        if isinstance(ts, np.datetime64):
            ts = pd.to_datetime(ts).to_pydatetime()

        cursor.execute(
            "INSERT INTO DataLine (hand, position, timestamp, gesture_id) VALUES (%s, %s, %s, %s)",
            (None, int(position), ts, int(gesture_id))
        )
        dataline_id = cursor.lastrowid

        cursor.execute(
            "INSERT INTO FingerDataLine (accX, accY, accZ, quatA, quatX, quatY, quatZ, id) VALUES (0, 0, 0, %s, %s, %s, %s, %s)",
            (float(quat[3]), float(quat[0]), float(quat[1]), float(quat[2]), int(dataline_id))
        )

        if int(position) == 5:
            cursor.execute("""
                INSERT INTO WristDataLine (magX, magY, magZ, id)
                VALUES (%s, %s, %s, %s)
            """, (0, 0, 0, dataline_id))

    conn.commit()
    print(f"✅ Saved new referential gesture '{name}' with ID: {gesture_id}")



def assign_actual_matches(categorized_df, gesture_ids, actual_matches):
    mapping = dict(zip(gesture_ids, actual_matches))
    categorized_df['actual_match'] = categorized_df['gesture_id'].map(mapping).fillna(0).astype(int)
    return categorized_df


def generate_angular_confusion_matrix(df, ref_ids, input_ids, position, output_prefix, actual_matches, args, threshold):
    actual_str = ''.join(map(str, actual_matches)) if actual_matches else 'unknown'
    dir_prefix = f"out_ref_gestures_{'_'.join(map(str, ref_ids))}_in_gestures_{'_'.join(map(str, input_ids))}_actual_matches_{actual_str}_th_{threshold:.6f}"
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

    if args.gen_cm:
        os.system(f"nautilus {out_path} & disown")
    print(log_content)



def angular_difference(q1, q2):
    rot1 = R.from_quat([q1[1], q1[2], q1[3], q1[0]])
    rot2 = R.from_quat([q2[1], q2[2], q2[3], q2[0]])
    return (rot1.inv() * rot2).magnitude()

def calculate_threshold(ref_gestures, method):
    min_len = min(len(g) for g in ref_gestures)
    aligned_gestures = [g[:min_len] for g in ref_gestures]
    avg_gesture = np.mean(aligned_gestures, axis=0)

    diffs_matrix = np.array([
        [angular_difference(q_avg, q_ref) for q_ref in gesture_row]
        for q_avg, gesture_row in zip(avg_gesture, zip(*aligned_gestures))
    ])

    if method == 'row-max-col-max':
        threshold = np.max(np.max(diffs_matrix, axis=1))
    elif method == 'row-max-col-avg':
        threshold = np.mean(np.max(diffs_matrix, axis=1))
    elif method == 'row-avg-col-max':
        threshold = np.max(np.mean(diffs_matrix, axis=1))
    elif method == 'row-avg-col-avg':
        threshold = np.mean(diffs_matrix)
    else:
        raise ValueError("Invalid method selected.")

    return threshold


def calc_all(args, conn):
    global gesture_match_flags, pos, ref_df, avg_ref, threshold, input_df, categorized_df, gesture_id
    all_gestures = list(set(args.gestures + args.ref_gestures))
    matrix_data = {}
    label_thresholds = {}
    print(f"\n🚀 Starting brute-force evaluation on {len(all_gestures)} gestures...")
    for r in range(1, len(all_gestures)):
        for ref_combo in combinations(all_gestures, r):
            ref_list = list(ref_combo)
            test_combo = all_gestures
            col_label_base = f"ref_{'-'.join(map(str, ref_list))}"
            label_with_threshold = None
            thresholds_for_combo = []
            gesture_match_flags = {gid: [] for gid in test_combo}

            for pos in args.positions:
                ref_df = fetch_gesture_data(conn, ref_list, pos)
                if ref_df.empty:
                    continue

                avg_ref, threshold = compute_avg_reference_and_threshold(
                    ref_df,
                    override_threshold=args.threshold if args.calc_avg else args.threshold,
                    calc_max=args.calc_max,
                    use_angular=args.angular_diff
                )
                thresholds_for_combo.append(threshold)

                input_df = fetch_gesture_data(conn, test_combo, pos)
                if input_df.empty:
                    continue

                categorized_df = categorize_by_angular_distance(input_df, avg_ref, threshold)

                for gesture_id in test_combo:
                    gesture_df = categorized_df[categorized_df['gesture_id'] == gesture_id]
                    position_groups = gesture_df.groupby('index')
                    all_matched = all(g['matched'].mean() == 1.0 for _, g in position_groups)
                    gesture_match_flags[gesture_id].append(all_matched)

            combo_threshold = max(thresholds_for_combo) if thresholds_for_combo else 0
            label_with_threshold = f"{col_label_base}@{combo_threshold:.3f}"
            label_thresholds[label_with_threshold] = combo_threshold

            print(f"📊 Summary for {label_with_threshold}:")

            for gesture_id in test_combo:
                row_label = f"gesture_{gesture_id}"
                if row_label not in matrix_data:
                    matrix_data[row_label] = {}

                if gesture_id in ref_list:
                    matrix_data[row_label][label_with_threshold] = None
                    print(f"  Gesture {gesture_id}: — (Excluded)")
                else:
                    fully_matched = all(gesture_match_flags.get(gesture_id, []))
                    matrix_data[row_label][label_with_threshold] = 1 if fully_matched else 0
                    status = "✔ Matched" if fully_matched else "✘ Not Matched"
                    print(f"  Gesture {gesture_id}: {status}")
    all_cols = sorted(matrix_data[next(iter(matrix_data))].keys())
    matrix_df = pd.DataFrame.from_dict(matrix_data, orient='index')[all_cols]
    matrix_df = matrix_df.transpose()
    matrix_df['Total'] = matrix_df.apply(lambda row: sum(val == 1 for val in row), axis=1)
    trust_scores = []
    ref_counts = []
    for row_label in matrix_df.index:
        try:
            ref_part = row_label.split('@')[0].replace('ref_', '')
            ref_ids = list(map(int, ref_part.split('-')))
            num_refs = len(ref_ids)
        except Exception:
            ref_ids = []
            num_refs = 1

        ref_counts.append(num_refs)
        num_inputs = len(matrix_df.columns) - 2
        total = matrix_df.at[row_label, 'Total']
        trust_c = total / num_refs if num_refs > 0 else 0
        trust_scores.append(trust_c)
    matrix_df['Trust'] = trust_scores
    thresholds = []
    for row_label in matrix_df.index:
        threshold_val = None
        for key in label_thresholds:
            if key in row_label:
                threshold_val = label_thresholds[key]
                break
        thresholds.append(threshold_val if threshold_val is not None else 0)
    matrix_df['Threshold'] = thresholds
    matrix_df['RefCount'] = ref_counts
    matrix_df['RefKey'] = matrix_df.index.to_series().apply(
        lambda x: '-'.join(sorted(x.split('@')[0].replace('ref_', '').split('-'), key=int)))
    matrix_df = matrix_df.sort_values(by=['RefCount', 'Total'], ascending=[True, False])
    threshold_df = matrix_df[['Threshold']]
    matrix_df = matrix_df.drop(columns=['RefCount', 'RefKey', 'Threshold'])
    print("\n📊 Brute-force Gesture Coverage Matrix (match counts):")
    print(matrix_df.to_string())
    heatmap_df = matrix_df.drop(columns=['Total', 'Trust']).copy()
    display_df = heatmap_df.fillna('-')
    total_df = matrix_df[['Total']]
    trust_df = matrix_df[['Trust']]
    fig, axes = plt.subplots(1, 4, figsize=(14, 0.2 * len(matrix_df)))
    ax0, ax1, ax2, ax3 = axes
    sns.heatmap(heatmap_df.astype(float), annot=display_df.values, fmt='', cmap='YlGnBu', ax=ax0, cbar=False,
                linewidths=0.5, linecolor='darkgrey')
    ax0.set_title("Match Counts")
    ax0.set_xlabel("Input Gestures")
    ax0.set_ylabel("Reference Gesture Combinations")
    ax0.tick_params(axis='x', rotation=45)
    sns.heatmap(total_df, annot=True, fmt='.0f', cmap='Greens', ax=ax1, cbar=False)
    ax1.set_title("Total")
    ax1.set_yticks([])
    ax1.set_xticklabels(['Total'], rotation=45, ha='right')
    sns.heatmap(trust_df, annot=True, fmt='.3f', cmap='Oranges', ax=ax2, cbar=False)
    ax2.set_title("Trust")
    ax2.set_yticks([])
    ax2.set_xticklabels(['Trust'], rotation=45, ha='right')
    sns.heatmap(threshold_df, annot=True, fmt='.3f', cmap='Purples', ax=ax3, cbar=False)
    ax3.set_title("Threshold")
    ax3.set_yticks([])
    ax3.set_xticklabels(['Threshold'], rotation=45, ha='right')
    plt.tight_layout()
    outHeapDirPath = "out_heatmap/gests_" + '_'.join(str(g) for g in all_gestures)
    os.makedirs(outHeapDirPath, exist_ok=True)
    heatmap_path = os.path.join(outHeapDirPath, "brute_force_heatmap.png")
    plt.savefig(heatmap_path)
    plt.close()
    print(f"\n✅ Saved brute-force coverage heatmap to {heatmap_path}")
    # ➕ Additional statistics summary
    print("📈 Summary Statistics:")
    print(f"Total combinations evaluated: {len(matrix_df)}")
    matched_rows = (matrix_df['Total'] > 0).sum()
    print(f"Rows with at least one gesture matched: {matched_rows}")
    threshold_zero_rows = (threshold_df['Threshold'] == 0).sum()
    threshold_nonzero_rows = (threshold_df['Threshold'] != 0).sum()
    print(f"Rows with threshold = 0: {threshold_zero_rows}")
    print(f"Rows with threshold ≠ 0: {threshold_nonzero_rows}")
    print("🏅 Best combinations by RefCount:")
    matrix_df['Threshold'] = threshold_df['Threshold']
    matrix_df['RefCount'] = matrix_df.index.to_series().apply(
        lambda x: len(x.split('@')[0].replace('ref_', '').split('-')))
    for refcount in sorted(matrix_df['RefCount'].unique()):
        group = matrix_df[matrix_df['RefCount'] == refcount]
        if group.empty:
            continue
        max_total = group['Total'].max()
        best_rows = group[group['Total'] == max_total]
        best_row = best_rows.sort_values(by='Threshold').iloc[0]
        best_combo = best_row.name
        best_threshold = best_row['Threshold']
        print(
            f"  RefCount {refcount}: Best = {best_combo} with {best_row['Total']} matches, Threshold = {best_threshold:.3f}")
    matrix_df = matrix_df.drop(columns=['RefCount', 'Threshold'])
    conn.close()
    sys.exit(0)


def single(args, conn):
    method = None
    if args.row_max_col_max:
        method = 'row-max-col-max'
    elif args.row_max_col_avg:
        method = 'row-max-col-avg'
    elif args.row_avg_col_max:
        method = 'row-avg-col-max'
    elif args.row_avg_col_avg:
        method = 'row-avg-col-avg'

    if method is None:
        raise ValueError("No calculation method specified.")

    agg_thresholds = {}

    # Load and filter reference gestures data once
    full_ref_df = fetch_gesture_data(conn, args.ref_gestures, args.positions)

    new_gesture_data = []
    for pos in args.positions:
        pos_ref_df = full_ref_df[full_ref_df['position'] == pos]
        if pos_ref_df.empty:
            print(f"❌ No referential data for position {pos}")
            continue

        # Group by gesture and sort each group by timestamp
        gesture_groups = {gid: group.sort_values('timestamp').reset_index(drop=True)
                          for gid, group in pos_ref_df.groupby('gesture_id')}

        min_len = min(len(g) for g in gesture_groups.values())

        row_max_values = []
        row_avg_values = []
        pos_new_quats = []

        for idx in range(min_len):
            quats_row = []
            timestamps_row = []

            for gid, group in gesture_groups.items():
                row = group.iloc[idx]
                quat = [row['qw'], row['qx'], row['qy'], row['qz']]
                quats_row.append(quat)
                timestamps_row.append(row['timestamp'])

            # Calculate average quaternion
            avg_quat = np.mean(quats_row, axis=0)
            timestamp = timestamps_row[0]

            pos_new_quats.append({'position': pos, 'timestamp': timestamp, 'quat': avg_quat})

            # Calculate differences for this row
            row_diffs = [angular_difference(avg_quat, q) for q in quats_row]

            row_max_values.append(np.max(row_diffs))
            row_avg_values.append(np.mean(row_diffs))

        # Select global threshold per position correctly
        if method == 'row-max-col-max':
            pos_global_threshold = np.max(row_max_values)
        elif method == 'row-max-col-avg':
            pos_global_threshold = np.mean(row_max_values)
        elif method == 'row-avg-col-max':
            pos_global_threshold = np.max(row_avg_values)
        elif method == 'row-avg-col-avg':
            pos_global_threshold = np.mean(row_avg_values)

        agg_thresholds[pos] = pos_global_threshold

        new_gesture_data.extend(pos_new_quats)

        print(f"📍 Position {pos}, threshold: {pos_global_threshold:.6f}")

    # Reorder by timestamp
    new_gesture_data_sorted = sorted(new_gesture_data, key=lambda x: x['timestamp'])

    # if args.save_ref_gesture:
    #     gesture_name = f"{args.save_ref_gesture}_{method}"
    #     avg_quats_array = np.array([item['quat'] for item in new_gesture_data_sorted])
    #     timestamps_df = pd.DataFrame(new_gesture_data_sorted)
    #     global_threshold = np.max(list(agg_thresholds.values())) if args.use_max_threshold else np.mean(
    #         list(agg_thresholds.values()))
    #     store_ref_gesture(conn, gesture_name, avg_quats_array, timestamps_df, global_threshold)

    if args.save_ref_gesture:
        gesture_name = f"{args.save_ref_gesture}_{method}"
        avg_quats_array = np.array([item['quat'] for item in new_gesture_data_sorted])

        # Zde přidáme 'gesture_id' ze vstupních referenčních gest
        timestamps_df = pd.DataFrame(new_gesture_data_sorted)
        timestamps_df['gesture_id'] = args.ref_gestures[0]  # Příklad použití prvního referenčního gesta

        global_threshold = np.max(list(agg_thresholds.values())) if args.use_max_threshold else np.mean(
            list(agg_thresholds.values()))
        store_ref_gesture(conn, gesture_name, avg_quats_array, timestamps_df, global_threshold)

    print("✅ Aggregated Thresholds:")
    for pos, thr in agg_thresholds.items():
        print(f"Position {pos}: Threshold {thr:.6f}")

    return agg_thresholds


def single2(args, conn):
    global pos, ref_df, avg_ref, threshold, input_df, categorized_df, gesture_id, gesture_match_flags, positions
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
            override_threshold=args.threshold if args.calc_avg else args.threshold,
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
            match_summary.get(gesture_id, {}).get(pos, {}).get('match_percentage', 0) == 100 for pos in
            args.positions)
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
    # plt.title("Final Confusion Matrix: \nAll Datelines and Positions\nTL: TN | TR: FP\nBL: FN | BR: TP")
    plt.title("Finální Matice záměn: \nVšechny kvatenriony všech pozic")
    plt.xlabel("Predicted")
    plt.ylabel("Actual")
    plt.tight_layout()
    plt.savefig(final_out_path)
    plt.close()
    print(f"✅ Saved final confusion matrix: {final_out_path}")
    print(f"nautilus {gesture_out_dir_path} & disown")


if __name__ == "__main__":
    args = parse_arguments()
    conn = connect_db(args.host, args.user, args.password, args.database)

    if args.calc_all:
        calc_all(args, conn)
    else:
        if args.calc_all_thresholds:
            agg_agg_threshold = {}
            for method_arg in ['row_max_col_max', 'row_max_col_avg', 'row_avg_col_max', 'row_avg_col_avg']:
                setattr(args, 'row_max_col_max', False)
                setattr(args, 'row_max_col_avg', False)
                setattr(args, 'row_avg_col_max', False)
                setattr(args, 'row_avg_col_avg', False)
                setattr(args, method_arg, True)

                agg_threshold = single(args, conn)
                agg_agg_threshold[method_arg] = agg_threshold

            pprint(agg_agg_threshold)

            print("\n📋 Summary of thresholds:")
            for method_name, thresholds in agg_agg_threshold.items():
                all_thresholds = list(thresholds.values())
                avg_threshold = np.mean(all_thresholds)
                max_threshold = np.max(all_thresholds)
                print(f"{method_name} -> avg: {avg_threshold:.6f}, max: {max_threshold:.6f}")
        else:
            # single2(args, conn)
            agg_threshold = single(args, conn)
            pprint(agg_threshold)

            all_thresholds = list(agg_threshold.values())
            avg_threshold = np.mean(all_thresholds)
            max_threshold = np.max(all_thresholds)
            print(f"avg: {avg_threshold:.6f}, max: {max_threshold:.6f}")
        # agg_threshold = single(args, conn)


