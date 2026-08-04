package com.aistudio.kidspolice.abcd.data

data class PoliceVehicle(
    val id: Int,
    val name: String,
    val description: String,
    val imageAssetPath: String
)

val policeVehicles = listOf(
    PoliceVehicle(1, "سيارة دورية", "تجوب الشوارع لحماية الأحياء", "img_police_patrol"),
    PoliceVehicle(2, "سيارة شرطة رياضية", "سريعة لملاحقة المطلوبين", "img_police_sports"),
    PoliceVehicle(3, "دراجة شرطة", "تتحرك بخفة بين الأزقة الضيقة", "img_police_motorcycle"),
    PoliceVehicle(4, "مروحية شرطة", "تراقب المدينة من الأعلى", "img_police_helicopter"),
    PoliceVehicle(5, "زورق شرطة", "يحمي السواحل والأنهار", "img_police_boat"),
    PoliceVehicle(6, "سيارة تدخل سريع", "تصل أولاً في حالات الطوارئ", "img_police_swat"),
    PoliceVehicle(7, "سيارة إسعاف شرطية", "تنقذ المصابين بسرعة", "img_police_ambulance"),
    PoliceVehicle(8, "سيارة مطافئ شرطية", "تطفئ الحرائق وتحمي الناس", "img_police_firetruck"),
    PoliceVehicle(9, "سيارة مكافحة شغب", "تحافظ على الأمن بالتجمعات", "img_police_riot"),
    PoliceVehicle(10, "سيارة قوات خاصة", "لمهام صعبة وخطيرة", "img_police_special_forces"),
    PoliceVehicle(11, "شاحنة نقل معدات الشرطة", "تنقل الأجهزة والمعدات", "img_police_truck"),
    PoliceVehicle(12, "سيارة كلاب بوليسية", "تنقل الكلاب المدربة للتفتيش", "img_police_k9"),
    PoliceVehicle(13, "دراجة هوائية شرطية", "دوريات خفيفة بالحدائق", "img_police_bicycle"),
    PoliceVehicle(14, "سيارة شرطة المرور", "تنظم حركة السيارات بالشوارع", "img_police_traffic"),
    PoliceVehicle(15, "طائرة استطلاع بدون طيار", "تراقب من الجو بدقة", "img_police_drone"),
    PoliceVehicle(16, "سيارة تحقيق جنائي", "تجمع الأدلة من مسرح الحادث", "img_police_csi"),
    PoliceVehicle(17, "زورق إنقاذ شرطي", "ينقذ الناس بالفيضانات والبحيرات", "img_police_rescue_boat"),
    PoliceVehicle(18, "سيارة نقل سجناء", "تنقل الموقوفين بأمان", "img_police_prisoner_transport"),
    PoliceVehicle(19, "سيارة شرطة النجدة", "تصل بسرعة عند الاتصال بالطوارئ", "img_police_emergency"),
    PoliceVehicle(20, "حصان شرطة", "دوريات تقليدية بالحدائق والمناسبات", "img_police_horse"),
    PoliceVehicle(21, "سيارة شرطة كهربائية صديقة للبيئة", "نظيفة وهادئة بالدوريات", "img_police_electric"),
    PoliceVehicle(22, "زلاجة شرطة ثلجية", "تعمل بالمناطق المغطاة بالثلوج", "img_police_snowmobile"),
    PoliceVehicle(23, "سيارة قيادة متنقلة (كوماند كار)", "مركز قيادة العمليات الكبيرة", "img_police_command"),
    PoliceVehicle(24, "مروحية إنقاذ جبلي", "تنقذ المحتجزين بالمناطق الجبلية", "img_police_mountain_rescue")
)
