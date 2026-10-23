package com.aistudio.kidspolice.abcd.toys

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.ui.graphics.Color
import com.aistudio.kidspolice.abcd.R

data class ToyWeapon(
    val id: String,
    val name: String,
    val description: String,
    @DrawableRes val imageRes: Int,
    @RawRes val soundRes: Int,
    val accentColor: Color,
    val effectType: EffectType,
    val assetLicenseInfo: String
)

enum class EffectType {
    LASER_BEAM,
    BUBBLE_BURST,
    SONIC_RINGS,
    CONFETTI_SHOWER
}

object ToyWeaponsRepository {
    val items: List<ToyWeapon> = listOf(
        ToyWeapon(
            id = "laser_blaster",
            name = "مسدس الليزر الفضائي",
            description = "أشعة ضوئية نيونية آمنة ومرحة للدوريات الخيالية",
            imageRes = R.drawable.toy_item_laser,
            soundRes = R.raw.toy_laser_zap,
            accentColor = Color(0xFF00E5FF),
            effectType = EffectType.LASER_BEAM,
            assetLicenseInfo = "صورة مخصصة 3D أصلية مولدة للتطبيق. صوت نيون أصلي مولد رياضياً (WAV 22kHz 16-bit) مرخص تجارياً ومعفى من الإتاوات."
        ),
        ToyWeapon(
            id = "bubble_cannon",
            name = "قاذف الفقاعات العجيب",
            description = "إطلاق فقاعات صابونية ملونة وأصوات مرحة للأطفال",
            imageRes = R.drawable.toy_item_bubble,
            soundRes = R.raw.toy_bubble_pop,
            accentColor = Color(0xFFFF9100),
            effectType = EffectType.BUBBLE_BURST,
            assetLicenseInfo = "صورة أصلية 3D كرتونية مخصصة للتطبيق. صوت تفرقع الفقاعات مولد حسابياً ومحفوظ محلياً ومجاني للاستخدام التجاري."
        ),
        ToyWeapon(
            id = "sonic_disruptor",
            name = "مكبر الموجات الصوتية",
            description = "موجات خيالية لطيفة لدوريات حماية الألعاب",
            imageRes = R.drawable.toy_item_sonic,
            soundRes = R.raw.toy_sonic_pulse,
            accentColor = Color(0xFFD500F9),
            effectType = EffectType.SONIC_RINGS,
            assetLicenseInfo = "صورة أصلية 3D مخصصة للتطبيق. صوت النبضات الترددية المركبة مولد داخلياً محلياً ومتاح لإعادة التوزيع."
        ),
        ToyWeapon(
            id = "confetti_blaster",
            name = "مدفع أوراق الاحتفال",
            description = "مفرقعات احتفالية آمنة تنثر قصاصات الفرح والنجوم",
            imageRes = R.drawable.toy_item_confetti,
            soundRes = R.raw.toy_confetti_burst,
            accentColor = Color(0xFFFF1744),
            effectType = EffectType.CONFETTI_SHOWER,
            assetLicenseInfo = "صورة أصلية 3D احتفالية مخصصة للتطبيق. صوت الصافرة والفرقعة المرحة مولد محلياً ومعفى من حقوق الملكية."
        )
    )
}
