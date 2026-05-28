package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onShowPrivacyPolicy: () -> Unit
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    var editName by remember { mutableStateOf(profile?.name ?: "") }
    var isEditing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("إعدادات الطفل", style = MaterialTheme.typography.titleLarge)
            
            OutlinedTextField(
                value = editName,
                onValueChange = { editName = it },
                label = { Text("اسم الطفل") },
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (isEditing) {
                Button(
                    onClick = { 
                        viewModel.saveProfileName(editName)
                        isEditing = false 
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("حفظ")
                }
            } else {
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("تعديل الاسم")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("معلومات التطبيق", style = MaterialTheme.typography.titleLarge)
            
            OutlinedButton(
                onClick = onShowPrivacyPolicy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("سياسة الخصوصية")
            }
        }
    }
}
