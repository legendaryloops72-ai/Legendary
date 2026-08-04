import re

with open('app/src/main/java/com.aistudio.kidspolice.abcd/ui/SoundsUniverseScreen.kt', 'r') as f:
    content = f.read()

# Add missing animals
# Triple("ديك", "🐓", "صياح الديك النشيط في الصباح الباكر"),
# Triple("ضفدع", "🐸", "نقيق الضفدع القافز المرح"),
# I will just insert missing animals near the end of the list.

missing_animals = """            Triple("دجاجة", "🐔", "صوت الدجاجة اللطيفة في المزرعة"),
            Triple("خنزير", "🐷", "صوت الخنزير المرح يحب اللعب في الطين"),
"""

content = content.replace('            Triple("ديك رومي", "🦃", "قبقبة الديك الرومي ذو الريش الجميل المفروش")', '            Triple("ديك رومي", "🦃", "قبقبة الديك الرومي ذو الريش الجميل المفروش"),\n' + missing_animals.rstrip('\n'))

missing_vehicles = """            Triple("سيارة", "🚗", "صوت محرك وبوق السيارة العادية"),
            Triple("سيارة إطفاء", "🚒", "صوت سيارة الإطفاء البطلة لإخماد الحرائق"),
            Triple("دراجة هوائية", "🚲", "ترن ترن! جرس الدراجة الهوائية السريعة"),
"""

content = content.replace('            Triple("صاروخ فضائي", "🚀", "صوت انطلاق الصاروخ الفضائي الخارق نحو النجوم")', '            Triple("صاروخ فضائي", "🚀", "صوت انطلاق الصاروخ الفضائي الخارق نحو النجوم"),\n' + missing_vehicles.rstrip('\n'))

with open('app/src/main/java/com.aistudio.kidspolice.abcd/ui/SoundsUniverseScreen.kt', 'w') as f:
    f.write(content)
