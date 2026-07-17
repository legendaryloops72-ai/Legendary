package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

import java.util.Calendar

data class KidQuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val rewardStars: Int = 5
)

// دالة لجلب أسئلة مخصصة وبناءً على يوم الأسبوع الحالي للأبطال لضمان التنوع اليومي
fun getQuestionsForToday(): List<KidQuizQuestion> {
    val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    return when (day) {
        Calendar.SATURDAY -> listOf(
            KidQuizQuestion(
                id = 101,
                question = "ماذا نفعل بفرشاة الأسنان الجميلة قبل النوم وكل صباح؟ 🪥",
                options = listOf("ننظف بها أسناننا بلطف لنحميها من التسوس والآلام", "نلعب بها في الحمام كسيارة كرتونية مضحكة"),
                correctIndex = 0
            ),
            KidQuizQuestion(
                id = 102,
                question = "كيف غسيل اليدين الصحيح والصحي لقتل الجراثيم؟ 🧼",
                options = listOf("نمسحها بالملابس على السريع دون ماء", "بالماء النظيف والصابون مع الفرك الجيد لـ 20 ثانية"),
                correctIndex = 1
            ),
            KidQuizQuestion(
                id = 103,
                question = "متى نقوم بقص أظافرنا لكي نبقى أنيقين؟ 💅",
                options = listOf("عندما تطول لتبقى نظيفة وخالية من الميكروبات والأتربة", "نتركها طويلة جداً لنخيف بها أصدقائنا"),
                correctIndex = 0
            )
        )
        Calendar.SUNDAY -> listOf(
            KidQuizQuestion(
                id = 201,
                question = "ما هو التصرف البطل بعد الانتهاء من اللعب بألعابنا الرائعة؟ 🧸",
                options = listOf("نترك الألعاب ملقاة على الأرض في كل مكان", "نجمعها بلطف ونرتبها في صندوق الألعاب المخصص لها"),
                correctIndex = 1
            ),
            KidQuizQuestion(
                id = 202,
                question = "كيف نساعد ماما وبابا في غرفتنا اليوم؟ 🛌",
                options = listOf("نرتب فراشنا وسريرنا الصباحي بنشاط وسرور", "نحدث فوضى جديدة ونرمي الوسائد على الأرض"),
                correctIndex = 0
            ),
            KidQuizQuestion(
                id = 203,
                question = "إذا رأينا منديلاً أو ورقة ملقاة على الأرض في الصالة، ماذا نفعل؟ 🗑️",
                options = listOf("نلتقطها فوراً ونرميها في سلة المهملات لنحافظ على نظافة بيتنا", "نتجاهلها ونمشي فوقها دون اهتمام"),
                correctIndex = 0
            )
        )
        Calendar.MONDAY -> listOf(
            KidQuizQuestion(
                id = 301,
                question = "هل يجوز لنا اللعب بمقابس الكهرباء أو كبريت المطبخ الخطير؟ ⚡",
                options = listOf("نعم، الكهرباء والنار مسلية جداً للمغامرة", "لا، خطرة جداً وقد تسبب لنا الأذى الشديد والحروق"),
                correctIndex = 1
            ),
            KidQuizQuestion(
                id = 302,
                question = "إذا رن جرس الباب الخارجي وماما وبابا غير موجودين بجانبي، ماذا أفعل؟ 🚪",
                options = listOf("أنادي والديّ فوراً ولا أفتح الباب أبداً بمفردي لغريب", "أفتح الباب فوراً وأرحب بأي شخص غريب بالخارج"),
                correctIndex = 0
            ),
            KidQuizQuestion(
                id = 303,
                question = "ما هو التصرف الصحيح إذا رأينا ماء مسكوباً على الأرض في المطبخ؟ 💧",
                options = listOf("نمسحه بسرعة بمنديل أو نطلب مساعدة الكبار لكي لا يتزحلق أحد", "نتجاهل الماء ونلعب بجانبه"),
                correctIndex = 0
            )
        )
        Calendar.TUESDAY -> listOf(
            KidQuizQuestion(
                id = 401,
                question = "ما هو السلوك الأجمل والمهذب عندما يطلب منا الوالدان طلباً؟ ❤️",
                options = listOf("نقول 'حاضر بكل حب' ونطيعهم بابتسامة فخورة", "نتجاهل كلامهم ونستمر في اللعب بالهاتف وتجاهلهم"),
                correctIndex = 0
            ),
            KidQuizQuestion(
                id = 402,
                question = "عندما يعطينا أحد الأقارب أو الأصدقاء هدية أو حلوى جميلة، ماذا نقول له؟ 🎁",
                options = listOf("نبتسم بلطف ونقول 'شكراً جزيلاً لك وجزاك الله خيراً'", "نأخذها بقوة ونجري بسرعة دون كلام"),
                correctIndex = 0
            ),
            KidQuizQuestion(
                id = 403,
                question = "إذا شعرنا بالضيق أو الغضب من شيء ما، كيف نعبر عن ذلك؟ 🥺",
                options = listOf("نتكلم بهدوء ونشرح لوالدينا ما يضايقنا لكي يساعدونا", "نصرخ بصوت مرتفع ونقوم برمي الأشياء على الأرض لكي يسمعونا"),
                correctIndex = 0
            )
        )
        Calendar.WEDNESDAY -> listOf(
            KidQuizQuestion(
                id = 501,
                question = "عند عبور الطريق الجميل مع الأهل، ماذا يجب أن نفعل لسلامتنا؟ 🚶",
                options = listOf("نمسك يد بابا أو ماما بقوة ونعبر من خط المشاة بأمان", "نجري بمفردنا وبسرعة فائقة دون أن ننظر للسيارات"),
                correctIndex = 0
            ),
            KidQuizQuestion(
                id = 502,
                question = "أين هو المكان الآمن والمناسب تماماً للجري واللعب بالكرة؟ ⚽",
                options = listOf("الحدائق العامة والملاعب الرياضية المخصصة والمحمية للأطفال", "وسط الشارع الخارجي المليء بالسيارات المارة السريعة"),
                correctIndex = 1
            ),
            KidQuizQuestion(
                id = 503,
                question = "لماذا يجب علينا دائماً ربط حزام الأمان في السيارة؟ 🚗",
                options = listOf("لحمايتنا وسلامتنا في كل رحلة جميلة نقوم بها", "لأن المقاعد تصبح ضيقة بدون الحزام"),
                correctIndex = 0
            )
        )
        Calendar.THURSDAY -> listOf(
            KidQuizQuestion(
                id = 601,
                question = "ماذا نفعل عندما يشرح المعلم أو المعلمة الدرس داخل الفصل؟ 🏫",
                options = listOf("نستمع بتركيز وهدوء تام لكي نفهم ونصبح من العباقرة الممتازين", "نلعب بالأقلام والطلّاسة وندردش مع زملائنا"),
                correctIndex = 0
            ),
            KidQuizQuestion(
                id = 602,
                question = "كيف نحافظ على كتبنا المدرسية ودفاترنا الجميلة من التلف؟ 📚",
                options = listOf("نحافظ عليها مغلفة ونظيفة ونضعها في الحقيبة المدرسية برفق", "نقطع أوراقها ونرسم عليها وجوهاً مضحكة ونرميها في الفصل"),
                correctIndex = 0
            ),
            KidQuizQuestion(
                id = 603,
                question = "ماذا تفعل إذا واجهت سؤالاً أو مسألة صعبة في واجبك المدرسي؟ ✍️",
                options = listOf("أحاول التفكير فيها مجدداً، أو أطلب المساعدة من والديّ بكل احترام", "أبكي وأترك الواجب بدون أي حل أو محاولة"),
                correctIndex = 0
            )
        )
        Calendar.FRIDAY -> listOf(
            KidQuizQuestion(
                id = 701,
                question = "إذا رأيت رجلاً مسناً أو سيدة تحمل أغراضاً ثقيلة في طريقك، كيف تتصرف؟ 🤝",
                options = listOf("أبتسم برفق وأبادر بمساعدتهم أو أطلب من كبار السن الآخرين المساعدة", "أضحك وأجري بجانبهم متجاهلاً تعبهم وسنهم الكبير"),
                correctIndex = 0
            ),
            KidQuizQuestion(
                id = 702,
                question = "عندما ننتهي من وجبة طعامنا اللذيذة والمغذية، ماذا نقول لربنا؟ 🍽️",
                options = listOf("نقول 'الحمد لله رب العالمين' شاكرين الله على النعمة العظيمة", "ننهض بسرعة من المائدة وننسى شكر الله وماما على الطعام"),
                correctIndex = 0
            ),
            KidQuizQuestion(
                id = 703,
                question = "كيف يجب أن نتعامل مع الحيوانات الأليفة كالقطط والطيور الصغيرة في الشارع؟ 🐈‍⬛",
                options = listOf("برفق وعطف شديد ونقدم لها الماء والطعام ولا نؤذيها أبداً", "نطاردها بالأحجار والعصي ونخيفها لكي تهرب منا"),
                correctIndex = 0
            )
        )
        else -> listOf(
            KidQuizQuestion(
                id = 801,
                question = "ماذا نفعل بفرشاة الأسنان الجميلة قبل النوم وكل صباح؟ 🪥",
                options = listOf("ننظف بها أسناننا بلطف لنحميها من التسوس والآلام", "نلعب بها في الحمام كسيارة كرتونية مضحكة"),
                correctIndex = 0
            ),
            KidQuizQuestion(
                id = 802,
                question = "ما هو التصرف البطل بعد الانتهاء من اللعب بألعابنا الرائعة؟ 🧸",
                options = listOf("نجمعها بلطف ونرتبها في الصندوق المخصص لها لكي يبدو بيتنا جميلاً", "نترك الألعاب ملقاة على الأرض في كل مكان ونذهب للنوم"),
                correctIndex = 0
            )
        )
    }
}

@Composable
fun ModernQuestionHeader(question: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // High-contrast question mark container flanked by beautiful fluffy custom styled clouds
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Left Cloud
            androidx.compose.foundation.Canvas(modifier = Modifier.size(width = 45.dp, height = 28.dp)) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    val w = size.width
                    val h = size.height
                    moveTo(w * 0.2f, h * 0.8f)
                    quadraticTo(w * 0.05f, h * 0.8f, w * 0.05f, h * 0.6f)
                    quadraticTo(w * 0.05f, h * 0.4f, w * 0.25f, h * 0.4f)
                    quadraticTo(w * 0.35f, h * 0.1f, w * 0.6f, h * 0.15f)
                    quadraticTo(w * 0.9f, h * 0.15f, w * 0.9f, h * 0.45f)
                    quadraticTo(w * 0.98f, h * 0.65f, w * 0.8f, h * 0.8f)
                    close()
                }
                drawPath(path, color = Color(0xFFEFF6FF))
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Main Question Indicator Circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF60A5FA), Color(0xFF2563EB))
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                    .padding(2.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.2f), androidx.compose.foundation.shape.CircleShape)
                ) {
                    Text(
                        text = "؟",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Cloud
            androidx.compose.foundation.Canvas(modifier = Modifier.size(width = 45.dp, height = 28.dp)) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    val w = size.width
                    val h = size.height
                    moveTo(w * 0.2f, h * 0.8f)
                    quadraticTo(w * 0.05f, h * 0.8f, w * 0.05f, h * 0.6f)
                    quadraticTo(w * 0.05f, h * 0.4f, w * 0.25f, h * 0.4f)
                    quadraticTo(w * 0.35f, h * 0.1f, w * 0.6f, h * 0.15f)
                    quadraticTo(w * 0.9f, h * 0.15f, w * 0.9f, h * 0.45f)
                    quadraticTo(w * 0.98f, h * 0.65f, w * 0.8f, h * 0.8f)
                    close()
                }
                drawPath(path, color = Color(0xFFEFF6FF))
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = question,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 26.sp
            ),
            textAlign = TextAlign.Center,
            color = Color(0xFF1E293B)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val questions = remember { getQuestionsForToday() }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var earnMessage by remember { mutableStateOf("") }
    
    val currentQuestion = questions[currentQuestionIndex]

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                // Twilight dark sky starry gradient background with elegant deep color tones
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF311060),
                            Color(0xFF4C1D95),
                            Color(0xFF1E1B4B)
                        )
                    )
                )
                .drawBehind {
                    // Draw decorative cute small glowing stars in the sky background
                    val starPositions = listOf(
                        Pair(0.12f, 0.08f), Pair(0.85f, 0.05f), Pair(0.35f, 0.14f),
                        Pair(0.72f, 0.18f), Pair(0.08f, 0.25f), Pair(0.92f, 0.35f),
                        Pair(0.24f, 0.42f), Pair(0.81f, 0.48f)
                    )
                    starPositions.forEach { (pctX, pctY) ->
                        drawCircle(
                            color = Color(0xFFFDE047).copy(alpha = 0.5f),
                            radius = 6f,
                            center = androidx.compose.ui.geometry.Offset(size.width * pctX, size.height * pctY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3f,
                            center = androidx.compose.ui.geometry.Offset(size.width * pctX, size.height * pctY)
                        )
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Pinned Custom Header with white round back arrow button & star badge points
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Circular back arrow button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.White)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = "Back",
                            tint = Color(0xFF4C1D95),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Stepper Question title
                    Text(
                        text = "السؤال ${currentQuestionIndex + 1} من ${questions.size}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    // Top Right Golden Happy Star Score Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⭐",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "أبطال",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // 2. Beautiful stepper indicator timeline dots (stepper line linkage)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    questions.forEachIndexed { qIdx, _ ->
                        val isDone = qIdx <= currentQuestionIndex
                        val isCurrent = qIdx == currentQuestionIndex
                        
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(
                                    if (isCurrent) Color(0xFF22C55E)
                                    else if (isDone) Color(0xFF10B981)
                                    else Color.White.copy(alpha = 0.25f)
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isCurrent) Color.White else Color.Transparent,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${qIdx + 1}",
                                color = if (isDone || isCurrent) Color.White else Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (qIdx < questions.size - 1) {
                            Spacer(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(3.dp)
                                    .background(
                                        if (qIdx < currentQuestionIndex) Color(0xFF10B981)
                                        else Color.White.copy(alpha = 0.2f)
                                    )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Question Card Box (super curved edges, 26dp)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    ModernQuestionHeader(currentQuestion.question)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 4. Stylized answers list with cartoon bubble tags
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    currentQuestion.options.forEachIndexed { index, option ->
                        val isSelected = selectedOptionIndex == index
                        val isCorrect = currentQuestion.correctIndex == index

                        val backgroundColor = when {
                            !hasAnswered -> {
                                if (isSelected) Color(0xFFEFF6FF) else Color.White
                            }
                            else -> {
                                if (isCorrect) Color(0xFFDCFCE7) // Lush feedback correct emerald green
                                else if (isSelected) Color(0xFFFEE2E2) // Incorrect red
                                else Color.White
                            }
                        }

                        val borderColor = when {
                            !hasAnswered -> if (isSelected) Color(0xFF3B82F6) else Color.White.copy(alpha = 0.08f)
                            else -> if (isCorrect) Color(0xFF10B981) else if (isSelected) Color(0xFFEF4444) else Color.White.copy(alpha = 0.08f)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !hasAnswered) {
                                    selectedOptionIndex = index
                                },
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = backgroundColor),
                            border = androidx.compose.foundation.BorderStroke(2.2.dp, borderColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Right Check Indicators
                                if (hasAnswered && isCorrect) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Correct answer check",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else if (hasAnswered && isSelected && !isCorrect) {
                                    Text("❌", fontSize = 14.sp)
                                } else {
                                    // Custom circle selection pill bullet to style left element elegantly
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                            .background(if (isSelected) Color(0xFF3B82F6) else Color(0xFFF1F5F9)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                                    .background(Color.White)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Real child option text description mapped on left side
                                Text(
                                    text = option,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E293B),
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.weight(1f)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                // Bullet category icon (Index mapping)
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            when (index) {
                                                0 -> Color(0xFFFEF3C7)
                                                1 -> Color(0xFFEFF6FF)
                                                2 -> Color(0xFFECFDF5)
                                                else -> Color(0xFFFDF2F8)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (index) {
                                            0 -> "💡"
                                            1 -> "🌟"
                                            2 -> "🧸"
                                            else -> "🪥"
                                        },
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. Immersive custom feedback text with cute waving cop illustration & main action button
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (hasAnswered) {
                        Text(
                            text = earnMessage,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFDE047),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(82.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // Action control button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB))
                                    )
                                )
                                .clickable(
                                    enabled = hasAnswered || (selectedOptionIndex != null),
                                    onClick = {
                                        if (hasAnswered) {
                                            if (currentQuestionIndex < questions.size - 1) {
                                                currentQuestionIndex++
                                                selectedOptionIndex = null
                                                hasAnswered = false
                                                earnMessage = ""
                                            } else {
                                                onBack()
                                            }
                                        } else {
                                            selectedOptionIndex?.let { selectedIdx ->
                                                hasAnswered = true
                                                val correct = selectedIdx == currentQuestion.correctIndex
                                                if (correct) {
                                                    earnMessage = "إجابة صحيحة ورائعة جداً! 🎉 حصلتي على ${currentQuestion.rewardStars} نجوم!"
                                                    viewModel.awardQuizStars(currentQuestion.rewardStars)
                                                } else {
                                                    earnMessage = "أوه! حاولي مرة أخرى يا بطلة. 🥰"
                                                }
                                            }
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (hasAnswered) {
                                        if (currentQuestionIndex < questions.size - 1) "السؤال التالي 🌟" else "إ كمال الاختبار ونيل الجوائز 🏆"
                                    } else {
                                        "تحقق من الإجابة 👍"
                                    },
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Overlapping friendly waving kid police cop illustration on bottom-left corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .offset(x = (-8).dp, y = 14.dp)
                                .size(95.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            KidPoliceIllustration(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}
