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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sound.CallSoundManager
import com.example.ui.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Bubble(val id: Int, val x: Float, var y: Float, val size: Float, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BubblePopGameScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val soundManager = remember { CallSoundManager(context) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val screenHeight = configuration.screenHeightDp.toFloat()

    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var gameActive by remember { mutableStateOf(true) }
    var bubbles by remember { mutableStateOf(emptyList<Bubble>()) }
    var bubbleIdCounter by remember { mutableIntStateOf(0) }
    val bestScore by viewModel.getBestScore("bubble_pop").collectAsState(initial = 0)

    // Game Timer
    LaunchedEffect(gameActive) {
        if (gameActive) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            gameActive = false
            viewModel.saveBestScore("bubble_pop", score)
            viewModel.awardQuizStars(score / 5)
            soundManager.speakDirect("انتهى الوقت! لقد فرقعت $score فقاعة!")
        }
    }

    // Bubble Generation and Movement
    LaunchedEffect(gameActive) {
        if (gameActive) {
            while (gameActive) {
                if (bubbles.size < 10) {
                    val newBubble = Bubble(
                        id = bubbleIdCounter++,
                        x = Random.nextFloat() * (screenWidth - 80),
                        y = screenHeight + 100,
                        size = 60f + Random.nextFloat() * 40f,
                        color = listOf(Color(0xFF60A5FA), Color(0xFFF472B6), Color(0xFF34D399), Color(0xFFFBBF24)).random()
                    )
                    bubbles = bubbles + newBubble
                }
                
                // Move bubbles up
                bubbles = bubbles.map { it.copy(y = it.y - (2f + score / 10f)) }.filter { it.y > -100 }
                
                delay(16) // ~60 FPS
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("فرقع الفقاعات 🫧", fontWeight = FontWeight.Black) },
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
                .background(Brush.verticalGradient(listOf(Color(0xFFF0F9FF), Color.White)))
        ) {
            // Game UI
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("النقاط: $score", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("الوقت: $timeLeft", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if (timeLeft < 5) Color.Red else Color.Black)
                }
                Text("أفضل نتيجة: ${bestScore ?: 0}", fontSize = 16.sp, color = Color.Gray)
            }

            // Bubbles
            bubbles.forEach { bubble ->
                BubbleItem(bubble) {
                    if (gameActive) {
                        soundManager.playSynthSound("bird") // Pop sound simulation
                        score++
                        bubbles = bubbles.filter { it.id != bubble.id }
                    }
                }
            }

            // End Game Modal
            if (!gameActive) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.width(300.dp).padding(16.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("انتهى التحدي! 🎉", fontSize = 28.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("لقد حصلت على:", fontSize = 18.sp)
                            Text("$score نقطة", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color(0xFF0284C7))
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    score = 0
                                    timeLeft = 30
                                    bubbles = emptyList()
                                    gameActive = true
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                Text("العب مرة أخرى", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BubbleItem(bubble: Bubble, onPop: () -> Unit) {
    var isPopping by remember { mutableStateOf(false) }
    val popScale by animateFloatAsState(
        targetValue = if (isPopping) 1.5f else 1f,
        animationSpec = tween(100),
        finishedListener = { if (isPopping) onPop() },
        label = "pop"
    )
    val popAlpha by animateFloatAsState(
        targetValue = if (isPopping) 0f else 0.8f,
        animationSpec = tween(100),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .offset(x = bubble.x.dp, y = bubble.y.dp)
            .size(bubble.size.dp)
            .scale(popScale)
            .alpha(popAlpha)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(Color.White, bubble.color)
                )
            )
            .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) {
                isPopping = true
            }
    )
}
