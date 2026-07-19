package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sound.CallSoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// Data classes for game definitions
data class GameInfo(
    val id: Int,
    val title: String,
    val description: String,
    val emoji: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onNavigateToMemoryGame: () -> Unit,
    onNavigateToColorTapGame: () -> Unit,
    onNavigateToNumberOrderGame: () -> Unit,
    onNavigateToShapeMatchGame: () -> Unit,
    onNavigateToAlphabetGame: () -> Unit,
    onNavigateToBubblePopGame: () -> Unit,
    onNavigateToFindDifferencesGame: () -> Unit,
    onNavigateToPuzzleGame: () -> Unit
) {
    val context = LocalContext.current
    val soundManager = remember { CallSoundManager(context) }
    val profile by viewModel.profile.collectAsState()
    
    // Animation for stars
    val infiniteTransition = rememberInfiniteTransition(label = "StarGlowing")
    val starScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "StarScale"
    )

    // List of 4 Hub Items
    val hubItems = remember {
        listOf(
            GameInfo(
                id = 1,
                title = "لعبة الذاكرة",
                description = "طابق أزواج الصور المتشابهة",
                emoji = "🧠",
                primaryColor = Color(0xFFFDE68A),
                secondaryColor = Color(0xFFFEF3C7),
                accentColor = Color(0xFFD97706)
            ),
            GameInfo(
                id = 2,
                title = "اضغط اللون",
                description = "اختر اللون الصحيح بسرعة",
                emoji = "🔴",
                primaryColor = Color(0xFFBFDBFE),
                secondaryColor = Color(0xFFDBEAFE),
                accentColor = Color(0xFF1D4ED8)
            ),
            GameInfo(
                id = 3,
                title = "ترتيب الأرقام",
                description = "رتب الأرقام من 1 إلى 5",
                emoji = "🔢",
                primaryColor = Color(0xFFFBCFE8),
                secondaryColor = Color(0xFFFCE7F3),
                accentColor = Color(0xFFBE185D)
            ),
            GameInfo(
                id = 4,
                title = "تطابق الأشكال",
                description = "اسحب الشكل لمكانه الصحيح",
                emoji = "🔺",
                primaryColor = Color(0xFFBBF7D0),
                secondaryColor = Color(0xFFDCFCE7),
                accentColor = Color(0xFF15803D)
            ),
            GameInfo(
                id = 5,
                title = "تحدي الحروف",
                description = "طابق الحروف العربية المتشابهة",
                emoji = "🔤",
                primaryColor = Color(0xFFDDD6FE),
                secondaryColor = Color(0xFFEDE9FE),
                accentColor = Color(0xFF7C3AED)
            ),
            GameInfo(
                id = 6,
                title = "فرقع الفقاعات",
                description = "فرقع الفقاعات الملونة بسرعة",
                emoji = "🫧",
                primaryColor = Color(0xFFBAE6FD),
                secondaryColor = Color(0xFFF0F9FF),
                accentColor = Color(0xFF0284C7)
            ),
            GameInfo(
                id = 7,
                title = "أوجد الاختلاف",
                description = "ابحث عن الاختلافات بين الصورتين",
                emoji = "🔍",
                primaryColor = Color(0xFFFFEDD5),
                secondaryColor = Color(0xFFFFF7ED),
                accentColor = Color(0xFFEA580C)
            ),
            GameInfo(
                id = 8,
                title = "تركيب البازل",
                description = "رتب قطع الصورة المبعثرة",
                emoji = "🧩",
                primaryColor = Color(0xFFCCFBF1),
                secondaryColor = Color(0xFFF0FDFA),
                accentColor = Color(0xFF0D9488)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFEFF6FF), Color(0xFFDBEAFE), Color(0xFFE0F2FE))
                )
            )
    ) {
        PlayfulCanvasBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .scale(starScale)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFBBF24), Color(0xFFF59E0B))
                            ),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .border(2.dp, Color.White, RoundedCornerShape(18.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("⭐", fontSize = 18.sp)
                        Text(
                            text = "${profile?.totalStars ?: 0} نجمة",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Text(
                    text = "الألعاب والمرح 🎮",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E3A8A)
                )

                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color.White, CircleShape)
                        .border(2.5.dp, Color(0xFF3B82F6), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع",
                        tint = Color(0xFF3B82F6)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                
                Text(
                    text = "اختر ما تريد القيام به يا بطل!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )

                hubItems.forEach { item ->
                    HubCard(
                        item = item,
                        onClick = {
                            soundManager.playSynthSound("funny")
                            when (item.id) {
                                1 -> onNavigateToMemoryGame()
                                2 -> onNavigateToColorTapGame()
                                3 -> onNavigateToNumberOrderGame()
                                4 -> onNavigateToShapeMatchGame()
                                5 -> onNavigateToAlphabetGame()
                                6 -> onNavigateToBubblePopGame()
                                7 -> onNavigateToFindDifferencesGame()
                                8 -> onNavigateToPuzzleGame()
                            }
                        }
                    )
                }
            }

            AdBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun HubCard(item: GameInfo, onClick: () -> Unit) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "Scale")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 2.dp else 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(item.secondaryColor, Color.White)
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = item.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = item.accentColor
                )
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Right
                )
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White, CircleShape)
                    .shadow(4.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.emoji, fontSize = 40.sp)
            }
        }
    }
}

// Playful background items using simple Canvas drawings
@Composable
fun PlayfulCanvasBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Smiley Yellow Sun ☀️ top left corner
        drawCircle(
            color = Color(0xFFFEF08A).copy(alpha = 0.35f),
            radius = 110.dp.toPx(),
            center = Offset(20.dp.toPx(), 40.dp.toPx())
        )
        drawCircle(
            color = Color(0xFFFDE047).copy(alpha = 0.5f),
            radius = 70.dp.toPx(),
            center = Offset(20.dp.toPx(), 40.dp.toPx())
        )

        // Cute ambient cloud patterns bottom right
        drawCircle(
            color = Color.White.copy(alpha = 0.45f),
            radius = 80.dp.toPx(),
            center = Offset(w - 50.dp.toPx(), h - 100.dp.toPx())
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.45f),
            radius = 60.dp.toPx(),
            center = Offset(w - 110.dp.toPx(), h - 90.dp.toPx())
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.35f),
            radius = 50.dp.toPx(),
            center = Offset(w - 20.dp.toPx(), h - 70.dp.toPx())
        )
    }
}

// Game dashboard tile component with 3D Pixar feel
@Composable
fun GameDashboardTile(game: GameInfo, onClick: () -> Unit) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.94f else 1.0f, label = "pressScale")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = LocalIndication.current) { onClick() },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.5.dp, game.primaryColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Emoji 3D Glowing Container
            Box(
                modifier = Modifier
                    .size(65.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(game.secondaryColor, game.primaryColor)
                        ),
                        shape = CircleShape
                    )
                    .border(2.dp, game.accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = game.emoji,
                    fontSize = 32.sp
                )
            }

            // Game metadata
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = game.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp
                )
                Text(
                    text = game.description,
                    fontSize = 9.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 11.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Bouncy Badge clicker
            Box(
                modifier = Modifier
                    .background(game.accentColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "العب الآن 🌟",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = game.accentColor
                )
            }
        }
    }
}

// ----------------------------------------------------
// 1. Memory Match Game Composable (معركة الذاكرة الكرتونية)
// ----------------------------------------------------
@Composable
fun MemoryMatchGame(soundManager: CallSoundManager, viewModel: AppViewModel, onFinish: () -> Unit) {
    val animalPairs = remember {
        listOf("🦁", "🐱", "🦒", "🐒", "🐸", "🐼", "🦁", "🐱", "🦒", "🐒", "🐸", "🐼").shuffled()
    }
    
    var flippedList by remember { mutableStateOf(List(12) { false }) }
    var matchedList by remember { mutableStateOf(List(12) { false }) }
    var selectedIndexes by remember { mutableStateOf(listOf<Int>()) }
    var isProcessing by remember { mutableStateOf(false) }
    var matchesFound by remember { mutableStateOf(0) }
    var showVictory by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(26.dp))
            .border(2.5.dp, Color(0xFFFDE047), RoundedCornerShape(26.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Description
        Text(
            text = "معركة الذاكرة للأذكياء 🦁",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1E3A8A)
        )
        Text(
            text = "اضغط على البطاقات لتكشف عن أزواج الحيوانات السعيدة والمطابقة!",
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        // Victory banner
        if (showVictory) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFDCFCE7), RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFF22C55E), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("أنت بطل رائع وعبقري! 🎉", fontWeight = FontWeight.Bold, color = Color(0xFF15803D), fontSize = 16.sp)
                    Text("+15 نجمة ذهبية تم إضافتها لمحفظتك!", color = Color(0xFF166534), fontSize = 11.sp)
                }
            }
        }

        // 4x3 Grid for 12 cards
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f).padding(vertical = 12.dp)
        ) {
            items(12) { index ->
                val isFlipped = flippedList[index] || matchedList[index]
                val item = animalPairs[index]
                
                Box(
                    modifier = Modifier
                        .height(85.dp)
                        .background(
                            brush = if (isFlipped) Brush.linearGradient(listOf(Color(0xFFFEF9C3), Color(0xFFFEF08A)))
                            else Brush.linearGradient(listOf(Color(0xFFFFD166), Color(0xFFF7B267))),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .border(
                            width = 2.5.dp,
                            color = if (matchedList[index]) Color(0xFF22C55E) else Color.White,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .clickable {
                            if (isProcessing || matchedList[index] || selectedIndexes.contains(index) || selectedIndexes.size >= 2) return@clickable
                            
                            // Flip card
                            soundManager.playSynthSound("funny")
                            flippedList = flippedList.toMutableList().also { it[index] = true }
                            selectedIndexes = selectedIndexes + index
                            
                            if (selectedIndexes.size == 2) {
                                isProcessing = true
                                val first = selectedIndexes[0]
                                val second = selectedIndexes[1]
                                
                                if (animalPairs[first] == animalPairs[second]) {
                                    // Matched!
                                    scope.launch {
                                        delay(400)
                                        matchedList = matchedList.toMutableList().also {
                                            it[first] = true
                                            it[second] = true
                                        }
                                        selectedIndexes = emptyList()
                                        matchesFound++
                                        soundManager.speakDirect("تطابق صحيح وممتاز! أحسنت!")
                                        viewModel.awardQuizStars(2)
                                        isProcessing = false
                                        
                                        if (matchesFound == 6) {
                                            showVictory = true
                                            soundManager.playSynthSound("bell")
                                            soundManager.speakDirect("وااو! مبروك يا بطل، لقد كشفت كل الحيوانات السعيدة في معركة الذاكرة! أنت مذهل!")
                                            viewModel.awardQuizStars(15)
                                        }
                                    }
                                } else {
                                    // Unmatched
                                    scope.launch {
                                        delay(1000)
                                        flippedList = flippedList.toMutableList().also {
                                            it[first] = false
                                            it[second] = false
                                        }
                                        selectedIndexes = emptyList()
                                        isProcessing = false
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isFlipped) {
                        Text(text = item, fontSize = 34.sp)
                    } else {
                        Text(text = "❓", fontSize = 28.sp, color = Color.White)
                    }
                }
            }
        }

        // Restart or back button controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    flippedList = List(12) { false }
                    matchedList = List(12) { false }
                    selectedIndexes = emptyList()
                    matchesFound = 0
                    showVictory = false
                    soundManager.speakDirect("دعنا نعيد اللعبة من جديد يا بطل! ركّز جيداً!")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "إعادة التحدي")
                Spacer(modifier = Modifier.width(4.dp))
                Text("إعادة اللعب 🔄", fontWeight = FontWeight.Black)
            }
        }
    }
}

// ----------------------------------------------------
// 2. Number Learning Game Composable (تعلم الأرقام السعيدة)
// ----------------------------------------------------
@Composable
fun NumberLearningGame(soundManager: CallSoundManager, viewModel: AppViewModel) {
    var activeNumber by remember { mutableStateOf(1) }
    val numbers = remember { (1..10).toList() }
    val numberArabic = remember {
        listOf("واحد", "اثنان", "ثلاثة", "أربعة", "خمسة", "ستة", "سبعة", "ثمانية", "تسعة", "عشرة")
    }
    val cuteCounters = remember {
        listOf("🍎", "🐞", "🌟", "🚗", "🧸", "🍪", "🍭", "⚽", "🐱", "🍌")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(26.dp))
            .border(2.5.dp, Color(0xFF22C55E), RoundedCornerShape(26.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "تعلم الأرقام الذكي 🔢",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF15803D)
        )
        Text(
            text = "اضغط على الأرقام لتتحدث الأرقام بصوتها ونعد العناصر الكيوت معاً!",
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        // Active Large Number Showcase Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF2FBF4)),
            border = BorderStroke(2.5.dp, Color(0xFF4ADE80))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$activeNumber",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF166534)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = numberArabic[activeNumber - 1],
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF22C55E)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Render dynamic counter icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val counterEmoji = cuteCounters[activeNumber - 1]
                    repeat(activeNumber) { index ->
                        Text(
                            text = counterEmoji,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .clickable {
                                    soundManager.playSynthSound("funny")
                                    soundManager.speakDirect("هذه هي العنصر رقم ${index + 1}!")
                                    viewModel.awardQuizStars(1)
                                }
                        )
                    }
                }
            }
        }

        // Grid selection for numbers 1 to 10
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f).padding(vertical = 12.dp)
        ) {
            items(10) { index ->
                val num = numbers[index]
                val isSelected = activeNumber == num
                
                Box(
                    modifier = Modifier
                        .height(55.dp)
                        .background(
                            color = if (isSelected) Color(0xFF22C55E) else Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = if (isSelected) Color(0xFF15803D) else Color.White,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            activeNumber = num
                            soundManager.playSynthSound("funny")
                            val text = "${numberArabic[num - 1]}! لنعد معاً ${num} من ${cuteCounters[num - 1]} السعيد!"
                            soundManager.speakDirect(text)
                            viewModel.awardQuizStars(2)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$num",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color.White else Color(0xFF1B5E20)
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// 3. Alphabet Learning Game Composable (الحروف والكلمات المرحة)
// ----------------------------------------------------
@Composable
fun AlphabetLearningGame(soundManager: CallSoundManager, viewModel: AppViewModel) {
    var activeIndex by remember { mutableIntStateOf(0) }
    
    // Arabic letters and visual examples
    val alphabet = remember {
        listOf(
            Pair("أ", "أرنب 🐰"),
            Pair("ب", "بطة 🦆"),
            Pair("ت", "تفاحة 🍎"),
            Pair("ث", "ثعلب 🦊"),
            Pair("ج", "جمل 🐪"),
            Pair("ح", "حصان 🐴"),
            Pair("خ", "خروف 🐑"),
            Pair("د", "ديك 🐓"),
            Pair("ر", "رمان 🍎"),
            Pair("س", "سمكة 🐟")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(26.dp))
            .border(2.5.dp, Color(0xFF3B82F6), RoundedCornerShape(26.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "تعلم حروف الهجاء العربية 🔤",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1D4ED8)
        )
        Text(
            text = "اسحب بطاقات الحروف أو استخدم الأسهم اللطيفة لتتعلم نطق وصور الحيوانات السعيدة!",
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        // Floating Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clickable {
                    val pair = alphabet[activeIndex]
                    soundManager.playSynthSound("bird")
                    val explanation = "حرف الـ ${pair.first}! ${pair.second}!"
                    soundManager.speakDirect(explanation)
                    viewModel.awardQuizStars(2)
                },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            border = BorderStroke(3.dp, Color(0xFF93C5FD))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large Floating Letter
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, CircleShape)
                        .border(2.dp, Color(0xFF3B82F6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = alphabet[activeIndex].first,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1D4ED8)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = alphabet[activeIndex].second,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "اضغط على البطاقة لتستمع للصوت 🔊",
                    fontSize = 10.sp,
                    color = Color(0xFF3B82F6)
                )
            }
        }

        // Left/Right arrow controllers
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    soundManager.playSynthSound("funny")
                    if (activeIndex > 0) activeIndex-- else activeIndex = alphabet.size - 1
                },
                modifier = Modifier.size(54.dp).background(Color(0xFFDBEAFE), CircleShape).border(2.dp, Color(0xFF3B82F6), CircleShape)
            ) {
                Text("◀️", fontSize = 24.sp)
            }
            
            Text(
                text = "بطاقة الحروف رقم ${activeIndex + 1} من ${alphabet.size}",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Black
            )

            IconButton(
                onClick = {
                    soundManager.playSynthSound("funny")
                    if (activeIndex < alphabet.size - 1) activeIndex++ else activeIndex = 0
                },
                modifier = Modifier.size(54.dp).background(Color(0xFFDBEAFE), CircleShape).border(2.dp, Color(0xFF3B82F6), CircleShape)
            ) {
                Text("▶️", fontSize = 24.sp)
            }
        }
    }
}

// ----------------------------------------------------
// 4. Shape Matching Game Composable (تحدي تطابق الأشكال)
// ----------------------------------------------------
@Composable
fun ShapeMatchingGame(soundManager: CallSoundManager, viewModel: AppViewModel) {
    var score by remember { mutableStateOf(0) }
    
    // Shape models: Name, Emoji Shape, Color Accent
    val shapes = remember {
        listOf(
            Triple("دائرة", "🔴", Color.Red),
            Triple("مربع", "🟦", Color.Blue),
            Triple("مثلث", "🔺", Color.Green),
            Triple("نجمة", "⭐", Color.Yellow),
            Triple("قلب", "🧡", Color.Magenta)
        )
    }

    var targetShapeIndex by remember { mutableStateOf(Random.nextInt(shapes.size)) }
    var alternatives by remember { mutableStateOf(shapes.shuffled()) }

    LaunchedEffect(targetShapeIndex) {
        alternatives = shapes.shuffled()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(26.dp))
            .border(2.5.dp, Color(0xFFEC4899), RoundedCornerShape(26.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "تحدي تطابق الأشكال وبناء الذكاء 🔺",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFBE185D)
        )
        Text(
            text = "أين هو الشكل المطلوب للمطابقة بالأسفل لتفوز بالنجوم؟",
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        // Large target box template
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
            border = BorderStroke(2.5.dp, Color(0xFFFDA4AF))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ابحث عن شكل:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBE185D)
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                
                Text(
                    text = shapes[targetShapeIndex].first,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF9F1239)
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Dash Outline Visual Container
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(Color.White, CircleShape)
                        .border(3.dp, Color(0xFFEC4899), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "❓",
                        fontSize = 42.sp
                    )
                }
            }
        }

        // Tap targets matching row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            alternatives.forEach { shape ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(65.dp)
                        .background(Color(0xFFFFF1F2), RoundedCornerShape(16.dp))
                        .border(2.dp, Color(0xFFFDA4AF), RoundedCornerShape(16.dp))
                        .clickable {
                            if (shape.first == shapes[targetShapeIndex].first) {
                                // Correct!
                                soundManager.playSynthSound("bell")
                                score++
                                soundManager.speakDirect("أحسنت يا ذكي! هذا هو الـ ${shape.first}!")
                                viewModel.awardQuizStars(5)
                                targetShapeIndex = Random.nextInt(shapes.size)
                            } else {
                                // Incorrect
                                soundManager.playSynthSound("funny")
                                soundManager.speakDirect("حاول مرة أخرى يا بطل لتبحث عن ${shapes[targetShapeIndex].first}!")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = shape.second, fontSize = 28.sp)
                        Text(text = shape.first, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 5. Color Recognition Game Composable (تمييز وفن الألوان السحري)
// ----------------------------------------------------
@Composable
fun ColorRecognitionGame(soundManager: CallSoundManager, viewModel: AppViewModel) {
    val colorPots = remember {
        listOf(
            Triple("أحمر", Color(0xFFEF4444), "🔴"),
            Triple("أزرق", Color(0xFF3B82F6), "🔵"),
            Triple("أصفر", Color(0xFFFBBF24), "🟡"),
            Triple("أخضر", Color(0xFF10B981), "🟢"),
            Triple("برتقالي", Color(0xFFF97316), "🟠"),
            Triple("وردي", Color(0xFFEC4899), "🌸")
        )
    }

    var targetColorIndex by remember { mutableStateOf(Random.nextInt(colorPots.size)) }
    var activeBackground by remember { mutableStateOf<Color?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = activeBackground ?: Color.White,
                shape = RoundedCornerShape(26.dp)
            )
            .border(2.5.dp, Color(0xFFF97316), RoundedCornerShape(26.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "تمييز الألوان الكرتوني 🎨",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFEA580C)
        )
        Text(
            text = "أين هو اللون المطلوب؟ اضغط بطل لتصنع سحراً ملوناً!",
            fontSize = 11.sp,
            color = Color(0xFF4B5563),
            textAlign = TextAlign.Center
        )

        // Large rabbit bubble prompt
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
            border = BorderStroke(2.5.dp, Color(0xFFFDBA74))
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🐰 الأرنب السعيد يقول:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFC2410C)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "أرجوك يا بطل.. أين هو اللون:",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = colorPots[targetColorIndex].first,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = colorPots[targetColorIndex].second
                )
            }
        }

        // Color selection grids
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(colorPots.size) { index ->
                val pot = colorPots[index]
                
                Box(
                    modifier = Modifier
                        .height(75.dp)
                        .background(pot.second.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .border(3.dp, pot.second, RoundedCornerShape(20.dp))
                        .clickable {
                            if (index == targetColorIndex) {
                                // Correct!
                                activeBackground = pot.second.copy(alpha = 0.25f)
                                soundManager.playSynthSound("funny")
                                soundManager.speakDirect("ممتاز يا بطل! أحسنت، هذا هو اللون الـ ${pot.first}!")
                                viewModel.awardQuizStars(5)
                                targetColorIndex = Random.nextInt(colorPots.size)
                            } else {
                                // Incorrect
                                soundManager.playSynthSound("funny")
                                soundManager.speakDirect("هذا ليس اللون الـ ${colorPots[targetColorIndex].first}! ركز لتكسب النجوم!")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = pot.third, fontSize = 32.sp)
                        Text(text = pot.first, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 6. Animal Sounds Game Composable (مسار أصوات الحيوانات ممتع)
// ----------------------------------------------------
@Composable
fun AnimalSoundsGame(soundManager: CallSoundManager, viewModel: AppViewModel) {
    val animals = remember {
        listOf(
            Triple("أسد", "🦁", "أسد زئير قوي وجريء!"),
            Triple("كلب", "🐶", "كلب يحرص البيت بنشاط!"),
            Triple("قطة", "🐱", "قطة بريئة تموء بلطف!"),
            Triple("عصفور", "🐤", "عصفور يغرد بحيوية!"),
            Triple("نحلة", "🐝", "نحلة تصنع العسل اللذيذ!"),
            Triple("ديك", "🐓", "ديك يوقظنا لصلاة الصباح!")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(26.dp))
            .border(2.5.dp, Color(0xFF8B5CF6), RoundedCornerShape(26.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "عالم أصوات الحيوانات الحقيقي 🐾",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF6D28D9)
        )
        Text(
            text = "اضغط على أي حيوان لطيف لتستمع لصوته الحقيقي في الطبيعة!",
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        // Lazy vertical grid for Animal items
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f).padding(vertical = 12.dp)
        ) {
            items(animals.size) { index ->
                val animal = animals[index]
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(Color(0xFFF5F3FF), RoundedCornerShape(24.dp))
                        .border(2.5.dp, Color(0xFFDDD6FE), RoundedCornerShape(24.dp))
                        .clickable {
                            soundManager.playKidsRealisticSound(animal.first, animal.third)
                            viewModel.awardQuizStars(4)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = animal.second, fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = animal.first,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF4C1D95)
                        )
                        Text(
                            text = "صوت ودروس 🔊",
                            fontSize = 8.sp,
                            color = Color(0xFF7C3AED)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 7. Puzzle Game Composable (تركيب البازل الذكي)
// ----------------------------------------------------
@Composable
fun PuzzleGame(soundManager: CallSoundManager, viewModel: AppViewModel) {
    // Puzzle slices (Emojis with 90 or 180 deg tilt to rotate)
    var angles by remember { mutableStateOf(listOf(90f, 180f, 270f, 90f)) }
    var solved by remember { mutableStateOf(false) }

    val emojis = remember { listOf("🚓", "👮", "🧸", "🏡") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(26.dp))
            .border(2.5.dp, Color(0xFF0D9488), RoundedCornerShape(26.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "تركيب البازل الكرتوني للأذكياء 🧩",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F766E)
        )
        Text(
            text = "اضغط على القطع لتدير زواياها لكي تتعدل صور الأبطال والسيارات!",
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        if (solved) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFCCFBF1), RoundedCornerShape(16.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎉 أحسنت! تم تعديل وتجميع البازل بنجاح! +10 نجوم",
                    color = Color(0xFF0F766E),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Active puzzle board slots view
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f).padding(vertical = 12.dp)
        ) {
            items(4) { index ->
                val angle = angles[index]
                
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .background(Color(0xFFF0FDFA), RoundedCornerShape(22.dp))
                        .border(
                            width = 3.dp,
                            color = if (angle == 0f) Color(0xFF22C55E) else Color(0xFF2DD4BF),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .clickable {
                            if (solved) return@clickable
                            soundManager.playSynthSound("funny")
                            
                            // Rotate by 90 deg clockwise
                            val currentAngle = angles[index]
                            val newAngle = (currentAngle + 90f) % 360f
                            angles = angles.toMutableList().also { it[index] = newAngle }
                            
                            // Check if all are 0f
                            if (angles.all { it == 0f }) {
                                solved = true
                                soundManager.playSynthSound("bell")
                                soundManager.speakDirect("وااو مذهل يا بطل! لقد حللت كل تراكيب البازل! أنت عبقري جداً اليوم!")
                                viewModel.awardQuizStars(10)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .rotate(angle)
                            // Animation on rotating
                            .animateContentSize()
                    ) {
                        Text(text = emojis[index], fontSize = 48.sp)
                    }
                }
            }
        }

        Button(
            onClick = {
                angles = listOf(90f, 180f, 270f, 90f).shuffled()
                solved = false
                soundManager.speakDirect("تم خلط البازل من جديد! ركّز لتقوم بتعديل الزوايا من فضلك!")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("خلط من جديد 🔄", fontWeight = FontWeight.Bold)
        }
    }
}

// ----------------------------------------------------
// 8. Balloon Pop Game Composable (مهرجان طق البالونات)
// ----------------------------------------------------
data class CustomBalloon(
    val id: Int,
    var xOffset: Float,
    var yOffset: Float,
    val color: Color,
    val text: String,
    var speed: Float,
    var isPopped: Boolean = false
)

@Composable
fun BalloonPopGame(soundManager: CallSoundManager, viewModel: AppViewModel) {
    var score by remember { mutableIntStateOf(0) }
    
    // Initial balloons with randomized starting locations and speeds
    var balloons by remember {
        mutableStateOf(
            List(5) { i ->
                CustomBalloon(
                    id = i,
                    xOffset = Random.nextFloat() * 240f + 10f,
                    yOffset = 500f + (i * 120f),
                    color = listOf(Color.Red, Color.Blue, Color.Green, Color.Magenta, Color(0xFFF59E0B))[i % 5],
                    text = listOf("🎈", "🎈", "🎈", "🎈", "🎈")[i % 5],
                    speed = Random.nextFloat() * 4f + 3f
                )
            }
        )
    }

    // Balloon motion engine
    LaunchedEffect(Unit) {
        while (true) {
            delay(30)
            balloons = balloons.map { balloon ->
                if (balloon.isPopped) {
                    // Re-spawn balloon at the bottom
                    balloon.copy(
                        yOffset = 600f,
                        xOffset = Random.nextFloat() * 240f + 10f,
                        isPopped = false,
                        speed = Random.nextFloat() * 4f + 3f
                    )
                } else {
                    val nextY = balloon.yOffset - balloon.speed
                    if (nextY < -50f) {
                        // Re-cycle if missed of view bounds
                        balloon.copy(
                            yOffset = 600f,
                            xOffset = Random.nextFloat() * 240f + 10f,
                            speed = Random.nextFloat() * 4f + 3f
                        )
                    } else {
                        balloon.copy(yOffset = nextY)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(26.dp))
            .border(2.5.dp, Color(0xFFEF4444), RoundedCornerShape(26.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "مهرجان طق البالونات السعيدة 🎈",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFB91C1C)
            )
            
            Box(
                modifier = Modifier
                    .background(Color(0xFFFEE2E2), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "مفقوع: $score",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444)
                )
            }
        }

        Text(
            text = "تطير البالونات لأعلى! طق البالونات بيدك بسرعة لتسمع صوت الطق وتجمع النقاط البطلة!",
            fontSize = 10.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        // Live Arcade Screen Area with Canvas bounds
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 10.dp)
                .background(Color(0xFFFEF2F2), RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFFCA5A5), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
        ) {
            balloons.forEach { balloon ->
                if (!balloon.isPopped) {
                    Box(
                        modifier = Modifier
                            .offset(x = balloon.xOffset.dp, y = balloon.yOffset.dp)
                            .size(65.dp)
                            .clickable {
                                // Pop sound and animations
                                soundManager.playAudioFromUrl("https://www.soundjay.com/cartoon/sounds/balloon-pop-01.mp3")
                                balloon.isPopped = true
                                score++
                                viewModel.awardQuizStars(2)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = balloon.text,
                            fontSize = 48.sp
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 9. Fruit Catcher Game Composable (سلة فواكه ديدي)
// ----------------------------------------------------
data class CustomFruit(
    val id: Int,
    var xOffset: Float,
    var yOffset: Float,
    val emoji: String,
    val isBomb: Boolean,
    var speed: Float,
    var active: Boolean = true
)

@Composable
fun FruitCatcherGame(soundManager: CallSoundManager, viewModel: AppViewModel) {
    var basketPosition by remember { mutableFloatStateOf(120f) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var gameOver by remember { mutableStateOf(false) }

    // Live fruits array
    var fruitsList by remember {
        mutableStateOf(
            List(4) { i ->
                CustomFruit(
                    id = i,
                    xOffset = Random.nextFloat() * 240f + 10f,
                    yOffset = -50f - (i * 120f),
                    emoji = if (i == 3) "💣" else listOf("🍎", "🍌", "🍓", "🍉")[i % 4],
                    isBomb = i == 3,
                    speed = Random.nextFloat() * 5f + 4f
                )
            }
        )
    }

    // Interactive arcade ticker loop
    LaunchedEffect(gameOver) {
        if (gameOver) return@LaunchedEffect
        while (lives > 0) {
            delay(30)
            fruitsList = fruitsList.map { item ->
                val nextY = item.yOffset + item.speed
                if (nextY > 410f) {
                    if (item.active) {
                        // Check if caught in basket range (e.g. basket position +/- 35.dp)
                        val dx = kotlin.math.abs(item.xOffset - basketPosition)
                        if (dx < 45f) {
                            if (item.isBomb) {
                                lives--
                                soundManager.playSynthSound("funny")
                                if (lives <= 0) {
                                    gameOver = true
                                    soundManager.speakDirect("اللعبة انتهت يا بطل! لقد جمعت $score نقطة!")
                                } else {
                                    soundManager.speakDirect("انتبه من القمبلة!")
                                }
                            } else {
                                score += 10
                                soundManager.playSynthSound("bell")
                                viewModel.awardQuizStars(3)
                            }
                        }
                    }
                    // Recycle fruit
                    CustomFruit(
                        id = item.id,
                        xOffset = Random.nextFloat() * 240f + 10f,
                        yOffset = -30f,
                        emoji = if (Random.nextFloat() < 0.25f) "💣" else listOf("🍎", "🍌", "🍓", "🍉")[Random.nextInt(4)],
                        isBomb = Random.nextFloat() < 0.25f,
                        speed = Random.nextFloat() * 5f + 4f,
                        active = true
                    )
                } else {
                    item.copy(yOffset = nextY)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(26.dp))
            .border(2.5.dp, Color(0xFFF59E0B), RoundedCornerShape(26.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Highscore status bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "سلة الفواكه السعيدة 🍎",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFA16207)
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFEF3C7), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("النقاط: $score", fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFEE2E2), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("القلوب: ${"❤️".repeat(lives)}", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 10.sp)
                }
            }
        }

        if (gameOver) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFEF3C7), RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFFF59E0B), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("انتهت القلوب يا بطل! 🍎", fontWeight = FontWeight.Black, color = Color(0xFFB45309))
                    Text("لقد كسبت نجوماً ذهبية كثيرة في هذه الجولة!", fontSize = 11.sp, color = Color(0xFF92400E))
                }
            }
        }

        // Live interactive play arena
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
                .background(Color(0xFFFFFBEB), RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFFDE68A), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
        ) {
            // Render falling delicious fruits
            fruitsList.forEach { fruit ->
                Box(
                    modifier = Modifier
                        .offset(x = fruit.xOffset.dp, y = fruit.yOffset.dp)
                        .size(45.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = fruit.emoji, fontSize = 32.sp)
                }
            }

            // Render playable basket at bottom (using horizontal slider controller row)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = (basketPosition - 120f).dp, y = (-12).dp)
                    .size(width = 85.dp, height = 55.dp)
                    .background(Color(0xFFD97706), RoundedCornerShape(16.dp))
                    .border(2.5.dp, Color.White, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🧺 سلة ديدي", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }

        // Left / Right touch interactive arrow switches
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { if (basketPosition > 20f && !gameOver) basketPosition -= 25f },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                modifier = Modifier.weight(1f).padding(end = 6.dp)
            ) {
                Text("◀️ لليسار", fontWeight = FontWeight.Bold)
            }
            
            Button(
                onClick = {
                    if (gameOver) {
                        lives = 3
                        score = 0
                        gameOver = false
                        basketPosition = 120f
                        soundManager.speakDirect("لنلعب من جديد! تذكر أن تلتقط الفواكه وتبتعد عن القمبلة!")
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                modifier = Modifier.padding(horizontal = 6.dp)
            ) {
                Text("🔄 تحدي جديد", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { if (basketPosition < 220f && !gameOver) basketPosition += 25f },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                modifier = Modifier.weight(1f).padding(start = 6.dp)
            ) {
                Text("لليمين ▶️", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ----------------------------------------------------
// 10. Superhero Adventure Game Composable (مغامرة البطل الخارق)
// ----------------------------------------------------
data class CustomCoin(
    val id: Int,
    var xOffset: Float,
    var yOffset: Float,
    var active: Boolean = true
)

@Composable
fun SuperheroAdventureGame(soundManager: CallSoundManager, viewModel: AppViewModel) {
    var heroYLocation by remember { mutableFloatStateOf(160f) }
    var score by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(true) }

    // Coins array drifting in from the right edge
    var coins by remember {
        mutableStateOf(
            List(3) { i ->
                CustomCoin(
                    id = i,
                    xOffset = 300f + (i * 140f),
                    yOffset = Random.nextFloat() * 220f + 30f
                )
            }
        )
    }

    // Adventure gravity & coin drift loops ticker math
    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        while (running) {
            delay(35)
            // Constant gravity forcing superhero downwards
            if (heroYLocation < 310f) {
                heroYLocation += 4.5f
            }
            
            // Move coins leftwards
            coins = coins.map { coin ->
                val nextX = coin.xOffset - 6f
                if (nextX < -30f) {
                    // Respawn coin
                    CustomCoin(
                        id = coin.id,
                        xOffset = 320f,
                        yOffset = Random.nextFloat() * 220f + 30f,
                        active = true
                    )
                } else {
                    // Collision check
                    if (coin.active) {
                        val dx = kotlin.math.abs(nextX - 45f)
                        val dy = kotlin.math.abs(coin.yOffset - heroYLocation)
                        if (dx < 35f && dy < 45f) {
                            score += 5
                            soundManager.playSynthSound("bell")
                            viewModel.awardQuizStars(2)
                            coin.copy(active = false)
                        } else {
                            coin.copy(xOffset = nextX)
                        }
                    } else {
                        coin.copy(xOffset = nextX)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(26.dp))
            .border(2.5.dp, Color(0xFF6366F1), RoundedCornerShape(26.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "مغامرة البطل الخارق الطائر 🦸",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF4338CA)
            )
            
            Box(
                modifier = Modifier
                    .background(Color(0xFFE0E7FF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "عملات: $score",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4F46E5)
                )
            }
        }

        // Live physics jump board
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 10.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFEEF2FF), Color(0xFFC7D2FE))
                    ),
                    RoundedCornerShape(24.dp)
                )
                .border(2.dp, Color(0xFF818CF8), RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
        ) {
            // Render Clouds deco
            Text("☁️", fontSize = 36.sp, modifier = Modifier.offset(x = 180.dp, y = 20.dp))
            Text("☁️", fontSize = 28.sp, modifier = Modifier.offset(x = 40.dp, y = 140.dp))

            // Render Flying Super Boy/Girl
            Box(
                modifier = Modifier
                    .offset(x = 25.dp, y = heroYLocation.dp)
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🦸",
                    fontSize = 42.sp,
                    modifier = Modifier.animateContentSize()
                )
            }

            // Render floating active golden coins
            coins.forEach { coin ->
                if (coin.active) {
                    Box(
                        modifier = Modifier
                            .offset(x = coin.xOffset.dp, y = coin.yOffset.dp)
                            .size(28.dp)
                            .background(Color(0xFFFBBF24), CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🪙", fontSize = 14.sp)
                    }
                }
            }
        }

        // Action controllers row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (heroYLocation > 25f) {
                        heroYLocation -= 65f
                        soundManager.playSynthSound("funny")
                    }
                },
                modifier = Modifier.weight(1.5f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "طِر للأعلى وقُص! 🚀",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    score = 0
                    heroYLocation = 160f
                    coins = List(3) { i ->
                        CustomCoin(
                            id = i,
                            xOffset = 300f + (i * 140f),
                            yOffset = Random.nextFloat() * 220f + 30f
                        )
                    }
                    soundManager.speakDirect("انطلق البطل الخارق من جديد ليحلق في السماء السعيدة اليوم!")
                },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("إعادة انطلاق 🔄", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
