package com.aistudio.kidspolice.abcd.data

data class PoliceVehicle(
    val id: Int,
    val name: String,
    val description: String,
    val imageAssetPath: String
)

val policeVehicles = listOf(
    PoliceVehicle(1, "سيارة دورية", "تجوب الشوارع لحماية الأحياء وحفظ الأمن والاستقرار", "img_police_patrol"),
    PoliceVehicle(2, "سيارة شرطة رياضية", "سريعة جداً لملاحقة المطلوبين على الطرق السريعة", "img_police_sports"),
    PoliceVehicle(3, "دراجة شرطة نارية", "تتحرك بخفة وسرعة عالية وتتجاوز الازدحام المروري", "img_police_motorcycle"),
    PoliceVehicle(4, "مروحية شرطة", "تراقب وتتابع الحركة المرورية والعمليات الأمنية من الجو", "img_police_helicopter"),
    PoliceVehicle(5, "زورق شرطة", "يقوم بالدوريات المائية وحماية السواحل والأنهار والموانئ", "img_police_boat"),
    PoliceVehicle(6, "مركبة التدخل السريع (SWAT)", "شاحنة مدرعة ومجهزة لمهام الاقتحام والعمليات الخاصة الصعبة", "img_police_swat"),
    PoliceVehicle(7, "سيارة الكلاب البوليسية (K9)", "مجهزة لنقل الكلاب المدربة على كشف الممنوعات وتتبع الأثر", "img_police_k9"),
    PoliceVehicle(8, "دراجة شرطة هوائية", "تستخدم في الدوريات البيئية والصديقة للبيئة بالحدائق العامة والأسواق", "img_police_bicycle"),
    PoliceVehicle(9, "خيل الشرطة", "يستخدم في دوريات الخيالة التقليدية بالمناسبات الكبرى والمناطق الوعرة", "img_police_horse"),
    PoliceVehicle(10, "سيارة التحقيق الجنائي (CSI)", "مختبر متنقل لجمع الأدلة الجنائية وفحص مسرح الجريمة بدقة", "img_police_csi"),
    PoliceVehicle(11, "طائرة استطلاع بدون طيار (درون)", "تراقب الحشود وتصل لأصعب الأماكن للتصوير والمسح الأمني من الأعلى", "img_police_drone"),
    PoliceVehicle(12, "حافلة نقل السجناء", "عربة مصفحة ومؤمنة بالكامل لنقل النزلاء والمطلوبين بأمان تام", "img_police_prisoner_transport")
)
