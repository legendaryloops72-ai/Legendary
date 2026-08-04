package com.aistudio.kidspolice.abcd.ui.games

import androidx.compose.animation.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.sound.CallSoundManager
import com.aistudio.kidspolice.abcd.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindDifferencesGameScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val soundManager = remember { CallSoundManager(context) }
    
    val bestScore by viewModel.getBestScore("find_differences").collectAsState(initial = 0)
    var differencesFound by remember { mutableStateOf(setOf<Int>()) }
    var showVictory by remember { mutableStateOf(false) }

    // Positions of differences relative to image size
    val differencePositions = remember {
        listOf(
            OffsetPercent(0.2f, 0.3f), // Hat change
            OffsetPercent(0.8f, 0.15f), // Star change
            OffsetPercent(0.5f, 0.8f)  // Button change
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("أوجد الاختلاف 🔍", fontWeight = FontWeight.Black) },
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
                .background(Brush.verticalGradient(listOf(Color(0xFFFFEDD5), Color.White)))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("الاختلافات المتبقية: ${3 - differencesFound.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("أفضل نتيجة: ${bestScore ?: 0} مستويات", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            // Two Images
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image 1 (Original)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(4.dp, Color.White, RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = com.aistudio.kidspolice.abcd.R.drawable.splash_image),
                        contentDescription = "Original",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Image 2 (With Differences)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(4.dp, Color(0xFFEA580C), RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = com.aistudio.kidspolice.abcd.R.drawable.splash_image),
                        contentDescription = "Difference",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Overlay differences
                    differencePositions.forEachIndexed { index, pos ->
                        val isFound = differencesFound.contains(index)
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            // The difference itself (only visible if not found, or maybe slightly different)
                            if (!isFound) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .offset(
                                            x = (LocalContext.current.resources.displayMetrics.widthPixels * pos.x / 4).dp, // Crude approximation
                                            y = (LocalContext.current.resources.displayMetrics.heightPixels * pos.y / 8).dp
                                        )
                                        .size(30.dp)
                                        .background(Color.Yellow.copy(alpha = 0.3f), CircleShape)
                                        .clickable {
                                            soundManager.playSynthSound("bell")
                                            differencesFound = differencesFound + index
                                            if (differencesFound.size == 3) {
                                                showVictory = true
                                                viewModel.awardQuizStars(10)
                                                viewModel.saveBestScore("find_differences", (bestScore ?: 0) + 1)
                                                soundManager.speakDirect("أحسنت! لقد وجدت كل الاختلافات!")
                                            }
                                        }
                                )
                            } else {
                                // Green checkmark when found
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .offset(
                                            x = (LocalContext.current.resources.displayMetrics.widthPixels * pos.x / 4).dp,
                                            y = (LocalContext.current.resources.displayMetrics.heightPixels * pos.y / 8).dp
                                        )
                                        .size(30.dp)
                                        .background(Color(0xFF22C55E).copy(alpha = 0.6f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("✅", fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }

            if (showVictory) {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        differencesFound = emptySet()
                        showVictory = false
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C))
                ) {
                    Text("المستوى التالي", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            Text("المس الاختلافات في الصورة السفلية!", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

data class OffsetPercent(val x: Float, val y: Float)
