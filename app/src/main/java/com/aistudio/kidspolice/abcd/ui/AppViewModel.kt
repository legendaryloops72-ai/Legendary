package com.aistudio.kidspolice.abcd.ui

import android.app.Application
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.kidspolice.abcd.data.Dialect
import com.aistudio.kidspolice.abcd.data.EducationalMission
import com.aistudio.kidspolice.abcd.data.PoliceRepository
import com.aistudio.kidspolice.abcd.data.PoliceScenario
import com.aistudio.kidspolice.abcd.data.PoliceSound
import com.aistudio.kidspolice.abcd.data.SoundType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sin

data class AppUiState(
    val selectedDialect: Dialect = Dialect.SAUDI,
    val childName: String = "البطل الصغير",
    val childGender: String = "ولد", // "ولد" or "بنت"
    val totalScore: Int = 180,
    val missions: List<EducationalMission> = PoliceRepository.initialMissions,
    val currentPlayingSoundId: String? = null,
    // Active Call State
    val isCallActive: Boolean = false,
    val callStatusText: String = "جاري الاتصال...",
    val activeScenario: PoliceScenario? = null,
    val activeDialogueIndex: Int = 0,
    val callDurationSeconds: Int = 0,
    val isMicMuted: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val isIncomingCall: Boolean = false,
    val isCallAnswered: Boolean = false,
    val customDialNumber: String = ""
)

class AppViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private var soundSynthJob: Job? = null
    private var activeAudioTrack: AudioTrack? = null

    private var callTimerJob: Job? = null
    private var dialogueJob: Job? = null

    init {
        try {
            tts = TextToSpeech(application.applicationContext, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar"))
            isTtsReady = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
            tts?.setPitch(0.85f) // slightly deeper authoritative/friendly police voice
            tts?.setSpeechRate(0.95f)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {}
                override fun onError(utteranceId: String?) {}
            })
        }
    }

    fun setSelectedDialect(dialect: Dialect) {
        _uiState.update { it.copy(selectedDialect = dialect) }
    }

    fun updateChildProfile(name: String, gender: String) {
        _uiState.update { it.copy(childName = name, childGender = gender) }
    }

    fun appendDialNumber(digit: String) {
        _uiState.update {
            if (it.customDialNumber.length < 6) {
                it.copy(customDialNumber = it.customDialNumber + digit)
            } else it
        }
    }

    fun deleteDialNumber() {
        _uiState.update {
            if (it.customDialNumber.isNotEmpty()) {
                it.copy(customDialNumber = it.customDialNumber.dropLast(1))
            } else it
        }
    }

    fun clearDialNumber() {
        _uiState.update { it.copy(customDialNumber = "") }
    }

    fun toggleMission(missionId: String) {
        _uiState.update { state ->
            val updated = state.missions.map { m ->
                if (m.id == missionId) {
                    val newCompleted = !m.isCompleted
                    m.copy(isCompleted = newCompleted)
                } else m
            }
            val completedPoints = updated.filter { it.isCompleted }.sumOf { it.points }
            state.copy(missions = updated, totalScore = 100 + completedPoints)
        }
    }

    // --- SOUND EFFECTS SYNTHESIZER ---
    fun togglePlaySound(sound: PoliceSound) {
        if (_uiState.value.currentPlayingSoundId == sound.id) {
            stopAllAudio()
        } else {
            stopAllAudio()
            _uiState.update { it.copy(currentPlayingSoundId = sound.id) }
            soundSynthJob = viewModelScope.launch(Dispatchers.Default) {
                playSynthesizedSound(sound.soundType)
                _uiState.update { it.copy(currentPlayingSoundId = null) }
            }
        }
    }

    fun stopAllAudio() {
        soundSynthJob?.cancel()
        soundSynthJob = null
        try {
            activeAudioTrack?.stop()
            activeAudioTrack?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        activeAudioTrack = null
        try {
            tts?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _uiState.update { it.copy(currentPlayingSoundId = null) }
    }

    private suspend fun playSynthesizedSound(type: SoundType) {
        val sampleRate = 22050
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        activeAudioTrack = track
        track.play()

        val bufferSize = 1024
        val buffer = ShortArray(bufferSize)
        var phase = 0.0

        val durationMillis = when (type) {
            SoundType.SIREN_CLASSIC, SoundType.SIREN_FAST, SoundType.SIREN_WAIL -> 8000L
            SoundType.RADIO_BEEP, SoundType.HORN, SoundType.POLICE_WHISTLE -> 3500L
            SoundType.WALKIE_TALKIE, SoundType.HELI_SOUND -> 6000L
        }

        val startTime = SystemClock.elapsedRealtime()

        while (viewModelScope.isActive && (SystemClock.elapsedRealtime() - startTime < durationMillis)) {
            val elapsed = (SystemClock.elapsedRealtime() - startTime).toDouble() / 1000.0

            for (i in 0 until bufferSize) {
                val t = elapsed + (i.toDouble() / sampleRate)
                val freq = when (type) {
                    SoundType.SIREN_CLASSIC -> {
                        // European/Arab Police two-tone: 600Hz <-> 900Hz alternation every 0.6s
                        if ((t % 1.2) < 0.6) 650.0 else 920.0
                    }
                    SoundType.SIREN_FAST -> {
                        // High-speed siren wail sweeping between 700Hz and 1400Hz
                        950.0 + 400.0 * sin(2.0 * Math.PI * 2.5 * t)
                    }
                    SoundType.SIREN_WAIL -> {
                        // Slow dramatic siren wail 500Hz to 1100Hz
                        800.0 + 350.0 * sin(2.0 * Math.PI * 0.8 * t)
                    }
                    SoundType.RADIO_BEEP -> {
                        // Walkie talkie squelch / 10-4 beep burst
                        if (t < 0.25 || (t in 0.4..0.65)) 1200.0 else 0.0
                    }
                    SoundType.HORN -> {
                        // Police dual-tone honk
                        if ((t % 0.8) < 0.4) 420.0 else 0.0
                    }
                    SoundType.POLICE_WHISTLE -> {
                        // 2200Hz warble whistle
                        if ((t % 0.7) < 0.45) 2300.0 + 200.0 * sin(2.0 * Math.PI * 30.0 * t) else 0.0
                    }
                    SoundType.WALKIE_TALKIE -> {
                        // Static noise with periodic command bleeps
                        if (t < 0.3 || (t in 2.0..2.3)) 1000.0 else 250.0 + (Math.random() * 200)
                    }
                    SoundType.HELI_SOUND -> {
                        // Helicopter rotor chop low frequency pulsation
                        val pulse = sin(2.0 * Math.PI * 14.0 * t)
                        if (pulse > 0) 180.0 else 90.0
                    }
                }

                phase += 2.0 * Math.PI * freq / sampleRate
                val sample = if (freq > 0) (sin(phase) * 26000.0).toInt().toShort() else 0
                buffer[i] = sample
            }
            track.write(buffer, 0, bufferSize)
        }

        try {
            track.stop()
            track.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- CALL SYSTEM ---
    fun startOutgoingCall(scenario: PoliceScenario) {
        stopAllAudio()
        _uiState.update {
            it.copy(
                isCallActive = true,
                isIncomingCall = false,
                isCallAnswered = false,
                activeScenario = scenario,
                activeDialogueIndex = 0,
                callDurationSeconds = 0,
                callStatusText = "جاري الاتصال بالضابط..."
            )
        }

        // Simulate phone ringing sound
        viewModelScope.launch(Dispatchers.Default) {
            playRingingTone(3)
            // Answer after ringing
            _uiState.update {
                it.copy(
                    isCallAnswered = true,
                    callStatusText = "مكالمة نشطة (متصل)"
                )
            }
            startCallTimer()
            startScenarioDialogues(scenario)
        }
    }

    fun startIncomingCall(scenario: PoliceScenario) {
        stopAllAudio()
        _uiState.update {
            it.copy(
                isCallActive = true,
                isIncomingCall = true,
                isCallAnswered = false,
                activeScenario = scenario,
                activeDialogueIndex = 0,
                callDurationSeconds = 0,
                callStatusText = "مكالمة واردة من شرطة الأطفال..."
            )
        }

        // Play continuous incoming ringtone
        viewModelScope.launch(Dispatchers.Default) {
            while (_uiState.value.isCallActive && !_uiState.value.isCallAnswered) {
                playRingingTone(1)
                delay(1200)
            }
        }
    }

    fun answerIncomingCall() {
        val scenario = _uiState.value.activeScenario ?: return
        _uiState.update {
            it.copy(
                isCallAnswered = true,
                isIncomingCall = false,
                callStatusText = "مكالمة نشطة (متصل)"
            )
        }
        startCallTimer()
        startScenarioDialogues(scenario)
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (_uiState.value.isCallActive && _uiState.value.isCallAnswered) {
                delay(1000)
                _uiState.update { it.copy(callDurationSeconds = it.callDurationSeconds + 1) }
            }
        }
    }

    private fun startScenarioDialogues(scenario: PoliceScenario) {
        dialogueJob?.cancel()
        dialogueJob = viewModelScope.launch {
            for (index in scenario.dialogues.indices) {
                if (!_uiState.value.isCallActive) break
                _uiState.update { it.copy(activeDialogueIndex = index) }
                val line = scenario.dialogues[index]

                // Speak via TTS if ready
                if (isTtsReady) {
                    tts?.speak(line.text, TextToSpeech.QUEUE_FLUSH, null, "dial_${scenario.id}_$index")
                }

                val waitTime = (line.durationSeconds * 1000L).coerceAtLeast(3500L)
                delay(waitTime)
                delay(line.pauseAfterSeconds * 1000L)
            }
        }
    }

    private suspend fun playRingingTone(times: Int) {
        val sampleRate = 22050
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        track.play()
        val buffer = ShortArray(1024)

        for (t in 0 until times) {
            if (!_uiState.value.isCallActive || _uiState.value.isCallAnswered) break
            var phase1 = 0.0
            var phase2 = 0.0
            val samplesCount = sampleRate * 1.5 // 1.5 sec ring
            var written = 0

            while (written < samplesCount && _uiState.value.isCallActive && !_uiState.value.isCallAnswered) {
                for (i in 0 until 1024) {
                    phase1 += 2.0 * Math.PI * 440.0 / sampleRate
                    phase2 += 2.0 * Math.PI * 480.0 / sampleRate
                    val sample = ((sin(phase1) + sin(phase2)) * 14000.0).toInt().toShort()
                    buffer[i] = sample
                }
                track.write(buffer, 0, 1024)
                written += 1024
            }
            delay(1500) // Pause between rings
        }

        try {
            track.stop()
            track.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun endCall() {
        dialogueJob?.cancel()
        dialogueJob = null
        callTimerJob?.cancel()
        callTimerJob = null
        tts?.stop()
        _uiState.update {
            it.copy(
                isCallActive = false,
                isIncomingCall = false,
                isCallAnswered = false,
                activeScenario = null,
                activeDialogueIndex = 0,
                callDurationSeconds = 0
            )
        }
    }

    fun toggleMic() {
        _uiState.update { it.copy(isMicMuted = !it.isMicMuted) }
    }

    fun toggleSpeaker() {
        _uiState.update { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    override fun onCleared() {
        super.onCleared()
        stopAllAudio()
        try {
            tts?.shutdown()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
