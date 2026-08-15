package com.aistudio.kidspolice.abcd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aistudio.kidspolice.abcd.ads.AdManager
import com.aistudio.kidspolice.abcd.ui.AppViewModel
import com.aistudio.kidspolice.abcd.ui.screens.CallScreen
import com.aistudio.kidspolice.abcd.ui.screens.CertificateScreen
import com.aistudio.kidspolice.abcd.ui.screens.DialerScreen
import com.aistudio.kidspolice.abcd.ui.screens.HomeScreen
import com.aistudio.kidspolice.abcd.ui.screens.MissionsScreen
import com.aistudio.kidspolice.abcd.ui.screens.SoundsScreen
import com.aistudio.kidspolice.abcd.ui.theme.KidsPoliceTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Ads SDK
        AdManager.initialize(this)

        setContent {
            KidsPoliceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KidsPoliceAppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun KidsPoliceAppNavigation(viewModel: AppViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToDialer = { navController.navigate("dialer") },
                onNavigateToSounds = { navController.navigate("sounds") },
                onNavigateToMissions = { navController.navigate("missions") },
                onNavigateToCertificate = { navController.navigate("certificate") },
                onStartCall = { navController.navigate("call") }
            )
        }

        composable("call") {
            CallScreen(
                viewModel = viewModel,
                onCallEnded = {
                    navController.popBackStack()
                }
            )
        }

        composable("dialer") {
            DialerScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onStartCall = { navController.navigate("call") }
            )
        }

        composable("sounds") {
            SoundsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("missions") {
            MissionsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("certificate") {
            CertificateScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
