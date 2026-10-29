package com.aistudio.kidspolice.abcd.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

private val GamesNavy = Color(0xFF0B1F46)
private val GamesBlue = Color(0xFF1454A4)

private enum class MiniGame(
    val title: String,
    val description: String,
    val emoji: String,
    val accent: Color
) {
    FIND_CAR("اعثر على سيارة الشرطة", "ابحث عن السيارة بين 9 بطاقات", "🚓", Color(0xFF1976D2)),
    MEMORY("طابق الشارات", "اكشف البطاقات وطابق 6 أزواج", "🛡️", Color(0xFF7B1FA2)),
    PUZZLE("رتّب الشارة", "حرّك القطع لتكمل الترتيب", "🧩", Color(0xFFE17800)),
    STARS("اجمع النجوم", "اجمع أكبر عدد خلال 15 ثانية", "⭐", Color(0xFF2E7D32))
}

@Composable
fun MiniGamesScreen(onBack: () -> Unit) {
    var selectedGame by remember { mutableStateOf<MiniGame?>(null) }

    BackHandler(enabled = selectedGame != null) {
        selectedGame = null
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(GamesNavy, Color(0xFF123B75), Color(0xFFEAF2FF))
                    )
                )
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (selectedGame == null) onBack() else selectedGame = null
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = if (selectedGame == null) "العودة" else "قائمة الألعاب",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedGame?.title ?: "ألعاب الأبطال",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = selectedGame?.description ?: "أربع ألعاب قصيرة وممتعة",
                        color = Color.White.copy(alpha = 0.82f),
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = selectedGame?.emoji ?: "🎮",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center
                )
            }

            if (selectedGame == null) {
                MiniGamesLibrary(
                    modifier = Modifier.weight(1f),
                    onSelectGame = { selectedGame = it }
                )
            } else {
                key(selectedGame) {
                    MiniGameContent(
                        game = selectedGame!!,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniGamesLibrary(
    modifier: Modifier = Modifier,
    onSelectGame: (MiniGame) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "اختر لعبتك وابدأ الجولة",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(MiniGame.values().toList()) { game ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clickable { onSelectGame(game) },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(game.accent.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = game.emoji, fontSize = 30.sp)
                        }
                        Column {
                            Text(
                                text = game.title,
                                color = GamesNavy,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 2
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = game.description,
                                color = Color(0xFF607086),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                        Text(
                            text = "العب الآن ←",
                            color = game.accent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniGameContent(game: MiniGame, modifier: Modifier = Modifier) {
    when (game) {
        MiniGame.FIND_CAR -> FindPoliceCarGame(modifier)
        MiniGame.MEMORY -> BadgeMemoryGame(modifier)
        MiniGame.PUZZLE -> BadgePuzzleGame(modifier)
        MiniGame.STARS -> CollectStarsGame(modifier)
    }
}

@Composable
private fun FindPoliceCarGame(modifier: Modifier = Modifier) {
    var round by remember { mutableIntStateOf(0) }
    val cards = remember(round) {
        val targetIndex = Random.nextInt(9)
        val distractors = listOf("🚑", "🚒", "🚕", "🚦", "📻", "🔦", "⭐", "🛡️")
        List(9) { index ->
            if (index == targetIndex) "🚓" else distractors[index.coerceAtMost(distractors.lastIndex)]
        }
    }
    var found by remember(round) { mutableStateOf(false) }
    var feedback by remember(round) { mutableStateOf("اضغط على سيارة الشرطة") }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameStatusCard(
            title = if (found) "أحسنت! عثرت عليها" else "أين سيارة الشرطة؟",
            subtitle = feedback
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(cards) { _, emoji ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(82.dp)
                        .clickable(enabled = !found) {
                            if (emoji == "🚓") {
                                found = true
                                feedback = "رائع! وجدت السيارة الصحيحة."
                            } else {
                                feedback = "ليست هي، حاول مرة أخرى."
                            }
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (found && emoji == "🚓") Color(0xFFDFF4E5) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = emoji, fontSize = 36.sp)
                    }
                }
            }
        }
        GameActionButton(
            text = "جولة جديدة",
            onClick = { round++ },
            enabled = found
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun BadgeMemoryGame(modifier: Modifier = Modifier) {
    var round by remember { mutableIntStateOf(0) }
    val symbols = remember(round) {
        listOf("🚓", "🛡️", "🚨", "⭐", "📻", "🏅")
            .flatMap { listOf(it, it) }
            .shuffled()
    }
    var revealed by remember(round) { mutableStateOf(emptyList<Int>()) }
    var matched by remember(round) { mutableStateOf(emptySet<Int>()) }
    var moves by remember(round) { mutableIntStateOf(0) }
    var isWaiting by remember(round) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameStatusCard(
            title = "الأزواج: ${matched.size / 2} من 6",
            subtitle = "المحاولات: $moves"
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            itemsIndexed(symbols) { index, symbol ->
                val isVisible = index in revealed || index in matched
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(74.dp)
                        .clickable(enabled = !isWaiting && !isVisible) {
                            if (revealed.isEmpty()) {
                                revealed = listOf(index)
                            } else {
                                val first = revealed.first()
                                revealed = listOf(first, index)
                                moves++
                                if (symbols[first] == symbol) {
                                    matched = matched + first + index
                                    revealed = emptyList()
                                } else {
                                    isWaiting = true
                                    scope.launch {
                                        delay(700)
                                        revealed = emptyList()
                                        isWaiting = false
                                    }
                                }
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isVisible) Color(0xFFE7F0FF) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isVisible) symbol else "؟",
                            color = GamesNavy,
                            fontSize = if (isVisible) 31.sp else 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
        if (matched.size == symbols.size) {
            Text(
                text = "أكملت كل الأزواج!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        GameActionButton(text = "ابدأ من جديد", onClick = { round++ })
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun BadgePuzzleGame(modifier: Modifier = Modifier) {
    var round by remember { mutableIntStateOf(0) }
    var tiles by remember(round) { mutableStateOf(shufflePuzzle()) }
    var moves by remember(round) { mutableIntStateOf(0) }
    val solved = tiles == listOf(1, 2, 3, 4, 5, 6, 7, 8, 0)

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameStatusCard(
            title = if (solved) "اكتمل الترتيب!" else "رتّب الأرقام من 1 إلى 8",
            subtitle = "الحركات: $moves • اضغط قطعة بجوار الفراغ"
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(vertical = 14.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(tiles) { index, value ->
                val emptyIndex = tiles.indexOf(0)
                val canMove = value != 0 && areAdjacent(index, emptyIndex) && !solved
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(82.dp)
                        .clickable(enabled = canMove) {
                            val updated = tiles.toMutableList()
                            updated[emptyIndex] = updated[index]
                            updated[index] = 0
                            tiles = updated
                            moves++
                        },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (value == 0) Color.White.copy(alpha = 0.2f) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (value == 0) 0.dp else 3.dp)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (value != 0) {
                            Text(
                                text = value.toString(),
                                color = GamesNavy,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
        GameActionButton(text = "خلط جديد", onClick = { round++ })
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun CollectStarsGame(modifier: Modifier = Modifier) {
    var round by remember { mutableIntStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableIntStateOf(15) }
    var score by remember { mutableIntStateOf(0) }

    LaunchedEffect(round, isRunning) {
        if (isRunning) {
            repeat(15) {
                delay(1_000)
                if (!isRunning) return@LaunchedEffect
                secondsLeft = (secondsLeft - 1).coerceAtLeast(0)
            }
            isRunning = false
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        GameStatusCard(
            title = "الوقت: $secondsLeft ثانية",
            subtitle = "نجومك: $score"
        )
        Button(
            onClick = { if (isRunning) score++ },
            enabled = isRunning,
            modifier = Modifier
                .size(190.dp)
                .padding(8.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFC928),
                disabledContainerColor = Color(0xFFFFE69A)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text(text = "⭐", fontSize = 72.sp)
        }
        Text(
            text = if (isRunning) "اضغط النجمة بسرعة!" else "ابدأ الجولة، ثم اجمع النجوم",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        GameActionButton(
            text = if (isRunning) "الجولة جارية…" else "ابدأ جولة جديدة",
            onClick = {
                if (!isRunning) {
                    score = 0
                    secondsLeft = 15
                    round++
                    isRunning = true
                }
            },
            enabled = !isRunning
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun GameStatusCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = GamesNavy,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = subtitle,
                color = Color(0xFF53627A),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GameActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GamesBlue,
            disabledContainerColor = GamesBlue.copy(alpha = 0.45f)
        )
    ) {
        Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

private fun shufflePuzzle(): List<Int> {
    val tiles = (1..8).toMutableList().apply { add(0) }
    var previousEmptyIndex = -1
    repeat(36) {
        val emptyIndex = tiles.indexOf(0)
        val choices = (0 until 9)
            .filter { areAdjacent(it, emptyIndex) && it != previousEmptyIndex }
            .ifEmpty { (0 until 9).filter { areAdjacent(it, emptyIndex) } }
        val nextIndex = choices[Random.nextInt(choices.size)]
        tiles[emptyIndex] = tiles[nextIndex]
        tiles[nextIndex] = 0
        previousEmptyIndex = emptyIndex
    }
    return tiles
}

private fun areAdjacent(first: Int, second: Int): Boolean {
    val sameRow = first / 3 == second / 3 && abs(first % 3 - second % 3) == 1
    val sameColumn = first % 3 == second % 3 && abs(first / 3 - second / 3) == 1
    return sameRow || sameColumn
}
