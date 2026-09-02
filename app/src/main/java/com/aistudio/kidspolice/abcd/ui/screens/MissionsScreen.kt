package com.aistudio.kidspolice.abcd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.R
import com.aistudio.kidspolice.abcd.data.DailyMission
import com.aistudio.kidspolice.abcd.ui.components.PoliceSirenLightBar
import com.aistudio.kidspolice.abcd.ui.theme.PoliceAccentCyan
import com.aistudio.kidspolice.abcd.ui.theme.PoliceCardBg
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGold
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGreen
import com.aistudio.kidspolice.abcd.ui.theme.PoliceNavy

@Composable
fun MissionsScreen(
    missions: List<DailyMission>,
    userScore: Int,
    onToggleMission: (String) -> Unit,
    onOpenCertificate: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(PoliceNavy).padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).clip(CircleShape).background(PoliceCardBg).clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "العودة", tint = PoliceNavy)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("مهام وسلوكيات البطل اليومية", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(PoliceGold.copy(alpha = 0.2f))
                    .border(1.dp, PoliceGold, RoundedCornerShape(16.dp))
                    .clickable { onOpenCertificate() }
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = PoliceGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("الشهادة", color = PoliceGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        PoliceSirenLightBar(isFlashing = true)
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PoliceCardBg)
                .border(1.dp, PoliceAccentCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("مجموع النقاط المكتسبة:", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                    Text("$userScore نقطة تميز", color = PoliceGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.Star, contentDescription = "الإنجازات", tint = PoliceGold, modifier = Modifier.size(34.dp))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(missions) { mission ->
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable { onToggleMission(mission.id) },
                    colors = CardDefaults.cardColors(containerColor = PoliceCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (mission.isCompleted) PoliceGreen else PoliceGold.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val missionIcon = when (mission.id) {
                            "m1" -> R.drawable.game_collect_stars
                            "m2" -> R.drawable.officer_kid
                            "m3" -> R.drawable.shield_logo
                            "m4" -> R.drawable.game_puzzle
                            "m5" -> R.drawable.game_match_badge
                            else -> R.drawable.officer_avatar
                        }
                        androidx.compose.foundation.Image(
                            painter = painterResource(missionIcon),
                            contentDescription = mission.title,
                            modifier = Modifier.size(44.dp).clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(mission.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(mission.description, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("+${mission.points} نقطة", color = PoliceGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier.size(34.dp).clip(CircleShape)
                                .background(if (mission.isCompleted) PoliceGreen else Color.Transparent)
                                .border(1.5.dp, if (mission.isCompleted) PoliceGreen else Color.Gray, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (mission.isCompleted) {
                                Icon(Icons.Default.Check, contentDescription = "مكتملة", tint = PoliceNavy, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
