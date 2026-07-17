package com.example.sound

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.os.Handler
import android.os.Looper
import java.util.Locale

class CallSoundManager(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    @Volatile
    private var isTtsInitialized = false
    private var toneGenerator: ToneGenerator? = null

    var onSpeakingChanged: ((Boolean) -> Unit)? = null
    private var mediaPlayer: MediaPlayer? = null

    @Volatile
    private var isReleased = false
    @Volatile
    private var isRingtonePlaying = false

    init {
        tts = TextToSpeech(context, this)
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_RING, 100)
        } catch (e: Exception) {
            Log.e("CallSoundManager", "ToneGenerator initialization failed: ${e.message}")
        }
        try {
            context.cacheDir.listFiles { file -> file.name.startsWith("sound_") && file.name.contains("_temp") }?.forEach { file ->
                file.delete()
            }
        } catch (e: Exception) {
            Log.e("CallSoundManager", "Temp files cleanup failed: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.forLanguageTag("ar"))
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

            tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                private val mainHandler = Handler(Looper.getMainLooper())
                override fun onStart(utteranceId: String?) {
                    mainHandler.post { onSpeakingChanged?.invoke(true) }
                }
                override fun onDone(utteranceId: String?) {
                    mainHandler.post { onSpeakingChanged?.invoke(false) }
                }
                override fun onError(utteranceId: String?) {
                    mainHandler.post { onSpeakingChanged?.invoke(false) }
                }
            })
        } else {
            Log.e("CallSoundManager", "Initialization of TTS failed")
        }
    }

    private fun safeStartTone(toneType: Int, durationMs: Int) {
        synchronized(this) {
            if (!isReleased) {
                try {
                    toneGenerator?.startTone(toneType, durationMs)
                } catch (e: Exception) {
                    Log.e("CallSoundManager", "safeStartTone error: ${e.message}")
                }
            }
        }
    }

    private fun safeStopTone() {
        synchronized(this) {
            if (!isReleased) {
                try {
                    toneGenerator?.stopTone()
                } catch (e: Exception) {
                    Log.e("CallSoundManager", "safeStopTone error: ${e.message}")
                }
            }
        }
    }

    fun playRingtone() {
        if (isReleased) return
        isRingtonePlaying = true
        Thread {
            try {
                // Ringing sound pattern
                for (i in 1..4) {
                    if (isReleased || !isRingtonePlaying) break
                    safeStartTone(ToneGenerator.TONE_SUP_RINGTONE, 800)
                    Thread.sleep(1500)
                }
            } catch (e: Exception) {
                Log.e("CallSoundManager", "Ringtone play error: ${e.message}")
            } finally {
                isRingtonePlaying = false
            }
        }.start()
    }

    fun stopRingtone() {
        isRingtonePlaying = false
        safeStopTone()
    }

    fun speakArabicGuidance(callerType: String, childName: String) {
        if (!isTtsInitialized || isReleased) {
            Log.w("CallSoundManager", "TTS not initialized yet or CallSoundManager has been released")
            return
        }

        stopRingtone()

        // Handle specific asset-based calls first
        val assetToPlay = when {
            callerType.startsWith("police") -> "sounds/vehicles/police.mp3"
            callerType == "doctor" -> "sounds/animals/horse.wav"
            callerType == "monster" || callerType == "police_monster" -> "sounds/animals/Creatures_Snoozing_Snark_02.mp3"
            else -> null
        }

        if (assetToPlay != null) {
            playAudioFromAsset(
                assetPath = assetToPlay,
                onStart = {
                    Log.d("CallSoundManager", "Playing asset for $callerType: $assetToPlay")
                },
                onComplete = {
                    // One-sided call, no TTS after the sound if it's a recording
                    Log.d("CallSoundManager", "Asset playback complete for $callerType")
                },
                onError = {
                    Log.e("CallSoundManager", "Error playing asset $assetToPlay, falling back to TTS")
                    speakTtsFallback(callerType, childName)
                }
            )
        } else {
            speakTtsFallback(callerType, childName)
        }
    }

    private fun speakTtsFallback(callerType: String, childName: String) {
        // Try to find in repository first
        val repositoryDialogue = when {
            callerType.startsWith("police_") -> {
                val trigger = callerType.removePrefix("police_")
                CallCharacterRepository.getDialogue("police", trigger)
            }
            callerType == "police" -> CallCharacterRepository.getDialogue("police", "greeting")
            callerType == "doctor" -> CallCharacterRepository.getDialogue("doctor", "greeting")
            callerType == "teacher" -> CallCharacterRepository.getDialogue("teacher", "greeting")
            else -> null
        }

        val nameToUse = if (childName.isNotBlank()) childName else "يا بطل"
        val textToSpeak = repositoryDialogue?.text ?: when(callerType) {
            "police_monster", "monster" -> {
                "أهلاً أهلاً! أنا الوحش اللطيف، أحب الأطفال الذين ينظفون غرفهم ويأكلون الخضروات! هل أنت بطل اليوم؟ ها ها ها!"
            }
            "police" -> {
                "أهلاً ومرحباً بك يا بطل! أنا الشرطي سامر صديقك المفضل من شرطة الأطفال الإيجابية والذكية. أنا سعيد جداً بالحديث معك اليوم! أخبرني يا بطل، كيف حالك؟ وما هي أفعالك الطيبة اليوم؟"
            }
            "police_not_listening" -> {
                "مرحباً يا بطل! أنا الشرطي سامر صديقك المخلص. سمعت أنك تواجه صعوبة صغيرة في سماع كلام الماما والبابا اليوم. هل تعلم يا بطل أن سماع كلام عائلتنا هو سر قوتنا وذكائنا؟ الماما والبابا يحبونك جداً ويريدونك أن تكون أفضل بطل في العالم!"
            }
            "police_sleep_late" -> {
                "أهلاً ومرحباً بصديقي البطل الخارق! أنا الشرطي سامر. لقد أخبرني درع الحراسة أنك ما زلت مستيقظاً وتسهر حتى وقت متأخر! هل تعلم أن النوم المبكر هو الذي يعيد شحن طاقتك الخارقة وذكائك العبقري؟"
            }
            "police_refusing_study" -> {
                "أهلاً بعبقري الغد ومستكشفنا الذكي! أنا الشرطي سامر. أخبرتني كتب الأبطال أنك تشعر ببعض الكسل تجاه حل واجباتك المدرسية اليوم. هل تعلم أن كل معلومة جديدة تقرأها تزيد من قوة عقلك وتجعلك تقترب خطوة لتكون طبيباً بارعاً؟"
            }
            "police_eating_sweets" -> {
                "أهلاً بصديقي البطل ذو الابتسامة الجميلة! أنا الشرطي سامر. علمت أنك ترغب في تناول الكثير من الحلوى والسكاكر اليوم وترفض تفريش أسنانك! الحلوى لذيذة ولكن الكثير منها يجعل الأسنان تشتكي وتتألم."
            }
            "police_messy_room" -> {
                "مرحباً بصديقي المنظم والذكي! أنا الشرطي سامر. لقد لاحظت دوريتنا أن ألعابك الجميلة مبعثرة على الأرض بعد اللعب. هل تعلم أن البطل الخارق يتميز بترتيب مملكته وغرفته لتظل جميلة ومنسقة؟"
            }
            "police_helping_parents" -> {
                "أهلاً وسهلاً ببطلنا الكبير والمحبوب جداً! أنا الشرطي سامر. لقد اتصلت بك اليوم خصيصاً لأقدم لك أسمى آيات الشكر والتقدير! لقد وصلتني تقارير رائعة تفيد بأنك بطل متعاون جداً."
            }
            "police_success" -> {
                "يا لها من فرحة كبيرة! مرحباً ببطلنا المتفوق وعبقري المستقبل! أنا الشرطي سامر. أهاديك تحية عسكرية فخرية بمناسبة نجاحك الباهر واجتهادك المتميز في المدرسة!"
            }
            "police_healthy_food" -> {
                "أهلاً بصديقي القوي ذو النشاط الخارق! أنا الشرطي سامر. لقد وصلتني أخبار سعيدة جداً اليوم بأنك تتناول طعاماً صحياً ومغذياً، وتأكل الخضروات والفاكهة لتزيد من قوة عضلاتك وعقلك!"
            }
            "doctor" -> {
                "أهلاً يا حلوين! أنا الدكتور طيب صديقكم الحبيب وطبيب الأطفال والأسنان البارع. أتصل بكم لأطمئن على صحة أسنانكم البراقة والجميلة!"
            }
            "teacher" -> {
                "أهلاً بك يا عبقري المستقبل ويا مستكشفنا الذكي! أنا معلمك الفاضل الأستاذ منير، فخور جداً بشغفك وحبك للتعلم والقراءة المستمرة."
            }
            "principal" -> {
                "أهلاً بك يا بطل مدرستنا المتفوق! أنا مدير مدرستك الأستاذ عادل. أتصل اليوم لأعبر عن فخري بتميزك واحترامك للماما والبابا وسلوكك الرائع."
            }
            else -> "أهلاً بك يا بطل! أنا صديقك الموجه الإيجابي الذكي، متحمس جداً للحديث معك والتعلم منك!"
        }

        try {
            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "KIDS_POLICE_TTS_ID")
        } catch (e: Exception) {
            Log.e("CallSoundManager", "TTS speak failed: ${e.message}")
        }
    }


    fun speakDialogue(characterId: String, trigger: String) {
        if (!isTtsInitialized || isReleased) return
        val dialogue = CallCharacterRepository.getDialogue(characterId, trigger)
        dialogue?.let {
            stopRingtone()
            try {
                tts?.speak(it.text, TextToSpeech.QUEUE_FLUSH, null, "KIDS_DIALOGUE_ID")
            } catch (e: Exception) {
                Log.e("CallSoundManager", "speakDialogue error: ${e.message}")
            }
        }
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
            onSpeakingChanged?.invoke(false)
        } catch (e: Exception) {
            Log.e("CallSoundManager", "TTS stop failed: ${e.message}")
        }
    }

    fun speakDirect(text: String) {
        if (!isTtsInitialized || isReleased) {
            Log.w("CallSoundManager", "TTS not initialized yet or CallSoundManager has been released")
            return
        }
        stopRingtone()
        try {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "KIDS_AUDIO_DIRECT_ID")
        } catch (e: Exception) {
            Log.e("CallSoundManager", "speakDirect error: ${e.message}")
        }
    }

    fun playSynthSound(patternType: String) {
        if (isReleased) return
        Thread {
            try {
                if (isReleased) return@Thread
                when (patternType) {
                    "lion" -> {
                        // Low pitched rumbling beeps for lion
                        safeStartTone(ToneGenerator.TONE_CDMA_LOW_L, 400)
                        Thread.sleep(300)
                        if (isReleased) return@Thread
                        safeStartTone(ToneGenerator.TONE_CDMA_LOW_L, 500)
                    }
                    "dog" -> {
                        // High short bark tone
                        safeStartTone(ToneGenerator.TONE_PROP_BEEP, 120)
                        Thread.sleep(150)
                        if (isReleased) return@Thread
                        safeStartTone(ToneGenerator.TONE_PROP_BEEP, 120)
                    }
                    "bird" -> {
                        // Chirps high beep
                        safeStartTone(ToneGenerator.TONE_CDMA_HIGH_L, 80)
                        Thread.sleep(100)
                        if (isReleased) return@Thread
                        safeStartTone(ToneGenerator.TONE_CDMA_HIGH_L, 80)
                    }
                    "cat" -> {
                        // Meow tone pattern
                        safeStartTone(ToneGenerator.TONE_CDMA_PIP, 200)
                    }
                    "siren" -> {
                        // Police siren high low
                        for (i in 1..3) {
                            if (isReleased) return@Thread
                            safeStartTone(ToneGenerator.TONE_SUP_DIAL, 250)
                            Thread.sleep(300)
                            if (isReleased) return@Thread
                            safeStartTone(ToneGenerator.TONE_SUP_RINGTONE, 250)
                            Thread.sleep(300)
                        }
                    }
                    "car_or_sports" -> {
                        // Accelerating tone
                        safeStartTone(ToneGenerator.TONE_SUP_BUSY, 200)
                    }
                    "laser" -> {
                        // Sci-fi laser blast
                        safeStartTone(ToneGenerator.TONE_PROP_BEEP2, 100)
                        Thread.sleep(120)
                        if (isReleased) return@Thread
                        safeStartTone(ToneGenerator.TONE_PROP_BEEP2, 150)
                    }
                    "portal" -> {
                        // magical chime
                        safeStartTone(ToneGenerator.TONE_PROP_NACK, 300)
                    }
                    "lightning" -> {
                        safeStartTone(ToneGenerator.TONE_SUP_RADIO_ACK, 200)
                    }
                    "bell" -> {
                        // Doorbell ding-dong
                        safeStartTone(ToneGenerator.TONE_PROP_ACK, 150)
                        Thread.sleep(200)
                        if (isReleased) return@Thread
                        safeStartTone(ToneGenerator.TONE_PROP_ACK, 300)
                    }
                    "vacuum" -> {
                        safeStartTone(ToneGenerator.TONE_PROP_BEEP2, 500)
                    }
                    "funny" -> {
                        // Cartoon funny boop beeps
                        safeStartTone(ToneGenerator.TONE_CDMA_PIP, 100)
                        Thread.sleep(150)
                        if (isReleased) return@Thread
                        safeStartTone(ToneGenerator.TONE_PROP_BEEP, 100)
                        Thread.sleep(150)
                        if (isReleased) return@Thread
                        safeStartTone(ToneGenerator.TONE_CDMA_PIP, 200)
                    }
                    else -> {
                        // General cute sound trigger
                        safeStartTone(ToneGenerator.TONE_PROP_BEEP, 150)
                    }
                }
            } catch (e: Exception) {
                Log.e("CallSoundManager", "Synth sound error: ${e.message}")
            }
        }.start()
    }

    private fun getRealisticOnomatopoeia(soundName: String): Triple<String, Float, Float> {
        val nameTrimmed = soundName.trim()
        return when (nameTrimmed) {
            "قطة" -> Triple("مياو! مياو مياو! ميوووووو!", 1.4f, 1.0f)
            "كلب" -> Triple("هوف هوف! هوف هوف! واف واف واف!", 1.0f, 1.1f)
            "أسد" -> Triple("غرااااووور! غرااااووور! زئير الأسد القوي!", 0.55f, 0.85f)
            "نمر" -> Triple("غرررررررررر! غرررررررررر!", 0.6f, 0.9f)
            "فيل" -> Triple("طوووووووووت! طوووووووووت!", 1.3f, 1.1f)
            "حصان" -> Triple("صهييييييل! صهييييييل!", 1.2f, 0.95f)
            "بقرة" -> Triple("مُووووووووو! مُووووووووو!", 0.65f, 0.8f)
            "خروف" -> Triple("مباااااااع! مباااااااع! مااااع!", 1.25f, 1.0f)
            "عصفور" -> Triple("زقزق زقزق! صو صو صو صو صو صو!", 1.6f, 1.25f)
            "بطة" -> Triple("واك واك! واك واك! واك واك واك!", 1.15f, 1.1f)
            "قرد" -> Triple("أو أو آ آ! أو أو آ آ! أو أو آ آ!", 1.5f, 1.3f)
            "ذئب" -> Triple("أووووووووووووووو! عواء الذئب القوي!", 0.75f, 0.8f)
            "ديك" -> Triple("كوكو كوكو كوكووووووووو!", 1.35f, 0.95f)
            "ضفدع" -> Triple("كواك كواك! ريبيت ريبيت! كواك كواك!", 0.7f, 0.8f)
            "بطريق" -> Triple("ببببب ببببب ببببب!", 1.30f, 1.1f)
            "نحلة" -> Triple("بزززززززززززززززززززززززززززز!", 1.2f, 1.4f)
            "بومة" -> Triple("هو هو.. هو هو.. هو هو!", 0.75f, 0.75f)
            "كنغر" -> Triple("بويينج بويينج بويينج!", 1.2f, 1.1f)
            "حوت" -> Triple("أووووووووووووممم... أووووووووووووممم...", 0.5f, 0.6f)
            "أرنب" -> Triple("هم نم هم نم هم نم! قضم الجزر السريع!", 1.3f, 1.15f)
            "ماعز" -> Triple("مبااااااع! مبااااااع! مااااااع!", 1.25f, 1.0f)
            "دب" -> Triple("غرووووور! غرووووور! زئير الدب الكبير!", 0.55f, 0.85f)
            "باندا" -> Triple("هم نم هم نم! الباندا الكيوت السعيد!", 1.3f, 1.1f)
            "ثعلب" -> Triple("ياو ياو ياو ياو!", 1.25f, 1.1f)
            "كوالا" -> Triple("همممممم.. همممممم..", 0.9f, 0.85f)
            "فأر" -> Triple("سويك سويك سويك سويك سويك!", 1.65f, 1.3f)
            "كتكوت" -> Triple("صوصو صوصو صوصو صوصو صوصو!", 1.6f, 1.15f)
            "زرافة" -> Triple("هممممممممم رافعة الرأس!", 0.95f, 0.85f)
            "حمار وحشي" -> Triple("نهيق الحمار الوحشي المخطط اللطيف!", 1.15f, 0.95f)
            "جمل" -> Triple("غرررررررررررررررراء! رغاء الجمل الصبور!", 0.6f, 0.8f)
            "قنفذ" -> Triple("فففف فففف شش شش!", 1.3f, 1.1f)
            "سنجاب" -> Triple("تشيب تشيب تشيب تشيب تشيب!", 1.55f, 1.2f)
            "فراشة" -> Triple("رف رف رف! رفرفة الفراشة الجميلة!", 1.4f, 1.0f)
            "سرطان البحر" -> Triple("كليك كليك كليك كليك!", 1.35f, 1.2f)
            "فهد" -> Triple("غررررررررر! الفهد السريع!", 0.65f, 0.9f)
            "غزال" -> Triple("ميووو ميووو الرائع!", 1.25f, 1.0f)
            "أخطبوط" -> Triple("بلوب بلوب بلوب بلوب بلوب بلوب!", 1.0f, 1.1f)
            "حلزون" -> Triple("يسير ببطء همممممم الصبور!", 1.2f, 0.75f)
            "خفاش" -> Triple("سيك سيك سيك سيك سيك!", 1.7f, 1.35f)
            "خرتيت" -> Triple("غرااااااااووووور القوي!", 0.6f, 0.85f)
            "سيد قشطة" -> Triple("هوف هوف هوووووف فرس النهر!", 0.6f, 0.85f)
            "حمار" -> Triple("نهييييييق نهييييييق! إي آآآو إي آآآو!", 1.2f, 0.8f)
            "فلامنجو" -> Triple("هونك هونك هونك الفلامنجو الوردي!", 1.2f, 1.0f)
            "ببغات", "ببغاء" -> Triple("كوكو كوكو! ألو ألو! الببغاء الذكي!", 1.45f, 1.2f)
            "نسر" -> Triple("كياااااااااااااار! صيحة النسر القوي!", 1.35f, 0.95f)
            "دلفين" -> Triple("كليك كليك كليك كليك! صفير الدلفين!", 1.5f, 1.25f)
            "غراب" -> Triple("قاع! قاع! قاع! غراب ذكي!", 0.75f, 0.8f)
            "تمساح" -> Triple("تشومب تشومب تشومب! التمساح الضخم!", 0.65f, 0.85f)
            "ثعبان" -> Triple("سسسسسسسسسسسسسسسسسسسسسسسس!", 1.0f, 0.75f)
            "ديك رومي" -> Triple("جلجل جلجل جلجل! قبقبة الديك الرومي القوي!", 1.1f, 1.2f)
            
            // Vehicles
            "سيارة رياضية" -> Triple("بررررررررررررررررّم! وووووششششش!", 1.0f, 1.2f)
            "سيارة شرطة" -> Triple("ويووو ويووو ويووو ويووو! دورية شرطة الأطفال!", 1.3f, 1.1f)
            "سيارة إسعاف" -> Triple("وييوو وييوو وييوو! إفساح الطريق للإسعاف!", 1.25f, 1.1f)
            "شاحنة كبيرة" -> Triple("بوم بوم بوم بوم! طووووط طووووط!", 0.7f, 0.9f)
            "دراجة نارية" -> Triple("برررررررررر رم رم رم!", 0.85f, 1.0f)
            "قطار" -> Triple("تشو تشو! توووت توووت! القطار السريع!", 1.1f, 1.0f)
            "طائرة" -> Triple("ووووووووووووووووووووش!", 0.75f, 0.9f)
            "هليكوبتر" -> Triple("طقطقطقطقطقطقطقطقطقطقطق!", 1.0f, 1.3f)
            "سفينة" -> Triple("طوووووووووووووووووووووط بوق السفينة!", 0.5f, 0.7f)
            "صاروخ فضائي" -> Triple("شووووووووووووووف! خمسة، أربعة، ثلاثة، اثنان، واحد.. انطلاق!", 0.7f, 0.9f)
            
            // Tools / Science / Nature
            "جرس الباب" -> Triple("دنغ دونغ! دنغ دونغ!", 1.4f, 1.1f)
            "مكنسة كهربائية" -> Triple("فففففففففففففففففففففف تفريغ الهواء!", 1.1f, 1.1f)
            "خلاط كهربائي" -> Triple("جججججججججججججج خلط العصير اللذيذ!", 1.1f, 1.2f)
            "كاميرا تصوير" -> Triple("كليك كليك! التقط صورة للابتسامة الجميلة!", 1.3f, 1.1f)
            "ماكينة حلاقة" -> Triple("ززززززززززززززز تسريحة شعر البطل!", 1.0f, 1.1f)
            "صعق كهربائي" -> Triple("زززت زززت شرارة كهربائية!", 1.2f, 1.2f)
            "آلة موسيقية" -> Triple("دو ري مي فا سو لا سي... عزف نغمات رنانة!", 1.2f, 1.0f)
            "صوت روبوت" -> Triple("بييب بووب بييب بووب! تم تفعيل المساعد الآلي!", 1.25f, 1.2f)
            "صوت المطر" -> Triple("بش بش بش بش بش.. تساقط قطرات المطر العذبة!", 1.2f, 1.0f)
            "صوت الرياح" -> Triple("هوووو هوووو هوووو.. هبوب نسيم الرياح الهادئ!", 0.8f, 0.85f)
            "صوت الرعد" -> Triple("كابووووووووم! صوت الرعد البعيد القوي!", 0.55f, 0.8f)
            "صوت النهر" -> Triple("خرخرخرخرخر.. خرير مياه النهر النقية!", 1.1f, 0.9f)
            "صوت أمواج البحر" -> Triple("شوووووووش.. طاخ... تلاطم أمواج البحر العالية!", 0.85f, 0.85f)
            
            // Funny / Heroes
            "ضربة كرتونية" -> Triple("بام! طاخ! بوم! ضربة كرتونية مضحكة!", 1.2f, 1.1f)
            "بوابة سحرية" -> Triple("شووووف! زززز! تفعيل البوابة السحرية الخارقة!", 1.3f, 1.15f)
            "روبوت بطل" -> Triple("بييب بووب! أنا روبوت البطل المساعد للحراسة والإنقاذ!", 1.25f, 1.2f)
            "طيران خارق" -> Triple("وووووووووش! طيران خارق وسريع فوق السحاب!", 1.1f, 1.1f)
            "درع طاقة" -> Triple("ووومممممممم! تنشيط درع الدفاع السري لحماية الأبرياء!", 1.0f, 1.0f)
            "شعاع ليزر" -> Triple("بيو بيو بيو! إطلاق أشعة الليزر الحمراء اللطيفة!", 1.4f, 1.3f)
            "سرعة البرق" -> Triple("فلااااااش! ووشش البطل السريع جداً كالبرق!", 1.2f, 1.2f)
            "إطلاق طاقة" -> Triple("بيووووووو! طاخ! تصادم الطاقة الكرتونية السحرية!", 1.3f, 1.15f)
            "قفزة قوية" -> Triple("بويينغ بويينغ! قفزة البطل الخارق الشجاع!", 1.3f, 1.1f)
            
            "ضحكة كرتونية" -> Triple("ها ها ها ها ها! هيه هيه هيه! ضحكة كوميدية تسعد الأبطال!", 1.3f, 1.1f)
            "زحلقة كوميدية" -> Triple("ووييييييييييب.. بوب! زحلقة طريفة من الرسوم المتحركة!", 1.3f, 1.15f)
            "قفزة يمبروك" -> Triple("بويييييينج! قفزة مبهجة ورائعة!", 1.35f, 1.1f)
            "صوت كائن فضائي" -> Triple("زيب زوب زيب زوب! فضائي أليف من كوكب الألعاب السعيدة!", 1.4f, 1.25f)
            "بالونة تنفجر" -> Triple("بوب! بوب! انفجار بالونة الحفلة الجميلة الملونة!", 1.3f, 1.1f)
            
            // New Animals
            "وحيد القرن السحري" -> Triple("نيهاااااع! صهيل وحيد القرن السحري الجميل المحب لقوس قزح!", 1.3f, 1.1f)
            "تنين طائر" -> Triple("غرااااووور! زئير التنين الطائر اللطيف صديق المغامرين في الغيوم!", 0.6f, 0.85f)
            "ديناصور عملاق" -> Triple("رووووواااار! ديناصور تي ريكس العملاق حامي الغابات القديمة!", 0.5f, 0.8f)
            "هريرة صغيرة" -> Triple("مياو مياو مياو! هريرة صغيرة لطيفة جداً تبحث عن الحليب واللعب!", 1.5f, 1.15f)
            "جرو صغير" -> Triple("هوف هوف هوف! جرو صغير مبهج ومحب للركض وإحضار الكرة!", 1.2f, 1.2f)
            "سلحفاة حكيمة" -> Triple("همممممم.. السلحفاة الهادئة البطيئة تسير على العشب وتسرد قصصاً حكيمة!", 1.0f, 0.8f)
            "هامستر كيوت" -> Triple("سويك سويك سويك! هامستر صغير يدور في العجلة بنشاط!", 1.6f, 1.3f)

            // New Vehicles
            "جرار زراعي" -> Triple("تشوك تشوك تشوك تشوك! جرار المزرعة القوي والبطيء النشيط في الحقول!", 0.8f, 0.9f)
            "حافلة مدرسية" -> Triple("طوووط طوووط! أهلاً بكم في حافلة المدرسة السعيدة صباحاً!", 1.1f, 1.0f)
            "غواصة مائية" -> Triple("بلوب بلوب بلوب بلوب بلوب بلوب! الغواصة الكرتونية تحت أعماق البحار والمحيطات المظلمة!", 0.9f, 1.1f)
            "سيارة كرتونية" -> Triple("بييب بييب! سيارتي الصغيرة الجميلة تسير في شارع الألعاب السعيدة!", 1.3f, 1.2f)
            "حوامة سريعة" -> Triple("ووووووووشششش! القارب السريع يشق مياه البحيرة الهادئة بحماس!", 1.0f, 1.15f)
            "منطاد طائر" -> Triple("ففففففففففف! صعود المنطاد الملون في السماء فوق الجبال الشاهقة!", 1.2f, 1.0f)

            // New Heroes
            "بطل الجليد" -> Triple("فراااااااش! رذاذ الجليد السحري البارد لتجميد الأشرار المشاغبين!", 1.35f, 1.15f)
            "بطل النار" -> Triple("ووووووشششش! قوة شعلة النار الدافئة الصديقة لإنارة الممرات المظلمة ومساعدة الأصدقاء!", 1.1f, 1.0f)
            "بطل الرياح" -> Triple("هوووو هوووو هوووو! إعصار الرياح اللطيف الملون لحمل ألعاب الأصدقاء إلى بر الأمان!", 0.9f, 1.1f)
            "قبضة حديدية" -> Triple("طاخ بوم بام! تفعيل القوة الفولاذية للقبضة الحديدية لحماية المدينة!", 1.0f, 1.0f)
            "تخاطر ذهني" -> Triple("زززززززززت! رنين التخاطر الذهني البطل لقراءة الأفكار وتوجيه الأصدقاء!", 1.4f, 1.2f)

            // New Tools
            "منبه الطاولة" -> Triple("تيك تاك تيك تاك! رن رن رن! حان وقت الاستيقاظ بنشاط وبدء يوم جميل!", 1.35f, 1.15f)
            "غلاية الماء" -> Triple("ففففففففففففففففف! غلاية الشاي اللذيذ تصفر بسعادة على الموقد!", 1.25f, 1.2f)
            "مطركة الخشب" -> Triple("طق طق طق! نقر مطرقة البناء الخشبية اللطيفة لتركيب الألعاب الخشبية الجميلة!", 0.95f, 1.0f)
            "مقص الأوراق" -> Triple("شيب شيب شيب! قص أوراق الرسم الملونة الجميلة وصنع أشكال مذهلة!", 1.4f, 1.25f)
            "مجفف الشعر" -> Triple("وووووووووووووووووش! مجفف الشعر الدافئ المريح بعد الاستحمام بالماء العذب!", 1.0f, 1.1f)

            // New Nature
            "تغريد العصافير" -> Triple("تويت تويت تويت تويت! زقزقة أروع عصافير الصباح المغردة في الغابات الخضراء!", 1.55f, 1.25f)
            "حفيف أوراق الشجر" -> Triple("شششششش شششششش! حفيف أوراق الشجر الأخضر اللطيف والمنعش مع نسيم الهواء الصافي!", 1.1f, 0.9f)
            "طقطقة النار" -> Triple("طق طق... كراك... طقطقة نار حطب مخيم الكشافة الدافئ والممتع تحت النجوم اللامعة!", 1.2f, 1.0f)
            "صوت شلال المياه" -> Triple("رشششششش رشششششش! تدفق مياه الشلال العذبة المنهمرة من أعلى الجبال الشاهقة!", 0.9f, 0.95f)
            "صوت غابة استوائية" -> Triple("أو أو آ آ! تويت تويت! أصوات مغامرة الغابة السعيدة والقرود المرحة والطيور الملونة!", 1.2f, 1.1f)

            // New Funny
            "العطس الكوميدي" -> Triple("أتشووووووووو! الحمد لله! عطسة كرتونية مضحكة جداً تفجر الضحكات في البيت!", 1.3f, 1.1f)
            "مضغ فقع الفقاعة" -> Triple("شومب شومب.. بوب! فقع فقاعة علكة الفواكه السحرية الملونة والمبهجة!", 1.25f, 1.2f)
            "شخير مضحك" -> Triple("خخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخخ بيييه! شخير كوميدي عميق ومسلي للأطفال أثناء النوم!", 0.8f, 0.85f)
            "بوق المهرج" -> Triple("طوط طوط! بوق المهرج الضاحك السعيد لإسعاد الجميع ورسم الابتسامة الملونة!", 1.45f, 1.3f)
            "تثاؤب كسول" -> Triple("هواااااااااه! تثاؤب البطل الصغير المستعد للنوم الهادئ والمريح بعد يوم طويل من اللعب!", 1.0f, 0.9f)
            "ضحكة الساحرة الشريرة" -> Triple("كيه هيه هيه هيه! ضحكة الساحرة الكرتونية الطريفة واللطيفة في قصص الخيال القديمة!", 1.25f, 1.15f)

            // New Sirens
            "إنذار الغواصة" -> Triple("أوووووكا أوووووكا أوووووكا! صفارة غوص الغواصة تحت أعماق البحار والمحيطات المظلمة!", 0.8f, 1.0f)
            "بوق القطار القديم" -> Triple("توووت توووت! بوق قطار البخار القديم الرائع في المحطة الكبيرة!", 1.0f, 0.95f)
            "إنذار سرقة السيارة" -> Triple("ويوو ويوو! بييب بييب بييب! إنذار السيارة الذكي لحمايتها من اللصوص والمشاغبين!", 1.25f, 1.1f)
            "بوق الشاحنة الرياضية" -> Triple("ببببببببببببببببب! بوق شاحنة نقل الألعاب الضخمة جداً في الطرق الطويلة السريعة!", 0.75f, 0.85f)
            "جرس المدرسة القديم" -> Triple("رن رن رن رن رن رن! جرس المدرسة لبدء الفسحة السعيدة واللعب مع الأصدقاء الطيبين!", 1.4f, 1.2f)
            "إنذار الفضاء المثير" -> Triple("بييب بييب بييب! إنذار سفينة الفضاء تقترب من كوكب الألعاب السعيدة والنجوم اللامعة!", 1.3f, 1.15f)

            else -> Triple(soundName, 1.1f, 1.0f)
        }
    }

    val realSoundUrls = mapOf(
        // Animals - Cats, Tigers, Leopards, Lions etc. (Big Cat roar)
        "قطة" to "https://upload.wikimedia.org/wikipedia/commons/b/b5/Cat_Meow_2.ogg",
        "أسد" to "https://upload.wikimedia.org/wikipedia/commons/e/e0/Lion_roar.ogg",
        "نمر" to "https://upload.wikimedia.org/wikipedia/commons/e/e0/Lion_roar.ogg",
        "فهد" to "https://upload.wikimedia.org/wikipedia/commons/e/e0/Lion_roar.ogg",
        "دب" to "https://upload.wikimedia.org/wikipedia/commons/e/e0/Lion_roar.ogg",
        "باندا" to "https://www.soundjay.com/beast/sounds/sheep-bleat-01.mp3",
        "خرتيت" to "https://upload.wikimedia.org/wikipedia/commons/e/e0/Lion_roar.ogg",
        "سيد قشطة" to "https://upload.wikimedia.org/wikipedia/commons/e/e0/Lion_roar.ogg",
        "تمساح" to "https://upload.wikimedia.org/wikipedia/commons/e/e0/Lion_roar.ogg",

        // Animals - Dogs, Wolves, Foxes etc. (Bark / Howl)
        "كلب" to "https://upload.wikimedia.org/wikipedia/commons/2/25/Dog_barking.ogg",
        "ذئب" to "https://upload.wikimedia.org/wikipedia/commons/0/07/Howl.ogg",
        "ثعلب" to "https://upload.wikimedia.org/wikipedia/commons/2/25/Dog_barking.ogg",
        "قرد" to "https://www.soundjay.com/beast/sounds/bird-chirp-01.mp3",
        "كوالا" to "https://www.soundjay.com/beast/sounds/sheep-bleat-01.mp3",
        "فأر" to "https://www.soundjay.com/beast/sounds/bird-chirp-01.mp3",
        "خفاش" to "https://www.soundjay.com/beast/sounds/bird-chirp-01.mp3",

        // Animals - Birds
        "عصفور" to "https://www.soundjay.com/beast/sounds/bird-chirp-01.mp3",
        "ببغات" to "https://www.soundjay.com/beast/sounds/bird-chirp-01.mp3",
        "ببغاء" to "https://www.soundjay.com/beast/sounds/bird-chirp-01.mp3",
        "نسر" to "https://www.soundjay.com/beast/sounds/bird-chirp-01.mp3",
        "فلامنجو" to "https://www.soundjay.com/beast/sounds/bird-chirp-01.mp3",
        "غراب" to "https://www.soundjay.com/beast/sounds/bird-chirp-01.mp3",
        "بطريق" to "https://www.soundjay.com/beast/sounds/duck-quack-01.mp3",
        "بومة" to "https://www.soundjay.com/beast/sounds/bird-chirp-01.mp3",
        "كتكوت" to "https://www.soundjay.com/beast/sounds/bird-chirp-01.mp3",

        // Animals - Duck
        "بطة" to "https://www.soundjay.com/beast/sounds/duck-quack-01.mp3",

        // Animals - Cows & Large herbivores
        "بقرة" to "https://www.soundjay.com/beast/sounds/cow-moo-01.mp3",
        "جمل" to "https://www.soundjay.com/beast/sounds/cow-moo-01.mp3",
        "زرافة" to "https://www.soundjay.com/nature/sounds/ocean-wave-1.mp3",
        "فيل" to "https://upload.wikimedia.org/wikipedia/commons/b/be/African_Elephant_Warning_Trumpet.ogg",

        // Animals - Sheep & Lambs
        "خروف" to "https://www.soundjay.com/beast/sounds/sheep-bleat-01.mp3",
        "شاة" to "https://www.soundjay.com/beast/sounds/sheep-bleat-01.mp3",
        "ماعز" to "https://www.soundjay.com/beast/sounds/sheep-bleat-01.mp3",
        "غزال" to "https://www.soundjay.com/beast/sounds/sheep-bleat-01.mp3",
        "أرنب" to "https://www.soundjay.com/cartoon/sounds/slide-whistle-up-01.mp3",
        "سنجاب" to "https://www.soundjay.com/beast/sounds/bird-chirp-01.mp3",
        "قنفذ" to "https://www.soundjay.com/cartoon/sounds/slide-whistle-up-01.mp3",
        "حلزون" to "https://www.soundjay.com/buttons/sounds/bell-ringing-04.mp3",
        "سرطان البحر" to "https://www.soundjay.com/nature/sounds/ocean-wave-1.mp3",
        "كنغر" to "https://www.soundjay.com/cartoon/sounds/slide-whistle-up-01.mp3",

        // Animals - Rooster
        "ديك" to "https://www.soundjay.com/beast/sounds/rooster-crowing-01.mp3",
        "دجاجة" to "https://upload.wikimedia.org/wikipedia/commons/2/29/Gallus_gallus_domesticus_2.ogg",
        "خنزير" to "https://upload.wikimedia.org/wikipedia/commons/3/30/Sus_scrofa_domesticus.ogg",
        "ديك رومي" to "https://www.soundjay.com/beast/sounds/rooster-crowing-01.mp3",

        // Animals - Frog & Reptiles
        "ضفدع" to "https://www.soundjay.com/beast/sounds/frog-croaking-01.mp3",
        "أخطبوط" to "https://www.soundjay.com/nature/sounds/ocean-wave-1.mp3",
        "ثعبان" to "https://www.soundjay.com/nature/sounds/cricket-chirping-01.mp3",

        // Animals - Equine (Horse/Zebra)
        "حصان" to "https://www.w3schools.com/html/horse.mp3",
        "حمار وحشي" to "https://www.soundjay.com/beast/sounds/donkey-bray-01.mp3",
        "حمار" to "https://www.soundjay.com/beast/sounds/donkey-bray-01.mp3",

        // Animals - Insects
        "نحلة" to "https://www.soundjay.com/nature/sounds/cricket-chirping-01.mp3",
        "فراشة" to "https://www.soundjay.com/nature/sounds/cricket-chirping-01.mp3",
        "قراد" to "https://www.soundjay.com/nature/sounds/cricket-chirping-01.mp3",
        "صرصار الليل" to "https://www.soundjay.com/nature/sounds/cricket-chirping-01.mp3",

        // Animals - Sea
        "حوت" to "https://www.soundjay.com/nature/sounds/ocean-wave-1.mp3",
        "دلفين" to "https://www.soundjay.com/nature/sounds/ocean-wave-1.mp3",

        // Vehicles
        "سيارة" to "https://www.soundjay.com/transportation/sounds/car-horn-1.mp3",
        "سيارة إطفاء" to "https://www.soundjay.com/transportation/sounds/police-siren-1.mp3",
        "دراجة هوائية" to "https://www.soundjay.com/buttons/sounds/bell-ringing-04.mp3",
        "سيارة شرطة" to "https://www.soundjay.com/transportation/sounds/police-siren-1.mp3",
        "سيارة إسعاف" to "https://www.soundjay.com/transportation/sounds/police-siren-1.mp3",
        "سيارة رياضية" to "https://www.soundjay.com/transportation/sounds/car-horn-1.mp3",
        "دراجة نارية" to "https://www.soundjay.com/transportation/sounds/car-horn-1.mp3",
        "صاروخ فضائي" to "https://www.soundjay.com/transportation/sounds/rocket-launch-01.mp3",
        "قطار" to "https://www.soundjay.com/transportation/sounds/train-horn-01.mp3",
        "هليكوبتر" to "https://www.soundjay.com/transportation/sounds/helicopter-flyby-1.mp3",
        "شاحنة كبيرة" to "https://www.soundjay.com/transportation/sounds/truck-horn-1.mp3",
        "سفينة" to "https://www.soundjay.com/transportation/sounds/truck-horn-1.mp3",
        "طائرة" to "https://www.soundjay.com/transportation/sounds/airplane-takeoff-01.mp3",

        // Sirens Section Fallbacks
        "صفارة إنذار الشرطة" to "https://www.soundjay.com/transportation/sounds/police-siren-1.mp3",
        "صفارة إنذار الإسعاف" to "https://www.soundjay.com/transportation/sounds/police-siren-1.mp3",
        "صفارة إنذار الإطفاء" to "https://www.soundjay.com/transportation/sounds/police-siren-1.mp3",
        "إنذار غارات جوية" to "https://www.soundjay.com/transportation/sounds/police-siren-1.mp3",
        "إنذار نووي خطير" to "https://www.soundjay.com/transportation/sounds/police-siren-1.mp3",
        "بوق سفينة عملاقة" to "https://www.soundjay.com/transportation/sounds/truck-horn-1.mp3",
        "إنذار الحريق المنزلي" to "https://www.soundjay.com/buttons/sounds/bell-ringing-04.mp3",
        "إنذار الإخلاء السريع" to "https://www.soundjay.com/transportation/sounds/police-siren-1.mp3",

        // Tools
        "جرس الباب" to "https://www.soundjay.com/buttons/sounds/doorbell-1.mp3",
        "مكنسة كهربائية" to "https://www.soundjay.com/household/sounds/vacuum-cleaner-1.mp3",
        "خلاط كهربائي" to "https://www.soundjay.com/household/sounds/vacuum-cleaner-1.mp3",
        "ماكينة حلاقة" to "https://www.soundjay.com/household/sounds/vacuum-cleaner-1.mp3",
        "صعق كهربائي" to "https://www.soundjay.com/household/sounds/vacuum-cleaner-1.mp3",
        "كاميرا تصوير" to "https://www.soundjay.com/mechanical/sounds/camera-shutter-click-01.mp3",
        "آلة موسيقية" to "https://www.soundjay.com/buttons/sounds/bell-ringing-04.mp3",
        "صوت روبوت" to "https://www.soundjay.com/buttons/sounds/button-10.mp3",

        // Nature
        "صوت الرعد" to "https://www.soundjay.com/nature/sounds/thunder-1.mp3",
        "صوت المطر" to "https://www.soundjay.com/nature/sounds/rain-02.mp3",
        "صوت النهر" to "https://www.soundjay.com/nature/sounds/rain-02.mp3",
        "صوت أمواج البحر" to "https://www.soundjay.com/nature/sounds/ocean-wave-1.mp3",
        "صوت الرياح" to "https://www.soundjay.com/nature/sounds/ocean-wave-1.mp3",

        // Funny & Heroes (and general)
        "ضربة كرتونية" to "https://www.soundjay.com/cartoon/sounds/cartoon-punch-01.mp3",
        "إطلاق طاقة" to "https://www.soundjay.com/cartoon/sounds/cartoon-punch-01.mp3",
        "شعاع ليزر" to "https://www.soundjay.com/cartoon/sounds/cartoon-punch-01.mp3",
        "درع طاقة" to "https://www.soundjay.com/cartoon/sounds/cartoon-punch-01.mp3",
        "ضحكة كرتونية" to "https://www.soundjay.com/cartoon/sounds/cartoon-laugh-01.mp3",
        "روبوت بطل" to "https://www.soundjay.com/cartoon/sounds/cartoon-laugh-01.mp3",
        "صوت كائن فضائي" to "https://www.soundjay.com/cartoon/sounds/cartoon-laugh-01.mp3",
        "زحلقة كوميدية" to "https://www.soundjay.com/cartoon/sounds/slide-whistle-up-01.mp3",
        "قفزة قوية" to "https://www.soundjay.com/cartoon/sounds/slide-whistle-up-01.mp3",
        "سرعة البرق" to "https://www.soundjay.com/cartoon/sounds/slide-whistle-up-01.mp3",
        "بوابة سحرية" to "https://www.soundjay.com/cartoon/sounds/slide-whistle-up-01.mp3",
        "طيران خارق" to "https://www.soundjay.com/cartoon/sounds/slide-whistle-up-01.mp3",
        "قفزة يمبروك" to "https://www.soundjay.com/cartoon/sounds/slide-whistle-up-01.mp3",
        "بالونة تنفجر" to "https://www.soundjay.com/cartoon/sounds/balloon-pop-01.mp3"
    )


    val localAssetMap = mapOf(
        // Animals
        "بقرة" to "sounds/animals/bull.mp3",
        "خروف" to "sounds/animals/sheep.wav",
        "ماعز" to "sounds/animals/goat.mp3",
        "ديك" to "sounds/animals/rooster.mp3",
        "حصان" to "sounds/animals/horse.wav",
        "حمار" to "sounds/animals/donkey.mp3",
        "حمار وحشي" to "sounds/animals/donkey.mp3",
        "قطة" to "sounds/animals/cat.mp3",
        "كلب" to "sounds/animals/dog.wav",
        "أسد" to "sounds/animals/lion.mp3",
        "نمر" to "sounds/animals/tiger.wav",
        "فيل" to "sounds/animals/elephant.mp3",
        "قرد" to "sounds/animals/monkey.mp3",
        "دب" to "sounds/animals/bear.wav",
        "ثعلب" to "sounds/animals/fox.mp3",
        "غراب" to "sounds/animals/crow.aiff",
        "بطة" to "sounds/animals/duck.wav",
        "تمساح" to "sounds/animals/alligator.mp3",
        "خفاش" to "sounds/animals/bat.mp3",
        "جمل" to "sounds/animals/camel.mp3",
        "ضفدع" to "sounds/animals/frog.mp3",
        "نحلة" to "sounds/animals/insect.mp3",
        "ديك رومي" to "sounds/animals/turkey.mp3",
        "ثعبان" to "sounds/animals/snake.wav",
        
        // Dynamic additions mapped to existing local files
        "هريرة صغيرة" to "sounds/animals/cat.mp3",
        "جرو صغير" to "sounds/animals/dog.wav",
        "وحيد القرن السحري" to "sounds/animals/horse.wav",
        "ديناصور عملاق" to "sounds/animals/tiger.wav",
        "تنين طائر" to "sounds/animals/bear.wav",
        
        // Extra category fallbacks to real sounds
        "جرار زراعي" to "sounds/vehicles/forklift.mp3",
        "حافلة مدرسية" to "sounds/vehicles/car.mp3",
        "غواصة مائية" to "sounds/sirens/submarine_siren.ogg",
        "حوامة سريعة" to "sounds/vehicles/helicopter.mp3",
        "منطاد طائر" to "sounds/vehicles/rocket.mp3",
        "طقطقة النار" to "sounds/downloaded/حفيف أوراق الشجر.ogg",
        "صوت شلال المياه" to "sounds/downloaded/صوت غابة استوائية.ogg",
        
        // Vehicles
        "سيارة" to "sounds/vehicles/car.mp3",
        "سيارة رياضية" to "sounds/vehicles/car.mp3",
        "دراجة نارية" to "sounds/vehicles/motorcycle.mp3",
        "سيارة إسعاف" to "sounds/vehicles/ambulance.mp3",
        "سيارة شرطة" to "sounds/vehicles/police.mp3",
        "قطار" to "sounds/vehicles/train.mp3",
        "صاروخ فضائي" to "sounds/vehicles/rocket.mp3",
        "طائرة" to "sounds/vehicles/helicopter.mp3", // fallback
        "هليكوبتر" to "sounds/vehicles/helicopter.mp3",
        "شاحنة كبيرة" to "sounds/vehicles/forklift.mp3",

        // Sirens Section Local Assets Fallbacks
        "صفارة إنذار الشرطة" to "sounds/vehicles/police.mp3",
        "صفارة إنذار الإسعاف" to "sounds/vehicles/ambulance.mp3",
        "صفارة إنذار الإطفاء" to "sounds/vehicles/police.mp3",
        "إنذار غارات جوية" to "sounds/vehicles/police.mp3",
        "إنذار نووي خطير" to "sounds/vehicles/police.mp3",
        "بوق سفينة عملاقة" to "sounds/vehicles/forklift.mp3",
        "إنذار الحريق المنزلي" to "sounds/vehicles/police.mp3",
        "إنذار الإخلاء السريع" to "sounds/vehicles/police.mp3"
    )

    fun playAudioFromAsset(assetPath: String, onStart: () -> Unit = {}, onComplete: () -> Unit = {}, onError: () -> Unit = {}) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            synchronized(this) {
                if (isReleased) return@post
                
                try {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                } catch (e: Exception) {
                    Log.e("CallSoundManager", "mediaPlayer reset error: ${e.message}")
                }

                try {
                    val mp = MediaPlayer()
                    mediaPlayer = mp
                    
                    var afd: AssetFileDescriptor? = null
                    try {
                        afd = context.assets.openFd(assetPath)
                    } catch (e: Exception) {
                        Log.d("CallSoundManager", "Direct FD access failed, falling back to temp file: $assetPath")
                    }

                    if (afd != null) {
                        mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
                    } else {
                        val suffix = if (assetPath.contains(".")) "." + assetPath.substringAfterLast('.') else ".mp3"
                        val tempFile = java.io.File.createTempFile("sound_", "_temp$suffix", context.cacheDir)
                        tempFile.deleteOnExit()
                        context.assets.open(assetPath).use { input ->
                            tempFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        mp.setDataSource(tempFile.absolutePath)
                    }

                    mp.apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                        )
                        
                        setOnPreparedListener { 
                            it.start()
                            onStart()
                        }
                        
                        setOnCompletionListener { completedMp ->
                            synchronized(this@CallSoundManager) {
                                if (mediaPlayer == completedMp) {
                                    onComplete()
                                }
                            }
                        }
                        
                        setOnErrorListener { errorMp, what, extra ->
                            Log.e("CallSoundManager", "MediaPlayer error: $assetPath (what=$what, extra=$extra)")
                            synchronized(this@CallSoundManager) {
                                if (mediaPlayer == errorMp) {
                                    onError()
                                }
                            }
                            true
                        }
                        
                        prepareAsync()
                    }
                } catch (e: Exception) {
                    Log.e("CallSoundManager", "Error playing local asset: $assetPath", e)
                    onError()
                }
            }
        }
    }

    fun playAudioFromUrl(url: String, onStart: () -> Unit = {}, onComplete: () -> Unit = {}, onError: () -> Unit = {}) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            synchronized(this) {
                if (isReleased) return@post
                try {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                } catch (e: Exception) {
                    Log.e("CallSoundManager", "mediaPlayer reset error: ${e.message}")
                }

                try {
                    val mp = MediaPlayer()
                    mediaPlayer = mp
                    mp.apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                        )
                        setDataSource(url)
                        setOnPreparedListener { preparedMp ->
                            synchronized(this@CallSoundManager) {
                                if (!isReleased && mediaPlayer == preparedMp) {
                                    try {
                                        preparedMp.start()
                                        onStart()
                                    } catch (ex: Exception) {
                                        Log.e("CallSoundManager", "MediaPlayer start error: ${ex.message}")
                                        onError()
                                    }
                                } else {
                                    try {
                                        preparedMp.release()
                                    } catch (e: Exception) {}
                                }
                            }
                        }
                        setOnCompletionListener { completedMp ->
                            synchronized(this@CallSoundManager) {
                                if (mediaPlayer == completedMp) {
                                    onComplete()
                                }
                            }
                        }
                        setOnErrorListener { errorMp, what, extra ->
                            Log.e("CallSoundManager", "MediaPlayer error: what=$what, extra=$extra")
                            synchronized(this@CallSoundManager) {
                                if (mediaPlayer == errorMp) {
                                    onError()
                                }
                            }
                            true
                        }
                        prepareAsync()
                    }
                } catch (e: Exception) {
                    Log.e("CallSoundManager", "playAudioFromUrl error: ${e.message}")
                    onError()
                }
            }
        }
    }

    fun stopAudio() {
        synchronized(this) {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            } catch (e: Exception) {
                Log.e("CallSoundManager", "stopAudio error: ${e.message}")
            }
        }
    }


    fun playKidsRealisticSound(soundName: String, description: String) {
        if (isReleased) return
        stopRingtone()
        stopSpeaking()
        stopAudio()

        val trimmedName = soundName.trim()
        var assetPath = localAssetMap[trimmedName]

        // 1. Dynamic lookup in sirens
        val sirenBaseNames = mapOf(
            "صفارة إنذار الشرطة" to "police_siren",
            "صفارة إنذار الإسعاف" to "ambulance_siren",
            "صفارة إنذار الإطفاء" to "fire_truck_siren",
            "إنذار غارات جوية" to "air_raid_siren",
            "إنذار نووي خطير" to "nuclear_alarm",
            "بوق سفينة عملاقة" to "ship_horn",
            "إنذار الحريق المنزلي" to "smoke_alarm",
            "إنذار الإخلاء السريع" to "evacuation_alarm",
            "إنذار الغواصة" to "submarine_siren",
            "بوق القطار القديم" to "train_horn",
            "إنذار سرقة السيارة" to "car_alarm",
            "بوق الشاحنة الرياضية" to "truck_horn",
            "جرس المدرسة القديم" to "school_bell",
            "إنذار الفضاء المثير" to "space_alarm"
        )
        val sirenBase = sirenBaseNames[trimmedName]
        if (sirenBase != null) {
            try {
                val sirenFiles = context.assets.list("sounds/sirens") ?: emptyArray()
                val matchedFile = sirenFiles.firstOrNull { it.substringBefore('.') == sirenBase }
                if (matchedFile != null) {
                    assetPath = "sounds/sirens/$matchedFile"
                    Log.d("CallSoundManager", "Dynamically found siren asset: $assetPath")
                }
            } catch (e: Exception) {
                Log.e("CallSoundManager", "Error listing sirens asset: ${e.message}")
            }
        }

        // 2. Dynamic lookup in extra downloaded sounds
        if (assetPath == null) {
            try {
                val downloadedFiles = context.assets.list("sounds/downloaded") ?: emptyArray()
                val matchedFile = downloadedFiles.firstOrNull { it.substringBefore('.') == trimmedName }
                if (matchedFile != null) {
                    assetPath = "sounds/downloaded/$matchedFile"
                    Log.d("CallSoundManager", "Dynamically found downloaded asset: $assetPath")
                }
            } catch (e: Exception) {
                Log.e("CallSoundManager", "Error listing downloaded asset: ${e.message}")
            }
        }

        val onStartAction = {
            Log.d("CallSoundManager", "Started playing sound for: $trimmedName")
            Unit
        }
        val onCompleteAction = {
            if (!isReleased && isTtsInitialized) {
                tts?.setPitch(1.1f)
                tts?.setSpeechRate(0.85f)
                val comment = "سمعنا الآن صوت $soundName: $description"
                tts?.speak(comment, TextToSpeech.QUEUE_FLUSH, null, "KIDS_ANIMAL_COMMENTARY_ID")
            }
        }
        
        if (assetPath != null) {
            playAudioFromAsset(
                assetPath = assetPath,
                onStart = onStartAction,
                onComplete = onCompleteAction,
                onError = {
                    Log.e("CallSoundManager", "Missing file: $assetPath, attempting URL fallback")
                    Unit
                    playFallbackUrl(trimmedName, description, onStartAction, onCompleteAction)
                }
            )
        } else {
            playFallbackUrl(trimmedName, description, onStartAction, onCompleteAction)
        }
    }

    private fun playFallbackUrl(trimmedName: String, description: String, onStartAction: () -> Unit, onCompleteAction: () -> Unit) {
        val url = realSoundUrls[trimmedName]
        if (url != null) {
            playAudioFromUrl(
                url = url,
                onStart = onStartAction,
                onComplete = onCompleteAction,
                onError = {
                    Log.e("CallSoundManager", "Online playing failed for $trimmedName, starting local fallback")
                    Unit
                    playLocalFallbackSound(trimmedName, description)
                }
            )
        } else {
            playLocalFallbackSound(trimmedName, description)
        }
    }

    private fun playLocalFallbackSound(soundName: String, description: String) {
        val mappedSynthType = when (soundName) {
            "أسد", "نمر", "فهد" -> "lion"
            "كلب" -> "dog"
            "عصفور", "كتكوت", "ببغاء" -> "bird"
            "قطة" -> "cat"
            "جرس الباب" -> "bell"
            "سيارة شرطة" -> "siren"
            "سيارة رياضية" -> "car_or_sports"
            "مكنسة كهربائية" -> "vacuum"
            "ضربة كرتونية", "ضحكة كرتونية", "زحلقة كوميدية", "بالونة تنفجر" -> "funny"
            else -> "general"
        }
        
        // Removed AI synth sound playback to make it sound strictly realistic/natural
        // playSynthSound(mappedSynthType)
        
        Thread {
            try {
                val (imitation, pitch, rate) = getRealisticOnomatopoeia(soundName)
                
                if (!isReleased && isTtsInitialized) {
                    tts?.setPitch(pitch)
                    tts?.setSpeechRate(rate)
                    tts?.speak(imitation, TextToSpeech.QUEUE_FLUSH, null, "KIDS_ANIMAL_IMITATION_ID")
                }
                
                val delayDuration = when (soundName) {
                    "نحلة", "ثعبان" -> 2000L
                    "أسد", "نمر", "فهد", "ذئب", "حمار" -> 2200L
                    else -> 1500L
                }
                Thread.sleep(delayDuration)
                
                if (!isReleased && isTtsInitialized) {
                    tts?.setPitch(1.1f)
                    tts?.setSpeechRate(0.85f)
                    val comment = "$soundName: $description"
                    tts?.speak(comment, TextToSpeech.QUEUE_ADD, null, "KIDS_ANIMAL_COMMENTARY_ID")
                }
            } catch (e: Exception) {
                Log.e("CallSoundManager", "playLocalFallbackSound thread error: ${e.message}")
            }
        }.start()
    }

    fun release() {
        synchronized(this) {
            isReleased = true
            isRingtonePlaying = false
            try {
                tts?.shutdown()
            } catch (e: Exception) {
                Log.e("CallSoundManager", "TTS shutdown failed: ${e.message}")
            }
            tts = null
            try {
                toneGenerator?.release()
            } catch (e: Exception) {
                Log.e("CallSoundManager", "ToneGenerator release failed: ${e.message}")
            }
            toneGenerator = null
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
            } catch (e: Exception) {
                Log.e("CallSoundManager", "MediaPlayer release failed: ${e.message}")
            }
            mediaPlayer = null
        }
    }
}
