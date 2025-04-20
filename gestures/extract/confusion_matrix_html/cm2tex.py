import pandas as pd
from bs4 import BeautifulSoup
import argparse
import re

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

# Convert DataFrame to LaTeX
def dataframe_to_latex(df):
    return df.to_latex(index=False, escape=True)

# Main script
parser = argparse.ArgumentParser(description='Convert HTML confusion matrices to LaTeX format.')
parser.add_argument('--file', required=True, help='Path to the HTML file containing confusion matrices.')
args = parser.parse_args()

html_path = args.file

with open(html_path, 'r', encoding='utf-8') as file:
    soup = BeautifulSoup(file, 'html.parser')

# Extract the first confusion matrix
first_matrix = soup.select('div.confusion-matrix table')[0]
df_first = parse_confusion_matrix(first_matrix)
latex_first = dataframe_to_latex(df_first)

# Extract the second confusion matrix (grouped)
second_matrix = soup.select('div[ng-if="groupedConfusionMatrix.length"] table')[0]
df_second = parse_confusion_matrix(second_matrix, use_ids=False)
latex_second = dataframe_to_latex(df_second)

# Output LaTeX code
print("LaTeX for Confusion Matrix 1:\n")
print(latex_first)
print("\n\n")

print("LaTeX for Confusion Matrix 2:\n")
print(latex_second)
print("\n\n")