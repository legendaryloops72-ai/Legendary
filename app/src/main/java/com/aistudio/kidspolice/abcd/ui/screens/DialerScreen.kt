package com.aistudio.kidspolice.abcd.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.data.Dialect
import com.aistudio.kidspolice.abcd.data.PoliceScenario
import com.aistudio.kidspolice.abcd.data.ScenarioCategory
import com.aistudio.kidspolice.abcd.ui.components.PoliceSirenLightBar
import com.aistudio.kidspolice.abcd.ui.theme.PoliceAccentCyan
import com.aistudio.kidspolice.abcd.ui.theme.PoliceCardBg
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGold
import com.aistudio.kidspolice.abcd.ui.theme.PoliceGreen
import com.aistudio.kidspolice.abcd.ui.theme.PoliceNavy

@Composable
fun DialerScreen(
    selectedDialect: Dialect,
    onStartCustomCall: (PoliceScenario) -> Unit,
    onBack: () -> Unit
) {
    var dialNumber by remember { mutableStateOf("") }
    var childName by remember { mutableStateOf("") }

    val digits = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("*", "0", "#")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PoliceNavy)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PoliceCardBg)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "➡️", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "طلب رقم طوارئ الأطفال 999",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        PoliceSirenLightBar(isFlashing = true)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = childName,
            onValueChange = { childName = it },
            label = { Text("اسم البطل / البطلة (اختياري)", color = PoliceAccentCyan) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PoliceGold,
                unfocusedBorderColor = PoliceCardBg,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PoliceCardBg)
                .border(1.dp, PoliceGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (dialNumber.isEmpty()) "999 شرطة الأطفال" else dialNumber,
                color = if (dialNumber.isEmpty()) Color.White.copy(alpha = 0.5f) else PoliceGold,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in digits) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (digit in row) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(PoliceCardBg)
                                .border(1.dp, PoliceAccentCyan.copy(alpha = 0.3f), CircleShape)
                                .clickable {
                                    if (dialNumber.length < 10) dialNumber += digit
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digit,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(PoliceCardBg)
                    .clickable {
                        if (dialNumber.isNotEmpty()) {
                            dialNumber = dialNumber.dropLast(1)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⌫", color = Color.White, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(28.dp))

            Button(
                onClick = {
                    val namePart = if (childName.isNotBlank()) "يا $childName " else "يا بطل "
                    val customScenario = PoliceScenario(
                        id = "custom_call",
                        title = "نداء طوارئ مخصص",
                        subtitle = "اتصال مباشر مع غرفة العمليات",
                        iconEmoji = "🚨",
                        category = ScenarioCategory.BEHAVIOR,
                        officerName = "غرفة العمليات المركزية",
                        responsesByDialect = mapOf(
                            Dialect.SAUDI to "السلام عليكم ${namePart}معك مركز شرطة الأطفال. تم استلام نداءك، ونحن دائماً في خدمتك لدعمك لتكون أفضل وأقوى بطل في الوطن!",
                            Dialect.EGYPTIAN to "ألو ${namePart}معاك العمليات في شرطة الأطفال. إحنا جنبك وسامعينك، خليك دايماً شاطر وممتاز وبنحبك جداً!",
                            Dialect.SYRIAN to "مرحبا ${namePart}معك شرطة الأطفال. نحن معك خطوة بخطوة حتى تضل بطلنا الشاطر والمؤدب دايماً!",
                            Dialect.GULF to "يا هلا ${namePart}معاك القيادة العامة. عساك على القوة دوم ونحن فخورين فيك يا بطل!",
                            Dialect.IRAQI to "هلو ${namePart}وياك غرفة العمليات. نحييك ونريدك دايماً شجاع ومبدع يا وردة!",
                            Dialect.MOROCCAN to "السلام ${namePart}معاك مركز الشرطة. تبارك الله عليك ونتمناو ليك ديما التوفيق والنجاح!",
                            Dialect.ALGERIAN to "سلام ${namePart}معاك مركز الشرطة. راك هايل وخليك دايماً متألق وشاطر!",
                            Dialect.FASHA to "السلام عليكم ${namePart}معكم غرفة القيادة والسيطرة بشرطة الأطفال. نحييكم ونتمنى لكم التوفيق والتميز دائماً."
                        )
                    )
                    onStartCustomCall(customScenario)
                },
                modifier = Modifier
                    .height(60.dp)
                    .width(150.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PoliceGreen),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(text = "اتصال 📞", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
