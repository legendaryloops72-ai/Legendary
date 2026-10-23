package com.aistudio.kidspolice.abcd.funny

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.ui.graphics.Color
import com.aistudio.kidspolice.abcd.R

enum class FunnyCategory(val title: String, @DrawableRes val iconRes: Int, val color: Color) {
    ALL("الكل", R.drawable.funny_photo_04, Color(0xFFFF5722)),
    CAMERA("أصوات الكاميرا", R.drawable.funny_photo_01, Color(0xFF00BCD4)),
    LAUGHTER("الضحكات", R.drawable.funny_photo_04, Color(0xFFFFB300)),
    PRANKS("المقالب", R.drawable.funny_photo_07, Color(0xFFE91E63)),
    HUMAN("أصوات بشرية", R.drawable.funny_photo_13, Color(0xFF4CAF50)),
    CARTOON("ألعاب ومؤثرات", R.drawable.funny_photo_17, Color(0xFF9C27B0))
}

data class FunnySound(
    val id: String,
    val title: String,
    val category: FunnyCategory,
    @DrawableRes val imageRes: Int,
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
            imageRes = R.drawable.funny_photo_01,
            soundRes = R.raw.funny_cam_shutter,
            accentColor = Color(0xFF00ACC1),
            description = "نقرة الغالق السريعة مع التقاط صورة تذكارية",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "cam_timer",
            title = "مؤقت التصوير",
            category = FunnyCategory.CAMERA,
            imageRes = R.drawable.funny_photo_02,
            soundRes = R.raw.funny_cam_timer,
            accentColor = Color(0xFF26C6DA),
            description = "عد تنازلي بيبات متسارعة مع صفارة الالتقاط",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "cam_flash",
            title = "فلاش الكاميرا",
            category = FunnyCategory.CAMERA,
            imageRes = R.drawable.funny_photo_03,
            soundRes = R.raw.funny_cam_flash,
            accentColor = Color(0xFF80DEEA),
            description = "صوت شحن وتفريغ فلاش الضوء الكرتوني",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),

        // 2. الضحكات
        FunnySound(
            id = "laugh_continuous",
            title = "ضحك متواصل",
            category = FunnyCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_04,
            soundRes = R.raw.funny_laugh_continuous,
            accentColor = Color(0xFFFFB300),
            description = "قهقهات طفولية متتالية تنشر البهجة والمرح",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "laugh_chuckle",
            title = "قهقهة خفيفة",
            category = FunnyCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_05,
            soundRes = R.raw.funny_laugh_chuckle,
            accentColor = Color(0xFFFFCA28),
            description = "ضحكة قصيرة ناعمة تدعو للمرح والابتسام",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "laugh_surprise",
            title = "ضحكة مفاجأة",
            category = FunnyCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_06,
            soundRes = R.raw.funny_laugh_surprise,
            accentColor = Color(0xFFFFE082),
            description = "شهقة دهشة سريعة تعقبها ضحكة مباغتة لطيفة",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),

        // 3. المقالب
        FunnySound(
            id = "prank_duck",
            title = "صوت البطة",
            category = FunnyCategory.PRANKS,
            imageRes = R.drawable.funny_photo_07,
            soundRes = R.raw.funny_prank_duck,
            accentColor = Color(0xFFFB8C00),
            description = "كواك كواك كرتوني مضحك للمقالب البريئة",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "prank_sneeze",
            title = "عطسة كوميدية",
            category = FunnyCategory.PRANKS,
            imageRes = R.drawable.funny_photo_08,
            soundRes = R.raw.funny_prank_sneeze,
            accentColor = Color(0xFFFF7043),
            description = "عطسة مسرحية مضحكة بصوت آآآتشووو",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "prank_snore",
            title = "شخير مضحك",
            category = FunnyCategory.PRANKS,
            imageRes = R.drawable.funny_photo_09,
            soundRes = R.raw.funny_prank_snore,
            accentColor = Color(0xFF8D6E63),
            description = "شخير نوم كرتوني ممتع للمداعبة الودودة",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "prank_spring",
            title = "صوت زنبرك",
            category = FunnyCategory.PRANKS,
            imageRes = R.drawable.funny_photo_10,
            soundRes = R.raw.funny_prank_spring,
            accentColor = Color(0xFFAB47BC),
            description = "قفزة الزنبرك الكرتونية الشهيرة (بويينغ!)",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "prank_fall",
            title = "سقوط كوميدي",
            category = FunnyCategory.PRANKS,
            imageRes = R.drawable.funny_photo_11,
            soundRes = R.raw.funny_prank_fall,
            accentColor = Color(0xFFEF5350),
            description = "صوت زحليقة وانزلاق مضحك مع ارتطام ناعم",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "prank_horn",
            title = "بوق الغازات الكرتوني",
            category = FunnyCategory.PRANKS,
            imageRes = R.drawable.funny_photo_12,
            soundRes = R.raw.funny_prank_horn,
            accentColor = Color(0xFFD4E157),
            description = "بوق كرتوني مرح للمقالب المسلية دون إزعاج",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),

        // 4. الأصوات البشرية المضحكة
        FunnySound(
            id = "human_wow",
            title = "صيحة دهشة",
            category = FunnyCategory.HUMAN,
            imageRes = R.drawable.funny_photo_13,
            soundRes = R.raw.funny_human_wow,
            accentColor = Color(0xFF42A5F5),
            description = "صوت وااااو مبهج يعبر عن الإعجاب والتصفيق",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "human_applause",
            title = "تصفيق حار",
            category = FunnyCategory.HUMAN,
            imageRes = R.drawable.funny_photo_14,
            soundRes = R.raw.funny_human_applause,
            accentColor = Color(0xFF66BB6A),
            description = "تصفيق أطفال تشجيعي مفعم بالحماس",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "human_yawn",
            title = "تثاؤب طريف",
            category = FunnyCategory.HUMAN,
            imageRes = R.drawable.funny_photo_15,
            soundRes = R.raw.funny_human_yawn,
            accentColor = Color(0xFF90A4AE),
            description = "تثاؤب كسول كوميدي يدعو للاسترخاء والضحك",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "human_monkey",
            title = "تقليد القرد",
            category = FunnyCategory.HUMAN,
            imageRes = R.drawable.funny_photo_16,
            soundRes = R.raw.funny_human_monkey,
            accentColor = Color(0xFF8D6E63),
            description = "تقليد قرد كرتوني سريع ومرح (أو أو آ آ)",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),

        // 5. ألعاب ومؤثرات عامة غير منسوبة لشخصيات محمية
        FunnySound(
            id = "toon_sponge",
            title = "صرير اللعبة المطاطية",
            category = FunnyCategory.CARTOON,
            imageRes = R.drawable.funny_photo_17,
            soundRes = R.raw.funny_toon_sponge,
            accentColor = Color(0xFFFFEB3B),
            description = "صرير لعبة مطاطية حقيقي مرح وآمن للأطفال",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "toon_bear",
            title = "نباح الحيوان اللطيف",
            category = FunnyCategory.CARTOON,
            imageRes = R.drawable.funny_photo_18,
            soundRes = R.raw.funny_toon_bear,
            accentColor = Color(0xFF795548),
            description = "صوت حيوان حقيقي لطيف، دون نسبه إلى شخصية كرتونية",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "toon_cop",
            title = "صوت لعبة خشبية",
            category = FunnyCategory.CARTOON,
            imageRes = R.drawable.funny_photo_19,
            soundRes = R.raw.funny_toon_cop,
            accentColor = Color(0xFF3F51B5),
            description = "صوت لعبة خشبية حقيقية لمؤثر مرح",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        ),
        FunnySound(
            id = "toon_speedy",
            title = "مؤثر جهاز سريع",
            category = FunnyCategory.CARTOON,
            imageRes = R.drawable.funny_photo_20,
            soundRes = R.raw.funny_toon_speedy,
            accentColor = Color(0xFFFF5722),
            description = "مؤثر جهاز عام سريع وغير منسوب إلى شخصية",
            licenseNotice = "تسجيل فعلي أو مؤثر مرخّص؛ راجع ASSET_LICENSES.md للمصدر وشروط الاستخدام."
        )
    )
}
