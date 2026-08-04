import re

with open('app/src/main/java/com.aistudio.kidspolice.abcd/ui/PoliceCarsGalleryScreen.kt', 'r') as f:
    content = f.read()

content = re.sub(r',\s*fontFamily\s*=\s*[a-zA-Z]+Family', '', content)

with open('app/src/main/java/com.aistudio.kidspolice.abcd/ui/PoliceCarsGalleryScreen.kt', 'w') as f:
    f.write(content)
