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

# Corrected function to calculate evaluation metrics
def calculate_metrics(df):
    df_indexed = df.set_index(df.columns[0])
    matrix = df_indexed.astype(int).values

    assert matrix.shape[0] == matrix.shape[1], (
        f"Confusion matrix must be square! Got shape {matrix.shape}"
    )

    TP = np.trace(matrix)
    total = matrix.sum()
    accuracy = TP / total if total else 0

    precision_per_class = []
    recall_per_class = []

    for i in range(matrix.shape[0]):
        tp = matrix[i, i]
        fp = matrix[:, i].sum() - tp
        fn = matrix[i, :].sum() - tp

        precision = tp / (tp + fp) if (tp + fp) else 0
        recall = tp / (tp + fn) if (tp + fn) else 0

        precision_per_class.append(precision)
        recall_per_class.append(recall)

    avg_precision = np.mean(precision_per_class)
    avg_recall = np.mean(recall_per_class)
    f1_score = (
        (2 * avg_precision * avg_recall) / (avg_precision + avg_recall)
        if (avg_precision + avg_recall)
        else 0
    )

    return accuracy, avg_precision, avg_recall, f1_score

# Convert DataFrame to LaTeX
def dataframe_to_latex(df):
    return df.to_latex(index=False, escape=True)

# Main script
parser = argparse.ArgumentParser(description='Convert HTML confusion matrices to LaTeX format.')
parser.add_argument('--file', required=True, help='Path to the HTML file containing confusion matrices.')
parser.add_argument('--col_map', default='{}', help='Column label mapping with regex support.')
parser.add_argument('--row_map', default='{}', help='Row label mapping with regex support.')
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

# # Extract and aggregate second confusion matrix (grouped)
# second_matrix = soup.select('div[ng-if="groupedConfusionMatrix.length"] table')[0]
# df_second = parse_confusion_matrix(second_matrix, use_ids=False)
# df_second = apply_label_mapping(df_second, {}, row_map)
# df_second.iloc[:, 0] = df_second.iloc[:, 0].apply(lambda x: re.sub(r'.*?:\\s*', '', x))
# df_second.set_index(df_second.columns[0], inplace=True)
# df_second = df_second.astype(int).groupby(df_second.index).sum().reset_index()
# df_second = apply_label_mapping(df_second, col_map, {})
# latex_second = dataframe_to_latex(df_second)

# Extract second confusion matrix (grouped)
second_matrix = soup.select('div[ng-if="groupedConfusionMatrix.length"] table')[0]
df_second = parse_confusion_matrix(second_matrix, use_ids=False)

# First remove IDs from labels (everything before ':')
df_second.iloc[:, 0] = df_second.iloc[:, 0].apply(lambda x: re.sub(r'^.*?:\s*', '', x))

# Now apply row label mappings using regex
df_second = apply_label_mapping(df_second, {}, row_map)

# Aggregate rows with the same label
df_second.set_index(df_second.columns[0], inplace=True)
df_second = df_second.astype(int).groupby(df_second.index).sum().reset_index()

# Apply column mappings
df_second = apply_label_mapping(df_second, col_map, {})
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
    accuracy, precision, recall, f1_score = calculate_metrics(df_second)
    print(f"Accuracy: {accuracy:.2f}")
    print(f"Precision: {precision:.2f}")
    print(f"Recall: {recall:.2f}")
    print(f"F1-score: {f1_score:.2f}")
