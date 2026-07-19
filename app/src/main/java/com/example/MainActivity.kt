package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.android.gms.ads.MobileAds
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.ui.*
import com.example.ui.games.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize AdMob
        try {
            MobileAds.initialize(this) {}
            // Preload interstitial ad on startup
            com.example.ui.AdManager.loadInterstitial(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        val database = try {
            Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                "kids_police_db_v2"
            )
            .fallbackToDestructiveMigration(true)
            .build()
        } catch (e: Throwable) {
            Room.inMemoryDatabaseBuilder(
                applicationContext,
                AppDatabase::class.java
            ).build()
        }

        val repository = AppRepository(database.appDao())
        val factory = AppViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: AppViewModel = viewModel(factory = factory)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(onSplashFinished = {
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
                                    launchSingleTop = true
                                }
                            })
                        }
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToTasks = { navController.navigate("tasks") },
                                onNavigateToQuizzes = { navController.navigate("quizzes") },
                                onNavigateToSettings = { navController.navigate("settings") },
                                onSimulateCall = { caller ->
                                    navController.navigate("call/$caller")
                                },
                                onNavigateToHeroesUniverse = { navController.navigate("heroes-universe") },
                                onNavigateToSoundsUniverse = { navController.navigate("sounds-universe") },
                                onNavigateToPrivacy = { navController.navigate("privacy") },
                                onNavigateToColoring = { navController.navigate("coloring") },
                                onNavigateToGames = { navController.navigate("games") },
                                onNavigateToStories = { navController.navigate("stories") },
                                onNavigateToPoliceScenarios = { navController.navigate("police-scenarios") },
                                onNavigateToCallHub = { navController.navigate("call-hub") },
                                onNavigateToRewards = { navController.navigate("rewards") }
                            )
                        }
                        composable("call-hub") {
                            CallHubScreen(
                                onNavigateToPoliceScenarios = { navController.navigate("police-scenarios") },
                                onNavigateToCall = { caller ->
                                    navController.navigate("call/$caller")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("police-scenarios") {
                            PoliceScenariosScreen(
                                viewModel = viewModel,
                                onNavigateToCall = { scenarioType ->
                                    navController.navigate("call/$scenarioType")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("coloring") {
                            ColoringScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("stories") {
                            StoriesScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("games") {
                            GamesScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onNavigateToMemoryGame = { navController.navigate("game-memory") },
                                onNavigateToColorTapGame = { navController.navigate("game-color-tap") },
                                onNavigateToNumberOrderGame = { navController.navigate("game-number-order") },
                                onNavigateToShapeMatchGame = { navController.navigate("game-shape-match") },
                                onNavigateToAlphabetGame = { navController.navigate("game-alphabet") },
                                onNavigateToBubblePopGame = { navController.navigate("game-bubble-pop") },
                                onNavigateToFindDifferencesGame = { navController.navigate("game-find-differences") },
                                onNavigateToPuzzleGame = { navController.navigate("game-puzzle") }
                            )
                        }
                        composable("game-memory") {
                            MemoryMatchGameScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("game-color-tap") {
                            ColorTapGameScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("game-number-order") {
                            NumberOrderGameScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("game-shape-match") {
                            ShapeMatchGameScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("game-alphabet") {
                            AlphabetGameScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("game-bubble-pop") {
                            BubblePopGameScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("game-find-differences") {
                            FindDifferencesGameScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("game-puzzle") {
                            SimplePuzzleGameScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("heroes-universe") {
                            SuperHeroesScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("sounds-universe") {
                            SoundsUniverseScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("quizzes") {
                            QuizScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("tasks") {
                            TasksScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("settings") {
                            SettingsScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onShowPrivacyPolicy = { navController.navigate("privacy") },
                                onNavigateToParentDashboard = { navController.navigate("parent-dashboard") }
                            )
                        }
                        composable("parent-dashboard") {
                            ParentDashboardScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("rewards") {
                            RewardsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                        }
                        composable("privacy") {
                            PrivacyPolicyScreen(onAccept = { navController.popBackStack() })
                        }
                        composable(
                            route = "call/{caller}",
                            deepLinks = listOf(
                                androidx.navigation.navDeepLink {
                                    uriPattern = "https://ais-pre-7ldjbf3a7dwula4tvp55mq-837550959080.europe-west2.run.app/call/{caller}"
                                },
                                androidx.navigation.navDeepLink {
                                    uriPattern = "https://ais-dev-7ldjbf3a7dwula4tvp55mq-837550959080.europe-west2.run.app/call/{caller}"
                                },
                                androidx.navigation.navDeepLink {
                                    uriPattern = "http://ais-pre-7ldjbf3a7dwula4tvp55mq-837550959080.europe-west2.run.app/call/{caller}"
                                },
                                androidx.navigation.navDeepLink {
                                    uriPattern = "http://ais-dev-7ldjbf3a7dwula4tvp55mq-837550959080.europe-west2.run.app/call/{caller}"
                                },
                                androidx.navigation.navDeepLink {
                                    uriPattern = "kidspolice://call/{caller}"
                                }
                            )
                        ) { backStackEntry ->
                            val caller = backStackEntry.arguments?.getString("caller") ?: "police"
                            FakeCallScreen(callerType = caller, viewModel = viewModel, onEndCall = { 
                                navController.popBackStack() 
                            })
                        }
                    }
                }
            }
        }
    }
}
