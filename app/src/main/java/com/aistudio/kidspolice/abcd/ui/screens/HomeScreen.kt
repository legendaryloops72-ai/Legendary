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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aistudio.kidspolice.abcd.audio.PoliceAudioPlayer
import com.aistudio.kidspolice.abcd.data.Dialect
import com.aistudio.kidspolice.abcd.data.PoliceScenario
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

// ==========================================
// COLOR PALETTE - PIXEL PERFECT TO DESIGN
// ==========================================
private val SkyBlueTop = Color(0xFF2FA2EE)
private val SkyBlueBottom = Color(0xFF196CB9)
private val DeepCityNavy = Color(0xFF0F3662)

private val PoliceNavyDark = Color(0xFF07172C)
private val PoliceNavyMedium = Color(0xFF0D2545)
private val ShieldNavy = Color(0xFF0A1E3B)
private val ShieldBorderGold = Color(0xFFFFD54F)

private val EmergencyRed = Color(0xFFFF2A2A)
private val EmergencyBlue = Color(0xFF1E88E5)
private val PoliceGold = Color(0xFFFFC800)
private val GoldShadow = Color(0xFFC77800)

private val CardBorderBlue = Color(0xFF00B0FF)
private val CardBorderRed = Color(0xFFFF5252)
private val CardBorderPurple = Color(0xFFAA00FF)
private val CardBorderGreen = Color(0xFF00E676)

private val PlayGreen = Color(0xFF00C853)
private val PlayGreenGlow = Color(0xFF69F0AE)

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
    val iconEmoji: String,
    val bgColor: Color,
    val badgeLabel: String
)

// ==========================================
// MAIN SCREEN COMPOSABLE
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
    // Navigation and state
    var selectedBottomNavIndex by remember { mutableIntStateOf(3) } // 3 = Home
    var selectedCarIndex by remember { mutableIntStateOf(1) } // 0=SUV, 1=Sedan, 2=Van
    var showParentGateDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showStoryReaderDialog by remember { mutableStateOf(false) }
    var showGameDialog by remember { mutableStateOf<MiniGame?>(null) }
    var showRewardsDialog by remember { mutableStateOf(false) }
    var currentPoints by rememberSaveable { mutableIntStateOf(1250) }
    var currentTrophies by rememberSaveable { mutableIntStateOf(8) }

    // Siren Audio state from player
    val isSirenPlayingFromPlayer by audioPlayer?.isSirenPlaying?.collectAsState() ?: remember { mutableStateOf(false) }

    // Infinite animations for lights and glows
    val infiniteTransition = rememberInfiniteTransition(label = "police_effects")
    val beaconPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_pulse"
    )

    val lightAlternator by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "light_alternator"
    )

    val soundWavesOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sound_waves"
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

    // Always enforce RTL for standard Arabic interface
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = PoliceNavyDark
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SkyBlueTop,
                                SkyBlueBottom,
                                PoliceNavyMedium,
                                PoliceNavyDark
                            ),
                            startY = 0f,
                            endY = 1200f
                        )
                    )
            ) {
                // Background city skyline & soft clouds
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    drawHeaderSkyline(size)
                }

                // Main Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(14.dp))

                    // ==========================================
                    // 1. TOP HEADER SECTION (Buttons, Shield, Profile)
                    // ==========================================
                    TopHeaderBar(
                        points = currentPoints,
                        trophies = currentTrophies,
                        lightAlternator = lightAlternator,
                        onOpenSettings = { showSettingsDialog = true },
                        onOpenParentGate = { showParentGateDialog = true },
                        onProfileClick = { showRewardsDialog = true }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // ==========================================
                    // 2. MAIN 2x2 CARDS GRID
                    // ==========================================
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // ROW 1: Police Cars (Blue, Top-Left) + Police Sirens (Red, Top-Right)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Top-Left: Police Cars (Blue Card)
                            PoliceCarsCard(
                                modifier = Modifier.weight(1f),
                                currentCarIndex = selectedCarIndex,
                                cars = carsList,
                                onSelectCar = { newIndex ->
                                    selectedCarIndex = newIndex
                                    audioPlayer?.playPoliceHorn()
                                },
                                onPrevCar = {
                                    selectedCarIndex = if (selectedCarIndex > 0) selectedCarIndex - 1 else carsList.size - 1
                                    audioPlayer?.playRadioChirp()
                                },
                                onNextCar = {
                                    selectedCarIndex = if (selectedCarIndex < carsList.size - 1) selectedCarIndex + 1 else 0
                                    audioPlayer?.playRadioChirp()
                                }
                            )

                            // Top-Right: Police Sirens (Red/Dark Card)
                            PoliceSirensCard(
                                modifier = Modifier.weight(1f),
                                isPlaying = isSirenPlayingFromPlayer,
                                pulseScale = beaconPulse,
                                soundWavePhase = soundWavesOffset,
                                onToggleMainSiren = {
                                    audioPlayer?.togglePoliceSiren()
                                },
                                onPlayWhistle = {
                                    audioPlayer?.playWhistle()
                                },
                                onPlayChirp = {
                                    audioPlayer?.playRadioChirp()
                                }
                            )
                        }

                        // ROW 2: Games (Purple, Bottom-Left) + Stories (Green, Bottom-Right)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Bottom-Left: Police Games (Purple Card)
                            PoliceGamesCard(
                                modifier = Modifier.weight(1f),
                                onGameClick = { game ->
                                    showGameDialog = game
                                    audioPlayer?.playRadioChirp()
                                }
                            )

                            // Bottom-Right: Police Stories (Green Card)
                            PoliceStoriesCard(
                                modifier = Modifier.weight(1f),
                                onReadNewStory = {
                                    showStoryReaderDialog = true
                                    audioPlayer?.playRadioChirp()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // ==========================================
                // 3. BOTTOM NAVIGATION BAR (Glassmorphic)
                // ==========================================
                BottomGlassNavigationBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    selectedIndex = selectedBottomNavIndex,
                    onSelectTab = { index ->
                        selectedBottomNavIndex = index
                        when (index) {
                            0 -> showRewardsDialog = true // مكافآت
                            1 -> onOpenCertificate()     // الإنجازات
                            2 -> onOpenMissions()        // التحديات
                            3 -> { /* الرئيسية - Already here */ }
                        }
                        audioPlayer?.playRadioChirp()
                    }
                )
            }
        }

        // ==========================================
        // DIALOGS & OVERLAYS
        // ==========================================

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

        // Settings Dialog (Dialects & Audio)
        if (showSettingsDialog) {
            SettingsDialog(
                selectedDialect = selectedDialect,
                onDialectSelected = { dialect ->
                    onDialectSelected(dialect)
                },
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

        // Mini Games Interactive Dialog
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
    }
}

// ==========================================
// 1. TOP HEADER SECTION & SHIELD COMPONENT
// ==========================================
@Composable
private fun TopHeaderBar(
    points: Int,
    trophies: Int,
    lightAlternator: Float,
    onOpenSettings: () -> Unit,
    onOpenParentGate: () -> Unit,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // TOP LEFT: Two Circular / Squircle Action Buttons (Settings & Parents)
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Button 1: Settings ⚙️
            HeaderSquareButton(
                icon = Icons.Default.Settings,
                label = "الإعدادات",
                onClick = onOpenSettings
            )

            // Button 2: Parent Gate 👪
            HeaderSquareButton(
                icon = Icons.Default.Lock,
                label = "ولي الأمر",
                onClick = onOpenParentGate
            )
        }

        // TOP CENTER: 3D Shield Badge with Red/Blue Emergency Lights
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            ShieldBadge3D(lightAlternator = lightAlternator)
        }

        // TOP RIGHT: Score & Officer Avatar Pill
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF0B254A),
                            Color(0xFF133E78)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF00B0FF), Color(0xFF0059B2))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable { onProfileClick() }
                .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Stats column
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$points",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "⭐", fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$trophies",
                        color = PoliceGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "🏆", fontSize = 13.sp)
                }
            }

            // Officer Avatar Icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1976D2))
                    .border(2.5.dp, Color(0xFF64B5F6), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawPoliceOfficerAvatar(size)
                }
            }
        }
    }
}

@Composable
private fun HeaderSquareButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(6.dp, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF153B6C),
                            Color(0xFF0B2140)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    color = Color(0xFF29B6F6),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// 3D Police Shield with layered Arabic Typography and Flashing Red/Blue Lights
@Composable
private fun ShieldBadge3D(lightAlternator: Float) {
    Box(
        modifier = Modifier
            .size(width = 160.dp, height = 136.dp),
        contentAlignment = Alignment.Center
    ) {
        // Canvas for Shield geometry, lights, and star
        Canvas(modifier = Modifier.fillMaxSize()) {
            draw3DPoliceShield(size, lightAlternator)
        }

        // Layered 3D Arabic Text Centered on the Shield
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            // "شرطة"
            Text(
                text = "شرطة",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            // "الأطفال" with 3D shadow effect
            Box {
                // Drop shadow
                Text(
                    text = "الأطفال",
                    color = GoldShadow,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 2.dp, start = 1.dp)
                )
                // Foreground Gold
                Text(
                    text = "الأطفال",
                    color = PoliceGold,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

// ==========================================
// 2. CARD 1: POLICE CARS (Blue Card)
// ==========================================
@Composable
private fun PoliceCarsCard(
    modifier: Modifier,
    currentCarIndex: Int,
    cars: List<PoliceCarModel>,
    onSelectCar: (Int) -> Unit,
    onPrevCar: () -> Unit,
    onNextCar: () -> Unit
) {
    val selectedCar = cars[currentCarIndex]

    Card(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF072144))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFF0052CC))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF0070BA), Color(0xFF0091EA))
                        )
                    )
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "سيارات الشرطة",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "🚓", fontSize = 16.sp)
                }
            }

            // Main Car Stage (Showroom with Podium & Skyline)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF0B3B73), Color(0xFF082245))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawPoliceCruiserShowroom(size, currentCarIndex)
                }
            }

            // Bottom Carousel Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF061833))
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Prev Arrow
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0D3261))
                        .clickable { onPrevCar() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "السابق",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // 3 Thumbnails
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    cars.forEachIndexed { index, car ->
                        val isSelected = (index == currentCarIndex)
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 38.dp else 34.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color(0xFF1565C0) else Color(0xFF0A2346))
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.5.dp,
                                    color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF2962FF),
                                    shape = CircleShape
                                )
                                .clickable { onSelectCar(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (index) {
                                    0 -> "🚙"
                                    1 -> "🚓"
                                    else -> "🚐"
                                },
                                fontSize = if (isSelected) 18.sp else 15.sp
                            )
                        }
                    }
                }

                // Next Arrow
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0D3261))
                        .clickable { onNextCar() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "التالي",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. CARD 2: POLICE SIRENS (Red Card)
// ==========================================
@Composable
private fun PoliceSirensCard(
    modifier: Modifier,
    isPlaying: Boolean,
    pulseScale: Float,
    soundWavePhase: Float,
    onToggleMainSiren: () -> Unit,
    onPlayWhistle: () -> Unit,
    onPlayChirp: () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B070D))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFF3D00), Color(0xFFB71C1C))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFC62828), Color(0xFFE53935))
                        )
                    )
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "صفارات الشرطة",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "🚨", fontSize = 16.sp)
                }
            }

            // Siren Stage with pulsating Beacon & Sound Waves
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (isPlaying) Color(0xFF5A0E17) else Color(0xFF26080D),
                                Color(0xFF0F0204)
                            )
                        )
                    )
                    .clickable { onToggleMainSiren() },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawSirenBeacon(
                        size = size,
                        isPlaying = isPlaying,
                        pulseScale = if (isPlaying) pulseScale else 1f,
                        soundWavePhase = soundWavePhase
                    )
                }
            }

            // Bottom 3 Round Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF140206))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Left Small Button (Secondary Siren / Whistle)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF330910))
                        .border(1.5.dp, Color(0xFFFF5252), CircleShape)
                        .clickable { onPlayWhistle() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🚨", fontSize = 16.sp)
                }

                // Center BIG Play / Stop Green Button
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    if (isPlaying) Color(0xFFFF5252) else Color(0xFF00E676),
                                    if (isPlaying) Color(0xFFD50000) else Color(0xFF00A344)
                                )
                            )
                        )
                        .border(
                            width = 2.5.dp,
                            color = if (isPlaying) Color(0xFFFF8A80) else Color(0xFFB9F6CA),
                            shape = CircleShape
                        )
                        .clickable { onToggleMainSiren() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isPlaying) {
                        // Stop Icon
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(Color.White, RoundedCornerShape(3.dp))
                        )
                    } else {
                        // Play Icon
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "تشغيل الصفارة",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Right Small Button (Radio Chirp / Horn)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF330910))
                        .border(1.5.dp, Color(0xFFFF5252), CircleShape)
                        .clickable { onPlayChirp() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🚨", fontSize = 16.sp)
                }
            }
        }
    }
}

// ==========================================
// 4. CARD 3: POLICE GAMES (Purple Card)
// ==========================================
@Composable
private fun PoliceGamesCard(
    modifier: Modifier,
    onGameClick: (MiniGame) -> Unit
) {
    val games = remember {
        listOf(
            MiniGame("game_search", "ابحث عن الشرطة", "🔍", Color(0xFF2E7D32), "بحث"),
            MiniGame("game_match", "طابق الشارة", "🛡️", Color(0xFF1565C0), "تطابق"),
            MiniGame("game_puzzle", "تركيب الصورة", "🧩", Color(0xFF0277BD), "تركيب"),
            MiniGame("game_stars", "اجمع النجوم", "🏆", Color(0xFFC62828), "تحدي")
        )
    }

    Card(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF240A38))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFE040FB), Color(0xFF7B1FA2))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF8E24AA), Color(0xFFAB47BC))
                        )
                    )
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "ألعاب",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "🎮", fontSize = 16.sp)
                }
            }

            // 2x2 Mini Game Tiles Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top Row: Game 1 (Search - Green) & Game 2 (Match - Blue)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniGameTile(
                        modifier = Modifier.weight(1f),
                        game = games[0],
                        drawContent = { drawSearchPoliceMiniTile(size) },
                        onClick = { onGameClick(games[0]) }
                    )
                    MiniGameTile(
                        modifier = Modifier.weight(1f),
                        game = games[1],
                        drawContent = { drawMatchBadgeMiniTile(size) },
                        onClick = { onGameClick(games[1]) }
                    )
                }

                // Bottom Row: Game 3 (Puzzle - Cyan/Blue) & Game 4 (Stars - Red)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniGameTile(
                        modifier = Modifier.weight(1f),
                        game = games[2],
                        drawContent = { drawPuzzlePiecesMiniTile(size) },
                        onClick = { onGameClick(games[2]) }
                    )
                    MiniGameTile(
                        modifier = Modifier.weight(1f),
                        game = games[3],
                        drawContent = { drawTrophyStarsMiniTile(size) },
                        onClick = { onGameClick(games[3]) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniGameTile(
    modifier: Modifier,
    game: MiniGame,
    drawContent: DrawScope.() -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(86.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        game.bgColor.copy(alpha = 0.95f),
                        game.bgColor.copy(alpha = 0.7f)
                    )
                )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        // Visual graphic representation in top half
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawContent()
            }
        }

        // Bottom label bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0xFF0B192E).copy(alpha = 0.85f))
                .padding(vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = game.title,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==========================================
// 5. CARD 4: POLICE STORIES (Green Card)
// ==========================================
@Composable
private fun PoliceStoriesCard(
    modifier: Modifier,
    onReadNewStory: () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF092912))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF00E676), Color(0xFF1B5E20))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF2E7D32), Color(0xFF43A047))
                        )
                    )
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "قصص الشرطة",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "📖", fontSize = 16.sp)
                }
            }

            // Warm Park Story Scene with Officer & Child
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF388E3C), Color(0xFF1B5E20))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawPoliceOfficerAndKidPark(size)
                }
            }

            // Bottom Full-Width Action Button: "اقرأ قصة جديدة"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF06180A))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onReadNewStory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .shadow(6.dp, RoundedCornerShape(20.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00C853)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "اقرأ قصة جديدة",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "📖", fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. BOTTOM NAVIGATION BAR (Glassmorphism)
// ==========================================
@Composable
private fun BottomGlassNavigationBar(
    modifier: Modifier,
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .shadow(16.dp, RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0E2548).copy(alpha = 0.95f),
                        Color(0xFF051020).copy(alpha = 0.98f)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF00E5FF).copy(alpha = 0.6f),
                        Color(0xFF2979FF).copy(alpha = 0.3f),
                        Color(0xFF00E5FF).copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 0: مكافآت ⭐
            BottomNavItem(
                icon = "⭐",
                label = "مكافآت",
                isSelected = (selectedIndex == 0),
                onClick = { onSelectTab(0) }
            )

            // Tab 1: الإنجازات 🏅
            BottomNavItem(
                icon = "🏅",
                label = "الإنجازات",
                isSelected = (selectedIndex == 1),
                onClick = { onSelectTab(1) }
            )

            // Tab 2: التحديات 🛡️
            BottomNavItem(
                icon = "🛡️",
                label = "التحديات",
                isSelected = (selectedIndex == 2),
                onClick = { onSelectTab(2) }
            )

            // Tab 3: الرئيسية 🏠 (Active Highlighted Gold)
            BottomNavItem(
                icon = "🏠",
                label = "الرئيسية",
                isSelected = (selectedIndex == 3),
                isHomeActive = true,
                onClick = { onSelectTab(3) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: String,
    label: String,
    isSelected: Boolean,
    isHomeActive: Boolean = false,
    onClick: () -> Unit
) {
    if (isSelected && isHomeActive) {
        // Highlighted Active Container with Golden Border
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1C4782),
                            Color(0xFF0E284D)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(PoliceGold, Color(0xFFFFA000))
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = icon, fontSize = 20.sp)
                Text(
                    text = label,
                    color = PoliceGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else {
        // Standard Tab Item
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .clickable { onClick() }
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                text = icon,
                fontSize = if (isSelected) 20.sp else 18.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = if (isSelected) Color(0xFF00E5FF) else Color(0xFFB0BEC5),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

// ==========================================
// CANVASES & CUSTOM VECTOR DRAWING LOGIC
// ==========================================

// 1. Sky & City Skyline behind the Header
private fun DrawScope.drawHeaderSkyline(canvasSize: Size) {
    val width = canvasSize.width
    val height = canvasSize.height

    // Clouds
    val cloudColor = Color.White.copy(alpha = 0.25f)
    drawCircle(cloudColor, radius = 35f, center = Offset(width * 0.15f, 50f))
    drawCircle(cloudColor, radius = 45f, center = Offset(width * 0.22f, 40f))
    drawCircle(cloudColor, radius = 30f, center = Offset(width * 0.28f, 55f))

    drawCircle(cloudColor, radius = 30f, center = Offset(width * 0.75f, 60f))
    drawCircle(cloudColor, radius = 42f, center = Offset(width * 0.82f, 48f))
    drawCircle(cloudColor, radius = 32f, center = Offset(width * 0.88f, 62f))

    // City Skyline Silhouette
    val skylinePath = Path().apply {
        moveTo(0f, height)
        lineTo(0f, height * 0.65f)
        lineTo(width * 0.05f, height * 0.65f)
        lineTo(width * 0.05f, height * 0.45f)
        lineTo(width * 0.12f, height * 0.45f)
        lineTo(width * 0.12f, height * 0.70f)
        lineTo(width * 0.18f, height * 0.70f)
        lineTo(width * 0.18f, height * 0.40f)
        lineTo(width * 0.25f, height * 0.40f)
        lineTo(width * 0.25f, height * 0.55f)
        lineTo(width * 0.32f, height * 0.55f)
        lineTo(width * 0.32f, height * 0.75f)

        // Center gap for shield
        lineTo(width * 0.68f, height * 0.75f)
        lineTo(width * 0.68f, height * 0.50f)
        lineTo(width * 0.75f, height * 0.50f)
        lineTo(width * 0.75f, height * 0.38f)
        lineTo(width * 0.82f, height * 0.38f)
        lineTo(width * 0.82f, height * 0.60f)
        lineTo(width * 0.90f, height * 0.60f)
        lineTo(width * 0.90f, height * 0.48f)
        lineTo(width, height * 0.48f)
        lineTo(width, height)
        close()
    }

    drawPath(
        path = skylinePath,
        color = DeepCityNavy.copy(alpha = 0.55f)
    )

    // Lit windows on buildings
    val windowColor = Color(0xFF80D8FF).copy(alpha = 0.4f)
    for (i in 0..6) {
        drawRect(
            color = windowColor,
            topLeft = Offset(width * 0.07f, height * (0.50f + i * 0.03f)),
            size = Size(8f, 6f)
        )
        drawRect(
            color = windowColor,
            topLeft = Offset(width * 0.20f, height * (0.45f + i * 0.03f)),
            size = Size(8f, 6f)
        )
        drawRect(
            color = windowColor,
            topLeft = Offset(width * 0.77f, height * (0.42f + i * 0.03f)),
            size = Size(8f, 6f)
        )
    }
}

// 2. 3D Police Shield with Wings, Flashing Lights & Star
private fun DrawScope.draw3DPoliceShield(canvasSize: Size, lightAlternator: Float) {
    val w = canvasSize.width
    val h = canvasSize.height
    val cx = w / 2f

    // Top Emergency Lights Bar (Red & Blue with Glow)
    val redActive = lightAlternator > 0.5f
    val redGlow = if (redActive) 1.3f else 0.7f
    val blueGlow = if (!redActive) 1.3f else 0.7f

    // Red Light (Left)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                EmergencyRed.copy(alpha = 0.9f * redGlow),
                EmergencyRed.copy(alpha = 0.3f),
                Color.Transparent
            ),
            center = Offset(cx - 30f, 16f),
            radius = 24f * redGlow
        ),
        radius = 24f * redGlow,
        center = Offset(cx - 30f, 16f)
    )
    drawRoundRect(
        color = EmergencyRed,
        topLeft = Offset(cx - 44f, 8f),
        size = Size(28f, 16f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Blue Light (Right)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                EmergencyBlue.copy(alpha = 0.9f * blueGlow),
                EmergencyBlue.copy(alpha = 0.3f),
                Color.Transparent
            ),
            center = Offset(cx + 30f, 16f),
            radius = 24f * blueGlow
        ),
        radius = 24f * blueGlow,
        center = Offset(cx + 30f, 16f)
    )
    drawRoundRect(
        color = EmergencyBlue,
        topLeft = Offset(cx + 16f, 8f),
        size = Size(28f, 16f),
        cornerRadius = CornerRadius(6f, 6f)
    )

    // Center Siren Mount Bar
    drawRoundRect(
        color = Color(0xFF263238),
        topLeft = Offset(cx - 16f, 10f),
        size = Size(32f, 14f),
        cornerRadius = CornerRadius(4f, 4f)
    )

    // Side Wings (Left & Right)
    val leftWingPath = Path().apply {
        moveTo(cx - 55f, 35f)
        lineTo(cx - 75f, 45f)
        lineTo(cx - 65f, 65f)
        lineTo(cx - 50f, 60f)
        close()
    }
    drawPath(path = leftWingPath, color = Color(0xFF103058))
    drawPath(path = leftWingPath, color = Color(0xFF1E88E5), style = Stroke(width = 2.5f))

    val rightWingPath = Path().apply {
        moveTo(cx + 55f, 35f)
        lineTo(cx + 75f, 45f)
        lineTo(cx + 65f, 65f)
        lineTo(cx + 50f, 60f)
        close()
    }
    drawPath(path = rightWingPath, color = Color(0xFF103058))
    drawPath(path = rightWingPath, color = Color(0xFF1E88E5), style = Stroke(width = 2.5f))

    // Main 3D Shield Path
    val shieldPath = Path().apply {
        moveTo(cx - 60f, 22f)
        lineTo(cx + 60f, 22f)
        cubicTo(cx + 64f, 50f, cx + 58f, 85f, cx, h - 18f)
        cubicTo(cx - 58f, 85f, cx - 64f, 50f, cx - 60f, 22f)
        close()
    }

    // Shield 3D Outer Border / Shadow
    drawPath(
        path = shieldPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF1E88E5), Color(0xFF0D47A1), Color(0xFF04142B))
        )
    )

    // Shield Inner Body
    val innerShieldPath = Path().apply {
        moveTo(cx - 54f, 26f)
        lineTo(cx + 54f, 26f)
        cubicTo(cx + 56f, 50f, cx + 52f, 82f, cx, h - 22f)
        cubicTo(cx - 52f, 82f, cx - 56f, 50f, cx - 54f, 26f)
        close()
    }

    drawPath(
        path = innerShieldPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF153765), Color(0xFF0A1E3B), Color(0xFF051226))
        )
    )

    // Shield Golden Edge Accent
    drawPath(
        path = innerShieldPath,
        color = Color(0xFF00E5FF).copy(alpha = 0.5f),
        style = Stroke(width = 2f)
    )

    // Bottom Golden 5-pointed Star
    draw5PointStar(
        center = Offset(cx, h - 14f),
        radius = 12f,
        color = PoliceGold
    )
}

// 3. Officer Avatar in Profile Pill
private fun DrawScope.drawPoliceOfficerAvatar(canvasSize: Size) {
    val cx = canvasSize.width / 2f
    val cy = canvasSize.height / 2f

    // Face skin
    drawCircle(
        color = Color(0xFFFFCC80),
        radius = 14f,
        center = Offset(cx, cy + 2f)
    )

    // Friendly Eyes
    drawCircle(color = Color(0xFF3E2723), radius = 2.2f, center = Offset(cx - 5f, cy + 1f))
    drawCircle(color = Color(0xFF3E2723), radius = 2.2f, center = Offset(cx + 5f, cy + 1f))

    // Smiling mouth
    val smilePath = Path().apply {
        moveTo(cx - 4f, cy + 7f)
        quadraticTo(cx, cy + 11f, cx + 4f, cy + 7f)
    }
    drawPath(path = smilePath, color = Color(0xFFD84315), style = Stroke(width = 2f, cap = StrokeCap.Round))

    // Police Officer Cap (Blue with Gold badge & Visor)
    val capPath = Path().apply {
        moveTo(cx - 16f, cy - 1f)
        lineTo(cx + 16f, cy - 1f)
        lineTo(cx + 14f, cy - 13f)
        quadraticTo(cx, cy - 18f, cx - 14f, cy - 13f)
        close()
    }
    drawPath(path = capPath, color = Color(0xFF0D47A1))

    // Cap Visor (Black)
    val visorPath = Path().apply {
        moveTo(cx - 15f, cy - 1f)
        quadraticTo(cx, cy + 3f, cx + 15f, cy - 1f)
    }
    drawPath(path = visorPath, color = Color(0xFF212121), style = Stroke(width = 3.5f, cap = StrokeCap.Round))

    // Gold Star badge on cap
    drawCircle(color = PoliceGold, radius = 3.5f, center = Offset(cx, cy - 7f))

    // Uniform Collar (Blue & Gold)
    val collarPath = Path().apply {
        moveTo(cx - 14f, canvasSize.height)
        lineTo(cx - 8f, cy + 14f)
        lineTo(cx, cy + 17f)
        lineTo(cx + 8f, cy + 14f)
        lineTo(cx + 14f, canvasSize.height)
        close()
    }
    drawPath(path = collarPath, color = Color(0xFF1565C0))
}

// 4. Police Cruiser Showroom Illustration (Cars Card)
private fun DrawScope.drawPoliceCruiserShowroom(canvasSize: Size, carIndex: Int) {
    val w = canvasSize.width
    val h = canvasSize.height
    val cx = w / 2f
    val cy = h / 2f

    // Background Skyline
    for (i in 0..7) {
        val bh = 30f + (i % 3) * 20f
        drawRect(
            color = Color(0xFF0F325E).copy(alpha = 0.6f),
            topLeft = Offset(w * (0.12f * i), 10f),
            size = Size(w * 0.10f, bh)
        )
    }

    // Circular Glowing Showroom Podium Platform
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.5f), Color(0xFF0070BA), Color(0xFF051D3D)),
            center = Offset(cx, cy + 38f),
            radius = w * 0.42f
        ),
        topLeft = Offset(cx - w * 0.42f, cy + 18f),
        size = Size(w * 0.84f, 44f)
    )
    drawOval(
        color = Color(0xFF00E5FF),
        topLeft = Offset(cx - w * 0.42f, cy + 18f),
        size = Size(w * 0.84f, 44f),
        style = Stroke(width = 2.5f)
    )

    // Police Car Body
    val carWidth = w * 0.65f
    val carLeft = cx - carWidth / 2f

    // Car Shadow
    drawOval(
        color = Color.Black.copy(alpha = 0.5f),
        topLeft = Offset(carLeft - 10f, cy + 24f),
        size = Size(carWidth + 20f, 18f)
    )

    // Car Wheels
    drawCircle(color = Color(0xFF212121), radius = 14f, center = Offset(carLeft + 25f, cy + 28f))
    drawCircle(color = Color(0xFF90A4AE), radius = 7f, center = Offset(carLeft + 25f, cy + 28f))

    drawCircle(color = Color(0xFF212121), radius = 14f, center = Offset(carLeft + carWidth - 25f, cy + 28f))
    drawCircle(color = Color(0xFF90A4AE), radius = 7f, center = Offset(carLeft + carWidth - 25f, cy + 28f))

    // Car Main Lower Body (Dark Navy / Black)
    val carBodyPath = Path().apply {
        moveTo(carLeft, cy + 24f)
        lineTo(carLeft + 8f, cy + 8f)
        lineTo(carLeft + carWidth * 0.28f, cy + 6f)
        lineTo(carLeft + carWidth * 0.42f, cy - 14f)
        lineTo(carLeft + carWidth * 0.72f, cy - 14f)
        lineTo(carLeft + carWidth * 0.84f, cy + 6f)
        lineTo(carLeft + carWidth, cy + 10f)
        lineTo(carLeft + carWidth, cy + 24f)
        close()
    }
    drawPath(path = carBodyPath, color = Color(0xFF1A1A24))

    // White Middle Doors (Classic Interceptor Look)
    val whiteDoorPath = Path().apply {
        moveTo(carLeft + carWidth * 0.35f, cy + 6f)
        lineTo(carLeft + carWidth * 0.70f, cy + 6f)
        lineTo(carLeft + carWidth * 0.70f, cy + 22f)
        lineTo(carLeft + carWidth * 0.35f, cy + 22f)
        close()
    }
    drawPath(path = whiteDoorPath, color = Color.White)

    // Glass Windshield & Windows (Sky Blue tint)
    val windowPath = Path().apply {
        moveTo(carLeft + carWidth * 0.32f, cy + 4f)
        lineTo(carLeft + carWidth * 0.44f, cy - 12f)
        lineTo(carLeft + carWidth * 0.70f, cy - 12f)
        lineTo(carLeft + carWidth * 0.80f, cy + 4f)
        close()
    }
    drawPath(path = windowPath, color = Color(0xFF81D4FA).copy(alpha = 0.85f))

    // Roof Lightbar (Red on Left, Blue on Right)
    drawRoundRect(
        color = Color(0xFF37474F),
        topLeft = Offset(carLeft + carWidth * 0.50f, cy - 18f),
        size = Size(carWidth * 0.16f, 4f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    drawRoundRect(
        color = EmergencyRed,
        topLeft = Offset(carLeft + carWidth * 0.50f, cy - 22f),
        size = Size(carWidth * 0.08f, 5f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    drawRoundRect(
        color = EmergencyBlue,
        topLeft = Offset(carLeft + carWidth * 0.58f, cy - 22f),
        size = Size(carWidth * 0.08f, 5f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Headlights Beam
    drawCircle(color = Color(0xFFFFF59D), radius = 5f, center = Offset(carLeft + 4f, cy + 14f))
}

// 5. Siren Beacon & Sound Waves Illustration (Sirens Card)
private fun DrawScope.drawSirenBeacon(
    size: Size,
    isPlaying: Boolean,
    pulseScale: Float,
    soundWavePhase: Float
) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f + 4f

    // Radiating Sound Waves `((( 🚨 )))`
    val waveColor = if (isPlaying) Color(0xFFFF5252) else Color(0xFFFF1744).copy(alpha = 0.3f)

    for (i in 1..3) {
        val waveRadius = (45f + i * 20f) * if (isPlaying) (1f + soundWavePhase * 0.3f) else 1f
        val alpha = if (isPlaying) (1f - (i * 0.25f)).coerceIn(0.2f, 1f) else 0.25f

        // Left Arc
        drawArc(
            color = waveColor.copy(alpha = alpha),
            startAngle = 140f,
            sweepAngle = 80f,
            useCenter = false,
            topLeft = Offset(cx - waveRadius - 10f, cy - waveRadius),
            size = Size(waveRadius * 2f, waveRadius * 2f),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        // Right Arc
        drawArc(
            color = waveColor.copy(alpha = alpha),
            startAngle = -40f,
            sweepAngle = 80f,
            useCenter = false,
            topLeft = Offset(cx - waveRadius + 10f, cy - waveRadius),
            size = Size(waveRadius * 2f, waveRadius * 2f),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
    }

    // Beacon Base (Black Heavy Mount)
    drawOval(
        color = Color(0xFF212121),
        topLeft = Offset(cx - 38f, cy + 22f),
        size = Size(76f, 22f)
    )
    drawOval(
        color = Color(0xFF424242),
        topLeft = Offset(cx - 35f, cy + 20f),
        size = Size(70f, 18f)
    )

    // Red Glowing Beacon Dome
    val domeWidth = 56f * pulseScale
    val domeHeight = 60f * pulseScale
    val domeLeft = cx - domeWidth / 2f
    val domeTop = cy - domeHeight / 2f

    // Glow halo behind
    if (isPlaying) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    EmergencyRed.copy(alpha = 0.8f),
                    EmergencyRed.copy(alpha = 0.2f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = 65f * pulseScale
            ),
            radius = 65f * pulseScale,
            center = Offset(cx, cy)
        )
    }

    // Dome Path
    val domePath = Path().apply {
        moveTo(domeLeft + 8f, cy + 22f)
        lineTo(domeLeft + domeWidth - 8f, cy + 22f)
        lineTo(domeLeft + domeWidth - 4f, domeTop + 18f)
        cubicTo(domeLeft + domeWidth, domeTop, domeLeft, domeTop, domeLeft + 4f, domeTop + 18f)
        close()
    }

    drawPath(
        path = domePath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFF8A80),
                EmergencyRed,
                Color(0xFFB71C1C)
            )
        )
    )

    // Internal Lamp Light Bulb
    drawCircle(
        color = Color(0xFFFFF59D),
        radius = 12f * pulseScale,
        center = Offset(cx, cy + 2f)
    )
    drawCircle(
        color = Color.White,
        radius = 6f * pulseScale,
        center = Offset(cx, cy + 2f)
    )

    // Glass Reflection Highlight
    drawArc(
        color = Color.White.copy(alpha = 0.6f),
        startAngle = 180f,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = Offset(domeLeft + 6f, domeTop + 4f),
        size = Size(domeWidth - 12f, domeHeight - 14f),
        style = Stroke(width = 3.5f, cap = StrokeCap.Round)
    )
}

// 6. Police Officer & Kid in Park Illustration (Stories Card)
private fun DrawScope.drawPoliceOfficerAndKidPark(canvasSize: Size) {
    val w = canvasSize.width
    val h = canvasSize.height

    // Sunlit Park Background (Green grass, path, trees)
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF81C784), Color(0xFF4CAF50), Color(0xFF2E7D32))
        ),
        topLeft = Offset(0f, 0f),
        size = Size(w, h)
    )

    // Distant City Skyline
    for (i in 0..5) {
        drawRect(
            color = Color(0xFFE0F2F1).copy(alpha = 0.4f),
            topLeft = Offset(w * (0.16f * i), 4f),
            size = Size(w * 0.12f, 26f)
        )
    }

    // Lush Tree Foliage
    drawCircle(Color(0xFF388E3C), radius = 40f, center = Offset(30f, 35f))
    drawCircle(Color(0xFF2E7D32), radius = 35f, center = Offset(w - 25f, 35f))

    // Street Lamp Post
    drawLine(
        color = Color(0xFF37474F),
        start = Offset(w - 30f, 20f),
        end = Offset(w - 30f, h - 10f),
        strokeWidth = 3.5f
    )
    drawCircle(Color(0xFFFFEE58), radius = 6f, center = Offset(w - 30f, 20f))

    // Paved Pathway (Tan stone curve)
    val pathShape = Path().apply {
        moveTo(w * 0.15f, h)
        quadraticTo(w * 0.45f, h * 0.65f, w * 0.85f, h)
        close()
    }
    drawPath(path = pathShape, color = Color(0xFFFFE082).copy(alpha = 0.7f))

    // Kneeling Officer (Left side, friendly pose talking with finger gesture)
    val ox = w * 0.38f
    val oy = h * 0.58f

    // Officer Body & Blue Uniform
    drawRoundRect(
        color = Color(0xFF1565C0),
        topLeft = Offset(ox - 16f, oy - 14f),
        size = Size(32f, 36f),
        cornerRadius = CornerRadius(8f, 8f)
    )

    // Officer Head & Cap
    drawCircle(color = Color(0xFFFFCC80), radius = 13f, center = Offset(ox, oy - 26f))
    // Police Cap
    drawArc(
        color = Color(0xFF0D47A1),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(ox - 14f, oy - 42f),
        size = Size(28f, 24f)
    )
    // Gold badge on cap
    drawCircle(color = PoliceGold, radius = 2.5f, center = Offset(ox, oy - 32f))

    // Officer Smile
    val officerSmile = Path().apply {
        moveTo(ox - 3f, oy - 22f)
        quadraticTo(ox + 2f, oy - 18f, ox + 6f, oy - 22f)
    }
    drawPath(path = officerSmile, color = Color(0xFFD84315), style = Stroke(width = 1.5f))

    // Little Boy (Right side, sitting happily listening)
    val kx = w * 0.72f
    val ky = h * 0.65f

    // Boy Red Shirt
    drawRoundRect(
        color = Color(0xFFE53935),
        topLeft = Offset(kx - 12f, ky - 10f),
        size = Size(24f, 22f),
        cornerRadius = CornerRadius(6f, 6f)
    )
    // Boy Blue Pants
    drawRoundRect(
        color = Color(0xFF1976D2),
        topLeft = Offset(kx - 14f, ky + 10f),
        size = Size(28f, 12f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    // Boy Head & Hair
    drawCircle(color = Color(0xFFFFE082), radius = 11f, center = Offset(kx, ky - 20f))
    drawArc(
        color = Color(0xFF5D4037),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(kx - 12f, ky - 34f),
        size = Size(24f, 20f)
    )
    // Boy Happy Smile
    val boySmile = Path().apply {
        moveTo(kx - 4f, ky - 18f)
        quadraticTo(kx, ky - 14f, kx + 4f, ky - 18f)
    }
    drawPath(path = boySmile, color = Color(0xFFD84315), style = Stroke(width = 1.5f))
}

// 7. Mini Game Tile Visual Renderers
private fun DrawScope.drawSearchPoliceMiniTile(size: Size) {
    val cx = size.width / 2f
    val cy = size.height / 2f - 2f

    // Mini Police Car
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(cx - 20f, cy - 6f),
        size = Size(40f, 16f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawRoundRect(
        color = Color(0xFF212121),
        topLeft = Offset(cx - 10f, cy - 6f),
        size = Size(20f, 16f),
        cornerRadius = CornerRadius(2f, 2f)
    )
    // Roof Siren
    drawCircle(EmergencyRed, radius = 2.5f, center = Offset(cx - 3f, cy - 9f))
    drawCircle(EmergencyBlue, radius = 2.5f, center = Offset(cx + 3f, cy - 9f))

    // Big Magnifying Glass over car
    drawCircle(
        color = Color(0xFF80D8FF).copy(alpha = 0.4f),
        radius = 18f,
        center = Offset(cx - 8f, cy - 2f)
    )
    drawCircle(
        color = Color(0xFF212121),
        radius = 18f,
        center = Offset(cx - 8f, cy - 2f),
        style = Stroke(width = 3.5f)
    )
    // Handle
    drawLine(
        color = Color(0xFF212121),
        start = Offset(cx - 20f, cy + 10f),
        end = Offset(cx - 32f, cy + 22f),
        strokeWidth = 4.5f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawMatchBadgeMiniTile(size: Size) {
    val cx = size.width / 2f
    val cy = size.height / 2f - 2f

    // Golden-Star Police Shield
    val badgePath = Path().apply {
        moveTo(cx - 18f, cy - 16f)
        lineTo(cx + 18f, cy - 16f)
        cubicTo(cx + 20f, cy + 2f, cx + 18f, cy + 14f, cx, cy + 22f)
        cubicTo(cx - 18f, cy + 14f, cx - 20f, cy + 2f, cx - 18f, cy - 16f)
        close()
    }
    drawPath(path = badgePath, color = Color(0xFF0D47A1))
    drawPath(path = badgePath, color = PoliceGold, style = Stroke(width = 2.5f))

    // Golden 5-point Star in center
    draw5PointStar(center = Offset(cx, cy + 2f), radius = 8f, color = PoliceGold)
    // Small companion stars
    drawCircle(PoliceGold, radius = 2f, center = Offset(cx - 10f, cy - 6f))
    drawCircle(PoliceGold, radius = 2f, center = Offset(cx + 10f, cy - 6f))
}

private fun DrawScope.drawPuzzlePiecesMiniTile(size: Size) {
    val cx = size.width / 2f
    val cy = size.height / 2f - 2f

    // Interlocking Puzzle Jigsaw Pieces (Yellow & Orange)
    // Yellow Piece
    drawRoundRect(
        color = Color(0xFFFFB300),
        topLeft = Offset(cx - 16f, cy - 16f),
        size = Size(20f, 20f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawCircle(Color(0xFFFFB300), radius = 5f, center = Offset(cx + 4f, cy - 6f))

    // Orange Piece
    drawRoundRect(
        color = Color(0xFFFB8C00),
        topLeft = Offset(cx - 4f, cy - 4f),
        size = Size(20f, 20f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    drawCircle(Color(0xFFFB8C00), radius = 5f, center = Offset(cx + 6f, cy + 16f))

    // Blue Piece
    drawRoundRect(
        color = Color(0xFF1976D2),
        topLeft = Offset(cx - 20f, cy + 2f),
        size = Size(18f, 18f),
        cornerRadius = CornerRadius(4f, 4f)
    )
}

private fun DrawScope.drawTrophyStarsMiniTile(size: Size) {
    val cx = size.width / 2f
    val cy = size.height / 2f - 2f

    // Golden Trophy Cup
    val cupPath = Path().apply {
        moveTo(cx - 16f, cy - 14f)
        lineTo(cx + 16f, cy - 14f)
        lineTo(cx + 14f, cy + 2f)
        cubicTo(cx + 10f, cy + 10f, cx - 10f, cy + 10f, cx - 14f, cy + 2f)
        close()
    }
    drawPath(path = cupPath, color = PoliceGold)

    // Handles
    drawArc(
        color = PoliceGold,
        startAngle = 90f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - 22f, cy - 12f),
        size = Size(12f, 14f),
        style = Stroke(width = 2.5f)
    )
    drawArc(
        color = PoliceGold,
        startAngle = -90f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx + 10f, cy - 12f),
        size = Size(12f, 14f),
        style = Stroke(width = 2.5f)
    )

    // Stem & Base
    drawRect(color = PoliceGold, topLeft = Offset(cx - 3f, cy + 10f), size = Size(6f, 6f))
    drawRoundRect(
        color = Color(0xFFFFA000),
        topLeft = Offset(cx - 12f, cy + 16f),
        size = Size(24f, 6f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Stars around trophy
    draw5PointStar(center = Offset(cx - 18f, cy + 6f), radius = 4f, color = PoliceGold)
    draw5PointStar(center = Offset(cx + 18f, cy + 6f), radius = 4f, color = PoliceGold)
}

// Utility: Draw 5-Point Star
private fun DrawScope.draw5PointStar(center: Offset, radius: Float, color: Color) {
    val path = Path()
    val innerRadius = radius * 0.45f
    val section = (Math.PI / 5.0)

    for (i in 0 until 10) {
        val r = if (i % 2 == 0) radius else innerRadius
        val angle = i * section - Math.PI / 2.0
        val x = center.x + (r * cos(angle)).toFloat()
        val y = center.y + (r * sin(angle)).toFloat()

        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path = path, color = color)
}

// ==========================================
// 8. DIALOG IMPLEMENTATIONS
// ==========================================

// Parent Gate Dialog (Math Challenge)
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
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2646))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🔒", fontSize = 32.sp)
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
                        .background(Color(0xFF163A69), RoundedCornerShape(12.dp))
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
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF1976D2)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "الإجابة غير صحيحة، حاول مجدداً!",
                        color = Color(0xFFFF5252),
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
                        shape = RoundedCornerShape(12.dp)
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("تأكيد", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Settings Dialog (Dialect & Extra Controls)
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
            shape = RoundedCornerShape(22.dp),
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
                        text = "⚙️ إعدادات شرطة الأطفال",
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

                // Dialect Options Grid
                Dialect.entries.forEach { dialect ->
                    val isSelected = (dialect == selectedDialect)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
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

                // Quick Action Buttons
                Button(
                    onClick = onOpenDialer,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0091EA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📞 لوحة الاتصال المخصصة", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onOpenSounds,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🔊 لوحة أصوات الشرطة الكاملة", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Story Reader Dialog with Narration
@Composable
private fun StoryReaderDialog(
    stories: List<PoliceStory>,
    audioPlayer: PoliceAudioPlayer?,
    onRewardClaimed: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var currentStoryIndex by remember { mutableIntStateOf(0) }
    val story = stories[currentStoryIndex]
    var isNarrating by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2B14))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📖 ${story.title}",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Story Illustration box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawPoliceOfficerAndKidPark(size)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Story text
                Text(
                    text = story.narration,
                    color = Color(0xFFE8F5E9),
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Moral badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1B5E20), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "💡 الحكمة: ${story.moral}",
                        color = PoliceGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Navigation & Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (currentStoryIndex > 0) currentStoryIndex--
                        },
                        enabled = currentStoryIndex > 0,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF33691E)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("السابقة")
                    }

                    Button(
                        onClick = {
                            onRewardClaimed(story.stars)
                            audioPlayer?.playPoliceHorn()
                            if (currentStoryIndex < stories.size - 1) {
                                currentStoryIndex++
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("أنهيت القصة ⭐ +${story.stars}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Mini Game Dialog (Interactive Challenges)
@Composable
private fun MiniGameDialog(
    game: MiniGame,
    audioPlayer: PoliceAudioPlayer?,
    onWinGame: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var gameState by remember { mutableIntStateOf(0) } // 0 = playing, 1 = won
    var scoreCount by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1D0B2E))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${game.iconEmoji} ${game.title}",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (gameState == 0) {
                    // Mini Game Play Area
                    Text(
                        text = when (game.id) {
                            "game_search" -> "انقر على سيارات الشرطة الأربعة لتأمين المدينة!"
                            "game_match" -> "طابق شارة الضابط الصحيحة للحصول على النجوم!"
                            "game_puzzle" -> "قم بتركيب قطع لغز الشرطة بالضغط عليها بالترتيب!"
                            else -> "اجمع 5 نجوم ذهبية لتنال وسام الشرف!"
                        },
                        color = Color(0xFFCE93D8),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simple Fun Tap Target Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (i in 1..4) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF3B175B))
                                    .border(2.dp, Color(0xFFAB47BC), RoundedCornerShape(14.dp))
                                    .clickable {
                                        scoreCount++
                                        audioPlayer?.playRadioChirp()
                                        if (scoreCount >= 4) {
                                            gameState = 1
                                            onWinGame(50)
                                            audioPlayer?.playPoliceHorn()
                                        }
                                    },
                                Alignment.Center
                            ) {
                                Text(
                                    text = if (scoreCount >= i) "⭐" else game.iconEmoji,
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }
                } else {
                    // Victory State
                    Text(text = "🎉 رائع يا بطل! 🎉", color = PoliceGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "لقد أكملت المهمة بنجاح وحصلت على 50 ⭐ ووسام جديد 🏆", color = Color.White, fontSize = 13.sp, textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("استلام المكافأة والعودة", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Rewards & Profile Dialog
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
                .padding(14.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2244))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "⭐ ملف الشرطي البطل ⭐", color = PoliceGold, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1976D2))
                        .border(3.dp, Color(0xFF00E5FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawPoliceOfficerAvatar(size)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "الرتبة: ضابط شرطة متميز 👮‍♂️", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .background(Color(0xFF133866), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "النقاط", color = Color(0xFF90CAF9), fontSize = 12.sp)
                            Text(text = "$points ⭐", color = PoliceGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .background(Color(0xFF133866), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "الأوسمة", color = Color(0xFF90CAF9), fontSize = 12.sp)
                            Text(text = "$trophies 🏆", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onOpenCertificate,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📜 عرض وسام وشهادة الشرف", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onOpenMissions,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0091EA)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🛡️ إنجاز المهام اليومية", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
