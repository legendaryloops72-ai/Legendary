package com.aistudio.kidspolice.abcd.ui.games

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.sound.CallSoundManager
import com.aistudio.kidspolice.abcd.ui.AppViewModel
import kotlin.math.roundToInt

sealed class GameShape(val name: String, val emoji: String, val color: Color) {
    object Circle : GameShape("دائرة", "⚪", Color.Red)
    object Square : GameShape("مربع", "🟦", Color.Blue)
    object Triangle : GameShape("مثلث", "🔺", Color.Green)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShapeMatchGameScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val soundManager = remember { CallSoundManager(context) }
    
    val shapes = listOf(GameShape.Circle, GameShape.Square, GameShape.Triangle)
    var targetShape by remember { mutableStateOf(shapes.random()) }
    var matchedCount by remember { mutableIntStateOf(0) }
    val bestScore by viewModel.getBestScore("shape_match").collectAsState(initial = 0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تطابق الأشكال 🔺", fontWeight = FontWeight.Black) },
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
                .background(Brush.verticalGradient(listOf(Color(0xFFDCFCE7), Color.White)))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("النقاط: $matchedCount", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("أفضل نتيجة: ${bestScore ?: 0}", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(40.dp))

            // Target area
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.5f))
                    .border(4.dp, Color.Gray, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ضع ال${targetShape.name} هنا",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Available shapes to tap (simpler than full drag for mobile touch simulation)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                shapes.forEach { shape ->
                    DraggableShapeItem(shape) {
                        if (shape == targetShape) {
                            soundManager.playSynthSound("bell")
                            matchedCount++
                            targetShape = (shapes - targetShape).random()
                            viewModel.awardQuizStars(5)
                            viewModel.saveBestScore("shape_match", matchedCount)
                        } else {
                            soundManager.playSynthSound("funny")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            Text("اضغط على الشكل الصحيح لوضعه في الصندوق!", fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun DraggableShapeItem(shape: GameShape, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(2.dp, shape.color, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(shape.emoji, fontSize = 40.sp)
    }
}
