package com.aistudio.kidspolice.abcd.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aistudio.kidspolice.abcd.R
import com.aistudio.kidspolice.abcd.audio.PoliceAudioPlayer
import com.aistudio.kidspolice.abcd.data.Dialect
import com.aistudio.kidspolice.abcd.data.PoliceScenario
import com.aistudio.kidspolice.abcd.data.PoliceScenariosRepository
import com.aistudio.kidspolice.abcd.data.ScenarioCategory
import kotlinx.coroutines.delay

// ==========================================
// PALETTE & STABLE DESIGN TOKENS
// ==========================================
private val PoliceNavyDark = Color(0xFF071938)
private val PoliceNavyMedium = Color(0xFF0D3268)
private val PoliceBluePrimary = Color(0xFF1565C0)
private val PoliceBlueVibrant = Color(0xFF1976D2)
private val PoliceBlueCyan = Color(0xFF00E5FF)
private val PoliceGold = Color(0xFFFFD54F)
private val PoliceGoldDark = Color(0xFFFFA000)
private val EmergencyRed = Color(0xFFE53935)
private val EmergencyGreen = Color(0xFF00C853)

// Zero-allocation brushes
private val MainScreenBgBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0B2146),
        Color(0xFF0D2F64),
        Color(0xFF103D82),
        Color(0xFF091E3E)
    )
)

private val TopBarBgBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFF071836), Color(0xFF0D2956))
)

private val MissionBannerBgBrush = Brush.horizontalGradient(
    colors = listOf(Color(0xFF092550), Color(0xFF0E3875), Color(0xFF144B9E))
)

private val CardCarsBgBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFF0D3268), Color(0xFF092248))
)

private val CardSirensBgBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFFB71C1C), Color(0xFF7F0000))
)

private val CardGamesBgBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFF1B5E20), Color(0xFF0E3813))
)

private val CardStoriesBgBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFF4A148C), Color(0xFF26064E))
)

private val Rounded28 = RoundedCornerShape(28.dp)
private val Rounded22 = RoundedCornerShape(22.dp)
private val Rounded16 = RoundedCornerShape(16.dp)
private val Rounded12 = RoundedCornerShape(12.dp)

// ==========================================
// DATA MODELS
// ==========================================
data class PoliceCarModel(
    val id: String,
    val name: String,
    val typeName: String,
    val speed: String,
    val soundAction: String
)

data class PoliceStory(
    val id: String,
    val title: String,
    val summary: String,
    val narration: String,
    val moral: String,
    val stars: Int
)

data class MiniGame(
    val id: String,
    val title: String,
    val bgColor: Color,
    val badgeLabel: String
)

// ==========================================
// MAIN COMPOSABLE: HomeScreen
// ==========================================
@Composable
fun HomeScreen(
    selectedDialect: Dialect,
    onDialectSelected: (Dialect) -> Unit,
    onStartCall: (PoliceScenario) -> Unit,
    onOpenDialer: () -> Unit,
    onOpenSounds: () -> Unit,
    onOpenMissions: () -> Unit,
    onOpenCertificate: () -> Unit,
    userScore: Int,
    onTestInterstitial: () -> Unit = {},
    audioPlayer: PoliceAudioPlayer? = null
) {
    // Navigation & Dialog states
    var selectedBottomNavIndex by remember { mutableIntStateOf(0) } // 0 = الرئيسية
    var selectedCarIndex by remember { mutableIntStateOf(1) } // 0=SUV, 1=Sedan, 2=Van
    var showParentGateDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showStoryReaderDialog by remember { mutableStateOf(false) }
    var showGameDialog by remember { mutableStateOf<MiniGame?>(null) }
    var showRewardsDialog by remember { mutableStateOf(false) }
    var showVehicleExplorerDialog by remember { mutableStateOf(false) }
    var showScenarioPickerDialog by remember { mutableStateOf(false) }
    var currentPoints by rememberSaveable { mutableIntStateOf(1250) }
    var currentTrophies by rememberSaveable { mutableIntStateOf(8) }

    val scenarios = remember { PoliceScenariosRepository.scenarios }
    val isSirenPlayingFromPlayer by audioPlayer?.isSirenPlaying?.collectAsState() ?: remember { mutableStateOf(false) }

    // Animations (State holders to prevent recomposition of static nodes)
    val infiniteTransition = rememberInfiniteTransition(label = "police_beacon_anim")
    val beaconPulseState = infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_pulse"
    )

    // Cars list
    val carsList = remember {
        listOf(
            PoliceCarModel("suv", "دورية الدفع الرباعي", "SUV 4x4", "180 كم/س", "horn"),
            PoliceCarModel("sedan", "سيارة الشرطة السريعة", "Interceptor", "220 كم/س", "siren"),
            PoliceCarModel("van", "حافلة المهام الخاصة", "Patrol Van", "150 كم/س", "radio")
        )
    }

    // Stories list
    val storiesList = remember {
        listOf(
            PoliceStory(
                id = "story_1",
                title = "الشرطي الصغير والأمانة",
                summary = "قصة جميلة عن بطل أعاد المحفظة الضائعة لصاحبها بمساعدة الشرطي الصديق.",
                narration = "في يوم مشمس، كان سامي يلعب في الحديقة عندما وجد محفظة على المقعد. تذكر فوراً نصيحة الضابط فهد بأن الأمانة شرف كبير، فذهب مباشرة إلى مركز الشرطة وسلّمها. شكره الضابط ومنحه شارة بطل الأمانة!",
                moral = "الأمانة تجعل منك بطلاً حقيقياً يحبه الجميع!",
                stars = 50
            ),
            PoliceStory(
                id = "story_2",
                title = "بطل النوم المبكر والنشاط",
                summary = "كيف ساعد النوم المبكر بطلنا في أن يكون نشيطاً ويحصل على وسام الشرطي المتفوق.",
                narration = "كان كريم يسهر طويلاً، وفي أحد الأيام نصحه الضابط ماجد بأن الضباط الأبطال ينامون باكراً ليكون لديهم طاقة خارقة في الصباح. التزم كريم بالنوم المبكر وأصبح الأول في مدرسته وفي تمارينه الرياضية!",
                moral = "النوم المبكر يمنحك الطاقة والقوة لتكون بطلاً متألقاً!",
                stars = 50
            ),
            PoliceStory(
                id = "story_3",
                title = "قواعد المرور والسلامة",
                summary = "مغامرة توعوية لعبور الشارع بأمان والالتزام بإشارات المرور مع دورية الشرطة.",
                narration = "أثناء عبور الشارع، علمت الشرطية سارة الأطفال النظر يميناً ويساراً واستخدام خطوط المشاة فقط عند الإشارة الخضراء. الجميع وصل بأمان وسلام!",
                moral = "احترام قواعد المرور يحمينا ويحمي من نحبهم دائماً.",
                stars = 50
            )
        )
    }

    // Enforce RTL Layout
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = PoliceNavyDark
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = MainScreenBgBrush)
            ) {
                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ==========================================
                    // 1. TOP HEADER: Elegant Brand & Officer Quick Stats
                    // ==========================================
                    NewTopBrandHeader(
                        points = currentPoints,
                        trophies = currentTrophies,
                        onOpenSettings = { showSettingsDialog = true },
                        onOpenParentGate = { showParentGateDialog = true },
                        onOpenRewards = { showRewardsDialog = true }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // ==========================================
                    // 2. HERO OFFICER & MISSION OF THE DAY
                    // ==========================================
                    MissionOfTheDayHeroSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        beaconPulse = { beaconPulseState.value },
                        selectedDialect = selectedDialect,
                        onStartEmergencyCall = {
                            showScenarioPickerDialog = true
                            audioPlayer?.playRadioChirp()
                        },
                        onOpenMissionsList = {
                            onOpenMissions()
                            audioPlayer?.playRadioChirp()
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // ==========================================
                    // 3. SECTION TITLE: الأقسام الرئيسية
                    // ==========================================
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "الأقسام الرئيسية",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Box(
                            modifier = Modifier
                                .clip(Rounded12)
                                .background(Color(0xFF0D3268))
                                .border(1.dp, Color(0xFF64B5F6).copy(alpha = 0.5f), Rounded12)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "٤ أقسام تفاعلية",
                                color = PoliceGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ==========================================
                    // 4. FOUR MAIN INTERACTIVE CARDS (2x2 GRID)
                    // ==========================================
                    FourMainPoliceSectionsGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        isSirenPlaying = isSirenPlayingFromPlayer,
                        onOpenCars = {
                            showVehicleExplorerDialog = true
                            audioPlayer?.playPoliceHorn()
                        },
                        onToggleSiren = {
                            audioPlayer?.togglePoliceSiren()
                        },
                        onOpenGames = {
                            showGameDialog = MiniGame("game_quiz", "تحدي الشارات والأبطال", Color(0xFF1B5E20), "لعبة تفاعلية")
                            audioPlayer?.playRadioChirp()
                        },
                        onOpenStories = {
                            showStoryReaderDialog = true
                            audioPlayer?.playRadioChirp()
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ==========================================
                    // 5. CAR SHOWROOM SPOTLIGHT
                    // ==========================================
                    QuickPoliceCarSelectorCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        currentCarIndex = selectedCarIndex,
                        cars = carsList,
                        onSelectCar = { newIndex ->
                            selectedCarIndex = newIndex
                            audioPlayer?.playPoliceHorn()
                        },
                        onOpenFleet = {
                            showVehicleExplorerDialog = true
                            audioPlayer?.playRadioChirp()
                        },
                        onPlayHorn = {
                            audioPlayer?.playPoliceHorn()
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ==========================================
                // BOTTOM NAVIGATION BAR
                // ==========================================
                NewModernBottomNav(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    selectedIndex = selectedBottomNavIndex,
                    onSelectTab = { index ->
                        selectedBottomNavIndex = index
                        when (index) {
                            0 -> { /* الرئيسية */ }
                            1 -> onOpenSounds()
                            2 -> {
                                showGameDialog = MiniGame("game_quiz", "تحدي الشارات والأبطال", Color(0xFF1B5E20), "لعبة تفاعلية")
                            }
                            3 -> showStoryReaderDialog = true
                            4 -> showRewardsDialog = true
                        }
                        audioPlayer?.playRadioChirp()
                    }
                )
            }
        }

        // ==========================================
        // DIALOGS & OVERLAYS
        // ==========================================

        // Vehicle Explorer Dialog
        if (showVehicleExplorerDialog) {
            VehicleExplorerDialog(
                cars = carsList,
                selectedCarIndex = selectedCarIndex,
                audioPlayer = audioPlayer,
                onSelectCar = { selectedCarIndex = it },
                onDismiss = { showVehicleExplorerDialog = false }
            )
        }

        // Parent Gate Dialog
        if (showParentGateDialog) {
            ParentGateDialog(
                onDismiss = { showParentGateDialog = false },
                onSuccess = {
                    showParentGateDialog = false
                    showSettingsDialog = true
                }
            )
        }

        // Settings Dialog
        if (showSettingsDialog) {
            SettingsDialog(
                selectedDialect = selectedDialect,
                onDialectSelected = { dialect -> onDialectSelected(dialect) },
                onOpenDialer = {
                    showSettingsDialog = false
                    onOpenDialer()
                },
                onOpenSounds = {
                    showSettingsDialog = false
                    onOpenSounds()
                },
                onTestAd = onTestInterstitial,
                onDismiss = { showSettingsDialog = false }
            )
        }

        // Story Reader Dialog
        if (showStoryReaderDialog) {
            StoryReaderDialog(
                stories = storiesList,
                audioPlayer = audioPlayer,
                onRewardClaimed = { bonusStars ->
                    currentPoints += bonusStars
                    currentTrophies += 1
                },
                onDismiss = { showStoryReaderDialog = false }
            )
        }

        // Mini Game Dialog
        showGameDialog?.let { game ->
            MiniGameDialog(
                game = game,
                audioPlayer = audioPlayer,
                onWinGame = { pointsEarned ->
                    currentPoints += pointsEarned
                    currentTrophies += 1
                },
                onDismiss = { showGameDialog = null }
            )
        }

        // Rewards Dialog
        if (showRewardsDialog) {
            RewardsDialog(
                points = currentPoints,
                trophies = currentTrophies,
                onOpenCertificate = {
                    showRewardsDialog = false
                    onOpenCertificate()
                },
                onOpenMissions = {
                    showRewardsDialog = false
                    onOpenMissions()
                },
                onDismiss = { showRewardsDialog = false }
            )
        }

        // Scenario Picker Dialog
        if (showScenarioPickerDialog) {
            ScenarioPickerDialog(
                scenarios = scenarios,
                selectedDialect = selectedDialect,
                audioPlayer = audioPlayer,
                onSelectScenario = { scenario ->
                    showScenarioPickerDialog = false
                    onStartCall(scenario)
                },
                onDismiss = { showScenarioPickerDialog = false }
            )
        }
    }
}

// ==========================================
// 1. TOP BRAND HEADER COMPONENT
// ==========================================
@Composable
private fun NewTopBrandHeader(
    points: Int,
    trophies: Int,
    onOpenSettings: () -> Unit,
    onOpenParentGate: () -> Unit,
    onOpenRewards: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(brush = TopBarBgBrush)
            .border(
                width = 1.5.dp,
                color = Color(0xFF1E88E5).copy(alpha = 0.5f),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title (Left/Start)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shield_logo),
                    contentDescription = "شعار شرطة الأطفال",
                    modifier = Modifier.size(40.dp)
                )

                Column {
                    Text(
                        text = "شرطة الأطفال",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "مركز القيادة المباشر",
                        color = Color(0xFF64B5F6),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Stats Badges (Center)
            Row(
                modifier = Modifier
                    .clip(Rounded12)
                    .background(Color(0xFF081C38))
                    .border(1.dp, Color(0xFF1976D2), Rounded12)
                    .clickable { onOpenRewards() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "نقاط",
                        tint = PoliceGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "$points",
                        color = PoliceGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(14.dp)
                        .background(Color(0xFF1E88E5).copy(alpha = 0.5f))
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🏆", fontSize = 12.sp)
                    Text(
                        text = "$trophies",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick Actions (Settings & Parent Gate)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onOpenParentGate,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(Rounded12)
                        .background(Color(0xFF1B5E20))
                        .border(1.dp, Color(0xFF81C784), Rounded12)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "ولي الأمر",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(Rounded12)
                        .background(Color(0xFF1565C0))
                        .border(1.dp, Color(0xFF90CAF9), Rounded12)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "الإعدادات",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// 2. MISSION OF THE DAY HERO SECTION
// ==========================================
@Composable
private fun MissionOfTheDayHeroSection(
    modifier: Modifier = Modifier,
    beaconPulse: () -> Float,
    selectedDialect: Dialect,
    onStartEmergencyCall: () -> Unit,
    onOpenMissionsList: () -> Unit
) {
    Card(
        modifier = modifier.shadow(14.dp, Rounded28),
        shape = Rounded28,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2246))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = MissionBannerBgBrush)
                .border(
                    width = 2.dp,
                    color = Color(0xFF00E5FF).copy(alpha = 0.6f),
                    shape = Rounded28
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Tag with Emergency Beacon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(Rounded12)
                            .background(EmergencyRed)
                            .border(1.dp, Color(0xFFFFCDD2), Rounded12)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "مهمة اليوم الخاصة",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Flashing Beacon Light
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .graphicsLayer { alpha = beaconPulse().coerceIn(0.3f, 1f) }
                                .clip(CircleShape)
                                .background(EmergencyRed)
                                .border(1.dp, Color.White, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .graphicsLayer { alpha = (1.4f - beaconPulse()).coerceIn(0.3f, 1f) }
                                .clip(CircleShape)
                                .background(PoliceBlueCyan)
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }
                }

                // Middle Row: Hero Officer Image + Mission Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 3D Police Officer Hero Image
                    Image(
                        painter = painterResource(id = R.drawable.officer_kid),
                        contentDescription = "الشرطي الصغير",
                        modifier = Modifier
                            .size(92.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .border(2.5.dp, PoliceGold, CircleShape)
                    )

                    // Mission Text Content
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "نداء دورية الأبطال الصغار",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "الضابط فهد مستعد للتحدث معك ومساعدتك في مهامك اليومية!",
                            color = Color(0xFFB3E5FC),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Text(
                            text = "اللهجة: ${selectedDialect.flag} ${selectedDialect.displayName.take(16)}",
                            color = PoliceGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onStartEmergencyCall,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                            .shadow(6.dp, Rounded16),
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyGreen),
                        shape = Rounded16,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بدء الاتصال المباشر",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Button(
                        onClick = onOpenMissionsList,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .shadow(6.dp, Rounded16),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        shape = Rounded16,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "جدول المهام",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. FOUR MAIN SECTIONS GRID (Cars, Sirens, Games, Stories)
// ==========================================
@Composable
private fun FourMainPoliceSectionsGrid(
    modifier: Modifier = Modifier,
    isSirenPlaying: Boolean,
    onOpenCars: () -> Unit,
    onToggleSiren: () -> Unit,
    onOpenGames: () -> Unit,
    onOpenStories: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: Cars + Sirens
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: سيارات الشرطة
            MainSectionActionCard(
                modifier = Modifier.weight(1f),
                title = "سيارات الشرطة",
                subtitle = "أسطول الدوريات السريعة",
                iconResId = R.drawable.police_car_main,
                bgBrush = CardCarsBgBrush,
                borderColor = Color(0xFF64B5F6),
                badgeText = "٣ دوريات",
                onClick = onOpenCars
            )

            // Card 2: صفارات الشرطة
            MainSectionActionCard(
                modifier = Modifier.weight(1f),
                title = "صفارات الشرطة",
                subtitle = if (isSirenPlaying) "يعمل الآن 🔊" else "أصوات وأبواق الإنذار",
                iconResId = R.drawable.red_siren,
                bgBrush = CardSirensBgBrush,
                borderColor = if (isSirenPlaying) Color(0xFFFFEB3B) else Color(0xFFFF8A80),
                badgeText = if (isSirenPlaying) "مشتغل" else "إنذار",
                onClick = onToggleSiren
            )
        }

        // Row 2: Games + Stories
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 3: ألعاب الشرطة
            MainSectionActionCard(
                modifier = Modifier.weight(1f),
                title = "ألعاب الشرطة",
                subtitle = "تحديات وألغاز الأبطال",
                iconResId = R.drawable.game_puzzle,
                bgBrush = CardGamesBgBrush,
                borderColor = Color(0xFF81C784),
                badgeText = "تحدي جديد",
                onClick = onOpenGames
            )

            // Card 4: قصص الشرطة
            MainSectionActionCard(
                modifier = Modifier.weight(1f),
                title = "قصص الشرطة",
                subtitle = "مغامرات وقيم هادفة",
                iconResId = R.drawable.officer_kid,
                bgBrush = CardStoriesBgBrush,
                borderColor = Color(0xFFBA68C8),
                badgeText = "٣ قصص",
                onClick = onOpenStories
            )
        }
    }
}

@Composable
private fun MainSectionActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    iconResId: Int,
    bgBrush: Brush,
    borderColor: Color,
    badgeText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(10.dp, Rounded22)
            .clickable(onClick = onClick),
        shape = Rounded22,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF092040))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = bgBrush)
                .border(width = 2.dp, color = borderColor, shape = Rounded22)
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top row: Icon + Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = title,
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(4.dp, CircleShape)
                    )

                    Box(
                        modifier = Modifier
                            .clip(Rounded12)
                            .background(Color.Black.copy(alpha = 0.35f))
                            .border(1.dp, borderColor.copy(alpha = 0.6f), Rounded12)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = Color.White,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Title & Subtitle
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        color = Color(0xFFECEFF1).copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. QUICK POLICE CAR SELECTOR SPOTLIGHT
// ==========================================
@Composable
private fun QuickPoliceCarSelectorCard(
    modifier: Modifier = Modifier,
    currentCarIndex: Int,
    cars: List<PoliceCarModel>,
    onSelectCar: (Int) -> Unit,
    onOpenFleet: () -> Unit,
    onPlayHorn: () -> Unit
) {
    Card(
        modifier = modifier.shadow(12.dp, Rounded24),
        shape = Rounded24,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF092040))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D2D5B))
                .border(2.dp, Color(0xFF1976D2), Rounded24)
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "دورية الشرطة النشطة",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = cars.getOrNull(currentCarIndex)?.name ?: "دورية الشرطة",
                        color = PoliceGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Thumbnails Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF071833), Rounded16)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    cars.forEachIndexed { index, car ->
                        val isSelected = (index == currentCarIndex)
                        val thumbResId = when (index) {
                            0 -> R.drawable.police_car_1
                            1 -> R.drawable.police_car_2
                            else -> R.drawable.police_car_3
                        }

                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF1E88E5) else Color(0xFF0D3268))
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) PoliceGold else Color(0xFF42A5F5),
                                    shape = CircleShape
                                )
                                .clickable { onSelectCar(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = thumbResId),
                                contentDescription = car.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                    }
                }

                // Quick Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenFleet,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                        shape = Rounded12,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("استعراض الأسطول", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onPlayHorn,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = Rounded12,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("بوق الدورية", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private val Rounded24 = RoundedCornerShape(24.dp)

// ==========================================
// 5. NEW MODERN BOTTOM NAVIGATION BAR
// ==========================================
@Composable
private fun NewModernBottomNav(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color(0xFF071836))
            .border(
                width = 1.5.dp,
                color = Color(0xFF1E88E5).copy(alpha = 0.6f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabItem(
                label = "الرئيسية",
                iconResId = R.drawable.shield_logo,
                isSelected = (selectedIndex == 0),
                onClick = { onSelectTab(0) }
            )
            NavTabItem(
                label = "الصفارات",
                iconResId = R.drawable.red_siren,
                isSelected = (selectedIndex == 1),
                onClick = { onSelectTab(1) }
            )
            NavTabItem(
                label = "الألعاب",
                iconResId = R.drawable.game_puzzle,
                isSelected = (selectedIndex == 2),
                onClick = { onSelectTab(2) }
            )
            NavTabItem(
                label = "القصص",
                iconResId = R.drawable.officer_kid,
                isSelected = (selectedIndex == 3),
                onClick = { onSelectTab(3) }
            )
            NavTabItem(
                label = "المكافآت",
                iconResId = R.drawable.officer_kid,
                isSelected = (selectedIndex == 4),
                onClick = { onSelectTab(4) }
            )
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    iconResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(Rounded12)
            .background(if (isSelected) Color(0xFF1565C0) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) PoliceGold else Color(0xFF90CAF9),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
        )
    }
}

// ==========================================
// 6. DIALOGS IMPLEMENTATION
// ==========================================

// Parent Gate Dialog
@Composable
private fun ParentGateDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var num1 by remember { mutableIntStateOf((4..9).random()) }
    var num2 by remember { mutableIntStateOf((3..8).random()) }
    var userAnswer by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = Rounded22,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2646))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = PoliceGold,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "منطقة أولياء الأمور",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "لحماية طفلك، يرجى حل المسألة الرياضية التالية:",
                    color = Color(0xFF90CAF9),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF163A69), Rounded12)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$num1 × $num2 = ؟",
                        color = PoliceGold,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = {
                        userAnswer = it.take(3)
                        errorMessage = false
                    },
                    placeholder = { Text("أدخل الناتج هنا...", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = PoliceBlueCyan,
                        unfocusedBorderColor = Color(0xFF1976D2)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "الإجابة غير صحيحة، حاول مجدداً!",
                        color = EmergencyRed,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                        shape = Rounded12
                    ) {
                        Text("إلغاء", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val ans = userAnswer.trim().toIntOrNull()
                            if (ans == num1 * num2) {
                                onSuccess()
                            } else {
                                errorMessage = true
                                num1 = (4..9).random()
                                num2 = (3..8).random()
                                userAnswer = ""
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyGreen),
                        shape = Rounded12
                    ) {
                        Text("تأكيد", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Settings Dialog
@Composable
private fun SettingsDialog(
    selectedDialect: Dialect,
    onDialectSelected: (Dialect) -> Unit,
    onOpenDialer: () -> Unit,
    onOpenSounds: () -> Unit,
    onTestAd: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = Rounded22,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D233F))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إعدادات شرطة الأطفال",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "اختر اللهجة المفضلة للمكالمات:",
                    color = Color(0xFF81D4FA),
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Dialect.entries.forEach { dialect ->
                    val isSelected = (dialect == selectedDialect)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(Rounded12)
                            .background(if (isSelected) Color(0xFF1565C0) else Color(0xFF133256))
                            .clickable { onDialectSelected(dialect) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${dialect.flag} ${dialect.displayName}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PoliceGold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenDialer,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        shape = Rounded12
                    ) {
                        Text("لوحة الأرقام", color = Color.White, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onOpenSounds,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = Rounded12
                    ) {
                        Text("لوحة الصفارات", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// Vehicle Explorer Dialog
@Composable
private fun VehicleExplorerDialog(
    cars: List<PoliceCarModel>,
    selectedCarIndex: Int,
    audioPlayer: PoliceAudioPlayer?,
    onSelectCar: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(selectedCarIndex) }
    var isEngineRunning by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = Rounded22,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF091E3A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                    Text(
                        text = "أسطول دوريات الشرطة",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Vehicle Image Display
                val thumbResId = when (currentIndex) {
                    0 -> R.drawable.police_car_1
                    1 -> R.drawable.police_car_2
                    else -> R.drawable.police_car_3
                }

                Image(
                    painter = painterResource(id = thumbResId),
                    contentDescription = cars[currentIndex].name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(Rounded22)
                        .border(2.dp, PoliceGold, Rounded22)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = cars[currentIndex].name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "السرعة القصوى: ${cars[currentIndex].speed}",
                    color = Color(0xFF90CAF9),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            isEngineRunning = !isEngineRunning
                            audioPlayer?.playPoliceHorn()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEngineRunning) EmergencyGreen else Color(0xFF1565C0)
                        ),
                        shape = Rounded12
                    ) {
                        Text(if (isEngineRunning) "المحرك شغال" else "تشغيل المحرك", color = Color.White, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            audioPlayer?.playPoliceHorn()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = Rounded12
                    ) {
                        Text("إطلاق البوق", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// Story Reader Dialog
@Composable
private fun StoryReaderDialog(
    stories: List<PoliceStory>,
    audioPlayer: PoliceAudioPlayer?,
    onRewardClaimed: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedStoryIndex by remember { mutableIntStateOf(0) }
    val currentStory = stories[selectedStoryIndex]
    var isReadingComplete by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = Rounded22,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1F3D))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                    Text(
                        text = "قصص الشرطي الصغير",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Story Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    stories.forEachIndexed { index, story ->
                        val isSel = (index == selectedStoryIndex)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(Rounded12)
                                .background(if (isSel) Color(0xFF7B1FA2) else Color(0xFF133256))
                                .clickable {
                                    selectedStoryIndex = index
                                    isReadingComplete = false
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "قصة ${index + 1}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentStory.title,
                    color = PoliceGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF07152B), Rounded16)
                        .padding(14.dp)
                ) {
                    Text(
                        text = currentStory.narration,
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1B5E20), Rounded12)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "العبرة: ${currentStory.moral}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        isReadingComplete = true
                        onRewardClaimed(currentStory.stars)
                        audioPlayer?.playPoliceHorn()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isReadingComplete) Color(0xFF455A64) else EmergencyGreen
                    ),
                    shape = Rounded12,
                    enabled = !isReadingComplete
                ) {
                    Text(
                        text = if (isReadingComplete) "تم استلام وسام القصة ⭐" else "أكملت القصة واكسب ٥٠ نجمة ⭐",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Mini Game Dialog
@Composable
private fun MiniGameDialog(
    game: MiniGame,
    audioPlayer: PoliceAudioPlayer?,
    onWinGame: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var isWon by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = Rounded22,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2246))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                    Text(
                        text = game.title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ماذا يفعل البطل الصغير عند رؤية إشارة المرور الحمراء؟",
                    color = PoliceGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                val options = listOf("يقف وينتظر الضوء الأخضر", "يركض بسرعة", "يعبر الشارع بدون انتباه")

                options.forEachIndexed { index, option ->
                    val isCorrect = (index == 0)
                    val isSelected = (selectedAnswer == index)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(Rounded12)
                            .background(
                                when {
                                    isSelected && isCorrect -> EmergencyGreen
                                    isSelected && !isCorrect -> EmergencyRed
                                    else -> Color(0xFF133866)
                                }
                            )
                            .clickable {
                                selectedAnswer = index
                                if (isCorrect) {
                                    isWon = true
                                    audioPlayer?.playPoliceHorn()
                                    onWinGame(100)
                                } else {
                                    audioPlayer?.playRadioChirp()
                                }
                            }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isWon) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "أحسنت يا بطل! حصلت على ١٠٠ نقطة ⭐",
                        color = EmergencyGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Rewards Dialog
@Composable
private fun RewardsDialog(
    points: Int,
    trophies: Int,
    onOpenCertificate: () -> Unit,
    onOpenMissions: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = Rounded22,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF092040))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                    Text(
                        text = "أوسمة وجوائز الشرطي",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF0D3268), Rounded16)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⭐ $points", color = PoliceGold, fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text(text = "مجموع النقاط", color = Color.White, fontSize = 11.sp)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF0D3268), Rounded16)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🏆 $trophies", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text(text = "الأوسمة المحققة", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onOpenCertificate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyGreen),
                    shape = Rounded12
                ) {
                    Text("عرض شهادة التقدير الرسمية", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onOpenMissions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    shape = Rounded12
                ) {
                    Text("جدول المهام اليومية", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Scenario Picker Dialog
@Composable
private fun ScenarioPickerDialog(
    scenarios: List<PoliceScenario>,
    selectedDialect: Dialect,
    audioPlayer: PoliceAudioPlayer?,
    onSelectScenario: (PoliceScenario) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategoryFilter by remember { mutableStateOf<ScenarioCategory?>(null) }
    val filteredScenarios = remember(selectedCategoryFilter, scenarios) {
        if (selectedCategoryFilter == null) scenarios
        else scenarios.filter { it.category == selectedCategoryFilter }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 520.dp)
                .padding(8.dp),
            shape = Rounded24,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF091E3A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, PoliceBlueCyan, Rounded24)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "اختيار موقف المكالمة",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedDialect.flag} ${selectedDialect.displayName.take(18)}",
                            color = Color(0xFF81D4FA),
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(Rounded12)
                            .background(if (selectedCategoryFilter == null) Color(0xFF0091EA) else Color(0xFF133866))
                            .clickable {
                                selectedCategoryFilter = null
                                audioPlayer?.playRadioChirp()
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("الكل", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(Rounded12)
                            .background(if (selectedCategoryFilter == ScenarioCategory.SLEEP) EmergencyGreen else Color(0xFF133866))
                            .clickable {
                                selectedCategoryFilter = ScenarioCategory.SLEEP
                                audioPlayer?.playRadioChirp()
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("النوم والأكل", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(Rounded12)
                            .background(if (selectedCategoryFilter == ScenarioCategory.OBEDIENCE) Color(0xFFFF9100) else Color(0xFF133866))
                            .clickable {
                                selectedCategoryFilter = ScenarioCategory.OBEDIENCE
                                audioPlayer?.playRadioChirp()
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("التربية والسلوك", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scenarios List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredScenarios) { scenario ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(Rounded16)
                                .background(Color(0xFF0F2B52))
                                .border(1.dp, Color(0xFF00B0FF).copy(alpha = 0.5f), Rounded16)
                                .clickable {
                                    audioPlayer?.playPoliceHorn()
                                    onSelectScenario(scenario)
                                }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(Rounded12)
                                        .background(EmergencyGreen)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("اتصال", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 10.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = scenario.title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.End
                                    )
                                    Text(
                                        text = scenario.subtitle,
                                        color = Color(0xFF90CAF9),
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.End,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
