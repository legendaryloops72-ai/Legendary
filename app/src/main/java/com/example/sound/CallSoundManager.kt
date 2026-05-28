package com.example.sound

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class CallSoundManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var toneGenerator: ToneGenerator? = null

    init {
        tts = TextToSpeech(context, this)
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_RING, 100)
        } catch (e: Exception) {
            Log.e("CallSoundManager", "ToneGenerator initialization failed: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("CallSoundManager", "Arabic language is not supported or missing data on this device")
                // Fallback to default
                tts?.setLanguage(Locale.getDefault())
            } else {
                isTtsInitialized = true
                // Setup sweet pitch appropriate for kids
                tts?.setPitch(1.1f)
                tts?.setSpeechRate(0.85f) // Gentle speaking pace for kids
            }
        } else {
            Log.e("CallSoundManager", "Initialization of TTS failed")
        }
    }

    fun playRingtone() {
        Thread {
            try {
                // Ringing sound pattern
                for (i in 1..4) {
                    toneGenerator?.startTone(ToneGenerator.TONE_SUP_RINGTONE, 800)
                    Thread.sleep(1500)
                }
            } catch (e: Exception) {
                Log.e("CallSoundManager", "Ringtone play error: ${e.message}")
            }
        }.start()
    }

    fun stopRingtone() {
        toneGenerator?.stopTone()
    }

    fun speakArabicGuidance(callerType: String, childName: String) {
        if (!isTtsInitialized) {
            Log.w("CallSoundManager", "TTS not initialized yet")
            return
        }

        val nameToUse = if (childName.isNotBlank()) childName else "يا بطل"
        
        val textToSpeak = when(callerType) {
            "police" -> {
                "مرحباً يا $nameToUse! أنا شرطي الأطفال. أحبّ الأبطال الذين يستمعون لكلام بابا وماما، ويرتّبون غرفتهم وألعابهم دائماً بنشاط سرور. هل أنت ولد مجتهد اليوم؟ ممتاز، استمر في طاعة والديك وسأعطيك نجمة ذهبية كبيرة!"
            }
            "doctor" -> {
                "أهلاً بك يا $nameToUse! أنا طبيب الأطفال الطيب. تذكر دائماً غسل يديك قبل تناول الطعام الصحي اللذيذ، ونظّف أسنانك بالفرشاة والمعجون يومياً قبل النوم لتكون أسنانك ناصعة البياض وقوية. دمت بصحة ونشاط!"
            }
            "teacher" -> {
                "حياك الله يا بني $nameToUse! أنا معلمك الفاضل. أنت تلميذ ذكي ومجتهد جداً. استمر في حل الفروض المدرسية بشغف واقرأ قصصاً مفيدة كل يوم لتدخل السعادة والسرور على قلب عائلتك الكريمة. فخور بك جداً!"
            }
            else -> "مرحباً بك يا بطل!"
        }

        stopRingtone()
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "KIDS_POLICE_TTS_ID")
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    fun release() {
        tts?.shutdown()
        toneGenerator?.release()
    }
}
