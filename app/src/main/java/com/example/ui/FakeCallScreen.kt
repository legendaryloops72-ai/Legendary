package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sound.CallSoundManager
import kotlinx.coroutines.delay

@Composable
fun FakeCallScreen(
    callerType: String,
    viewModel: AppViewModel,
    onEndCall: () -> Unit
) {
    val context = LocalContext.current
    var isCallAnswered by remember { mutableStateOf(false) }
    var secondsElapsed by remember { mutableStateOf(0) }
    
    // Profiles name fetching
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val childName = profile?.name ?: "يا بطل"

    // Instantiate Call Sound Manager
    val callSoundManager = remember { CallSoundManager(context) }

    // Start Ringing Sounds automatically
    LaunchedEffect(Unit) {
        if (!isCallAnswered) {
            callSoundManager.playRingtone()
        }
    }

    // Call duration counter
    LaunchedEffect(isCallAnswered) {
        if (isCallAnswered) {
            callSoundManager.stopRingtone()
            callSoundManager.speakArabicGuidance(callerType, childName)
            while (true) {
                delay(1000)
                secondsElapsed++
            }
        }
    }

    // Release TTS and cleanup when component leafed
    DisposableEffect(Unit) {
        onDispose {
            callSoundManager.stopRingtone()
            callSoundManager.stopSpeaking()
            callSoundManager.release()
        }
    }

    // Modern layout colors based on active character
    val cardColorsGradient = when(callerType) {
        "police" -> listOf(Color(0xFF1E3A8A), Color(0xFF1D4ED8)) // Deep blue theme for police
        "doctor" -> listOf(Color(0xFF065F46), Color(0xFF047857)) // Warm green theme for pediatric doctor
        "teacher" -> listOf(Color(0xFF78350F), Color(0xFFB45309)) // Friendly amber for teacher
        else -> listOf(Color(0xFF1E293B), Color(0xFF475569))
    }

    val callerEmoji = when(callerType) {
        "police" -> "👮"
        "doctor" -> "👨‍⚕️"
        "teacher" -> "👩‍🏫"
        else -> "📞"
    }

    val callerTitle = when(callerType) {
        "police" -> "شرطي الأطفال الطيب"
        "doctor" -> "طبيب الأطفال البارع"
        "teacher" -> "معلّم الأحلام الذكي"
        else -> "اتصال عائلي آمن"
    }

    val callerDesc = when(callerType) {
        "police" -> "الاتصال التربوي للتشجيع والتوجيه"
        "doctor" -> "إرشادات طبية وحيويات صحيّة ممتعة"
        "teacher" -> "تهنئة وتوجيه لحب العلم والمعرفة"
        else -> "اتصال للأبطال"
    }

    // Infinite pulse animations for background when calling to look amazing
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(cardColorsGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Pulse wave circle background
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                if (!isCallAnswered) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(pulseScale)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = callerEmoji, fontSize = 64.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = callerTitle,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = callerDesc,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // State Badge
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCallAnswered) Color(0xFF10B981) else Color(0xFFF59E0B)
                )
            ) {
                Text(
                    text = if (isCallAnswered) {
                        val minutes = secondsElapsed / 60
                        val secs = secondsElapsed % 60
                        String.format("جاري التحدث.. %02d:%02d", minutes, secs)
                    } else "يتصل بك الآن بالصوت...",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Beautiful Call Script text box during active talking
            if (isCallAnswered) {
                Spacer(modifier = Modifier.height(40.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🔊 استمع بعناية إلى توجيهات $callerTitle",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "« يحثك على الاستماع لكلام والديك والالتزام بالمهام اليومية لتكسب نجوماً جديدة يومياً! »",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Dual Button Action Layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reject Button (Red)
                FloatingActionButton(
                    onClick = {
                        callSoundManager.stopRingtone()
                        callSoundManager.stopSpeaking()
                        onEndCall()
                    },
                    containerColor = Color.Red,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(76.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CallEnd,
                        contentDescription = "End Call",
                        modifier = Modifier.size(36.dp)
                    )
                }

                if (!isCallAnswered) {
                    // Accept Button (Green)
                    FloatingActionButton(
                        onClick = { isCallAnswered = true },
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(76.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Call,
                            contentDescription = "Receive Call",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}
