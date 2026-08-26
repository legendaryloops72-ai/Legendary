package com.aistudio.kidspolice.abcd.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import com.aistudio.kidspolice.abcd.BuildConfig
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.data.Dialect
import com.aistudio.kidspolice.abcd.data.PoliceScenario
import com.aistudio.kidspolice.abcd.data.PoliceScenariosRepository
import com.aistudio.kidspolice.abcd.ui.components.PoliceSirenLightBar
import com.aistudio.kidspolice.abcd.ui.components.TestBannerAdView
import com.aistudio.kidspolice.abcd.ui.theme.PoliceAccentCyan
import com.aistudio.kidspolice.abcd.ui.theme.PoliceCardBg
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGold
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGreen
import com.aistudio.kidspolice.abcd.ui.theme.PoliceNavy
import com.aistudio.kidspolice.abcd.ui.theme.PoliceRed

@Composable
fun HomeScreen(
    selectedDialect: Dialect,
    onDialectSelected: (Dialect) -> Unit,
    onStartCall: (PoliceScenario) -> Unit,
    onOpenDialer: () -> Unit,
    onOpenSounds: () -> Unit,
    onOpenMissions: () -> Unit,
    onOpenCertificate: () -> Unit,
    userScore: Int,
    onTestInterstitial: () -> Unit = {}
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1931),
                        PoliceNavy,
                        Color(0xFF030914)
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            if (BuildConfig.DEBUG) {
                                onTestInterstitial()
                            }
                        }
                    )
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PoliceGold, Color(0xFFFFA000))
                            )
                        )
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👮‍♂️", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "شرطة الأطفال",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "تعلم • العب • كن بطلاً",
                        color = PoliceAccentCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(PoliceCardBg)
                    .border(1.5.dp, PoliceGold, RoundedCornerShape(24.dp))
                    .clickable { onOpenCertificate() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⭐", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$userScore نقطة",
                        color = PoliceGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        PoliceSirenLightBar(isFlashing = true)

        Spacer(modifier = Modifier.height(4.dp))

        // Welcome Hero Banner Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(containerColor = PoliceCardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PoliceAccentCyan.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF102A56),
                                Color(0xFF1B3B6F)
                            )
                        )
                    )
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "مرحبًا أيها البطل! 👮",
                        color = PoliceGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "اختر مهمتك وابدأ التدريب اليومي بنجاح",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(PoliceGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🛡️", fontSize = 22.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Actions Grid (Dialer, Sounds, Missions, Certificate)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionButton(
                title = "شرطة 999",
                emoji = "📞",
                color = PoliceGreen,
                modifier = Modifier.weight(1f),
                onClick = onOpenDialer
            )
            QuickActionButton(
                title = "أصوات الطوارئ",
                emoji = "🚨",
                color = PoliceRed,
                modifier = Modifier.weight(1f),
                onClick = onOpenSounds
            )
            QuickActionButton(
                title = "مهام البطل",
                emoji = "📋",
                color = PoliceAccentCyan,
                modifier = Modifier.weight(1f),
                onClick = onOpenMissions
            )
            QuickActionButton(
                title = "جوائز وشرف",
                emoji = "🏅",
                color = PoliceGold,
                modifier = Modifier.weight(1f),
                onClick = onOpenCertificate
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dialects Selector
        Text(
            text = "اختر لهجة الشرطي:",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 4.dp)
        ) {
            items(Dialect.entries.toTypedArray()) { dialect ->
                val isSelected = dialect == selectedDialect
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) PoliceGold else PoliceCardBg)
                        .border(
                            1.5.dp,
                            if (isSelected) PoliceGold else PoliceAccentCyan.copy(alpha = 0.4f),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onDialectSelected(dialect) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = dialect.flag, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = dialect.displayName.replace("اللهجة ", "").replace("اللغة العربية ", ""),
                            color = if (isSelected) PoliceNavy else Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Scenarios List Header
        Text(
            text = "المكالمات والسيناريوهات المتاحة:",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(PoliceScenariosRepository.scenarios) { scenario ->
                ScenarioCard(
                    scenario = scenario,
                    selectedDialect = selectedDialect,
                    onCallClick = { onStartCall(scenario) }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        TestBannerAdView()
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
            .height(74.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PoliceCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ScenarioCard(
    scenario: PoliceScenario,
    selectedDialect: Dialect,
    onCallClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCallClick() },
        colors = CardDefaults.cardColors(containerColor = PoliceCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PoliceGold.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF0F254A), Color(0xFF1B3B6F))
                        )
                    )
                    .border(1.dp, PoliceAccentCyan.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = scenario.iconEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scenario.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = scenario.subtitle,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👮‍♂️ ", fontSize = 11.sp)
                    Text(
                        text = scenario.officerName,
                        color = PoliceGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PoliceGreen, Color(0xFF2E7D32))
                        )
                    )
                    .clickable { onCallClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📞", fontSize = 20.sp)
            }
        }
    }
}

