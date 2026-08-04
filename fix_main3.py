import re
with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'r') as f:
    lines = f.readlines()

def fix_line(num, old, new):
    idx = num - 1
    if old in lines[idx]:
        lines[idx] = lines[idx].replace(old, new)
    else:
        # Just insert the new line after the screen name if it was a missing argument
        pass

# SettingsScreen has ambiguous overload. Let's make sure it has exactly one viewModel.
# We will use regex to find screen definitions in MainActivity and ensure they have viewModel if they need it.
with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'r') as f:
    content = f.read()

# All screens that need viewModel = appViewModel
screens = [
    "SetupScreen", "TasksScreen", "QuizScreen", "SettingsScreen", 
    "ParentDashboardScreen", "MemoryMatchGameScreen", "ColorTapGameScreen",
    "NumberOrderGameScreen", "ShapeMatchGameScreen", "AlphabetGameScreen",
    "BubblePopGameScreen", "FindDifferencesGameScreen", "SimplePuzzleGameScreen",
    "StoriesScreen", "RewardsScreen"
]

for screen in screens:
    # Match: ScreenName( \n
    content = re.sub(rf"({screen}\(\s*)", rf"\1viewModel = appViewModel,\n                                ", content)

# Remove double viewModels again if any
content = re.sub(r'(viewModel = appViewModel,\s*){2,}', r'viewModel = appViewModel,\n                                ', content)

# Fix GamesScreen onBack
content = content.replace('onNavigateBack = { currentScreen = "home" }', 'onBack = { currentScreen = "home" }')

with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'w') as f:
    f.write(content)
