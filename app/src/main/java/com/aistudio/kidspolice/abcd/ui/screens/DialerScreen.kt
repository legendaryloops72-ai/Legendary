package com.aistudio.kidspolice.abcd.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.ads.AdBanner
import com.aistudio.kidspolice.abcd.ads.AdManager
import com.aistudio.kidspolice.abcd.data.PoliceRepository
import com.aistudio.kidspolice.abcd.data.PoliceScenario
import com.aistudio.kidspolice.abcd.ui.AppViewModel
import com.aistudio.kidspolice.abcd.ui.theme.PoliceBlue
import com.aistudio.kidspolice.abcd.ui.theme.PoliceCrimson
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGold
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialerScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onStartCall: (PoliceScenario) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("لوحة طلب الشرطة", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PoliceBlue)
            )
        },
        bottomBar = {
            AdBanner()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Number Display Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = uiState.customDialNumber.ifEmpty { "اطلب رقم الشرطة (مثال: 999)" },
                        fontSize = if (uiState.customDialNumber.isEmpty()) 16.sp else 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.customDialNumber.isEmpty()) Color.Gray else PoliceBlue
                    )

                    if (uiState.customDialNumber.isNotEmpty()) {
                        IconButton(onClick = { viewModel.deleteDialNumber() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "حذف رقم",
                                tint = PoliceCrimson
                            )
                        }
                    }
                }
            }

            // Fast Emergency Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("999 (الشرطة)", "911 (الطوارئ)", "112 (النجدة)").forEach { numText ->
                    val num = numText.substringBefore(" ")
                    Button(
                        onClick = {
                            viewModel.clearDialNumber()
                            num.forEach { ch -> viewModel.appendDialNumber(ch.toString()) }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PoliceBlue.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = numText,
                            fontSize = 11.sp,
                            color = PoliceBlue,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            // Keypad Grid
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("*", "0", "#")
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                keys.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { digit ->
                            KeypadButton(
                                digit = digit,
                                onClick = { viewModel.appendDialNumber(digit) }
                            )
                        }
                    }
                }
            }

            // Big Call Button
            Button(
                onClick = {
                    val defaultScenario = PoliceRepository.scenarios.firstOrNull {
                        it.dialect == uiState.selectedDialect
                    } ?: PoliceRepository.scenarios.first()

                    if (activity != null) {
                        AdManager.showInterstitial(activity) {
                            viewModel.startOutgoingCall(defaultScenario)
                            onStartCall(defaultScenario)
                        }
                    } else {
                        viewModel.startOutgoingCall(defaultScenario)
                        onStartCall(defaultScenario)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PoliceGreen)
            ) {
                Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "اتصال بالدورية",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun KeypadButton(
    digit: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = PoliceBlue,
            textAlign = TextAlign.Center
        )
    }
}
