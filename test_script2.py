import urllib.request
import json
url = "https://api.stackexchange.com/2.3/questions/79721071/answers?order=desc&sort=votes&site=stackoverflow&filter=withbody"
req = urllib.request.Request(url)
with urllib.request.urlopen(req) as response:
    print(response.read().decode('utf-8'))
