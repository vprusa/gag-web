from bs4 import BeautifulSoup

# Load HTML content
with open('merged.html', 'r', encoding='utf-8') as file:
    html_content = file.read()

soup = BeautifulSoup(html_content, 'html.parser')

# Extract the header from the first table
first_table_header = soup.find('table').find('thead')

# Collect all rows globally
all_rows = []
for table in soup.find_all('table'):
    tbody = table.find('tbody')
    if tbody:
        all_rows.extend(tbody.find_all('tr'))

# Specify indexes of rows to extract
# filtered_indexes = {0, 2, 5}
# filtered_indexes = {0,1,2,3,258,259,256,257,107,108,109,110,111,173,174,175,176,205,206,207,208,209,210,211,229,230,231,231,232,233,234,235,246,247,258,248,249,250,251,252,253,254,255}
filtered_indexes = {0,1,2,3,283,284,285,286,107,108,109,110,111,173,174,175,176,205,206,207,208,209,210,211,229,274,275,276,277,234,235,246,247,258,262,263,264,265,252,253,254,255}


# Output HTML
print("<table>")
if first_table_header:
    print(first_table_header)
print("<tbody>")
for idx, row in enumerate(all_rows):
    if idx in filtered_indexes:
        print(row)
print("</tbody>")
print("</table>")
