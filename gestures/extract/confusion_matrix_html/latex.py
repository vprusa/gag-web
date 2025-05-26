import pandas as pd
import argparse
import numpy as np

# gesture_mapping = {
#     'T-R-INDEX-U-D': 'INDEX',
#     'T-R-EACH-FINGER-U-D': 'EACH',
#     'Z-R-WAVE-L45-M-R45-M': 'WAVE',
#     'Z-R-SWITCH-Y180': 'SW',
#     'Z-R-OK': 'OK',
#     'Z-R-SWITCH-AND-BACK': 'SW-BACK',
#     'Z-R-FIST': 'FIST',
#     'Z-R-SWITCH-FIST': 'SW-FIST',
#     'Z-R-MAYBE-L-M-R-M': 'MAYBE',
#     'Z-R-VICTORIA': 'VICT'
# }

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


def calculate_metrics(conf_matrix):
    gestures = conf_matrix.index
    n = len(gestures)

    TP = np.diag(conf_matrix)
    FP = conf_matrix.sum(axis=0) - TP
    FN = conf_matrix.sum(axis=1) - TP
    TN = conf_matrix.values.sum() - (TP + FP + FN)

    accuracy = ((TP + TN) / (TP + TN + FP + FN)).round(6)
    precision = (TP / (TP + FP)).fillna(0).round(6)
    recall = (TP / (TP + FN)).fillna(0).round(6)
    f1_score = (2 * precision * recall / (precision + recall)).fillna(0).round(6)

    global_accuracy = accuracy.mean().round(6)
    global_precision = precision.mean().round(6)
    global_recall = recall.mean().round(6)
    global_f1 = f1_score.mean().round(6)

    global_stats = pd.DataFrame([{
        'Gesto': 'Globální',
        'Přesnost': global_accuracy,
        'Preciznost': global_precision,
        'Odezva': global_recall,
        'F1-skóre': global_f1
    }])

    stats_df = pd.DataFrame({
        'Gesto': gestures,
        #'Přesnost': ['-'] * n,
        'Přesnost': accuracy.round(6),
        'Preciznost': precision,
        'Odezva': recall,
        'F1-skóre': f1_score
    })

    return pd.concat([global_stats, stats_df], ignore_index=True)

def main(csv_file):
    data = pd.read_csv(csv_file)
    data['suffix'] = data['Identifier'].str.extract(r'_(0\.\d)$')
    gestures = list(gesture_mapping.keys())

    for suffix in ['0.3', '0.4', '0.5']:
        group_data = data[data['suffix'] == suffix]

        conf_matrix = pd.DataFrame(index=gestures, columns=gestures)
        for gesture in gestures:
            row = group_data[group_data['Identifier'].str.contains(gesture)]
            if not row.empty:
                conf_matrix.loc[gesture] = row.iloc[0, 1:11].values
            else:
                conf_matrix.loc[gesture] = [0]*len(gestures)

        conf_matrix = conf_matrix.fillna(0).astype(int)
        conf_matrix.index = conf_matrix.index.map(gesture_mapping)
        conf_matrix.columns = conf_matrix.columns.map(gesture_mapping)

        stats_df = calculate_metrics(conf_matrix)

        latex_conf_matrix = conf_matrix.to_latex(
            caption=f'Matice záměn při hraniční hodnotě {suffix}',
            label=f'tab:matice_zamen_{suffix}',
            index=True
        ).replace('\\begin{table}', '\\begin{table}\n    \\centering')

        latex_stats = stats_df.to_latex(
            index=False,
            caption=f'Statistiky při hraniční hodnotě {suffix}',
            label=f'tab:statistiky_{suffix}'
        ).replace('\\begin{table}', '\\begin{table}\n    \\centering')

        print(latex_conf_matrix)
        print(latex_stats)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Generuje LaTeX tabulky ze souboru CSV s daty gest.')
    parser.add_argument('--file', required=True, help='Cesta k CSV souboru.')
    args = parser.parse_args()
    main(args.file)
