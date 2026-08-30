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
                            Color(0xFF030712), // Deep Space Black
                            Color(0xFF0B192C), // Midnight Blue
                            Color(0xFF1E3A8A), // Royal Police Blue
                            Color(0xFF020617)  // Deep Navy
                        )
                    )
                )
        ) {
            // Futuristic AI Radar Background
            SplashRadarCanvas(
                rotationAngle = radarRotation,
                modifier = Modifier.fillMaxSize()
            )

            // Skip button top left
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 36.dp, start = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B).copy(alpha = 0.7f))
                    .border(1.dp, Color(0xFF64748B), RoundedCornerShape(16.dp))
                    .clickable { onSplashFinished() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "تخطي ⏩",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
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
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF1D4ED8), Color(0xFF0284C7))
                            )
                        )
                        .border(1.5.dp, Color(0xFF60A5FA), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🛡️ نظام شرطة الأطفال الذكي 🤖",
                        color = Color(0xFFFDE047),
                        fontSize = 13.sp,
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
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = beaconPulse))
                                .border(2.dp, Color(0xFFFCA5A5), CircleShape)
                                .shadow(8.dp, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8).copy(alpha = 1.4f - beaconPulse))
                                .border(2.dp, Color(0xFFBAE6FD), CircleShape)
                                .shadow(8.dp, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3D Police Shield Crest Art
                    SplashBadgeHeroCanvas(modifier = Modifier.size(160.dp))

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "شرطة الأطفال",
                        color = Color(0xFFFACC15),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "مغامرات، نداءات، وتدريب الأبطال الصغار",
                        color = Color(0xFFE2E8F0),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
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
                        color = Color(0xFF38BDF8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Modern Glowing Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.5.dp, Color(0xFF1E3A8A), RoundedCornerShape(10.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .height(16.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF2563EB),
                                            Color(0xFF38BDF8),
                                            Color(0xFFFACC15)
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
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            color = Color(0xFFFDE047),
                            fontSize = 12.sp,
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
            color = Color(0xFF38BDF8).copy(alpha = 0.08f),
            radius = 120.dp.toPx(),
            center = Offset(cx, cy),
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawCircle(
            color = Color(0xFF38BDF8).copy(alpha = 0.06f),
            radius = 180.dp.toPx(),
            center = Offset(cx, cy),
            style = Stroke(width = 1.5.dp.toPx())
        )
        drawCircle(
            color = Color(0xFF38BDF8).copy(alpha = 0.04f),
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
                colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.5f), Color.Transparent),
                start = Offset(cx, cy),
                end = Offset(endX, endY)
            ),
            start = Offset(cx, cy),
            end = Offset(endX, endY),
            strokeWidth = 2.5.dp.toPx(),
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
                colors = listOf(Color(0xFF2563EB).copy(alpha = 0.4f), Color.Transparent),
                center = Offset(cx, cy),
                radius = w * 0.55f
            ),
            center = Offset(cx, cy),
            radius = w * 0.55f
        )

        // Shield Outer Frame (Gold Gradient)
        val outerShield = Path().apply {
            moveTo(cx, h * 0.08f)
            lineTo(w * 0.88f, h * 0.24f)
            lineTo(w * 0.88f, h * 0.62f)
            cubicTo(w * 0.88f, h * 0.82f, cx, h * 0.96f, cx, h * 0.96f)
            cubicTo(cx, h * 0.96f, w * 0.12f, h * 0.82f, w * 0.12f, h * 0.62f)
            lineTo(w * 0.12f, h * 0.24f)
            close()
        }
        drawPath(
            path = outerShield,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFDE047), Color(0xFFF59E0B), Color(0xFFB45309))
            )
        )

        // Shield Inner Plate (Deep Navy)
        val innerShield = Path().apply {
            moveTo(cx, h * 0.15f)
            lineTo(w * 0.80f, h * 0.28f)
            lineTo(w * 0.80f, h * 0.60f)
            cubicTo(w * 0.80f, h * 0.77f, cx, h * 0.89f, cx, h * 0.89f)
            cubicTo(cx, h * 0.89f, w * 0.20f, h * 0.77f, w * 0.20f, h * 0.60f)
            lineTo(w * 0.20f, h * 0.28f)
            close()
        }
        drawPath(
            path = innerShield,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F172A), Color(0xFF020617))
            )
        )

        // Large 5-Point Star in the Center
        val starPath = Path().apply {
            val starR = w * 0.22f
            val innerR = starR * 0.42f
            val starCy = cy + h * 0.04f
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
                colors = listOf(Color(0xFFFEF08A), Color(0xFFFACC15), Color(0xFFCA8A04)),
                center = Offset(cx, cy + h * 0.04f),
                radius = w * 0.25f
            )
        )
    }
}
