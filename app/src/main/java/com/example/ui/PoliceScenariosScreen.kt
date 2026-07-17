package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data model representing an interactive behavioral scenario for the kids police
data class PoliceScenario(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val bgGradient: List<Color>,
    val textColor: Color,
    val tagColor: Color,
    val isPositive: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliceScenariosScreen(
    viewModel: AppViewModel,
    onNavigateToCall: (String) -> Unit,
    onBack: () -> Unit
) {
    // List of kid-friendly educational scenarios (both behavioral adjustment & positive reinforcement)
    val scenarios = listOf(
        PoliceScenario(
            id = "police_not_listening",
            title = "عدم سماع الكلام والعناد",
            description = "مكالمة توجيهية هادئة تحفز البطل الصغير على سماع نصائح الماما والبابا بحب.",
            emoji = "🙉",
            bgGradient = listOf(Color(0xFFEFF6FF), Color(0xFFDBEAFE)),
            textColor = Color(0xFF1E3A8A),
            tagColor = Color(0xFF2563EB),
            isPositive = false
        ),
        PoliceScenario(
            id = "police_sleep_late",
            title = "السهر والتأخر في النوم",
            description = "الشرطي سامر يشرح للبطل كيف يعيد النوم المبكر شحن طاقته وقوته الخارقة.",
            emoji = "🌙",
            bgGradient = listOf(Color(0xFFF5F3FF), Color(0xFFEDE9FE)),
            textColor = Color(0xFF5B21B6),
            tagColor = Color(0xFF7C3AED),
            isPositive = false
        ),
        PoliceScenario(
            id = "police_refusing_study",
            title = "التكاسل عن الدراسة والواجبات",
            description = "تشجيع حماسي للبطل لحب العلم والمدرسة ليستكشف المستقبل الباهر.",
            emoji = "📚",
            bgGradient = listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A)),
            textColor = Color(0xFF92400E),
            tagColor = Color(0xFFD97706),
            isPositive = false
        ),
        PoliceScenario(
            id = "police_eating_sweets",
            title = "الإفراط في الحلوى والفرشاة",
            description = "تنبيه لطيف وممتع لحماية الأسنان البراقة كاللؤلؤ والاهتمام بنظافتها وصحتها.",
            emoji = "🍬",
            bgGradient = listOf(Color(0xFFFCE7F3), Color(0xFFFBCFE8)),
            textColor = Color(0xFF9D174D),
            tagColor = Color(0xFFDB2777),
            isPositive = false
        ),
        PoliceScenario(
            id = "police_messy_room",
            title = "ترك الألعاب مبعثرة والفوضى",
            description = "تحدي ممتع لترتيب الألعاب وتنظيم الغرفة ليظل البطل في مملكة جميلة مرتبة.",
            emoji = "🧸",
            bgGradient = listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5)),
            textColor = Color(0xFF065F46),
            tagColor = Color(0xFF059669),
            isPositive = false
        ),
        PoliceScenario(
            id = "police_helping_parents",
            title = "مساعدة الوالدين والبر بهما",
            description = "مكالمة شكر وتقدير عسكرية فخرية للبطل المتعاون والمطيع الذي يسعد قلب عائلته.",
            emoji = "🤝",
            bgGradient = listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7)),
            textColor = Color(0xFF166534),
            tagColor = Color(0xFF16A34A),
            isPositive = true
        ),
        PoliceScenario(
            id = "police_success",
            title = "التميز والنجاح الدراسي",
            description = "تهنئة وتكريم بطل الصف العبقري لتفوقه الباهر وحصوله على علامات ممتازة.",
            emoji = "🏆",
            bgGradient = listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7)),
            textColor = Color(0xFF854D0E),
            tagColor = Color(0xFFCA8A04),
            isPositive = true
        ),
        PoliceScenario(
            id = "police_healthy_food",
            title = "تناول الطعام الصحي القوي",
            description = "تشجيع رائع للبطل الصحي الذي يأكل الفواكه والخضروات المفيدة ليزداد نشاطاً وعافية.",
            emoji = "🥦",
            bgGradient = listOf(Color(0xFFF0FDF4), Color(0xFFD1FAE5)),
            textColor = Color(0xFF065F46),
            tagColor = Color(0xFF10B981),
            isPositive = true
        ),
        PoliceScenario(
            id = "police_monster",
            title = "وحش الأطفال اللطيف",
            description = "مكالمة من وحش الأطفال اللطيف يشجع البطل على الشجاعة والنوم المبكر والترتيب.",
            emoji = "👾",
            bgGradient = listOf(Color(0xFFF5F3FF), Color(0xFFDDD6FE)),
            textColor = Color(0xFF4C1D95),
            tagColor = Color(0xFF8B5CF6),
            isPositive = true
        )
    )

    // Layout configuration forcing RTL (Arabic)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "سيناريوهات الشرطة التفاعلية 👮✨",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E3A8A)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .testTag("scenarios_back_button")
                                .padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = Color(0xFF1E3A8A)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.shadow(2.dp)
                )
            },
            containerColor = Color(0xFFF8FAFC) // Slate 50 background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Intro text banner with beautiful playful gradient card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, Color(0xFF1E3A8A).copy(alpha = 0.1f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF1E3A8A).copy(alpha = 0.04f), Color.White)
                                )
                            )
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "بوابة شرطة الأطفال التوجيهية 🤝",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "اختر السلوك أو الموقف المناسب لطفلك لتبدأ مكالمة تفاعلية هادفة وداعمة ومحفزة للغاية مع الشرطي سامر!",
                            fontSize = 11.5.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Grid containing the scenarios cards
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    items(scenarios) { scenario ->
                        ScenarioCard(
                            scenario = scenario,
                            onClick = {
                                viewModel.resetCallChat()
                                onNavigateToCall(scenario.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScenarioCard(
    scenario: PoliceScenario,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, shape = RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .testTag("scenario_card_${scenario.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = 2.dp,
            color = if (scenario.isPositive) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF3B82F6).copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .background(Brush.verticalGradient(scenario.bgGradient))
                .padding(14.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Emoji and Badge indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type badge label
                Surface(
                    color = scenario.tagColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (scenario.isPositive) "إيجابي 👍" else "توجيهي 👮",
                        color = scenario.tagColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                
                Text(
                    text = scenario.emoji,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Scenario Title
            Text(
                text = scenario.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = scenario.textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Scenario Description
            Text(
                text = scenario.description,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                color = scenario.textColor.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // Action button
            Surface(
                color = scenario.textColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "اتصال الآن 📞",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            }
        }
    }
}
