package com.aistudio.kidspolice.abcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.google.android.gms.ads.MobileAds
import com.aistudio.kidspolice.abcd.data.AppDatabase
import com.aistudio.kidspolice.abcd.data.AppRepository
import com.aistudio.kidspolice.abcd.ui.*
import com.aistudio.kidspolice.abcd.ui.games.*
import com.aistudio.kidspolice.abcd.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Google Mobile Ads SDK
        MobileAds.initialize(this) {}

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "app_database"
        ).fallbackToDestructiveMigration().build()
        
        val repository = AppRepository(db.appDao())
        val factory = AppViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val appViewModel: AppViewModel = viewModel(factory = factory)
                    
                    var currentScreen by rememberSaveable { mutableStateOf("splash") }
                    var selectedVehicleId by remember { mutableStateOf(0) }
                    var selectedCallType by rememberSaveable { mutableStateOf("police") }

                    when (currentScreen) {
                        "splash" -> {
                            SplashScreen(
                                onSplashFinished = {
                                    currentScreen = "home"
                                }
                            )
                        }
                        "setup" -> {
                            SetupScreen(
                                viewModel = appViewModel,
                                onComplete = {
                                    currentScreen = "home"
                                }
                            )
                        }
                        "home" -> {
                            HomeScreen(
                                viewModel = appViewModel,
                                
                                onNavigateToTasks = { currentScreen = "tasks" },
                                onNavigateToQuizzes = { currentScreen = "quiz" },
                                onNavigateToSettings = { currentScreen = "settings" },
                                onSimulateCall = { callId ->
                                    selectedCallType = callId
                                    currentScreen = "fake_call"
                                },
                                onNavigateToHeroesUniverse = { currentScreen = "heroes" },
                                onNavigateToPoliceCars = { currentScreen = "police_cars" },
                                onNavigateToPrivacy = { currentScreen = "privacy" },
                                onNavigateToColoring = { currentScreen = "coloring" },
                                onNavigateToGames = { currentScreen = "games" },
                                onNavigateToStories = { currentScreen = "stories" },
                                onNavigateToPoliceScenarios = { currentScreen = "police_scenarios" },
                                onNavigateToCallHub = { currentScreen = "call_hub" },
                                onNavigateToRewards = { currentScreen = "rewards" }
                            )
                        }
                        "police_scenarios" -> {
                            PoliceScenariosScreen(
                                viewModel = appViewModel,
                                onNavigateToCall = { callId ->
                                    selectedCallType = callId
                                    currentScreen = "fake_call"
                                },
                                onBack = { currentScreen = "home" }
                            )
                        }
                        "call_hub" -> {
                            CallHubScreen(
                                onNavigateToPoliceScenarios = { currentScreen = "police_scenarios" },
                                onNavigateToCall = { callId ->
                                    selectedCallType = callId
                                    currentScreen = "fake_call"
                                },
                                onBack = { currentScreen = "home" }
                            )
                        }
                        "fake_call" -> {
                            FakeCallScreen(
                                callerType = selectedCallType,
                                viewModel = appViewModel,
                                onEndCall = { currentScreen = "home" }
                            )
                        }
                        "tasks" -> {
                            TasksScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "home" }
                            )
                        }
                        "quiz" -> {
                            QuizScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "home" }
                            )
                        }
                        "settings" -> {
                            SettingsScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "home" },
                                onShowPrivacyPolicy = { currentScreen = "privacy" },
                                onNavigateToParentDashboard = { currentScreen = "parent_dashboard" }
                            )
                        }
                        "parent_dashboard" -> {
                            ParentDashboardScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "settings" }
                            )
                        }
                        "heroes" -> {
                            HeroesUniverseScreen(
                                viewModel = appViewModel,
onBack = { currentScreen = "home" }
                            )
                        }
                        "police_cars" -> {
                            PoliceCarsGalleryScreen(
                                onNavigateBack = { currentScreen = "home" },
                                onVehicleSelected = { id ->
                                    selectedVehicleId = id
                                    currentScreen = "police_car_detail"
                                }
                            )
                        }
                        "police_car_detail" -> {
                            PoliceCarDetailScreen(
                                vehicleId = selectedVehicleId,
                                onNavigateBack = { currentScreen = "police_cars" }
                            )
                        }
                        "privacy" -> {
                            PrivacyPolicyScreen(
                                onAccept = { currentScreen = "home" }
                            )
                        }
                        "coloring" -> {
                            ColoringScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "home" }
                            )
                        }
                        "games" -> {
                            GamesScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "home" },
                                onNavigateToMemoryGame = { currentScreen = "game_memory" },
                                onNavigateToColorTapGame = { currentScreen = "game_colortap" },
                                onNavigateToNumberOrderGame = { currentScreen = "game_numberorder" },
                                onNavigateToShapeMatchGame = { currentScreen = "game_shapematch" },
                                onNavigateToAlphabetGame = { currentScreen = "game_alphabet" },
                                onNavigateToBubblePopGame = { currentScreen = "game_bubblepop" },
                                onNavigateToFindDifferencesGame = { currentScreen = "game_finddiff" },
                                onNavigateToPuzzleGame = { currentScreen = "game_puzzle" }
                            )
                        }
                        "game_memory" -> {
                            MemoryMatchGameScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "games" }
                            )
                        }
                        "game_colortap" -> {
                            ColorTapGameScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "games" }
                            )
                        }
                        "game_numberorder" -> {
                            NumberOrderGameScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "games" }
                            )
                        }
                        "game_shapematch" -> {
                            ShapeMatchGameScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "games" }
                            )
                        }
                        "game_alphabet" -> {
                            AlphabetGameScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "games" }
                            )
                        }
                        "game_bubblepop" -> {
                            BubblePopGameScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "games" }
                            )
                        }
                        "game_finddiff" -> {
                            FindDifferencesGameScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "games" }
                            )
                        }
                        "game_puzzle" -> {
                            SimplePuzzleGameScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "games" }
                            )
                        }
                        "stories" -> {
                            StoriesScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "home" }
                            )
                        }
                        "rewards" -> {
                            RewardsScreen(
                                viewModel = appViewModel,
                                onBack = { currentScreen = "home" }
                            )
                        }
                        else -> {
                            HomeScreen(
                                viewModel = appViewModel,
                                
                                onNavigateToTasks = { currentScreen = "tasks" },
                                onNavigateToQuizzes = { currentScreen = "quiz" },
                                onNavigateToSettings = { currentScreen = "settings" },
                                onSimulateCall = { callId ->
                                    selectedCallType = callId
                                    currentScreen = "fake_call"
                                },
                                onNavigateToHeroesUniverse = { currentScreen = "heroes" },
                                onNavigateToPoliceCars = { currentScreen = "police_cars" },
                                onNavigateToPrivacy = { currentScreen = "privacy" },
                                onNavigateToColoring = { currentScreen = "coloring" },
                                onNavigateToGames = { currentScreen = "games" },
                                onNavigateToStories = { currentScreen = "stories" },
                                onNavigateToPoliceScenarios = { currentScreen = "police_scenarios" },
                                onNavigateToCallHub = { currentScreen = "call_hub" },
                                onNavigateToRewards = { currentScreen = "rewards" }
                            )
                        }
                    }
                }
            }
        }
    }
}
