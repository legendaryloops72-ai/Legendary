package com.aistudio.kidspolice.abcd.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.aistudio.kidspolice.abcd.sound.CallSoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameType { FLIGHT, STRENGTH, SPEED, INTELLIGENCE }

data class SuperHero(
    val id: String,
    val name: String,
    val description: String,
    val color: Color,
    val icon: String,
    val gameType: GameType,
    val starsRequired: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperHeroesScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val soundManager = remember { CallSoundManager(context) }
    val scope = rememberCoroutineScope()

    val heroes = listOf(
        SuperHero("captain_joe", "كابتن جو", "بطل الطيران والتوجيه الذكي!", Color(0xFF3B82F6), "✈️", GameType.FLIGHT),
        SuperHero("qawiya", "قوية", "بطلة القوة والعد الخارق!", Color(0xFFEF4444), "💪", GameType.STRENGTH),
        SuperHero("barq", "البرق", "بطل السرعة والألوان الخاطفة!", Color(0xFFFBBF24), "⚡", GameType.SPEED),
        SuperHero("aqel", "العقل", "بطل الذكاء والترتيب المنطقي!", Color(0xFF8B5CF6), "🧠", GameType.INTELLIGENCE)
    )

    var activeGameHero by remember { mutableStateOf<SuperHero?>(null) }
    var gameResultStars by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("أبطال المدينة", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF1E3A8A))
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6))))
        ) {
            if (activeGameHero == null && !showResult) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.aistudio.kidspolice.abcd.R.drawable.superheroes_banner_1783979147767),
                        contentDescription = "Superheroes Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .padding(bottom = 16.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )

                    Text(
                        "اختر بطلاً وساعده في مهمته!",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(heroes) { _, hero ->
                            HeroCard(hero = hero, onClick = {
                                activeGameHero = hero
                                soundManager.speakDirect("هيا نساعد ${hero.name}")
                            })
                        }
                    }
                }
            } else if (showResult) {
                ResultContent(
                    hero = activeGameHero!!,
                    stars = gameResultStars,
                    message = feedbackMessage,
                    onPlayAgain = {
                        showResult = false
                    },
                    onHome = {
                        activeGameHero = null
                        showResult = false
                    }
                )
            } else {
                // Mini Games
                when (activeGameHero?.gameType) {
                    GameType.FLIGHT -> FlightGame(hero = activeGameHero!!, soundManager = soundManager) { stars, msg ->
                        gameResultStars = stars
                        feedbackMessage = msg
                        viewModel.awardQuizStars(stars * 5)
                        showResult = true
                    }
                    GameType.STRENGTH -> StrengthGame(hero = activeGameHero!!, soundManager = soundManager) { stars, msg ->
                        gameResultStars = stars
                        feedbackMessage = msg
                        viewModel.awardQuizStars(stars * 5)
                        showResult = true
                    }
                    GameType.SPEED -> SpeedGame(hero = activeGameHero!!, soundManager = soundManager) { stars, msg ->
                        gameResultStars = stars
                        feedbackMessage = msg
                        viewModel.awardQuizStars(stars * 5)
                        showResult = true
                    }
                    GameType.INTELLIGENCE -> IntelligenceGame(hero = activeGameHero!!, soundManager = soundManager) { stars, msg ->
                        gameResultStars = stars
                        feedbackMessage = msg
                        viewModel.awardQuizStars(stars * 5)
                        showResult = true
                    }
                    null -> {}
                }
            }
        }
    }
}

@Composable
fun FlightGame(hero: SuperHero, soundManager: CallSoundManager, onComplete: (Int, String) -> Unit) {
    var heroX by remember { mutableStateOf(0f) }
    var heroY by remember { mutableStateOf(0f) }
    val targetX = remember { Random.nextFloat() * 1.6f - 0.8f }
    val targetY = remember { Random.nextFloat() * 1.6f - 0.8f }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("وجّه الكابتن جو للوصول إلى الهدف!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp)
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
        ) {
            // Target
            Text(
                "🎯",
                fontSize = 40.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (targetX * 150).dp, y = (targetY * 150).dp)
            )

            // Hero
            Text(
                hero.icon,
                fontSize = 50.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (heroX * 150).dp, y = (heroY * 150).dp)
            )
        }

        // Controls
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(bottom = 32.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { heroY -= 0.1f }) { Icon(Icons.Default.KeyboardArrowUp, null, tint = Color.White, modifier = Modifier.size(48.dp)) }
                Row {
                    IconButton(onClick = { heroX -= 0.1f }) { Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color.White, modifier = Modifier.size(48.dp)) }
                    Spacer(Modifier.width(48.dp))
                    IconButton(onClick = { heroX += 0.1f }) { Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.White, modifier = Modifier.size(48.dp)) }
                }
                IconButton(onClick = { heroY += 0.1f }) { Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(48.dp)) }
            }
        }

        LaunchedEffect(heroX, heroY) {
            if (kotlin.math.abs(heroX - targetX) < 0.15f && kotlin.math.abs(heroY - targetY) < 0.15f) {
                soundManager.playSynthSound("funny")
                onComplete(3, "أحسنت يا بطل! طيران رائع ومثالي!")
            }
        }
    }
}

@Composable
fun StrengthGame(hero: SuperHero, soundManager: CallSoundManager, onComplete: (Int, String) -> Unit) {
    var targetCount by remember { mutableIntStateOf(5) }
    var currentCount by remember { mutableIntStateOf(0) }
    val boxPositions = remember { mutableStateListOf<androidx.compose.ui.geometry.Offset>() }

    LaunchedEffect(Unit) {
        repeat(targetCount) {
            boxPositions.add(androidx.compose.ui.geometry.Offset(Random.nextFloat() * 0.8f - 0.4f, Random.nextFloat() * 0.8f - 0.4f))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("فرقع الصناديق لتظهر قوة ${hero.name}!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("باقي: ${targetCount - currentCount}", color = Color.White.copy(alpha = 0.7f))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            boxPositions.forEachIndexed { index, offset ->
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (offset.x * 300).dp, y = (offset.y * 300).dp)
                        .size(70.dp)
                        .background(hero.color, RoundedCornerShape(12.dp))
                        .clickable {
                            soundManager.playSynthSound("funny")
                            boxPositions[index] = androidx.compose.ui.geometry.Offset(2000f, 2000f)
                            currentCount++
                            if (currentCount == targetCount) {
                                onComplete(3, "قوة مذهلة! لقد جمعت كل الصناديق!")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("📦", fontSize = 30.sp)
                }
            }
        }
    }
}

@Composable
fun SpeedGame(hero: SuperHero, soundManager: CallSoundManager, onComplete: (Int, String) -> Unit) {
    var timeLeft by remember { mutableIntStateOf(15) }
    var score by remember { mutableIntStateOf(0) }
    val targetColor = remember { if (Random.nextBoolean()) Color.Green else Color.Red }
    val colors = remember { mutableStateListOf<Color>().apply { repeat(12) { add(if (Random.nextBoolean()) Color.Green else Color.Red) } } }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        if (score >= 4) onComplete(2, "عمل رائع وسريع!") else onComplete(1, "محاولة جيدة! كن أسرع في المرة القادمة.")
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("البرق السريع: اضغط اللون ${if (targetColor == Color.Green) "الأخضر" else "الأحمر"}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("الوقت المتبقي: $timeLeft", color = if (timeLeft < 5) Color.Red else Color.White)
        Text("النقاط: $score", color = Color.White.copy(alpha = 0.8f))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(colors.size) { index ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(colors[index])
                        .clickable {
                            if (colors[index] == targetColor) {
                                soundManager.playSynthSound("funny")
                                score++
                                colors[index] = Color.Gray
                                if (score >= 6) onComplete(3, "برق سريع جداً! أنت الأسرع!")
                            } else {
                                soundManager.playSynthSound("funny")
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun IntelligenceGame(hero: SuperHero, soundManager: CallSoundManager, onComplete: (Int, String) -> Unit) {
    val steps = listOf("١. أضع التربة", "٢. أضع البذرة", "٣. أسقيها بالماء")
    var currentStep by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("العقل الذكي: كيف نزرع نبتة؟", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("اضغط الخطوات بالترتيب الصحيح", color = Color.White.copy(alpha = 0.7f))

        Spacer(Modifier.height(48.dp))

        steps.forEachIndexed { index, step ->
            val isCorrect = currentStep > index
            val isCurrent = currentStep == index

            Button(
                onClick = {
                    if (isCurrent) {
                        soundManager.playSynthSound("funny")
                        currentStep++
                        if (currentStep == steps.size) {
                            onComplete(3, "ذكاء خارق! لقد رتبت الخطوات بشكل صحيح!")
                        }
                    } else if (index > currentStep) {
                        soundManager.speakDirect("ركز يا بطل، هذه الخطوة تأتي لاحقاً.")
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).height(70.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCorrect) Color.Green.copy(alpha = 0.5f) else if (isCurrent) Color.White else Color.White.copy(alpha = 0.2f)
                )
            ) {
                Text(step, color = if (isCurrent) Color.Black else Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ResultContent(hero: SuperHero, stars: Int, message: String, onPlayAgain: () -> Unit, onHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎉 مبروك يا بطل! 🎉", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
        Spacer(Modifier.height(16.dp))
        Row {
            repeat(3) { i ->
                Icon(
                    Icons.Default.Star,
                    null,
                    tint = if (i < stars) Color.Yellow else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(50.dp)
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(message, textAlign = TextAlign.Center, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(48.dp))
        Button(onClick = onPlayAgain, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
            Icon(Icons.Default.Refresh, null, tint = hero.color)
            Spacer(Modifier.width(8.dp))
            Text("العب مرة ثانية", color = hero.color, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onHome, modifier = Modifier.fillMaxWidth().height(56.dp), border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)) {
            Text("العودة للأبطال", color = Color.White)
        }
    }
}

@Composable
fun HeroCard(hero: SuperHero, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_anim")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(animation = tween(1500), repeatMode = RepeatMode.Reverse),
        label = "scale"
    )

    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).scale(scale).clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(60.dp).background(hero.color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(hero.icon, fontSize = 32.sp)
            }
            Spacer(Modifier.height(12.dp))
            Text(hero.name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = hero.color, textAlign = TextAlign.Center)
            Text(hero.description, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center, lineHeight = 12.sp)
        }
    }
}
