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
import com.example.ads.AdManager
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        AdManager.initialize(this)
        
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "kids_police_db"
        ).build()
        val repository = AppRepository(database.appDao())
        val factory = AppViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: AppViewModel = viewModel(factory = factory)
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        // يفتح مباشر إلى الصفحة الرئيسية بدون حواجز
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(onSplashFinished = {
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
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
                                }
                            )
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
                                onShowPrivacyPolicy = { navController.navigate("privacy") }
                            )
                        }
                        composable("privacy") {
                            PrivacyPolicyScreen(onAccept = { navController.popBackStack() })
                        }
                        composable("call/{caller}") { backStackEntry ->
                            val caller = backStackEntry.arguments?.getString("caller") ?: "police"
                            FakeCallScreen(callerType = caller, viewModel = viewModel, onEndCall = { 
                                AdManager.showInterstitialAd(this@MainActivity) {
                                    navController.popBackStack() 
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}
