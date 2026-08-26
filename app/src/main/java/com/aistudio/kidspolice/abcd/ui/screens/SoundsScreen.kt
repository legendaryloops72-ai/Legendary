package com.aistudio.kidspolice.abcd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.audio.PoliceAudioPlayer
import com.aistudio.kidspolice.abcd.ui.components.PoliceSirenLightBar
import com.aistudio.kidspolice.abcd.ui.theme.PoliceAccentCyan
import com.aistudio.kidspolice.abcd.ui.theme.PoliceCardBg
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGold
import com.aistudio.kidspolice.abcd.ui.theme.PoliceNavy
import com.aistudio.kidspolice.abcd.ui.theme.PoliceRed

data class SoundEffectItem(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val actionType: SoundType,
    val color: Color
)

enum class SoundType {
    SIREN,
    RADIO,
    HORN,
    WHISTLE,
    RINGTONE
}

@Composable
fun SoundsScreen(
    audioPlayer: PoliceAudioPlayer,
    onBack: () -> Unit
) {
    val isSirenPlaying by audioPlayer.isSirenPlaying.collectAsState()

    val sounds = listOf(
        SoundEffectItem("سارينة الدورية", "تشغيل وإيقاف مستمر", "🚨", SoundType.SIREN, PoliceRed),
        SoundEffectItem("لاسلكي العمليات", "صوت إشارة الراديو", "📻", SoundType.RADIO, PoliceAccentCyan),
        SoundEffectItem("بوري سيارة الشرطة", "تنبيه الطريق", "📢", SoundType.HORN, PoliceGold),
        SoundEffectItem("صافرة المرور", "إشارة توقف وانتباه", "🪈", SoundType.WHISTLE, Color(0xFF00E676)),
        SoundEffectItem("نغمة الاتصال", "جرس هاتف الطوارئ", "🔔", SoundType.RINGTONE, Color(0xFFFF9100))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PoliceNavy)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PoliceCardBg)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "➡️", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "مؤثرات وأصوات دورية الشرطة",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        PoliceSirenLightBar(isFlashing = isSirenPlaying)

        Spacer(modifier = Modifier.height(14.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sounds) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            when (item.actionType) {
                                SoundType.SIREN -> audioPlayer.togglePoliceSiren()
                                SoundType.RADIO -> audioPlayer.playRadioChirp()
                                SoundType.HORN -> audioPlayer.playPoliceHorn()
                                SoundType.WHISTLE -> audioPlayer.playWhistle()
                                SoundType.RINGTONE -> audioPlayer.playRingTone()
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = PoliceCardBg),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (item.actionType == SoundType.SIREN && isSirenPlaying) PoliceRed else item.color.copy(alpha = 0.4f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = item.emoji, fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (item.actionType == SoundType.SIREN && isSirenPlaying) "إيقاف السارينة" else item.title,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.subtitle,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
