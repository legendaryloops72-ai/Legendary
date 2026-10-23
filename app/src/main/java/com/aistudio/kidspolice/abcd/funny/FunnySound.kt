package com.aistudio.kidspolice.abcd.funny

import androidx.annotation.RawRes
import androidx.compose.ui.graphics.Color
import com.aistudio.kidspolice.abcd.R

enum class FunnyCategory(val title: String, val emoji: String, val color: Color) {
    ALL("الكل", "🌟", Color(0xFFFF5722)),
    CAMERA("أصوات الكاميرا", "📸", Color(0xFF00BCD4)),
    LAUGHTER("الضحكات", "😂", Color(0xFFFFB300)),
    PRANKS("المقالب", "🤪", Color(0xFFE91E63)),
    HUMAN("أصوات بشرية", "🗣️", Color(0xFF4CAF50)),
    CARTOON("شخصيات كرتونية", "👾", Color(0xFF9C27B0))
}

data class FunnySound(
    val id: String,
    val title: String,
    val category: FunnyCategory,
    val emojiIcon: String,
    @RawRes val soundRes: Int,
    val accentColor: Color,
    val description: String,
    val licenseNotice: String
)

object FunnySoundsRepository {
    val sounds: List<FunnySound> = listOf(
        // 1. أصوات الكاميرا
        FunnySound(
            id = "cam_shutter",
            title = "غالق الكاميرا",
            category = FunnyCategory.CAMERA,
            emojiIcon = "📷",
            soundRes = R.raw.funny_cam_shutter,
            accentColor = Color(0xFF00ACC1),
            description = "نقرة الغالق السريعة مع التقاط صورة تذكارية",
            licenseNotice = "مؤثر ميكانيكي أصلي مولد حسابياً ومحفوظ محلياً بصيغة WAV معفى من حقوق الملكية."
        ),
        FunnySound(
            id = "cam_timer",
            title = "مؤقت التصوير",
            category = FunnyCategory.CAMERA,
            emojiIcon = "⏱️",
            soundRes = R.raw.funny_cam_timer,
            accentColor = Color(0xFF26C6DA),
            description = "عد تنازلي بيبات متسارعة مع صفارة الالتقاط",
            licenseNotice = "نغمات ترددية مركبة أصلية مولدة محلياً ومرخصة للاستخدام التجاري وإعادة التوزيع."
        ),
        FunnySound(
            id = "cam_flash",
            title = "فلاش الكاميرا",
            category = FunnyCategory.CAMERA,
            emojiIcon = "⚡",
            soundRes = R.raw.funny_cam_flash,
            accentColor = Color(0xFF80DEEA),
            description = "صوت شحن وتفريغ فلاش الضوء الكرتوني",
            licenseNotice = "مؤثر صوتي تركيبي أصلي ومولد رياضياً وخالي من أي قيود ملكية فكرية."
        ),

        // 2. الضحكات
        FunnySound(
            id = "laugh_continuous",
            title = "ضحك متواصل",
            category = FunnyCategory.LAUGHTER,
            emojiIcon = "🤣",
            soundRes = R.raw.funny_laugh_continuous,
            accentColor = Color(0xFFFFB300),
            description = "قهقهات طفولية متتالية تنشر البهجة والمرح",
            licenseNotice = "أصوات ضحك توافقية أصلية مولدة إلكترونياً ومحفوظة محلياً بالكامل."
        ),
        FunnySound(
            id = "laugh_chuckle",
            title = "قهقهة خفيفة",
            category = FunnyCategory.LAUGHTER,
            emojiIcon = "🤭",
            soundRes = R.raw.funny_laugh_chuckle,
            accentColor = Color(0xFFFFCA28),
            description = "ضحكة قصيرة ناعمة تدعو للمرح والابتسام",
            licenseNotice = "مؤثر صوتي أصلي ومصمم برمجياً للمشروع ومرخص للاستخدام التجاري."
        ),
        FunnySound(
            id = "laugh_surprise",
            title = "ضحكة مفاجأة",
            category = FunnyCategory.LAUGHTER,
            emojiIcon = "😮",
            soundRes = R.raw.funny_laugh_surprise,
            accentColor = Color(0xFFFFE082),
            description = "شهقة دهشة سريعة تعقبها ضحكة مباغتة لطيفة",
            licenseNotice = "توليد صوتي أصلي تركيبي وخالي من أي حقوق ملكية خارجية."
        ),

        // 3. المقالب
        FunnySound(
            id = "prank_duck",
            title = "صوت البطة",
            category = FunnyCategory.PRANKS,
            emojiIcon = "🦆",
            soundRes = R.raw.funny_prank_duck,
            accentColor = Color(0xFFFB8C00),
            description = "كواك كواك كرتوني مضحك للمقالب البريئة",
            licenseNotice = "صوت بطة تركيبي رقمي أصلي مصمم للمشروع."
        ),
        FunnySound(
            id = "prank_sneeze",
            title = "عطسة كوميدية",
            category = FunnyCategory.PRANKS,
            emojiIcon = "🤧",
            soundRes = R.raw.funny_prank_sneeze,
            accentColor = Color(0xFFFF7043),
            description = "عطسة مسرحية مضحكة بصوت آآآتشووو",
            licenseNotice = "مؤثر صوتي أصلي مولد رياضياً ومحفوظ محلياً."
        ),
        FunnySound(
            id = "prank_snore",
            title = "شخير مضحك",
            category = FunnyCategory.PRANKS,
            emojiIcon = "😴",
            soundRes = R.raw.funny_prank_snore,
            accentColor = Color(0xFF8D6E63),
            description = "شخير نوم كرتوني ممتع للمداعبة الودودة",
            licenseNotice = "مؤثر صوتي أصلي ومجاني للاستخدام التجاري."
        ),
        FunnySound(
            id = "prank_spring",
            title = "صوت زنبرك",
            category = FunnyCategory.PRANKS,
            emojiIcon = "🌀",
            soundRes = R.raw.funny_prank_spring,
            accentColor = Color(0xFFAB47BC),
            description = "قفزة الزنبرك الكرتونية الشهيرة (بويينغ!)",
            licenseNotice = "مؤثر صوتي فيزيائي مولد حسابياً ومحفوظ محلياً."
        ),
        FunnySound(
            id = "prank_fall",
            title = "سقوط كوميدي",
            category = FunnyCategory.PRANKS,
            emojiIcon = "🍌",
            soundRes = R.raw.funny_prank_fall,
            accentColor = Color(0xFFEF5350),
            description = "صوت زحليقة وانزلاق مضحك مع ارتطام ناعم",
            licenseNotice = "توليد صوتي أصلي رقمي ومتاح لإعادة التوزيع."
        ),
        FunnySound(
            id = "prank_horn",
            title = "بوق الغازات الكرتوني",
            category = FunnyCategory.PRANKS,
            emojiIcon = "💨",
            soundRes = R.raw.funny_prank_horn,
            accentColor = Color(0xFFD4E157),
            description = "بوق كرتوني مرح للمقالب المسلية دون إزعاج",
            licenseNotice = "نغمة هواء كرتونية تركيبية آمنة وأصلية بالكامل."
        ),

        // 4. الأصوات البشرية المضحكة
        FunnySound(
            id = "human_wow",
            title = "صيحة دهشة",
            category = FunnyCategory.HUMAN,
            emojiIcon = "🤩",
            soundRes = R.raw.funny_human_wow,
            accentColor = Color(0xFF42A5F5),
            description = "صوت وااااو مبهج يعبر عن الإعجاب والتصفيق",
            licenseNotice = "مؤثر نغمي أصلي مولد محلياً داخل التطبيق."
        ),
        FunnySound(
            id = "human_applause",
            title = "تصفيق حار",
            category = FunnyCategory.HUMAN,
            emojiIcon = "👏",
            soundRes = R.raw.funny_human_applause,
            accentColor = Color(0xFF66BB6A),
            description = "تصفيق أطفال تشجيعي مفعم بالحماس",
            licenseNotice = "محاكاة صوتية رقمية أصلية لضربات التصفيق المتتالية."
        ),
        FunnySound(
            id = "human_yawn",
            title = "تثاؤب طريف",
            category = FunnyCategory.HUMAN,
            emojiIcon = "🥱",
            soundRes = R.raw.funny_human_yawn,
            accentColor = Color(0xFF90A4AE),
            description = "تثاؤب كسول كوميدي يدعو للاسترخاء والضحك",
            licenseNotice = "مؤثر صوتي أصلي مرخص للاستخدام وإعادة التوزيع."
        ),
        FunnySound(
            id = "human_monkey",
            title = "تقليد القرد",
            category = FunnyCategory.HUMAN,
            emojiIcon = "🐵",
            soundRes = R.raw.funny_human_monkey,
            accentColor = Color(0xFF8D6E63),
            description = "تقليد قرد كرتوني سريع ومرح (أو أو آ آ)",
            licenseNotice = "توليد نغمي تركيبي أصلي خالي من حقوق الملكية."
        ),

        // 5. أصوات الشخصيات الكرتونية الأصلية
        FunnySound(
            id = "toon_sponge",
            title = "سبونجي بوبو الصديق",
            category = FunnyCategory.CARTOON,
            emojiIcon = "🧽",
            soundRes = R.raw.funny_toon_sponge,
            accentColor = Color(0xFFFFEB3B),
            description = "شخصية إسفنجية أصلية تطلق فقاعات بحرية سعيدة (بديل كرتوني أصلي آمن)",
            licenseNotice = "شخصية ومؤثرات أصلية بديلة غير مقلدة وخالية تماماً من أي تسجيلات محمية بحقوق النشر."
        ),
        FunnySound(
            id = "toon_bear",
            title = "الدب المحبوب وماشا",
            category = FunnyCategory.CARTOON,
            emojiIcon = "🐻",
            soundRes = R.raw.funny_toon_bear,
            accentColor = Color(0xFF795548),
            description = "نغمة كرتونية دافئة مستوحاة من مغامرات الدب اللطيف في الغابة (أصلية بالكامل)",
            licenseNotice = "مؤثر نغمي كرتوني أصلي مؤلف برمجياً ومجاني للاستخدام التجاري دون تقليد أي ممثل."
        ),
        FunnySound(
            id = "toon_cop",
            title = "الضابط بندق المضحك",
            category = FunnyCategory.CARTOON,
            emojiIcon = "👮‍♂️",
            soundRes = R.raw.funny_toon_cop,
            accentColor = Color(0xFF3F51B5),
            description = "شرطي كرتوني مرح بصافرة إرشادية مضحكة للمغامرات",
            licenseNotice = "تسجيل نغمي تركيبي أصلي خاص بتطبيق شرطة الأطفال."
        ),
        FunnySound(
            id = "toon_speedy",
            title = "فهود النفاث السريع",
            category = FunnyCategory.CARTOON,
            emojiIcon = "⚡",
            soundRes = R.raw.funny_toon_speedy,
            accentColor = Color(0xFFFF5722),
            description = "انطلاقة كرتونية نفاثة سريعة مليئة بالحماس",
            licenseNotice = "مؤثر صوتي رقمي أصلي مصمم للمشروع ومحفوظ محلياً."
        )
    )
}
