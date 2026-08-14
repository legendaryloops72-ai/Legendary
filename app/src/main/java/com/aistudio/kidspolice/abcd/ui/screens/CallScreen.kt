package com.aistudio.kidspolice.abcd.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.ui.AppViewModel
import com.aistudio.kidspolice.abcd.ui.theme.PoliceBlue
import com.aistudio.kidspolice.abcd.ui.theme.PoliceCrimson
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGold
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGreen
import com.aistudio.kidspolice.abcd.ui.theme.PoliceNavy
import com.aistudio.kidspolice.abcd.ui.theme.PoliceRed
import java.util.Locale

@Composable
fun CallScreen(
    viewModel: AppViewModel,
    onCallEnded: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scenario = uiState.activeScenario

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PoliceNavy, Color(0xFF03071E))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Caller Information
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text(
                    text = "🚨 مركز شرطة الأطفال 🚨",
                    color = PoliceGold,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = scenario?.callerName ?: "الضابط المسؤول",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = scenario?.callerRank ?: "القيادة العامة لشرطة الأطفال",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Call Duration or Status
                if (uiState.isCallAnswered) {
                    val minutes = uiState.callDurationSeconds / 60
                    val seconds = uiState.callDurationSeconds % 60
                    val timeStr = String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    Text(
                        text = timeStr,
                        color = PoliceGreen,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = uiState.callStatusText,
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Center: Officer Avatar with animated badge
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .scale(if (!uiState.isCallAnswered) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(PoliceBlue, Color(0xFF1E3A8A))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (scenario?.isReward == true) "👮‍♂️" else "👮",
                            fontSize = 72.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Subtitle Dialogue Box during conversation
                if (uiState.isCallAnswered && scenario != null && scenario.dialogues.isNotEmpty()) {
                    val currentDialogue = scenario.dialogues.getOrNull(uiState.activeDialogueIndex)
                    if (currentDialogue != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "🗣️ ${currentDialogue.speaker}:",
                                    color = PoliceGold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentDialogue.text,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 24.sp,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }

            // Footer Call Actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                if (uiState.isIncomingCall && !uiState.isCallAnswered) {
                    // Incoming Call: Answer or Decline
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Decline
                        IconButton(
                            onClick = {
                                viewModel.endCall()
                                onCallEnded()
                            },
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(PoliceCrimson)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "رفض المكالمة",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        // Answer
                        IconButton(
                            onClick = { viewModel.answerIncomingCall() },
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(PoliceGreen)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "الرد على المكالمة",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                } else {
                    // Active or Outgoing Call: Mic, Speaker, End Call Controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mic Mute Toggle
                        IconButton(
                            onClick = { viewModel.toggleMic() },
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(if (uiState.isMicMuted) PoliceRed else Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = if (uiState.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "كتم الصوت",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        // Speaker Toggle
                        IconButton(
                            onClick = { viewModel.toggleSpeaker() },
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(if (uiState.isSpeakerOn) PoliceBlue else Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = if (uiState.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = "مكبر الصوت",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    // End Call Big Red Button
                    IconButton(
                        onClick = {
                            viewModel.endCall()
                            onCallEnded()
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(PoliceCrimson)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "إنهاء المكالمة",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}
