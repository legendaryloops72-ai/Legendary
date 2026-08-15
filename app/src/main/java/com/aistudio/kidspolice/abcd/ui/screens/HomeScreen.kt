package com.aistudio.kidspolice.abcd.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneCallback
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.ads.AdBanner
import com.aistudio.kidspolice.abcd.ads.AdManager
import com.aistudio.kidspolice.abcd.data.CallCategory
import com.aistudio.kidspolice.abcd.data.Dialect
import com.aistudio.kidspolice.abcd.data.PoliceRepository
import com.aistudio.kidspolice.abcd.data.PoliceScenario
import com.aistudio.kidspolice.abcd.ui.AppViewModel
import com.aistudio.kidspolice.abcd.ui.theme.PoliceBlue
import com.aistudio.kidspolice.abcd.ui.theme.PoliceCrimson
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGold
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGreen
import com.aistudio.kidspolice.abcd.ui.theme.PoliceNavy
import com.aistudio.kidspolice.abcd.ui.theme.PoliceRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateToDialer: () -> Unit,
    onNavigateToSounds: () -> Unit,
    onNavigateToMissions: () -> Unit,
    onNavigateToCertificate: () -> Unit,
    onStartCall: (PoliceScenario) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Bad Behavior (Warning), 2: Good Behavior (Reward)
    var showProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PoliceGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "شعار الشرطة",
                                tint = PoliceBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "شرطة الأطفال الذكية",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "التربية بالتشجيع والإرشاد",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showProfileDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "الملف الشخصي والنقاط",
                            tint = PoliceGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PoliceBlue)
            )
        },
        bottomBar = {
            AdBanner()
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // Hero Status Card
            item {
                HeroBanner(
                    childName = uiState.childName,
                    score = uiState.totalScore,
                    onOpenProfile = { showProfileDialog = true },
                    onCertificateClick = onNavigateToCertificate
                )
            }

            // Quick Access Shortcut Grid
            item {
                QuickActionSection(
                    onDialerClick = onNavigateToDialer,
                    onSoundsClick = onNavigateToSounds,
                    onMissionsClick = onNavigateToMissions,
                    onCertificateClick = onNavigateToCertificate
                )
            }

            // Dialect Selector
            item {
                DialectSelector(
                    selectedDialect = uiState.selectedDialect,
                    onSelect = { viewModel.setSelectedDialect(it) }
                )
            }

            // Scenarios Category Filter Tabs
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = PoliceBlue,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("الكل", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("⚠️ ضبط السلوك", color = PoliceCrimson, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("⭐ شاطر ومكافأة", color = PoliceGreen, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Scenarios List for current dialect
            val allScenarios = PoliceRepository.scenarios
            val dialectScenarios = allScenarios.filter { it.dialect == uiState.selectedDialect }
            val filteredScenarios = when (selectedTab) {
                1 -> dialectScenarios.filter { !it.isReward }
                2 -> dialectScenarios.filter { it.isReward }
                else -> dialectScenarios
            }

            if (filteredScenarios.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "اختر لهجة أخرى أو قسم آخر للاطلاع على المكالمات المسجلة",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(filteredScenarios) { scenario ->
                    ScenarioCard(
                        scenario = scenario,
                        onOutgoingCall = {
                            if (activity != null) {
                                AdManager.showInterstitial(activity) {
                                    viewModel.startOutgoingCall(scenario)
                                    onStartCall(scenario)
                                }
                            } else {
                                viewModel.startOutgoingCall(scenario)
                                onStartCall(scenario)
                            }
                        },
                        onIncomingCall = {
                            if (activity != null) {
                                AdManager.showInterstitial(activity) {
                                    viewModel.startIncomingCall(scenario)
                                    onStartCall(scenario)
                                }
                            } else {
                                viewModel.startIncomingCall(scenario)
                                onStartCall(scenario)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showProfileDialog) {
        ProfileEditDialog(
            currentName = uiState.childName,
            currentGender = uiState.childGender,
            score = uiState.totalScore,
            onDismiss = { showProfileDialog = false },
            onSave = { name, gender ->
                viewModel.updateChildProfile(name, gender)
                showProfileDialog = false
            }
        )
    }
}

@Composable
fun HeroBanner(
    childName: String,
    score: Int,
    onOpenProfile: () -> Unit,
    onCertificateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(PoliceBlue, PoliceNavy)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "مرحباً يا بطل",
                            color = PoliceGold,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = childName,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onCertificateClick() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "نقاط الشجاعة",
                                tint = PoliceGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$score نقطة",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "شرطة الأطفال في خدمتك لبناء أبطال المستقبل بالأخلاق والنظام والتشجيع المستمر 🚓✨",
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun QuickActionSection(
    onDialerClick: () -> Unit,
    onSoundsClick: () -> Unit,
    onMissionsClick: () -> Unit,
    onCertificateClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickActionButton(
            title = "لوحة الاتصال",
            emoji = "🔢",
            color = PoliceBlue,
            modifier = Modifier.weight(1f),
            onClick = onDialerClick
        )
        QuickActionButton(
            title = "أصوات وسارينات",
            emoji = "🚨",
            color = PoliceCrimson,
            modifier = Modifier.weight(1f),
            onClick = onSoundsClick
        )
        QuickActionButton(
            title = "مهام الأبطال",
            emoji = "🎯",
            color = PoliceGreen,
            modifier = Modifier.weight(1f),
            onClick = onMissionsClick
        )
        QuickActionButton(
            title = "شهادة الشرف",
            emoji = "🏆",
            color = PoliceGold,
            modifier = Modifier.weight(1f),
            onClick = onCertificateClick
        )
    }
}

@Composable
fun QuickActionButton(
    title: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 26.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
fun DialectSelector(
    selectedDialect: Dialect,
    onSelect: (Dialect) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        Text(
            text = "اختر لهجة الاتصال:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PoliceBlue)
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(PoliceRepository.dialects) { dialect ->
                val isSelected = dialect == selectedDialect
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelect(dialect) },
                    label = {
                        Text(
                            text = "${dialect.flag} ${dialect.titleAr}",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PoliceBlue,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@Composable
fun ScenarioCard(
    scenario: PoliceScenario,
    onOutgoingCall: () -> Unit,
    onIncomingCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (scenario.isReward) PoliceGreen.copy(alpha = 0.15f) else PoliceRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (scenario.isReward) "⭐" else "🚨",
                            fontSize = 22.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = scenario.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PoliceBlue
                            )
                        )
                        Text(
                            text = "${scenario.callerName} • ${scenario.callerRank}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (scenario.isReward) PoliceGreen.copy(alpha = 0.1f) else PoliceRed.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (scenario.isReward) "تشجيع" else "توجيه",
                        color = if (scenario.isReward) PoliceGreen else PoliceRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOutgoingCall,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PoliceBlue)
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("اتصال الآن", fontSize = 13.sp)
                }

                Button(
                    onClick = onIncomingCall,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (scenario.isReward) PoliceGreen else PoliceRed)
                ) {
                    Icon(imageVector = Icons.Default.PhoneCallback, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مكالمة واردة", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileEditDialog(
    currentName: String,
    currentGender: String,
    score: Int,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var gender by remember { mutableStateOf(currentGender) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "بيانات البطل الصغير 👮‍♂️",
                fontWeight = FontWeight.Bold,
                color = PoliceBlue
            )
        },
        text = {
            Column {
                Text(text = "رصيد النقاط الحالي: $score نقطة 🌟", color = PoliceGold, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الطفل / البطل") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(
                        selected = gender == "ولد",
                        onClick = { gender = "ولد" },
                        label = { Text("ولد 👦") }
                    )
                    FilterChip(
                        selected = gender == "بنت",
                        onClick = { gender = "بنت" },
                        label = { Text("بنت 👧") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name.ifBlank { "البطل الصغير" }, gender) },
                colors = ButtonDefaults.buttonColors(containerColor = PoliceBlue)
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
