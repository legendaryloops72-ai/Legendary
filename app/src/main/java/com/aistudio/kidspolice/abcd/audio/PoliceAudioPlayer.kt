package com.aistudio.kidspolice.abcd.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.aistudio.kidspolice.abcd.data.Dialect
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

class PoliceAudioPlayer(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var mediaPlayer: MediaPlayer? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isSirenPlaying = MutableStateFlow(false)
    val isSirenPlaying: StateFlow<Boolean> = _isSirenPlaying.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var sirenJob: Job? = null
    private var ttsJob: Job? = null
    private var currentAudioJob: Job? = null
    private var scenarioCallJob: Job? = null
    private var soundEffectJob: Job? = null

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getTts(): TextToSpeech? {
        if (tts == null) {
            try {
                tts = TextToSpeech(context.applicationContext) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        isTtsReady = true
                        try {
                            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                                override fun onStart(utteranceId: String?) {
                                    _isSpeaking.value = true
                                }

                                override fun onDone(utteranceId: String?) {
                                    _isSpeaking.value = false
                                }

                                override fun onError(utteranceId: String?) {
                                    _isSpeaking.value = false
                                }
                            })
                        } catch (_: Exception) {}
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("PoliceAudioPlayer", "Error initializing TextToSpeech: ${e.message}", e)
            }
        }
        return tts
    }

    override fun onInit(status: Int) {
        // handled in getTts lambda
    }

    fun testVoice01Direct() {
        scope.launch(Dispatchers.IO) {
            try {
                val resId = com.aistudio.kidspolice.abcd.R.raw.licensed_02_police_siren
                val allBytes = context.resources.openRawResource(resId).readBytes()
                var pcmStart = 44
                if (allBytes.size > 12) {
                    for (i in 12 until allBytes.size - 8) {
                        if (allBytes[i] == 'd'.code.toByte() &&
                            allBytes[i + 1] == 'a'.code.toByte() &&
                            allBytes[i + 2] == 't'.code.toByte() &&
                            allBytes[i + 3] == 'a'.code.toByte()) {
                            pcmStart = i + 8
                            break
                        }
                    }
                }
                val pcmBytes = if (pcmStart < allBytes.size) allBytes.copyOfRange(pcmStart, allBytes.size) else allBytes
                val sampleRate = 24000
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                val audioFormatObj = AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
                val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
                val bufferSize = Math.max(minBufferSize, pcmBytes.size)
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormatObj)
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
                audioTrack.setVolume(AudioTrack.getMaxVolume())
                audioTrack.play()
                audioTrack.write(pcmBytes, 0, pcmBytes.size)
                val durationMs = (pcmBytes.size.toLong() * 1000) / (sampleRate * 2)
                delay(durationMs + 500)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                android.util.Log.e("PoliceAudioPlayer", "DIRECT_ERROR: ${e.message}", e)
            }
        }
    }

    fun testSyntheticPcm() {
        scope.launch(Dispatchers.IO) {
            var trackError = "none"
            var bytesWritten = 0
            var state100 = -1
            var state500 = -1
            var state1s = -1
            var headPos = 0
            var completed = false
            try {
                android.util.Log.d("PoliceAudioPlayer", "SYNTHETIC_PCM: PLAY_CALLED=true")
                val sampleRate = 24000
                val durationSec = 1
                val totalSamples = sampleRate * durationSec
                val pcmBytes = ByteArray(totalSamples * 2)
                for (i in 0 until totalSamples) {
                    val angle = 2.0 * Math.PI * 440.0 * i / sampleRate
                    val sample = (Math.sin(angle) * 16383.0).toInt().toShort()
                    pcmBytes[2 * i] = (sample.toInt() and 0xff).toByte()
                    pcmBytes[2 * i + 1] = ((sample.toInt() shr 8) and 0xff).toByte()
                }
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                val audioFormatObj = AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
                val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
                val bufferSize = Math.max(minBufferSize, pcmBytes.size)
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormatObj)
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
                audioTrack.play()
                bytesWritten = audioTrack.write(pcmBytes, 0, pcmBytes.size)
                delay(100)
                state100 = audioTrack.playState
                delay(400)
                state500 = audioTrack.playState
                delay(500)
                state1s = audioTrack.playState
                headPos = audioTrack.playbackHeadPosition
                audioTrack.stop()
                audioTrack.release()
                completed = true
            } catch (e: Exception) {
                trackError = e.message ?: "unknown"
            }
            android.util.Log.d("PoliceAudioPlayer", "SYNTHETIC_PCM: PLAY_CALLED=true")
            android.util.Log.d("PoliceAudioPlayer", "SYNTHETIC_PCM: BYTES_WRITTEN=$bytesWritten")
            android.util.Log.d("PoliceAudioPlayer", "SYNTHETIC_PCM: PLAY_STATE_AFTER_100MS=$state100")
            android.util.Log.d("PoliceAudioPlayer", "SYNTHETIC_PCM: PLAY_STATE_AFTER_500MS=$state500")
            android.util.Log.d("PoliceAudioPlayer", "SYNTHETIC_PCM: PLAY_STATE_AFTER_1SEC=$state1s")
            android.util.Log.d("PoliceAudioPlayer", "SYNTHETIC_PCM: PLAYBACK_HEAD_POSITION=$headPos")
            android.util.Log.d("PoliceAudioPlayer", "SYNTHETIC_PCM: PLAYBACK_COMPLETED=$completed")
            android.util.Log.d("PoliceAudioPlayer", "SYNTHETIC_PCM: AUDIO_TRACK_ERROR=$trackError")
            android.util.Log.d("PoliceAudioPlayer", "SYNTHETIC_PCM: AUDIBLE_RESULT=USER_HEARD_OR_NOT")
        }
    }

    private var currentAudioTrack: AudioTrack? = null

    fun playRawAudioFile(resId: Int, onComplete: () -> Unit = {}) {
        currentAudioJob?.cancel()
        try {
            currentAudioTrack?.stop()
            currentAudioTrack?.release()
        } catch (_: Exception) {}
        currentAudioTrack = null
        _isSpeaking.value = false

        currentAudioJob = scope.launch(Dispatchers.IO) {
            var audioTrack: AudioTrack? = null
            var completed = false
            try {
                _isSpeaking.value = true
                val allBytes = context.resources.openRawResource(resId).readBytes()
                var pcmStart = 44
                if (allBytes.size > 12) {
                    for (i in 12 until allBytes.size - 8) {
                        if (allBytes[i] == 'd'.code.toByte() &&
                            allBytes[i + 1] == 'a'.code.toByte() &&
                            allBytes[i + 2] == 't'.code.toByte() &&
                            allBytes[i + 3] == 'a'.code.toByte()) {
                            pcmStart = i + 8
                            break
                        }
                    }
                }
                val pcmBytes = if (pcmStart < allBytes.size) allBytes.copyOfRange(pcmStart, allBytes.size) else allBytes
                val sampleRate = if (allBytes.size >= 28) ByteBuffer.wrap(allBytes, 24, 4).order(ByteOrder.LITTLE_ENDIAN).int else 44100
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                val audioFormatObj = AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
                val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
                val bufferSize = Math.max(minBufferSize, pcmBytes.size)
                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormatObj)
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                val isVoice14 = resId == com.aistudio.kidspolice.abcd.R.raw.licensed_03_emergency_car_arrival
                if (isVoice14) android.util.Log.d("PoliceAudioPlayer", "VOICE_14_PLAY_STARTED")

                currentAudioTrack = audioTrack
                audioTrack.setVolume(AudioTrack.getMaxVolume())
                audioTrack.play()
                audioTrack.write(pcmBytes, 0, pcmBytes.size)
                val durationMs = (pcmBytes.size.toLong() * 1000) / (sampleRate * 2)
                delay(durationMs + 300)
                completed = true
                if (isVoice14) android.util.Log.d("PoliceAudioPlayer", "VOICE_14_PLAY_COMPLETED")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e("PoliceAudioPlayer", "PLAY_RAW_ERROR: ${e.message}", e)
            } finally {
                if (currentAudioTrack === audioTrack) currentAudioTrack = null
                try {
                    if (audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING) audioTrack.stop()
                } catch (_: Exception) {}
                try {
                    audioTrack?.release()
                } catch (_: Exception) {}
                if (currentAudioJob?.isActive == false) currentAudioJob = null
                _isSpeaking.value = false
            }

            if (completed && isActive) {
                withContext(Dispatchers.Main.immediate) {
                    if (isActive) onComplete()
                }
            }
        }
    }

    private suspend fun playRawAudioSuspend(resId: Int) = suspendCancellableCoroutine<Unit> { continuation ->
        playRawAudioFile(resId) {
            if (continuation.isActive) continuation.resume(Unit) {}
        }
        continuation.invokeOnCancellation {
            currentAudioJob?.cancel()
        }
    }

    fun playScenarioCall(scenarioId: String) {
        android.util.Log.d("PoliceAudioPlayer", "PLAY_SCENARIO_CALL_CALLED")
        stopSpeaking()
        scenarioCallJob = scope.launch(Dispatchers.IO) {
            android.util.Log.d("PoliceAudioPlayer", "VOICE_14_REQUESTED")
            playRawAudioSuspend(com.aistudio.kidspolice.abcd.R.raw.licensed_03_emergency_car_arrival)
            val scenarioResId = when (scenarioId) {
                "sleep_early" -> com.aistudio.kidspolice.abcd.R.raw.licensed_27_urgent_emergency_tone
                "eating_food" -> com.aistudio.kidspolice.abcd.R.raw.licensed_13_car_engine_start
                "listen_parents" -> com.aistudio.kidspolice.abcd.R.raw.licensed_22_radio_signal
                "homework_study" -> com.aistudio.kidspolice.abcd.R.raw.licensed_26_double_beep_alert
                "hero_reward" -> com.aistudio.kidspolice.abcd.R.raw.licensed_29_police_whistle
                else -> com.aistudio.kidspolice.abcd.R.raw.licensed_23_radio_transmission
            }
            playRawAudioSuspend(scenarioResId)
        }
        scenarioCallJob?.invokeOnCompletion { if (scenarioCallJob?.isActive == false) scenarioCallJob = null }
    }

    fun speakOfficer(text: String, dialect: Dialect) {
        android.util.Log.d("PoliceAudioPlayer", "POLICE_CALL_BUTTON_CLICKED")
        ttsJob?.cancel()
        val resId = when {
            text.contains("نمت") || text.contains("سريرك") || text.contains("تختك") || text.contains("تصبح على خير") -> com.aistudio.kidspolice.abcd.R.raw.licensed_27_urgent_emergency_tone
            text.contains("تاكل") || text.contains("أكلك") || text.contains("وجبتك") || text.contains("طبقك") -> com.aistudio.kidspolice.abcd.R.raw.licensed_13_car_engine_start
            text.contains("ماما وبابا") || text.contains("والديك") || text.contains("احترام") || text.contains("طاعة") -> com.aistudio.kidspolice.abcd.R.raw.licensed_22_radio_signal
            text.contains("واجباتك") || text.contains("دراسة") || text.contains("كتبك") || text.contains("المذاكرة") -> com.aistudio.kidspolice.abcd.R.raw.licensed_26_double_beep_alert
            text.contains("بطلنا العظيم") || text.contains("وسام") || text.contains("مكافأة") || text.contains("ألف مبروك") -> com.aistudio.kidspolice.abcd.R.raw.licensed_29_police_whistle
            else -> com.aistudio.kidspolice.abcd.R.raw.licensed_23_radio_transmission
        }
        playRawAudioFile(resId)
    }

    private fun playAudioFileVerified(file: File) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                android.util.Log.d("PoliceAudioPlayer", "AUDIO_MEDIA_PLAYER_CREATED=true")
                setDataSource(file.absolutePath)
                android.util.Log.d("PoliceAudioPlayer", "AUDIO_PREPARE_STARTED=true")
                prepare()
                android.util.Log.d("PoliceAudioPlayer", "AUDIO_PREPARED=true")
                setOnCompletionListener {
                    _isSpeaking.value = false
                    android.util.Log.d("PoliceAudioPlayer", "AUDIO_COMPLETED=true")
                }
                setOnErrorListener { _, what, extra ->
                    _isSpeaking.value = false
                    android.util.Log.e("PoliceAudioPlayer", "AUDIO_PLAYER_ERROR: what=$what extra=$extra")
                    android.util.Log.d("PoliceAudioPlayer", "AUDIO_PLAYER_ERROR=what=$what extra=$extra")
                    true
                }
                android.util.Log.d("PoliceAudioPlayer", "AUDIO_START_CALLED=true")
                start()
                android.util.Log.d("PoliceAudioPlayer", "AUDIO_IS_PLAYING=$isPlaying")
            }
        } catch (e: Exception) {
            _isSpeaking.value = false
            android.util.Log.e("PoliceAudioPlayer", "AUDIO_PLAYER_ERROR exception: ${e.message}", e)
            android.util.Log.d("PoliceAudioPlayer", "AUDIO_PLAYER_ERROR=${e.message}")
        }
    }

    private fun createWavHeader(pcmData: ByteArray, sampleRate: Int, channels: Int, bitsPerSample: Int): ByteArray {
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8
        val dataSize = pcmData.size
        val chunkSize = 36 + dataSize
        val header = ByteArray(44)
        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        header[4] = (chunkSize and 0xff).toByte(); header[5] = ((chunkSize shr 8) and 0xff).toByte(); header[6] = ((chunkSize shr 16) and 0xff).toByte(); header[7] = ((chunkSize shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0
        header[20] = 1; header[21] = 0; header[22] = channels.toByte(); header[23] = 0
        header[24] = (sampleRate and 0xff).toByte(); header[25] = ((sampleRate shr 8) and 0xff).toByte(); header[26] = ((sampleRate shr 16) and 0xff).toByte(); header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte(); header[29] = ((byteRate shr 8) and 0xff).toByte(); header[30] = ((byteRate shr 16) and 0xff).toByte(); header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = blockAlign.toByte(); header[33] = 0; header[34] = bitsPerSample.toByte(); header[35] = 0
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
        header[40] = (dataSize and 0xff).toByte(); header[41] = ((dataSize shr 8) and 0xff).toByte(); header[42] = ((dataSize shr 16) and 0xff).toByte(); header[43] = ((dataSize shr 24) and 0xff).toByte()
        return header + pcmData
    }

    private fun fallbackAndroidTts(text: String, dialect: Dialect) {
        val ttsInstance = getTts()
        if (!isTtsReady || ttsInstance == null) {
            _isSpeaking.value = false
            return
        }
        val targetLocale = when (dialect) {
            Dialect.SAUDI -> Locale("ar", "SA")
            Dialect.EGYPTIAN -> Locale("ar", "EG")
            Dialect.SYRIAN -> Locale("ar", "SY")
            Dialect.GULF -> Locale("ar", "AE")
            Dialect.IRAQI -> Locale("ar", "IQ")
            Dialect.MOROCCAN -> Locale("ar", "MA")
            Dialect.ALGERIAN -> Locale("ar", "DZ")
            Dialect.FASHA -> Locale("ar")
        }
        try {
            val result = ttsInstance.setLanguage(targetLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) ttsInstance.setLanguage(Locale("ar"))
            ttsInstance.setPitch(0.95f)
            ttsInstance.setSpeechRate(0.92f)
            val params = android.os.Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "officer_speech_${System.currentTimeMillis()}")
            ttsInstance.speak(text, TextToSpeech.QUEUE_FLUSH, params, "officer_speech")
        } catch (e: Exception) {
            _isSpeaking.value = false
            android.util.Log.e("PoliceAudioPlayer", "Error in fallbackAndroidTts: ${e.message}", e)
        }
    }

    fun stopSpeaking() {
        scenarioCallJob?.cancel()
        scenarioCallJob = null
        currentAudioJob?.cancel()
        currentAudioJob = null
        soundEffectJob?.cancel()
        soundEffectJob = null
        ttsJob?.cancel()
        try {
            currentAudioTrack?.stop()
            currentAudioTrack?.release()
            currentAudioTrack = null
        } catch (_: Exception) {}
        try {
            tts?.stop()
        } catch (_: Exception) {}
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (_: Exception) {}
        _isSpeaking.value = false
    }

    fun togglePoliceSiren() {
        if (_isSirenPlaying.value) stopPoliceSiren() else startPoliceSiren()
    }

    fun startPoliceSiren() {
        if (_isSirenPlaying.value) return
        soundEffectJob?.cancel()
        _isSirenPlaying.value = true
        sirenJob = scope.launch {
            val sampleRate = 44100
            val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
                .setAudioFormat(AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                .setBufferSizeInBytes(minBufferSize)
                .build()
            audioTrack.play()
            try {
                var phase = 0.0
                val buffer = ShortArray(1024)
                var time = 0.0
                while (isActive && _isSirenPlaying.value) {
                    val frequency = 650.0 + 350.0 * sin(2 * PI * 1.8 * time)
                    val phaseIncrement = 2 * PI * frequency / sampleRate
                    for (i in buffer.indices) {
                        buffer[i] = (sin(phase) * Short.MAX_VALUE * 0.45).toInt().toShort()
                        phase += phaseIncrement
                        if (phase > 2 * PI) phase -= 2 * PI
                    }
                    time += buffer.size.toDouble() / sampleRate
                    audioTrack.write(buffer, 0, buffer.size)
                }
            } finally {
                try { if (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) audioTrack.stop() } catch (_: Exception) {}
                try { audioTrack.release() } catch (_: Exception) {}
            }
        }
    }

    fun stopPoliceSiren() {
        _isSirenPlaying.value = false
        sirenJob?.cancel()
        sirenJob = null
    }

    private fun playSoundEffect(block: suspend () -> Unit) {
        soundEffectJob?.cancel()
        soundEffectJob = scope.launch {
            try { block() } catch (_: CancellationException) { throw CancellationException() } catch (_: Exception) {}
        }
    }

    fun playRadioChirp() {
        playSoundEffect {
            val toneGen = ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 85)
            try {
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 180)
                vibrate(100)
                delay(200)
            } finally { toneGen.release() }
        }
    }

    fun playWhistle() {
        playSoundEffect {
            stopSpeaking()
            val toneGen = ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 100)
            try {
                toneGen.startTone(ToneGenerator.TONE_SUP_RINGTONE, 350)
                vibrate(150)
                delay(400)
            } finally { toneGen.release() }
        }
    }

    fun playPoliceHorn() {
        playSoundEffect {
            stopSpeaking()
            val toneGen = ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 100)
            try {
                toneGen.startTone(ToneGenerator.TONE_DTMF_D, 400)
                vibrate(250)
                delay(450)
            } finally { toneGen.release() }
        }
    }

    fun playRingTone() {
        playSoundEffect {
            stopSpeaking()
            val toneGen = ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 90)
            try {
                repeat(2) {
                    toneGen.startTone(ToneGenerator.TONE_SUP_RINGTONE, 900)
                    vibrate(400)
                    delay(1200)
                }
            } finally { toneGen.release() }
        }
    }

    private fun vibrate(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        stopPoliceSiren()
        stopSpeaking()
        tts?.shutdown()
        try {
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (_: Exception) {}
    }
}
