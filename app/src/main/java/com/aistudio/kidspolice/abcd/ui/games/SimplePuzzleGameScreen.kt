package com.aistudio.kidspolice.abcd.ui.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Card representation for the matching puzzle game
data class PuzzleCard(
    val id: Int,
    val emoji: String,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun SimplePuzzleGameScreen(
    onPointsEarned: (Int) -> Unit,
    playTone: (Float) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Emojis for the cards (Police and Rescue theme)
    val originalEmojis = listOf("🚓", "👮‍♂️", "🚨", "🛡️", "🚁", "👨‍⚕️", "🚒", "🏍️")
    
    var cards by remember { mutableStateOf(generateCards(originalEmojis)) }
    var selectedIndices by remember { mutableStateOf(listOf<Int>()) }
    var moves by remember { mutableStateOf(0) }
    var isCelebrationActive by remember { mutableStateOf(false) }
    
    fun resetGame() {
        cards = generateCards(originalEmojis)
        selectedIndices = emptyList()
        moves = 0
        isCelebrationActive = false
        playTone(600f)
    }

    // Logic for card click
    fun onCardClick(index: Int) {
        if (selectedIndices.size >= 2 || cards[index].isFaceUp || cards[index].isMatched) return

        playTone(400f + index * 40f)

        // Reveal card
        cards = cards.toMutableList().apply {
            this[index] = this[index].copy(isFaceUp = true)
        }
        val currentSelected = selectedIndices + index
        selectedIndices = currentSelected

        if (currentSelected.size == 2) {
            moves++
            val firstIdx = currentSelected[0]
            val secondIdx = currentSelected[1]

            if (cards[firstIdx].emoji == cards[secondIdx].emoji) {
                // Match found!
                coroutineScope.launch {
                    delay(500)
                    cards = cards.toMutableList().apply {
                        this[firstIdx] = this[firstIdx].copy(isMatched = true)
                        this[secondIdx] = this[secondIdx].copy(isMatched = true)
                    }
                    selectedIndices = emptyList()
                    playTone(880f)
                    onPointsEarned(15) // Give rewards
                    
                    // Check if all matched
                    if (cards.all { it.isMatched }) {
                        isCelebrationActive = true
                        playTone(1200f)
                    }
                }
            } else {
                // Not a match
                coroutineScope.launch {
                    delay(1000)
                    cards = cards.toMutableList().apply {
                        this[firstIdx] = this[firstIdx].copy(isFaceUp = false)
                        this[secondIdx] = this[secondIdx].copy(isFaceUp = false)
                    }
                    selectedIndices = emptyList()
                    playTone(250f)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🧩 لعبة مطابقة أبطال الشرطة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "الحركات: $moves",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "طابق بطاقات الشرطة لتكسب النقاط!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // Card Grid
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(cards) { index, card ->
                    val isRevealed = card.isFaceUp || card.isMatched
                    val scale by animateFloatAsState(
                        targetValue = if (isRevealed) 1f else 0.95f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    )

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .scale(scale)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                color = if (card.isMatched) {
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f)
                                } else if (card.isFaceUp) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                            .clickable { onCardClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isRevealed) {
                            Text(
                                text = card.emoji,
                                fontSize = 32.sp
                            )
                        } else {
                            // Badge shape
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Card Back",
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }
            }

            // Celebration Modal/Banner overlay
            if (isCelebrationActive) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "🎉 أحسنت يا بطل! 🎉",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.tertiary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "لقد أكملت اللعبة بنجاح في $moves حركات وحصلت على وسام الشرف والذكاء الخارق!",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, "star", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(36.dp))
                            Icon(Icons.Default.Star, "star", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(48.dp))
                            Icon(Icons.Default.Star, "star", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(36.dp))
                        }
                        Button(
                            onClick = { resetGame() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("العب مرة أخرى 🔄", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // Action controls
        Button(
            onClick = { resetGame() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "إعادة اللعب")
                Text("إعادة خلط البطاقات", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

private fun generateCards(emojis: List<String>): List<PuzzleCard> {
    val duplicated = (emojis + emojis).shuffled()
    return duplicated.mapIndexed { index, emoji ->
        PuzzleCard(id = index, emoji = emoji)
    }
}
