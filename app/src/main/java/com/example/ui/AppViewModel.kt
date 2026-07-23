package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.ChildProfile
import com.example.data.KidTask
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import com.example.BuildConfig
import com.example.data.*

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    val profile: StateFlow<ChildProfile?> = repository.profile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val tasks: StateFlow<List<KidTask>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val badges: StateFlow<List<BadgeItem>> = repository.allBadges
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _newUnlockedBadge = MutableStateFlow<BadgeItem?>(null)
    val newUnlockedBadge = _newUnlockedBadge.asStateFlow()

    fun dismissUnlockedBadge() {
        _newUnlockedBadge.value = null
    }

    init {
        // تهيئة الملف الافتراضي تلقائياً لكي يفتح للطفل فوراً دون حواجز
        viewModelScope.launch {
            try {
                val exist = repository.profile.firstOrNull()
                if (exist == null) {
                    repository.saveProfile(ChildProfile(id = 1, name = "البطل الصغير", totalStars = 15))
                    addInitialTasks()
                }
                // Initialize default badges if none exist
                val existingBadges = repository.allBadges.firstOrNull()
                if (existingBadges.isNullOrEmpty()) {
                    val defaultBadges = listOf(
                        BadgeItem("badge_first_task", "نجم البداية", "أكمل أول مهمة تربوية بنجاح", "🌟", true, "tasks", 1, 1),
                        BadgeItem("badge_morning_hero", "البطل المنظم", "استيقظ ورتب سريره بانتظام", "🛏️", false, "tasks", 1, 0),
                        BadgeItem("badge_clean_teeth", "طبيب الأسنان الصغير", "حافظ على نظافة أسنانه وابتسامته البيضاء", "🪥", false, "tasks", 1, 0),
                        BadgeItem("badge_reader", "قارئ المستقبل", "أتم قراءة القصص المفيدة في المكتبة", "📚", false, "stories", 2, 0),
                        BadgeItem("badge_artist", "فنان الألوان", "شارك في تلوين ورسم اللوحات المبدعة", "🎨", false, "coloring", 1, 0),
                        BadgeItem("badge_gamer", "عبقري الألعاب", "أتقن ألعاب التركيز والتفكير والذاكرة", "🎮", false, "games", 1, 0),
                        BadgeItem("badge_helper", "مساعد العائلة البار", "ساعد ماما وبابا في البيت بكل حب", "❤️", false, "tasks", 1, 0),
                        BadgeItem("badge_stars_25", "نجم الـ 25 نجمة", "جمع 25 نجمة ذهبية من المهام والتحديات", "🏆", false, "stars", 25, 15),
                        BadgeItem("badge_police_hero", "حارس السلوك الأمثل", "استمع لنصائح الشرطي سامر والجهات الأمنية", "🛡️", true, "general", 1, 1),
                        BadgeItem("badge_superhero", "بطل الأبطال الخارقين", "أكمل جميع المهام اليومية والتربوية", "🚀", false, "tasks", 5, 0)
                    )
                    defaultBadges.forEach { repository.insertBadge(it) }
                }
            } catch (e: Exception) {
                android.util.Log.e("AppViewModel", "Database profile init failed: ${e.message}", e)
            }
        }
    }

    fun saveProfileName(name: String) {
        viewModelScope.launch {
            val current = profile.value
            if (current == null) {
                repository.saveProfile(ChildProfile(name = name, totalStars = 15))
                addInitialTasks()
            } else {
                repository.saveProfile(current.copy(name = name))
            }
        }
    }

    fun completeTask(task: KidTask) {
        viewModelScope.launch {
            if (!task.isCompleted) {
                repository.updateTask(task.copy(isCompleted = true))
                repository.addStars(task.starsReward)
                checkAndUnlockBadges()
            }
        }
    }

    fun awardQuizStars(stars: Int) {
        viewModelScope.launch {
            repository.addStars(stars)
            checkAndUnlockBadges()
        }
    }

    fun checkAndUnlockBadges() {
        viewModelScope.launch {
            val currentProfile = profile.value
            val currentTasks = tasks.value
            val currentBadges = badges.value

            val completedCount = currentTasks.count { it.isCompleted }
            val stars = currentProfile?.totalStars ?: 0

            suspend fun unlock(id: String) {
                val b = currentBadges.find { it.id == id }
                if (b != null && !b.isUnlocked) {
                    repository.unlockBadge(id)
                    _newUnlockedBadge.value = b
                }
            }

            if (completedCount >= 1) unlock("badge_first_task")
            if (completedCount >= 2) unlock("badge_morning_hero")
            if (completedCount >= 3) unlock("badge_clean_teeth")
            if (completedCount >= 4) unlock("badge_helper")
            if (completedCount >= currentTasks.size && currentTasks.isNotEmpty()) unlock("badge_superhero")
            if (stars >= 25) unlock("badge_stars_25")
        }
    }

    fun getBestScore(gameId: String): Flow<Int?> = repository.getBestScore(gameId)

    fun saveBestScore(gameId: String, score: Int) {
        viewModelScope.launch {
            val currentBest = repository.getBestScore(gameId).firstOrNull() ?: 0
            if (score > currentBest) {
                repository.saveBestScore(GameScore(gameId, score))
            }
        }
    }

    fun updateParentMessage(message: String) {
        viewModelScope.launch {
            val current = profile.value
            if (current != null) {
                repository.saveProfile(current.copy(parentMessage = message))
            }
        }
    }

    fun addTaskToChild(title: String, stars: Int) {
        viewModelScope.launch {
            repository.insertTask(KidTask(title = title, starsReward = stars))
        }
    }

    fun removeTask(task: KidTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // --- Conversational AI Call States & Methods ---
    private val _aiCallResponse = MutableStateFlow<String?>(null)
    val aiCallResponse = _aiCallResponse.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking = _isThinking.asStateFlow()

    private val _callChatHistory = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val callChatHistory = _callChatHistory.asStateFlow()

    fun resetCallChat() {
        _callChatHistory.value = emptyList()
        _aiCallResponse.value = null
        _isThinking.value = false
    }

    fun sendMessageToCallAI(callerType: String, childName: String, childMessage: String, onResponseReady: (String) -> Unit) {
        viewModelScope.launch {
            _isThinking.value = true
            val currentHistory = _callChatHistory.value.toMutableList()
            currentHistory.add("user" to childMessage)
            _callChatHistory.value = currentHistory

            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "YOUR_API_KEY" || apiKey == "MY_GEMINI_API_KEY") {
                    val fallback = when {
                        callerType.startsWith("police") -> {
                            when (callerType) {
                                "police_not_listening" -> "أنا فخور بك يا بطل! تذكر دائماً أن الاستماع لنصائح الماما والبابا يجعلك بطلاً قوياً ومحبوباً لدى الجميع. هل تعدني بسماع كلامهما؟"
                                "police_sleep_late" -> "أنا الشرطي سامر، النوم المبكر هو سر قوة الأبطال الخارقين! اذهب الآن لسريرك الدافئ لتستيقظ نشيطاً وبطلاً!"
                                "police_refusing_study" -> "أنا فخور باجتهادك وذكائك العبقري! الدراسة والواجبات طريقنا للقمة والتميز، ابدأ الآن بنشاط!"
                                "police_eating_sweets" -> "ابتسامتك جميلة وصحية كاللؤلؤ، ويجب أن نحميها بتفريش الأسنان والتقليل من الحلوى الضارة!"
                                "police_messy_room" -> "ترتيب الألعاب وتنظيم غرفتك يجعل الماما سعيدة ويثبت أنك بطل منظم ورائع!"
                                "police_helping_parents" -> "أحييك بحرارة عسكرية يا بطل! مساعدتك للماما والبابا عمل عظيم ويسعد قلبي جداً، استمر بذلك!"
                                "police_success" -> "ألف مبروك نجاحك الباهر يا بطل المستقبل! تفوقك الدراسي مصدر فخر لنا جميعاً في شرطة الأطفال!"
                                "police_healthy_food" -> "الغذاء الصحي المغذي كالفواكه والخضروات يمنحك الطاقة الخارقة والذكاء المتوقد، بطلنا الصحي المميز!"
                                else -> "أنا الشرطي سامر، أحييك يا بطل! لم تكتمل إعدادات الذكاء الاصطناعي لدي، لكني دائماً فخور بسلوكك الإيجابي الرائع!"
                            }
                        }
                        callerType == "doctor" -> "أنا طبيبك الطيب، فخور بك جداً! حافظ على نظافة أسنانك وصحتك دائماً لتكون بطلاً قوياً!"
                        callerType == "teacher" -> "أنا معلمك المخلص، فخور بك! واصل القراءة والتعلم بهمة ونشاط!"
                        callerType == "principal" -> "أنا مدير مدرستك، فخور بتميزك واجتهادك الدائم!"
                        else -> "أنا صديقك الموجه، فخور بك يا بطل!"
                    }
                    val updatedHistory = _callChatHistory.value.toMutableList()
                    updatedHistory.add("ai" to fallback)
                    _callChatHistory.value = updatedHistory
                    _aiCallResponse.value = fallback
                    onResponseReady(fallback)
                    return@launch
                }

                // Prepare system instruction based on the active caller type
                val systemInstructionText = when {
                    callerType.startsWith("police") -> {
                        val scenarioContext = when (callerType) {
                            "police_not_listening" -> "السيناريو الحالي: الطفل يرفض الاستماع لكلام والديه أو يعاندهما. ركز في الرد على أهمية احترام وتلبية نصائح الوالدين بحب ولطف ليظل الطفل بطلاً ذكياً وقوياً."
                            "police_sleep_late" -> "السيناريو الحالي: الطفل يسهر ليلاً ويرفض النوم باكراً. وجهه بلطف لفوائد النوم المبكر لتخزين الطاقة واسترداد النشاط الخارق ليوم غد."
                            "police_refusing_study" -> "السيناريو الحالي: الطفل يتكاسل عن الدراسة والواجبات المدرسية. حببه في العلم والكتب والمستقبل الباهر بأسلوب مشوق."
                            "police_eating_sweets" -> "السيناريو الحالي: الطفل يتناول الكثير من الحلويات ويرفض غسل أسنانه. شجعه على الحفاظ على أسنانه بيضاء براقة كاللؤلؤ بالتقليل من السكاكر والتنظيف المستمر."
                            "police_messy_room" -> "السيناريو الحالي: الطفل يترك ألعابه مبعثرة ولا يرتب غرفته. شجعه بحماس لتنظيم ألعابه ومملكتها ليكون بطلاً مرتباً يساعد الماما."
                            "police_helping_parents" -> "السيناريو الحالي (إيجابي): اتصال شكر وتشجيع لأن الطفل يساعد والديه في المنزل ومطيع جداً. عبر عن سعادتك البالغة وفخرك به وقدم له تحية عسكرية افتراضية."
                            "police_success" -> "السيناريو الحالي (إيجابي): اتصال تهنئة بالنجاح الدراسي الباهر والحصول على درجات عالية. كرمه بكلمات ملهمة تعزز تفوقه وثقته بنفسه."
                            "police_healthy_food" -> "السيناريو الحالي (إيجابي): اتصال تشجيع لتناول الطعام الصحي والخضروات المفيدة والابتعاد عن الوجبات السريعة الضارة لتقوية عضلاته وعقله الذكي."
                            "police_monster" -> "السيناريو الحالي: وحش الأطفال اللطيف يتحدث مع الطفل. هو وحش مضحك ولطيف جداً، يحب الأطفال الشجعان الذين يرتبون غرفهم وينامون باكراً. يجب أن يكون الصوت والأسلوب مرحاً ومحبباً."
                            else -> "الطفل يجري مكالمة عامة وتفاعلية مع الشرطة."
                        }
                        """
                        أنت شرطي تربوي، مشجع، وصديق للأطفال تدعى 'الشرطي سامر'. مهمتك هي التحدث مع الأطفال وتوجيههم بشكل إيجابي بناءً على ما يقولونه.
                        $scenarioContext
                        القواعد الصارمة:
                        - ممنوع تماماً استخدام أي ترهيب، تخويف، سجن، أو ذكر السجن والعقاب والنار أو قص الآذان واللسان أو حلقة الدود.
                        - يجب أن يكون أسلوبك مليئاً بالتحفيز والتربية الإيجابية والأبوة الحانية (مثل: يا بطل، يا ذكي، أنا فخور بك جداً، يا شاطر).
                        - استخدم لغة عربية فصحى مبسطة جداً ومحببة ومفهومة للأطفال.
                        - اجعل الردود قصيرة ومباشرة ومرحة (لا تتعدى جملتين أو ثلاث على الأكثر) لتناسب استيعاب الطفل وعمره الصغير.
                        - إذا فعل الطفل شيئاً جيداً، شجعه بحماس. وإذا ذكر أنه رفض سلوكاً معيناً، حفزه بذكر فوائد السلوك الصحيح بأسلوب محبب وممتع جداً.
                        - تذكر دائماً أن اسم الطفل هو: $childName.
                        """.trimIndent()
                    }
                    callerType == "doctor" -> """
                        أنت طبيب أطفال وطبيب أسنان طيب ولطيف جداً تدعى 'الدكتور طيب'. مهمتك هي توجيه الأطفال ودعمهم صحياً وتثقيفهم بأسلوب إيجابي ومحبب.
                        القواعد الصارمة:
                        - ممنوع تماماً تخويف الأطفال بالإبرة، أو سحب السن بالعنف، أو التهديد بالألم الشديد.
                        - شجعهم على تنظيف الأسنان بالفرشاة والمعجون مرتين يومياً، وتناول الغذاء الصحي كالخضروات والفواكه.
                        - تحدث بلغة عربية بسيطة ومحفزة جداً (مثل: بطلنا الصحي، السن اللامع، أسنانك مثل اللؤلؤ).
                        - اجعل الردود قصيرة للغاية (لا تتعدى جملتين أو ثلاث).
                        - تذكر دائماً أن اسم الطفل هو: $childName.
                    """.trimIndent()
                    callerType == "teacher" -> """
                        أنت معلم أحلام ذكي ومحفز ومحبوب جداً تدعى 'الأستاذ منير'. مهمتك التوجيه العلمي والمعرفي وغرس حب القراءة والتعلم في قلوب الأطفال.
                        القواعد الصارمة:
                        - ممنوع تماماً التوبيخ، أو القول بأن الطفل فاشل أو كسول.
                        - ركز على حب العلم، واكتشاف العالم، وحل الفروض المدرسية بشغف وسعادة.
                        - تحدث بأسلوب حماسي ممتع ولغة عربية مبسطة جداً (مثل: عبقري المستقبل، مستكشفنا الذكي، شعلة الذكاء).
                        - اجعل الردود قصيرة (لا تتعدى جملتين أو ثلاث).
                        - تذكر دائماً أن اسم الطفل هو: $childName.
                    """.trimIndent()
                    callerType == "principal" -> """
                        أنت مدير المدرسة الفاضل والمشجع والمكرم للطلبة المتميزين تدعى 'المدير عادل'. مهمتك بث الثقة والتقدير والاحترام في نفوس الطلاب ودعم نجاحهم وتفوقهم.
                        القواعد الصارمة:
                        - ممنوع تماماً التهديد بالفصل من المدرسة، أو العقاب الشديد، أو التوبيخ أمام زملائه.
                        - شجع على الانضباط، والتعاون مع الأصدقاء، والاستماع للمعلم والماما والبابا، والنجاح والتفوق للوصول لأعلى مراتب البطولة والمجد.
                        - لغتك عربية مبسطة، رسمية ولكنها دافئة ومحفزة جداً.
                        - اجعل الردود قصيرة ومباشرة (لا تتعدى جملتين أو ثلاث).
                        - تذكر دائماً أن اسم الطفل هو: $childName.
                    """.trimIndent()
                    else -> """
                        أنت مرشد تربوي وصديق وفي وموجه إيجابي للأطفال تدعى 'العم حكيم'. مهمتك غرس السلوك الإيجابي ومساعدة الوالدين في التربية بطريقة ممتعة جداً.
                        القواعد الصارمة:
                        - التوجيه الذكي الإيجابي دائماً بدون خوف أو قلق.
                        - تحدث بلغة بسيطة وقصيرة (جملتين أو ثلاث).
                        - اسم الطفل هو: $childName.
                    """.trimIndent()
                }

                // Construct conversation context from call history
                val contentsList = mutableListOf<Content>()
                _callChatHistory.value.forEach { turn ->
                    contentsList.add(Content(parts = listOf(Part(text = turn.second))))
                }

                val request = GenerateContentRequest(
                    contents = contentsList,
                    systemInstruction = Content(parts = listOf(Part(text = systemInstructionText))),
                    generationConfig = GenerationConfig(temperature = 0.7f)
                )

                val response = RetrofitClient.service.generateContent(
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey",
                    request
                )

                val reply = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "عذراً يا بطل، يبدو أن هناك تشويشاً في الاتصال، أعد ما قلته من فضلك!"

                val updatedHistory = _callChatHistory.value.toMutableList()
                updatedHistory.add("ai" to reply)
                _callChatHistory.value = updatedHistory
                _aiCallResponse.value = reply
                onResponseReady(reply)

            } catch (e: Exception) {
                e.printStackTrace()
                android.util.Log.e("GeminiCallError", "API Error", e)
                val errorFallback = "أنا فخور بك يا بطل! أسمعك بوضوح، حافظ على سلوكك الرائع المتميز دائماً!"
                val updatedHistory = _callChatHistory.value.toMutableList()
                updatedHistory.add("ai" to errorFallback)
                _callChatHistory.value = updatedHistory
                _aiCallResponse.value = errorFallback
                onResponseReady(errorFallback)
            } finally {
                _isThinking.value = false
            }
        }
    }

    private val _generatedStory = MutableStateFlow<String?>(null)
    val generatedStory = _generatedStory.asStateFlow()

    private val _isGeneratingStory = MutableStateFlow(false)
    val isGeneratingStory = _isGeneratingStory.asStateFlow()

    fun clearStory() {
        _generatedStory.value = null
    }

    fun generateStory(topic: String) {
        viewModelScope.launch {
            _isGeneratingStory.value = true
            _generatedStory.value = null
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "YOUR_API_KEY" || apiKey == "MY_GEMINI_API_KEY") {
                    _generatedStory.value = "الرجاء إعداد مفتاح API الخاص بـ Gemini في لوحة الأسرار (Secrets Panel) لعرض القصص الممتعة."
                    _isGeneratingStory.value = false
                    return@launch
                }
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = "اكتب قصة قصيرة للأطفال باللغة العربية حول موضوع: $topic. يجب أن تكون القصة تعليمية وتحتوي على مغزى أخلاقي واضح. اجعل القصة مشوقة ومناسبة للأطفال الصغار، ولا تزيد عن 200 كلمة."))))
                )
                val response = RetrofitClient.service.generateContent("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey", request)
                _generatedStory.value = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "عذراً يا بطل، لم أتمكن من تأليف القصة الآن، حاول مرة أخرى!"
            } catch (e: Exception) {
                e.printStackTrace()
                android.util.Log.e("GeminiError", "API Error", e)
                _generatedStory.value = "حدث خطأ: ${e.message} - ${e.javaClass.simpleName}"
            } finally {
                _isGeneratingStory.value = false
            }
        }
    }

    private fun addInitialTasks() {
        viewModelScope.launch {
            val initialTasks = listOf(
                KidTask(title = "ترتيب السرير الصباحي 🛏️", starsReward = 5),
                KidTask(title = "تنظيف الأسنان مرتين بالفرشاة 🪥", starsReward = 5),
                KidTask(title = "المشاهدة والتعلم اليومي المفيد 📚", starsReward = 5),
                KidTask(title = "غسل اليدين بالماء والصابون 🧼", starsReward = 5),
                KidTask(title = "مساعدة ماما وبابا في البيت 🏡", starsReward = 5)
            )
            initialTasks.forEach { repository.insertTask(it) }
        }
    }
}

