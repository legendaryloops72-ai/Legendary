package com.aistudio.kidspolice.abcd.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aistudio.kidspolice.abcd.sound.CallSoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// Hero Data Model
data class Superhero(
    val id: Int,
    val name: String,
    val iconEmoji: String,
    val category: String, // "الطبيعة", "التكنولوجيا", "الفضاء", "القوى الخارقة"
    val powerName: String,
    val animType: String, // "jump", "fly", "spin", "dash", "shield", "laser"
    val accentColor: Color,
    val voiceScript: String,
    val forceMultiplier: Int,
    val unlockStarsRequired: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroesUniverseScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val totalStars = profile?.totalStars ?: 15
    val childName = profile?.name ?: "البطل"

    // Sound Manager Instantiation for Live Arabic Voice Description
    val callSoundManager = remember { CallSoundManager(context) }

    // Particle state for golden floating coins/stars
    var particles by remember { mutableStateOf<List<StarParticle>>(emptyList()) }

    // 20 Custom Superheroes list
    val allHeroes = remember {
        listOf(
            Superhero(
                id = 1,
                name = "بطل العنكبوت",
                iconEmoji = "🕷️🕸️",
                category = "القوى الخارقة",
                powerName = "القفز وإطلاق شباك الحماية",
                animType = "jump",
                accentColor = Color(0xFFEF4444),
                voiceScript = "بطل العنكبوت الرائع يقفز عالياً بين البنايات الكرتونية ويطلق خيوط العنكبوت القوية لينقذ الجميع من المشاكل! أنت بطل مثله يا %s!",
                forceMultiplier = 95,
                unlockStarsRequired = 0
            ),
            Superhero(
                id = 2,
                name = "فارس الليل",
                iconEmoji = "🦇🌌",
                category = "القوى الخارقة",
                powerName = "الظهور وحماية الأطفال النيام",
                animType = "spin",
                accentColor = Color(0xFF1E293B),
                voiceScript = "فارس الليل يظهر بثقة من الظلال، يرتدي رداءه الأنيق ويحمي غرف الأطفال الجميلة لكي ينام الجميع بأمان وسلام! أحسنت يا %s!",
                forceMultiplier = 92,
                unlockStarsRequired = 0
            ),
            Superhero(
                id = 3,
                name = "الرجل الطائر",
                iconEmoji = "🦅⚡",
                category = "الفضاء",
                powerName = "الطيران السحابي السريع وزر الليزر",
                animType = "fly",
                accentColor = Color(0xFF3B82F6),
                voiceScript = "الرجل الطائر يحلق في أعالي السماء الزرقاء، بين السحب، ويطلق أشعة السلام لحماية بيئتنا الجميلة! طيران بطل ورائع!",
                forceMultiplier = 98,
                unlockStarsRequired = 0
            ),
            Superhero(
                id = 4,
                name = "النمر السريع",
                iconEmoji = "🐆💨",
                category = "الطبيعة",
                powerName = "الجري بسرعة البرق لحل الأزمات",
                animType = "dash",
                accentColor = Color(0xFFF59E0B),
                voiceScript = "النمر السريع يجري كالسهم الخارق في الملاعب والوديان لينقذ الحيوانات الضعيفة ويعلمنا النشاط الصباحي والرياضة!",
                forceMultiplier = 90,
                unlockStarsRequired = 0
            ),
            Superhero(
                id = 5,
                name = "روبو الحماية",
                iconEmoji = "🤖🛡️",
                category = "التكنولوجيا",
                powerName = "حقل الطاقة الإلكتروني المضيء",
                animType = "shield",
                accentColor = Color(0xFF10B981),
                voiceScript = "روبو الحماية الذكي يبني جداراً مضيئاً قوياً ليحمينا من مخاطر الكهرباء في البيت ويعلمنا الالتزام بالسلامة!",
                forceMultiplier = 88,
                unlockStarsRequired = 0
            ),
            Superhero(
                id = 6,
                name = "الفهد الأزرق",
                iconEmoji = "🐾💎",
                category = "الطبيعة",
                powerName = "الجري والقفز المزدوج السريع",
                animType = "dash",
                accentColor = Color(0xFF06B6D4),
                voiceScript = "الفهد الأزرق الخارق يقفز فوق العقبات بمرح ويعلم الأطفال الذكاء والسرعة في إنجاز الواجبات اليومية!",
                forceMultiplier = 89,
                unlockStarsRequired = 10
            ),
            Superhero(
                id = 7,
                name = "أمير البرق",
                iconEmoji = "⚡👑",
                category = "الفضاء",
                powerName = "ومضات الطاقة الكهربائية الصديقة",
                animType = "laser",
                accentColor = Color(0xFFEAB308),
                voiceScript = "أمير البرق يلمع في السماء ليضيء المدن المعتمة بطاقة نظيفة، ويشجع الأطفال على حب العلوم والاختراعات المفيدة!",
                forceMultiplier = 96,
                unlockStarsRequired = 15
            ),
            Superhero(
                id = 8,
                name = "قائد النار",
                iconEmoji = "🔥🧑‍🚒",
                category = "الطبيعة",
                powerName = "المشي الناري وصاروخ الدفء",
                animType = "jump",
                accentColor = Color(0xFFF97316),
                voiceScript = "قائد النار البراق يتحكم بالنيران بلطف ليصنع لنا الدفء ويساعد طواقم الإطفاء في توجيه الأطفال وتجنب اللعب بالكبريت!",
                forceMultiplier = 91,
                unlockStarsRequired = 20
            ),
            Superhero(
                id = 9,
                name = "بطل الجليد",
                iconEmoji = "❄️🏔️",
                category = "الطبيعة",
                powerName = "تجميد الصغائر وصنع كرات المرح والثلج",
                animType = "shield",
                accentColor = Color(0xFF38BDF8),
                voiceScript = "بطل الجليد يصنع زلاقات ثلجية ممتعة، ويهدئ الأجواء الغاضبة بلمسة باردة منعشة مليئة بالهدوء والسلام!",
                forceMultiplier = 87,
                unlockStarsRequired = 25
            ),
            Superhero(
                id = 10,
                name = "محارب الفضاء",
                iconEmoji = "🧑‍🚀🌌",
                category = "الفضاء",
                powerName = "الطيران العائم وضباب المجرات",
                animType = "fly",
                accentColor = Color(0xFF8B5CF6),
                voiceScript = "محارب الفضاء يستكشف الأقمار البعيدة بمركبته الذكية، ويهدي الأطفال نجوماً مضيئة تشجعهم على التفكير والنجاح الباهر!",
                forceMultiplier = 94,
                unlockStarsRequired = 30
            ),
            Superhero(
                id = 11,
                name = "الحارس الذهبي",
                iconEmoji = "🦁🔱",
                category = "التكنولوجيا",
                powerName = "الدرع المشع والأمان الذهبي",
                animType = "shield",
                accentColor = Color(0xFFD97706),
                voiceScript = "الحارس الذهبي يمسك بدرعه الشمسي اللامع، ناشراً طاقة التفاؤل والود وحماية بيوتنا من كل فوضى!",
                forceMultiplier = 93,
                unlockStarsRequired = 35
            ),
            Superhero(
                id = 12,
                name = "بطل الظلال",
                iconEmoji = "🥷🌀",
                category = "القوى الخارقة",
                powerName = "سرعة الوميض والانتقال السحري",
                animType = "dash",
                accentColor = Color(0xFF475569),
                voiceScript = "بطل الظلال اللطيف يتنقل كالبرق لإرجاع الألعاب المفقودة وترتيب البيوت في ثوانٍ دون إزعاج أحد!",
                forceMultiplier = 86,
                unlockStarsRequired = 40
            ),
            Superhero(
                id = 13,
                name = "قناص الليزر",
                iconEmoji = "🎯⚡",
                category = "التكنولوجيا",
                powerName = "أشعة الليزر لرسم الضحكة وتحديد الأهداف",
                animType = "laser",
                accentColor = Color(0xFFF43F5E),
                voiceScript = "قناص الليزر يرسم بالأشعة الملونة وجوهاً ضاحكة ومسائل رياضية شيقة ليساعدنا على التفكير السريع والتعلم!",
                forceMultiplier = 93,
                unlockStarsRequired = 45
            ),
            Superhero(
                id = 14,
                name = "الكابتن الصغير",
                iconEmoji = "🧑‍✈️🏅",
                category = "القوى الخارقة",
                powerName = "التحية العسكرية وطاقة القيادة المثالية",
                animType = "jump",
                accentColor = Color(0xFF4338CA),
                voiceScript = "الكابتن الصغير يلقي التحية بقوة ويوجه الطائرات والسفن بأمان، ويشجع الأبطال الصغار على سماع كلام الوالدين يومياً!",
                forceMultiplier = 95,
                unlockStarsRequired = 50
            ),
            Superhero(
                id = 15,
                name = "الأميرة الخارقة",
                iconEmoji = "👸✨",
                category = "القوى الخارقة",
                powerName = "فقاعات الحب الوردية وطيران الفراشات",
                animType = "fly",
                accentColor = Color(0xFFEC4899),
                voiceScript = "الأميرة الخارقة تنشر فقاعات كرتونية وردية تحمل عطور النظافة والصابون، وتسعد قلوب الفتيات والأولاد بالحب والمرح الوفير!",
                forceMultiplier = 94,
                unlockStarsRequired = 55
            ),
            Superhero(
                id = 16,
                name = "بطل الغابة",
                iconEmoji = "🦁🌿",
                category = "الطبيعة",
                powerName = "التأرجح على الحبال وصداقة الأسود",
                animType = "spin",
                accentColor = Color(0xFF15803D),
                voiceScript = "بطل الغابة يتأرجح بنشاط بين فروع الأشجار الكبيرة، ويدعونا للاهتمام بالخضروات والفاكهة اللذيذة لنكون أقوياء تماماً مثله!",
                forceMultiplier = 90,
                unlockStarsRequired = 60
            ),
            Superhero(
                id = 17,
                name = "راكب التنين",
                iconEmoji = "🐉🔥",
                category = "الفضاء",
                powerName = "زئير التنين الودود وصواريخ النجوم",
                animType = "fly",
                accentColor = Color(0xFF701A75),
                voiceScript = "راكب التنين يطير على ظهر تنينه المرح الذي ينفث بالونات الهواء الملونة بدلاً من النار لتسليتنا وإبراز مهارة التعاون!",
                forceMultiplier = 97,
                unlockStarsRequired = 65
            ),
            Superhero(
                id = 18,
                name = "سيد العواصف",
                iconEmoji = "🌪️⚡",
                category = "الطبيعة",
                powerName = "دوامات الرياح النظيفة لتجفيف الغسيل",
                animType = "spin",
                accentColor = Color(0xFF0F766E),
                voiceScript = "سيد العواصف يصنع تيارات نسيم باردة ومضحكة، ترفع الطائرات الورقية للأطفال وتجفف الملابس بنشاط وسرور!",
                forceMultiplier = 92,
                unlockStarsRequired = 70
            ),
            Superhero(
                id = 19,
                name = "محارب الطاقة",
                iconEmoji = "🔮🪐",
                category = "التكنولوجيا",
                powerName = "كرة التوهج البنفسجية وحماية الحساب",
                animType = "shield",
                accentColor = Color(0xFFBE185D),
                voiceScript = "محارب الطاقة البنفسجي يولد بلورات براقة تعكس الذكاء وتمنح الأطفال الشجاعة لمواجهة المسائل الدراسية الصعبة بكل ثقة!",
                forceMultiplier = 94,
                unlockStarsRequired = 75
            ),
            Superhero(
                id = 20,
                name = "بطل النجوم",
                iconEmoji = "🌠⭐",
                category = "الفضاء",
                powerName = "الاتصال بمجرات التفوق والابتكار",
                animType = "fly",
                accentColor = Color(0xFF0369A1),
                voiceScript = "بطل النجوم يتتبع المذنبات الملونة، ويزين سماء غرفتنا بأجمل النجوم والشهب المحفزة للدراسة والتألق الدائم!",
                forceMultiplier = 99,
                unlockStarsRequired = 80
            )
        )
    }

    // Duel screen state ("اختبارات الصور")
    // Show 2 random heroes to choose between
    var duelHeroLeft by remember { mutableStateOf<Superhero?>(null) }
    var duelHeroRight by remember { mutableStateOf<Superhero?>(null) }

    fun refreshDuel() {
        val unlockedList = allHeroes.filter { totalStars >= it.unlockStarsRequired }
        if (unlockedList.size >= 2) {
            val shuffled = unlockedList.shuffled()
            duelHeroLeft = shuffled[0]
            duelHeroRight = shuffled[1]
        } else {
            duelHeroLeft = allHeroes[0]
            duelHeroRight = allHeroes[1]
        }
    }

    LaunchedEffect(key1 = totalStars) {
        refreshDuel()
    }

    // Selected hero details for visual animation trigger
    var activeHeroAnimation by remember { mutableStateOf<Superhero?>(null) }
    var scaleTrigger by remember { mutableStateOf(1.0f) }
    var rotateTrigger by remember { mutableStateOf(0f) }
    var flashAlpha by remember { mutableStateOf(0f) }

    // Surprise Chest state
    var isChestDialogOpen by remember { mutableStateOf(false) }
    var chestRewardMessage by remember { mutableStateOf("") }
    var chestRewardStarBonus by remember { mutableStateOf(0) }

    // Categories filter for grid list
    var selectedCategory by remember { mutableStateOf("الكل") }

    val filteredHeroes = remember(selectedCategory, totalStars) {
        if (selectedCategory == "الكل") allHeroes
        else allHeroes.filter { it.category == selectedCategory }
    }

    // Level calculator
    val currentLevel = (totalStars / 20) + 1
    val nextLevelStarsGoal = currentLevel * 20
    val prevLevelStarsGoal = (currentLevel - 1) * 20
    val levelProgress = remember(totalStars, currentLevel) {
        val totalNeededForLevel = nextLevelStarsGoal - prevLevelStarsGoal
        val achievedInCurrentLevel = totalStars - prevLevelStarsGoal
        (achievedInCurrentLevel.toFloat() / totalNeededForLevel.toFloat()).coerceIn(0f, 1f)
    }

    // Background animation
    val infiniteTransition = rememberInfiniteTransition(label = "HeroAtmosphere")
    val cosmicGlowScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CosmicGlow"
    )
    val cityStarsAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "StarsAlpha"
    )

    // Main layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // Beautiful Night Cosmic City gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF090D16))
                    )
                )

                // Twinkling stars in sky
                val rand = Random(42)
                for (i in 0..12) {
                    val cx = rand.nextFloat() * size.width
                    val cy = rand.nextFloat() * size.height * 0.7f
                    val r = rand.nextFloat() * 6f + 3f
                    val alpha = (rand.nextFloat() * 0.5f + 0.5f) * cityStarsAlpha
                    drawCircle(
                        color = Color(0xFFFDE047).copy(alpha = alpha),
                        radius = r,
                        center = Offset(cx, cy)
                    )
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // A. Top Navbar / Custom App Header (Tactile 3D Soft UI styling)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Return Back button
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.3f)), CircleShape)
                        .clickable {
                            callSoundManager.stopSpeaking()
                            onBack()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "عودة",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Interactive Level Badge & Stars indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Level Badge
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "المستوى $currentLevel",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Stars
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                            .border(androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFDE047).copy(alpha = 0.6f)), RoundedCornerShape(14.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$totalStars",
                            color = Color(0xFFFDE047),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Stars",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // XP level progress line
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                LinearProgressIndicator(
                    progress = { levelProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF10B981),
                    trackColor = Color.White.copy(alpha = 0.15f),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "هدف المستوى التالي: $nextLevelStarsGoal ⭐",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "تقدم الأبطال",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 20.dp)
            ) {
                // Section 1: Title Banner - "عالم الأبطال الخارقين"
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.8.dp, Color(0xFFFFDE59).copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🦸 عالم الأبطال الخارقين 🦸",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFDE59),
                            textAlign = TextAlign.Center,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = Shadow(
                                    color = Color(0xFFF59E0B).copy(alpha = 0.4f),
                                    offset = Offset(1.5f, 1.5f),
                                    blurRadius = 3f
                                )
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "أهلاً بك يا بطلنا $childName! هنا تلتقي بأقوى الأبطال، تختر المفضلة، وتحصل على النجوم والملصقات!",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Section 2: Duel "أي بطل تفضل؟" (اختبار الصور التفاعلي)
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "👇 اختبار الأبطال: أي بطل تفضل اليوم؟",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (duelHeroLeft != null && duelHeroRight != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Left Hero Card
                        Box(modifier = Modifier.weight(1f)) {
                            DualHeroItem(
                                hero = duelHeroLeft!!,
                                onSelect = {
                                    activeHeroAnimation = duelHeroLeft
                                    coroutineScope.launch {
                                        // Trigger epic particle splash
                                        particles = (1..18).map {
                                            StarParticle(
                                                x = 150f + Random.nextFloat() * 100f,
                                                y = 350f + Random.nextFloat() * 100f,
                                                vx = (Random.nextFloat() - 0.5f) * 12f,
                                                vy = -Random.nextFloat() * 14f - 8f,
                                                color = duelHeroLeft!!.accentColor,
                                                size = Random.nextFloat() * 16f + 8f
                                            )
                                        }
                                        // Scale & rotate animation triggers
                                        scaleTrigger = 1.3f
                                        rotateTrigger = 15f
                                        flashAlpha = 0.8f
                                        delay(300)
                                        scaleTrigger = 1.0f
                                        rotateTrigger = 0f
                                        delay(400)
                                        flashAlpha = 0f
                                    }

                                    // Spontaneous Voice script speak
                                    callSoundManager.speakArabicGuidance("custom_script_hero", String.format(duelHeroLeft!!.voiceScript, childName))

                                    // Award stars
                                    viewModel.awardQuizStars(5)

                                    // Automatic next duel layout update
                                    coroutineScope.launch {
                                        delay(4500)
                                        refreshDuel()
                                    }
                                }
                            )
                        }

                        // Right Hero Card
                        Box(modifier = Modifier.weight(1f)) {
                            DualHeroItem(
                                hero = duelHeroRight!!,
                                onSelect = {
                                    activeHeroAnimation = duelHeroRight
                                    coroutineScope.launch {
                                        // Particles
                                        particles = (1..18).map {
                                            StarParticle(
                                                x = 350f + Random.nextFloat() * 100f,
                                                y = 350f + Random.nextFloat() * 100f,
                                                vx = (Random.nextFloat() - 0.5f) * 12f,
                                                vy = -Random.nextFloat() * 14f - 8f,
                                                color = duelHeroRight!!.accentColor,
                                                size = Random.nextFloat() * 16f + 8f
                                            )
                                        }
                                        scaleTrigger = 1.3f
                                        rotateTrigger = -15f
                                        flashAlpha = 0.8f
                                        delay(300)
                                        scaleTrigger = 1.0f
                                        rotateTrigger = 0f
                                        delay(400)
                                        flashAlpha = 0f
                                    }

                                    // Voice speak
                                    callSoundManager.speakArabicGuidance("custom_script_hero", String.format(duelHeroRight!!.voiceScript, childName))

                                    // Award stars
                                    viewModel.awardQuizStars(5)

                                    // Delay & refresh
                                    coroutineScope.launch {
                                        delay(4500)
                                        refreshDuel()
                                    }
                                }
                            )
                        }
                    }
                }

                // Section 3: Surprise Toy Box Reward System (صندوق المفاجآت)
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                    border = androidx.compose.foundation.BorderStroke(2.5.dp, Color(0xFFEAB308))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Gift visual action
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .scale(cosmicGlowScale)
                                .clickable {
                                    // Kids chest opening simulation
                                    if (totalStars >= 10) {
                                        viewModel.awardQuizStars(-10) // cost of opening chest
                                        val randomHeroSticker = allHeroes.random().name
                                        val coinsReward = Random.nextInt(5, 20)
                                        chestRewardMessage = "✨ مبروك! حصلت على ملصق نادر لـ ($randomHeroSticker) بالإضافة إلى مكافأة ذهبية $coinsReward عملة!"
                                        chestRewardStarBonus = coinsReward
                                        isChestDialogOpen = true
                                        viewModel.awardQuizStars(coinsReward)
                                    } else {
                                        chestRewardMessage = "❌ عذراً يا بطل! لفتح صندوق المفاجآت تحتاج على الأقل إلى 10 نجوم ⭐. أكمل واجباتك اليومية لتربح النجوم!"
                                        chestRewardStarBonus = 0
                                        isChestDialogOpen = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎁", fontSize = 48.sp)
                        }

                        // Text content
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                "تحصيص الهدايا ومفاجأة الأبطال 🎉",
                                color = Color(0xFFFFDE59),
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "تكلفة فتح الصندوق: 10 نجوم ⭐. لتربح ملصق أبطال عشوائي وعملات ممتازة!",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                textAlign = TextAlign.Right,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // Section 4: All 20 heroes browse and details grid inside tabs
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "🌟 معرض شخصيات الأبطال (20 بطلاً للفتح)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable category tabs Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    val categories = listOf("الكل", "الطبيعة", "التكنولوجيا", "الفضاء", "القوى الخارقة")
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Button(
                            onClick = { selectedCategory = cat },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFFFFDE59) else Color.White.copy(alpha = 0.12f)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color(0xFF0F172A) else Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // All heroes grid logic
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredHeroes) { hero ->
                        val isUnlocked = totalStars >= hero.unlockStarsRequired
                        HeroGridCard(
                            hero = hero,
                            isUnlocked = isUnlocked,
                            isPlayingAnimation = activeHeroAnimation?.id == hero.id,
                            scale = if (activeHeroAnimation?.id == hero.id) scaleTrigger else 1.0f,
                            rotate = if (activeHeroAnimation?.id == hero.id) rotateTrigger else 0f,
                            onClick = {
                                if (isUnlocked) {
                                    activeHeroAnimation = hero
                                    coroutineScope.launch {
                                        scaleTrigger = 1.2f
                                        rotateTrigger = 10f
                                        delay(250)
                                        scaleTrigger = 1.0f
                                        rotateTrigger = 0f
                                    }
                                    callSoundManager.speakArabicGuidance("custom_script_hero", String.format(hero.voiceScript, childName))
                                } else {
                                    // Speak lock description
                                    callSoundManager.speakArabicGuidance("lock", "عذراً يا بطل! هذه الشخصية مقفلة، تحتاج إلى ${hero.unlockStarsRequired} نجمة لفتحها كلياً!")
                                }
                            }
                        )
                    }
                }
            }
        }

        // Active Overlay visual effects for Superhero actions (like Laser eyes / shooting webs!)
        if (activeHeroAnimation != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(alpha = flashAlpha)
                    .background(activeHeroAnimation!!.accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                // Flash action overlay
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(24.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = activeHeroAnimation!!.iconEmoji,
                        fontSize = 72.sp,
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = scaleTrigger,
                                scaleY = scaleTrigger,
                                rotationZ = rotateTrigger
                            )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = activeHeroAnimation!!.name,
                        fontSize = 20.sp,
                        color = activeHeroAnimation!!.accentColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "القدرة: ${activeHeroAnimation!!.powerName} ⚡",
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Particle System Canvas showing floating golden stars
        if (particles.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                particles.forEach { particle ->
                    drawCircle(
                        color = particle.color,
                        radius = particle.size * 0.5f,
                        center = Offset(particle.x, particle.y)
                    )
                }
            }
            // Update particles frame rate
            LaunchedEffect(particles) {
                delay(16)
                particles = particles.map { p ->
                    p.copy(
                        x = p.x + p.vx,
                        y = p.y + p.vy,
                        vy = p.vy + 0.4f // gravity
                    )
                }.filter { it.y < 1800f }
            }
        }

        // Surprise Box Dialog
        if (isChestDialogOpen) {
            AlertDialog(
                onDismissRequest = { isChestDialogOpen = false },
                title = {
                    Text(
                        text = "صندوق المفاجآت للأبطال 🎁",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = chestRewardMessage,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { isChestDialogOpen = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("حسناً يا بطل! 🎉", fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

// Sparkle Star particle class
data class StarParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float
)

@Composable
fun DualHeroItem(
    hero: Superhero,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(2.5.dp, hero.accentColor.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circle avatar frame with neon edge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(hero.accentColor.copy(alpha = 0.15f))
                    .border(androidx.compose.foundation.BorderStroke(2.dp, hero.accentColor), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = hero.iconEmoji, fontSize = 42.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = hero.name,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = hero.category,
                fontSize = 10.sp,
                color = hero.accentColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(containerColor = hero.accentColor),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text("تفضيل هذا البطل 🌟", fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HeroGridCard(
    hero: Superhero,
    isUnlocked: Boolean,
    isPlayingAnimation: Boolean,
    scale: Float,
    rotate: Float,
    onClick: () -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else scale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "hero_grid_press_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            )
            .graphicsLayer(
                scaleX = animatedScale,
                scaleY = animatedScale,
                rotationZ = rotate
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color(0xFF1E1B4B) else Color(0xFF0F172A).copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            2.5.dp,
            if (isUnlocked) hero.accentColor.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 8.dp else 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Elegant category gradient backdrop overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                if (isUnlocked) hero.accentColor.copy(alpha = 0.18f) else Color.Transparent,
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // High-tech circular chest crest slot drawing
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (isUnlocked) hero.accentColor.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(
                            2.dp,
                            if (isUnlocked) hero.accentColor else Color.White.copy(alpha = 0.2f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isUnlocked) {
                        // Drawing custom modern neon superhero chest plate lines behind the emoji
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            // Draw diagonal neon hero shoulder armor lines
                            drawLine(
                                color = hero.accentColor.copy(alpha = 0.6f),
                                start = Offset(0f, h * 0.2f),
                                end = Offset(w * 0.35f, h * 0.5f),
                                strokeWidth = 3f
                            )
                            drawLine(
                                color = hero.accentColor.copy(alpha = 0.6f),
                                start = Offset(w, h * 0.2f),
                                end = Offset(w * 0.65f, h * 0.5f),
                                strokeWidth = 3f
                            )
                            // Core circle chest insignia
                            drawCircle(
                                color = Color.White.copy(alpha = 0.18f),
                                radius = w * 0.25f,
                                center = Offset(w * 0.5f, h * 0.5f)
                            )
                        }
                        Text(text = hero.iconEmoji, fontSize = 30.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Locked",
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = hero.name,
                        fontSize = 13.sp,
                        color = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = hero.category,
                        fontSize = 9.sp,
                        color = if (isUnlocked) hero.accentColor else Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                // Power index or unlock requirement styled as high-tech neon bar
                if (isUnlocked) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "القوة اللاسلكية",
                                fontSize = 8.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${hero.forceMultiplier}%",
                                fontSize = 8.sp,
                                color = hero.accentColor,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        LinearProgressIndicator(
                            progress = { hero.forceMultiplier / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = hero.accentColor,
                            trackColor = Color.White.copy(alpha = 0.12f)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFB900).copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${hero.unlockStarsRequired} ⭐",
                            color = Color(0xFFFFCC00),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "للفك",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
