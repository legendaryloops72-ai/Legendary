package com.aistudio.kidspolice.abcd.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.ui.theme.PoliceBlue
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGold
import com.aistudio.kidspolice.abcd.ui.theme.PoliceRed
import kotlinx.coroutines.delay

@Composable
fun PoliceSirenLightBar(
    isFlashing: Boolean = true,
    modifier: Modifier = Modifier
) {
    var isRedActive by remember { mutableStateOf(true) }

    LaunchedEffect(isFlashing) {
        if (isFlashing) {
            while (true) {
                delay(280)
                isRedActive = !isRedActive
            }
        }
    }

    val redLightColor by animateColorAsState(
        targetValue = if (isFlashing && isRedActive) PoliceRed else PoliceRed.copy(alpha = 0.2f),
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "red_siren_anim"
    )

    val blueLightColor by animateColorAsState(
        targetValue = if (isFlashing && !isRedActive) PoliceBlue else PoliceBlue.copy(alpha = 0.2f),
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "blue_siren_anim"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Red light side
        Box(
            modifier = Modifier
                .weight(1f)
                .height(28.dp)
                .shadow(if (isFlashing && isRedActive) 10.dp else 2.dp, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(redLightColor)
                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🚨", fontSize = 14.sp)
        }

        // Center badge
        Box(
            modifier = Modifier
                .height(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF0F2042))
                .border(1.dp, PoliceGold, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "POLICE",
                color = PoliceGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Blue light side
        Box(
            modifier = Modifier
                .weight(1f)
                .height(28.dp)
                .shadow(if (isFlashing && !isRedActive) 10.dp else 2.dp, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(blueLightColor)
                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⚡", fontSize = 14.sp)
        }
    }
}
