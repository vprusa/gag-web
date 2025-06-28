import argparse
import pandas as pd
import numpy as np
from bs4 import BeautifulSoup
from io import StringIO


gesture_mapping = {
    'T-R-EACH-FINGER-U-D': 'EACH',
    'T-R-INDEX-U-D': 'INDEX',
    'Z-R-FIST': 'FIST',
    'Z-R-MAYBE-L-M-R-M': 'MAYBE',
    'Z-R-OK': 'OK',
    'Z-R-SWITCH-AND-BACK': 'SW-BACK',
    'Z-R-SWITCH-FIST': 'SW-FIST',
    'Z-R-SWITCH-Y180': 'SW',
    'Z-R-VICTORIA': 'VICT',
    'Z-R-WAVE-L45-M-R45-M': 'WAVE'
}

def parse_arguments():
    parser = argparse.ArgumentParser(description="Generate LaTeX confusion matrices from HTML table")
    parser.add_argument("--input_html", type=str, required=True)
    parser.add_argument("--best", action='store_true', help="Process best gestures HTML file")
    return parser.parse_args()

# def calculate_metrics(df, gestures_short):
#     cm = df[gestures_short].values
#     TP = np.diag(cm)
#     FP = cm.sum(axis=0) - TP
#     FN = cm.sum(axis=1) - TP
#     TN = cm.sum() - (TP + FP + FN)

#     accuracy = (TP + TN) / (TP + TN + FP + FN)
#     precision = TP / (TP + FP)
#     recall = TP / (TP + FN)
#     f1_score = 2 * (precision * recall) / (precision + recall)

#     accuracy[np.isnan(accuracy)] = 0
#     precision[np.isnan(precision)] = 0
#     recall[np.isnan(recall)] = 0
#     f1_score[np.isnan(f1_score)] = 0

#     global_stats = pd.DataFrame([{
#         'Gesto': 'Globální',
#         'Přesnost': np.mean(accuracy),
#         'Preciznost': np.mean(precision),
#         'Odezva': np.mean(recall),
#         'F1-skóre': np.mean(f1_score)
#     }])

#     stats_df = pd.DataFrame({
#         'Gesto': gestures_short,
#         'Přesnost': accuracy,
#         'Preciznost': precision,
#         'Odezva': recall,
#         'F1-skóre': f1_score
#     })

#     return pd.concat([global_stats, stats_df], ignore_index=True)

import numpy as np
import pandas as pd

def calculate_metrics(df, gestures_short):
    TOTAL_RECORDINGS_PER_GESTURE = 50
    TOTAL_RECORDINGS = TOTAL_RECORDINGS_PER_GESTURE * len(gestures_short)

    cm = df[gestures_short].values
    TP = np.diag(cm)
    FP = cm.sum(axis=0) - TP
    FN = TOTAL_RECORDINGS_PER_GESTURE - TP
    TN = TOTAL_RECORDINGS - (TP + FP + FN)

    # Calculation based on provided equations
    accuracy = (TP + TN) / (TP + TN + FP + FN)
    precision = TP / (TP + FP)
    recall = TP / (TP + FN)
    f1_score = 2 * (precision * recall) / (precision + recall)

    # Handle division by zero
    accuracy = np.nan_to_num(accuracy)
    precision = np.nan_to_num(precision)
    recall = np.nan_to_num(recall)
    f1_score = np.nan_to_num(f1_score)

    global_stats = pd.DataFrame([{
        'Gesto': 'Globální',
        'Přesnost': np.mean(accuracy),
        'Preciznost': np.mean(precision),
        'Odezva': np.mean(recall),
        'F1-skóre': np.mean(f1_score)
    }])

    stats_df = pd.DataFrame({
        'Gesto': gestures_short,
        'Přesnost': accuracy,
        'Preciznost': precision,
        'Odezva': recall,
        'F1-skóre': f1_score
    })

    return pd.concat([global_stats, stats_df], ignore_index=True)


def main():
    args = parse_arguments()

    with open(args.input_html, 'r', encoding='utf-8') as file:
        soup = BeautifulSoup(file, 'html.parser')

    df = pd.read_html(StringIO(str(soup.find('table'))))[0]

    if args.best:
        gestures_short = list(gesture_mapping.values())

        conf_matrix = df[['Gesture', 'Group', 'Threshold'] + gestures_short].copy()
        conf_matrix.set_index('Gesture', inplace=True)
        conf_matrix.index = conf_matrix.index.map(gesture_mapping)

        stats_df = calculate_metrics(conf_matrix, gestures_short)

        latex_conf_matrix = conf_matrix.to_latex(
            caption='Matice záměn pro skupinu',
            label='tab:matice_zamen_nejlepsi',
            index=True
        ).replace('\\begin{table}', '\\begin{table}\n    \\centering')

        latex_stats = stats_df.to_latex(
            index=False,
            caption='Statistiky pro nejlepší ze skupin',
            label='tab:statistiky_nejlepsi'
        ).replace('\\begin{table}', '\\begin{table}\n    \\centering')

        print(latex_conf_matrix)
        print(latex_stats)
    else:
        print("Regular processing for non-best files not implemented.")

if __name__ == '__main__':
    main()