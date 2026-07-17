import urllib.request
import urllib.parse
import json
import os

queries = {
    "police_siren": "police siren ogg",
    "ambulance_siren": "ambulance siren ogg",
    "fire_truck_siren": "fire engine siren ogg",
    "air_raid_siren": "air raid siren ogg",
    "nuclear_alarm": "civil defense siren ogg",
    "ship_horn": "fog horn ogg",
    "smoke_alarm": "smoke detector ogg",
    "evacuation_alarm": "tornado siren ogg",
    "submarine_siren": "submarine klaxon ogg",
    "train_horn": "train horn ogg",
    "car_alarm": "car alarm ogg",
    "school_bell": "school bell ogg",
    "space_alarm": "sci-fi alarm ogg",
    "truck_horn": "truck horn ogg"
}

def search_wikimedia_audio(query):
    print(f"Searching for: {query}")
    params = {
        "action": "query",
        "list": "search",
        "srnamespace": "6",  # File namespace
        "srsearch": f"{query} intitle:ogg|wav|mp3",
        "srlimit": "10",
        "format": "json"
    }
    url = "https://commons.wikimedia.org/w/api.php?" + urllib.parse.urlencode(params)
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read().decode('utf-8'))
            results = data.get("query", {}).get("search", [])
            # Return first result that is an audio file
            for res in results:
                title = res.get("title", "")
                if any(ext in title.lower() for ext in [".ogg", ".wav", ".mp3"]):
                    return title
    except Exception as e:
        print(f"Error searching for {query}: {e}")
    return None

def get_file_url(file_title):
    params = {
        "action": "query",
        "titles": file_title,
        "prop": "imageinfo",
        "iiprop": "url",
        "format": "json"
    }
    url = "https://commons.wikimedia.org/w/api.php?" + urllib.parse.urlencode(params)
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read().decode('utf-8'))
            pages = data.get("query", {}).get("pages", {})
            for page_id, page_data in pages.items():
                imageinfo = page_data.get("imageinfo", [])
                if imageinfo:
                    return imageinfo[0].get("url")
    except Exception as e:
        print(f"Error getting URL for {file_title}: {e}")
    return None

os.makedirs("/app/src/main/assets/sounds/sirens", exist_ok=True)

for key, q in queries.items():
    file_title = search_wikimedia_audio(q)
    if not file_title:
        # Fallback to broader query if specific fails
        file_title = search_wikimedia_audio(key.replace("_", " "))
    
    if file_title:
        file_url = get_file_url(file_title)
        if file_url:
            ext = os.path.splitext(file_title)[1].lower()
            dest_filename = f"{key}{ext}"
            dest_path = os.path.join("/app/src/main/assets/sounds/sirens", dest_filename)
            print(f"Downloading {file_url} to {dest_path}")
            try:
                req = urllib.request.Request(file_url, headers={'User-Agent': 'Mozilla/5.0'})
                with urllib.request.urlopen(req) as response, open(dest_path, 'wb') as out_file:
                    out_file.write(response.read())
                print(f"Successfully downloaded {dest_filename}")
            except Exception as e:
                print(f"Error downloading {dest_filename}: {e}")
        else:
            print(f"No direct URL found for {file_title}")
    else:
        print(f"No audio file found for query: {q}")
