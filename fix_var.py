with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'r') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if 'var currentScreen by rememberSaveable' in line:
        lines.insert(i+1, "                    var selectedVehicleId by remember { mutableStateOf(0) }\n")
        break

with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'w') as f:
    f.writelines(lines)
