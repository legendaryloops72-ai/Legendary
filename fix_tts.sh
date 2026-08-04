sed -i '/DisposableEffect(context) {/,/    }/c \
    DisposableEffect(context) {\
        var textToSpeech: TextToSpeech? = null\
        textToSpeech = TextToSpeech(context) { status ->\
            if (status == TextToSpeech.SUCCESS) {\
                textToSpeech?.language = Locale("ar")\
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {\
                    override fun onStart(utteranceId: String?) {\
                        isSpeaking = true\
                    }\
                    override fun onDone(utteranceId: String?) {\
                        isSpeaking = false\
                    }\
                    override fun onError(utteranceId: String?) {\
                        isSpeaking = false\
                    }\
                })\
                tts = textToSpeech\
            }\
        }\
        onDispose {\
            textToSpeech?.stop()\
            textToSpeech?.shutdown()\
        }\
    }' app/src/main/java/com.aistudio.kidspolice.abcd/ui/StoriesScreen.kt

