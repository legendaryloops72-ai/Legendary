package com.aistudio.kidspolice.abcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.android.libraries.ads.mobile.sdk.common.RequestConfiguration
import com.google.android.libraries.ads.mobile.sdk.common.AgeRestrictedTreatment
import com.aistudio.kidspolice.abcd.audio.PoliceAudioPlayer
import com.aistudio.kidspolice.abcd.data.DailyMission
import com.aistudio.kidspolice.abcd.data.Dialect
import com.aistudio.kidspolice.abcd.data.MissionRepository
import com.aistudio.kidspolice.abcd.data.PoliceScenario
import com.aistudio.kidspolice.abcd.data.PoliceScenariosRepository
import com.aistudio.kidspolice.abcd.ui.screens.CallScreen
import com.aistudio.kidspolice.abcd.ui.screens.CertificateScreen
import com.aistudio.kidspolice.abcd.ui.screens.DialerScreen
import com.aistudio.kidspolice.abcd.ui.screens.HomeScreen
import com.aistudio.kidspolice.abcd.ui.screens.MissionsScreen
import com.aistudio.kidspolice.abcd.ui.screens.SoundsScreen
import com.aistudio.kidspolice.abcd.ui.theme.KidsPoliceTheme
import com.aistudio.kidspolice.abcd.ui.theme.PoliceNavy

class MainActivity : ComponentActivity() {
    private lateinit var appOpenAdManager: AppOpenAdManager
    private lateinit var interstitialAdManager: InterstitialAdManager
    private var isFirstResume = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appOpenAdManager = AppOpenAdManager(applicationContext)
        interstitialAdManager = InterstitialAdManager(applicationContext)

        // Initialize Mobile Ads SDK on a background thread to avoid ANR
        Thread {
            val initConfig =
                InitializationConfig.Builder(
                    "ca-app-pub-4760027279848820~4114638850"
                )
                .setRequestConfiguration(
                    RequestConfiguration.Builder()
                        .setAgeRestrictedTreatment(AgeRestrictedTreatment.CHILD)
                        .build()
                )
                .build()

            MobileAds.initialize(
                applicationContext,
                initConfig
            ) {
                appOpenAdManager.loadAd()
                interstitialAdManager.loadAd()
            }
        }.start()

        setContent {
            KidsPoliceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PoliceNavy
                ) {
                    KidsPoliceApp(onTestInterstitial = { interstitialAdManager.showAd(this) })
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
        } else {
            appOpenAdManager.showAdIfAvailable(this)
        }
    }
}

@Composable
fun KidsPoliceApp(onTestInterstitial: () -> Unit) {
    val context = LocalContext.current
    val audioPlayer = remember { PoliceAudioPlayer(context) }
    val navController = rememberNavController()

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
                userScore = userScore,
                onTestInterstitial = onTestInterstitial
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
