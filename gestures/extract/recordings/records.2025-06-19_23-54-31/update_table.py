import mysql.connector
import argparse
from bs4 import BeautifulSoup
import re

def parse_arguments():
    parser = argparse.ArgumentParser(description="Add threshold column from database")
    parser.add_argument("--host", type=str, required=True)
    parser.add_argument("--user", type=str, required=True)
    parser.add_argument("--password", type=str, required=True)
    parser.add_argument("--database", type=str, required=True)
    parser.add_argument("--input_html", type=str, required=True)
    parser.add_argument("--output_html", type=str, required=True)
    return parser.parse_args()

def connect_db(host, user, password, database):
    return mysql.connector.connect(host=host, user=user, password=password, database=database)

args = parse_arguments()
conn = connect_db(args.host, args.user, args.password, args.database)
cursor = conn.cursor()

with open(args.input_html, 'r', encoding='utf-8') as file:
    soup = BeautifulSoup(file, 'html.parser')

# Add header for threshold after first original column
header_row = soup.find('table').find('thead').find('tr')
threshold_header = soup.new_tag('th')
threshold_header.string = 'Threshold'
header_row.find_all('th')[0].insert_after(threshold_header)

# Process rows to add threshold after first original column
tbody = soup.find('table').find('tbody')
for row in tbody.find_all('tr'):
    first_cell_text = row.find('td').get_text()
    match = re.match(r'\s*(\d+):', first_cell_text)
    gesture_id = match.group(1) if match else None

    threshold_value = 'N/A'
    if gesture_id:
        cursor.execute("SELECT shouldMatch FROM Gesture WHERE id = %s", (gesture_id,))
        result = cursor.fetchone()
        if result:
            threshold_value = result[0]

    new_td = soup.new_tag('td')
    new_td.string = str(threshold_value)
    row.find_all('td')[0].insert_after(new_td)

with open(args.output_html, 'w', encoding='utf-8') as file:
    file.write(str(soup))

conn.close()

print(f"✅ Threshold column added. Check '{args.output_html}'")