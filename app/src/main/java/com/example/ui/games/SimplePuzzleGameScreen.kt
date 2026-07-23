package com.example.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import com.example.sound.CallSoundManager
import com.example.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimplePuzzleGameScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val soundManager = remember { CallSoundManager(context) }
    
    val bestScore by viewModel.getBestScore("puzzle").collectAsState(initial = 0)
    
    // 2x2 Puzzle pieces (indices 0, 1, 2, 3)
    var currentOrder by remember { mutableStateOf(listOf(0, 1, 2, 3).shuffled()) }
    var selectedPieceIndex by remember { mutableStateOf<Int?>(null) }
    var showVictory by remember { mutableStateOf(false) }

    LaunchedEffect(currentOrder) {
        if (currentOrder == listOf(0, 1, 2, 3)) {
            showVictory = true
            soundManager.playSynthSound("bell")
            viewModel.awardQuizStars(15)
            viewModel.saveBestScore("puzzle", (bestScore ?: 0) + 1)
            soundManager.speakDirect("أحسنت يا بطل! لقد رتبت الصورة بشكل صحيح!")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تركيب البازل 🧩", fontWeight = FontWeight.Black) },
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
                .background(Brush.verticalGradient(listOf(Color(0xFFCCFBF1), Color.White)))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("رتب قطع الصورة!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
            Text("أفضل نتيجة: ${bestScore ?: 0} بازل مكتمل", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(40.dp))

            // Puzzle Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .size(300.dp)
                    .border(4.dp, Color(0xFF0D9488), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White),
                userScrollEnabled = false
            ) {
                itemsIndexed(currentOrder) { displayIndex, pieceId ->
                    val isSelected = selectedPieceIndex == displayIndex
                    
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .border(1.dp, Color.LightGray)
                            .background(if (isSelected) Color(0xFF99F6E4) else Color.White)
                            .clickable {
                                if (selectedPieceIndex == null) {
                                    selectedPieceIndex = displayIndex
                                    soundManager.playSynthSound("funny")
                                } else {
                                    // Swap
                                    val first = selectedPieceIndex!!
                                    val second = displayIndex
                                    val newList = currentOrder.toMutableList()
                                    val temp = newList[first]
                                    newList[first] = newList[second]
                                    newList[second] = temp
                                    currentOrder = newList
                                    selectedPieceIndex = null
                                    soundManager.playSynthSound("funny")
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        PuzzlePiece(pieceId)
                    }
                }
            }

            if (showVictory) {
                Spacer(modifier = Modifier.height(30.dp))
                Text("🎉 رائع جداً! 🎉", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF0D9488))
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        currentOrder = listOf(0, 1, 2, 3).shuffled()
                        showVictory = false
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488))
                ) {
                    Text("العب مرة أخرى", fontWeight = FontWeight.Bold)
                }
            } else {
                Spacer(modifier = Modifier.height(40.dp))
                Text("المس قطعتين لتبديل مكانهما!", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun PuzzlePiece(id: Int) {
    // We use different crops of the same image to simulate puzzle pieces
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = com.example.R.drawable.captain_didi),
            contentDescription = "Piece",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = when(id) {
                0 -> Alignment.TopStart
                1 -> Alignment.TopEnd
                2 -> Alignment.BottomStart
                else -> Alignment.BottomEnd
            }
        )
        
        // Piece ID for debugging/visual help
        // Text("$id", modifier = Modifier.align(Alignment.Center), color = Color.White, fontWeight = FontWeight.Bold)
    }
}
