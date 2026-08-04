package com.aistudio.kidspolice.abcd.sound

data class CallCharacter(
    val id: String,
    val name: String,
    val avatarAsset: String, // Resource name or path
    val dialogues: List<CallDialogue>
)

data class CallDialogue(
    val trigger: String,
    val text: String,
    val animationState: String = "idle"
)

object CallCharacterRepository {
    val characters = listOf(
        CallCharacter(
            id = "police",
            name = "الشرطي سامر",
            avatarAsset = "police_avatar",
            dialogues = listOf(
                CallDialogue("greeting", "أهلاً ومرحباً بك يا بطل! أنا الشرطي سامر صديقك المفضل. أنا سعيد جداً بالحديث معك اليوم!"),
                CallDialogue("not_listening", "مرحباً يا بطل! سمعت أنك تواجه صعوبة في سماع كلام الماما والبابا. هل تعلم أن سماع كلام عائلتنا هو سر قوتنا؟"),
                CallDialogue("sleep_late", "أهلاً وصديقي البطل! لقد أخبرني درع الحراسة أنك ما زلت مستيقظاً! النوم المبكر يعيد شحن طاقتك الخارقة."),
                CallDialogue("refusing_study", "أهلاً بعبقري الغد! الدراسة ممتعة جداً وهي طريقنا للنجاح الباهر. ما رأيك أن نبدأ الآن؟"),
                CallDialogue("eating_sweets", "أهلاً بصديقي! الحلوى لذيذة ولكن الكثير منها يؤلم الأسنان. هل تعدني بأن تنظف أسنانك الآن؟"),
                CallDialogue("messy_room", "مرحباً بصديقي المنظم! البطل الخارق يتميز بترتيب غرفته. ما رأيك أن نرتب الألعاب معاً؟"),
                CallDialogue("helping_parents", "أهلاً وسهلاً ببطلنا المحبوب! أنا فخور بك جداً لأنك تساعد الماما والبابا في المنزل."),
                CallDialogue("success", "يا لها من فرحة كبيرة! مبروك نجاحك الباهر واجتهادك المتميز. أنت شعلة تضيء لنا المستقبل!"),
                CallDialogue("healthy_food", "أهلاً بصديقي القوي! أنا سعيد جداً لأنك تأكل الخضروات والفاكهة لتزيد من قوة عضلاتك.")
            )
        ),
        CallCharacter(
            id = "doctor",
            name = "الدكتور طيب",
            avatarAsset = "doctor_avatar",
            dialogues = listOf(
                CallDialogue("greeting", "أهلاً يا حلوين! أنا الدكتور طيب صديقكم الحبيب. أتصل لأطمئن على صحة أسنانكم البراقة والجميلة!"),
                CallDialogue("tooth_brushing", "تفريش الأسنان يحمي ابتسامتكم لتظل قوية كاللؤلؤ. هل فرشت أسنانك اليوم؟")
            )
        ),
        CallCharacter(
            id = "teacher",
            name = "الأستاذ منير",
            avatarAsset = "teacher_avatar",
            dialogues = listOf(
                CallDialogue("greeting", "أهلاً بك يا عبقري المستقبل! أنا معلمك الأستاذ منير، فخور جداً بشغفك وحبك للتعلم.")
            )
        ),
        CallCharacter(
            id = "monster",
            name = "الوحش اللطيف",
            avatarAsset = "monster_avatar",
            dialogues = listOf(
                CallDialogue("greeting", "أهلاً أهلاً! أنا الوحش اللطيف، أحب الأطفال الذين ينظفون غرفهم ويأكلون الخضروات! هل أنت بطل اليوم؟"),
                CallDialogue("brave", "يا لك من بطل شجاع! أنا فخور بك جداً لأنك لا تخاف من الظلام وتنام بمفردك كالأبطال."),
                CallDialogue("laugh", "ها ها ها! ضحكتك جميلة جداً وتجعلني سعيداً. هل نضحك معاً؟")
            )
        )
    )

    fun getCharacterById(id: String): CallCharacter? = characters.find { it.id == id }
    
    fun getDialogue(characterId: String, trigger: String): CallDialogue? {
        return getCharacterById(characterId)?.dialogues?.find { it.trigger == trigger }
    }
}
