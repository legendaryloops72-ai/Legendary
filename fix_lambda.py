import re

with open('app/src/main/java/com/example/sound/CallSoundManager.kt', 'r') as f:
    content = f.read()

# Replace Log.d(...) with Log.d(...); Unit
content = content.replace('Log.d("CallSoundManager", "Started playing sound for: $trimmedName")', 'Log.d("CallSoundManager", "Started playing sound for: $trimmedName")\n            Unit')
content = content.replace('Log.e("CallSoundManager", "Missing file: $assetPath, attempting URL fallback")', 'Log.e("CallSoundManager", "Missing file: $assetPath, attempting URL fallback")\n                    Unit')
content = content.replace('Log.e("CallSoundManager", "Online playing failed for $trimmedName, starting local fallback")', 'Log.e("CallSoundManager", "Online playing failed for $trimmedName, starting local fallback")\n                    Unit')
content = content.replace('Log.d("CallSoundManager", "Started playing sound online for: $trimmedName")', 'Log.d("CallSoundManager", "Started playing sound online for: $trimmedName")\n                    Unit')


with open('app/src/main/java/com/example/sound/CallSoundManager.kt', 'w') as f:
    f.write(content)
