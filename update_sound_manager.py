import re

with open('app/src/main/java/com/example/sound/CallSoundManager.kt', 'r') as f:
    content = f.read()

# Add the localAssetMap
local_asset_map = """
    val localAssetMap = mapOf(
        // Animals
        "بقرة" to "sounds/animals/cow.mp3",
        "خروف" to "sounds/animals/sheep.mp3",
        "ماعز" to "sounds/animals/goat.mp3",
        "دجاجة" to "sounds/animals/hen.mp3",
        "ديك" to "sounds/animals/rooster.mp3",
        "حصان" to "sounds/animals/horse.mp3",
        "خنزير" to "sounds/animals/pig.mp3",
        "حمار" to "sounds/animals/donkey.mp3",
        "قطة" to "sounds/animals/cat.mp3",
        "كلب" to "sounds/animals/dog.mp3",
        "أسد" to "sounds/animals/lion.mp3",
        "نمر" to "sounds/animals/tiger.mp3",
        "ذئب" to "sounds/animals/wolf.mp3",
        "فيل" to "sounds/animals/elephant.mp3",
        "قرد" to "sounds/animals/monkey.mp3",
        "دب" to "sounds/animals/bear.mp3",
        "ثعلب" to "sounds/animals/fox.mp3",
        "بومة" to "sounds/animals/owl.mp3",
        "ببغاء" to "sounds/animals/parrot.mp3",
        "بطة" to "sounds/animals/duck.mp3",
        "عصفور" to "sounds/animals/bird.mp3",
        "دولفين" to "sounds/animals/dolphin.mp3",
        "حوت" to "sounds/animals/whale.mp3",
        
        // Vehicles
        "سيارة" to "sounds/vehicles/car.mp3",
        "شاحنة كبيرة" to "sounds/vehicles/truck.mp3",
        "دراجة نارية" to "sounds/vehicles/motorcycle.mp3",
        "سيارة إسعاف" to "sounds/vehicles/ambulance.mp3",
        "سيارة إطفاء" to "sounds/vehicles/firetruck.mp3",
        "سيارة شرطة" to "sounds/vehicles/police_car.mp3",
        "قطار" to "sounds/vehicles/train.mp3",
        "طائرة" to "sounds/vehicles/airplane.mp3",
        "سفينة" to "sounds/vehicles/ship.mp3",
        "دراجة هوائية" to "sounds/vehicles/bicycle.mp3"
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
                    val afd = context.assets.openFd(assetPath)
                    val mp = MediaPlayer()
                    mediaPlayer = mp
                    mp.apply {
                        setAudioStreamType(AudioManager.STREAM_MUSIC)
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        afd.close()
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
                    Log.e("CallSoundManager", "Error: Audio file is missing in assets -> $assetPath")
                    onError()
                }
            }
        }
    }
"""

content = content.replace("    fun playAudioFromUrl(url: String", local_asset_map + "\n    fun playAudioFromUrl(url: String")

# Update playKidsRealisticSound
play_kids_realistic_sound = """
    fun playKidsRealisticSound(soundName: String, description: String) {
        if (isReleased) return
        stopRingtone()
        stopSpeaking()
        stopAudio()

        val trimmedName = soundName.trim()
        val assetPath = localAssetMap[trimmedName]

        val onStartAction = {
            Log.d("CallSoundManager", "Started playing sound for: $trimmedName")
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
                    playLocalFallbackSound(trimmedName, description)
                }
            )
        } else {
            playLocalFallbackSound(trimmedName, description)
        }
    }
"""

content = re.sub(r'    fun playKidsRealisticSound\(soundName: String, description: String\).*?private fun playLocalFallbackSound', play_kids_realistic_sound + "\n    private fun playLocalFallbackSound", content, flags=re.DOTALL)

with open('app/src/main/java/com/example/sound/CallSoundManager.kt', 'w') as f:
    f.write(content)
