import pandas as pd
from bs4 import BeautifulSoup
import argparse
import re
import ast
import numpy as np

# Function to parse confusion matrices
def parse_confusion_matrix(table, use_ids=True):
    rows = table.find_all('tr')
    headers = [header.get_text(strip=True) for header in rows[0].find_all(['th', 'td'])]
    if use_ids:
        headers = [re.match(r'(\d+)', h).group(1) if re.match(r'(\d+)', h) else h for h in headers]
    data = []
    for row in rows[1:]:
        cols = row.find_all(['td', 'th'])
        row_data = [ele.get_text(strip=True) for ele in cols]
        if use_ids:
            row_data[0] = re.match(r'(\d+)', row_data[0]).group(1) if re.match(r'(\d+)', row_data[0]) else row_data[0]
        data.append(row_data)
    df = pd.DataFrame(data, columns=headers)
    return df

# Function to apply custom label mappings with regex support
def apply_label_mapping(df, col_map, row_map):
    def map_labels(label, mapping):
        for pattern, new_label in mapping.items():
            if re.match(pattern, label):
                return new_label
        return label

    if col_map:
        df.columns = [map_labels(col, col_map) for col in df.columns]
    if row_map:
        df.iloc[:,0] = df.iloc[:,0].apply(lambda x: map_labels(x, row_map))
    return df

def calculate_metrics(df):
    df_indexed = df.set_index(df.columns[0])
    
    # Explicitly align columns to match row indices (ensure correct alignment)
    df_indexed = df_indexed.loc[:, df_indexed.index.intersection(df_indexed.columns)]
    matrix = df_indexed.astype(int).values

    assert matrix.shape[0] == matrix.shape[1], (
        f"Confusion matrix must be square! Got shape {matrix.shape}"
    )

    total = matrix.sum()
    accuracy = np.trace(matrix) / total if total else 0

    metrics_per_row = {}
    precision_per_class = []
    recall_per_class = []

    for idx, label in enumerate(df_indexed.index):
        tp = matrix[idx, idx]
        fp = matrix[:, idx].sum() - tp
        fn = matrix[idx, :].sum() - tp

        precision = tp / (tp + fp) if (tp + fp) else 0
        recall = tp / (tp + fn) if (tp + fn) else 0
        f1 = (2 * precision * recall) / (precision + recall) if (precision + recall) else 0

        metrics_per_row[label] = {
            'precision': precision,
            'recall': recall,
            'f1-score': f1
        }

        precision_per_class.append(precision)
        recall_per_class.append(recall)

    avg_precision = np.mean(precision_per_class)
    avg_recall = np.mean(recall_per_class)
    global_f1_score = (
        (2 * avg_precision * avg_recall) / (avg_precision + avg_recall)
        if (avg_precision + avg_recall)
        else 0
    )

    global_metrics = {
        'accuracy': accuracy,
        'precision': avg_precision,
        'recall': avg_recall,
        'f1-score': global_f1_score
    }

    return metrics_per_row, global_metrics




# Convert DataFrame to LaTeX
def dataframe_to_latex(df):
    return df.to_latex(index=False, escape=True)

# Convert metrics to LaTeX
def metrics_to_latex(metrics_per_row, global_metrics):
    rows = [{'Gesto': 'Globální', 'Přesnost': global_metrics['accuracy'], 'Preciznost': global_metrics['precision'], 'Odezva': global_metrics['recall'], 'F1-skóre': global_metrics['f1-score']}]
    for gesture, metrics in metrics_per_row.items():
        row = {'Gesto': gesture, 'Přesnost': '-', 'Preciznost': metrics['precision'], 'Odezva': metrics['recall'], 'F1-skóre': metrics['f1-score']}
        rows.append(row)
    df_metrics = pd.DataFrame(rows)
    return df_metrics.to_latex(index=False, float_format="%.2f")

# Main script
parser = argparse.ArgumentParser(description='Convert HTML confusion matrices to LaTeX format.')
parser.add_argument('--file', required=True, help='Path to the HTML file containing confusion matrices.')
parser.add_argument('--col_map', default='{}', help='Column label mapping with regex support.')
parser.add_argument('--row_map', default='{}', help='Row label mapping with regex support.')
parser.add_argument('--ignore_row', default=None, help='Regex to ignore rows based on row label including IDs.')
parser.add_argument('--calc', action='store_true', help='Calculate evaluation metrics from second matrix.')
args = parser.parse_args()

col_map = ast.literal_eval(args.col_map)
row_map = ast.literal_eval(args.row_map)

html_path = args.file

with open(html_path, 'r', encoding='utf-8') as file:
    soup = BeautifulSoup(file, 'html.parser')

# Extract first confusion matrix
first_matrix = soup.select('div.confusion-matrix table')[0]
df_first = parse_confusion_matrix(first_matrix)
df_first = apply_label_mapping(df_first, col_map, row_map)
latex_first = dataframe_to_latex(df_first)

# Extract and aggregate second confusion matrix (grouped)
second_matrix = soup.select('div[ng-if="groupedConfusionMatrix.length"] table')[0]
df_second = parse_confusion_matrix(second_matrix, use_ids=False)

# Remove ignored rows first
if args.ignore_row:
    df_second = df_second[~df_second.iloc[:, 0].str.match(args.ignore_row)]

# Remove IDs from labels
df_second.iloc[:, 0] = df_second.iloc[:, 0].apply(lambda x: re.sub(r'.*?:\s*', '', x))

# Apply mappings immediately to rows and columns
df_second = apply_label_mapping(df_second, col_map, row_map)

# Set index and aggregate duplicated rows
df_second.set_index(df_second.columns[0], inplace=True)
df_second = df_second.astype(int).groupby(df_second.index).sum()

# Now explicitly reorder columns to match row labels
common_labels = df_second.index.intersection(df_second.columns)
df_second = df_second.loc[common_labels, common_labels].reset_index()

latex_second = dataframe_to_latex(df_second)

# Output LaTeX code
print("LaTeX for Confusion Matrix 1:\n")
print(latex_first)
print("\n\n")

print("LaTeX for Confusion Matrix 2:\n")
print(latex_second)
print("\n\n")

# Calculate and print metrics if requested
if args.calc:
    metrics_per_row, global_metrics = calculate_metrics(df_second)
    latex_metrics = metrics_to_latex(metrics_per_row, global_metrics)
    print("Metrics LaTeX Table:\n")
    print(latex_metrics)
