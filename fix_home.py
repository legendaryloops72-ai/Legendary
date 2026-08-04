import re

with open('app/src/main/java/com.aistudio.kidspolice.abcd/ui/HomeScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('onNavigateToPhotoFrame', 'onNavigateToPoliceCars')
content = content.replace('📸', '🚓')
content = content.replace('إطار صور الشرطة', 'معرض سيارات الشرطة')
content = content.replace('التقط صورة كشرطي', 'تعرف على مركبات الشرطة')
content = content.replace('title = "الأصوات"', 'title = "سيارات الشرطة"')
content = content.replace('subtitle = "أصوات الحيوانات والمركبات"', 'subtitle = "تعرف على مركبات الشرطة"')

with open('app/src/main/java/com.aistudio.kidspolice.abcd/ui/HomeScreen.kt', 'w') as f:
    f.write(content)
