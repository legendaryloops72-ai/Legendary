package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sound.CallSoundManager
import com.example.ui.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberOrderGameScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val soundManager = remember { CallSoundManager(context) }
    val scope = rememberCoroutineScope()

    var numbers by remember { mutableStateOf((1..5).toList().shuffled()) }
    var nextToTap by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    val bestScore by viewModel.getBestScore("number_order").collectAsState(initial = 0)
    var showVictory by remember { mutableStateOf(false) }

    val balloonColors = listOf(
        Color(0xFFFFADAD), Color(0xFFFFD6A5), Color(0xFFFDFFB6),
        Color(0xFFCAFFBF), Color(0xFF9BFBC0), Color(0xFFA0C4FF)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ترتيب الأرقام 🔢", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFFFCE7F3), Color.White)))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("المستوى: $score", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                if (showVictory) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉 ممتاز يا بطل! 🎉", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFFBE185D))
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                numbers = (1..5).toList().shuffled()
                                nextToTap = 1
                                showVictory = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE185D))
                        ) {
                            Text("العب مرة أخرى")
                        }
                    }
                } else {
                    Text("اضغط على الأرقام بالترتيب: 1 -> 2 -> 3 -> 4 -> 5", fontSize = 14.sp, color = Color.Gray)
                    
                    Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        numbers.forEachIndexed { index, num ->
                            val tapped = num < nextToTap
                            if (!tapped) {
                                // Simple randomized positions for balloons
                                val xOffset = (index % 3) * 110 - 110
                                val yOffset = (index / 3) * 150 - 75
                                
                                BalloonNumber(
                                    number = num,
                                    color = balloonColors[num % balloonColors.size],
                                    modifier = Modifier.align(Alignment.Center).offset(xOffset.dp, yOffset.dp),
                                    onClick = {
                                        if (num == nextToTap) {
                                            soundManager.playSynthSound("bell")
                                            nextToTap++
                                            if (nextToTap > 5) {
                                                score++
                                                showVictory = true
                                                viewModel.awardQuizStars(5)
                                                viewModel.saveBestScore("number_order", score)
                                            }
                                        } else {
                                            soundManager.playSynthSound("funny")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                
                Text("أفضل نتيجة: ${bestScore ?: 0}", fontSize = 16.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun BalloonNumber(number: Int, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "balloon")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse),
        label = "float"
    )

    Box(
        modifier = modifier
            .offset(y = floatAnim.dp)
            .size(90.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .border(2.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("$number", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color.White)
        
        // Balloon string
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 20.dp)
                .width(2.dp)
                .height(30.dp)
                .background(Color.Gray.copy(alpha = 0.5f))
        )
    }
}
