package com.example.ui.games

import android.os.VibrationEffect
import android.os.Vibrator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sound.CallSoundManager
import com.example.ui.AppViewModel
import kotlin.random.Random

data class ColorOption(val name: String, val color: Color)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorTapGameScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val soundManager = remember { CallSoundManager(context) }
    val vibrator = context.getSystemService(Vibrator::class.java)

    val allColors = listOf(
        ColorOption("أحمر", Color.Red),
        ColorOption("أزرق", Color.Blue),
        ColorOption("أخضر", Color.Green),
        ColorOption("أصفر", Color.Yellow),
        ColorOption("برتقالي", Color(0xFFFFA500)),
        ColorOption("بنفسجي", Color(0xFF800080))
    )

    var targetColor by remember { mutableStateOf(allColors.random()) }
    var options by remember { mutableStateOf(allColors.shuffled().take(4)) }
    var score by remember { mutableIntStateOf(0) }
    val bestScore by viewModel.getBestScore("color_tap").collectAsState(initial = 0)

    LaunchedEffect(targetColor) {
        if (!options.contains(targetColor)) {
            options = (options.take(3) + targetColor).shuffled()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اضغط اللون 🔴", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFFDBEAFE), Color.White)))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("النقاط: $score", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("أفضل نتيجة: ${bestScore ?: 0}", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "أين هو اللون ال${targetColor.name}؟",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    ColorCircle(options[0]) { checkColor(options[0], targetColor, soundManager, vibrator) { 
                        score++
                        targetColor = allColors.random()
                        options = allColors.shuffled().take(4)
                        viewModel.saveBestScore("color_tap", score)
                    } }
                    ColorCircle(options[1]) { checkColor(options[1], targetColor, soundManager, vibrator) { 
                        score++
                        targetColor = allColors.random()
                        options = allColors.shuffled().take(4)
                        viewModel.saveBestScore("color_tap", score)
                    } }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    ColorCircle(options[2]) { checkColor(options[2], targetColor, soundManager, vibrator) { 
                        score++
                        targetColor = allColors.random()
                        options = allColors.shuffled().take(4)
                        viewModel.saveBestScore("color_tap", score)
                    } }
                    ColorCircle(options[3]) { checkColor(options[3], targetColor, soundManager, vibrator) { 
                        score++
                        targetColor = allColors.random()
                        options = allColors.shuffled().take(4)
                        viewModel.saveBestScore("color_tap", score)
                    } }
                }
            }
        }
    }
}

@Composable
fun ColorCircle(option: ColorOption, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(option.color)
            .clickable(onClick = onClick)
            .border(4.dp, Color.White, CircleShape)
            .shadow(8.dp, CircleShape)
    )
}

fun checkColor(
    selected: ColorOption,
    target: ColorOption,
    soundManager: CallSoundManager,
    vibrator: Vibrator?,
    onCorrect: () -> Unit
) {
    if (selected == target) {
        soundManager.playSynthSound("bell")
        onCorrect()
    } else {
        soundManager.playSynthSound("funny")
        vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}
