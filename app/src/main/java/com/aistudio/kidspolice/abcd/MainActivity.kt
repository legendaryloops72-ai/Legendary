package com.aistudio.kidspolice.abcd

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aistudio.kidspolice.abcd.ads.AppOpenAdManager
import com.aistudio.kidspolice.abcd.audio.PoliceAudioPlayer
import com.aistudio.kidspolice.abcd.data.Dialect
import com.aistudio.kidspolice.abcd.data.DailyMission
import com.aistudio.kidspolice.abcd.data.MissionRepository
import com.aistudio.kidspolice.abcd.data.PoliceScenario
import com.aistudio.kidspolice.abcd.data.PoliceScenariosRepository
import com.aistudio.kidspolice.abcd.ui.screens.CallScreen
import com.aistudio.kidspolice.abcd.ui.screens.CartoonVoicesScreen
import com.aistudio.kidspolice.abcd.ui.screens.CertificateScreen
import com.aistudio.kidspolice.abcd.ui.screens.DialerScreen
import com.aistudio.kidspolice.abcd.ui.screens.FunnySoundsScreen
import com.aistudio.kidspolice.abcd.ui.screens.HomeScreen
import com.aistudio.kidspolice.abcd.ui.screens.MissionsScreen
import com.aistudio.kidspolice.abcd.ui.screens.SoundsScreen
import com.aistudio.kidspolice.abcd.ui.screens.ToySimulatorScreen
import com.aistudio.kidspolice.abcd.ui.theme.KidsPoliceTheme
import com.aistudio.kidspolice.abcd.ui.theme.PoliceNavy
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.common.AgeRestrictedTreatment
import com.google.android.libraries.ads.mobile.sdk.common.RequestConfiguration
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "KidsPolice"
private const val ROUTE_HOME = "home"
private const val ROUTE_CALL = "call"

/** Logs only in debug builds. */
private fun debugLog(message: String) {
    if (BuildConfig.DEBUG) Log.d(TAG, message)
}

/* ------------------------------------------------------------------ */
/*  ViewModel: يحفظ النقاط والمهام واللهجة (تبقى بعد تدوير الشاشة       */
/*  وإغلاق التطبيق)                                                      */
/* ------------------------------------------------------------------ */
class KidsPoliceViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("kidspolice_state", Context.MODE_PRIVATE)

    var selectedDialect by mutableStateOf(loadDialect())
        private set

    // لا يُحفظ عند إغلاق التطبيق، ويرجع للسيناريو الأول
    var currentScenario by mutableStateOf<PoliceScenario?>(null)
        private set

    var missions by mutableStateOf(loadMissions())
        private set

    // النقاط تُحسب من المهام المكتملة، فلا يحدث تعارض بينهما أبداً
    val userScore: Int
        get() = BASE_SCORE + missions.filter { it.isCompleted }.sumOf { it.points }

    fun selectDialect(dialect: Dialect) {
        selectedDialect = dialect
        prefs.edit().putString(KEY_DIALECT, dialect.name).apply()
    }

    fun setScenario(scenario: PoliceScenario?) {
        currentScenario = scenario
    }

    fun toggleMission(missionId: Any) {
        val updated = missions.map { m ->
            if (m.id.toString() == missionId.toString()) m.copy(isCompleted = !m.isCompleted) else m
        }
        missions = updated
        prefs.edit()
            .putStringSet(
                KEY_COMPLETED,
                updated.filter { it.isCompleted }.map { it.id.toString() }.toSet()
            )
            .apply()
    }

    private fun loadDialect(): Dialect {
        val saved = prefs.getString(KEY_DIALECT, null)
        return Dialect.values().firstOrNull { it.name == saved } ?: Dialect.SAUDI
    }

    private fun loadMissions(): List<DailyMission> {
        val completed = prefs.getStringSet(KEY_COMPLETED, emptySet()) ?: emptySet()
        return MissionRepository.getDefaultMissions().map { m ->
            m.copy(isCompleted = m.id.toString() in completed)
        }
    }

    private companion object {
        const val BASE_SCORE = 100
        const val KEY_DIALECT = "dialect"
        const val KEY_COMPLETED = "completed_missions"
    }
}

/* ------------------------------------------------------------------ */
/*  Activity                                                            */
/* ------------------------------------------------------------------ */
class MainActivity : ComponentActivity() {

    private val viewModel: KidsPoliceViewModel by viewModels()

    private lateinit var appOpenAdManager: AppOpenAdManager
    private var activeAudioPlayer: PoliceAudioPlayer? = null
    private var currentDestination: String = ROUTE_HOME

    private val adPrefs by lazy { getSharedPreferences("ads_state", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        debugLog("MainActivity onCreate")

        appOpenAdManager = AppOpenAdManager(applicationContext).apply {
            isAudioActiveProvider = {
                val player = activeAudioPlayer
                (player?.isSpeaking?.value == true) || (player?.isSirenPlaying?.value == true)
            }
            // الإعلان فقط في الشاشة الرئيسية
            isSafeScreenToDisplayProvider = { currentDestination == ROUTE_HOME }
            currentActivityProvider = { this@MainActivity }
        }

        setContent {
            KidsPoliceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PoliceNavy
                ) {
                    KidsPoliceApp(
                        viewModel = viewModel,
                        onAudioPlayerCreated = { activeAudioPlayer = it },
                        onAudioPlayerReleased = { activeAudioPlayer = null },
                        onDestinationChanged = { currentDestination = it }
                    )
                }
            }
        }

        // تهيئة الإعلانات في خيط خلفي
        lifecycleScope.launch(Dispatchers.IO) {
            val initConfig = InitializationConfig.Builder("ca-app-pub-4760027279848820~4114638850")
                .setRequestConfiguration(
                    RequestConfiguration.Builder()
                        .setAgeRestrictedTreatment(AgeRestrictedTreatment.CHILD)
                        .setMaxAdContentRating(
                            RequestConfiguration.MaxAdContentRating.MAX_AD_CONTENT_RATING_G
                        )
                        .build()
                )
                .build()

            MobileAds.initialize(applicationContext, initConfig) {
                appOpenAdManager.loadAd()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        debugLog("MainActivity onResume")
        if (::appOpenAdManager.isInitialized && shouldTryAppOpenAd()) {
            appOpenAdManager.showAdIfAvailable(this)
        }
    }

    override fun onPause() {
        super.onPause()
        debugLog("MainActivity onPause")
        if (::appOpenAdManager.isInitialized) {
            appOpenAdManager.onActivityPaused()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        debugLog("MainActivity onDestroy")
    }

    /**
     * بوابة تردد لإعلان App Open:
     *  - أول تشغيل للتطبيق: بدون إعلان.
     *  - بعدها: مرة كل 4 ساعات كحد أقصى.
     * ملاحظة: الوقت يُسجَّل عند المحاولة، حتى لو لم يكن الإعلان جاهزاً.
     * الأدق نقل هذا المنطق داخل AppOpenAdManager وتسجيل الوقت عند ظهور الإعلان فعلاً.
     */
    private fun shouldTryAppOpenAd(): Boolean {
        if (currentDestination != ROUTE_HOME) return false

        val now = System.currentTimeMillis()
        val last = adPrefs.getLong(KEY_LAST_AD_ATTEMPT, 0L)

        if (last == 0L) {
            adPrefs.edit().putLong(KEY_LAST_AD_ATTEMPT, now).apply()
            return false
        }
        if (now - last < AD_MIN_INTERVAL_MS) return false

        adPrefs.edit().putLong(KEY_LAST_AD_ATTEMPT, now).apply()
        return true
    }

    private companion object {
        const val KEY_LAST_AD_ATTEMPT = "last_app_open_attempt"
        const val AD_MIN_INTERVAL_MS = 4L * 60 * 60 * 1000 // 4 ساعات
    }
}

/* ------------------------------------------------------------------ */
/*  Compose                                                             */
/* ------------------------------------------------------------------ */

/** يمنع فتح نفس الشاشة مرتين عند الضغط المزدوج. */
private fun NavController.go(route: String) {
    navigate(route) { launchSingleTop = true }
}

@Composable
fun KidsPoliceApp(
    viewModel: KidsPoliceViewModel,
    onAudioPlayerCreated: (PoliceAudioPlayer) -> Unit = {},
    onAudioPlayerReleased: () -> Unit = {},
    onDestinationChanged: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val audioPlayer = remember {
        PoliceAudioPlayer(context).also { onAudioPlayerCreated(it) }
    }
    val navController = rememberNavController()

    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            destination.route?.let { onDestinationChanged(it) }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }

    DisposableEffect(audioPlayer) {
        onDispose {
            onAudioPlayerReleased() // نصفّر المرجع قبل التحرير
            audioPlayer.release()
        }
    }

    NavHost(navController = navController, startDestination = ROUTE_HOME) {

        composable(ROUTE_HOME) {
            HomeScreen(
                selectedDialect = viewModel.selectedDialect,
                onDialectSelected = viewModel::selectDialect,
                onStartCall = { scenario ->
                    viewModel.setScenario(scenario)
                    navController.go(ROUTE_CALL)
                },
                onOpenDialer = { navController.go("dialer") },
                onOpenSounds = { navController.go("sounds") },
                onOpenMissions = { navController.go("missions") },
                onOpenCertificate = { navController.go("certificate") },
                onOpenToys = { navController.go("toys") },
                onOpenFunnySounds = { navController.go("funny_sounds") },
                onOpenCartoonVoices = { navController.go("cartoon_voices") },
                userScore = viewModel.userScore,
                audioPlayer = audioPlayer
            )
        }

        composable("funny_sounds") {
            FunnySoundsScreen(onBack = { navController.popBackStack() })
        }

        composable("cartoon_voices") {
            CartoonVoicesScreen(onBack = { navController.popBackStack() })
        }

        composable("toys") {
            ToySimulatorScreen(onBack = { navController.popBackStack() })
        }

        composable(ROUTE_CALL) {
            val scenario = viewModel.currentScenario
                ?: PoliceScenariosRepository.scenarios.first()
            CallScreen(
                scenario = scenario,
                dialect = viewModel.selectedDialect,
                audioPlayer = audioPlayer,
                onEndCall = {
                    audioPlayer.stopSpeaking()
                    navController.popBackStack()
                }
            )
        }

        composable("dialer") {
            DialerScreen(
                selectedDialect = viewModel.selectedDialect,
                onStartCustomCall = { customScenario ->
                    viewModel.setScenario(customScenario)
                    navController.go(ROUTE_CALL)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("sounds") {
            SoundsScreen(
                audioPlayer = audioPlayer,
                onBack = { navController.popBackStack() }
            )
        }

        composable("missions") {
            MissionsScreen(
                missions = viewModel.missions,
                userScore = viewModel.userScore,
                onToggleMission = viewModel::toggleMission,
                onOpenCertificate = { navController.go("certificate") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("certificate") {
            CertificateScreen(
                userScore = viewModel.userScore,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
 android.util.Log.d("KidsPoliceDebug", "KIDSPOLICE_MAIN_ACTIVITY_ONDESTROY=true")
        super.onDestroy()
    }
}

@Composable
fun KidsPoliceApp(
    onAudioPlayerCreated: (PoliceAudioPlayer) -> Unit = {},
    onDestinationChanged: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val audioPlayer = remember {
        PoliceAudioPlayer(context).also { onAudioPlayerCreated(it) }
    }
    val navController = rememberNavController()

    DisposableEffect(navController) {
        val listener = androidx.navigation.NavController.OnDestinationChangedListener { _, destination, _ ->
            destination.route?.let { onDestinationChanged(it) }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    var selectedDialect by remember { mutableStateOf(Dialect.SAUDI) }
    var currentScenario by remember { mutableStateOf<PoliceScenario?>(null) }
    var missions by remember { mutableStateOf(MissionRepository.getDefaultMissions()) }
    var userScore by remember { mutableIntStateOf(100) }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.release()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                selectedDialect = selectedDialect,
                onDialectSelected = { selectedDialect = it },
                onStartCall = { scenario ->
                    currentScenario = scenario
                    navController.navigate("call")
                },
                onOpenDialer = { navController.navigate("dialer") },
                onOpenSounds = { navController.navigate("sounds") },
                onOpenMissions = { navController.navigate("missions") },
                onOpenCertificate = { navController.navigate("certificate") },
                onOpenToys = { navController.navigate("toys") },
                onOpenFunnySounds = { navController.navigate("funny_sounds") },
                onOpenCartoonVoices = { navController.navigate("cartoon_voices") },
                userScore = userScore,
                audioPlayer = audioPlayer
            )
        }

        composable("funny_sounds") {
            com.aistudio.kidspolice.abcd.ui.screens.FunnySoundsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("cartoon_voices") {
            com.aistudio.kidspolice.abcd.ui.screens.CartoonVoicesScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("toys") {
            com.aistudio.kidspolice.abcd.ui.screens.ToySimulatorScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("call") {
            val scenario = currentScenario ?: PoliceScenariosRepository.scenarios.first()
            CallScreen(
                scenario = scenario,
                dialect = selectedDialect,
                audioPlayer = audioPlayer,
                onEndCall = {
                    audioPlayer.stopSpeaking()
                    navController.popBackStack()
                }
            )
        }

        composable("dialer") {
            DialerScreen(
                selectedDialect = selectedDialect,
                onStartCustomCall = { customScenario ->
                    currentScenario = customScenario
                    navController.navigate("call")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("sounds") {
            SoundsScreen(
                audioPlayer = audioPlayer,
                onBack = { navController.popBackStack() }
            )
        }

        composable("missions") {
            MissionsScreen(
                missions = missions,
                userScore = userScore,
                onToggleMission = { missionId ->
                    missions = missions.map { mission ->
                        if (mission.id == missionId) {
                            val newStatus = !mission.isCompleted
                            if (newStatus) {
                                userScore += mission.points
                            } else {
                                userScore -= mission.points
                            }
                            mission.copy(isCompleted = newStatus)
                        } else {
                            mission
                        }
                    }
                },
                onOpenCertificate = { navController.navigate("certificate") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("certificate") {
            CertificateScreen(
                userScore = userScore,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
