# import argparse
# import pandas as pd
# import numpy as np
# from bs4 import BeautifulSoup

# gesture_mapping = {
#     'T-R-EACH-FINGER-U-D': 'EACH',
#     'T-R-INDEX-U-D': 'INDEX',
#     'Z-R-FIST': 'FIST',
#     'Z-R-MAYBE-L-M-R-M': 'MAYBE',
#     'Z-R-OK': 'OK',
#     'Z-R-SWITCH-AND-BACK': 'SW-BACK',
#     'Z-R-SWITCH-FIST': 'SW-FIST',
#     'Z-R-SWITCH-Y180': 'SW',
#     'Z-R-VICTORIA': 'VICT',
#     'Z-R-WAVE-L45-M-R45-M': 'WAVE'
# }


# #     gesture_mapping = {
# #     'T-R-EACH-FINGER-U-D': 'EACH',
# #     'T-R-INDEX-U-D': 'INDEX',
# #     'Z-R-FIST': 'FIST',
# #     'Z-R-MAYBE-L-M-R-M': 'MAY',
# #     'Z-R-OK': 'OK',
# #     'Z-R-SWITCH-AND-BACK': 'SW-B',
# #     'Z-R-SWITCH-FIST': 'SW-F',
# #     'Z-R-SWITCH-Y180': 'SW',
# #     'Z-R-VICTORIA': 'VICT',
# #     'Z-R-WAVE-L45-M-R45-M': 'WAVE'
# # }


# def parse_arguments():
#     parser = argparse.ArgumentParser(description="Generate LaTeX confusion matrices from HTML table")
#     parser.add_argument("--input_html", type=str, required=True)
#     parser.add_argument("--best", action='store_true', help="Process best gestures HTML file")
#     return parser.parse_args()

# # def calculate_metrics(conf_matrix):
# #     gestures = conf_matrix.index
# #     TP = np.diag(conf_matrix)
# #     FP = conf_matrix.sum(axis=0) - TP
# #     FN = conf_matrix.sum(axis=1) - TP
# #     TN = conf_matrix.values.sum() - (TP + FP + FN)

# #     accuracy = ((TP + TN) / (TP + TN + FP + FN)).round(6)
# #     precision = (TP / (TP + FP)).fillna(0).round(6)
# #     recall = (TP / (TP + FN)).fillna(0).round(6)
# #     f1_score = (2 * precision * recall / (precision + recall)).fillna(0).round(6)

# #     global_stats = pd.DataFrame([{
# #         'Gesto': 'Globální',
# #         'Přesnost': accuracy.mean(),
# #         'Preciznost': precision.mean(),
# #         'Odezva': recall.mean(),
# #         'F1-skóre': f1_score.mean()
# #     }])

# #     stats_df = pd.DataFrame({
# #         'Gesto': gestures,
# #         'Přesnost': accuracy,
# #         'Preciznost': precision,
# #         'Odezva': recall,
# #         'F1-skóre': f1_score
# #     })

# #     return pd.concat([global_stats, stats_df], ignore_index=True)

# def calculate_metrics(df, gestures):
#     conf_matrix = df[gestures].to_numpy()
#     actual = np.array([gesture for gesture in df.index])

#     # Actual and predicted labels for confusion matrix
#     y_true, y_pred = [], []
#     for i, gesture in enumerate(actual):
#         y_true.extend([gesture] * conf_matrix[i].sum())
#         for j, pred_count in enumerate(conf_matrix[i]):
#             y_pred.extend([gestures[j]] * pred_count)

#     from sklearn.metrics import confusion_matrix, accuracy_score, precision_score, recall_score, f1_score

#     cm = confusion_matrix(y_true, y_pred, labels=gestures)

#     accuracy = accuracy_score(y_true, y_pred)
#     precision = precision_score(y_true, y_pred, labels=gestures, average=None, zero_division=0)
#     recall = recall_score(y_true, y_pred, labels=gestures, average=None, zero_division=0)
#     f1 = f1_score(y_true, y_pred, labels=gestures, average=None, zero_division=0)

#     global_stats = pd.DataFrame({
#         'Gesto': ['Globální'],
#         'Přesnost': [accuracy],
#         'Preciznost': [np.mean(precision)],
#         'Odezva': [np.mean(recall)],
#         'F1-skóre': [np.mean(f1)]
#     })

#     stats_df = pd.DataFrame({
#         'Gesto': gestures,
#         'Přesnost': [accuracy] * len(gestures),
#         'Preciznost': precision,
#         'Odezva': recall,
#         'F1-skóre': f1
#     })

#     return pd.concat([global_stats, stats_df], ignore_index=True)

# def main():
#     args = parse_arguments()

#     with open(args.input_html, 'r', encoding='utf-8') as file:
#         soup = BeautifulSoup(file, 'html.parser')

#     df = pd.read_html(str(soup.find('table')))[0]

#     if args.best:
#         gestures_short = list(gesture_mapping.values())
#         conf_matrix = df[gestures_short].astype(int)
#         conf_matrix.index = df['Gesture'].map(gesture_mapping)

#         conf_matrix.insert(0, 'Threshold', df['Threshold'].values)
#         conf_matrix.insert(0, 'Group', df['Group'].values)

#         # stats_df = calculate_metrics(conf_matrix[gestures_short].astype(int))
#         stats_df = calculate_metrics(df, gestures_short)

#         latex_conf_matrix = conf_matrix.to_latex(
#             caption='Matice záměn pro skupinu',
#             label='tab:matice_zamen_nejlepsi',
#             index=True
#         ).replace('\\begin{table}', '\\begin{table}\n    \\centering')

#         latex_stats = stats_df.to_latex(
#             index=False,
#             caption='Statistiky pro nejlepší ze skupin',
#             label='tab:statistiky_nejlepsi'
#         ).replace('\\begin{table}', '\\begin{table}\n    \\centering')

#         print(latex_conf_matrix)
#         print(latex_stats)
#     else:
#         print("Regular processing for non-best files not implemented.")

# if __name__ == '__main__':
#     main()


import argparse
import pandas as pd
import numpy as np
from bs4 import BeautifulSoup
from io import StringIO
from sklearn.metrics import confusion_matrix, accuracy_score, precision_score, recall_score, f1_score

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
def calculate_metrics(df, gestures_short):
    y_true, y_pred = [], []

    for idx, row in df.iterrows():
        true_label = gesture_mapping[row['Gesture']]
        for pred_label in gestures_short:
            count = row[pred_label]
            y_true.extend([true_label] * count)
            y_pred.extend([pred_label] * count)

    labels = gestures_short

    cm = confusion_matrix(y_true, y_pred, labels=labels)

    accuracy = accuracy_score(y_true, y_pred)
    precision = precision_score(y_true, y_pred, labels=labels, average=None, zero_division=0)
    recall = recall_score(y_true, y_pred, labels=labels, average=None, zero_division=0)
    f1 = f1_score(y_true, y_pred, labels=labels, average=None, zero_division=0)

    global_stats = pd.DataFrame({
        'Gesto': ['Globální'],
        'Přesnost': [accuracy],
        'Preciznost': [np.mean(precision)],
        'Odezva': [np.mean(recall)],
        'F1-skóre': [np.mean(f1)]
    })

    stats_df = pd.DataFrame({
        'Gesto': labels,
        'Přesnost': accuracy,
        'Preciznost': precision,
        'Odezva': recall,
        'F1-skóre': f1
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

        stats_df = calculate_metrics(df, gestures_short)

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