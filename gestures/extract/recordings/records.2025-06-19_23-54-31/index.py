from bs4 import BeautifulSoup

# Load HTML content
with open('merged.html', 'r', encoding='utf-8') as file:
    html_content = file.read()

soup = BeautifulSoup(html_content, 'html.parser')

# Index rows globally across all tables by adding a new column
global_idx = 0
for table in soup.find_all('table'):
    tbody = table.find('tbody')
    if tbody:
        for row in tbody.find_all('tr'):
            new_td = soup.new_tag('td')
            new_td.string = str(global_idx)
            row.insert(0, new_td)
            global_idx += 1

# Save modified HTML to a new file
with open('merged_indexed.html', 'w', encoding='utf-8') as file:
    file.write(str(soup))

print("Indexing complete. Check 'merged_indexed.html'")