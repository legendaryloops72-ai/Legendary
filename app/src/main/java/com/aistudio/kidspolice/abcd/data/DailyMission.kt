package com.aistudio.kidspolice.abcd.data

data class DailyMission(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val points: Int,
    val isCompleted: Boolean = false
)

object MissionRepository {
    fun getDefaultMissions(): List<DailyMission> = listOf(
        DailyMission("m1", "النوم في الساعة 9 مساءً", "النوم في موعده يمنحك قوة ونشاط الأبطال", "🛌", 25, false),
        DailyMission("m2", "إنهاء طبق الطعام كاملاً", "تناول الخضار والفواكه والوجبة المفيدة", "🥗", 20, false),
        DailyMission("m3", "سماع كلام ماما وبابا فوراً", "تنفيذ التوجيهات دون تأخير وبر الوالدين", "❤️", 30, false),
        DailyMission("m4", "ترتيب الألعاب والغرفة", "المحافظة على النظافة وترتيب المكان", "🧸", 15, false),
        DailyMission("m5", "حل الواجبات المدرسية", "كتابة الدروس بتركيز وإتقان", "📚", 25, false),
        DailyMission("m6", "تنظيف الأسنان قبل النوم", "حماية الأسنان والمحافظة على النظافة", "🪥", 15, false)
    )
}
