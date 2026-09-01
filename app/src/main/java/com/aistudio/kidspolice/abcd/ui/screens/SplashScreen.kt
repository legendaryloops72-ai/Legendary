package com.aistudio.kidspolice.abcd.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var statusText by remember { mutableStateOf("جاري بدء النظام الذكي...") }

    // Animate progress smoothly
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 350, easing = LinearEasing),
        label = "progress"
    )

    // Siren / Pulse animations
    val infiniteTransition = rememberInfiniteTransition(label = "splash_effects")
    val beaconPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_pulse"
    )
    val badgeScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badge_scale"
    )
    val radarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "radar_rot"
    )

    LaunchedEffect(Unit) {
        // Step 1
        delay(400)
        progress = 0.25f
        statusText = "📡 جاري تفعيل الاتصال بمركز العمليات..."

        // Step 2
        delay(600)
        progress = 0.55f
        statusText = "🚔 تجهيز دوريات وسيارات الشرطة الذكية..."

        // Step 3
        delay(600)
        progress = 0.85f
        statusText = "🚨 فحص صفارات الإنذار ومهام اليوم..."

        // Step 4
        delay(500)
        progress = 1.0f
        statusText = "⭐ جاهز للانطلاق! أهلاً بالبطل الصغير!"

        // Transition to home
        delay(400)
        onSplashFinished()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D47A1), // Deep Vibrant Blue
                            Color(0xFF1565C0), // Royal Blue
                            Color(0xFF1E88E5), // Vivid Blue
                            Color(0xFF0D47A1)  // Deep Blue Base
                        )
                    )
                )
        ) {
            // Futuristic AI Radar / Sky Cloud Canvas Background
            SplashRadarCanvas(
                rotationAngle = radarRotation,
                modifier = Modifier.fillMaxSize()
            )

            // Skip button top left
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 40.dp, start = 16.dp)
                    .shadow(6.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1565C0).copy(alpha = 0.85f))
                    .border(1.5.dp, Color(0xFF64B5F6), RoundedCornerShape(18.dp))
                    .clickable { onSplashFinished() }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "تخطي ⏩",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Central Hero Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Space / Tag
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF1976D2), Color(0xFF42A5F5))
                            )
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 18.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "🛡️ نظام شرطة الأطفال الذكي 🤖",
                        color = Color(0xFFFFF9C4),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Center Animated Emblem & Hero Badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(badgeScale)
                ) {
                    // Twin Emergency Flashing Beacons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .shadow(12.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935).copy(alpha = beaconPulse))
                                .border(2.5.dp, Color(0xFFFFCDD2), CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .shadow(12.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color(0xFF00B0FF).copy(alpha = 1.4f - beaconPulse))
                                .border(2.5.dp, Color(0xFFE1F5FE), CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3D Police Shield Crest Art with Officer Cap
                    SplashBadgeHeroCanvas(modifier = Modifier.size(175.dp))

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "شرطة الأطفال",
                        color = Color(0xFFFFD54F),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "مغامرات، نداءات، وتدريب الأبطال الصغار",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // Bottom Loader & Progress Bar
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Status text
                    Text(
                        text = statusText,
                        color = Color(0xFFE0F7FA),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Modern Glowing Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .shadow(6.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF082759))
                            .border(2.dp, Color(0xFF64B5F6), RoundedCornerShape(12.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .height(18.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF1E88E5),
                                            Color(0xFF00E5FF),
                                            Color(0xFFFFD600)
                                        )
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "الإصدار 3.0 الذكي",
                            color = Color(0xFFB0BEC5),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            color = Color(0xFFFFD54F),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SplashRadarCanvas(
    rotationAngle: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height * 0.44f

        // Radar grid circles
        drawCircle(
            color = Color(0xFF64B5F6).copy(alpha = 0.15f),
            radius = 120.dp.toPx(),
            center = Offset(cx, cy),
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawCircle(
            color = Color(0xFF64B5F6).copy(alpha = 0.10f),
            radius = 180.dp.toPx(),
            center = Offset(cx, cy),
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawCircle(
            color = Color(0xFF64B5F6).copy(alpha = 0.08f),
            radius = 240.dp.toPx(),
            center = Offset(cx, cy),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Animated Radar Beam
        val sweepRadius = 220.dp.toPx()
        val sweepRad = Math.toRadians(rotationAngle.toDouble())
        val endX = cx + (sweepRadius * Math.cos(sweepRad)).toFloat()
        val endY = cy + (sweepRadius * Math.sin(sweepRad)).toFloat()

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF80D8FF).copy(alpha = 0.6f), Color.Transparent),
                start = Offset(cx, cy),
                end = Offset(endX, endY)
            ),
            start = Offset(cx, cy),
            end = Offset(endX, endY),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun SplashBadgeHeroCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val w = size.width
        val h = size.height

        // Outer Glow Aura
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF42A5F5).copy(alpha = 0.5f), Color.Transparent),
                center = Offset(cx, cy),
                radius = w * 0.58f
            ),
            center = Offset(cx, cy),
            radius = w * 0.58f
        )

        // Shield Outer Frame (Gold 3D Gradient)
        val outerShield = Path().apply {
            moveTo(cx, h * 0.12f)
            lineTo(w * 0.90f, h * 0.28f)
            lineTo(w * 0.90f, h * 0.64f)
            cubicTo(w * 0.90f, h * 0.84f, cx, h * 0.98f, cx, h * 0.98f)
            cubicTo(cx, h * 0.98f, w * 0.10f, h * 0.84f, w * 0.10f, h * 0.64f)
            lineTo(w * 0.10f, h * 0.28f)
            close()
        }
        drawPath(
            path = outerShield,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFFF59D), Color(0xFFFFD54F), Color(0xFFFF8F00), Color(0xFFC62828))
            )
        )

        // Shield Inner Plate (Deep Royal Blue)
        val innerShield = Path().apply {
            moveTo(cx, h * 0.18f)
            lineTo(w * 0.82f, h * 0.32f)
            lineTo(w * 0.82f, h * 0.62f)
            cubicTo(w * 0.82f, h * 0.79f, cx, h * 0.91f, cx, h * 0.91f)
            cubicTo(cx, h * 0.91f, w * 0.18f, h * 0.79f, w * 0.18f, h * 0.62f)
            lineTo(w * 0.18f, h * 0.32f)
            close()
        }
        drawPath(
            path = innerShield,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1E88E5), Color(0xFF0D47A1), Color(0xFF071E4A))
            )
        )

        // Top Left Red Beacon Glow
        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFF8A80), Color(0xFFE53935))
            ),
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = Offset(w * 0.20f, h * 0.20f),
            size = Size(w * 0.25f, h * 0.18f)
        )

        // Top Right Blue Beacon Glow
        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF80D8FF), Color(0xFF00B0FF))
            ),
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = true,
            topLeft = Offset(w * 0.55f, h * 0.20f),
            size = Size(w * 0.25f, h * 0.18f)
        )

        // 3D Police Cap on Top
        val capCrown = Path().apply {
            moveTo(cx, h * 0.08f)
            cubicTo(w * 0.30f, h * 0.08f, w * 0.24f, h * 0.22f, w * 0.24f, h * 0.30f)
            cubicTo(w * 0.24f, h * 0.36f, w * 0.32f, h * 0.38f, cx, h * 0.38f)
            cubicTo(w * 0.68f, h * 0.38f, w * 0.76f, h * 0.36f, w * 0.76f, h * 0.30f)
            cubicTo(w * 0.76f, h * 0.22f, w * 0.70f, h * 0.08f, cx, h * 0.08f)
            close()
        }
        drawPath(
            path = capCrown,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1976D2), Color(0xFF0D47A1), Color(0xFF051329))
            )
        )

        // Glossy Cap Visor
        val capVisor = Path().apply {
            moveTo(w * 0.28f, h * 0.32f)
            cubicTo(w * 0.32f, h * 0.42f, w * 0.68f, h * 0.42f, w * 0.72f, h * 0.32f)
            cubicTo(w * 0.66f, h * 0.37f, w * 0.34f, h * 0.37f, w * 0.28f, h * 0.32f)
            close()
        }
        drawPath(
            path = capVisor,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF455A64), Color(0xFF212121), Color(0xFF000000))
            )
        )

        // Cap Gold Badge
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFD54F), Color(0xFFFFA000))
            ),
            radius = w * 0.05f,
            center = Offset(cx, h * 0.24f)
        )

        // Large 5-Point Star in the Center (Lower Plate)
        val starPath = Path().apply {
            val starR = w * 0.22f
            val innerR = starR * 0.42f
            val starCy = h * 0.62f
            for (i in 0 until 10) {
                val r = if (i % 2 == 0) starR else innerR
                val angle = Math.toRadians((i * 36 - 90).toDouble())
                val x = cx + (r * Math.cos(angle)).toFloat()
                val y = starCy + (r * Math.sin(angle)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(
            path = starPath,
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFFDE7), Color(0xFFFFEE58), Color(0xFFFFB300), Color(0xFFE65100)),
                center = Offset(cx, h * 0.62f),
                radius = w * 0.24f
            )
        )

        // Star Specular Highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.75f),
            radius = w * 0.04f,
            center = Offset(cx - w * 0.04f, h * 0.58f)
        )
    }
}
