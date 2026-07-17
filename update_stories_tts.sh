sed -i 's/import androidx.compose.material.icons.filled.ArrowBack/import androidx.compose.material.icons.filled.ArrowBack\nimport androidx.compose.material.icons.filled.PlayArrow\nimport androidx.compose.material.icons.filled.Stop\nimport android.speech.tts.TextToSpeech\nimport androidx.compose.ui.platform.LocalContext\nimport java.util.Locale/' app/src/main/java/com/example/ui/StoriesScreen.kt

sed -i '/val generatedStory by viewModel.generatedStory.collectAsStateWithLifecycle()/a \
\
    val context = LocalContext.current\
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }\
    var isSpeaking by remember { mutableStateOf(false) }\
\
    DisposableEffect(context) {\
        val textToSpeech = TextToSpeech(context) { status ->\
            if (status == TextToSpeech.SUCCESS) {\
                tts?.language = Locale("ar")\
            }\
        }\
        tts = textToSpeech\
        onDispose {\
            textToSpeech.stop()\
            textToSpeech.shutdown()\
        }\
    }\
\
    LaunchedEffect(generatedStory) {\
        if (generatedStory == null) {\
            tts?.stop()\
            isSpeaking = false\
        }\
    }' app/src/main/java/com/example/ui/StoriesScreen.kt

