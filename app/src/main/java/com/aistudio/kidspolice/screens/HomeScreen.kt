package com.aistudio.kidspolice.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.ui.theme.PoliceBlue
import com.aistudio.kidspolice.ui.theme.PoliceRed

data class BehaviorItem(val title: String, val icon: ImageVector, val isGood: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCallClick: (name: String, gender: String, behavior: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isBoy by remember { mutableStateOf(true) }
    var selectedBehavior by remember { mutableStateOf<BehaviorItem?>(null) }
    var customBehavior by remember { mutableStateOf("") }

    val behaviors = listOf(
        BehaviorItem("لا يريد النوم", Icons.Default.NightlightRound, false),
        BehaviorItem("لا يأكل طعامه", Icons.Default.LocalDining, false),
        BehaviorItem("يلعب بالهاتف كثيراً", Icons.Default.SportsEsports, false),
        BehaviorItem("يضرب إخوته", Icons.Default.Warning, false),
        BehaviorItem("أنهى طعامه", Icons.Default.Favorite, true),
        BehaviorItem("رتب غرفته", Icons.Default.Face, true),
        BehaviorItem("كتب واجباته", Icons.Default.School, true),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("شرطة الأطفال", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PoliceBlue)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("اسم الطفل") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                FilterChip(
                    selected = isBoy,
                    onClick = { isBoy = true },
                    label = { Text("ولد", modifier = Modifier.padding(horizontal = 16.dp)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
                FilterChip(
                    selected = !isBoy,
                    onClick = { isBoy = false },
                    label = { Text("بنت", modifier = Modifier.padding(horizontal = 16.dp)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("اختر السلوك:", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.End))
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(behaviors) { item ->
                    BehaviorCard(
                        item = item,
                        isSelected = selectedBehavior == item,
                        onClick = { 
                            selectedBehavior = item 
                            customBehavior = ""
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = customBehavior,
                onValueChange = { 
                    customBehavior = it
                    if (it.isNotEmpty()) selectedBehavior = null
                },
                label = { Text("أو اكتب سلوكاً آخر...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    val finalBehavior = if (customBehavior.isNotBlank()) customBehavior else selectedBehavior?.title ?: ""
                    onCallClick(
                        name.ifBlank { "الطفل" }, 
                        if (isBoy) "ذكر" else "أنثى", 
                        finalBehavior.ifBlank { "لم يسمع الكلام" }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PoliceRed),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("اتصال", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BehaviorCard(item: BehaviorItem, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PoliceBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon, 
                contentDescription = null,
                tint = if (item.isGood) Color(0xFF388E3C) else PoliceRed,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title, 
                textAlign = TextAlign.Center, 
                fontWeight = FontWeight.Medium,
                color = if (isSelected) PoliceBlue else Color.Unspecified
            )
        }
    }
}
