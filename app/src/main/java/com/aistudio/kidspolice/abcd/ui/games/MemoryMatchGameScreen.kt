package com.aistudio.kidspolice.abcd.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.sound.CallSoundManager
import com.aistudio.kidspolice.abcd.ui.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryMatchGameScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val soundManager = remember { CallSoundManager(context) }
    val scope = rememberCoroutineScope()
    
    // 16 cards (8 pairs)
    val icons = remember { 
        listOf("🍎", "🍓", "🍒", "🍭", "🍬", "🍦", "🍪", "🍩", "🍎", "🍓", "🍒", "🍭", "🍬", "🍦", "🍪", "🍩").shuffled() 
    }
    var flippedStates by remember { mutableStateOf(List(16) { false }) }
    var matchedStates by remember { mutableStateOf(List(16) { false }) }
    var selectedIndexes by remember { mutableStateOf(emptyList<Int>()) }
    var isProcessing by remember { mutableStateOf(false) }
    
    val bestScore by viewModel.getBestScore("memory").collectAsState(initial = 0)
    var currentTries by remember { mutableIntStateOf(0) }
    var showVictory by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("لعبة الذاكرة 🧠", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFFE0F2FE), Color.White)))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("المحاولات: $currentTries", fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                    Text("أفضل نتيجة: ${bestScore ?: 0}", fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(16) { index ->
                        val isFlipped = flippedStates[index] || matchedStates[index]
                        MemoryCard(
                            icon = icons[index],
                            isFlipped = isFlipped,
                            isMatched = matchedStates[index],
                            onClick = {
                                if (!isProcessing && !isFlipped && selectedIndexes.size < 2) {
                                    soundManager.playSynthSound("funny")
                                    flippedStates = flippedStates.toMutableList().also { it[index] = true }
                                    selectedIndexes = selectedIndexes + index
                                    
                                    if (selectedIndexes.size == 2) {
                                        currentTries++
                                        isProcessing = true
                                        scope.launch {
                                            delay(800)
                                            val first = selectedIndexes[0]
                                            val second = selectedIndexes[1]
                                            if (icons[first] == icons[second]) {
                                                matchedStates = matchedStates.toMutableList().also {
                                                    it[first] = true
                                                    it[second] = true
                                                }
                                                if (matchedStates.all { it }) {
                                                    showVictory = true
                                                    soundManager.speakDirect("أحسنت يا بطل! لقد وجدت كل الأزواج المتشابهة!")
                                                    viewModel.awardQuizStars(15)
                                                    viewModel.saveBestScore("memory", currentTries)
                                                }
                                            } else {
                                                flippedStates = flippedStates.toMutableList().also {
                                                    it[first] = false
                                                    it[second] = false
                                                }
                                            }
                                            selectedIndexes = emptyList()
                                            isProcessing = false
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                if (showVictory) {
                    Text(
                        "🎉 مبروك يا بطل! 🎉",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0369A1),
                        modifier = Modifier.padding(12.dp)
                    )
                    Button(
                        onClick = {
                            flippedStates = List(16) { false }
                            matchedStates = List(16) { false }
                            selectedIndexes = emptyList()
                            currentTries = 0
                            showVictory = false
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                    ) {
                        Text("العب مرة أخرى", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryCard(icon: String, isFlipped: Boolean, isMatched: Boolean, onClick: () -> Unit) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            // Back of card (Blue with Question Mark)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0EA5E9))
                    .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "؟",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        } else {
            // Front of card (Yellow with Icon)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(180f)
                    .background(if (isMatched) Color(0xFFFDE68A) else Color(0xFFFFD970))
                    .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 32.sp
                )
            }
        }
    }
}
