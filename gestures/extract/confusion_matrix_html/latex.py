import pandas as pd
import argparse

# Mapování gest
gesture_mapping = {
    'T-R-INDEX-U-D': 'INDEX',
    'T-R-EACH-FINGER-U-D': 'EACH',
    'Z-R-WAVE-L45-M-R45-M': 'WAVE',
    'Z-R-SWITCH-Y180': 'SW',
    'Z-R-OK': 'OK',
    'Z-R-SWITCH-AND-BACK': 'SW-BACK',
    'Z-R-FIST': 'FIST',
    'Z-R-SWITCH-FIST': 'SW-FIST',
    'Z-R-MAYBE-L-M-R-M': 'MAYBE',
    'Z-R-VICTORIA': 'VICT'
}

# Výpočet statistik
def calculate_statistics(conf_matrix):
    diagonal = pd.Series([conf_matrix.iloc[i, i] for i in range(len(conf_matrix))], index=conf_matrix.index)
    precision = diagonal / conf_matrix.sum(axis=0)
    recall = diagonal / conf_matrix.sum(axis=1)
    f1_score = 2 * (precision * recall) / (precision + recall)

    precision = precision.fillna(0)
    recall = recall.fillna(0)
    f1_score = f1_score.fillna(0)

    global_accuracy = diagonal.sum() / conf_matrix.values.sum()
    global_precision = precision.mean()
    global_recall = recall.mean()
    global_f1 = f1_score.mean()

    stats_df = pd.DataFrame({
        'Gesto': conf_matrix.index,
        'Přesnost': ['-'] * len(conf_matrix),
        'Preciznost': precision.round(2),
        'Odezva': recall.round(2),
        'F1-skóre': f1_score.round(2)
    })

    global_stats = pd.DataFrame([{
        'Gesto': 'Globální',
        'Přesnost': round(global_accuracy, 2),
        'Preciznost': round(global_precision, 2),
        'Odezva': round(global_recall, 2),
        'F1-skóre': round(global_f1, 2)
    }])

    return pd.concat([global_stats, stats_df], ignore_index=True)

# Hlavní funkce
def main(csv_file):
    data = pd.read_csv(csv_file)
    data['suffix'] = data['Identifier'].str.extract(r'_(0\.\d)$')
    complete_gesture_set = list(gesture_mapping.keys())

    for suffix in ['0.3', '0.4', '0.5']:
        group_data = data[data['suffix'] == suffix]

        conf_matrix = pd.DataFrame(0, index=complete_gesture_set, columns=complete_gesture_set)
        for _, row in group_data.iterrows():
            actual_gesture = row['Identifier'].split(': ')[1].split('_')[0]
            conf_matrix.loc[actual_gesture] = row[complete_gesture_set].reindex(complete_gesture_set, fill_value=0).values

        conf_matrix = conf_matrix.rename(index=gesture_mapping, columns=gesture_mapping)
        stats_df = calculate_statistics(conf_matrix)

        #print(f'\nHraniční hodnota {suffix}: Matice záměn\n')
        print(conf_matrix.to_latex(caption=f'Matice záměn při hraniční hodnotě {suffix}', label=f'tab:matice_zamen_{suffix}').replace('\\begin{table}', '\\begin{table}\n    \centering'))

        #print(f'\nHraniční hodnota {suffix}: Statistiky\n')
        print(stats_df.to_latex(index=False, caption=f'Statistiky při hraniční hodnotě {suffix}', label=f'tab:statistiky_{suffix}').replace('\\begin{table}', '\\begin{table}\n    \centering'))

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Generuje LaTeX tabulky ze souboru CSV s daty gest.')
    parser.add_argument('--file', required=True, help='Cesta k CSV souboru.')
    args = parser.parse_args()
    main(args.file)