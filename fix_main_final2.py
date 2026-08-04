import re
with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'r') as f:
    content = f.read()

# Remove all `viewModel = appViewModel,`
content = re.sub(r'\s*viewModel\s*=\s*appViewModel,', '', content)

# Now, we carefully add `viewModel = appViewModel,` to exactly the screens that need it.
# We will do a generic replacement: for any ScreenName( add viewModel = appViewModel, IF it's in the list.
screens_with_vm = [
    "HomeScreen", "SetupScreen", "TasksScreen", "QuizScreen", "SettingsScreen", 
    "ParentDashboardScreen", "HeroesUniverseScreen",
    "MemoryMatchGameScreen", "ColorTapGameScreen",
    "NumberOrderGameScreen", "ShapeMatchGameScreen", "AlphabetGameScreen",
    "BubblePopGameScreen", "FindDifferencesGameScreen", "SimplePuzzleGameScreen",
    "StoriesScreen", "RewardsScreen"
]

for screen in screens_with_vm:
    # We replace exactly "ScreenName(" with "ScreenName(\n                                viewModel = appViewModel,"
    content = re.sub(rf"({screen}\()", rf"\1\n                                viewModel = appViewModel,", content)

# But wait, some SettingsScreen has AppLockViewModel. In MainActivity it's AppViewModel.
with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'w') as f:
    f.write(content)
