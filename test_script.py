import urllib.request
import json
url = "https://api.stackexchange.com/2.3/search/advanced?order=desc&sort=relevance&q=attributionTag+not+declared+in+manifest&site=stackoverflow"
req = urllib.request.Request(url)
with urllib.request.urlopen(req) as response:
    print(response.read().decode('utf-8'))
