package com.aistudio.kidspolice.abcd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.ads.AdBanner
import com.aistudio.kidspolice.abcd.ui.AppViewModel
import com.aistudio.kidspolice.abcd.ui.theme.PoliceBlue
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGold
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGreen
import com.aistudio.kidspolice.abcd.ui.theme.PoliceNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val rankTitle = when {
        uiState.totalScore >= 300 -> "لواء شرطة الشرف والأخلاق"
        uiState.totalScore >= 200 -> "عميد أبطال المستقبل"
        uiState.totalScore >= 150 -> "نقيب الأبطال المتميزين"
        else -> "ملازم أول الشجاعة"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("شهادة تقدير ووسام الشرف", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Certificate Ornamental Frame Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(4.dp, PoliceGold, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(PoliceNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = PoliceGold,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "القيادة العامة لشرطة الأطفال",
                        color = PoliceBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = "⭐ شهادة وسام التميز والشجاعة ⭐",
                        color = Color(0xFFB8860B),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "تمنح شرطة الأطفال هذا الوسام الفخري للبطل:",
                        color = Color.DarkGray,
                        fontSize = 14.sp
                    )

                    Text(
                        text = uiState.childName,
                        color = PoliceBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Text(
                        text = "تقديراً لالتزامه بالأخلاق الحميدة وطاعة الوالدين وإنجاز المهام التربوية واليومية بنجاح واقتدار.",
                        color = Color.DarkGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PoliceGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "الرتبة الحالية: $rankTitle",
                            color = PoliceGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "رصيد النقاط", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "${uiState.totalScore} نقطة", fontWeight = FontWeight.Bold, color = PoliceGold, fontSize = 16.sp)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "ختم الاعتماد", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "👮‍♂️ معتمد رسمي", fontWeight = FontWeight.Bold, color = PoliceBlue, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
