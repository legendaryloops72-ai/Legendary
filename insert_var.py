with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'r') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if 'var currentScreen by remember { mutableStateOf("splash") }' in line:
        lines.insert(i+1, "                var selectedVehicleId by remember { mutableIntStateOf(0) }\n")
        break

with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'w') as f:
    f.writelines(lines)
