with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'r') as f:
    lines = f.readlines()

def fix_line(num, old, new):
    idx = num - 1
    lines[idx] = lines[idx].replace(old, new)

# e: file:///app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt:89:33 No parameter with name 'onNavigateBack' found.
fix_line(89, 'onNavigateBack', 'onBack')

# e: file:///app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt:100:33 No parameter with name 'onNavigateBack' found.
fix_line(100, 'onNavigateBack', 'onBack')

# e: file:///app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt:115:33 No parameter with name 'onNavigateBack' found.
fix_line(115, 'onNavigateBack', 'onBack')

# e: file:///app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt:122:33 No parameter with name 'onNavigateBack' found.
fix_line(122, 'onNavigateBack', 'onBack')

# e: file:///app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt:126:29 None of the following candidates is applicable:
# SettingsScreen takes onBack, onShowPrivacyPolicy, onNavigateToParentDashboard
fix_line(128, 'onNavigateBack =', 'onBack =')

# 144: No parameter with name 'onNavigateBack' found. (Wait, privacy?)
fix_line(144, 'onNavigateBack', 'onBack')

# 164: No parameter with name 'onNavigateBack' found.
fix_line(164, 'onNavigateBack', 'onBack')

# 171: No parameter with name 'onNavigateBack' found.
fix_line(171, 'onNavigateBack', 'onBack')

# 242: No parameter with name 'onNavigateBack' found.
fix_line(242, 'onNavigateBack', 'onBack')

# 249: No parameter with name 'onNavigateBack' found.
fix_line(249, 'onNavigateBack', 'onBack')

with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'w') as f:
    f.writelines(lines)
