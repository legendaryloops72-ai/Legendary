package com.aistudio.kidspolice.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.aistudio.kidspolice.ui.theme.PoliceBlue
import com.aistudio.kidspolice.ui.theme.PoliceRed

@Composable
fun CallScreen(
    isIncoming: Boolean,
    onAnswer: () -> Unit,
    onEndCall: () -> Unit
) {
    var seconds by remember { mutableStateOf(0) }

    LaunchedEffect(isIncoming) {
        if (!isIncoming) {
            while (true) {
                delay(1000)
                seconds++
            }
        }
    }

    val formatTime = { s: Int ->
        val m = s / 60
        val sec = s % 60
        String.format("%02d:%02d", m, sec)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        
        Text(
            text = "شرطة الأطفال",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (isIncoming) "مكالمة واردة..." else formatTime(seconds),
            color = Color.LightGray,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .background(PoliceBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            if (isIncoming) {
                CallButton(
                    icon = Icons.Default.CallEnd,
                    color = PoliceRed,
                    onClick = onEndCall,
                    animate = false
                )
                
                CallButton(
                    icon = Icons.Default.Call,
                    color = Color(0xFF4CAF50),
                    onClick = onAnswer,
                    animate = true
                )
            } else {
                CallButton(
                    icon = Icons.Default.CallEnd,
                    color = PoliceRed,
                    onClick = onEndCall,
                    animate = false
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun CallButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    animate: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (animate) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(72.dp)
            .scale(scale)
            .background(color, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }
}
