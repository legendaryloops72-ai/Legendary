package com.aistudio.kidspolice.abcd.ui

import android.app.Activity
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val SafeEaseInOutBack = Easing { fraction ->
    try {
        if (fraction >= 0.99f) 1f else if (fraction <= 0.01f) 0f else EaseInOutBack.transform(fraction)
    } catch (e: Throwable) {
        fraction
    }
}

private val SafeEaseOutBack = Easing { fraction ->
    try {
        if (fraction >= 0.99f) 1f else if (fraction <= 0.01f) 0f else EaseOutBack.transform(fraction)
    } catch (e: Throwable) {
        fraction
    }
}

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val activity = context as? Activity
    
    var progress by remember { mutableFloatStateOf(0f) }
    
    // Animation States
    val scale = remember { Animatable(0.4f) }
    val alpha = remember { Animatable(0f) }
    val textYOffset = remember { Animatable(40f) }
    
    // Sparkle / Rotation Animation
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = SafeEaseInOutBack),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // 1. Entrance & Transition Orchestration
    LaunchedEffect(Unit) {
        // Parallel animations for visual polish
        val anim1 = launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        val anim2 = launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(1000, easing = SafeEaseOutBack)
            )
        }
        val anim3 = launch {
            textYOffset.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
        
        // Progress bar simulation (Determinate)
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        ) { value, _ ->
            progress = value
        }

        // Wait for all essential entrance animations to finish if they haven't already
        anim1.join()
        anim2.join()
        anim3.join()
        
        // Final heartbeat delay for brand recognition
        delay(300)
        
        onSplashFinished()
    }

    // Beautiful starry deep cosmic background gradient
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF030C24), // Deep Space Midnight Blue
            Color(0xFF08102A),
            Color(0xFF030C24)
        )
    )

    val titleColor = Color(0xFFF8FAFC) // Crisp high-contrast white for space theme
    val subtitleColor = Color(0xFF38BDF8) // Soft cosmic cyan


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background elements (Rotating Sparkles & Magic Circle)
        Box(
            modifier = Modifier
                .size(350.dp)
                .graphicsLayer {
                    rotationZ = rotation
                    scaleX = pulseScale
                    scaleY = pulseScale
                }
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            // A glowing soft radial backdrop
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (isDark) {
                                listOf(Color(0x330284C7), Color.Transparent)
                            } else {
                                listOf(Color(0x443B82F6), Color.Transparent)
                            }
                        )
                    )
            )
        }

        // Main content column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Animated Mascot Card
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        this.alpha = alpha.value
                    }
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(40.dp),
                        clip = false,
                        ambientColor = Color(0xFF38BDF8),
                        spotColor = Color(0xFF38BDF8)
                    )
                    .background(Color(0xFF0F172A).copy(alpha = 0.6f), RoundedCornerShape(40.dp))
                    .border(
                        width = 2.dp,
                        color = Color(0xFF38BDF8).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(40.dp)
                    )
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = com.aistudio.kidspolice.abcd.R.drawable.kids_police_splash_1783935509010),
                    contentDescription = "Mascot",
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Animated Typography Block
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    translationY = textYOffset.value
                    this.alpha = alpha.value
                }
            ) {
                Text(
                    text = "شرطة الأطفال والأبطال",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = titleColor,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "عالم مليء بالمغامرات والتعلم المرح! 🌟",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = subtitleColor,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Cute matching progress bar to indicate active loading
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(140.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .graphicsLayer { this.alpha = alpha.value },
                color = if (isDark) Color(0xFF38BDF8) else Color(0xFF3B82F6),
                trackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
            )
        }
    }
}
