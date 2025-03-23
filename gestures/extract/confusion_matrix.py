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


import mysql.connector
import numpy as np
import pandas as pd
import argparse
import seaborn as sns
import matplotlib.pyplot as plt
import os
from sklearn.metrics import confusion_matrix
from sklearn.neighbors import KNeighborsClassifier
from datetime import datetime



# Connect to MySQL database
def connect_db(host, user, password, database):
    try:
        return mysql.connector.connect(host=host, user=user, password=password, database=database)
    except mysql.connector.Error as err:
        print(f"❌ Database Connection Error: {err}")
        return None


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
    parser.add_argument("--output_prefix", type=str, default="confusion_matrix_out",
                        help="Output directory prefix for confusion matrices")

    return parser.parse_args()


# Ensure equal number of datalines per position per gesture
def filter_valid_datalines(df):
    counts = df.groupby(['gesture_id', 'position']).size().unstack()
    min_count = counts.min().min()

    if min_count > 0:
        df_filtered = df.groupby(['gesture_id', 'position']).head(min_count)
        return df_filtered
    else:
        print("⚠️ Not enough matching datalines across gestures and positions.")
        return pd.DataFrame()


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
        print(df[['gesture_id', 'position', 'qw', 'qx', 'qy', 'qz']].to_string(index=False))

    return df


# Generate and save confusion matrix for each quaternion component
def generate_confusion_matrices_by_component(df, output_dir):
    df = df.copy()
    gestures = df['gesture_id'].unique()
    samples_per_gesture = df.groupby('gesture_id').size().min()

    os.makedirs(output_dir, exist_ok=True)

    components = ['qw', 'qx', 'qy', 'qz']

    for component in components:
        print(f"\n📈 Generating confusion matrices for component: {component}")
        for i in range(samples_per_gesture):
            sample_df = df.groupby('gesture_id').nth(i).reset_index()
            X = sample_df[[component]].values
            y_true = sample_df['gesture_id'].values

            clf = KNeighborsClassifier(n_neighbors=1)
            clf.fit(X, y_true)
            y_pred = clf.predict(X)

            cm = confusion_matrix(y_true, y_pred, labels=gestures)

            file_base = os.path.join(output_dir, f"{component}_quaternion_{i}")
            plt.figure(figsize=(8, 6))
            sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=gestures, yticklabels=gestures)
            plt.xlabel("Predicted Label")
            plt.ylabel("True Label")
            plt.title(f"Confusion Matrix - {component} - Sample {i}")
            plt.savefig(file_base + ".png")
            plt.close()

            pd.DataFrame(cm, index=gestures, columns=gestures).to_csv(file_base + ".csv")
            print(f"✅ Saved: {file_base}.(png/csv)")

# Unit test: simple test using one quaternion component
def test_single_component_confusion():
    print("\n🧪 Running simple test using list of float values...")
    values = [0.995605, 0.995117, 0.992371, 0.976013]
    # labels = [74, 75, 76, 77]  # Fake gesture IDs just for test
    labels = [74, 74, 76, 76]  # Fake gesture IDs just for test

    X = np.array(values).reshape(-1, 1)
    y = np.array(labels)

    clf = KNeighborsClassifier(n_neighbors=1)
    clf.fit(X, y)
    y_pred = clf.predict(X)

    cm = confusion_matrix(y, y_pred, labels=labels)
    print("Confusion Matrix:")
    print(pd.DataFrame(cm, index=labels, columns=labels))

# Main function
if __name__ == "__main__":
    args = parse_arguments()

    print("\n🔧 Running script with arguments:")
    print(args)

    test_single_component_confusion()

    if False:
        conn = connect_db(args.host, args.user, args.password, args.database)

        if not conn:
            exit(1)

        gesturesStr = '_'.join(map(str, args.gesture_ids))
        output_base_dir = f"{args.output_prefix}_gestures_{gesturesStr}"

        for position in args.positions:
            print(f"\n📡 Processing position: {position}")
            df_gestures = fetch_gesture_data(conn, args.gesture_ids, position)
            if df_gestures is not None:
                df_filtered = filter_valid_datalines(df_gestures)
                if not df_filtered.empty:
                    output_dir = os.path.join(output_base_dir, f"position_{position}")
                    generate_confusion_matrices_by_component(df_filtered, output_dir)

        conn.close()
