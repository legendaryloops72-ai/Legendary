package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToQuizzes: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSimulateCall: (String) -> Unit
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    var showStoryDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color(0xFF0284C7), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👮", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "شرطة الأطفال",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    "أهلاً بك، ${profile?.name ?: "بطلنا الصغير"} 👋",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        
                        // Stars Score Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFFEF3C7), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${profile?.totalStars ?: 15}",
                                color = Color(0xFFD97706),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("⭐", fontSize = 16.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF1F5F9)) // Light Blue-Gray background tint
        ) {
            
            // Daily Progress Challenge Card (Gradient header from Tailwind style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF0284C7), Color(0xFF0D9488))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        "تحدّي بطل اليوم 🌟",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "ترتيب الغرفة وغسل اليدين بالماء والصابون",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { 0.75f },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "75%",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Grid of Actions
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    ActionCard(
                        title = "📞 اتصال الشرطة",
                        subTitle = "تربوي ووهمي",
                        emoji = "📞",
                        backgroundColor = Color(0xFFEFF6FF),
                        accentColor = Color(0xFF2563EB)
                    ) { onSimulateCall("police") }
                }
                item {
                    ActionCard(
                        title = "👨‍⚕️ اتصال الطبيب",
                        subTitle = "صحي وودود",
                        emoji = "👨‍⚕️",
                        backgroundColor = Color(0xFFECFDF5),
                        accentColor = Color(0xFF059669)
                    ) { onSimulateCall("doctor") }
                }
                item {
                    ActionCard(
                        title = "👩‍🏫 اتصال المعلم",
                        subTitle = "تشجيعي وتحفيزي",
                        emoji = "👩‍🏫",
                        backgroundColor = Color(0xFFFFF7ED),
                        accentColor = Color(0xFFD97706)
                    ) { onSimulateCall("teacher") }
                }
                item {
                    ActionCard(
                        title = "🏆 اختبارات الأبطال",
                        subTitle = "ثقّف نفسك واكسب 🌟",
                        emoji = "🧠",
                        backgroundColor = Color(0xFFFAF5FF),
                        accentColor = Color(0xFF7C3AED)
                    ) { onNavigateToQuizzes() }
                }
                item {
                    ActionCard(
                        title = "✅ مهام البطل",
                        subTitle = "نقاط نجوم يومية",
                        emoji = "🎯",
                        backgroundColor = Color(0xFFFFF1F2),
                        accentColor = Color(0xFFE11D48)
                    ) { onNavigateToTasks() }
                }
                item {
                    ActionCard(
                        title = "📖 قصص تعليمية",
                        subTitle = "العقل والفضيلة",
                        emoji = "📖",
                        backgroundColor = Color(0xFFF0FDF4),
                        accentColor = Color(0xFF16A34A)
                    ) {
                        showStoryDialog = "قوة الصدق والأمانة"
                    }
                }
            }

            // Beautiful Bottom Parents Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onNavigateToSettings() },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚙️", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "قسم وغرفة الوالدين",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            "تعديل الاسم وإدارة سياسة الاستخدام والخصوصية",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Parents Area",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Stories Dialog box
        showStoryDialog?.let { storyTitle ->
            AlertDialog(
                onDismissRequest = { showStoryDialog = null },
                confirmButton = {
                    TextButton(onClick = { showStoryDialog = null }) {
                        Text("قرأتها وفهمتها! 👍", fontWeight = FontWeight.Bold)
                    }
                },
                title = { Text("📖 قصة اليوم: $storyTitle") },
                text = {
                    Text(
                        "كان هناك طفل بطل اسمه أحمد. في يوم من الأيام، وجد أحمد محفظة نقود ملقاة على الأرض في ساحة المدرسة المجاورة.\n\n" +
                        "لم يأخذها أحمد لنفسه، بل أسرع فوراً وسلمها لمعلم الصف الأمين. فرح المعلم بأمانة أحمد وشجعه أمام جميع زملائه، وأعطاه لقب 'البطل الصادق الإيجابي'!\n\n" +
                        "الصدق والأمانة يجعلان الجميع يحبوننا ويثقون بنا دائماً يا أبطالنا الرائعين! ❤️",
                        lineHeight = 22.sp,
                        color = Color(0xFF334155)
                    )
                },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    subTitle: String,
    emoji: String,
    backgroundColor: Color,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(backgroundColor, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 22.sp)
                }
                
                Box(
                    modifier = Modifier
                        .background(backgroundColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "جديد ✨",
                        color = accentColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = subTitle,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
