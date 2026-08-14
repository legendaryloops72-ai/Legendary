package com.aistudio.kidspolice.abcd

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.kidspolice.abcd.ui.theme.KidsPoliceTheme
import com.aistudio.kidspolice.abcd.ui.games.SimplePuzzleGameScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.concurrent.thread
import kotlin.math.sin

// --- SIREN AUDIO SYNTHESIZER ---
class SirenSynthesizer {
    private var audioTrack: AudioTrack? = null
    private var isPlaying = false

    fun startSiren(type: String) {
        stopSiren()
        isPlaying = true
        thread {
            val sampleRate = 22050
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = minBufferSize * 2
            
            val track = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
            )
            audioTrack = track
            
            try {
                track.play()
            } catch (e: Exception) {
                return@thread
            }

            val buffer = ShortArray(1024)
            var phase = 0.0
            var time = 0.0

            while (isPlaying) {
                for (i in buffer.indices) {
                    val freq = when (type) {
                        "fast" -> 550.0 + sin(time * 2.0 * Math.PI * 3.5) * 200.0
                        "slow" -> 600.0 + sin(time * 2.0 * Math.PI * 0.7) * 300.0
                        "warning" -> if (((time * 4.0).toInt() % 2) == 0) 850.0 else 450.0
                        "yelp" -> {
                            val sweep = (time * 12.0) % 1.0
                            400.0 + sweep * 800.0
                        }
                        else -> 500.0
                    }
                    
                    buffer[i] = (sin(phase) * 12000.0).toInt().toShort()
                    phase += 2.0 * Math.PI * freq / sampleRate
                    if (phase > 2.0 * Math.PI) {
                        phase -= 2.0 * Math.PI
                    }
                    time += 1.0 / sampleRate
                }
                track.write(buffer, 0, buffer.size)
            }
            try {
                track.stop()
                track.release()
            } catch (e: Exception) {}
        }
    }

    fun stopSiren() {
        isPlaying = false
        audioTrack?.let {
            try {
                it.stop()
                it.release()
            } catch (e: Exception) {}
        }
        audioTrack = null
    }
}

// --- SPEECH MANAGER (ARABIC TTS) ---
class SpeechManager(context: Context, private val onInitSuccess: () -> Unit) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar"))
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isReady = true
                tts?.setSpeechRate(0.85f) // Warm, readable tempo for kids
                onInitSuccess()
            }
        }
    }

    fun speak(text: String) {
        if (isReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ar_kidspolice_speech")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
    }
}

// --- DATA MODEL CLASSES ---
data class PoliceCharacter(
    val id: String,
    val name: String,
    val title: String,
    val emoji: String,
    val avatarColor: Color,
    val description: String,
    val speechText: String
)

data class BehaviorTask(
    val id: String,
    val title: String,
    val points: Int,
    val emoji: String,
    val description: String
)

data class EducationalStory(
    val id: String,
    val title: String,
    val emoji: String,
    val moral: String,
    val content: String
)

// --- MAIN ACTIVITY ENTRY POINT ---
class MainActivity : ComponentActivity() {
    private val sirenSynthesizer = SirenSynthesizer()
    private var speechManager: SpeechManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        speechManager = SpeechManager(this) {
            // Callback when ready
        }

        setContent {
            KidsPoliceTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KidsPoliceDashboard(
                        sirenSynthesizer = sirenSynthesizer,
                        speechManager = speechManager!!
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sirenSynthesizer.stopSiren()
        speechManager?.stop()
        speechManager?.shutdown()
    }
}

// --- DASHBOARD UI COMPOSE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidsPoliceDashboard(
    sirenSynthesizer: SirenSynthesizer,
    speechManager: SpeechManager
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Core App State
    var activeTab by rememberSaveable { mutableStateOf("none") } // "calls", "tasks", "stories", "sirens", "games", "superheroes"
    var currentScreenState by remember { mutableStateOf("splash") } // "splash", "dashboard", "sub_screen", "calling", "active_call"
    var activeStory by remember { mutableStateOf<EducationalStory?>(null) }
    var splashProgress by remember { mutableStateOf(0f) }
    
    // Active Call State
    var activeCharacter by remember { mutableStateOf<PoliceCharacter?>(null) }
    var callTimer by remember { mutableStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    
    // Score & Points State
    var userPoints by rememberSaveable { mutableStateOf(50) }
    var completedTasks by rememberSaveable { mutableStateOf(setOf<String>()) }

    // Constants & Resources
    val characters = listOf(
        PoliceCharacter(
            id = "brave_officer",
            name = "الشرطي شجاع",
            title = "شرطي الأمان والسلوك الحسن",
            emoji = "👮‍♂️",
            avatarColor = Color(0xFF29B6F6),
            description = "مكافأة وتشجيع للأطفال المطيعين الذين يسمعون كلام الوالدين ويحافظون على ترتيب غرفتهم.",
            speechText = "أهلاً يا بطل! أنا الشرطي شجاع من مركز شرطة الأطفال. لقد أخبرني والداك أنك طفل مطيع وتستمع لكلامهما وتحب تناول الفطور الصحي وتفرش أسنانك! أنا فخور جداً بك وأرسل لك وسام البطل الشجاع! استمر في هذا السلوك الرائع دائماً وسأزورك قريباً لأقدم لك هدية مميزة!"
        ),
        PoliceCharacter(
            id = "disciplined_officer",
            name = "الشرطي حازم",
            title = "شرطي الفوضى وتعديل السلوك",
            emoji = "🛡️",
            avatarColor = Color(0xFFEF5350),
            description = "مخصص لتنبيه وتعديل سلوك الأطفال الفوضويين أو الذين يرفضون النوم مبكراً.",
            speechText = "مرحباً! أنا الشرطي حازم من شرطة التدخل السريع للأطفال. لقد سمعت أن هناك طفلاً جميلاً يرفض النوم مبكراً أو يثير الفوضى في المنزل. هل هذا أنت؟ أريدك أن تعلم أن الأطفال الأبطال والأذكياء ينامون مبكراً ويحافظون على نظافة غرفتهم ويسمعون كلام والديهم. أعدني أنك ستذهب للنوم الآن فوراً وتطيع والديك حتى أرسل لك وسام الذكاء!"
        ),
        PoliceCharacter(
            id = "kids_doctor",
            name = "الدكتور رامي",
            title = "طبيب الأطفال والصحة",
            emoji = "👨‍⚕️",
            avatarColor = Color(0xFF26A69A),
            description = "تشجيع الأطفال الذين يرفضون تناول الطعام الصحي أو الدواء، بأسلوب علمي تربوي لطيف.",
            speechText = "أهلاً بك يا صغيري، أنا الدكتور رامي طبيب الأطفال. لقد اتصل بي والداك وأخبراني أنك ترفض تناول الدواء أو الطعام الصحي. الدواء يجعلك قوياً ونشيطاً ويحميك من الجراثيم! أريدك أن تكون شجاعاً وتتناول طعامك ودواءك الآن، وسأعطيك نجمة الطبيب الذهبية عندما أراك!"
        ),
        PoliceCharacter(
            id = "smart_teacher",
            name = "المعلمة منيرة",
            title = "معلمة العلوم والدراسة",
            emoji = "👩‍🏫",
            avatarColor = Color(0xFFFFB74D),
            description = "تشجيع للأطفال على كتابة الواجبات المدرسية وحب القراءة والمدرسة.",
            speechText = "أهلاً يا ذكي! أنا المعلمة منيرة. أنا سعيدة جداً لأنك تحب التعلم وتحل واجباتك المدرسية وتساعد أصدقاءك! القراءة والكتابة تجعلان منك شخصاً عظيماً في المستقبل. استمر في الاجتهاد يا بطل!"
        )
    )

    val behaviorTasks = listOf(
        BehaviorTask("brush_teeth", "تفريش الأسنان", 20, "🪥", "تنظيف الأسنان مرتين يومياً لحمايتها من التسوس."),
        BehaviorTask("early_sleep", "النوم مبكراً", 20, "😴", "الخلود للنوم في الوقت المحدد دون كسل لصحة وقوة أفضل."),
        BehaviorTask("healthy_food", "تناول الفطور الصحي", 15, "🥛", "شرب الحليب وتناول الفطور الصحي لنمو ذكي."),
        BehaviorTask("school_work", "حل الواجبات اليومية", 25, "✏️", "كتابة الدروس والواجبات المدرسية بنشاط واهتمام."),
        BehaviorTask("listen_parents", "طاعة الوالدين والاستماع لهما", 30, "💖", "سماع كلام الأب والأم والتبسم في وجوههما دائماً.")
    )

    val educationalStories = listOf(
        EducationalStory(
            id = "story_honesty",
            title = "الشرطي الصغير والأمانة",
            emoji = "💎",
            moral = "الصدق والأمانة هما صفات الأبطال الحقيقيين.",
            content = "كان هناك طفل صغير اسمه سامي، وجد محفظة نقود ملقاة على الأرض في حديقة الألعاب العامة. لم يأخذ سامي النقود لنفسه ولم يخبئها، بل ذهب فوراً إلى رجل الشرطة القريب وأعطاه المحفظة. شكره رجل الشرطة كثيراً وأطلق عليه لقب 'الشرطي الصغير الأمين' وأهداه لعبة رائعة. شعر سامي بسعادة بالغة لأن الأمانة تجعل الجميع يحبوننا ويثقون بنا."
        ),
        EducationalStory(
            id = "story_health",
            title = "سر القوة الخارقة للبطل كرم",
            emoji = "🍎",
            moral = "الطعام الصحي يبني الجسم ويمنح العقل القوة والذكاء.",
            content = "في قديم الزمان، كان هناك طفل اسمه كرم يرفض تناول الخضار والفواكه ويحب تناول السكريات والحلويات فقط. في يوم من الأيام، وجد كرم نفسه تعباً جداً ولا يستطيع اللعب مع أصدقائه. زاره الشرطي الرياضي ونصحه بتناول التفاح والجزر والسبانخ ليكون قوياً ونشيطاً. بدأ كرم بتناول الطعام الصحي اللذيذ، وخلال أيام قليلة أصبح أقوى وأسرع بطل في المدرسة!"
        ),
        EducationalStory(
            id = "story_sharing",
            title = "النحلة لولو والعمل الجماعي",
            emoji = "🐝",
            moral = "التعاون والنشاط والاجتهاد هما سر النجاح والسعادة.",
            content = "كانت هناك نحلة صغيرة اسمها لولو تحب النوم والكسل طوال اليوم، بينما كانت زميلاتها يعملن بجد لجمع العسل وتحضير الغذاء. عندما جاء الشتاء البارد، لم تجد لولو أي طعام دافئ في بيتها الخشبي. ساعدتها زميلاتها النحلات وقسمن طعامهن اللذيذ معها بحب. تعلمت لولو أن الكسل يضر صاحبه، وأن التعاون والعمل الجماعي يجلبان الدفء والسعادة للجميع."
        )
    )

    // Siren Playing State
    var activeSirenType by remember { mutableStateOf<String?>(null) }

    // splash screen and active call simulation effects
    LaunchedEffect(currentScreenState) {
        if (currentScreenState == "splash") {
            val duration = 2500
            val steps = 25
            for (i in 1..steps) {
                delay((duration / steps).toLong())
                splashProgress = i.toFloat() / steps
            }
            currentScreenState = "dashboard"
            activeTab = "none"
        } else if (currentScreenState == "calling") {
            delay(3000) // Dialing sound simulation
            currentScreenState = "active_call"
            callTimer = 0
            if (!isMuted) {
                activeCharacter?.let {
                    speechManager.speak(it.speechText)
                    sirenSynthesizer.startSiren("slow") // background police sound
                }
            }
        } else if (currentScreenState == "active_call") {
            while (currentScreenState == "active_call") {
                delay(1000)
                callTimer++
                if (callTimer >= 35) { // Auto-hangup when monologue ends
                    currentScreenState = "dashboard"
                    speechManager.stop()
                    sirenSynthesizer.stopSiren()
                }
            }
        } else {
            speechManager.stop()
            sirenSynthesizer.stopSiren()
        }
    }

    if (currentScreenState == "splash") {
        KidsPoliceSplashScreen(splashProgress) {
            currentScreenState = "dashboard"
            activeTab = "none"
        }
    } else {
        Scaffold(
        topBar = {
            if (currentScreenState == "dashboard" && activeTab != "none") {
                LargeTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { activeTab = "none" }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back to Home",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = when (activeTab) {
                                    "calls" -> "📞 مكالمات شرطة الأطفال"
                                    "tasks" -> "🛡️ مهام بطل الشرطة"
                                    "stories" -> "📖 قصص الأطفال الهادفة"
                                    "sirens" -> "🚨 أصوات وصفارات الإنذار"
                                    "games" -> "🧩 ألعاب الذكاء والمطابقة"
                                    "superheroes" -> "🦸‍♂️ أبطال شرطة الإنقاذ"
                                    else -> "🚓 شرطة الأطفال"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    },
                    actions = {
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Points",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = "$userPoints نقطة",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreenState == "dashboard" && activeTab != "none") {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = activeTab == "calls",
                        onClick = { activeTab = "calls" },
                        icon = { Icon(Icons.Default.Call, contentDescription = "مكالمات") },
                        label = { Text("المكالمات", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    NavigationBarItem(
                        selected = activeTab == "tasks",
                        onClick = { activeTab = "tasks" },
                        icon = { Icon(Icons.Default.CheckCircle, contentDescription = "مهام") },
                        label = { Text("مهامي", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    NavigationBarItem(
                        selected = activeTab == "stories",
                        onClick = { activeTab = "stories" },
                        icon = { Icon(Icons.Default.Book, contentDescription = "قصص") },
                        label = { Text("قصص", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    NavigationBarItem(
                        selected = activeTab == "sirens",
                        onClick = { activeTab = "sirens" },
                        icon = { Icon(Icons.Default.Notifications, contentDescription = "أصوات") },
                        label = { Text("صفارات", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    NavigationBarItem(
                        selected = activeTab == "games",
                        onClick = { activeTab = "games" },
                        icon = { Icon(Icons.Default.Extension, contentDescription = "ألعاب") },
                        label = { Text("الألعاب", fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (currentScreenState == "dashboard") {
                if (activeTab == "none") {
                    MainDashboardHome(userPoints = userPoints) { selectedTab ->
                        activeTab = selectedTab
                    }
                } else {
                    when (activeTab) {
                        "superheroes" -> {
                            SuperheroesListScreen(
                                playTone = {
                                        sirenSynthesizer.startSiren("slow")
                                        coroutineScope.launch {
                                            delay(150)
                                            sirenSynthesizer.stopSiren()
                                        }
                                },
                                speechManager = speechManager
                            )
                        }
                        "calls" -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "🎙️ اختر شخصية للاتصال بالطفل",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "مكالمات وهمية بصوت واقعي جداً تهدف لتشجيع السلوك الطيب وتنبيه السلوك الخاطئ بأحدث الطرق التربوية العلمية.",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Center,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }

                            items(characters) { character ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            activeCharacter = character
                                            currentScreenState = "calling"
                                        },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .background(
                                                    color = character.avatarColor.copy(alpha = 0.2f),
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = character.emoji,
                                                fontSize = 28.sp
                                            )
                                        }

                                        Column(
                                            modifier = Modifier.weight(1f),
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Text(
                                                text = character.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 17.sp,
                                                color = character.avatarColor,
                                                textAlign = TextAlign.Right
                                            )
                                            Text(
                                                text = character.title,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                                textAlign = TextAlign.Right
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = character.description,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                                textAlign = TextAlign.Right,
                                                lineHeight = 16.sp
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.Call,
                                            contentDescription = "اتصال",
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "tasks" -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "🏆 جدول السلوك اليومي للبطل",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "شجع طفلك على إتمام مهامه اليومية وامنحه النقاط لتعزيز ثقته بنفسه وتطوير عاداته الإيجابية السليمة.",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Center,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }

                            items(behaviorTasks) { task ->
                                val isChecked = completedTasks.contains(task.id)
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        if (isChecked) {
                                            completedTasks = completedTasks - task.id
                                            userPoints = maxOf(0, userPoints - task.points)
                                        } else {
                                            completedTasks = completedTasks + task.id
                                            userPoints += task.points
                                        }
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = {
                                                if (isChecked) {
                                                    completedTasks = completedTasks - task.id
                                                    userPoints = maxOf(0, userPoints - task.points)
                                                } else {
                                                    completedTasks = completedTasks + task.id
                                                    userPoints += task.points
                                                }
                                            }
                                        )

                                        Column(
                                            modifier = Modifier.weight(1f),
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Text(
                                                text = "${task.emoji} ${task.title}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Right
                                            )
                                            Text(
                                                text = task.description,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                                textAlign = TextAlign.Right
                                            )
                                        }

                                        Text(
                                            text = "+${task.points} ن",
                                            color = MaterialTheme.colorScheme.tertiary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "stories" -> {
                        if (activeStory == null) {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item {
                                    Text(
                                        text = "📖 قصص تربوية هادفة للأطفال",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.padding(bottom = 8.dp),
                                        textAlign = TextAlign.Right
                                    )
                                }

                                items(educationalStories) { story ->
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { activeStory = story }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .background(
                                                        color = MaterialTheme.colorScheme.primary.copy(
                                                            alpha = 0.15f
                                                        ), shape = CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(story.emoji, fontSize = 26.sp)
                                            }

                                            Column(
                                                modifier = Modifier.weight(1f),
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Text(
                                                    text = story.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    textAlign = TextAlign.Right
                                                )
                                                Text(
                                                    text = "الحكمة: ${story.moral}",
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.tertiary,
                                                    textAlign = TextAlign.Right,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }

                                            Icon(
                                                imageVector = Icons.Default.ArrowForward,
                                                contentDescription = "عرض",
                                                tint = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            val story = activeStory!!
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = {
                                        activeStory = null
                                        speechManager.stop()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "إغلاق",
                                            tint = Color.White
                                        )
                                    }
                                    Text(
                                        text = story.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(story.emoji, fontSize = 28.sp)
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(20.dp)
                                            .fillMaxSize()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = MaterialTheme.colorScheme.tertiary.copy(
                                                        alpha = 0.15f
                                                    ),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                                .align(Alignment.CenterHorizontally)
                                        ) {
                                            Text(
                                                text = "حكمة القصة: ${story.moral}",
                                                color = MaterialTheme.colorScheme.tertiary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        Text(
                                            text = story.content,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Justify,
                                            lineHeight = 26.sp,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Button(
                                            onClick = { speechManager.speak(story.content) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = "قراءة القصة"
                                                )
                                                Text(
                                                    text = "استمع للقصة الآن بصوت الشرطي",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "sirens" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "🚨 لوحة أصوات صفارات الإنذار",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "صفارات إنذار حقيقية مصنّعة رقمياً وتعمل بشكل فوري لمحاكاة أجواء دوريات الشرطة والإنقاذ التفاعلية.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            val sirens = listOf(
                                Triple("fast", "إنذار سريع ومتقطع (Yelp)", "🔊"),
                                Triple("slow", "صفارة الدورية الطويلة (Wail)", "🚓"),
                                Triple("warning", "إنذار الخطر النابض (Pulse)", "⚠️"),
                                Triple("yelp", "تنبيه سريع جداً وصارخ (Chirp)", "⚡")
                            )

                            sirens.forEach { (type, name, emoji) ->
                                val isSirenPlaying = activeSirenType == type
                                Button(
                                    onClick = {
                                        if (isSirenPlaying) {
                                            sirenSynthesizer.stopSiren()
                                            activeSirenType = null
                                        } else {
                                            sirenSynthesizer.startSiren(type)
                                            activeSirenType = type
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSirenPlaying) Color(0xFFEF5350) else MaterialTheme.colorScheme.surface,
                                        contentColor = if (isSirenPlaying) Color.White else MaterialTheme.colorScheme.onSurface
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isSirenPlaying) "⏹️ إيقاف الصوت" else "▶️ تشغيل الصوت",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            textAlign = TextAlign.Right
                                        )
                                        Text(text = emoji, fontSize = 24.sp)
                                    }
                                }
                            }

                            if (activeSirenType != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "جاري تجميع وإخراج ترددات الصوت في الخلفية...",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    "games" -> {
                        SimplePuzzleGameScreen(
                            onPointsEarned = { earned ->
                                userPoints += earned
                            },
                            playTone = { freq ->
                                sirenSynthesizer.startSiren("slow")
                                coroutineScope.launch {
                                    delay(100)
                                    sirenSynthesizer.stopSiren()
                                }
                            }
                        )
                    }
                }
            }
        }

            // --- CALLING SCREEN OVERLAY ---
            if (currentScreenState == "calling" && activeCharacter != null) {
                val character = activeCharacter!!
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.85f,
                    targetValue = 1.15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "scale"
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF030712),
                                    Color(0xFF0D1B2A),
                                    Color(0xFF1B263B)
                                )
                            )
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 40.dp)
                    ) {
                        Text(
                            text = "جاري الاتصال بـ...",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = character.name,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = character.title,
                            color = character.avatarColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Pulsing Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .scale(scale)
                            .background(
                                color = character.avatarColor.copy(alpha = 0.15f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .background(
                                    color = character.avatarColor.copy(alpha = 0.3f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = character.emoji,
                                fontSize = 64.sp
                            )
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 40.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Decline Button (Red)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                currentScreenState = "dashboard"
                                speechManager.stop()
                                sirenSynthesizer.stopSiren()
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color(0xFFEF5350), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CallEnd,
                                    contentDescription = "رفض",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "إلغاء", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }

            // --- ACTIVE CALL SCREEN OVERLAY ---
            if (currentScreenState == "active_call" && activeCharacter != null) {
                val character = activeCharacter!!
                val minutes = callTimer / 60
                val seconds = callTimer % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF030712),
                                    Color(0xFF0D1B2A),
                                    Color(0xFF1B263B)
                                )
                            )
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 40.dp)
                    ) {
                        Text(
                            text = "مكالمة نشطة",
                            color = Color(0xFF4CAF50),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = character.name,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = timeString,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Active talking visualizer (Subtitles of dialog)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.08f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "💬 الشرطي يتحدث الآن:",
                                color = character.avatarColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Right
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = character.speechText,
                                color = Color.White,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                textAlign = TextAlign.Right
                            )
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 40.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mute/Speaker Toggle
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                isMuted = !isMuted
                                if (isMuted) {
                                    speechManager.stop()
                                    sirenSynthesizer.stopSiren()
                                } else {
                                    speechManager.speak(character.speechText)
                                    sirenSynthesizer.startSiren("slow")
                                }
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        if (isMuted) Color.White.copy(alpha = 0.2f) else Color.White.copy(
                                            alpha = 0.1f
                                        ), shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                    contentDescription = "كتم",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = if (isMuted) "تشغيل الصوت" else "كتم الصوت", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        }

                        // Hangup (Red Button)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                currentScreenState = "dashboard"
                                speechManager.stop()
                                sirenSynthesizer.stopSiren()
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(Color(0xFFEF5350), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CallEnd,
                                    contentDescription = "إنهاء",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "إنهاء المكالمة", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun KidsPoliceSplashScreen(
    progress: Float,
    onFinished: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Police Badge or Star emoji with animated scale
            Text(
                text = "🚓",
                fontSize = 80.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "شرطة الأطفال",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "تطبيق تربوي تفاعلي هادف",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF2563EB),
                                    Color(0xFF60A5FA)
                                )
                            )
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "جاري التحميل... ${(progress * 100).toInt()}%",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
    
    if (progress >= 1f) {
        LaunchedEffect(Unit) {
            onFinished()
        }
    }
}

@Composable
fun MainDashboardHome(
    userPoints: Int,
    onTabSelected: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏆 لوحة تحكم بطل الشرطة 🏆",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "أهلاً بك يا بطل في شاشتك التفاعلية! اجمع النقاط عبر إتمام المهام اليومية واستمع للقصص الهادفة والمكالمات التربوية الرائعة.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    // Points chip
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "رصيدك الحالي: $userPoints نقطة ⭐",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "اختر وجهتك المفضلة:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            )
        }

        // 6 Grid Cards list
        item {
            val gridItems = listOf(
                Triple("calls", "📞 المكالمات", "تواصل مع شخصيات الشرطة والأطباء لتعديل السلوك."),
                Triple("tasks", "🛡️ مهام البطل", "جدول السلوك اليومي لبناء العادات الطيبة وإحراز النقاط."),
                Triple("stories", "📖 قصص هادفة", "استمع لقصص تربوية رائعة تعلم الأطفال الأخلاق الحميدة."),
                Triple("sirens", "🚨 صفارات الشرطة", "شغّل أصوات صفارات وسيارات الشرطة الحقيقية والمميزة."),
                Triple("games", "🧩 ألعاب الذكاء", "العب ألعاب مطابقة الذاكرة لتنمية عقلك وذكائك."),
                Triple("superheroes", "🦸‍♂️ الأبطال", "تعرف على أبطال شرطة الإنقاذ الأقوياء وسلوكياتهم.")
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (i in gridItems.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // First item
                        val item1 = gridItems[i]
                        DashboardGridCard(
                            title = item1.second,
                            desc = item1.third,
                            modifier = Modifier.weight(1f),
                            onClick = { onTabSelected(item1.first) }
                        )

                        // Second item if exists
                        if (i + 1 < gridItems.size) {
                            val item2 = gridItems[i + 1]
                            DashboardGridCard(
                                title = item2.second,
                                desc = item2.third,
                                modifier = Modifier.weight(1f),
                                onClick = { onTabSelected(item2.first) }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardGridCard(
    title: String,
    desc: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )
        }
    }
}

data class Superhero(
    val id: String,
    val name: String,
    val power: String,
    val emoji: String,
    val color: Color,
    val motto: String,
    val audioText: String
)

@Composable
fun SuperheroesListScreen(
    playTone: (Float) -> Unit,
    speechManager: SpeechManager?
) {
    val superheroes = listOf(
        Superhero(
            "captain_shujaa",
            "الكابتن شجاع",
            "قوة الأمان والصدق والشجاعة الفائقة",
            "🦸‍♂️",
            Color(0xFF29B6F6),
            "الصدق والأمانة هما درعي الأقوى دائماً!",
            "أهلاً بك يا بطل! أنا الكابتن شجاع قائد فرقة أبطال الأمان. تذكر دائماً أن البطل الحقيقي هو من يقول الصدق ويساعد المحتاجين ويحمي الضعفاء. كن شجاعاً وصادقاً دائماً!"
        ),
        Superhero(
            "heroine_amal",
            "البطلة أمل",
            "قوة الذكاء الخارق والتنظيم السريع",
            "🦸‍♀️",
            Color(0xFFEC407A),
            "بالذكاء والترتيب، نصنع المستحيل ونرتب العالم!",
            "مرحباً يا بطل المستقبل! أنا البطلة أمل. قوتي الخارقة تأتي من تنظيم غرفتي وحل واجباتي اليومية مبكراً ومساعدة الآخرين بذكائي. أريدك أن تكون ذكياً ومنظماً مثلي لتنضم لفرقتنا!"
        ),
        Superhero(
            "hero_karam",
            "البطل كرم",
            "قوة النشاط البدني والغذاء الصحي والطاقة المتجددة",
            "⚡",
            Color(0xFFFFB74D),
            "الغذاء الصحي والنوم المبكر هما سر قوتي ونشاطي الخارق!",
            "أهلاً بصديقي النشيط! أنا البطل كرم. هل تعرف سر سرعتي وقوتي البدنية؟ إنها الفواكه الطازجة، الخضار الشهية، والنوم المبكر! تناول طعامك الصحي كاملاً لتملك طاقة الأبطال الخارقين مثلي!"
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🦸‍♂️ فرقة أبطال شرطة الإنقاذ 🦸‍♀️",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تعرف على الأبطال الخارقين الذين يحمون السلوك الطيب، واضغط على أي بطل لتستمع لنصيحته الخاصة وصوته الخارق لتصبح بطلاً مثله!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        items(superheroes) { hero ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        playTone(700f)
                        speechManager?.speak(hero.audioText)
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Hero Emoji/Badge
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = hero.color.copy(alpha = 0.18f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = hero.emoji,
                            fontSize = 28.sp
                        )
                    }

                    // Hero Info
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = hero.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = hero.color,
                            textAlign = TextAlign.Right
                        )
                        Text(
                            text = "القدرة: ${hero.power}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Right
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "الشعار: \"${hero.motto}\"",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Right,
                            lineHeight = 14.sp
                        )
                    }

                    // Play audio icon indicator
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "استماع",
                        tint = hero.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

