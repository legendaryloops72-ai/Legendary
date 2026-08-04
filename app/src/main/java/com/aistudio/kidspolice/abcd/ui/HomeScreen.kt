package com.aistudio.kidspolice.abcd.ui

import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.drawBehind
import androidx.compose.animation.core.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.aistudio.kidspolice.abcd.sound.CallSoundManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Image

// 👮 1. رسم الشرطي البطل الصغير مع تفاصيل واقعية وعيون برّاقة
@Composable
fun QuizChallengeIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFFCFAFF), Color(0xFFE9D5FF))
                ),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // أ. توهج بنفسجي سحري غامض محيط بالكتاب والمصباح
            drawCircle(
                color = Color(0xFFA855F7).copy(alpha = 0.25f),
                radius = w * 0.46f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f)
            )

            // ب. كتاب الحكمة والعلم المفتوح المستقر في الخلفية
            val bookShadowPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.14f, h * 0.50f)
                quadraticTo(w * 0.5f, h * 0.40f, w * 0.86f, h * 0.50f)
                lineTo(w * 0.86f, h * 0.86f)
                quadraticTo(w * 0.5f, h * 0.76f, w * 0.14f, h * 0.86f)
                close()
            }
            drawPath(bookShadowPath, color = Color(0xFF581C87)) // الغلاف الجلدي للكتاب الأرجواني

            // الصفحات المفتوحة مع ثنيات جميلة
            val leftPagePath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.50f, h * 0.81f)
                quadraticTo(w * 0.34f, h * 0.71f, w * 0.16f, h * 0.81f)
                lineTo(w * 0.16f, h * 0.51f)
                quadraticTo(w * 0.34f, h * 0.41f, w * 0.50f, h * 0.51f)
                close()
            }
            drawPath(leftPagePath, color = Color(0xFFFAF5FF))

            val rightPagePath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.50f, h * 0.81f)
                quadraticTo(w * 0.66f, h * 0.71f, w * 0.84f, h * 0.81f)
                lineTo(w * 0.84f, h * 0.51f)
                quadraticTo(w * 0.66f, h * 0.41f, w * 0.50f, h * 0.51f)
                close()
            }
            drawPath(rightPagePath, color = Color.White)

            // خطوط الكتابة الملطفة (المعرفة المكتوبة) لزيادة التفاصيل الواقعية
            drawLine(Color(0xFFE9D5FF), androidx.compose.ui.geometry.Offset(w * 0.22f, h * 0.58f), androidx.compose.ui.geometry.Offset(w * 0.42f, h * 0.55f), strokeWidth = 2.5f)
            drawLine(Color(0xFFE9D5FF), androidx.compose.ui.geometry.Offset(w * 0.22f, h * 0.65f), androidx.compose.ui.geometry.Offset(w * 0.42f, h * 0.62f), strokeWidth = 2.5f)
            drawLine(Color(0xFFE9D5FF), androidx.compose.ui.geometry.Offset(w * 0.22f, h * 0.72f), androidx.compose.ui.geometry.Offset(w * 0.38f, h * 0.69f), strokeWidth = 2.5f)

            drawLine(Color(0xFFE9D5FF), androidx.compose.ui.geometry.Offset(w * 0.58f, h * 0.55f), androidx.compose.ui.geometry.Offset(w * 0.78f, h * 0.58f), strokeWidth = 2.5f)
            drawLine(Color(0xFFE9D5FF), androidx.compose.ui.geometry.Offset(w * 0.58f, h * 0.62f), androidx.compose.ui.geometry.Offset(w * 0.78f, h * 0.65f), strokeWidth = 2.5f)
            drawLine(Color(0xFFE9D5FF), androidx.compose.ui.geometry.Offset(w * 0.58f, h * 0.69f), androidx.compose.ui.geometry.Offset(w * 0.74f, h * 0.72f), strokeWidth = 2.5f)

            // ج. مصباح فكرة متوهج ثلاثي الأبعاد وعالي الواقعية (فوق مركز الكتاب)
            // توهج خارجي لمصباح الفكرة (إشعاع ناعم)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFEF08A).copy(alpha = 0.8f), Color(0xFFFEF08A).copy(alpha = 0.0f)),
                    center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.32f),
                    radius = w * 0.28f
                ),
                radius = w * 0.28f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.32f)
            )

            // الهيكل الزجاجي للمصباح مع انعكاسات
            val bulbGlassPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.41f, h * 0.41f)
                cubicTo(w * 0.34f, h * 0.35f, w * 0.34f, h * 0.20f, w * 0.50f, h * 0.14f)
                cubicTo(w * 0.66f, h * 0.20f, w * 0.66f, h * 0.35f, w * 0.59f, h * 0.41f)
                lineTo(w * 0.57f, h * 0.46f)
                lineTo(w * 0.43f, h * 0.46f)
                close()
            }
            drawPath(
                bulbGlassPath,
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFFEF08A)),
                    center = androidx.compose.ui.geometry.Offset(w * 0.47f, h * 0.28f),
                    radius = w * 0.18f
                )
            )

            // سلك الفتيل المشتعل المشكل لقلة متألقة
            val filamentPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.47f, h * 0.38f)
                lineTo(w * 0.47f, h * 0.30f)
                quadraticTo(w * 0.50f, h * 0.23f, w * 0.50f, h * 0.23f)
                quadraticTo(w * 0.50f, h * 0.23f, w * 0.53f, h * 0.30f)
                lineTo(w * 0.53f, h * 0.38f)
            }
            drawPath(
                filamentPath,
                color = Color(0xFFEA580C),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
            drawCircle(color = Color.White, radius = w * 0.02f, center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.23f))

            // قاعدة السكرو اللولبية المعدنية الفضية التدرج
            drawRoundRect(
                brush = Brush.linearGradient(colors = listOf(Color(0xFFCBD5E1), Color(0xFF64748B))),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.43f, h * 0.46f),
                size = androidx.compose.ui.geometry.Size(w * 0.14f, h * 0.06f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )
            drawRoundRect(
                brush = Brush.linearGradient(colors = listOf(Color(0xFFCBD5E1), Color(0xFF64748B))),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.45f, h * 0.51f),
                size = androidx.compose.ui.geometry.Size(w * 0.10f, h * 0.04f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )
            drawCircle(color = Color(0xFF0F172A), radius = w * 0.02f, center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.56f))

            // د. جزيئات غبار سحرية متطايرة لامعة
            drawCircle(color = Color(0xFFFACC15), radius = w * 0.022f, center = androidx.compose.ui.geometry.Offset(w * 0.22f, h * 0.23f))
            drawCircle(color = Color(0xFFFACC15), radius = w * 0.015f, center = androidx.compose.ui.geometry.Offset(w * 0.78f, h * 0.28f))
        }
    }
}

// 🎯 5. رسم أيقونة كأس النجاح الذهبي (تحقيق المهام التربوية والالتزام الأخلاقي للأبطال)
@Composable
fun TasksIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF1F2), Color(0xFFFECDD3))
                ),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // أ. هالة نجية ذهبية متوهجة للاحتفال
            drawCircle(
                color = Color(0xFFFB7185).copy(alpha = 0.25f),
                radius = w * 0.46f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f)
            )

            // ب. أكاليل الغار الخضراء الدائرية المحيطة بالمنتصر
            drawArc(
                color = Color(0xFF10B981).copy(alpha = 0.6f),
                startAngle = 45f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.13f, h * 0.15f),
                size = androidx.compose.ui.geometry.Size(w * 0.74f, h * 0.65f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
            drawCircle(color = Color(0xFF059669), radius = w * 0.025f, center = androidx.compose.ui.geometry.Offset(w * 0.16f, h * 0.38f))
            drawCircle(color = Color(0xFF059669), radius = w * 0.025f, center = androidx.compose.ui.geometry.Offset(w * 0.18f, h * 0.52f))
            drawCircle(color = Color(0xFF059669), radius = w * 0.025f, center = androidx.compose.ui.geometry.Offset(w * 0.84f, h * 0.38f))
            drawCircle(color = Color(0xFF059669), radius = w * 0.025f, center = androidx.compose.ui.geometry.Offset(w * 0.82f, h * 0.52f))

            // ج. كأس المنتصر الذهبي ثلاثي الأبعاد اللامع
            // مقابض الكأس الأنيقة الجانبية (الأيسر والأيمن بلمعان 3D)
            drawArc(
                brush = Brush.linearGradient(colors = listOf(Color(0xFFFEF08A), Color(0xFFD97706))),
                startAngle = 100f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.22f, h * 0.28f),
                size = androidx.compose.ui.geometry.Size(w * 0.20f, h * 0.25f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5.5f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
            drawArc(
                brush = Brush.linearGradient(colors = listOf(Color(0xFFFEF08A), Color(0xFFD97706))),
                startAngle = 260f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.58f, h * 0.28f),
                size = androidx.compose.ui.geometry.Size(w * 0.20f, h * 0.25f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5.5f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            // هيكل الكأس الرئيسي المخروطي الفخم
            val chalicePath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.31f, h * 0.23f)
                lineTo(w * 0.69f, h * 0.23f)
                quadraticTo(w * 0.69f, h * 0.51f, w * 0.50f, h * 0.62f)
                quadraticTo(w * 0.31f, h * 0.51f, w * 0.31f, h * 0.23f)
                close()
            }
            drawPath(
                chalicePath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFF176), Color(0xFFFB8C00)),
                    start = androidx.compose.ui.geometry.Offset(w * 0.31f, h * 0.23f),
                    end = androidx.compose.ui.geometry.Offset(w * 0.69f, h * 0.62f)
                )
            )

            // شعاع لمعان طولي يعكس لمعان الإستيل والذهب الملكي
            val shineChalice = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.44f, h * 0.24f)
                lineTo(w * 0.48f, h * 0.24f)
                quadraticTo(w * 0.48f, h * 0.50f, w * 0.50f, h * 0.60f)
                quadraticTo(w * 0.44f, h * 0.50f, w * 0.44f, h * 0.24f)
                close()
            }
            drawPath(shineChalice, color = Color.White.copy(alpha = 0.3f))

            // النجمة الذهبية المحفورة بمنتصف الكأس
            val starPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.50f, h * 0.32f)
                lineTo(w * 0.53f, h * 0.38f)
                lineTo(w * 0.59f, h * 0.39f)
                lineTo(w * 0.54f, h * 0.43f)
                lineTo(w * 0.56f, h * 0.49f)
                lineTo(w * 0.50f, h * 0.45f)
                lineTo(w * 0.44f, h * 0.49f)
                lineTo(w * 0.46f, h * 0.43f)
                lineTo(w * 0.41f, h * 0.39f)
                lineTo(w * 0.47f, h * 0.38f)
                close()
            }
            drawPath(
                starPath,
                brush = Brush.linearGradient(colors = listOf(Color.White, Color(0xFFFEF3C7)))
            )

            // ذراع التثبيت للكأس الحلقي المنحني
            drawRect(
                brush = Brush.linearGradient(colors = listOf(Color(0xFFFDD835), Color(0xFFEF6C00))),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.45f, h * 0.62f),
                size = androidx.compose.ui.geometry.Size(w * 0.10f, h * 0.12f)
            )

            // قاعدة الكأس الرخامية الداكنة مع تفاصيل حجرية ممتازة عريضة
            drawRoundRect(
                brush = Brush.linearGradient(colors = listOf(Color(0xFF422006), Color(0xFF1F1003))),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.33f, h * 0.74f),
                size = androidx.compose.ui.geometry.Size(w * 0.34f, h * 0.13f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
            )
            // اللوحة الذهبية لاسم الفائز
            drawRoundRect(
                brush = Brush.linearGradient(colors = listOf(Color(0xFFFFEE58), Color(0xFFF57C00))),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.42f, h * 0.78f),
                size = androidx.compose.ui.geometry.Size(w * 0.16f, h * 0.05f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
            )
        }
    }
}

// 📖 6. رسم صندوق الكنوز والحكم التربوية للأبطال (صندوق كنز الحكايات المفتوح والمليء بالجواهر الباهظة)
@Composable
fun StoriesIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7))
                ),
                RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // أ. توهج سحري زمردي أخضر لإشعاع الأثر الصالح للقصص
            drawCircle(
                color = Color(0xFF22C55E).copy(alpha = 0.25f),
                radius = w * 0.46f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.5f)
            )

            // ب. بدن الصندوق الخشبي السفلي المتراص بالألواح وزوايا الذهب الحامية
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF78350F), Color(0xFF451A03)),
                    start = androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.55f),
                    end = androidx.compose.ui.geometry.Offset(w * 0.8f, h * 0.85f)
                ),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.20f, h * 0.55f),
                size = androidx.compose.ui.geometry.Size(w * 0.60f, h * 0.32f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
            )

            // حوافر الزوايا الذهبية الثقيلة (الأيسر والأيمن بلمعان 3D فخم)
            val leftGuard = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.20f, h * 0.76f)
                lineTo(w * 0.27f, h * 0.77f)
                lineTo(w * 0.27f, h * 0.87f)
                lineTo(w * 0.20f, h * 0.87f)
                close()
            }
            drawPath(leftGuard, brush = Brush.linearGradient(colors = listOf(Color(0xFFFDE047), Color(0xFFCA8A04))))

            val rightGuard = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.80f, h * 0.76f)
                lineTo(w * 0.73f, h * 0.77f)
                lineTo(w * 0.73f, h * 0.87f)
                lineTo(w * 0.80f, h * 0.87f)
                close()
            }
            drawPath(rightGuard, brush = Brush.linearGradient(colors = listOf(Color(0xFFFDE047), Color(0xFFCA8A04))))

            // قفل الصندوق الذهبي العتيق مع فتحة المفتاح الشهيرة
            drawRoundRect(
                brush = Brush.linearGradient(colors = listOf(Color(0xFFFFEA3F), Color(0xFFB45309))),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.44f, h * 0.52f),
                size = androidx.compose.ui.geometry.Size(w * 0.12f, h * 0.18f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )
            drawCircle(color = Color(0xFF0F172A), radius = w * 0.02f, center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.61f))
            drawLine(Color(0xFF0F172A), androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.61f), androidx.compose.ui.geometry.Offset(w * 0.50f, h * 0.68f), strokeWidth = 3f)

            // ج. شعاع المصباح والكنوز الداخلية المتلألئة المتمثلة بالياقوت والألماس المنبثقة من الداخل
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFEE58).copy(alpha = 0.9f), Color(0xFFFFEE58).copy(alpha = 0f)),
                    center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.45f),
                    radius = w * 0.18f
                ),
                radius = w * 0.18f,
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.45f)
            )

            // ياقوتة حمراء متلالئة (Red Diamond)
            val rubyPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.32f, h * 0.50f)
                lineTo(w * 0.38f, h * 0.43f)
                lineTo(w * 0.44f, h * 0.50f)
                lineTo(w * 0.38f, h * 0.57f)
                close()
            }
            drawPath(rubyPath, color = Color(0xFFEF4444))

            // جوهرة زرقاء نقية (Blue Sapphire)
            val sapphirePath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.56f, h * 0.50f)
                lineTo(w * 0.62f, h * 0.43f)
                lineTo(w * 0.68f, h * 0.50f)
                lineTo(w * 0.62f, h * 0.57f)
                close()
            }
            drawPath(sapphirePath, color = Color(0xFF3B82F6))

            // د. غطاء الصندوق الخشبي المقوس المفتوح للأعلى لتبرز النعم المعنوية للقصة
            val lidPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.20f, h * 0.41f)
                quadraticTo(w * 0.50f, h * 0.18f, w * 0.80f, h * 0.41f)
                lineTo(w * 0.78f, h * 0.47f)
                quadraticTo(w * 0.50f, h * 0.25f, w * 0.22f, h * 0.47f)
                close()
            }
            drawPath(
                lidPath,
                brush = Brush.linearGradient(colors = listOf(Color(0xFF9A3412), Color(0xFF5C1D02)))
            )

            // أربطة الصندوق المعدنية العريضة المقوسة
            drawArc(
                brush = Brush.linearGradient(colors = listOf(Color(0xFFFEF08A), Color(0xFFCA8A04))),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.25f, h * 0.28f),
                size = androidx.compose.ui.geometry.Size(w * 0.50f, h * 0.30f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
            )

            // نجوم سحرية براقة متطايرة (تجسيد القصص الجميلة للأطفال)
            drawCircle(color = Color(0xFFFBBF24), radius = w * 0.025f, center = androidx.compose.ui.geometry.Offset(w * 0.28f, h * 0.23f))
            drawCircle(color = Color(0xFFFBBF24), radius = w * 0.022f, center = androidx.compose.ui.geometry.Offset(w * 0.72f, h * 0.22f))
        }
    }
}

// 🛡️ شريط الضابط الترحيبي العريض (Top Header Character Banner) لحل طلب دمج صورة الكابتن الشرطي بدقة بالغة داخل التطبيق
@Composable
fun KidPoliceHeaderBanner(childName: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "banner_bounce")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "banner_float_y"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { translationY = floatOffset }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEFF6FF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFF8FAFC), Color(0xFFF0FDF4), Color(0xFFEFF6FF))
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // رسم الشرطي الكرتوني البطل المطبق للصورة المرفقة بحجم عريض وبارز
            KidPoliceIllustration(
                modifier = Modifier
                    .size(95.dp)
                    .clip(RoundedCornerShape(22.dp))
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End // محاذاة لليمين دعماً للغة العربية الكريمة
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFDBEAFE), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "تفاعل رسمي تربوي 🛡️",
                        color = Color(0xFF1E40AF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "صديقك المخلص كابتن الشرطة البطل:",
                    fontSize = 13.sp,
                    color = Color(0xFF1E3A8A),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Right
                )
                Text(
                    "أهلاً بك يا بطلنا الصغير $childName! أنا شرطي الأطفال الطيب، أدعوك لإنجاز مهامك اليومية واجتياز اختبارات الذكاء لكي تصبح بطلاً مثالياً ونظيفاً ونفخر بك دائماً! 🥰✨",
                    fontSize = 11.5.sp,
                    color = Color(0xFF334155),
                    lineHeight = 17.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToQuizzes: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSimulateCall: (String) -> Unit,
    onNavigateToHeroesUniverse: () -> Unit,
    onNavigateToPoliceCars: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToColoring: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToStories: () -> Unit,
    onNavigateToPoliceScenarios: () -> Unit,
    onNavigateToCallHub: () -> Unit,
    onNavigateToRewards: () -> Unit
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    var showStoryDialog by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val context = LocalContext.current
    val soundManager = remember { CallSoundManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
        }
    }
    
    // Drawer setup
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Core custom kid animations for 3D UI
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundAtmosphere")
    val twinkleScale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Twinkle"
    )
    val cloudDrift by infiniteTransition.animateFloat(
        initialValue = -18f,
        targetValue = 18f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CloudDrift"
    )
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WaveOffset"
    )

    val childName = profile?.name ?: "علي"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF1E1B4B), // Premium cosmos background
                modifier = Modifier.width(310.dp)
            ) {
                // Beautiful Arabic-styled kid profile header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF2E1065), Color(0xFF1E1B4B))
                            )
                        )
                        .padding(top = 40.dp, bottom = 24.dp)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🧒", fontSize = 40.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "البطل $childName",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${profile?.totalStars ?: 27}",
                            color = Color(0xFFFFDE59),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text("⭐", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "الرصيد الذهبي",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val itemModifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                
                // 1. الرئيسية
                NavigationDrawerItem(
                    icon = { Text("🏡", fontSize = 20.sp) },
                    label = { Text("الصفحة الرئيسية", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // تلوين الأطفال الإبداعي
                NavigationDrawerItem(
                    icon = { Text("🎨", fontSize = 20.sp) },
                    label = { Text("عالم التلوين الإبداعي", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToColoring()
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // قصص الحكمة والأخلاق
                NavigationDrawerItem(
                    icon = { Text("📖", fontSize = 20.sp) },
                    label = { Text("قصص الحكمة والأخلاق", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToStories()
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // ألعاب ديدي التعليمية
                NavigationDrawerItem(
                    icon = { Text("🎮", fontSize = 20.sp) },
                    label = { Text("عالم الألعاب التعليمية", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToGames()
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // المفضلة
                NavigationDrawerItem(
                    icon = { Text("⭐", fontSize = 20.sp) },
                    label = { Text("لوحاتي المفضلة", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToColoring()
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // الرسمات الأخيرة
                NavigationDrawerItem(
                    icon = { Text("🕒", fontSize = 20.sp) },
                    label = { Text("رسماتي الأخيرة", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToColoring()
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // 2. المهام (Missions)
                NavigationDrawerItem(
                    icon = { Text("🏆", fontSize = 20.sp) },
                    label = { Text("مهامي والمسؤوليات", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToTasks()
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // 2.5 الجوائز (Rewards)
                NavigationDrawerItem(
                    icon = { Text("🎖️", fontSize = 20.sp) },
                    label = { Text("لوحة جوائزي", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToRewards()
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // 3. إطار الشرطة
                NavigationDrawerItem(
                    icon = { Text("🚓", fontSize = 20.sp) },
                    label = { Text("معرض سيارات الشرطة", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToPoliceCars()
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // 4. الأبطال الخارقين (Superheroes)
                NavigationDrawerItem(
                    icon = { Text("🦸", fontSize = 20.sp) },
                    label = { Text("اختبارات صور الأبطال", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToHeroesUniverse()
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // 5. سياسة الخصوصية (Privacy Policy)
                NavigationDrawerItem(
                    icon = { Text("📜", fontSize = 20.sp) },
                    label = { Text("سياسة الخصوصية", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToPrivacy()
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // 6. مشاركة التطبيق (Share App)
                NavigationDrawerItem(
                    icon = { Text("📤", fontSize = 20.sp) },
                    label = { Text("مشاركة التطبيق", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, "حمل تطبيق شرطة الأطفال الآن واستمتع! https://play.google.com/store/apps/details?id=com.aistudio.kidspolice.abcd")
                            type = "text/plain"
                        }
                        val shareIntent = android.content.Intent.createChooser(sendIntent, "مشاركة عبر")
                        context.startActivity(shareIntent)
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // 7. تقييم التطبيق (Rate App)
                NavigationDrawerItem(
                    icon = { Text("⭐", fontSize = 20.sp) },
                    label = { Text("تقييم التطبيق", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        val uri = android.net.Uri.parse("market://details?id=com.aistudio.kidspolice.abcd")
                        val goToMarket = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(goToMarket)
                        } catch (e: android.content.ActivityNotFoundException) {
                            context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.aistudio.kidspolice.abcd")))
                        }
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )

                // 8. الإعدادات (Settings)
                NavigationDrawerItem(
                    icon = { Text("⚙️", fontSize = 20.sp) },
                    label = { Text("غرفة التحكم والوالدين", color = Color.White, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToSettings()
                    },
                    modifier = itemModifier,
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
                }
                
                Text(
                    text = "شرطة الأطفال وأبطال الغد 👮✨",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {}
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .drawBehind {
                        val w = size.width
                        val h = size.height
                        
                        // Premium deep space cosmic gradient background
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF030C24), // Deep Space Midnight Blue
                                    Color(0xFF08102A),
                                    Color(0xFF030C24)
                                )
                            )
                        )

                        // Helper to draw modern 4-corner shine sparkle stars
                        fun drawSparkle(cx: Float, cy: Float, size: Float, alpha: Float) {
                            val path = Path().apply {
                                moveTo(cx, cy - size)
                                quadraticTo(cx, cy, cx + size, cy)
                                quadraticTo(cx, cy, cx + size, cy)
                                quadraticTo(cx, cy, cx, cy + size)
                                quadraticTo(cx, cy, cx - size, cy)
                                quadraticTo(cx, cy, cx, cy - size)
                                close()
                            }
                            drawPath(path, color = Color(0xFFFBBF24).copy(alpha = alpha))
                        }

                        // 1. Draw glowing twinkling stars at safe, spread out spots
                        drawSparkle(w * 0.12f, h * 0.12f, 15f * twinkleScale, 0.82f)
                        drawSparkle(w * 0.88f, h * 0.08f, 20f * (2f - twinkleScale), 0.75f)
                        drawSparkle(w * 0.78f, h * 0.35f, 12f * twinkleScale, 0.90f)
                        drawSparkle(w * 0.06f, h * 0.48f, 18f * (1.5f - twinkleScale), 0.70f)
                        drawSparkle(w * 0.92f, h * 0.65f, 22f * twinkleScale, 0.85f)
                        drawSparkle(w * 0.15f, h * 0.85f, 14f * (2f - twinkleScale), 0.80f)

                        // 2. Draw subtle cosmic dust / nebula effect instead of clouds for space theme
                        fun drawNebula(cx: Float, cy: Float, scale: Float) {
                            val nebulaColor = Color(0xFF38BDF8).copy(alpha = 0.15f)
                            drawCircle(nebulaColor, radius = 120f * scale, center = Offset(cx, cy))
                        }
                        
                        drawNebula(w * 0.22f + cloudDrift, h * 0.25f, 1.2f)
                        drawNebula(w * 0.75f - cloudDrift, h * 0.75f, 1.5f)
                    }
            ) {
                // ------------------ Pinned Top Custom Notification/Greeting Bar ------------------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Menu slide button on left (Tactile 3D Soft UI styling with gentle scale feedback)
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .scale(1.03f)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.White)
                            .clickable {
                                scope.launch { drawerState.open() }
                            }
                            .border(androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFEFF6FF)), androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "عرض القائمة",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Points Badge on right: glowing white rounded capsule with soft gold outline and twinkling pulse scale!
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .scale(twinkleScale)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.White)
                            .border(androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFBBF24).copy(alpha = 0.6f)), RoundedCornerShape(22.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${profile?.totalStars ?: 27}",
                            color = Color(0xFFD97706),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("⭐", fontSize = 14.sp)
                    }
                }

                // Big greeting row containing giant waving character on left, text on right
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 14.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left side: Waving friendly character illustration with custom floating up-and-down waveOffset
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(y = waveOffset.dp)
                                .scale(1.02f)
                        ) {
                            Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.aistudio.kidspolice.abcd.R.drawable.kids_police_splash_1783935509010),
                                contentDescription = "Kids Police Superhero",
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .border(androidx.compose.foundation.BorderStroke(3.dp, Color.White), CircleShape),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                    }

                    // Right side: "شرطة الأطفال" bold branding texts with gorgeous Arabic shadow layout
                    Column(
                        modifier = Modifier.weight(1.5f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            "شرطة الأطفال",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E3A8A),
                            lineHeight = 36.sp,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = Shadow(
                                    color = Color(0xFF1E3A8A).copy(alpha = 0.15f),
                                    offset = Offset(2f, 2f),
                                    blurRadius = 4f
                                )
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "👋 أهلاً بك، $childName!",
                            fontSize = 15.sp,
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Main Kids Dashboard Content (2x2 Grid)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // 1. Sounds Icon
                            KidsMainCard(
                                title = "معرض سيارات الشرطة",
                                subtitle = "تعرف على مركبات الشرطة",
                                imageRes = com.aistudio.kidspolice.abcd.R.drawable.img_dash_sounds_1783955804158,
                                backgroundColor = Color(0xFFFDF2F8),
                                accentColor = Color(0xFFDB2777),
                                modifier = Modifier.weight(1f)
                            ) {
                                soundManager.playSynthSound("funny")
                                onNavigateToPoliceCars()
                            }

                            // 2. Superheroes Icon
                            KidsMainCard(
                                title = "الأبطال الخارقون",
                                subtitle = "عالم الأبطال المذهل",
                                imageRes = com.aistudio.kidspolice.abcd.R.drawable.img_dash_heroes_1783955818726,
                                backgroundColor = Color(0xFFFFF7ED),
                                accentColor = Color(0xFFEA580C),
                                modifier = Modifier.weight(1f)
                            ) {
                                soundManager.playSynthSound("funny")
                                onNavigateToHeroesUniverse()
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // 3. Calls Icon
                            KidsMainCard(
                                title = "المكالمات",
                                subtitle = "تحدث مع أصدقائك الموجهين",
                                imageRes = com.aistudio.kidspolice.abcd.R.drawable.img_dash_calls_1783955830382,
                                backgroundColor = Color(0xFFF0FDF4),
                                accentColor = Color(0xFF16A34A),
                                modifier = Modifier.weight(1f)
                            ) {
                                soundManager.playSynthSound("funny")
                                onNavigateToCallHub()
                            }

                            // 4. Games & Fun Icon
                            KidsMainCard(
                                title = "الألعاب والمرح",
                                subtitle = "ألعاب، اختبارات وتلوين",
                                imageRes = com.aistudio.kidspolice.abcd.R.drawable.img_dash_games_1783955841991,
                                backgroundColor = Color(0xFFEFF6FF),
                                accentColor = Color(0xFF2563EB),
                                modifier = Modifier.weight(1f)
                            ) {
                                soundManager.playSynthSound("funny")
                                onNavigateToGames()
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // 5. Stories Icon
                            KidsMainCard(
                                title = "قصص ممتعة",
                                subtitle = "قصص تعليمية وتفاعلية",
                                imageRes = com.aistudio.kidspolice.abcd.R.drawable.dash_stories_icon_1784050441881,
                                backgroundColor = Color(0xFFF0F9FF),
                                accentColor = Color(0xFF0284C7),
                                modifier = Modifier.weight(1f)
                            ) {
                                soundManager.playSynthSound("funny")
                                onNavigateToStories()
                            }

                            // 6. Tasks Icon
                            KidsMainCard(
                                title = "مهامي",
                                subtitle = "جدول المهام اليومية للأبطال",
                                imageRes = com.aistudio.kidspolice.abcd.R.drawable.img_dash_heroes_1783955818726, // Using heroes as fallback for now
                                backgroundColor = Color(0xFFFEF2F2),
                                accentColor = Color(0xFFDC2626),
                                modifier = Modifier.weight(1f)
                            ) {
                                soundManager.playSynthSound("funny")
                                onNavigateToTasks()
                            }
                        }
                    }
                }
                
                // AdMob Banner Ad
                AdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun KidsMainCard(
    title: String,
    subtitle: String,
    imageRes: Int,
    backgroundColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Bounce/Scale animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "ScaleAnimation"
    )

    Card(
        modifier = modifier
            .aspectRatio(0.85f)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 2.dp else 12.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, backgroundColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(backgroundColor.copy(alpha = 0.3f), Color.White)
                    )
                )
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                
                // Subtle Gloss Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent, Color.Black.copy(alpha = 0.05f))
                            )
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = accentColor,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun GridActionCard(
    title: String,
    subTitle: String,
    topSectionBackgroundColor: Color,
    accentColor: Color,
    hasCheckmark: Boolean = false,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "grid_card_press_scale"
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp,
        animationSpec = tween(durationMillis = 100),
        label = "grid_card_press_elevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(3.dp, accentColor.copy(alpha = 0.28f)),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Section (colored background, star, checkmark)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(topSectionBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                // Main Illustration icon
                Box(
                    modifier = Modifier.size(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                // Top right star pill (unlabelled white star circular indicator)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(24.dp)
                        .background(Color.White, androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⭐", fontSize = 11.sp)
                }

                // If checkmark is required (e.g., for the tasks card, it has a checked badge)
                if (hasCheckmark) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(10.dp)
                            .size(24.dp)
                            .background(Color(0xFF22C55E), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Bottom Section (White rounded panel with Arabic texts and small right arrow indicator)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Arrow indicator in small circular matching accent border or background
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(accentColor.copy(alpha = 0.15f), androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack, // Standard back/forward mirrors correctly in RTL formats
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Text labels on right side (RTL)
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = subTitle,
                        fontSize = 10.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CallAvatarCard(
    name: String,
    desc: String,
    accentColor: Color,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "avatar_card_press_scale"
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) 1.5.dp else 5.dp,
        animationSpec = tween(durationMillis = 100),
        label = "avatar_card_press_elevation"
    )

    Card(
        modifier = Modifier
            .width(115.dp)
            .scale(animatedScale)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(3.dp, accentColor.copy(alpha = 0.28f)),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                icon()
                // Small green calling phone badge
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(Color(0xFF10B981), androidx.compose.foundation.shape.CircleShape)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Call,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center
            )
            Text(
                text = desc,
                fontSize = 9.5.sp,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

// 👮 1.5. رسم شرطي الأطفال العراقي التفاعلي ذو النظارات الشمسية السوداء والخلفية الخضراء الساطعة تزامناً مع فيديو شرطة الأطفال العراقية 100%
@Composable
fun SoftNavigationTabItem(
    selected: Boolean,
    label: String,
    iconColor: Color,
    animIcon: String,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "TabScale"
    )
    val glowColor = iconColor.copy(alpha = 0.14f)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.CenterHorizontally)
                .scale(scale)
                .size(if (selected) 44.dp else 38.dp)
                .background(
                    if (selected) glowColor else Color.Transparent,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = animIcon,
                fontSize = if (selected) 24.sp else 21.sp
            )
        }
        Spacer(modifier = Modifier.height(3.dp).align(androidx.compose.ui.Alignment.CenterHorizontally))
        Text(
            text = label,
            fontSize = if (selected) 11.5.sp else 10.sp,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (selected) iconColor else Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun HeroicTaskThumbnail(emoji: String, title: String, difficulty: Int, color: Color) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // "Real-life" photo placeholder (emoji with background)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 48.sp)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(3) { index ->
                    Text(
                        if (index < difficulty) "⭐" else "☆",
                        fontSize = 10.sp
                    )
                }
            }
            
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = color),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("ابدأ الاختبار", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MiniSoundThumbnail(emoji: String, bgColor: Color) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(bgColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .border(1.dp, bgColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Simple representation of the realistic object
        Text(emoji, fontSize = 20.sp)
        
        // Play icon overlay
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 4.dp, y = 4.dp)
                .size(16.dp)
                .background(Color.White, CircleShape)
                .border(0.5.dp, Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Play",
                tint = Color(0xFF0CA678), // Green healthy play color
                modifier = Modifier.size(12.dp)
            )
        }
    }
}




