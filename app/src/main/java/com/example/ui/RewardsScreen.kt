package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.EmojiEvents
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
import com.example.data.BadgeItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val badges by viewModel.badges.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val newUnlockedBadge by viewModel.newUnlockedBadge.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("all") } // "all", "unlocked", "locked"
    var inspectedBadge by remember { mutableStateOf<BadgeItem?>(null) }

    val unlockedCount = badges.count { it.isUnlocked }
    val totalCount = badges.size
    val totalStars = profile?.totalStars ?: 0

    val filteredBadges = when (selectedFilter) {
        "unlocked" -> badges.filter { it.isUnlocked }
        "locked" -> badges.filter { !it.isUnlocked }
        else -> badges
    }

    // Celebratory dialog when a new badge is unlocked
    if (newUnlockedBadge != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissUnlockedBadge() },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissUnlockedBadge() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("رائع! إنجاز عظيم 🎉", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("🎉 مبروك يا بطل!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(
                                Brush.radialGradient(listOf(Color(0xFFFFDE59), Color(0xFFF59E0B))),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = newUnlockedBadge!!.emoji, fontSize = 48.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = newUnlockedBadge!!.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = newUnlockedBadge!!.description,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White
        )
    }

    // Inspected Badge Dialog
    if (inspectedBadge != null) {
        AlertDialog(
            onDismissRequest = { inspectedBadge = null },
            confirmButton = {
                TextButton(onClick = { inspectedBadge = null }) {
                    Text("إغلاق", fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                }
            },
            title = {
                Text(
                    text = if (inspectedBadge!!.isUnlocked) "⭐ ملصق مفتوح" else "🔒 ملصق مغلق",
                    fontWeight = FontWeight.Bold,
                    color = if (inspectedBadge!!.isUnlocked) Color(0xFF10B981) else Color(0xFF64748B)
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                if (inspectedBadge!!.isUnlocked)
                                    Brush.radialGradient(listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD)))
                                else
                                    Brush.radialGradient(listOf(Color(0xFFF1F5F9), Color(0xFFCBD5E1))),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = inspectedBadge!!.emoji, fontSize = 40.sp)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = inspectedBadge!!.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = inspectedBadge!!.description,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (!inspectedBadge!!.isUnlocked) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "💡 استمر في إنجاز المهام والتحديات لفتح هذا الملصق!",
                                fontSize = 12.sp,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Surface(
                            color = Color(0xFFD1FAE5),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "🎉 تهانينا! لقد حصلت على هذا الملصق الرائع.",
                                fontSize = 12.sp,
                                color = Color(0xFF065F46),
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ألبوم الجوائز والملصقات 🏆", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "عودة")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A8A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFEFF6FF))
                .padding(16.dp)
        ) {
            // Stats Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "ألبوم ملصقات البطل",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "تم فتح $unlockedCount من $totalCount ملصقاً",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Star, contentDescription = "Stars", tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$totalStars",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "all",
                    onClick = { selectedFilter = "all" },
                    label = { Text("الكل ($totalCount)") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedFilter == "unlocked",
                    onClick = { selectedFilter = "unlocked" },
                    label = { Text("المفتوحة ✨ ($unlockedCount)") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedFilter == "locked",
                    onClick = { selectedFilter = "locked" },
                    label = { Text("المغلقة 🔒 (${totalCount - unlockedCount})") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Badges / Stickers Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredBadges) { badge ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.85f)
                            .clickable { inspectedBadge = badge },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (badge.isUnlocked) Color.White else Color(0xFFF1F5F9)
                        ),
                        border = if (badge.isUnlocked)
                            BorderStroke(2.dp, Color(0xFFFFDE59))
                        else
                            BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 8.dp else 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        if (badge.isUnlocked)
                                            Brush.radialGradient(listOf(Color(0xFFFEF08A), Color(0xFFFACC15)))
                                        else
                                            Brush.radialGradient(listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8))),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (badge.isUnlocked) {
                                    Text(text = badge.emoji, fontSize = 34.sp)
                                } else {
                                    Icon(
                                        Icons.Filled.Lock,
                                        contentDescription = "Locked",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = badge.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (badge.isUnlocked) Color(0xFF1E3A8A) else Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = badge.description,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = if (badge.isUnlocked) Color(0xFFD1FAE5) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (badge.isUnlocked) "مفتوح 🎉" else "قيد الإنجاز",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (badge.isUnlocked) Color(0xFF065F46) else Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
