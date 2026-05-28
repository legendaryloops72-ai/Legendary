package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SetupScreen(viewModel: AppViewModel, onComplete: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var hasAcceptedTerms by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("أهلاً بك في شرطة الأطفال!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("أدخل اسم طفلك") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = hasAcceptedTerms, onCheckedChange = { hasAcceptedTerms = it })
            Text("أوافق على سياسة الخصوصية (بصفتي ولي الأمر)")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && hasAcceptedTerms) {
                    viewModel.saveProfileName(name)
                    onComplete()
                }
            },
            enabled = name.isNotBlank() && hasAcceptedTerms,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ابدأ التطبيق")
        }
    }
}
