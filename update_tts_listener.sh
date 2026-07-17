sed -i 's/import android.speech.tts.TextToSpeech/import android.speech.tts.TextToSpeech\nimport android.speech.tts.UtteranceProgressListener/' app/src/main/java/com/example/ui/StoriesScreen.kt

sed -i '/tts?.language = Locale("ar")/a \
                textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {\
                    override fun onStart(utteranceId: String?) {\
                        isSpeaking = true\
                    }\
                    override fun onDone(utteranceId: String?) {\
                        isSpeaking = false\
                    }\
                    override fun onError(utteranceId: String?) {\
                        isSpeaking = false\
                    }\
                })' app/src/main/java/com/example/ui/StoriesScreen.kt

sed -i 's/tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, null)/tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, "story")/g' app/src/main/java/com/example/ui/StoriesScreen.kt

