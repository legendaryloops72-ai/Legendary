package com.aistudio.kidspolice.viewmodel

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.kidspolice.BuildConfig
import com.aistudio.kidspolice.api.ApiClient
import com.aistudio.kidspolice.api.Content
import com.aistudio.kidspolice.api.GenerateContentRequest
import com.aistudio.kidspolice.api.GenerationConfig
import com.aistudio.kidspolice.api.Part
import com.aistudio.kidspolice.api.ThinkingConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel : ViewModel(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Home)
    val uiState: StateFlow<UiState> = _uiState

    private val _callScript = MutableStateFlow<String?>(null)
    
    private var isTtsReady = false

    fun initTts(context: Context) {
        if (tts == null) {
            tts = TextToSpeech(context, this)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported")
            } else {
                isTtsReady = true
            }
        }
    }

    fun startCall(name: String, gender: String, behavior: String) {
        _uiState.value = UiState.IncomingCall
        _callScript.value = null
        
        viewModelScope.launch {
            try {
                val model = "gemini-3.1-pro-preview"
                
                val prompt = """
                    أنت ضابط شرطة من "شرطة الأطفال". يتصل بك أحد الوالدين لأن طفلهما اسمه "${'$'}name" (الجنس: "${'$'}gender") قام بالسلوك التالي: "${'$'}behavior".
                    يرجى كتابة سيناريو قصير لما ستقوله للطفل على الهاتف. 
                    - تحدث باللغة العربية بلهجة واضحة.
                    - إذا كان السلوك سيئاً، كن حازماً ولكن لطيفاً، واطلب منه التوقف عن ذلك السلوك وأن يكون مطيعاً.
                    - إذا كان السلوك جيداً، قم بمدحه وشكره وتشجيعه.
                    - لا تستخدم أي رموز تعبيرية (Emojis) أو تنسيقات (مثل النجوم أو الأقواس)، اكتب فقط النص الذي سيتم نطقه صوتياً بشكل مباشر.
                    - اجعل الرد قصيراً ومناسباً للأطفال (حوالي 3-5 جمل).
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = prompt)))
                    ),
                    generationConfig = GenerationConfig(
                        thinkingConfig = ThinkingConfig(thinkingLevel = "HIGH"),
                        temperature = 0.7
                    )
                )

                val response = ApiClient.geminiApi.generateContent(
                    model = model,
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )
                
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                _callScript.value = text ?: "مرحباً يا بطل، أتمنى أن تكون بأفضل حال!"
                
                if (_uiState.value is UiState.OnCall) {
                    playScript()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _callScript.value = "حدث خطأ في الاتصال بشرطة الأطفال، الرجاء المحاولة لاحقاً."
                if (_uiState.value is UiState.OnCall) {
                    playScript()
                }
            }
        }
    }

    fun answerCall() {
        _uiState.value = UiState.OnCall(durationSeconds = 0)
        if (_callScript.value != null) {
            playScript()
        }
    }

    fun endCall() {
        tts?.stop()
        _uiState.value = UiState.Home
    }

    private fun playScript() {
        val script = _callScript.value ?: return
        if (isTtsReady) {
            tts?.speak(script, TextToSpeech.QUEUE_FLUSH, null, "CallScript")
        }
    }

    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        super.onCleared()
    }
}

sealed class UiState {
    object Home : UiState()
    object IncomingCall : UiState()
    data class OnCall(val durationSeconds: Int) : UiState()
}
