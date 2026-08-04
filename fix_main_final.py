import re
with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'r') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if "ColoringScreen(" in line:
        # Check if previous line is viewModel
        if "viewModel =" in lines[i-1]:
            lines[i-1] = ""
    if "MemoryMatchGameScreen(" in line:
        if "viewModel =" not in lines[i+1]:
            lines[i] = line + "                                viewModel = appViewModel,\n"

with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'w') as f:
    f.writelines(lines)
