package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text

@Composable
fun FriendlyDoctorIllustration(modifier: Modifier = Modifier, isSpeaking: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "doctor_anim")
    
    val translationY by if (isSpeaking) {
        infiniteTransition.animateFloat(
            initialValue = -4f,
            targetValue = 4f,
            animationSpec = infiniteRepeatable(
                animation = tween(350, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bob"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val rotationAngle by if (isSpeaking) {
        infiniteTransition.animateFloat(
            initialValue = -1.5f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "rotate"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val mouthScaleY by if (isSpeaking) {
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(220, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "mouth"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    val blinkScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.keyframes {
                durationMillis = 3200
                1f at 0
                1f at 2800
                0.1f at 3000
                1f at 3200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )

    Box(
        modifier = modifier
            .size(80.dp)
            .graphicsLayer {
                this.translationY = translationY * density
                this.rotationZ = rotationAngle
                this.scaleX = breathingScale
                this.scaleY = breathingScale
            }
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFECFDF5), Color(0xFFA7F3D0))
                ),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawCircle(
                color = Color(0xFF34D399).copy(alpha = 0.25f),
                radius = w * 0.46f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f)
            )

            val neckPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.44f, h * 0.68f)
                lineTo(w * 0.56f, h * 0.68f)
                lineTo(w * 0.56f, h * 0.80f)
                lineTo(w * 0.44f, h * 0.80f)
                close()
            }
            drawPath(neckPath, color = Color(0xFFFDBA74).copy(alpha = 0.8f))

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFE5E5), Color(0xFFFDBA74)),
                    center = androidx.compose.ui.geometry.Offset(w * 0.48f, h * 0.52f),
                    radius = w * 0.24f
                ),
                radius = w * 0.24f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.54f)
            )

            drawCircle(
                color = Color(0xFFFB7185).copy(alpha = 0.35f),
                radius = w * 0.04f,
                center = androidx.compose.ui.geometry.Offset(w * 0.36f, h * 0.58f)
            )
            drawCircle(
                color = Color(0xFFFB7185).copy(alpha = 0.35f),
                radius = w * 0.04f,
                center = androidx.compose.ui.geometry.Offset(w * 0.64f, h * 0.58f)
            )

            val hairPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.23f, h * 0.45f)
                quadraticTo(w * 0.5f, h * 0.16f, w * 0.77f, h * 0.45f)
                lineTo(w * 0.73f, h * 0.32f)
                quadraticTo(w * 0.5f, h * 0.15f, w * 0.27f, h * 0.32f)
                close()
            }
            drawPath(
                hairPath,
                brush = Brush.verticalGradient(listOf(Color(0xFF78350F), Color(0xFF451A03)))
            )

            val whiteEyeHeight = w * 0.11f * blinkScale
            val irisEyeHeight = w * 0.07f * blinkScale
            val pupilEyeHeight = w * 0.036f * blinkScale

            drawOval(color = Color.White, topLeft = androidx.compose.ui.geometry.Offset(w * 0.39f - w * 0.055f, h * 0.51f - whiteEyeHeight / 2f), size = androidx.compose.ui.geometry.Size(w * 0.11f, whiteEyeHeight))
            drawOval(brush = Brush.radialGradient(colors = listOf(Color(0xFF0D9488), Color(0xFF115E59)), radius = w * 0.035f), topLeft = androidx.compose.ui.geometry.Offset(w * 0.39f - w * 0.035f, h * 0.51f - irisEyeHeight / 2f), size = androidx.compose.ui.geometry.Size(w * 0.07f, irisEyeHeight))
            drawOval(color = Color(0xFF0F172A), topLeft = androidx.compose.ui.geometry.Offset(w * 0.39f - w * 0.018f, h * 0.51f - pupilEyeHeight / 2f), size = androidx.compose.ui.geometry.Size(w * 0.036f, pupilEyeHeight))
            if (blinkScale > 0.5f) {
                drawCircle(color = Color.White, radius = w * 0.008f, center = androidx.compose.ui.geometry.Offset(w * 0.375f, h * 0.495f))
            }

            drawOval(color = Color.White, topLeft = androidx.compose.ui.geometry.Offset(w * 0.61f - w * 0.055f, h * 0.51f - whiteEyeHeight / 2f), size = androidx.compose.ui.geometry.Size(w * 0.11f, whiteEyeHeight))
            drawOval(brush = Brush.radialGradient(colors = listOf(Color(0xFF0D9488), Color(0xFF115E59)), radius = w * 0.035f), topLeft = androidx.compose.ui.geometry.Offset(w * 0.61f - w * 0.035f, h * 0.51f - irisEyeHeight / 2f), size = androidx.compose.ui.geometry.Size(w * 0.07f, irisEyeHeight))
            drawOval(color = Color(0xFF0F172A), topLeft = androidx.compose.ui.geometry.Offset(w * 0.61f - w * 0.018f, h * 0.51f - pupilEyeHeight / 2f), size = androidx.compose.ui.geometry.Size(w * 0.036f, pupilEyeHeight))
            if (blinkScale > 0.5f) {
                drawCircle(color = Color.White, radius = w * 0.008f, center = androidx.compose.ui.geometry.Offset(w * 0.595f, h * 0.495f))
            }

            if (isSpeaking) {
                val mouthWidth = w * 0.09f
                val mouthHeight = h * 0.07f * mouthScaleY
                drawOval(
                    color = Color(0xFF991B1B),
                    topLeft = androidx.compose.ui.geometry.Offset(w * 0.5f - mouthWidth / 2f, h * 0.62f - mouthHeight / 2f),
                    size = androidx.compose.ui.geometry.Size(mouthWidth, mouthHeight)
                )
                val tongueWidth = mouthWidth * 0.6f
                val tongueHeight = mouthHeight * 0.3f
                drawOval(
                    color = Color(0xFFEF4444),
                    topLeft = androidx.compose.ui.geometry.Offset(w * 0.5f - tongueWidth / 2f, h * 0.62f + mouthHeight / 2f - tongueHeight),
                    size = androidx.compose.ui.geometry.Size(tongueWidth, tongueHeight)
                )
            } else {
                val smilePath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.44f, h * 0.61f)
                    quadraticTo(w * 0.5f, h * 0.67f, w * 0.56f, h * 0.61f)
                }
                drawPath(
                    smilePath,
                    color = Color(0xFFBE123C),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }
            drawCircle(color = Color(0xFFF4A261), radius = w * 0.012f, center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.56f))

            val scrubPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.40f, h * 0.74f)
                lineTo(w * 0.50f, h * 0.86f)
                lineTo(w * 0.60f, h * 0.74f)
                close()
            }
            drawPath(scrubPath, color = Color(0xFF0D9488))

            val coatPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.24f, h * 0.74f)
                lineTo(w * 0.14f, h * 0.98f)
                lineTo(w * 0.86f, h * 0.98f)
                lineTo(w * 0.76f, h * 0.74f)
                close()
            }
            drawPath(
                coatPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color.White, Color(0xFFF1F5F9))
                )
            )

            drawRoundRect(
                color = Color(0xFFE2E8F0),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.62f, h * 0.82f),
                size = androidx.compose.ui.geometry.Size(w * 0.14f, h * 0.12f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )
            drawRect(
                color = Color(0xFFEF4444),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.675f, h * 0.84f),
                size = androidx.compose.ui.geometry.Size(w * 0.03f, h * 0.08f)
            )
            drawRect(
                color = Color(0xFFEF4444),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.65f, h * 0.865f),
                size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.03f)
            )

            drawArc(
                color = Color(0xFF475569),
                startAngle = 10f,
                sweepAngle = 160f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.25f, h * 0.53f),
                size = androidx.compose.ui.geometry.Size(w * 0.50f, h * 0.28f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5.5f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
            drawCircle(
                color = Color(0xFF94A3B8),
                radius = w * 0.025f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.81f)
            )
            drawCircle(
                color = Color(0xFF64748B),
                radius = w * 0.07f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.89f)
            )
            drawCircle(
                color = Color(0xFFCBD5E1),
                radius = w * 0.055f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.89f)
            )
            drawCircle(
                color = Color.White,
                radius = w * 0.022f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.89f)
            )
        }
    }
}

@Composable
fun SmartTeacherIllustration(modifier: Modifier = Modifier, isSpeaking: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "teacher_anim")
    
    val translationY by if (isSpeaking) {
        infiniteTransition.animateFloat(
            initialValue = -4f,
            targetValue = 4f,
            animationSpec = infiniteRepeatable(
                animation = tween(350, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bob"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val rotationAngle by if (isSpeaking) {
        infiniteTransition.animateFloat(
            initialValue = -1.5f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "rotate"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val mouthScaleY by if (isSpeaking) {
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(220, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "mouth"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    val blinkScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.keyframes {
                durationMillis = 3200
                1f at 0
                1f at 2800
                0.1f at 3000
                1f at 3200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )

    Box(
        modifier = modifier
            .size(80.dp)
            .graphicsLayer {
                this.translationY = translationY * density
                this.rotationZ = rotationAngle
                this.scaleX = breathingScale
                this.scaleY = breathingScale
            }
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF7ED), Color(0xFFFED7AA))
                ),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawCircle(
                color = Color(0xFFF97316).copy(alpha = 0.25f),
                radius = w * 0.46f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f)
            )

            val neckPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.44f, h * 0.68f)
                lineTo(w * 0.56f, h * 0.68f)
                lineTo(w * 0.56f, h * 0.80f)
                lineTo(w * 0.44f, h * 0.80f)
                close()
            }
            drawPath(neckPath, color = Color(0xFFFDC7C7))

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFE5E5), Color(0xFFFDBA74)),
                    center = androidx.compose.ui.geometry.Offset(w * 0.48f, h * 0.52f),
                    radius = w * 0.24f
                ),
                radius = w * 0.24f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.54f)
            )

            val hairPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.22f, h * 0.45f)
                quadraticTo(w * 0.5f, h * 0.22f, w * 0.78f, h * 0.45f)
                lineTo(w * 0.76f, h * 0.32f)
                quadraticTo(w * 0.5f, h * 0.19f, w * 0.24f, h * 0.32f)
                close()
            }
            drawPath(
                hairPath,
                brush = Brush.verticalGradient(listOf(Color(0xFF9A3412), Color(0xFF431407)))
            )

            drawCircle(
                color = Color(0xFFEA580C),
                radius = w * 0.085f,
                center = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.51f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.5f)
            )
            drawCircle(
                color = Color(0xFFDBEAFE).copy(alpha = 0.4f),
                radius = w * 0.075f,
                center = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.51f)
            )
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = androidx.compose.ui.geometry.Offset(w * 0.34f, h * 0.47f),
                end = androidx.compose.ui.geometry.Offset(w * 0.39f, h * 0.53f),
                strokeWidth = 3f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            val leftPupilEyeHeight = w * 0.05f * blinkScale
            drawOval(
                color = Color(0xFF1E293B),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.38f - w * 0.025f, h * 0.51f - leftPupilEyeHeight / 2f),
                size = androidx.compose.ui.geometry.Size(w * 0.05f, leftPupilEyeHeight)
            )

            drawCircle(
                color = Color(0xFFEA580C),
                radius = w * 0.085f,
                center = androidx.compose.ui.geometry.Offset(w * 0.62f, h * 0.51f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.5f)
            )
            drawCircle(
                color = Color(0xFFDBEAFE).copy(alpha = 0.4f),
                radius = w * 0.075f,
                center = androidx.compose.ui.geometry.Offset(w * 0.62f, h * 0.51f)
            )
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = androidx.compose.ui.geometry.Offset(w * 0.58f, h * 0.47f),
                end = androidx.compose.ui.geometry.Offset(w * 0.63f, h * 0.53f),
                strokeWidth = 3f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            val rightPupilEyeHeight = w * 0.05f * blinkScale
            drawOval(
                color = Color(0xFF1E293B),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.62f - w * 0.025f, h * 0.51f - rightPupilEyeHeight / 2f),
                size = androidx.compose.ui.geometry.Size(w * 0.05f, rightPupilEyeHeight)
            )

            drawLine(
                color = Color(0xFFEA580C),
                start = androidx.compose.ui.geometry.Offset(w * 0.465f, h * 0.51f),
                end = androidx.compose.ui.geometry.Offset(w * 0.535f, h * 0.51f),
                strokeWidth = 3.5f
            )

            if (isSpeaking) {
                val mouthWidth = w * 0.08f
                val mouthHeight = h * 0.06f * mouthScaleY
                drawOval(
                    color = Color(0xFF7F1D1D),
                    topLeft = androidx.compose.ui.geometry.Offset(w * 0.5f - mouthWidth / 2f, h * 0.63f - mouthHeight / 2f),
                    size = androidx.compose.ui.geometry.Size(mouthWidth, mouthHeight)
                )
            } else {
                val smilePath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.45f, h * 0.63f)
                    quadraticTo(w * 0.5f, h * 0.67f, w * 0.55f, h * 0.63f)
                }
                drawPath(
                    smilePath,
                    color = Color(0xFFBE123C),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }

            val shirtPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.25f, h * 0.76f)
                lineTo(w * 0.15f, h * 0.98f)
                lineTo(w * 0.85f, h * 0.98f)
                lineTo(w * 0.75f, h * 0.76f)
                close()
            }
            drawPath(
                shirtPath,
                brush = Brush.linearGradient(colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)))
            )

            val collarPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.40f, h * 0.76f)
                lineTo(w * 0.50f, h * 0.86f)
                lineTo(w * 0.60f, h * 0.76f)
                close()
            }
            drawPath(collarPath, color = Color.White)

            val tiePath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.47f, h * 0.82f)
                lineTo(w * 0.53f, h * 0.82f)
                lineTo(w * 0.55f, h * 0.98f)
                lineTo(w * 0.50f, h * 1.0f)
                lineTo(w * 0.45f, h * 0.98f)
                close()
            }
            drawPath(tiePath, color = Color(0xFF1E293B))
        }
    }
}

@Composable
fun IraqiPoliceIllustration(modifier: Modifier = Modifier, isSpeaking: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "police_anim")
    
    val translationY by if (isSpeaking) {
        infiniteTransition.animateFloat(
            initialValue = -4f,
            targetValue = 4f,
            animationSpec = infiniteRepeatable(
                animation = tween(350, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bob"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val rotationAngle by if (isSpeaking) {
        infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "rotate"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val mouthScaleY by if (isSpeaking) {
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(250, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "mouth"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    val blinkScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.keyframes {
                durationMillis = 4000
                1f at 0
                1f at 3700
                0.1f at 3850
                1f at 4000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "blink"
    )

    Box(
        modifier = modifier
            .size(80.dp)
            .graphicsLayer {
                this.translationY = translationY * density
                this.rotationZ = rotationAngle
            },
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawCircle(
                color = Color(0xFF1E3A8A).copy(alpha = 0.2f),
                radius = w * 0.48f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f)
            )

            val neckPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.44f, h * 0.68f)
                lineTo(w * 0.56f, h * 0.68f)
                lineTo(w * 0.56f, h * 0.82f)
                lineTo(w * 0.44f, h * 0.82f)
                close()
            }
            drawPath(neckPath, color = Color(0xFFFDBA74).copy(alpha = 0.85f))

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF1F1), Color(0xFFFDBA74)),
                    center = androidx.compose.ui.geometry.Offset(w * 0.47f, h * 0.55f),
                    radius = w * 0.26f
                ),
                radius = w * 0.26f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.58f)
            )

            val whiteEyeHeight = w * 0.08f * blinkScale
            val pupilEyeHeight = w * 0.03f * blinkScale

            drawCircle(color = Color.White, radius = w * 0.05f, center = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.55f))
            drawOval(color = Color(0xFF0F172A), topLeft = androidx.compose.ui.geometry.Offset(w * 0.38f - w * 0.015f, h * 0.55f - pupilEyeHeight / 2f), size = androidx.compose.ui.geometry.Size(w * 0.03f, pupilEyeHeight))

            drawCircle(color = Color.White, radius = w * 0.05f, center = androidx.compose.ui.geometry.Offset(w * 0.62f, h * 0.55f))
            drawOval(color = Color(0xFF0F172A), topLeft = androidx.compose.ui.geometry.Offset(w * 0.62f - w * 0.015f, h * 0.55f - pupilEyeHeight / 2f), size = androidx.compose.ui.geometry.Size(w * 0.03f, pupilEyeHeight))

            if (isSpeaking) {
                val mouthWidth = w * 0.1f
                val mouthHeight = h * 0.08f * mouthScaleY
                drawOval(
                    color = Color(0xFF7F1D1D),
                    topLeft = androidx.compose.ui.geometry.Offset(w * 0.5f - mouthWidth / 2f, h * 0.65f - mouthHeight / 2f),
                    size = androidx.compose.ui.geometry.Size(mouthWidth, mouthHeight)
                )
            } else {
                val smilePath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.43f, h * 0.66f)
                    quadraticTo(w * 0.5f, h * 0.72f, w * 0.57f, h * 0.66f)
                }
                drawPath(smilePath, color = Color(0xFFBE123C), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            }

            val capPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.15f, h * 0.35f)
                quadraticTo(w * 0.5f, h * 0.08f, w * 0.85f, h * 0.35f)
                lineTo(w * 0.82f, h * 0.48f)
                quadraticTo(w * 0.5f, h * 0.42f, w * 0.18f, h * 0.48f)
                close()
            }
            drawPath(capPath, brush = Brush.verticalGradient(listOf(Color(0xFF2E5CB8), Color(0xFF1E3A8A))))

            drawCircle(color = Color(0xFFEAB308), radius = w * 0.07f, center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.28f))

            val suitPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.22f, h * 0.80f)
                lineTo(w * 0.10f, h * 0.98f)
                lineTo(w * 0.90f, h * 0.98f)
                lineTo(w * 0.78f, h * 0.80f)
                quadraticTo(w * 0.5f, h * 0.76f, w * 0.22f, h * 0.80f)
                close()
            }
            drawPath(suitPath, color = Color(0xFF1E3A8A))
        }
    }
}

@Composable
fun SchoolPrincipalIllustration(modifier: Modifier = Modifier, isSpeaking: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "principal_anim")
    
    val translationY by if (isSpeaking) {
        infiniteTransition.animateFloat(
            initialValue = -3f,
            targetValue = 3f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bob"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    Box(
        modifier = modifier
            .size(80.dp)
            .graphicsLayer {
                this.translationY = translationY * density
            }
            .background(
                Brush.radialGradient(colors = listOf(Color(0xFFF1F5F9), Color(0xFFCBD5E1))),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawCircle(
                color = Color(0xFF334155),
                radius = w * 0.25f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.55f)
            )

            val hairPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.24f, h * 0.48f)
                quadraticTo(w * 0.5f, h * 0.25f, w * 0.76f, h * 0.48f)
                lineTo(w * 0.74f, h * 0.38f)
                quadraticTo(w * 0.5f, h * 0.22f, w * 0.26f, h * 0.38f)
                close()
            }
            drawPath(hairPath, color = Color(0xFF475569))

            drawCircle(color = Color(0xFF0F172A), radius = w * 0.025f, center = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.55f))
            drawCircle(color = Color(0xFF0F172A), radius = w * 0.025f, center = androidx.compose.ui.geometry.Offset(w * 0.62f, h * 0.55f))

            val smilePath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.45f, h * 0.65f)
                quadraticTo(w * 0.5f, h * 0.69f, w * 0.55f, h * 0.65f)
            }
            drawPath(smilePath, color = Color(0xFFBE123C), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))

            val suitPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.22f, h * 0.78f)
                lineTo(w * 0.10f, h * 0.98f)
                lineTo(w * 0.90f, h * 0.98f)
                lineTo(w * 0.78f, h * 0.78f)
                close()
            }
            drawPath(suitPath, color = Color(0xFF334155))
        }
    }
}

@Composable
fun FriendlyMonsterIllustration(modifier: Modifier = Modifier, isSpeaking: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "monster_anim")
    
    val translationY by if (isSpeaking) {
        infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(300, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bob"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val mouthScaleY by if (isSpeaking) {
        infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(200, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "mouth"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    Box(
        modifier = modifier
            .size(80.dp)
            .graphicsLayer {
                this.translationY = translationY * density
            }
            .background(
                Brush.radialGradient(colors = listOf(Color(0xFFF5F3FF), Color(0xFFDDD6FE))),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Body
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF8B5CF6), Color(0xFF5B21B6)),
                    center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.6f),
                    radius = w * 0.4f
                ),
                radius = w * 0.4f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.6f)
            )

            // Horns
            val leftHorn = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.3f, h * 0.35f)
                quadraticTo(w * 0.25f, h * 0.15f, w * 0.15f, h * 0.2f)
                quadraticTo(w * 0.2f, h * 0.38f, w * 0.3f, h * 0.35f)
            }
            drawPath(leftHorn, color = Color(0xFFC084FC))

            val rightHorn = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.7f, h * 0.35f)
                quadraticTo(w * 0.75f, h * 0.15f, w * 0.85f, h * 0.2f)
                quadraticTo(w * 0.8f, h * 0.38f, w * 0.7f, h * 0.35f)
            }
            drawPath(rightHorn, color = Color(0xFFC084FC))

            // One big eye
            drawCircle(color = Color.White, radius = w * 0.18f, center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f))
            drawCircle(color = Color(0xFF1E293B), radius = w * 0.08f, center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f))
            drawCircle(color = Color.White, radius = w * 0.02f, center = androidx.compose.ui.geometry.Offset(w * 0.46f, h * 0.46f))

            // Mouth
            if (isSpeaking) {
                val mouthWidth = w * 0.2f
                val mouthHeight = h * 0.15f * mouthScaleY
                drawOval(
                    color = Color(0xFF4C1D95),
                    topLeft = androidx.compose.ui.geometry.Offset(w * 0.5f - mouthWidth / 2f, h * 0.75f - mouthHeight / 2f),
                    size = androidx.compose.ui.geometry.Size(mouthWidth, mouthHeight)
                )
                // Teeth
                drawRect(Color.White, topLeft = androidx.compose.ui.geometry.Offset(w * 0.46f, h * 0.75f - mouthHeight / 2f), size = androidx.compose.ui.geometry.Size(w * 0.03f, h * 0.03f))
                drawRect(Color.White, topLeft = androidx.compose.ui.geometry.Offset(w * 0.51f, h * 0.75f - mouthHeight / 2f), size = androidx.compose.ui.geometry.Size(w * 0.03f, h * 0.03f))
            } else {
                val smilePath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.4f, h * 0.72f)
                    quadraticTo(w * 0.5f, h * 0.82f, w * 0.6f, h * 0.72f)
                }
                drawPath(smilePath, color = Color.White, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            }
        }
    }
}

@Composable
fun KidPoliceIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFEFF6FF), Color(0xFF93C5FD))
                ),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawCircle(
                color = Color(0xFF60A5FA).copy(alpha = 0.25f),
                radius = w * 0.46f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f)
            )

            val neckPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.44f, h * 0.72f)
                lineTo(w * 0.56f, h * 0.72f)
                lineTo(w * 0.56f, h * 0.84f)
                lineTo(w * 0.44f, h * 0.84f)
                close()
            }
            drawPath(neckPath, color = Color(0xFFFDBA74).copy(alpha = 0.8f))

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF1F1), Color(0xFFFDBA74)),
                    center = androidx.compose.ui.geometry.Offset(w * 0.47f, h * 0.58f),
                    radius = w * 0.25f
                ),
                radius = w * 0.25f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.61f)
            )

            drawCircle(
                color = Color(0xFFFB7185).copy(alpha = 0.4f),
                radius = w * 0.045f,
                center = androidx.compose.ui.geometry.Offset(w * 0.35f, h * 0.65f)
            )
            drawCircle(
                color = Color(0xFFFB7185).copy(alpha = 0.4f),
                radius = w * 0.045f,
                center = androidx.compose.ui.geometry.Offset(w * 0.65f, h * 0.65f)
            )

            drawCircle(
                color = Color.White,
                radius = w * 0.06f,
                center = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.59f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF2563EB), Color(0xFF1E3A8A)),
                    center = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.59f),
                    radius = w * 0.04f
                ),
                radius = w * 0.04f,
                center = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.59f)
            )
            drawCircle(
                color = Color(0xFF0F172A),
                radius = w * 0.022f,
                center = androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.59f)
            )

            drawCircle(
                color = Color.White,
                radius = w * 0.06f,
                center = androidx.compose.ui.geometry.Offset(w * 0.62f, h * 0.59f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF2563EB), Color(0xFF1E3A8A)),
                    center = androidx.compose.ui.geometry.Offset(w * 0.62f, h * 0.59f),
                    radius = w * 0.04f
                ),
                radius = w * 0.04f,
                center = androidx.compose.ui.geometry.Offset(w * 0.62f, h * 0.59f)
            )
            drawCircle(
                color = Color(0xFF0F172A),
                radius = w * 0.022f,
                center = androidx.compose.ui.geometry.Offset(w * 0.62f, h * 0.59f)
            )

            val smilePath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.43f, h * 0.69f)
                quadraticTo(w * 0.5f, h * 0.76f, w * 0.57f, h * 0.69f)
            }
            drawPath(
                smilePath,
                color = Color(0xFFBE123C),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            val capPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.12f, h * 0.28f)
                quadraticTo(w * 0.5f, h * 0.01f, w * 0.88f, h * 0.28f)
                lineTo(w * 0.84f, h * 0.44f)
                quadraticTo(w * 0.5f, h * 0.38f, w * 0.16f, h * 0.44f)
                close()
            }
            drawPath(
                capPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF2E5CB8), Color(0xFF1E3A8A))
                )
            )

            val jacketPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.22f, h * 0.81f)
                lineTo(w * 0.12f, h * 0.98f)
                lineTo(w * 0.88f, h * 0.98f)
                lineTo(w * 0.78f, h * 0.81f)
                quadraticTo(w * 0.5f, h * 0.77f, w * 0.22f, h * 0.81f)
                close()
            }
            drawPath(
                jacketPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1E40AF), Color(0xFF1E3A8A))
                )
            )
        }
    }
}

@Composable
fun KidSpidermanIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFE0F2FE), Color(0xFF38BDF8))
                ),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawCircle(
                color = Color(0xFFEF4444).copy(alpha = 0.2f),
                radius = w * 0.46f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f)
            )

            val neckPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.43f, h * 0.70f)
                lineTo(w * 0.57f, h * 0.70f)
                lineTo(w * 0.57f, h * 0.85f)
                lineTo(w * 0.43f, h * 0.85f)
                close()
            }
            drawPath(neckPath, color = Color(0xFFDC2626))

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFEF4444), Color(0xFFB91C1C)),
                    center = androidx.compose.ui.geometry.Offset(w * 0.48f, h * 0.52f),
                    radius = w * 0.28f
                ),
                radius = w * 0.28f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.55f)
            )

            val leftEyeOuter = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.32f, h * 0.50f)
                quadraticTo(w * 0.42f, h * 0.44f, w * 0.47f, h * 0.55f)
                quadraticTo(w * 0.42f, h * 0.64f, w * 0.33f, h * 0.58f)
                close()
            }
            drawPath(leftEyeOuter, color = Color(0xFF0F172A))

            val leftEyeInner = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.34f, h * 0.51f)
                quadraticTo(w * 0.41f, h * 0.46f, w * 0.45f, h * 0.54f)
                quadraticTo(w * 0.41f, h * 0.61f, w * 0.35f, h * 0.57f)
                close()
            }
            drawPath(leftEyeInner, color = Color.White)

            val rightEyeOuter = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.68f, h * 0.50f)
                quadraticTo(w * 0.58f, h * 0.44f, w * 0.53f, h * 0.55f)
                quadraticTo(w * 0.58f, h * 0.64f, w * 0.67f, h * 0.58f)
                close()
            }
            drawPath(rightEyeOuter, color = Color(0xFF0F172A))

            val rightEyeInner = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.66f, h * 0.51f)
                quadraticTo(w * 0.59f, h * 0.46f, w * 0.55f, h * 0.54f)
                quadraticTo(w * 0.59f, h * 0.61f, w * 0.65f, h * 0.57f)
                close()
            }
            drawPath(rightEyeInner, color = Color.White)

            val suitPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.20f, h * 0.80f)
                lineTo(w * 0.08f, h * 0.98f)
                lineTo(w * 0.92f, h * 0.98f)
                lineTo(w * 0.80f, h * 0.80f)
                quadraticTo(w * 0.5f, h * 0.76f, w * 0.20f, h * 0.80f)
                close()
            }
            drawPath(
                suitPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1E3A8A), Color(0xFF1D4ED8))
                )
            )
        }
    }
}
