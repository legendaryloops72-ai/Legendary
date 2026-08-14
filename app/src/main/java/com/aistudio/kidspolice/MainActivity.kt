package com.aistudio.kidspolice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.aistudio.kidspolice.screens.CallScreen
import com.aistudio.kidspolice.screens.HomeScreen
import com.aistudio.kidspolice.ui.theme.KidsPoliceTheme
import com.aistudio.kidspolice.viewmodel.MainViewModel
import com.aistudio.kidspolice.viewmodel.UiState

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        viewModel.initTts(this)

        setContent {
            KidsPoliceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsState()

                    when (uiState) {
                        is UiState.Home -> {
                            HomeScreen(
                                onCallClick = { name, gender, behavior ->
                                    viewModel.startCall(name, gender, behavior)
                                }
                            )
                        }
                        is UiState.IncomingCall -> {
                            CallScreen(
                                isIncoming = true,
                                onAnswer = { viewModel.answerCall() },
                                onEndCall = { viewModel.endCall() }
                            )
                        }
                        is UiState.OnCall -> {
                            CallScreen(
                                isIncoming = false,
                                onAnswer = { },
                                onEndCall = { viewModel.endCall() }
                            )
                        }
                    }
                }
            }
        }
    }
}
