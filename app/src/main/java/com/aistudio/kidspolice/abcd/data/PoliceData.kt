package com.aistudio.kidspolice.abcd.data

import kotlinx.serialization.Serializable

@Serializable
enum class Dialect(val id: String, val titleAr: String, val flag: String) {
    SAUDI("saudi", "اللهجة السعودية / الخليجية", "🇸🇦"),
    EGYPTIAN("egyptian", "اللهجة المصرية", "🇪🇬"),
    SYRIAN("syrian", "اللهجة الشامية / السورية", "🇸🇾"),
    MOROCCAN("moroccan", "اللهجة المغربية / المغاربية", "🇲🇦"),
    IRAQI("iraqi", "اللهجة العراقية", "🇮🇶"),
    CLASSICAL("classical", "اللغة العربية الفصحى", "⭐")
}

@Serializable
enum class CallCategory(val titleAr: String, val iconName: String) {
    DISOBEDIENT("ترهيب تربوي (مشاغب)", "warning"),
    GOOD_BEHAVIOR("تشجيع ومكافأة (شاطر)", "star"),
    EATING("رفض الأكل والخضار", "restaurant"),
    SLEEPING("رفض النوم مبكراً", "bedtime"),
    STUDYING("إهمال الواجبات المدرسية", "school"),
    SCREAMING("الصراخ والعناد", "volume_up"),
    CLEANING("ترتيب الغرفة والألعاب", "cleaning_services"),
    TEETH("تنظيف الأسنان", "brush")
}

@Serializable
data class PoliceScenario(
    val id: String,
    val title: String,
    val dialect: Dialect,
    val category: CallCategory,
    val isReward: Boolean,
    val callerName: String,
    val callerRank: String,
    val dialogues: List<DialogueLine>
)

@Serializable
data class DialogueLine(
    val speaker: String,
    val text: String,
    val durationSeconds: Int,
    val pauseAfterSeconds: Int = 2
)

@Serializable
data class PoliceSound(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val soundType: SoundType
)

enum class SoundType {
    SIREN_CLASSIC,
    SIREN_FAST,
    SIREN_WAIL,
    RADIO_BEEP,
    HORN,
    WALKIE_TALKIE,
    POLICE_WHISTLE,
    HELI_SOUND
}

@Serializable
data class EducationalMission(
    val id: String,
    val title: String,
    val description: String,
    val points: Int,
    val category: String,
    val isCompleted: Boolean = false
)

object PoliceRepository {

    val dialects = Dialect.entries.toList()

    val scenarios: List<PoliceScenario> = listOf(
        // Saudi Scenarios
        PoliceScenario(
            id = "saudi_bad_sleep",
            title = "ما يبي ينام (سعودي)",
            dialect = Dialect.SAUDI,
            category = CallCategory.SLEEPING,
            isReward = false,
            callerName = "الضابط فهد",
            callerRank = "الملازم أول - شرطة الأطفال",
            dialogues = listOf(
                DialogueLine("الضابط فهد", "الو، السلام عليكم! معكم شرطة الأطفال، الضابط فهد يتحدث.", 4),
                DialogueLine("الضابط فهد", "جانا بلاغ عاجل من الوالدين إن في بطل صغير رافض ينام الحين؟ صحيح هالكلام؟", 5),
                DialogueLine("الضابط فهد", "شوف يا بطل.. النوم المبكر يخليك قوي وذكي وتكبر وتصير بطل حقيقي!", 5),
                DialogueLine("الضابط فهد", "أنا مشغل الدورية وجاي بالطريق.. إذا ما نمت الحين راح آخذ كل الألعاب!", 5),
                DialogueLine("الضابط فهد", "يلا الحين تروح فراشك وتقول دعاء النوم وتغمض عيونك، تمام؟ مع السلامة!", 5)
            )
        ),
        PoliceScenario(
            id = "saudi_good_boy",
            title = "بطل ومؤدب ومطيع (سعودي)",
            dialect = Dialect.SAUDI,
            category = CallCategory.GOOD_BEHAVIOR,
            isReward = true,
            callerName = "النقيب خالد",
            callerRank = "قسم المكافآت والأبطال",
            dialogues = listOf(
                DialogueLine("النقيب خالد", "هلا والله بالبطل! معاك النقيب خالد من شرطة الأطفال.", 4),
                DialogueLine("النقيب خالد", "وصلنا تقرير ممتاز من البيت يقول إنك ولد شاطر وتسمع كلام الماما والبابا!", 5),
                DialogueLine("النقيب خالد", "إحنا في مركز الشرطة فخورين فيك جداً وتستاهل وسام الشجاعة والهدية!", 5),
                DialogueLine("النقيب خالد", "استمر كذا دايم بطل مؤدب، وراح نزورك ونسلم عليك بنفسنا. كفو والله!", 5)
            )
        ),
        PoliceScenario(
            id = "saudi_bad_eat",
            title = "ما يبي يأكل الخضار (سعودي)",
            dialect = Dialect.SAUDI,
            category = CallCategory.EATING,
            isReward = false,
            callerName = "الضابط فهد",
            callerRank = "دورية التغذية الصحية",
            dialogues = listOf(
                DialogueLine("الضابط فهد", "مرحباً يا بطل، الضابط فهد معاك. وش السالفة؟ سمعت إنك رافض تأكل صحنك؟", 5),
                DialogueLine("الضابط فهد", "الأكل الصحي هو اللي يبني العضلات ويخليك ذكي وسريع مثل رجال الأمن!", 5),
                DialogueLine("الضابط فهد", "الحين أمك مجهزة لك أكل لذيذ وصحي، أبغاك تخلصه كامل ولا حبة تبقى!", 5),
                DialogueLine("الضابط فهد", "إذا خلصته بلغني عشان نسجل اسمك في لوحة شرف الأبطال الأقوياء!", 4)
            )
        ),

        // Egyptian Scenarios
        PoliceScenario(
            id = "egypt_bad_screaming",
            title = "بيصرخ ويعند (مصري)",
            dialect = Dialect.EGYPTIAN,
            category = CallCategory.SCREAMING,
            isReward = false,
            callerName = "العميد حسام",
            callerRank = "مدير المباحث التربوية",
            dialogues = listOf(
                DialogueLine("العميد حسام", "ألو! أيوة يا فندم، معاك العميد حسام من شرطة الأطفال والنجدة.", 4),
                DialogueLine("العميد حسام", "إيه ده يا كابتن؟ سامع صوت عياط وزعيق وعناد ليه كده؟! ده سلوك ما يصحش أبداً!", 5),
                DialogueLine("العميد حسام", "الشطار والمحترمين بيتكلموا بهدوء ويسمعوا كلام ماما فوراً من غير عناد.", 5),
                DialogueLine("العميد حسام", "أنا جهزت عربية البوليس بالسارينة، لو ما هديتش حالا هاجي أخدك على القسم!", 5),
                DialogueLine("العميد حسام", "يلا اعتذر لماما وبوس راسها واسمع الكلام علشان نفضل أصحاب.. فاهم؟ مع السلامة.", 5)
            )
        ),
        PoliceScenario(
            id = "egypt_good_study",
            title = "شاطر وذاكر دروسه (مصري)",
            dialect = Dialect.EGYPTIAN,
            category = CallCategory.GOOD_BEHAVIOR,
            isReward = true,
            callerName = "المقدم طارق",
            callerRank = "وحدة تكريم الشطار",
            dialogues = listOf(
                DialogueLine("المقدم طارق", "مساء الورد والياسمين! معاك المقدم طارق من شرطة الأطفال.", 4),
                DialogueLine("المقدم طارق", "جاي أهنيك يا دكتور المستقبل علشان شاطر وبتكتب واجباتك وبتسمع الكلام!", 5),
                DialogueLine("المقدم طارق", "كل الظباط في القسم بيحيوك وبيسقفوا لك عشان إنت قدوة لكل أصحابك!", 5),
                DialogueLine("المقدم طارق", "لك شهادة تقدير وهدية ممتازة.. خليك دايماً نجم وناجح ومحبوب!", 4)
            )
        ),
        PoliceScenario(
            id = "egypt_bad_teeth",
            title = "مش راضي يغسل سنانه (مصري)",
            dialect = Dialect.EGYPTIAN,
            category = CallCategory.TEETH,
            isReward = false,
            callerName = "العميد حسام",
            callerRank = "قسم الصحة المدرسية",
            dialogues = listOf(
                DialogueLine("العميد حسام", "ألو.. مين اللي مكسل يغسل سنانه بالفرشاة والمعجون ده؟", 4),
                DialogueLine("العميد حسام", "السوسة الشريرة مستنية تهجم على أسنانك لو ما غسلتهاش دلوقتي حالا!", 5),
                DialogueLine("العميد حسام", "يلا قوم امسك الفرشاة وغسل كل أسنانك كويس علشان تكون ابتسامتك جميلة وقوية!", 5),
                DialogueLine("العميد حسام", "هتابع مع ماما بعد دقيقتين وأشوف غسلتها ولا لأ!", 3)
            )
        ),

        // Syrian Scenarios
        PoliceScenario(
            id = "syrian_bad_toys",
            title = "ما بيرتب ألعابه (شامي)",
            dialect = Dialect.SYRIAN,
            category = CallCategory.CLEANING,
            isReward = false,
            callerName = "الضابط سامر",
            callerRank = "مركز شرطة الأطفال العام",
            dialogues = listOf(
                DialogueLine("الضابط سامر", "مرحبا حبيبي! معك الضابط سامر من شرطة الأطفال.", 4),
                DialogueLine("الضابط سامر", "عم يخبروني إنك تارك ألعابك بالأرض وما عم تساعد ماما بترتيب غرفتك؟", 5),
                DialogueLine("الضابط سامر", "البطل الشاطر والمرتب لازم يضب ألعابه كلها بصندوق الألعاب بعد ما يخلص لعب!", 5),
                DialogueLine("الضابط سامر", "يلا هلأ بسرعة بدنا الغرفة تلمع وتصير أحلى غرفة.. رح اتصل اتأكد بعد شوي، ماشي؟", 5)
            )
        ),
        PoliceScenario(
            id = "syrian_good_eating",
            title = "أكل وجبته كاملة (شامي)",
            dialect = Dialect.SYRIAN,
            category = CallCategory.GOOD_BEHAVIOR,
            isReward = true,
            callerName = "الملازم عمر",
            callerRank = "دورية الأبطال الصغار",
            dialogues = listOf(
                DialogueLine("الملازم عمر", "أهلين بالبطل الحلو! معك الملازم عمر من شرطة الأطفال.", 4),
                DialogueLine("الملازم عمر", "سمعت خبرية كتير حلوة إنك أكلت صحنك كله ومبسوط ومطيع!", 5),
                DialogueLine("الملازم عمر", "صحة وهنا يا رب! هيك بتصير قوي وصحتك ممتازة وبتتفوق بالمدرسة.", 5),
                DialogueLine("الملازم عمر", "عم نبعتلك تحية كبيرة ونجمة ذهبية لغرفتك. يسلم هالإيدين!", 4)
            )
        ),

        // Moroccan Scenarios
        PoliceScenario(
            id = "moroccan_bad_disobedient",
            title = "ما كيسمعش الهضرة (مغربي)",
            dialect = Dialect.MOROCCAN,
            category = CallCategory.DISOBEDIENT,
            isReward = false,
            callerName = "الكوميسير مراد",
            callerRank = "فرقة حماية الطفولة",
            dialogues = listOf(
                DialogueLine("الكوميسير مراد", "ألو السلام عليكم، معاك الكوميسير مراد من شرطة الأطفال والشباب.", 4),
                DialogueLine("الكوميسير مراد", "علاش ما باغي تسمع كلام والديك؟ الصداع والبسالة ما خداماش أصاحبي!", 5),
                DialogueLine("الكوميسير مراد", "الدري المزيان خاصو يكون ضريف ومأدب ويسمع الهضرة من أول مرة.", 5),
                DialogueLine("الكوميسير مراد", "دابا دير عقلك واسمع للماما والبابا باش نجيبو ليك كادو زوين. تهلا!", 5)
            )
        ),

        // Iraqi Scenarios
        PoliceScenario(
            id = "iraqi_bad_screaming",
            title = "يبجي ويعاند (عراقي)",
            dialect = Dialect.IRAQI,
            category = CallCategory.SCREAMING,
            isReward = false,
            callerName = "المقدم علي",
            callerRank = "مديرية شرطة الأطفال",
            dialogues = listOf(
                DialogueLine("المقدم علي", "هلو عيني.. وياك المقدم علي من شرطة الأطفال والأبطال.", 4),
                DialogueLine("المقدم علي", "شنو هاي السالفة؟ ليش تبجي وتصرخ وتزعل أمك وأبوك؟ البطل ما يعاند هيج!", 5),
                DialogueLine("المقدم علي", "أريدك تصير سبع ومؤدب وتمسح دموعك وتصير خوش ولد وتسمع الحجي.", 5),
                DialogueLine("المقدم علي", "يلا حباب.. صالح ماما وبوسها وراح نسجلك ويانا بطل من أبطال الشرطة. فدوى لعينك!", 5)
            )
        ),

        // Classical Scenarios
        PoliceScenario(
            id = "fusha_studying",
            title = "المذاكرة والاجتهاد (فصحى)",
            dialect = Dialect.CLASSICAL,
            category = CallCategory.STUDYING,
            isReward = true,
            callerName = "القائد منصور",
            callerRank = "قيادة أبطال المستقبل",
            dialogues = listOf(
                DialogueLine("القائد منصور", "السلام عليكم ورحمة الله، معك القائد منصور من القيادة العامة لشرطة الأطفال.", 5),
                DialogueLine("القائد منصور", "أحييك أيها الطالب النجيب على مثابرتك واجتهادك وإنجازك لمهامك الدراسية.", 5),
                DialogueLine("القائد منصور", "إن العلم والنظام هما سلاح المستقبل لبناء الوطن وحماية المجتمع.", 5),
                DialogueLine("القائد منصور", "نمنحك وسام التميز الدراسي، ونتمنى لك دوام التفوق والنجاح الباهر!", 5)
            )
        )
    )

    val sounds: List<PoliceSound> = listOf(
        PoliceSound("snd_siren1", "سارينة الشرطة الكلاسيكية", "صوت دورية الطوارئ العادية", "🚨", SoundType.SIREN_CLASSIC),
        PoliceSound("snd_siren2", "سارينة التدخل السريع", "صوت عالي للعمليات الطارئة", "🚓", SoundType.SIREN_FAST),
        PoliceSound("snd_siren3", "سارينة النجدة المتقطعة", "صوت مميز لفتح الطريق", "🚨", SoundType.SIREN_WAIL),
        PoliceSound("snd_radio", "نداء اللاسلكي العملياتي", "10-4 استلمنا البلاغ يا دورية", "📻", SoundType.RADIO_BEEP),
        PoliceSound("snd_walkie", "جهاز الووكي توكي", "صوت طقطقة اللاسلكي للشرطة", "📡", SoundType.WALKIE_TALKIE),
        PoliceSound("snd_horn", "بوري سيارة الشرطة", "تنبيه إخلاء الطريق", "📢", SoundType.HORN),
        PoliceSound("snd_whistle", "صفارة ضابط المرور", "إشارة توقف أو انتباه", "👮‍♂️", SoundType.POLICE_WHISTLE),
        PoliceSound("snd_heli", "طائرة مروحية الشرطة", "صوت رادارات المروحية الجوية", "🚁", SoundType.HELI_SOUND)
    )

    val initialMissions: List<EducationalMission> = listOf(
        EducationalMission("m1", "أكل طبق الخضار والفاكهة كاملاً", "تناول وجبة صحية ومفيدة لتقوية الجسم", 50, "صحة"),
        EducationalMission("m2", "ترتيب الألعاب في الصندوق", "إعادة جميع الألعاب بعد الانتهاء من اللعب", 40, "نظام"),
        EducationalMission("m3", "غسيل الأسنان صباحاً ومساءً", "تنظيف الأسنان بالفرشاة لمدة دقيقتين", 30, "نظافة"),
        EducationalMission("m4", "قول الصدق وعدم الكذب أبداً", "التحدث بالحقيقة دائماً مثل أبطال الشرطة", 60, "أخلاق"),
        EducationalMission("m5", "النوم المبكر الساعة 8:30", "الذهاب للسرير مبكراً للنشاط والحيوية", 45, "نوم"),
        EducationalMission("m6", "مساعدة ماما في ترتيب السفرة", "مساعدة الأسرة والتعاون في المنزل", 50, "تعاون"),
        EducationalMission("m7", "حل الواجبات المدرسية بإتقان", "إنهاء المهام المدرسية دون تأجيل", 70, "دراسة")
    )
}
