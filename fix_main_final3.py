with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'r') as f:
    lines = f.readlines()

def fix_missing(lines, start_idx, target_screen):
    # Find the target_screen usage
    for i in range(start_idx, len(lines)):
        if f"{target_screen}(" in lines[i]:
            # Now insert viewModel = appViewModel, if missing
            if "viewModel =" not in "".join(lines[i:i+4]):
                lines.insert(i+1, "                                viewModel = appViewModel,\n")
            return

screens = [
    "TasksScreen", "QuizScreen", "MemoryMatchGameScreen", 
    "ColorTapGameScreen", "NumberOrderGameScreen", "ShapeMatchGameScreen",
    "AlphabetGameScreen", "BubblePopGameScreen", "FindDifferencesGameScreen",
    "SimplePuzzleGameScreen", "StoriesScreen", "RewardsScreen",
    "ColoringScreen", "PoliceScenariosScreen", "FakeCallScreen", "GamesScreen"
]

for screen in screens:
    fix_missing(lines, 0, screen)
    fix_missing(lines, 200, screen) # For else block

with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'w') as f:
    f.writelines(lines)
