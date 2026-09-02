package com.aistudio.kidspolice.abcd.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.R
import com.aistudio.kidspolice.abcd.ui.components.PoliceSirenLightBar
import com.aistudio.kidspolice.abcd.ui.theme.PoliceAccentCyan
import com.aistudio.kidspolice.abcd.ui.theme.PoliceCardBg
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGold
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGreen
import com.aistudio.kidspolice.abcd.ui.theme.PoliceNavy

@Composable
fun CertificateScreen(
    userScore: Int,
    onBack: () -> Unit
) {
    var heroName by remember { mutableStateOf("البطل المتميز") }

    Column(
        modifier = Modifier.fillMaxSize().background(PoliceNavy).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(42.dp).clip(CircleShape).background(PoliceCardBg).clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "العودة", tint = PoliceNavy)
            }
            Spacer(modifier = Modifier.size(12.dp))
            Text("شهادة شرف ورتبة الضابط الصغير", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }

        PoliceSirenLightBar(isFlashing = true)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = heroName,
            onValueChange = { heroName = it },
            label = { Text("اكتب اسم البطل على الشهادة", color = PoliceAccentCyan) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PoliceGold,
                unfocusedBorderColor = PoliceCardBg,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B36)),
            border = androidx.compose.foundation.BorderStroke(2.5.dp, PoliceGold)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(82.dp).clip(CircleShape).background(PoliceCardBg),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_police_officer_hero),
                        contentDescription = "ضابط الشرطة الصغير",
                        modifier = Modifier.size(78.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("القيادة العامة لشرطة الأطفال", color = PoliceGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("شهادة تقدير ووسام الشجاعة", color = PoliceAccentCyan, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(14.dp))
                Text("تمنح هذه الشهادة الفخرية إلى البطل:", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(heroName.ifBlank { "البطل المتميز" }, color = PoliceGold, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "تقديراً لحسن السلوك، وسماع كلام الوالدين، والنوم المبكر، والإنجاز الممتاز في مهام البطولة اليومية برصيد ($userScore) نقطة.",
                    color = Color.White,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = PoliceGreen, modifier = Modifier.size(24.dp))
                        Text("رتبة البطل", color = PoliceGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("ملازم شرفي", color = Color.White, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = PoliceGold, modifier = Modifier.size(24.dp))
                        Text("التوقيع والاعتماد", color = PoliceGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("قائد شرطة الأطفال", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
