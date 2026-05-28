package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
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

data class KidQuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val rewardStars: Int = 5
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val questions = remember {
        listOf(
            KidQuizQuestion(
                id = 1,
                question = "ماذا نفعل بفرشاة الأسنان قبل النوم؟",
                options = listOf("ننظف بها أسناننا بلطف لنحميها من التسوس", "نلعب بها في الحمام كسيارة كرتونية"),
                correctIndex = 0
            ),
            KidQuizQuestion(
                id = 2,
                question = "ما هو التصرف الصحيح بعد الانتهاء من اللعب بألعابنا الرائعة؟",
                options = listOf("نترك الألعاب ملقاة على الأرض ونخرج", "نجمعها بلطف ونرتبها في الصندوق المخصص لها"),
                correctIndex = 1
            ),
            KidQuizQuestion(
                id = 3,
                question = "هل يجوز اللعب بمقابس الكهرباء أو كبريت المطبخ؟",
                options = listOf("نعم، الألعاب النارية والكهرباء مسلية", "لا، خطرة جداً وقد تسبب لنا الأذى والحروق"),
                correctIndex = 1
            ),
            KidQuizQuestion(
                id = 4,
                question = "عند عبور الشارع الجميل، ماذا يجب أن نفعل؟",
                options = listOf("نمسك يد بابا أو ماما بقوة وننظر يميناً ويساراً بالأمان", "نجري بمفردنا بسرعة فائقة"),
                correctIndex = 0
            ),
            KidQuizQuestion(
                id = 5,
                question = "ما هو السلوك الأجمل عندما يطلب منا الوالدان طلباً؟",
                options = listOf("نقول 'حاضر' ونطيعهم بابتسامة فخورة", "نتجاهلهم ونستمر في مشاهدة التلفاز"),
                correctIndex = 0
            )
        )
    }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var earnMessage by remember { mutableStateOf("") }
    
    val currentQuestion = questions[currentQuestionIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اختبارات الأبطال", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Progress Label
                Text(
                    text = "السؤال ${currentQuestionIndex + 1} من ${questions.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )

                // Linear progress bar
                LinearProgressIndicator(
                    progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Beautiful Question Card (Natural Glassmorphism-style)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "❓",
                            fontSize = 40.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = currentQuestion.question,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 30.sp
                            ),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Options
                currentQuestion.options.forEachIndexed { index, option ->
                    val isSelected = selectedOptionIndex == index
                    val isCorrect = currentQuestion.correctIndex == index
                    
                    val cardColor = when {
                        !hasAnswered -> {
                            if (isSelected) MaterialTheme.colorScheme.secondaryContainer 
                            else Color.White
                        }
                        else -> {
                            if (isCorrect) Color(0xFFD1FAE5) // Soft green for correct
                            else if (isSelected) Color(0xFFFEE2E2) // Soft red for wrong selected
                            else Color.White
                        }
                    }

                    val borderColor = when {
                        !hasAnswered -> if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent
                        else -> if (isCorrect) Color(0xFF10B981) else if (isSelected) Color(0xFFEF4444) else Color.Transparent
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !hasAnswered) {
                                selectedOptionIndex = index
                            },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        border = if (borderColor != Color.Transparent) androidx.compose.foundation.BorderStroke(2.dp, borderColor) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (index == 0) "أ" else "ب",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                            
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 24.sp
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            if (hasAnswered && isCorrect) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Correct",
                                    tint = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom feedback & navigation
                if (hasAnswered) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = earnMessage,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (currentQuestionIndex < questions.size - 1) {
                                    currentQuestionIndex++
                                    selectedOptionIndex = null
                                    hasAnswered = false
                                    earnMessage = ""
                                } else {
                                    onBack()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (currentQuestionIndex < questions.size - 1) "السؤال التالي" else "إكمال الاختبار والعودة والنجوم ✨",
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            selectedOptionIndex?.let { selectedIdx ->
                                hasAnswered = true
                                val isCorrect = selectedIdx == currentQuestion.correctIndex
                                if (isCorrect) {
                                    earnMessage = "إجابة صحيحة ورائعة جداً! 🎉 حصلت على ${currentQuestion.rewardStars} نجوم!"
                                    viewModel.awardQuizStars(currentQuestion.rewardStars)
                                } else {
                                    earnMessage = "أوه! حاولي طريقتكِ اللذيذة في السؤال التالي يا بطلة. 🥰"
                                }
                            }
                        },
                        enabled = selectedOptionIndex != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("التحقق من الإجابة", modifier = Modifier.padding(6.dp))
                    }
                }
            }
        }
    }
}
