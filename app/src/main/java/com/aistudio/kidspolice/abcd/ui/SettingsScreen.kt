package com.aistudio.kidspolice.abcd.ui

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
    onShowPrivacyPolicy: () -> Unit,
    onNavigateToParentDashboard: () -> Unit
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    var editName by remember(profile?.name) { mutableStateOf(profile?.name ?: "") }
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
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("معلومات التطبيق", style = MaterialTheme.typography.titleLarge)
            
            val context = androidx.compose.ui.platform.LocalContext.current

            OutlinedButton(
                onClick = {
                    val shareUrl = "https://ais-pre-7ldjbf3a7dwula4tvp55mq-837550959080.europe-west2.run.app/call/police"
                    val sendIntent = android.content.Intent().apply {
                        action = android.content.Intent.ACTION_SEND
                        putExtra(
                            android.content.Intent.EXTRA_TEXT, 
                            "حمل تطبيق شرطة الأطفال العراقي المميز وجرب اتصال المعلق الصوتي التفاعلي فوراً عبر هذا الرابط: $shareUrl"
                        )
                        type = "text/plain"
                    }
                    val shareIntent = android.content.Intent.createChooser(sendIntent, "مشاركة رابط دعوة المعلق")
                    context.startActivity(shareIntent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("مشاركة رابط دعوة تجربة المعلق 👮")
            }

            OutlinedButton(
                onClick = onShowPrivacyPolicy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("سياسة الخصوصية")
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateToParentDashboard,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
            ) {
                Text("إدارة الوالدين (المهام والرسائل) 👨‍👩‍👧‍👦")
            }
        }
    }
}
