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
    parser.add_argument("--calc-all", action="store_true", help="Enable brute-force gesture coverage evaluation")
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


if __name__ == "__main__":
    args = parse_arguments()
    conn = connect_db(args.host, args.user, args.password, args.database)

    if args.calc_all:
        all_gestures = list(set(args.gestures + args.ref_gestures))
        matrix_data = {}
        label_thresholds = {}

        print(f"\n🚀 Starting brute-force evaluation on {len(all_gestures)} gestures...")

        for r in range(1, len(all_gestures)):
            for ref_combo in combinations(all_gestures, r):
                test_combo = [g for g in all_gestures if g not in ref_combo]
                if not test_combo:
                    continue

                ref_list = list(ref_combo)
                col_label_base = f"ref_{'-'.join(map(str, ref_list))}"
                total_matches = 0
                combo_threshold = None

                for pos in args.positions:
                    ref_df = fetch_gesture_data(conn, ref_list, pos)
                    if ref_df.empty:
                        continue

                    avg_ref, threshold = compute_avg_reference_and_threshold(
                        ref_df,
                        override_threshold=args.threshold if args.calc_threshold else args.threshold,
                        calc_max=args.calc_max,
                        use_angular=args.angular_diff
                    )
                    combo_threshold = threshold

                    input_df = fetch_gesture_data(conn, test_combo, pos)
                    if input_df.empty:
                        continue

                    categorized_df = categorize_by_angular_distance(input_df, avg_ref, threshold)
                    matches = categorized_df['matched'].sum()
                    total_matches += matches

                    for gesture_id in test_combo:
                        row_label = f"gesture_{gesture_id}"
                        if row_label not in matrix_data:
                            matrix_data[row_label] = {}
                        label_with_threshold = f"{col_label_base}@{combo_threshold:.3f}"
                        matrix_data[row_label][label_with_threshold] = matrix_data[row_label].get(label_with_threshold, 0) + \
                            categorized_df[categorized_df['gesture_id'] == gesture_id]['matched'].sum()

        all_cols = sorted(matrix_data[next(iter(matrix_data))].keys())
        matrix_df = pd.DataFrame.from_dict(matrix_data, orient='index').fillna(0).astype(int)[all_cols]
        matrix_df = matrix_df.transpose()  # 🔁 Switch axes: now rows=ref_gestures, cols=input_gestures

        # ➕ Add a 'Total' column with row-wise sum (total matches per reference combination)
        matrix_df['Total'] = matrix_df.sum(axis=1)

        # ➕ Compute 'Trust' as Total / (#ref_gestures * #input_gestures)
        trust_scores = []
        ref_counts = []

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
            num_inputs = len(matrix_df.columns) - 2  # excluding Total and Trust
            total = matrix_df.at[row_label, 'Total']
            # 📐 Option A: Matches per reference-input pair
            trust_a = total / (num_refs * num_inputs) if num_refs * num_inputs > 0 else 0
            # 📊 Option B: Matches per input gesture (ignoring ref count)
            trust_b = total / num_inputs if num_inputs > 0 else 0
            # 🧮 Option C: Matches per ref gesture (ignoring input count)
            trust_c = total / num_refs if num_refs > 0 else 0
            # 🎯 Choose your preferred trust strategy:
            trust = trust_c  # change to trust_b or trust_c if desired

            trust_scores.append(trust)

        matrix_df['Trust'] = trust_scores

        # ➕ Add 'Threshold' from label_thresholds
        thresholds = []
        for row_label in matrix_df.index:
            threshold_val = None
            for key in label_thresholds:
                if key in row_label:
                    threshold_val = label_thresholds[key]
                    break
            thresholds.append(threshold_val if threshold_val is not None else 0)
        matrix_df['Threshold'] = thresholds

        # 🔀 Reorder rows by number of reference gestures and alphabetically by gesture IDs
        matrix_df['RefCount'] = ref_counts
        matrix_df['RefKey'] = matrix_df.index.to_series().apply(
            lambda x: '-'.join(sorted(x.split('@')[0].replace('ref_', '').split('-'), key=int)))
        matrix_df['Threshold'] = matrix_df.index.to_series().apply( lambda x: float(x.split('@')[1]))
        # threshold_df = matrix_df.index.to_series().apply( lambda x: float(x.split('@')[1]))
        matrix_df = matrix_df.sort_values(by=['RefCount', 'Total'], ascending=[True, False])
        threshold_df = matrix_df[['Threshold']]
        matrix_df = matrix_df.drop(columns=['RefCount', 'RefKey', 'Threshold'])

        print("\n📊 Brute-force Gesture Coverage Matrix (match counts):")
        print(matrix_df.to_string())

        # ➖ Extract columns to be plotted separately
        heatmap_df = matrix_df.drop(columns=['Total', 'Trust']).copy()

        # Create visual versions with '-' for gesture overlap and proper formatting
        display_df = heatmap_df.fillna('-')

        total_df = matrix_df[['Total']]
        trust_df = matrix_df[['Trust']]

        # 🔧 Create figure and axes manually (no GridSpec)
        fig, axes = plt.subplots(1, 4, figsize=(14, 0.2 * len(matrix_df)))
        ax0, ax1, ax2, ax3 = axes

        sns.heatmap(heatmap_df.astype(float), annot=display_df.values, fmt='', cmap='YlGnBu', ax=ax0, cbar=False,
                    linewidths=0.5, linecolor='darkgrey')
        ax0.set_title("Match Counts")
        ax0.set_xlabel("Input Gestures")
        ax0.set_ylabel("Reference Gesture Combinations")
        ax0.tick_params(axis='x', rotation=45)

        sns.heatmap(total_df, annot=True, fmt='d', cmap='Greens', ax=ax1, cbar=False)
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
        os.makedirs("out_heatmap", exist_ok=True)
        heatmap_path = os.path.join("out_heatmap", "brute_force_heatmap.png")
        plt.savefig(heatmap_path)
        plt.close()
        print(f"\n✅ Saved brute-force coverage heatmap to {heatmap_path}")
        conn.close()
        sys.exit(0)

