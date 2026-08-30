package com.aistudio.kidspolice.abcd.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.audio.PoliceAudioPlayer
import com.aistudio.kidspolice.abcd.data.Dialect
import com.aistudio.kidspolice.abcd.data.PoliceScenario
import com.aistudio.kidspolice.abcd.ui.components.PoliceSirenLightBar
import com.aistudio.kidspolice.abcd.ui.theme.PoliceAccentCyan
import com.aistudio.kidspolice.abcd.ui.theme.PoliceCardBg
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGold
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGreen
import com.aistudio.kidspolice.abcd.ui.theme.PoliceNavy
import com.aistudio.kidspolice.abcd.ui.theme.PoliceRed
import kotlinx.coroutines.delay

enum class CallState {
    RINGING,
    CONNECTED
}

@Composable
fun CallScreen(
    scenario: PoliceScenario,
    dialect: Dialect,
    audioPlayer: PoliceAudioPlayer,
    onEndCall: () -> Unit
) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        android.util.Log.d("PoliceAudioPlayer", "CALL_SCREEN_STARTED")
    }

    var callState by remember { mutableStateOf(CallState.RINGING) }
    var secondsElapsed by remember { mutableIntStateOf(0) }
    val isSpeaking by audioPlayer.isSpeaking.collectAsState()

    val speechText = scenario.getSpeech(dialect)

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stopSpeaking()
        }
    }

    LaunchedEffect(callState) {
        if (callState == CallState.RINGING) {
            audioPlayer.playRingTone()
            delay(3500)
            callState = CallState.CONNECTED
        }
    }

    LaunchedEffect(callState) {
        if (callState == CallState.CONNECTED) {
            audioPlayer.playRadioChirp()
            delay(600)
            audioPlayer.playScenarioCall(scenario.id)

            while (true) {
                delay(1000)
                secondsElapsed++
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PoliceNavy)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PoliceSirenLightBar(isFlashing = true)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "مكالمة واردة من مركز شرطة الأطفال",
                color = PoliceGold,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (callState == CallState.RINGING) "جارٍ الاتصال بالضابط... 🔔" else "مكالمة جارية: %02d:%02d".format(secondsElapsed / 60, secondsElapsed % 60),
                color = if (callState == CallState.RINGING) PoliceAccentCyan else PoliceGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF152A4A))
                    .border(3.dp, if (isSpeaking) PoliceAccentCyan else PoliceGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👮‍♂️", fontSize = 65.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = scenario.officerName,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${dialect.displayName} ${dialect.flag}",
                color = PoliceAccentCyan,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = PoliceCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, PoliceGold.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = scenario.title,
                        color = PoliceGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"$speechText\"",
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    AnimatedVisibility(visible = isSpeaking) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "🔊 الضابط يتحدث الآن...",
                                color = PoliceAccentCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (callState == CallState.CONNECTED) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            audioPlayer.playRadioChirp()
                            audioPlayer.speakOfficer(speechText, dialect)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PoliceCardBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PoliceAccentCyan)
                    ) {
                        Text("إعادة كلام الضابط 📢", color = Color.White, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { audioPlayer.playWhistle() },
                        colors = ButtonDefaults.buttonColors(containerColor = PoliceCardBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PoliceGold)
                    ) {
                        Text("صافرة 🪈", color = PoliceGold, fontSize = 13.sp)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (callState == CallState.RINGING) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(PoliceGreen)
                            .clickable {
                                android.util.Log.d("PoliceAudioPlayer", "CALL_BUTTON_CLICKED")
                                callState = CallState.CONNECTED
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📞", fontSize = 32.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "رد على الضابط", color = Color.White, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(60.dp))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(PoliceRed)
                        .clickable { onEndCall() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "❌", fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "إنهاء المكالمة", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}
