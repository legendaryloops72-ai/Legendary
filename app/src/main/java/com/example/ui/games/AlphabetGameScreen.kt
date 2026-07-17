package com.example.ui.games

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sound.CallSoundManager
import com.example.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetGameScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val soundManager = remember { CallSoundManager(context) }
    val vibrator = context.getSystemService(Vibrator::class.java)

    val allLetters = listOf(
        "أ", "ب", "ت", "ث", "ج", "ح", "خ", "د", "ذ", "ر", "ز", "س", "ش", "ص", "ض", "ط", "ظ", "ع", "غ", "ف", "ق", "ك", "ل", "م", "ن", "هـ", "و", "ي"
    )

    var targetLetter by remember { mutableStateOf(allLetters.random()) }
    var options by remember { mutableStateOf(allLetters.shuffled().take(4)) }
    var correctCount by remember { mutableIntStateOf(0) }
    var showVictory by remember { mutableStateOf(false) }
    val bestScore by viewModel.getBestScore("alphabet").collectAsState(initial = 0)

    LaunchedEffect(targetLetter) {
        if (!options.contains(targetLetter)) {
            options = (options.take(3) + targetLetter).shuffled()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تحدي الحروف 🔤", fontWeight = FontWeight.Black) },
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
                .background(Brush.verticalGradient(listOf(Color(0xFFEDE9FE), Color.White)))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("المستوى: $correctCount / 5", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
                Text("أفضل نتيجة: ${bestScore ?: 0}", fontSize = 16.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(40.dp))

                if (showVictory) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉 ممتاز يا ذكي! 🎉", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF7C3AED))
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                correctCount = 0
                                showVictory = false
                                targetLetter = allLetters.random()
                                options = allLetters.shuffled().take(4)
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
                        ) {
                            Text("العب مرة أخرى", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .background(Color.White, CircleShape)
                            .border(4.dp, Color(0xFF7C3AED), CircleShape)
                            .shadow(8.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(targetLetter, fontSize = 72.sp, fontWeight = FontWeight.Black, color = Color(0xFF7C3AED))
                    }

                    Spacer(modifier = Modifier.height(60.dp))

                    Text("اختر الحرف المطابق:", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(20.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                    ) {
                        items(options) { letter ->
                            Button(
                                onClick = {
                                    if (letter == targetLetter) {
                                        soundManager.playSynthSound("bell")
                                        correctCount++
                                        if (correctCount >= 5) {
                                            showVictory = true
                                            viewModel.awardQuizStars(10)
                                            viewModel.saveBestScore("alphabet", (bestScore ?: 0) + 1)
                                            soundManager.speakDirect("أحسنت يا بطل! أنت تعرف حروفك جيداً!")
                                        } else {
                                            targetLetter = allLetters.random()
                                            options = allLetters.shuffled().take(4)
                                        }
                                    } else {
                                        soundManager.playSynthSound("funny")
                                        vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                                    }
                                },
                                modifier = Modifier.height(80.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                border = BorderStroke(2.dp, Color(0xFF7C3AED))
                            ) {
                                Text(letter, fontSize = 40.sp, fontWeight = FontWeight.Black, color = Color(0xFF7C3AED))
                            }
                        }
                    }
                }
            }
        }
    }
}
