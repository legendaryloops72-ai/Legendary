package com.aistudio.kidspolice.abcd.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.aistudio.kidspolice.abcd.data.Dialect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

class PoliceAudioPlayer(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isSirenPlaying = MutableStateFlow(false)
    val isSirenPlaying: StateFlow<Boolean> = _isSirenPlaying.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default)
    private var sirenJob: Job? = null

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
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
        }
    }

    fun speakOfficer(text: String, dialect: Dialect) {
        if (!isTtsReady || tts == null) return
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

        val result = tts?.setLanguage(targetLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.setLanguage(Locale("ar"))
        }

        tts?.setPitch(0.95f)
        tts?.setSpeechRate(0.92f)

        val params = android.os.Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "officer_speech_${System.currentTimeMillis()}")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "officer_speech")
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun togglePoliceSiren() {
        if (_isSirenPlaying.value) {
            stopPoliceSiren()
        } else {
            startPoliceSiren()
        }
    }

    fun startPoliceSiren() {
        if (_isSirenPlaying.value) return
        _isSirenPlaying.value = true
        sirenJob = scope.launch {
            val sampleRate = 44100
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val audioTrack = AudioTrack.Builder()
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
                audioTrack.stop()
                audioTrack.release()
            }
        }
    }

    fun stopPoliceSiren() {
        _isSirenPlaying.value = false
        sirenJob?.cancel()
        sirenJob = null
    }

    fun playRadioChirp() {
        scope.launch {
            try {
                val toneGen = ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 85)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 180)
                vibrate(100)
                delay(200)
                toneGen.release()
            } catch (_: Exception) { }
        }
    }

    fun playWhistle() {
        scope.launch {
            try {
                val toneGen = ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 100)
                toneGen.startTone(ToneGenerator.TONE_SUP_RINGTONE, 350)
                vibrate(150)
                delay(400)
                toneGen.release()
            } catch (_: Exception) { }
        }
    }

    fun playPoliceHorn() {
        scope.launch {
            try {
                val toneGen = ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 100)
                toneGen.startTone(ToneGenerator.TONE_DTMF_D, 400)
                vibrate(250)
                delay(450)
                toneGen.release()
            } catch (_: Exception) { }
        }
    }

    fun playRingTone() {
        scope.launch {
            try {
                val toneGen = ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 90)
                repeat(2) {
                    toneGen.startTone(ToneGenerator.TONE_SUP_RINGTONE, 900)
                    vibrate(400)
                    delay(1200)
                }
                toneGen.release()
            } catch (_: Exception) { }
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
        } catch (_: Exception) { }
    }

    fun release() {
        stopPoliceSiren()
        stopSpeaking()
        tts?.shutdown()
    }
}
