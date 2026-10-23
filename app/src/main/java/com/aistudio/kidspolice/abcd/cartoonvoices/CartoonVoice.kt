package com.aistudio.kidspolice.abcd.cartoonvoices

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.ui.graphics.Color
import com.aistudio.kidspolice.abcd.R

enum class CartoonVoiceCategory(val title: String, @DrawableRes val iconRes: Int, val color: Color) {
    ALL("الكل", R.drawable.funny_photo_04, Color(0xFFFF5722)),
    LAUGHTER("الضحكات", R.drawable.funny_photo_01, Color(0xFFFFB300)),
    CHARACTERS("الشخصيات الخيالية", R.drawable.funny_photo_11, Color(0xFF9C27B0)),
    HUMAN("أصوات بشرية", R.drawable.funny_photo_04, Color(0xFF4CAF50)),
    REACTIONS("ردود فعل مرحة", R.drawable.funny_photo_06, Color(0xFFE91E63))
}

data class CartoonVoice(val id: String, val title: String, val category: CartoonVoiceCategory, @DrawableRes val imageRes: Int, @RawRes val soundRes: Int, val accentColor: Color, val description: String, val licenseNotice: String)

object CartoonVoicesRepository {
    val voices: List<CartoonVoice> = listOf(
        CartoonVoice(
            id = "cartoon_voice_01",
            title = "ضحكة كرتونية مرحة",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_01,
            soundRes = R.raw.cartoon_voice_01_72842_lonemonk_approx_800_laugh_1_wa,
            accentColor = Color(0xFFFFB300),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY 3.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_02",
            title = "ضحك متواصل مرح",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_02,
            soundRes = R.raw.cartoon_voice_02_72843_lonemonk_approx_800_laughter_o,
            accentColor = Color(0xFF8E24AA),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY 3.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_03",
            title = "ضحكة وحش لطيف",
            category = CartoonVoiceCategory.CHARACTERS,
            imageRes = R.drawable.funny_photo_03,
            soundRes = R.raw.cartoon_voice_03_beast_laughter_ogg,
            accentColor = Color(0xFF43A047),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "Public domain; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_04",
            title = "ضحكة طفل مسجلة",
            category = CartoonVoiceCategory.HUMAN,
            imageRes = R.drawable.funny_photo_04,
            soundRes = R.raw.cartoon_voice_04_boy_giggle_ogg,
            accentColor = Color(0xFFE53935),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY 3.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_05",
            title = "تنفّس بشري هادئ",
            category = CartoonVoiceCategory.HUMAN,
            imageRes = R.drawable.funny_photo_05,
            soundRes = R.raw.cartoon_voice_05_breathing_man_ogg,
            accentColor = Color(0xFFFF7043),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "Public domain; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_06",
            title = "تجشؤ كوميدي قصير",
            category = CartoonVoiceCategory.REACTIONS,
            imageRes = R.drawable.funny_photo_06,
            soundRes = R.raw.cartoon_voice_06_burp_ogg,
            accentColor = Color(0xFFFFB300),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "Public domain; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_07",
            title = "تجشؤ كوميدي متواصل",
            category = CartoonVoiceCategory.REACTIONS,
            imageRes = R.drawable.funny_photo_07,
            soundRes = R.raw.cartoon_voice_07_burping_ogg,
            accentColor = Color(0xFF8E24AA),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY-SA 3.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_08",
            title = "تجشؤ كوميدي عميق",
            category = CartoonVoiceCategory.REACTIONS,
            imageRes = R.drawable.funny_photo_08,
            soundRes = R.raw.cartoon_voice_08_burping_wav,
            accentColor = Color(0xFF43A047),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY-SA 4.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_09",
            title = "تجشؤ سريع طريف",
            category = CartoonVoiceCategory.REACTIONS,
            imageRes = R.drawable.funny_photo_09,
            soundRes = R.raw.cartoon_voice_09_burping10_ogg,
            accentColor = Color(0xFFE53935),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY-SA 4.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_10",
            title = "ضحكة مفاجئة",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_10,
            soundRes = R.raw.cartoon_voice_10_bursting_out_laughing_ogg,
            accentColor = Color(0xFFFF7043),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "Public domain; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_11",
            title = "صندوق ضحك كرتوني",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_11,
            soundRes = R.raw.cartoon_voice_11_cartoon_laugh_ogg,
            accentColor = Color(0xFFFFB300),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_12",
            title = "ضحكة قزم خيالي",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_12,
            soundRes = R.raw.cartoon_voice_12_elf_laughter_ogg,
            accentColor = Color(0xFF8E24AA),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "Public domain; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_13",
            title = "تعجب: ها ها",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_13,
            soundRes = R.raw.cartoon_voice_13_en_uk_ha_ha_ogg,
            accentColor = Color(0xFF43A047),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY-SA 3.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_14",
            title = "ضحكة شريرة تمثيلية",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_14,
            soundRes = R.raw.cartoon_voice_14_evil_laugh_ogg,
            accentColor = Color(0xFFE53935),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_15",
            title = "ضحكة شريرة قصيرة",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_15,
            soundRes = R.raw.cartoon_voice_15_evil_laugh_2_oga,
            accentColor = Color(0xFFFF7043),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY-SA 3.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_16",
            title = "ضحكة شريرة طويلة",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_16,
            soundRes = R.raw.cartoon_voice_16_evil_laughter_ogg,
            accentColor = Color(0xFFFFB300),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "Public domain; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_17",
            title = "تصفيق وفرح مسجل",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_17,
            soundRes = R.raw.cartoon_voice_17_fireworks_end_of_act_applause_ogg,
            accentColor = Color(0xFF8E24AA),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "Public domain; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_18",
            title = "ضحكة طفل عمر أربع سنوات",
            category = CartoonVoiceCategory.HUMAN,
            imageRes = R.drawable.funny_photo_18,
            soundRes = R.raw.cartoon_voice_18_four_years_old_child_laughing_ogg,
            accentColor = Color(0xFF43A047),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY 3.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_19",
            title = "مناغاة طفل مسجلة",
            category = CartoonVoiceCategory.HUMAN,
            imageRes = R.drawable.funny_photo_19,
            soundRes = R.raw.cartoon_voice_19_gabbling_baby_2_months_old_ogg,
            accentColor = Color(0xFFE53935),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "Public domain; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_20",
            title = "ضحكة بشرية عامة",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_20,
            soundRes = R.raw.cartoon_voice_20_laughter,
            accentColor = Color(0xFFFF7043),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "Public domain; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_21",
            title = "ضحك خفيف متكرر",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_01,
            soundRes = R.raw.cartoon_voice_21_giggling_ogg,
            accentColor = Color(0xFFFFB300),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY-SA 4.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_22",
            title = "صوت تذمر خيالي",
            category = CartoonVoiceCategory.CHARACTERS,
            imageRes = R.drawable.funny_photo_02,
            soundRes = R.raw.cartoon_voice_22_harrumphing_ogg,
            accentColor = Color(0xFF8E24AA),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY-SA 4.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_23",
            title = "تنفّس ثقيل تمثيلي",
            category = CartoonVoiceCategory.CHARACTERS,
            imageRes = R.drawable.funny_photo_03,
            soundRes = R.raw.cartoon_voice_23_heavy_breathing_2_ogg,
            accentColor = Color(0xFF43A047),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "Public domain; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_24",
            title = "تنفّس ثقيل طويل",
            category = CartoonVoiceCategory.CHARACTERS,
            imageRes = R.drawable.funny_photo_04,
            soundRes = R.raw.cartoon_voice_24_heavy_breathing_3_ogg,
            accentColor = Color(0xFFE53935),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "Public domain; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_25",
            title = "فواق مضحك مسجل",
            category = CartoonVoiceCategory.CHARACTERS,
            imageRes = R.drawable.funny_photo_05,
            soundRes = R.raw.cartoon_voice_25_hiccupsound_ogg,
            accentColor = Color(0xFFFF7043),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY-SA 3.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_26",
            title = "ضحكة رعب خيالية مناسبة للأطفال",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_06,
            soundRes = R.raw.cartoon_voice_26_horror_laugh_ogg,
            accentColor = Color(0xFFFFB300),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY 3.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_27",
            title = "ضحكة صاخبة مع صدى",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_07,
            soundRes = R.raw.cartoon_voice_27_insane_laughter_man_reverb_ogg,
            accentColor = Color(0xFF8E24AA),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY 3.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_28",
            title = "مزيج ضحكات بشرية",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_08,
            soundRes = R.raw.cartoon_voice_28_lachen_mix_mm_ogg,
            accentColor = Color(0xFF43A047),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC BY 4.0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_29",
            title = "ضحكة قصيرة مرخّصة",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_09,
            soundRes = R.raw.cartoon_voice_29_laughter_s_ogg,
            accentColor = Color(0xFFE53935),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
        CartoonVoice(
            id = "cartoon_voice_30",
            title = "ضحكة فتاة قصيرة مسجلة",
            category = CartoonVoiceCategory.LAUGHTER,
            imageRes = R.drawable.funny_photo_10,
            soundRes = R.raw.cartoon_voice_30_short_clip_of_girl_laughing_wav,
            accentColor = Color(0xFFFF7043),
            description = "تسجيل حقيقي أو مؤثر أدائي منشور بترخيص يسمح بالاستخدام التجاري وإعادة التوزيع داخل التطبيق.",
            licenseNotice = "CC0; راجع ASSET_LICENSES.md للمصدر والمؤلف وشروط الاستخدام."
        ),
    )
}