import numpy as np
import pandas as pd
import mysql.connector
import argparse

# Quaternion similarity measure (angle between quaternions)
def quaternion_angle(q1, q2):
    dot_product = np.abs(np.clip(np.dot(q1, q2), -1.0, 1.0))
    angle = 2 * np.arccos(dot_product)
    return angle

# Normalize quaternion
def normalize_quat(q):
    return q / np.linalg.norm(q)

# Corrected recognition algorithm
def recognize_gestures(input_quats, ref_quats, threshold_rad, verbose=False):
    recognition_results = []
    partial_matches = []
    partial_match_counter = 0

    ref_quats = np.array([normalize_quat(q) for q in ref_quats])

    for idx, row in input_quats.iterrows():
        input_quat = normalize_quat(row[['qw', 'qx', 'qy', 'qz']].values)
        timestamp = row['timestamp']
        matches_info = {'timestamp': timestamp, 'matched_refs': [], 'match_counts': 0, 'partial_matches': []}

        updated_partial_matches = []
        for match in partial_matches:
            next_index = match['ref_idx']
            angle_diff = quaternion_angle(input_quat, ref_quats[next_index])
            if angle_diff <= threshold_rad:
                match['ref_idx'] += 1
                if verbose:
                    print(f"Partial match id {match['id']} advanced to {match['ref_idx']} at {timestamp}")
                if match['ref_idx'] == len(ref_quats):
                    matches_info['matched_refs'].append(match['id'])
                    matches_info['match_counts'] += 1
                    if verbose:
                        print(f"Complete match by id {match['id']} at {timestamp}")
                else:
                    updated_partial_matches.append(match)
            else:
                updated_partial_matches.append(match)
                if verbose:
                    print(f"Partial match id {match['id']} did not advance at {timestamp}, angle: {angle_diff:.6f}")

        angle_diff_first_ref = quaternion_angle(input_quat, ref_quats[0])
        if angle_diff_first_ref <= threshold_rad:
            updated_partial_matches.append({'id': partial_match_counter, 'ref_idx': 1, 'start_time': timestamp})
            if verbose:
                print(f"New partial match id {partial_match_counter} started at {timestamp}")
            partial_match_counter += 1

        partial_matches = updated_partial_matches

        matches_info['partial_matches'] = [f"{match['id']}:{match['ref_idx']}" for match in partial_matches]

        if verbose:
            print(f"Timestamp: {timestamp}, Input quat: {input_quat}")
            print(f"Current Partial Matches: {matches_info['partial_matches']}")

        recognition_results.append(matches_info)

    results_df = input_quats.copy()
    results_df['matched_refs'] = [res['matched_refs'] for res in recognition_results]
    results_df['match_counts'] = [res['match_counts'] for res in recognition_results]
    results_df['partial_matches'] = [res['partial_matches'] for res in recognition_results]

    return results_df

def connect_db(host, user, password, database):
    return mysql.connector.connect(host=host, user=user, password=password, database=database)

def fetch_gesture_data(conn, gesture_ids, positions):
    query = f"""
    SELECT dl.id, dl.position, dl.timestamp,
           fdl.quatA AS qw, fdl.quatX AS qx, fdl.quatY AS qy, fdl.quatZ AS qz,
           dl.gesture_id, g.shouldMatch, g.userAlias
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

def to_latex_table(df):
    latex = """\\begin{table}[ht]
    \\centering
    \\resizebox{\\textwidth}{!}{%
    \\begin{tabular}{c c c c c c c c}
        \\toprule
        Timestamp & qw & qx & qy & qz & Matched Refs & Match Counts & Partial Matches \\
        \\midrule
"""
    for _, row in df.iterrows():
        matched_refs = ','.join(map(str, row['matched_refs'])) if row['matched_refs'] else '-'
        partial_matches = ','.join(row['partial_matches']) if row['partial_matches'] else '-'
        latex += f"{row['timestamp']} & {row['qw']:.6f} & {row['qx']:.6f} & {row['qy']:.6f} & {row['qz']:.6f} & {matched_refs} & {row['match_counts']} & {partial_matches} \\\\ \n"
    latex += """        \\bottomrule
    \\end{tabular}%
    }
    \\caption{Gesture Recognition Results}
    \\label{tab:gesture_recognition_results}
\\end{table}"""
    return latex

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--in-gest", type=int, required=True)
    parser.add_argument("--ref-gest", type=int, required=True)
    parser.add_argument("--host", type=str, required=True)
    parser.add_argument("--user", type=str, required=True)
    parser.add_argument("--password", type=str, required=True)
    parser.add_argument("--database", type=str, required=True)
    parser.add_argument("--threshold", type=float, default=0.141197)
    parser.add_argument("--ref-gest-quat-order", type=str, default="axyz")
    parser.add_argument("-v", "--verbose", action="store_true")
    args = parser.parse_args()

    conn = connect_db(args.host, args.user, args.password, args.database)
    input_quats = fetch_gesture_data(conn, [args.in_gest], [1])
    ref_quats_df = fetch_gesture_data(conn, [args.ref_gest], [1])
    ref_quats_df = ref_quats_df.rename(columns={'qw':'a', 'qx':'x', 'qy':'y', 'qz':'z'})
    ref_quats = ref_quats_df[list(args.ref_gest_quat_order)].values

    results_df = recognize_gestures(input_quats, ref_quats, args.threshold, args.verbose)
    print(to_latex_table(results_df))
