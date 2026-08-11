package com.aistudio.kidspolice.abcd.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.kidspolice.abcd.data.AppRepository
import com.aistudio.kidspolice.abcd.data.ChildProfile
import com.aistudio.kidspolice.abcd.data.KidTask
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
import com.aistudio.kidspolice.abcd.BuildConfig
import com.aistudio.kidspolice.abcd.data.*

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

    init {
        // تهيئة الملف الافتراضي تلقائياً لكي يفتح للطفل فوراً دون حواجز
        viewModelScope.launch {
            try {
                val exist = repository.profile.firstOrNull()
                if (exist == null) {
                    repository.saveProfile(ChildProfile(id = 1, name = "البطل الصغير", totalStars = 15))
                    addInitialTasks()
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
            }
        }
    }

    fun awardQuizStars(stars: Int) {
        viewModelScope.launch {
            repository.addStars(stars)
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

            try {
                // Initialize Vertex AI in Firebase
                val config = com.google.firebase.vertexai.type.generationConfig {
                    temperature = 0.7f
                }
                val sysInstruction = com.google.firebase.vertexai.type.content {
                    text(systemInstructionText)
                }

                // Call the generative model from Firebase Vertex AI (recommending gemini-2.5-flash)
                val model = com.google.firebase.vertexai.FirebaseVertexAI.getInstance(com.google.firebase.FirebaseApp.getInstance()).generativeModel(
                    modelName = "gemini-2.5-flash",
                    generationConfig = config,
                    systemInstruction = sysInstruction
                )

                // Map history items
                val historyList = _callChatHistory.value.map { turn ->
                    com.google.firebase.vertexai.type.content(role = if (turn.first == "user") "user" else "model") {
                        text(turn.second)
                    }
                }

                val response = model.generateContent(*historyList.toTypedArray())
                val reply = response.text ?: "عذراً يا بطل، يبدو أن هناك تشويشاً في الاتصال، أعد ما قلته من فضلك!"

                val updatedHistory = _callChatHistory.value.toMutableList()
                updatedHistory.add("ai" to reply)
                _callChatHistory.value = updatedHistory
                _aiCallResponse.value = reply
                onResponseReady(reply)

            } catch (e: Exception) {
                e.printStackTrace()
                android.util.Log.e("FirebaseVertexCallError", "Vertex AI in Firebase failed/unconfigured, using offline fallback", e)

                // Secure educational offline fallbacks based on caller type
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
                val config = com.google.firebase.vertexai.type.generationConfig {
                    temperature = 0.8f
                }
                val model = com.google.firebase.vertexai.FirebaseVertexAI.getInstance(com.google.firebase.FirebaseApp.getInstance()).generativeModel(
                    modelName = "gemini-2.5-flash",
                    generationConfig = config
                )

                val prompt = "اكتب قصة قصيرة للأطفال باللغة العربية حول موضوع: $topic. يجب أن تكون القصة تعليمية وتحتوي على مغزى أخلاقي واضح. اجعل القصة مشوقة ومناسبة للأطفال الصغار، ولا تزيد عن 200 كلمة."
                val response = model.generateContent(prompt)
                _generatedStory.value = response.text ?: "عذراً يا بطل، لم أتمكن من تأليف القصة الآن، حاول مرة أخرى!"
            } catch (e: Exception) {
                e.printStackTrace()
                android.util.Log.e("FirebaseVertexStoryError", "Vertex AI in Firebase failed/unconfigured, using offline fallback", e)

                // High-quality localized fallback stories based on child-selected themes
                val fallbackStory = when {
                    topic.contains("كذب") || topic.contains("صدق") -> """
                        كان يا ما كان، في قرية هادئة، يعيش طفل صغير اسمه سيف. كان سيف طفلاً ذكياً ولكنه كان يمزح أحياناً ويكذب على أصدقائه ليضحك.
                        في يوم من الأيام، قال سيف لأصدقائه: "لقد رأيت عصفوراً ذهبياً يطير!"، فبحثوا عنه طويلاً ولم يجدوا شيئاً، فضحك سيف وقال: "كنت أمزح!".
                        بعد أيام، ضاع قلم سيف المميز، فطلب المساعدة من أصدقائه، لكنهم ظنوا أنه يكذب كالعادة فلم يصدقوه. شعر سيف بالحزن الشديد وعرف أن الكذب يفقد الناس ثقتهم به.
                        قرر سيف من ذلك اليوم أن لا يقول إلا الصدق دائماً، فعادت محبة وثقة أصدقائه إليه وعاش سعيداً وصادقاً.
                    """.trimIndent()
                    topic.contains("نظافة") || topic.contains("وسخ") -> """
                        في حديقة جميلة، كان الأرنب سمسم يحب اللعب بالتراب والطين، ولكنه بعد اللعب كان ينسى غسل يديه بالماء والصابون ويتناول طعامه فوراً.
                        في ليلة من الليالي، شعر سمسم بألم شديد في بطنه وبدأ بالبكاء. زاره الطبيب الحكيم وقال له: "يا سمسم، الجراثيم الصغيرة انتقلت من يديك المتسختين إلى بطنك عندما أكلت دون غسيل".
                        تعلم سمسم الدرس، وبدأ يغسل يديه بالماء والصابون بانتظام قبل الأكل وبعد اللعب، واهتم بنظافة غرفته وجسده، فذهب الألم وعاد يلعب بنشاط وصحة دائمين.
                    """.trimIndent()
                    topic.contains("دراسة") || topic.contains("واجب") || topic.contains("مدرسة") -> """
                        كانت النملة تنومة تحب اللعب والغناء طوال اليوم، بينما كان جيرانها النمل يدرسون ويعملون بجد. وعندما كان يقترب الامتحان، كانت تنومة تتكاسل وتؤجل واجباتها.
                        جاء يوم الامتحان الصعب، ولم تستطع تنومة الإجابة على الأسئلة فشعرت بالندم الشديد والدموع في عينيها. جاءت صديقتها النملة النشيطة وقالت لها: "لا تحزني يا تنومة، لكن تذكري أن من يزرع يجد، والاجتهاد والدراسة اليوم هما سر النجاح غداً".
                        قررت تنومة تنظيم وقتها للدراسة واللعب، واجتهدت كثيراً في الفصل التالي وحصلت على درجات رائعة، فعرفت لذة النجاح والتفوق.
                    """.trimIndent()
                    else -> """
                        في قديم الزمان، كان هناك طفل عراقي ذكي يحب مساعدة الجميع في بيته وقريته. كان ينظف غرفته، ويساعد والديه في ترتيب المائدة، ويشارك ألعابه مع أصدقائه بحب ولطف.
                        في أحد الأيام، كافأه والده بهدية جميلة وقال له: "أنت بطلنا الحقيقي لأنك تنشر الخير والمساعدة حولك". شعر البطل بسعادة غامرة وعاهد نفسه أن يستمر دائماً في تقديم العون لمن يحتاج، ليبقى قدوة حسنة وصديقاً محبوباً لدى الجميع.
                    """.trimIndent()
                }
                _generatedStory.value = fallbackStory
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

