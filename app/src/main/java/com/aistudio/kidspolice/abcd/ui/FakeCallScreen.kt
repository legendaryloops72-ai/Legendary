package com.aistudio.kidspolice.abcd.ui

import android.Manifest
import android.content.pm.PackageManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aistudio.kidspolice.abcd.sound.CallSoundManager
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
    var isSpeaking by remember { mutableStateOf(false) }

    DisposableEffect(callSoundManager) {
        callSoundManager.onSpeakingChanged = { speaking ->
            isSpeaking = speaking
        }
        onDispose {
            callSoundManager.onSpeakingChanged = null
            callSoundManager.release()
        }
    }

    // Start Ringing Sounds automatically & Reset Call Chat
    LaunchedEffect(Unit) {
        viewModel.resetCallChat()
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

    // Use direct realistic calling background (dark navy-blue dual tones) to look exactly like standard phone screen
    val dialerGradient = listOf(Color(0xFF031024), Color(0xFF010610))

    val callerTitle = when {
        callerType.startsWith("police") -> "شرطة الأطفال العراقية"
        callerType == "doctor" -> "طبيب الأسنان والأطفال البارع"
        callerType == "teacher" -> "معلّم الأحلام الذكي"
        callerType == "principal" -> "مدير المدرسة الفاضل"
        else -> "اتصال عائلي آمن"
    }

    val callerDesc = when {
        callerType.startsWith("police") -> {
            when (callerType) {
                "police_not_listening" -> "سلوك: عدم سماع الكلام والعناد"
                "police_sleep_late" -> "سلوك: السهر والتأخر في النوم"
                "police_refusing_study" -> "سلوك: التكاسل عن الواجبات والدراسة"
                "police_eating_sweets" -> "سلوك: الإفراط في الحلويات والسكاكر"
                "police_messy_room" -> "سلوك: ترك الألعاب مبعثرة والفوضى"
                "police_helping_parents" -> "سلوك إيجابي: مساعدة الوالدين والبر"
                "police_success" -> "سلوك إيجابي: التميز والنجاح الدراسي"
                "police_healthy_food" -> "سلوك إيجابي: تناول الغذاء الصحي القوي"
                else -> "تتصل بك..."
            }
        }
        callerType == "doctor" -> "الاهتمام بنظافة الأسنان والصحة 🪥"
        callerType == "teacher" -> "التوجيه لحب العلم والمعرفة"
        callerType == "principal" -> "التشجيع والنجاح الدراسي"
        else -> "مكالمة آمنة"
    }

    // Infinite pulse animations for background when calling to look amazing
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(dialerGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // A. Top real VoIP system status bar
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // التحكم وخيارات المشاركة للدعوة لتجربة المعلق البطل
                IconButton(onClick = {
                    val shareUrl = "https://ais-pre-7ldjbf3a7dwula4tvp55mq-837550959080.europe-west2.run.app/call/$callerType"
                    val sendIntent = android.content.Intent().apply {
                        action = android.content.Intent.ACTION_SEND
                        putExtra(
                            android.content.Intent.EXTRA_TEXT, 
                            "جرب اتصال $callerTitle (المعلق الصوتي التفاعلي البطل) الآن للأطفال عبر هذا الرابط: $shareUrl"
                        )
                        type = "text/plain"
                    }
                    val shareIntent = android.content.Intent.createChooser(sendIntent, "مشاركة رابط الدعوة لتجربة المعلق")
                    context.startActivity(shareIntent)
                }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }

                // Center Title banner (VoIP indicator)
                Spacer(modifier = Modifier.width(4.dp))

                // Right icons (WiFi + green WhatsApp VoIP bar with call icon like in the actual screenshot!)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isCallAnswered) {
                            val minutes = secondsElapsed / 60
                            val secs = secondsElapsed % 60
                            String.format("%02d:%02d", minutes, secs)
                        } else "٠٠:٠٢",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Small green pill with mini call symbol (VoIP active indicator)
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF22C55E), RoundedCornerShape(10.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Call,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(9.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // B. Avatar ring Container matching the picture (White bold borders around the green circular background)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(175.dp)
            ) {
                if (!isCallAnswered) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(pulseScale)
                            .background(Color.White.copy(alpha = 0.08f), CircleShape)
                    )
                }
                
                // Outer clean white bold circular border
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color.White, CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    ) {
                        when {
                            callerType.startsWith("police") -> {
                                IraqiPoliceIllustration(modifier = Modifier.fillMaxSize(), isSpeaking = isSpeaking)
                            }
                            callerType == "doctor" -> {
                                FriendlyDoctorIllustration(modifier = Modifier.fillMaxSize(), isSpeaking = isSpeaking)
                            }
                            callerType == "teacher" -> {
                                SmartTeacherIllustration(modifier = Modifier.fillMaxSize(), isSpeaking = isSpeaking)
                            }
                            callerType == "principal" -> {
                                SchoolPrincipalIllustration(modifier = Modifier.fillMaxSize(), isSpeaking = isSpeaking)
                            }
                            callerType == "monster" -> {
                                FriendlyMonsterIllustration(modifier = Modifier.fillMaxSize(), isSpeaking = isSpeaking)
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF1E3A8A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "📞", fontSize = 68.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // C. Text Headers
            Text(
                text = callerTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isCallAnswered) "جاري المكالمة الصوتية..." else callerDesc,
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (!isCallAnswered) {
                // D. Dialer 3x2 Grid Options representing actual call capabilities
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Row 1: "إضافة مكالمة" + "تعليق المكالمة" + "البلوتوث"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 1. إضافة مكالمة
                        CallOptionButton(
                            label = "إضافة مكالمة",
                            icon = {
                                androidx.compose.foundation.Canvas(modifier = Modifier.size(24.dp)) {
                                    val w = size.width
                                    val h = size.height
                                    drawLine(color = Color.White, start = androidx.compose.ui.geometry.Offset(w * 0.5f, 0f), end = androidx.compose.ui.geometry.Offset(w * 0.5f, h), strokeWidth = 3.5f)
                                    drawLine(color = Color.White, start = androidx.compose.ui.geometry.Offset(0f, h * 0.5f), end = androidx.compose.ui.geometry.Offset(w, h * 0.5f), strokeWidth = 3.5f)
                                }
                            }
                        ) {}

                        // 2. تعليق المكالمة
                        CallOptionButton(
                            label = "تعليق المكالمة",
                            icon = {
                                androidx.compose.foundation.Canvas(modifier = Modifier.size(22.dp)) {
                                    val w = size.width
                                    val h = size.height
                                    drawLine(color = Color.White, start = androidx.compose.ui.geometry.Offset(w * 0.33f, 0f), end = androidx.compose.ui.geometry.Offset(w * 0.33f, h), strokeWidth = 4f)
                                    drawLine(color = Color.White, start = androidx.compose.ui.geometry.Offset(w * 0.67f, 0f), end = androidx.compose.ui.geometry.Offset(w * 0.67f, h), strokeWidth = 4f)
                                }
                            }
                        ) {}

                        // 3. البلوتوث
                        CallOptionButton(
                            label = "البلوتوث",
                            icon = {
                                androidx.compose.foundation.Canvas(modifier = Modifier.size(22.dp)) {
                                    val w = size.width
                                    val h = size.height
                                    val path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(w * 0.3f, h * 0.3f)
                                        lineTo(w * 0.7f, h * 0.7f)
                                        lineTo(w * 0.5f, h * 0.9f)
                                        lineTo(w * 0.5f, h * 0.1f)
                                        lineTo(w * 0.7f, h * 0.3f)
                                        lineTo(w * 0.3f, h * 0.7f)
                                    }
                                    drawPath(
                                        path = path,
                                        color = Color.White,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f)
                                    )
                                }
                            }
                        ) {}
                    }

                    // Row 2: "لوحة المفاتيح" + "كتم" + "مكبر الصوت (أخضر نشط)"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 4. لوحة المفاتيح
                        CallOptionButton(
                            label = "لوحة المفاتيح",
                            icon = {
                                androidx.compose.foundation.Canvas(modifier = Modifier.size(22.dp)) {
                                    val r = 3f
                                    val color = Color.White
                                    drawCircle(color, r, androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.2f))
                                    drawCircle(color, r, androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.2f))
                                    drawCircle(color, r, androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.2f))
                                    drawCircle(color, r, androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.5f))
                                    drawCircle(color, r, androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.5f))
                                    drawCircle(color, r, androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.5f))
                                    drawCircle(color, r, androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.8f))
                                    drawCircle(color, r, androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.8f))
                                    drawCircle(color, r, androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.8f))
                                }
                            }
                        ) {}

                        // 5. كتم الميكروفون
                        CallOptionButton(
                            label = "كتم",
                            icon = {
                                androidx.compose.foundation.Canvas(modifier = Modifier.size(22.dp)) {
                                    val w = size.width
                                    val h = size.height
                                    drawRoundRect(
                                        color = Color.White,
                                        topLeft = androidx.compose.ui.geometry.Offset(w * 0.35f, h * 0.2f),
                                        size = androidx.compose.ui.geometry.Size(w * 0.3f, h * 0.5f),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f)
                                    )
                                    drawLine(color = Color.White, start = androidx.compose.ui.geometry.Offset(0f, h), end = androidx.compose.ui.geometry.Offset(w, 0f), strokeWidth = 4f)
                                }
                            }
                        ) {}

                        // 6. مكبر الصوت (يكون أخضر ونشط ومضيء 100% كما في صورة الفيديو الممتازة)
                        CallOptionButton(
                            label = "مكبر الصوت",
                            isActive = true, // Highlighted in solid bright green matching screenshot!
                            icon = {
                                androidx.compose.foundation.Canvas(modifier = Modifier.size(24.dp)) {
                                    val w = size.width
                                    val h = size.height
                                    val path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(w * 0.25f, h * 0.35f)
                                        lineTo(w * 0.45f, h * 0.35f)
                                        lineTo(w * 0.7f, h * 0.15f)
                                        lineTo(w * 0.7f, h * 0.85f)
                                        lineTo(w * 0.45f, h * 0.65f)
                                        lineTo(w * 0.25f, h * 0.65f)
                                        close()
                                    }
                                    drawPath(path, color = Color.White)
                                }
                            }
                        ) {}
                    }
                }
            } else {
                // One-sided call view: Show that the character is speaking
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isSpeaking) {
                            Text(
                                text = "يتم التحدث الآن...",
                                color = Color(0xFF10B981),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.weight(1f))

            // E. Ring-Accept / Disconnect Buttons at the Bottom
            if (!isCallAnswered) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 36.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Accept button (Green)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FloatingActionButton(
                            onClick = { isCallAnswered = true },
                            containerColor = Color(0xFF22C55E),
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(70.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Call,
                                contentDescription = "Accept",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Accept", color = Color(0xFF22C55E), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // 2. Decline button (Red)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FloatingActionButton(
                            onClick = {
                                callSoundManager.stopRingtone()
                                callSoundManager.stopSpeaking()
                                onEndCall()
                            },
                            containerColor = Color(0xFFEF4444),
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(70.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CallEnd,
                                contentDescription = "Decline",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Decline", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                // If call is answered, just show a single centered red disconnect button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = {
                            callSoundManager.stopRingtone()
                            callSoundManager.stopSpeaking()
                            onEndCall()
                        },
                        containerColor = Color(0xFFEF4444),
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CallEnd,
                            contentDescription = "End Call",
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CallOptionButton(
    label: String,
    isActive: Boolean = false,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .background(
                    if (isActive) Color(0xFF22C55E) else Color.White.copy(alpha = 0.08f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isActive) Color(0xFF22C55E) else Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}
