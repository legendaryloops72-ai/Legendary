import re
with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'r') as f:
    content = f.read()

# Fix viewModel
content = re.sub(r'(\s+onNavigateBack\s*=\s*{[^}]+})', r'\n                                viewModel = appViewModel,\1', content)
content = re.sub(r'(\s+onBack\s*=\s*{[^}]+})', r'\n                                viewModel = appViewModel,\1', content)
content = re.sub(r'(\s+onEndCall\s*=\s*{[^}]+})', r'\n                                viewModel = appViewModel,\1', content)
content = re.sub(r'(\s+onAccept\s*=\s*{[^}]+})', r'\n                                viewModel = appViewModel,\1', content)

# Remove double viewModels
content = re.sub(r'(viewModel = appViewModel,\s*){2,}', r'viewModel = appViewModel,\n', content)

# Restore specific parameter names
content = content.replace("onNavigateBack = { currentScreen = \"settings\" }", "onBack = { currentScreen = \"settings\" }")
content = content.replace("onNavigateBack = { currentScreen = \"games\" }", "onBack = { currentScreen = \"games\" }")

with open('app/src/main/java/com.aistudio.kidspolice.abcd/MainActivity.kt', 'w') as f:
    f.write(content)
