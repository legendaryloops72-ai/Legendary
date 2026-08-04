package com.aistudio.kidspolice.abcd.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aistudio.kidspolice.abcd.data.KidTask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    var messageInput by remember { mutableStateOf(profile?.parentMessage ?: "") }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskStars by remember { mutableStateOf("5") }

    LaunchedEffect(profile?.parentMessage) {
        if (profile?.parentMessage != null && messageInput.isEmpty() && profile!!.parentMessage.isNotEmpty()) {
            messageInput = profile!!.parentMessage
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إدارة الوالدين") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("رسالة اليوم للطفل", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            label = { Text("الرسالة") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.updateParentMessage(messageInput) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("حفظ الرسالة")
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("إضافة مهمة جديدة", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            label = { Text("عنوان المهمة") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newTaskStars,
                            onValueChange = { newTaskStars = it },
                            label = { Text("النقاط (المكافأة)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val stars = newTaskStars.toIntOrNull() ?: 5
                                if (newTaskTitle.isNotBlank()) {
                                    viewModel.addTaskToChild(newTaskTitle, stars)
                                    newTaskTitle = ""
                                    newTaskStars = "5"
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("إضافة")
                        }
                    }
                }
            }

            item {
                Text("المهام الحالية", style = MaterialTheme.typography.titleLarge)
            }

            items(tasks) { task ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(task.title, style = MaterialTheme.typography.titleMedium)
                            Text("النقاط: ${task.starsReward} | مكتملة: ${if (task.isCompleted) "نعم" else "لا"}", style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = { viewModel.removeTask(task) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete Task", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
