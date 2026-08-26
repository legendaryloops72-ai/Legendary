package com.aistudio.kidspolice.abcd.data

enum class Dialect(
    val displayName: String,
    val flag: String,
    val languageCode: String
) {
    SAUDI("اللهجة السعودية / الخليجية", "🇸🇦", "ar-SA"),
    EGYPTIAN("اللهجة المصرية", "🇪🇬", "ar-EG"),
    SYRIAN("اللهجة الشامية", "🇸🇾", "ar-SY"),
    GULF("اللهجة الإماراتية / الكويتية", "🇦🇪", "ar-AE"),
    IRAQI("اللهجة العراقية", "🇮🇶", "ar-IQ"),
    MOROCCAN("اللهجة المغربية", "🇲🇦", "ar-MA"),
    ALGERIAN("اللهجة الجزائرية", "🇩🇿", "ar-DZ"),
    FASHA("اللغة العربية الفصحى", "🌍", "ar")
}
