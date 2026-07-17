package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sound.CallSoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 🔊 Data Model for Sound Items
data class SoundItem(
    val id: Int,
    val name: String,
    val emoji: String,
    val category: String, // "animals", "vehicles", "heroes", "tools", "nature", "funny"
    val description: String,
    val synthType: String,
    val gradientColors: List<Color>,
    val starsReward: Int = 3
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundsUniverseScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val callSoundManager = remember { CallSoundManager(context) }

    // Clean up sounds when leaving
    DisposableEffect(Unit) {
        onDispose {
            callSoundManager.release()
        }
    }

    // List of Categories
    val categories = listOf(
        "animals" to ("أصوات الحيوانات 🐶" to Color(0xFF10B981)),
        "vehicles" to ("أصوات المركبات 🚗" to Color(0xFF3B82F6)),
        "sirens" to ("صفارات الإنذار 🚨" to Color(0xFFEF4444)),
        "heroes" to ("أصوات الأبطال 🦸" to Color(0xFF8B5CF6)),
        "tools" to ("أصوات الأدوات ⚡" to Color(0xFFF59E0B)),
        "nature" to ("أصوات الطبيعة 🌧️" to Color(0xFF06B6D4)),
        "funny" to ("أصوات مضحكة 😂" to Color(0xFFEC4899))
    )

    var selectedCategory by remember { mutableStateOf("animals") }
    var discoveredSoundIds by remember { mutableStateOf(setOf<Int>()) }
    var activePlayingId by remember { mutableStateOf<Int?>(null) }
    var showStarsAwardedDialog by remember { mutableStateOf(false) }
    var dynamicStarsCount by remember { mutableStateOf(0) }

    // Retrieve child name from DB safely
    val childProfileState by viewModel.profile.collectAsState()
    val childName = childProfileState?.name ?: "البطل الصغير"

    // 🌟 Define the comprehensive 50 animals as requested
    val allSounds = remember {
        val lists = mutableListOf<SoundItem>()

        // 1. Animals (50 items)
        val animalData = listOf(
            Triple("قطة", "🐱", "مواء قطة لطيفة وودودة"),
            Triple("كلب", "🐶", "نباح كلب حارس مخلص"),
            Triple("أسد", "🦁", "زئير ملك الغابة القوي والمثقّف"),
            Triple("نمر", "🐯", "زئير النمر البطل السريع"),
            Triple("فيل", "🐘", "صوت الفيل ذو الخرطوم الطويل والمرح"),
            Triple("حصان", "🐴", "صهيل الحصان العربي الأصيل"),
            Triple("بقرة", "🐮", "خوار البقرة اللطيفة المعطاءة"),
            Triple("خروف", "🐑", "ثغاء الخروف الصغير الوديع"),
            Triple("عصفور", "🐦", "زقزقة العصفور الصغير الجميل"),
            Triple("بطة", "🦆", "صوت البطة الراقصة في البحيرة"),
            Triple("قرد", "🐵", "صوت القرد الذكي الضاحك"),
            Triple("ذئب", "🐺", "عواء الذئب القوي في الجبل"),
            Triple("ديك", "🐓", "صياح الديك النشيط في الصباح الباكر"),
            Triple("ضفدع", "🐸", "نقيق الضفدع القافز المرح"),
            Triple("بطريق", "🐧", "صوت البطريق الراقص على الجليد"),
            Triple("نحلة", "🐝", "طنين النحلة النشيطة صانعة العسل"),
            Triple("بومة", "🦉", "نباح بومة الحكمة والسهر اللطيفة"),
            Triple("كنغر", "🦘", "صوت الكنغر القفاز السريع"),
            Triple("حوت", "🐋", "أهازيج الحوت العملاق في المحيط"),
            Triple("أرنب", "🐰", "صوت الأرنب السريع محب الجزر"),
            Triple("ماعز", "🐐", "ثغاء الماعز الصغير المتسلق للاشجار"),
            Triple("دب", "🐻", "صوت الدب الكبير اللطيف الكسول"),
            Triple("باندا", "🐼", "مرح الباندا الكيوت محب الخيزران"),
            Triple("ثعلب", "🦊", "عواء الثعلب الذكي السريع"),
            Triple("كوالا", "🐨", "صوت الكوالا الهادئ الرائع"),
            Triple("فأر", "🐭", "صوت الفأر الصغير الودود"),
            Triple("كتكوت", "🐥", "صوصوة الكتكوت الأصفر الجميل"),
            Triple("زرافة", "🦒", "صوت الزرافة الطويلة واللطيفة"),
            Triple("حمار وحشي", "🦓", "صوت الحمار الوحشي المخطط الفريد والجميل"),
            Triple("جمل", "🐫", "رغاء الجمل سفينة الصحراء الصبور"),
            Triple("قنفذ", "🦔", "صوت القنفذ الصغير الخجول اللطيف"),
            Triple("سنجاب", "🐿️", "صوت السنجاب السريع محب البندق"),
            Triple("فراشة", "🦋", "رفرفة أجنحة الفراشة الملونة الزاهية"),
            Triple("سرطان البحر", "🦀", "صوت سرطان البحر ذو المقصات الودية"),
            Triple("فهد", "🐆", "زئير الفهد الأسرع في البرية"),
            Triple("غزال", "🦌", "صوت الغزال الجميل الرقيق"),
            Triple("أخطبوط", "🐙", "مرح الأخطبوط الصديق ذي الأذرع الثمانية"),
            Triple("حلزون", "🐌", "مشي الحلزون الهادئ الصبور"),
            Triple("خفاش", "🦇", "صوت الخفاش بطل الليل الطائر"),
            Triple("خرتيت", "🦏", "هدير الخرتيت القوي ذي القرن المميز"),
            Triple("سيد قشطة", "🦛", "صوت فرس النهر الضخم والمرح"),
            Triple("حمار", "🫏", "صوت الحمار الأليف الصبور النشيط"),
            Triple("فلامنجو", "🦩", "صوت الفلامنجو ذي الألوان الوردية البديعة"),
            Triple("ببغاء", "🦜", "تقليد الببغاء الذكي الملون الرائع للكلمات"),
            Triple("نسر", "🦅", "صيحة النسر الجارح القوي في السماء"),
            Triple("دلفين", "🐬", "صفير الدلفين الذكي اللطيف صديق الإنسان"),
            Triple("غراب", "🐦‍⬛", "صوت الغراب الذكي المنظم"),
            Triple("تمساح", "🐊", "صوت التمساح الكبير الهادئ في البركة"),
            Triple("ثعبان", "🐍", "حفيف الثعبان الصغير الملون السريع"),
            Triple("ديك رومي", "🦃", "قبقبة الديك الرومي ذو الريش الجميل المفروش"),
            Triple("دجاجة", "🐔", "صوت الدجاجة اللطيفة في المزرعة"),
            Triple("خنزير", "🐷", "صوت الخنزير المرح يحب اللعب في الطين"),
            Triple("وحيد القرن السحري", "🦄", "صهيل وحيد القرن السحري المحب لقوس قزح والنجوم اللامعة"),
            Triple("تنين طائر", "🐲", "زئير التنين الطائر اللطيف صديق المغامرين في الغيوم"),
            Triple("ديناصور عملاق", "🦖", "صوت الديناصور الخارق العملاق حامي الغابات القديمة"),
            Triple("هريرة صغيرة", "🐈‍⬛", "مواء هريرة صغيرة تبحث عن اللعب والدغدغة والمرح"),
            Triple("جرو صغير", "🐕", "نباح جرو صغير مبهج ومحب للركض وإحضار الكرة اللامعة"),
            Triple("سلحفاة حكيمة", "🐢", "صوت السلحفاة الحكيمة الهادئة تسرد القصص تحت أشعة الشمس"),
            Triple("هامستر كيوت", "🐹", "صوت هامستر صغير لطيف يدور في عجلته الملونة بنشاط"),
        )

        var idCounter = 1
        animalData.forEach { (name, emoji, desc) ->
            val synthType = when (name) {
                "أسد", "نمر", "فهد" -> "lion"
                "كلب" -> "dog"
                "عصفور", "كتكوت", "ببغاء" -> "bird"
                "قطة" -> "cat"
                else -> "general"
            }
            lists.add(
                SoundItem(
                    id = idCounter++,
                    name = name,
                    emoji = emoji,
                    category = "animals",
                    description = desc,
                    synthType = synthType,
                    gradientColors = listOf(Color(0xFFE6F4EA), Color(0xFFA3E635))
                )
            )
        }

        // 2. Vehicles (10 items)
        val vehicleData = listOf(
            Triple("سيارة رياضية", "🏎️", "هدير محرك السيارة الرياضية السريعة اللامعة"),
            Triple("سيارة شرطة", "🚓", "صوت دورية شرطة الأطفال السريعة المساعدة"),
            Triple("سيارة إسعاف", "🚑", "صوت سيارة الإسعاف المنقذة التي تساعد المرضى"),
            Triple("شاحنة كبيرة", "🚚", "صوت الشاحنة القوية التي تنقل الألعاب اللذيذة"),
            Triple("دراجة نارية", "🏍️", "صوت الدراجة النارية الشجاعة محبة السباقات"),
            Triple("قطار", "🚂", "توووت توووت! صوت القطار السريع عبر الجبال"),
            Triple("طائرة", "✈️", "أزيز الطائرة التي تحلق عالياً في الغيوم"),
            Triple("هليكوبتر", "🚁", "مراوح الهليكوبتر الذكية تحلق فوق المدن"),
            Triple("سفينة", "🚢", "صوت بوق السفينة الكبيرة تشق الأمواج الزرقاء"),
            Triple("صاروخ فضائي", "🚀", "صوت انطلاق الصاروخ الفضائي الخارق نحو النجوم"),
            Triple("سيارة", "🚗", "صوت محرك وبوق السيارة العادية"),
            Triple("سيارة إطفاء", "🚒", "صوت سيارة الإطفاء البطلة لإخماد الحرائق"),
            Triple("دراجة هوائية", "🚲", "ترن ترن! جرس الدراجة الهوائية السريعة"),
            Triple("جرار زراعي", "🚜", "صوت جرار المزرعة القوي والبطيء النشيط في الحقول"),
            Triple("حافلة مدرسية", "🚌", "صوت حافلة المدرسة ترحب بالأطفال السعداء صباحاً"),
            Triple("غواصة مائية", "🌊", "بلوب بلوب! صوت محرك الغواصة الكرتونية في أعماق المحيطات"),
            Triple("سيارة كرتونية", "🚗", "بييب بييب! صوت بوق سيارة الكرتون المرحة والملونة"),
            Triple("حوامة سريعة", "🛥️", "صوت قارب سريع يشق مياه البحيرة الهادئة بحماس"),
            Triple("منطاد طائر", "🎈", "صوت نفث الهواء الدافئ لتحليق المنطاد الملون فوق الجبال"),
        )
        vehicleData.forEach { (name, emoji, desc) ->
            val synthType = if (name.contains("شرطة") || name.contains("إسعاف")) "siren" else "car_or_sports"
            lists.add(
                SoundItem(
                    id = idCounter++,
                    name = name,
                    emoji = emoji,
                    category = "vehicles",
                    description = desc,
                    synthType = synthType,
                    gradientColors = listOf(Color(0xFFE0F2FE), Color(0xFF60A5FA))
                )
            )
        }

        // 3. Heroes (9 items)
        val heroData = listOf(
            Triple("طيران خارق", "🚀", "صوت الطيران السريع للبطل الخارق في الفضاء"),
            Triple("إطلاق طاقة", "💥", "إطلاق درع الطاقة السحري الملون لحماية الأبرياء"),
            Triple("قفزة قوية", "🦘", "قفزة البطل الخارق العالية فوق ناطحات السحاب"),
            Triple("سرعة البرق", "⚡", "سرعة البرق الخارقة التي تطوي المسافات"),
            Triple("درع طاقة", "🛡️", "تفعيل درع الحماية الفولاذي ضد الأشرار"),
            Triple("شعاع ليزر", "🔴", "إطلاق شعاع الليزر الأخضر الذكي الدقيق مرتين"),
            Triple("ضربة كرتونية", "🥊", "ضربة البطل الممتعة للقضاء على مصادر المشاغبة"),
            Triple("بوابة سحرية", "🌀", "فتح البوابة السحرية لنقل البطل لإنقاذ اليوم"),
            Triple("روبوت بطل", "🤖", "تفعيل روبوت البطل الحديدي المساعد الذكي وسماع أصواته اللطيفة"),
            Triple("بطل الجليد", "❄️", "إطلاق رذاذ الجليد السحري لتجميد الأشرار المشاغبين وحماية المدينة"),
            Triple("بطل النار", "🔥", "قوة النار الدافئة الصديقة لإنارة الممرات المظلمة ومساعدة الأصدقاء"),
            Triple("بطل الرياح", "💨", "توليد إعصار لطيف ملون لحمل ألعاب الأصدقاء إلى بر الأمان"),
            Triple("قبضة حديدية", "🤛", "صوت تفعيل القوة الفولاذية لقبضة البطل الحارس الشجاع"),
            Triple("تخاطر ذهني", "🧠", "رنين التخاطر الذهني لقراءة أفكار المساعدين اللطيفين للتنسيق البطل"),
        )
        heroData.forEach { (name, emoji, desc) ->
            val synthType = when (name) {
                "إطلاق طاقة", "شعاع ليزر" -> "laser"
                "بوابة سحرية" -> "portal"
                "سرعة البرق" -> "lightning"
                else -> "funny"
            }
            lists.add(
                SoundItem(
                    id = idCounter++,
                    name = name,
                    emoji = emoji,
                    category = "heroes",
                    description = desc,
                    synthType = synthType,
                    gradientColors = listOf(Color(0xFFF3E8FF), Color(0xFFC084FC))
                )
            )
        }

        // 4. Tools (8 items)
        val toolData = listOf(
            Triple("ماكينة حلاقة", "🪒", "طنين ماكينة الحلاقة اللطيفة لعمل تسريحة شعر رائعة للبطل"),
            Triple("صعق كهربائي", "⚡", "شرارات الصعق الكهربائي الكرتوني اللطيف الخفيف"),
            Triple("مكنسة كهربائية", "🧹", "صوت المكنسة الكهربائية النشطة تنظف البيت بذكاء"),
            Triple("خلاط كهربائي", "🌪️", "صوت الخلاط يمزج الفواكه اللذيذة ليصنع عصير الأبطال"),
            Triple("كاميرا تصوير", "📸", "كليك! التقاط صورة تذكارية مبهجة لابتسامة البطل"),
            Triple("جرس الباب", "🔔", "دنغ دونغ! جرس الباب يرحب بالزوار الكرام الطيبين"),
            Triple("آلة موسيقية", "🎹", "عزف نغمات رنانة جميلة تدخل السرور على القلوب"),
            Triple("صوت روبوت", "🤖", "بييب بووب! الروبوت المنزلي اللطيف يستجيب للأوامر"),
            Triple("منبه الطاولة", "⏰", "تيك تاك تيك تاك! رنين المنبه اللطيف للاستيقاظ بنشاط وبدء يوم جميل"),
            Triple("غلاية الماء", "🫖", "ففففف! صفير غلاية الشاي الساخن واللذيذ لتحضير فطور الأبطال"),
            Triple("مطركة الخشب", "🔨", "طق طق طق! نقر مطرقة النجار لإصلاح وتركيب الألعاب الخشبية الجميلة"),
            Triple("مقص الأوراق", "✂️", "قص قص قص! صوت مقص أوراق الرسم الملونة لقص الأشكال الكرتونية"),
            Triple("مجفف الشعر", "💨", "صوت دافئ لطيف لتجفيف شعر البطل الصغير بعد الاستحمام بالماء العذب"),
        )
        toolData.forEach { (name, emoji, desc) ->
            val synthType = when (name) {
                "ماكينة حلاقة" -> "vacuum"
                "مكنسة كهربائية" -> "vacuum"
                "صعق كهربائي" -> "lightning"
                "جرس الباب" -> "bell"
                "صوت روبوت" -> "funny"
                else -> "general"
            }
            lists.add(
                SoundItem(
                    id = idCounter++,
                    name = name,
                    emoji = emoji,
                    category = "tools",
                    description = desc,
                    synthType = synthType,
                    gradientColors = listOf(Color(0xFFFEF3C7), Color(0xFFFBBF24))
                )
            )
        }

        // 5. Nature (5 items)
        val natureData = listOf(
            Triple("صوت المطر", "🌧️", "حبات المطر اللطيفة تروي الزهور العطرة وترسم الابتسامة"),
            Triple("صوت الرياح", "💨", "نسيم الرياح الهادئة تلاعب أوراق الأشجار الخضراء"),
            Triple("صوت الرعد", "⚡", "صوت الرعد البعيد في السماء يبشر بالخير والبركة"),
            Triple("صوت النهر", "🌊", "خرير مياه النهر العذبة الجارية بصوت يبعث على الارتياح"),
            Triple("صوت أمواج البحر", "🏖️", "تلاطم أمواج البحر الهادف مع الصخور الشاطئية الذهبية"),
            Triple("تغريد العصافير", "🐦", "أجمل ألحان العصافير المغردة في الصباح الباكر في الغابات الخضراء"),
            Triple("حفيف أوراق الشجر", "🍃", "صوت حركة أوراق الشجر الخضراء اللطيف والمنعش مع نسيم الهواء الصافي"),
            Triple("طقطقة النار", "🔥", "صوت طقطقة حطب مخيم الكشافة الدافئ والممتع تحت النجوم اللامعة"),
            Triple("صوت شلال المياه", "⛰️", "صوت تدفق مياه الشلال العذبة المنهمرة من أعلى الجبال الشاهقة"),
            Triple("صوت غابة استوائية", "🌴", "مزيج رائع من أصوات طيور وحيوانات الغابة السعيدة والقرود المرحة"),
        )
        natureData.forEach { (name, emoji, desc) ->
            lists.add(
                SoundItem(
                    id = idCounter++,
                    name = name,
                    emoji = emoji,
                    category = "nature",
                    description = desc,
                    synthType = "general",
                    gradientColors = listOf(Color(0xFFECFDF5), Color(0xFF2DD4BF))
                )
            )
        }

        // 6. Funny (5 items)
        val funnyData = listOf(
            Triple("ضحكة كرتونية", "😂", "ضحكات مضحكة جدا تسعد الأبطال وتزرع البهجة"),
            Triple("زحلقة كوميدية", "🍌", "أصوات انزلاق كوميدي مضحك من الرسوم المتحركة"),
            Triple("قفزة يمبروك", "🦘", "صوت قفزة كرتونية مطاطية رنانة مع صدى صوت مميز"),
            Triple("صوت كائن فضائي", "👽", "بييب بييب! فضائي لطيف من كوكب الحلوى يرحب بنا"),
            Triple("بالونة تنفجر", "🎈", "بوب! صوت انفجار بالونة الحفلة الملونة بالألوان الزاهية"),
            Triple("العطس الكوميدي", "🤧", "أتشوووووو! عطسة كرتونية مضحكة جداً تفجر الضحكات في البيت"),
            Triple("مضغ فقع الفقاعة", "🫧", "شومب شومب.. بوب! مضغ علكة الفواكه وفقع فقاعة سحرية مبهجة"),
            Triple("شخير مضحك", "😴", "خخخخ بييييه.. شخير كوميدي نائم مضحك ومسلٍ للأطفال أثناء النوم"),
            Triple("بوق المهرج", "🤡", "طوط طوط! ضغط بوق مهرج السيرك الملون لإسعاد الجميع ورسم الابتسامة"),
            Triple("تثاؤب كسول", "🥱", "هوووااااه! تثاؤب كارتوني طويل ومضحك يعبر عن النعاس والرغبة بالنوم الهادئ"),
            Triple("ضحكة الساحرة الشريرة", "🧙‍♀️", "كيه هيه هيه هيه! ضحكة الساحرة الكرتونية المضحكة واللطيفة في قصص الخيال"),
        )
        funnyData.forEach { (name, emoji, desc) ->
            lists.add(
                SoundItem(
                    id = idCounter++,
                    name = name,
                    emoji = emoji,
                    category = "funny",
                    description = desc,
                    synthType = "funny",
                    gradientColors = listOf(Color(0xFFFCE7F3), Color(0xFFF472B6))
                )
            )
        }

        // 7. Sirens (8 items)
        val sirenData = listOf(
            Triple("صفارة إنذار الشرطة", "🚨", "صوت صفارة إنذار دورية الشرطة السريعة والشجاعة"),
            Triple("صفارة إنذار الإسعاف", "🚑", "صوت صفارة إسعاف الأبطال لإنقاذ المرضى بسرعة"),
            Triple("صفارة إنذار الإطفاء", "🚒", "صوت صفارة شاحنة الإطفاء القوية لإطفاء الحرائق ببطولة"),
            Triple("إنذار غارات جوية", "📢", "صوت إنذار الطوارئ الجوية لتحذير المواطنين وحمايتهم"),
            Triple("إنذار نووي خطير", "☣️", "صوت إنذار التحذير للمناطق الصناعية الحساسة"),
            Triple("بوق سفينة عملاقة", "🚢", "صوت بوق السفينة القوي لتنبيه السفن الأخرى في الضباب"),
            Triple("إنذار الحريق المنزلي", "🧯", "بييب بييب! جهاز إنذار الدخان للحفاظ على سلامة البيت"),
            Triple("إنذار الإخلاء السريع", "🏃‍♂️", "صوت صفارات الإخلاء الآمن والمنظم وقت الطوارئ"),
            Triple("إنذار الغواصة", "🚨", "صوت إنذار الغواصة الكرتونية تحت أعماق البحار والمحيطات المظلمة"),
            Triple("بوق القطار القديم", "🚂", "توووت توووت! صوت بوق القطار البخاري القديم القوي في المحطة"),
            Triple("إنذار سرقة السيارة", "🚘", "ويوو ويوو بييب بييب! إنذار السيارة الذكي لحمايتها من اللصوص والمشاغبين"),
            Triple("بوق الشاحنة الرياضية", "🚛", "بببببببببب! بوق هواء الشاحنة الضخمة القوي جداً في الطرق الطويلة"),
            Triple("جرس المدرسة القديم", "🔔", "رن رن رن! جرس المدرسة الكلاسيكي لبدء وقت الفسحة والمرح واللعب مع الأصدقاء"),
            Triple("إنذار الفضاء المثير", "🛸", "بييب بييب بييب! إنذار السفينة الفضائية عند الاقتراب من كوكب الألعاب السعيدة"),
        )
        sirenData.forEach { (name, emoji, desc) ->
            lists.add(
                SoundItem(
                    id = idCounter++,
                    name = name,
                    emoji = emoji,
                    category = "sirens",
                    description = desc,
                    synthType = "siren",
                    gradientColors = listOf(Color(0xFFFEE2E2), Color(0xFFF87171))
                )
            )
        }

        lists
    }

    // Filter sounds based on active category
    val currentSounds = remember(selectedCategory) {
        allSounds.filter { it.category == selectedCategory }
    }

    // Infinite float animation for welcoming card, wave animations, and glow effects
    val infiniteTransition = rememberInfiniteTransition(label = "sounds_universe_infinite")
    val waveHeight by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_height"
    )
    val floatStarsAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_stars_alpha"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "عالم الأصوات الممتعة 🎧",
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color(0xFF1E3A8A)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "للخلف",
                            tint = Color(0xFF1E3A8A)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF0F9FF)
                )
            )
        },
        containerColor = Color(0xFFF0F9FF) // Clean soft sky blue background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF0F9FF))
        ) {
            // 🌟 Top Banner: "شخصية ترحب بالطفل" + Progress status with modern glow values
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .graphicsLayer { translationY = waveHeight },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: 3D-Like headphone mascot with live soft neon background
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFDBEAFE), Color(0xFF93C5FD))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎧", fontSize = 38.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "مرحباً بك يا $childName! 👋",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E3A8A),
                            textAlign = TextAlign.Start
                        )
                        Text(
                            "اكتشف الأصوات واجمع النجوم الذهبية الرائعة!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Score progress
                        val totalCount = allSounds.size
                        val discoveredCount = discoveredSoundIds.size
                        val progress = if (totalCount > 0) discoveredCount.toFloat() / totalCount else 0f

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "تم اكتشاف: $discoveredCount / $totalCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3B82F6)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⭐", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    "نجومك: ${discoveredCount * 3}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }
                }
            }

            // 🏆 Medals or Badges Indicator if they reach certain discovery levels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val levels = listOf(
                    Triple(5, "🏅 مبتدئ", Color(0xFFCD7F32)), // Bronze
                    Triple(15, "🥈 خبير الأصوات", Color(0xFFC0C0C0)), // Silver
                    Triple(30, "🥇 مستكشف خارق", Color(0xFFFFD700)), // Gold
                    Triple(50, "👑 ملك الأصوات", Color(0xFFD4AF37)) // Platinum Crown
                )
                levels.forEach { (required, title, color) ->
                    val isUnlocked = discoveredSoundIds.size >= required
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isUnlocked) color.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                        border = BorderStroke(
                            width = 1.5.dp,
                            color = if (isUnlocked) color else Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) color else Color(0xFF94A3B8),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                        )
                    }
                }
            }

            // 🏷️ Category Selection Tab Row (Horizontal Scroll)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { (catId, pair) ->
                    val (title, color) = pair
                    val isSelected = selectedCategory == catId

                    val scaleValue = if (isSelected) 1.05f else 1f
                    val animatedScale by animateFloatAsState(
                        targetValue = scaleValue,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "tab_scale"
                    )

                    Button(
                        onClick = { selectedCategory = catId },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) color else Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = if (isSelected) 4.dp else 1.dp
                        ),
                        modifier = Modifier
                            .scale(animatedScale)
                            .border(
                                width = if (isSelected) 0.dp else 1.5.dp,
                                color = color.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            color = if (isSelected) Color.White else Color(0xFF334155),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 🎮 Central Sound Interactive Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(currentSounds, key = { it.id }) { sound ->
                    val isDiscovered = discoveredSoundIds.contains(sound.id)
                    val isPlaying = activePlayingId == sound.id

                    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()

                    val itemScale by animateFloatAsState(
                        targetValue = when {
                            isPlaying -> 1.08f
                            isPressed -> 0.94f
                            else -> 1.0f
                        },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                        label = "sound_card_bounce"
                    )

                    Card(
                        onClick = {
                            scope.launch {
                                // 🌟 Set playing status
                                activePlayingId = sound.id
                                
                                // Play beautiful, high-fidelity realistic sound imitation & commentary using dynamic TTS voices!
                                callSoundManager.playKidsRealisticSound(sound.name, sound.description)

                                // Trigger discovery & award stars immediately if new!
                                if (!discoveredSoundIds.contains(sound.id)) {
                                    discoveredSoundIds = discoveredSoundIds + sound.id
                                    viewModel.awardQuizStars(sound.starsReward)
                                    dynamicStarsCount = sound.starsReward
                                    showStarsAwardedDialog = true
                                }

                                delay(1200)
                                activePlayingId = null
                            }
                        },
                        interactionSource = interactionSource,
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(
                            width = if (isPlaying) 3.dp else 1.5.dp,
                            color = if (isPlaying) sound.gradientColors[1] else Color(0xFFE2E8F0)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlaying) 10.dp else 3.dp),
                        modifier = Modifier
                            .scale(itemScale)
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            sound.gradientColors[0].copy(alpha = 0.5f),
                                            Color.White
                                        )
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            // Discover star mark
                            if (isDiscovered) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .size(24.dp)
                                        .background(Color(0xFFFEF3C7), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("⭐", fontSize = 12.sp)
                                }
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Dynamic animated bouncing emoji
                                Text(
                                    text = sound.emoji,
                                    fontSize = if (isPlaying) 52.sp else 44.sp,
                                    modifier = Modifier
                                        .padding(vertical = 12.dp)
                                        .graphicsLayer {
                                            if (isPlaying) {
                                                translationY = waveHeight * 1.5f
                                            }
                                        }
                                )

                                Text(
                                    text = sound.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E293B),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (isDiscovered) "تم الاكتشاف! 🥳" else "صوت جديد ✨",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDiscovered) Color(0xFF10B981) else Color(0xFF3B82F6),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Sparkly play button
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isPlaying) {
                                                Brush.linearGradient(sound.gradientColors)
                                            } else {
                                                Brush.linearGradient(
                                                    listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))
                                                )
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = if (isPlaying) "🔊 يستمع..." else "▶️ تشغيل",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isPlaying) Color.White else Color(0xFF475569)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ⭐ Beautiful floating stars congratulations overlay when kids collect stars
    if (showStarsAwardedDialog) {
        AlertDialog(
            onDismissRequest = { showStarsAwardedDialog = false },
            confirmButton = {
                TextButton(onClick = { showStarsAwardedDialog = false }) {
                    Text("شكراً يا بطل! 😍", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                }
            },
            title = {
                Text(
                    "أحسنت صنعاً! 🎉",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E3A8A),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "🌟 🌟 🌟",
                        fontSize = 32.sp,
                        modifier = Modifier.graphicsLayer { alpha = floatStarsAlpha }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "لقد اكتشفت صوتاً رائعاً وحصلت على:\n+$dynamicStarsCount نجمة ذهبية!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569),
                        textAlign = TextAlign.Center
                    )
                }
            },
            shape = RoundedCornerShape(26.dp),
            containerColor = Color.White
        )
    }
}
