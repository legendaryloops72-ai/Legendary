package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CallCharacterItem(
    val id: String,
    val name: String,
    val description: String,
    val icon: @Composable (Modifier) -> Unit,
    val bgGradient: List<Color>,
    val accentColor: Color,
    val type: String // "direct" or "scenarios"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallHubScreen(
    onNavigateToPoliceScenarios: () -> Unit,
    onNavigateToCall: (String) -> Unit,
    onBack: () -> Unit
) {
    val characters = listOf(
        CallCharacterItem(
            id = "police",
            name = "الشرطي سامر",
            description = "مكالمات توجيهية وإيجابية",
            icon = { IraqiPoliceIllustration(it) },
            bgGradient = listOf(Color(0xFFEFF6FF), Color(0xFFDBEAFE)),
            accentColor = Color(0xFF1E3A8A),
            type = "scenarios"
        ),
        CallCharacterItem(
            id = "doctor",
            name = "الدكتور طيب",
            description = "نصائح للصحة والأسنان",
            icon = { FriendlyDoctorIllustration(it) },
            bgGradient = listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5)),
            accentColor = Color(0xFF059669),
            type = "direct"
        ),
        CallCharacterItem(
            id = "teacher",
            name = "الأستاذ منير",
            description = "تشجيع على العلم والدراسة",
            icon = { SmartTeacherIllustration(it) },
            bgGradient = listOf(Color(0xFFFFF7ED), Color(0xFFFED7AA)),
            accentColor = Color(0xFFEA580C),
            type = "direct"
        ),
        CallCharacterItem(
            id = "monster",
            name = "الوحش اللطيف",
            description = "مكالمات ممتعة ومشجعة",
            icon = { FriendlyMonsterIllustration(it) },
            bgGradient = listOf(Color(0xFFF5F3FF), Color(0xFFEDE9FE)),
            accentColor = Color(0xFF7C3AED),
            type = "direct"
        ),
        CallCharacterItem(
            id = "principal",
            name = "المدير عادل",
            description = "تكريم النجاح والتميز",
            icon = { SchoolPrincipalIllustration(it) },
            bgGradient = listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0)),
            accentColor = Color(0xFF334155),
            type = "direct"
        )
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "مركز المكالمات التفاعلية 📞",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E3A8A)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = Color(0xFF1E3A8A)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = Color(0xFFF8FAFC)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "اختر الشخصية التي ترغب في التحدث معها:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(characters) { character ->
                        CallHubCard(character) {
                            if (character.type == "scenarios") {
                                onNavigateToPoliceScenarios()
                            } else {
                                onNavigateToCall(character.id)
                            }
                        }
                    }
                }

                AdBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun CallHubCard(
    character: CallCharacterItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .shadow(2.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(character.bgGradient))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .shadow(1.dp, CircleShape)
                    .background(Color.White, CircleShape)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                character.icon(Modifier.fillMaxSize().clip(CircleShape))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = character.accentColor
                )
                Text(
                    text = character.description,
                    fontSize = 12.sp,
                    color = character.accentColor.copy(alpha = 0.7f)
                )
            }

            Surface(
                color = character.accentColor,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "اتصل الآن",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
