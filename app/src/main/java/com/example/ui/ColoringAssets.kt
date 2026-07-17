package com.example.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.pow

enum class ColoringShapeType {
    CIRCLE, OVAL, RECTANGLE, STAR, POLYGON, TRIANGLE
}

data class PercentOffset(val x: Float, val y: Float)

data class ColoringShapeItem(
    val id: String,
    val shapeType: ColoringShapeType,
    val labelAr: String, // Label in Arabic for TTS/fun feedback
    // Relative coordinates (0.0 to 1.0)
    val cx: Float = 0.5f,
    val cy: Float = 0.5f,
    val rx: Float = 0.1f, // Horizontal scale/radius
    val ry: Float = 0.1f, // Vertical scale/radius
    val x1: Float = 0f,
    val y1: Float = 0f,
    val x2: Float = 0f,
    val y2: Float = 0f,
    val points: List<PercentOffset> = emptyList(),
    // Coloring properties
    val isOutlineOnly: Boolean = false,
    val strokeWidth: Float = 5f
)

data class ColoringPage(
    val id: String,
    val title: String,
    val titleAr: String,
    val category: String,
    val categoryAr: String,
    val iconEmoji: String,
    val shapes: List<ColoringShapeItem>,
    val difficultyStars: Int = 3
)

object ColoringAssets {
    const val CAT_ANIMALS = "animals"
    const val CAT_CARS = "cars"
    const val CAT_SUPERHEROES = "superheroes"
    const val CAT_NATURE = "nature"
    const val CAT_CARTOON = "cartoon"
    const val CAT_EDUCATIONAL = "educational"

    val categories = listOf(
        CategoryInfo(CAT_ANIMALS, "حيوانات لطيفة", "🐼"),
        CategoryInfo(CAT_CARS, "مركبات وسيارات", "🚗"),
        CategoryInfo(CAT_SUPERHEROES, "أبطال خارقين", "🦸"),
        CategoryInfo(CAT_NATURE, "طبيعة وجمال", "🌳"),
        CategoryInfo(CAT_CARTOON, "شخصيات كرتونية", "👾"),
        CategoryInfo(CAT_EDUCATIONAL, "أشياء تعليمية", "✏️")
    )

    data class CategoryInfo(val id: String, val nameAr: String, val icon: String)

    // Helper functions to generate coordinates for geometric stars & polygons
    private fun getStarPoints(cx: Float, cy: Float, rOuter: Float, rInner: Float, numPoints: Int = 5): List<PercentOffset> {
        val points = mutableListOf<PercentOffset>()
        var angle = -Math.PI / 2
        val angleIncrement = Math.PI / numPoints
        for (i in 0 until numPoints * 2) {
            val r = if (i % 2 == 0) rOuter else rInner
            val x = cx + (r * Math.cos(angle)).toFloat()
            val y = cy + (r * Math.sin(angle)).toFloat()
            points.add(PercentOffset(x, y))
            angle += angleIncrement
        }
        return points
    }

    private fun getPolygonPoints(cx: Float, cy: Float, radius: Float, sides: Int): List<PercentOffset> {
        val points = mutableListOf<PercentOffset>()
        var angle = -Math.PI / 2
        val angleIncrement = (Math.PI * 2) / sides
        for (i in 0 until sides) {
            val x = cx + (radius * Math.cos(angle)).toFloat()
            val y = cy + (radius * Math.sin(angle)).toFloat()
            points.add(PercentOffset(x, y))
            angle += angleIncrement
        }
        return points
    }

    val coloringPages: List<ColoringPage> = listOf(
        // ==========================================
        // 1. ANIMALS (5 Pages)
        // ==========================================
        ColoringPage(
            id = "animal_panda",
            title = "Smiling Panda",
            titleAr = "الباندا الضاحك",
            category = CAT_ANIMALS,
            categoryAr = "حيوانات لطيفة",
            iconEmoji = "🐼",
            shapes = listOf(
                // Body
                ColoringShapeItem("body", ColoringShapeType.OVAL, "جسم الباندا", cx = 0.5f, cy = 0.65f, rx = 0.22f, ry = 0.22f),
                // Belly white patch
                ColoringShapeItem("belly", ColoringShapeType.OVAL, "بطن الباندا", cx = 0.5f, cy = 0.68f, rx = 0.15f, ry = 0.14f),
                // Left Ear
                ColoringShapeItem("left_ear", ColoringShapeType.CIRCLE, "أذن يسرى", cx = 0.38f, cy = 0.3f, rx = 0.07f),
                // Right Ear
                ColoringShapeItem("right_ear", ColoringShapeType.CIRCLE, "أذن يمنى", cx = 0.62f, cy = 0.3f, rx = 0.07f),
                // Face
                ColoringShapeItem("face", ColoringShapeType.OVAL, "رأس الباندا", cx = 0.5f, cy = 0.42f, rx = 0.18f, ry = 0.15f),
                // Left Eye patch
                ColoringShapeItem("left_eye_patch", ColoringShapeType.OVAL, "هالة العين اليسرى", cx = 0.44f, cy = 0.42f, rx = 0.045f, ry = 0.035f),
                // Right Eye patch
                ColoringShapeItem("right_eye_patch", ColoringShapeType.OVAL, "هالة العين اليمنى", cx = 0.56f, cy = 0.42f, rx = 0.045f, ry = 0.035f),
                // Left pupil
                ColoringShapeItem("left_pupil", ColoringShapeType.CIRCLE, "عين يسرى", cx = 0.44f, cy = 0.42f, rx = 0.015f),
                // Right pupil
                ColoringShapeItem("right_pupil", ColoringShapeType.CIRCLE, "عين يمنى", cx = 0.56f, cy = 0.42f, rx = 0.015f),
                // Nose
                ColoringShapeItem("nose", ColoringShapeType.OVAL, "الأنف الطيب", cx = 0.5f, cy = 0.46f, rx = 0.025f, ry = 0.015f),
                // Mouth outline only
                ColoringShapeItem("mouth", ColoringShapeType.OVAL, "الابتسامة", cx = 0.5f, cy = 0.5f, rx = 0.03f, ry = 0.015f, isOutlineOnly = true)
            )
        ),
        ColoringPage(
            id = "animal_rabbit",
            title = "Happy Rabbit",
            titleAr = "الأرنب القفاز",
            category = CAT_ANIMALS,
            categoryAr = "حيوانات لطيفة",
            iconEmoji = "🐰",
            shapes = listOf(
                // Body
                ColoringShapeItem("body", ColoringShapeType.OVAL, "جسم الأرنب", cx = 0.5f, cy = 0.72f, rx = 0.20f, ry = 0.18f),
                // Left Long Ear
                ColoringShapeItem("left_ear", ColoringShapeType.OVAL, "أذن الأرنب اليسرى", cx = 0.44f, cy = 0.22f, rx = 0.05f, ry = 0.15f),
                // Right Long Ear
                ColoringShapeItem("right_ear", ColoringShapeType.OVAL, "أذن الأرنب اليمنى", cx = 0.56f, cy = 0.22f, rx = 0.05f, ry = 0.15f),
                // Left Inner Ear pink
                ColoringShapeItem("left_ear_inner", ColoringShapeType.OVAL, "أذن داخلية يسرى", cx = 0.44f, cy = 0.24f, rx = 0.025f, ry = 0.11f),
                // Right Inner Ear pink
                ColoringShapeItem("right_ear_inner", ColoringShapeType.OVAL, "أذن داخلية يمنى", cx = 0.56f, cy = 0.24f, rx = 0.025f, ry = 0.11f),
                // Head
                ColoringShapeItem("head", ColoringShapeType.CIRCLE, "رأس الأرنب", cx = 0.5f, cy = 0.45f, rx = 0.14f),
                // Left Eye
                ColoringShapeItem("left_eye", ColoringShapeType.CIRCLE, "عين يسرى", cx = 0.45f, cy = 0.43f, rx = 0.018f),
                // Right Eye
                ColoringShapeItem("right_eye", ColoringShapeType.CIRCLE, "عين يمنى", cx = 0.55f, cy = 0.43f, rx = 0.018f),
                // Nose/Cheeks
                ColoringShapeItem("nose", ColoringShapeType.CIRCLE, "الأنف الكيوت", cx = 0.5f, cy = 0.48f, rx = 0.015f),
                // Cute tooth
                ColoringShapeItem("tooth", ColoringShapeType.RECTANGLE, "الأرنب المشاكس", x1 = 0.48f, y1 = 0.51f, x2 = 0.52f, y2 = 0.54f),
                // Tail
                ColoringShapeItem("tail", ColoringShapeType.CIRCLE, "ذيل قطني", cx = 0.3f, cy = 0.78f, rx = 0.045f)
            )
        ),
        ColoringPage(
            id = "animal_goldfish",
            title = "Shiny Goldfish",
            titleAr = "السمكة الذهبية",
            category = CAT_ANIMALS,
            categoryAr = "حيوانات لطيفة",
            iconEmoji = "🐠",
            shapes = listOf(
                // Tail Back Fin
                ColoringShapeItem("tail_fin", ColoringShapeType.TRIANGLE, "ذيل السمكة", points = listOf(PercentOffset(0.24f, 0.38f), PercentOffset(0.24f, 0.62f), PercentOffset(0.42f, 0.5f))),
                // Top Fin
                ColoringShapeItem("top_fin", ColoringShapeType.OVAL, "الزعنفة العلوية", cx = 0.52f, cy = 0.3f, rx = 0.09f, ry = 0.05f),
                // Bottom Fin
                ColoringShapeItem("bottom_fin", ColoringShapeType.OVAL, "الزعنفة السفلية", cx = 0.52f, cy = 0.7f, rx = 0.07f, ry = 0.04f),
                // Main Fish Body
                ColoringShapeItem("fish_body", ColoringShapeType.OVAL, "جسم السمكة اللامع", cx = 0.5f, cy = 0.5f, rx = 0.22f, ry = 0.16f),
                // Face line separator
                ColoringShapeItem("face_separator", ColoringShapeType.OVAL, "رأس السمكة", cx = 0.62f, cy = 0.5f, rx = 0.05f, ry = 0.13f, isOutlineOnly = false),
                // Eye
                ColoringShapeItem("fish_eye", ColoringShapeType.CIRCLE, "عين السمكة البرّاقة", cx = 0.62f, cy = 0.46f, rx = 0.025f),
                // Inner pupil
                ColoringShapeItem("fish_pupil", ColoringShapeType.CIRCLE, "بؤبؤ العين", cx = 0.63f, cy = 0.46f, rx = 0.010f),
                // Mouth
                ColoringShapeItem("fish_mouth", ColoringShapeType.OVAL, "فم السمكة المبتسم", cx = 0.7f, cy = 0.54f, rx = 0.018f, ry = 0.015f),
                // Water Bubbles
                ColoringShapeItem("bubble1", ColoringShapeType.CIRCLE, "فقاعة ماء 1", cx = 0.78f, cy = 0.35f, rx = 0.025f),
                ColoringShapeItem("bubble2", ColoringShapeType.CIRCLE, "فقاعة ماء 2", cx = 0.83f, cy = 0.25f, rx = 0.015f)
            )
        ),
        ColoringPage(
            id = "animal_bear",
            title = "Teddy Bear",
            titleAr = "الدب الصغير الطيب",
            category = CAT_ANIMALS,
            categoryAr = "حيوانات لطيفة",
            iconEmoji = "🧸",
            shapes = listOf(
                // Ears
                ColoringShapeItem("left_ear", ColoringShapeType.CIRCLE, "أذن يسرى", cx = 0.36f, cy = 0.32f, rx = 0.06f),
                ColoringShapeItem("right_ear", ColoringShapeType.CIRCLE, "أذن يمنى", cx = 0.64f, cy = 0.32f, rx = 0.06f),
                ColoringShapeItem("left_ear_inner", ColoringShapeType.CIRCLE, "أذن داخلية يسرى", cx = 0.36f, cy = 0.32f, rx = 0.035f),
                ColoringShapeItem("right_ear_inner", ColoringShapeType.CIRCLE, "أذن داخلية يمنى", cx = 0.64f, cy = 0.32f, rx = 0.035f),
                // Body
                ColoringShapeItem("body", ColoringShapeType.OVAL, "جسم الدبدوب لتدفئته", cx = 0.5f, cy = 0.68f, rx = 0.18f, ry = 0.18f),
                ColoringShapeItem("belly", ColoringShapeType.OVAL, "بطن الدبدوب العسل", cx = 0.5f, cy = 0.68f, rx = 0.11f, ry = 0.11f),
                // Hands
                ColoringShapeItem("left_hand", ColoringShapeType.OVAL, "يد يسرى للسلام", cx = 0.31f, cy = 0.62f, rx = 0.05f, ry = 0.08f),
                ColoringShapeItem("right_hand", ColoringShapeType.OVAL, "يد يمنى للسلام", cx = 0.69f, cy = 0.62f, rx = 0.05f, ry = 0.08f),
                // Feet/Paws
                ColoringShapeItem("left_foot", ColoringShapeType.CIRCLE, "القدم اليسرى المريحة", cx = 0.36f, cy = 0.82f, rx = 0.065f),
                ColoringShapeItem("right_foot", ColoringShapeType.CIRCLE, "القدم اليمنى المريحة", cx = 0.64f, cy = 0.82f, rx = 0.065f),
                // Face/Head
                ColoringShapeItem("head", ColoringShapeType.CIRCLE, "رأس الدب الحليم", cx = 0.5f, cy = 0.44f, rx = 0.15f),
                // Eyes
                ColoringShapeItem("left_eye", ColoringShapeType.CIRCLE, "عين يسرى", cx = 0.45f, cy = 0.42f, rx = 0.015f),
                ColoringShapeItem("right_eye", ColoringShapeType.CIRCLE, "عين يمنى", cx = 0.55f, cy = 0.42f, rx = 0.015f),
                // Snout
                ColoringShapeItem("snout", ColoringShapeType.OVAL, "وجه الدب الأمامي", cx = 0.5f, cy = 0.48f, rx = 0.045f, ry = 0.03f),
                ColoringShapeItem("nose", ColoringShapeType.OVAL, "أنف أسود لطيف", cx = 0.5f, cy = 0.47f, rx = 0.02f, ry = 0.012f)
            )
        ),
        ColoringPage(
            id = "animal_lion",
            title = "Brave Lion",
            titleAr = "الأسد الشجاع",
            category = CAT_ANIMALS,
            categoryAr = "حيوانات لطيفة",
            iconEmoji = "🦁",
            shapes = listOf(
                // Mane Backdrop
                ColoringShapeItem("mane", ColoringShapeType.STAR, "لبدة الأسد الشامخ", points = getStarPoints(0.5f, 0.45f, 0.28f, 0.21f, 12)),
                // Body
                ColoringShapeItem("body", ColoringShapeType.OVAL, "جسم ملك الغابة", cx = 0.5f, cy = 0.74f, rx = 0.18f, ry = 0.14f),
                // Feet
                ColoringShapeItem("left_foot", ColoringShapeType.OVAL, "مخلب أيسر مسالم", cx = 0.38f, cy = 0.84f, rx = 0.05f, ry = 0.04f),
                ColoringShapeItem("right_foot", ColoringShapeType.OVAL, "مخلب أيمن مسالم", cx = 0.62f, cy = 0.84f, rx = 0.05f, ry = 0.04f),
                // Face Circle
                ColoringShapeItem("face", ColoringShapeType.CIRCLE, "رأس الأسد الضاحك", cx = 0.5f, cy = 0.45f, rx = 0.16f),
                // Inner Ears
                ColoringShapeItem("left_ear", ColoringShapeType.CIRCLE, "أذن يسرى", cx = 0.38f, cy = 0.32f, rx = 0.035f),
                ColoringShapeItem("right_ear", ColoringShapeType.CIRCLE, "أذن يمنى", cx = 0.62f, cy = 0.32f, rx = 0.035f),
                // Eyes
                ColoringShapeItem("left_eye", ColoringShapeType.CIRCLE, "عين ذكية يسرى", cx = 0.44f, cy = 0.42f, rx = 0.015f),
                ColoringShapeItem("right_eye", ColoringShapeType.CIRCLE, "عين ذكية يمنى", cx = 0.56f, cy = 0.42f, rx = 0.015f),
                // Snout
                ColoringShapeItem("snout", ColoringShapeType.OVAL, "أنف ملك الغابة الأنيق", cx = 0.5f, cy = 0.49f, rx = 0.04f, ry = 0.025f),
                ColoringShapeItem("nose", ColoringShapeType.TRIANGLE, "الأنف الطيب", points = listOf(PercentOffset(0.48f, 0.47f), PercentOffset(0.52f, 0.47f), PercentOffset(0.5f, 0.5f)))
            )
        ),

        // ==========================================
        // 2. CARS (5 Pages)
        // ==========================================
        ColoringPage(
            id = "car_racing",
            title = "Racing Car",
            titleAr = "سيارة السباق البرق",
            category = CAT_CARS,
            categoryAr = "مركبات وسيارات",
            iconEmoji = "🏎️",
            shapes = listOf(
                // Spoiler Back Wing
                ColoringShapeItem("spoiler", ColoringShapeType.RECTANGLE, "جناح سيارة السباق", x1 = 0.22f, y1 = 0.4f, x2 = 0.28f, y2 = 0.5f),
                // Road base
                ColoringShapeItem("road", ColoringShapeType.RECTANGLE, "شارع الأسفلت", x1 = 0.15f, y1 = 0.74f, x2 = 0.85f, y2 = 0.76f),
                // Car Frame Body
                ColoringShapeItem("car_body", ColoringShapeType.POLYGON, "هيكل سيارة السباق النفاث", points = listOf(
                    PercentOffset(0.24f, 0.52f), PercentOffset(0.38f, 0.42f),
                    PercentOffset(0.58f, 0.42f), PercentOffset(0.78f, 0.52f),
                    PercentOffset(0.78f, 0.66f), PercentOffset(0.24f, 0.66f)
                )),
                // Cabin Glass window
                ColoringShapeItem("window", ColoringShapeType.POLYGON, "نافذة السائق الزجاجية", points = listOf(
                    PercentOffset(0.42f, 0.45f), PercentOffset(0.54f, 0.45f),
                    PercentOffset(0.58f, 0.52f), PercentOffset(0.38f, 0.52f)
                )),
                // Left Wheel Wheelwell & Tire
                ColoringShapeItem("wheel_l", ColoringShapeType.CIRCLE, "العجلة الخلفية السريعة", cx = 0.36f, cy = 0.66f, rx = 0.08f),
                ColoringShapeItem("wheel_l_hub", ColoringShapeType.CIRCLE, "محور عجلة يسرى", cx = 0.36f, cy = 0.66f, rx = 0.035f),
                // Right Wheel Wheelwell & Tire
                ColoringShapeItem("wheel_r", ColoringShapeType.CIRCLE, "العجلة الأمامية السريعة", cx = 0.66f, cy = 0.66f, rx = 0.08f),
                ColoringShapeItem("wheel_r_hub", ColoringShapeType.CIRCLE, "محور عجلة يمنى", cx = 0.66f, cy = 0.66f, rx = 0.035f),
                // Racing Number 1 badge
                ColoringShapeItem("badge", ColoringShapeType.CIRCLE, "شعار رقم 1 للسباق", cx = 0.48f, cy = 0.59f, rx = 0.045f)
            )
        ),
        ColoringPage(
            id = "car_police",
            title = "Cartoon Police Car",
            titleAr = "سيارة الشرطة البطلة",
            category = CAT_CARS,
            categoryAr = "مركبات وسيارات",
            iconEmoji = "🚓",
            shapes = listOf(
                // Siren support
                ColoringShapeItem("siren_base", ColoringShapeType.RECTANGLE, "قاعدة صفارة الخطر", x1 = 0.47f, y1 = 0.32f, x2 = 0.53f, y2 = 0.35f),
                // Red Siren Light
                ColoringShapeItem("siren_light", ColoringShapeType.OVAL, "ضوء الشرطة الدوار", cx = 0.5f, cy = 0.29f, rx = 0.04f, ry = 0.03f),
                // Car Cabin Base
                ColoringShapeItem("cabin", ColoringShapeType.POLYGON, "هيكل سيارة الشرطة العلوي", points = listOf(
                    PercentOffset(0.32f, 0.52f), PercentOffset(0.42f, 0.35f),
                    PercentOffset(0.6f, 0.35f), PercentOffset(0.72f, 0.52f)
                )),
                // Side Glass Window
                ColoringShapeItem("window", ColoringShapeType.POLYGON, "نافذة دورية الشرطة الذكية", points = listOf(
                    PercentOffset(0.44f, 0.38f), PercentOffset(0.58f, 0.38f),
                    PercentOffset(0.66f, 0.49f), PercentOffset(0.36f, 0.49f)
                )),
                // Lower Car Body
                ColoringShapeItem("police_body", ColoringShapeType.RECTANGLE, "هيكل سيارة الشرطة السفلي", x1 = 0.23f, y1 = 0.52f, x2 = 0.79f, y2 = 0.70f),
                // Wheels
                ColoringShapeItem("wheel_l", ColoringShapeType.CIRCLE, "عجلة يسرى قوية", cx = 0.36f, cy = 0.7f, rx = 0.075f),
                ColoringShapeItem("wheel_l_rim", ColoringShapeType.CIRCLE, "محور عجلة يسرى", cx = 0.36f, cy = 0.7f, rx = 0.03f),
                ColoringShapeItem("wheel_r", ColoringShapeType.CIRCLE, "عجلة يمنى قوية", cx = 0.66f, cy = 0.7f, rx = 0.075f),
                ColoringShapeItem("wheel_r_rim", ColoringShapeType.CIRCLE, "محور عجلة يمنى", cx = 0.66f, cy = 0.7f, rx = 0.03f),
                // Emblem Star
                ColoringShapeItem("star_badge", ColoringShapeType.STAR, "نجمة شرف دورية الأطفال", points = getStarPoints(0.5f, 0.61f, 0.045f, 0.02f))
            )
        ),
        ColoringPage(
            id = "car_truck",
            title = "Friendly Truck",
            titleAr = "الشاحنة الصديقة الكبيرة",
            category = CAT_CARS,
            categoryAr = "مركبات وسيارات",
            iconEmoji = "🚚",
            shapes = listOf(
                // Truck Back Carrier container
                ColoringShapeItem("cargo", ColoringShapeType.RECTANGLE, "صندوق شحن البضائع والألعاب", x1 = 0.21f, y1 = 0.35f, x2 = 0.56f, y2 = 0.65f),
                // Cab Connector
                ColoringShapeItem("connector", ColoringShapeType.RECTANGLE, "رابط المقطورة", x1 = 0.56f, y1 = 0.58f, x2 = 0.61f, y2 = 0.63f),
                // Front Cabin Driver
                ColoringShapeItem("cabin", ColoringShapeType.POLYGON, "مقصورة سائق الشاحنة الكيوت", points = listOf(
                    PercentOffset(0.61f, 0.44f), PercentOffset(0.72f, 0.44f),
                    PercentOffset(0.79f, 0.56f), PercentOffset(0.79f, 0.65f),
                    PercentOffset(0.61f, 0.65f)
                )),
                // Driver Window
                ColoringShapeItem("window", ColoringShapeType.RECTANGLE, "شباك الشاحنة للنظر", x1 = 0.64f, y1 = 0.47f, x2 = 0.72f, y2 = 0.54f),
                // Under Guard Rail
                ColoringShapeItem("under_rail", ColoringShapeType.RECTANGLE, "سكة الأمان الأرضية", x1 = 0.23f, y1 = 0.65f, x2 = 0.77f, y2 = 0.68f),
                // Heavy Wheels
                ColoringShapeItem("wheel1", ColoringShapeType.CIRCLE, "إطار شحن خلفي 1", cx = 0.3f, cy = 0.72f, rx = 0.07f),
                ColoringShapeItem("wheel2", ColoringShapeType.CIRCLE, "إطار شحن خلفي 2", cx = 0.45f, cy = 0.72f, rx = 0.07f),
                ColoringShapeItem("wheel3", ColoringShapeType.CIRCLE, "إطار شحن أمامي مفرد", cx = 0.69f, cy = 0.72f, rx = 0.07f)
            )
        ),
        ColoringPage(
            id = "car_bus",
            title = "School Bus",
            titleAr = "حافلة المدرسة المبهجة",
            category = CAT_CARS,
            categoryAr = "مركبات وسيارات",
            iconEmoji = "🚌",
            shapes = listOf(
                // Bus main box
                ColoringShapeItem("bus_box", ColoringShapeType.RECTANGLE, "هيكل حافلة المدرسة الأصفر", x1 = 0.22f, y1 = 0.35f, x2 = 0.78f, y2 = 0.65f),
                // Front driver screen slant
                ColoringShapeItem("front_slant", ColoringShapeType.TRIANGLE, "مقدمة الباص الصديق", points = listOf(PercentOffset(0.78f, 0.48f), PercentOffset(0.83f, 0.54f), PercentOffset(0.78f, 0.65f))),
                // Headlight
                ColoringShapeItem("headlight", ColoringShapeType.CIRCLE, "المصباح المنير", cx = 0.81f, cy = 0.6f, rx = 0.02f),
                // Windows left to right
                ColoringShapeItem("window1", ColoringShapeType.RECTANGLE, "شباك الطالب الأول", x1 = 0.26f, y1 = 0.4f, x2 = 0.35f, y2 = 0.48f),
                ColoringShapeItem("window2", ColoringShapeType.RECTANGLE, "شباك الطالب الثاني", x1 = 0.39f, y1 = 0.4f, x2 = 0.48f, y2 = 0.48f),
                ColoringShapeItem("window3", ColoringShapeType.RECTANGLE, "شباك الطالب الثالث", x1 = 0.52f, y1 = 0.4f, x2 = 0.61f, y2 = 0.48f),
                ColoringShapeItem("window4", ColoringShapeType.RECTANGLE, "شباك الأستاذ المرافق", x1 = 0.65f, y1 = 0.4f, x2 = 0.74f, y2 = 0.48f),
                // Wheels
                ColoringShapeItem("wheel_l", ColoringShapeType.CIRCLE, "عجلة الباص الخلفية", cx = 0.36f, cy = 0.68f, rx = 0.075f),
                ColoringShapeItem("wheel_r", ColoringShapeType.CIRCLE, "عجلة الباص الأمامية", cx = 0.66f, cy = 0.68f, rx = 0.075f)
            )
        ),
        ColoringPage(
            id = "car_rocket",
            title = "Space Rocket",
            titleAr = "الصاروخ الفضائي المغامر",
            category = CAT_CARS,
            categoryAr = "مركبات وسيارات",
            iconEmoji = "🚀",
            shapes = listOf(
                // Left Space thruster fin
                ColoringShapeItem("left_fin", ColoringShapeType.TRIANGLE, "جناح صاروخ أيسر", points = listOf(PercentOffset(0.4f, 0.65f), PercentOffset(0.31f, 0.74f), PercentOffset(0.42f, 0.74f))),
                // Right Space thruster fin
                ColoringShapeItem("right_fin", ColoringShapeType.TRIANGLE, "جناح صاروخ أيمن", points = listOf(PercentOffset(0.6f, 0.65f), PercentOffset(0.69f, 0.74f), PercentOffset(0.58f, 0.74f))),
                // Booster bottom nozzle
                ColoringShapeItem("booster", ColoringShapeType.RECTANGLE, "محرك الدفع الصاروخي", x1 = 0.46f, y1 = 0.74f, x2 = 0.54f, y2 = 0.79f),
                // Thrust Flame
                ColoringShapeItem("flame", ColoringShapeType.TRIANGLE, "لهب النار النفاث", points = listOf(PercentOffset(0.45f, 0.79f), PercentOffset(0.55f, 0.79f), PercentOffset(0.5f, 0.89f))),
                // Main Fuselage Cone body
                ColoringShapeItem("rocket_body", ColoringShapeType.OVAL, "جسم الصاروخ المعدني", cx = 0.5f, cy = 0.52f, rx = 0.11f, ry = 0.24f),
                // Nose Cap Cone
                ColoringShapeItem("nose_cone", ColoringShapeType.TRIANGLE, "مقدمة صاروخ الفضاء", points = listOf(PercentOffset(0.42f, 0.35f), PercentOffset(0.5f, 0.2f), PercentOffset(0.58f, 0.35f))),
                // Circular Astronaut window
                ColoringShapeItem("window", ColoringShapeType.CIRCLE, "نافذة رائد الفضاء الكيوت", cx = 0.5f, cy = 0.46f, rx = 0.045f),
                ColoringShapeItem("window_glass", ColoringShapeType.CIRCLE, "الزجاج الواقي للرؤية", cx = 0.5f, cy = 0.46f, rx = 0.035f),
                // Outer space stars background
                ColoringShapeItem("star1", ColoringShapeType.STAR, "نجمة الفضاء البعيدة 1", points = getStarPoints(0.24f, 0.3f, 0.035f, 0.015f)),
                ColoringShapeItem("star2", ColoringShapeType.STAR, "نجمة الفضاء البعيدة 2", points = getStarPoints(0.76f, 0.45f, 0.04f, 0.018f))
            )
        ),

        // ==========================================
        // 3. SUPERHEROES (5 Pages)
        // ==========================================
        ColoringPage(
            id = "super_shield",
            title = "Superhero Shield",
            titleAr = "درع البطل الخارق القوي",
            category = CAT_SUPERHEROES,
            categoryAr = "أبطال خارقين",
            iconEmoji = "🛡️",
            shapes = listOf(
                // Concentric circles starting from outer
                ColoringShapeItem("ring1", ColoringShapeType.CIRCLE, "طوق الدرع الخارجي الأحمر", cx = 0.5f, cy = 0.5f, rx = 0.35f),
                ColoringShapeItem("ring2", ColoringShapeType.CIRCLE, "الطوق الأوسط الفضي والمعدني", cx = 0.5f, cy = 0.5f, rx = 0.28f),
                ColoringShapeItem("ring3", ColoringShapeType.CIRCLE, "الطوق الداخلي الأزرق", cx = 0.5f, cy = 0.5f, rx = 0.21f),
                ColoringShapeItem("star_center", ColoringShapeType.STAR, "نجمة البطولة والشجاعة اللامعة", points = getStarPoints(0.5f, 0.5f, 0.16f, 0.07f))
            )
        ),
        ColoringPage(
            id = "super_mask",
            title = "Hero Mask",
            titleAr = "قناع البطل الطائر السري",
            category = CAT_SUPERHEROES,
            categoryAr = "أبطال خارقين",
            iconEmoji = "🎭",
            shapes = listOf(
                // Mask Background winged design
                ColoringShapeItem("mask_wings", ColoringShapeType.POLYGON, "أجنحة قناع البطل الأنيقة", points = listOf(
                    PercentOffset(0.22f, 0.5f), PercentOffset(0.32f, 0.33f),
                    PercentOffset(0.5f, 0.41f), PercentOffset(0.68f, 0.33f),
                    PercentOffset(0.78f, 0.5f), PercentOffset(0.64f, 0.65f),
                    PercentOffset(0.5f, 0.57f), PercentOffset(0.36f, 0.65f)
                )),
                // Forehead Crest star
                ColoringShapeItem("forehead_star", ColoringShapeType.STAR, "شعار جبهة القناع المميز", points = getStarPoints(0.5f, 0.46f, 0.045f, 0.02f)),
                // Left Eye cut-out
                ColoringShapeItem("left_eye_cutout", ColoringShapeType.OVAL, "فتحة العين اليسرى الحادة", cx = 0.4f, cy = 0.5f, rx = 0.06f, ry = 0.025f),
                // Right Eye cut-out
                ColoringShapeItem("right_eye_cutout", ColoringShapeType.OVAL, "فتحة العين اليمنى الحادة", cx = 0.6f, cy = 0.5f, rx = 0.06f, ry = 0.025f)
            )
        ),
        ColoringPage(
            id = "super_lightning",
            title = "Lightning Flash",
            titleAr = "شعار البرق الخاطف الأسرع",
            category = CAT_SUPERHEROES,
            categoryAr = "أبطال خارقين",
            iconEmoji = "⚡",
            shapes = listOf(
                // Circular energy background shield
                ColoringShapeItem("plate", ColoringShapeType.CIRCLE, "درع حماية الطاقة المستدير", cx = 0.5f, cy = 0.5f, rx = 0.34f),
                ColoringShapeItem("inner_plate", ColoringShapeType.CIRCLE, "طاقة صاعقة البرق", cx = 0.5f, cy = 0.5f, rx = 0.30f),
                // Sharp Lightning bolt shape
                ColoringShapeItem("lightning_bolt", ColoringShapeType.POLYGON, "رمز البرق الخاطف الصاعق والمبهر", points = listOf(
                    PercentOffset(0.51f, 0.22f), PercentOffset(0.66f, 0.38f),
                    PercentOffset(0.54f, 0.48f), PercentOffset(0.63f, 0.54f),
                    PercentOffset(0.44f, 0.77f), PercentOffset(0.48f, 0.56f),
                    PercentOffset(0.38f, 0.52f), PercentOffset(0.48f, 0.38f)
                ))
            )
        ),
        ColoringPage(
            id = "super_star",
            title = "Super Star Badge",
            titleAr = "وسام نجمة الأبطال الذهبية",
            category = CAT_SUPERHEROES,
            categoryAr = "أبطال خارقين",
            iconEmoji = "⭐",
            shapes = listOf(
                // Star shape concentric 1 (Outer)
                ColoringShapeItem("star_outer", ColoringShapeType.STAR, "نجمة البطولة الخارجية الكبيرة", points = getStarPoints(0.5f, 0.5f, 0.34f, 0.16f)),
                // Star shape concentric 2 (Inner)
                ColoringShapeItem("star_inner", ColoringShapeType.STAR, "النجمة الذهبية الداخلية المنيرة", points = getStarPoints(0.5f, 0.5f, 0.24f, 0.11f)),
                // Smallest core gem
                ColoringShapeItem("core_gem", ColoringShapeType.CIRCLE, "جوهرة القوة والشرف الحمراء", cx = 0.5f, cy = 0.5f, rx = 0.05f)
            )
        ),
        ColoringPage(
            id = "super_fist",
            title = "Steel Fist Frame",
            titleAr = "شعار القبضة الفولاذية القوية",
            category = CAT_SUPERHEROES,
            categoryAr = "أبطال خارقين",
            iconEmoji = "✊",
            shapes = listOf(
                // Hexagonal Shield Backing
                ColoringShapeItem("hex_shield", ColoringShapeType.POLYGON, "شعار درع الحماية السداسي المتين", points = listOf(
                    PercentOffset(0.5f, 0.2f), PercentOffset(0.76f, 0.33f),
                    PercentOffset(0.76f, 0.67f), PercentOffset(0.5f, 0.8f),
                    PercentOffset(0.24f, 0.67f), PercentOffset(0.24f, 0.33f)
                )),
                // Inner Shield Frame
                ColoringShapeItem("inner_shield", ColoringShapeType.POLYGON, "المعدن الداخلي المقاوم للضربات", points = listOf(
                    PercentOffset(0.5f, 0.24f), PercentOffset(0.72f, 0.36f),
                    PercentOffset(0.72f, 0.64f), PercentOffset(0.5f, 0.76f),
                    PercentOffset(0.28f, 0.64f), PercentOffset(0.28f, 0.36f)
                )),
                // Cartoon Fist - wrist/arm
                ColoringShapeItem("wrist", ColoringShapeType.RECTANGLE, "ساعد وبطل القبضة الفولاذية", x1 = 0.44f, y1 = 0.58f, x2 = 0.56f, y2 = 0.72f),
                // Fist Knuckles/Box
                ColoringShapeItem("fist_box", ColoringShapeType.OVAL, "مجموع مفاصل قبضة الشجاعة", cx = 0.5f, cy = 0.48f, rx = 0.13f, ry = 0.10f),
                // Thumb folded
                ColoringShapeItem("thumb", ColoringShapeType.OVAL, "إصبع الإبهام المغلق للثقة", cx = 0.41f, cy = 0.52f, rx = 0.05f, ry = 0.035f)
            )
        ),

        // ==========================================
        // 4. NATURE (5 Pages)
        // ==========================================
        ColoringPage(
            id = "nature_tree",
            title = "Apple Tree",
            titleAr = "شجرة التفاح السعيدة الغنية",
            category = CAT_NATURE,
            categoryAr = "طبيعة وجمال",
            iconEmoji = "🌳",
            shapes = listOf(
                // Trunk
                ColoringShapeItem("trunk", ColoringShapeType.RECTANGLE, "جذع شجرة البندق الخشبي", x1 = 0.45f, y1 = 0.58f, x2 = 0.55f, y2 = 0.82f),
                // Left Root slant
                ColoringShapeItem("root_l", ColoringShapeType.TRIANGLE, "جذر أيسر ثابت بالتربة", points = listOf(PercentOffset(0.45f, 0.75f), PercentOffset(0.38f, 0.82f), PercentOffset(0.45f, 0.82f))),
                // Right Root slant
                ColoringShapeItem("root_r", ColoringShapeType.TRIANGLE, "جذر أيمن ثابت بالتربة", points = listOf(PercentOffset(0.55f, 0.75f), PercentOffset(0.62f, 0.82f), PercentOffset(0.55f, 0.82f))),
                // Broad Foliage Circles
                ColoringShapeItem("foliage_back", ColoringShapeType.OVAL, "أوراق الشجر وسقف الظل", cx = 0.5f, cy = 0.42f, rx = 0.22f, ry = 0.18f),
                ColoringShapeItem("foliage_l", ColoringShapeType.CIRCLE, "أوراق الجانب الأيسر", cx = 0.42f, cy = 0.4f, rx = 0.11f),
                ColoringShapeItem("foliage_r", ColoringShapeType.CIRCLE, "أوراق الجانب الأيمن", cx = 0.58f, cy = 0.4f, rx = 0.11f),
                ColoringShapeItem("foliage_top", ColoringShapeType.CIRCLE, "أوراق هرم الشجرة من فوق", cx = 0.5f, cy = 0.3f, rx = 0.10f),
                // Red delicious apples
                ColoringShapeItem("apple1", ColoringShapeType.CIRCLE, "تفاحة حمراء طازجة 1", cx = 0.4f, cy = 0.38f, rx = 0.025f),
                ColoringShapeItem("apple2", ColoringShapeType.CIRCLE, "تفاحة حمراء طازجة 2", cx = 0.58f, cy = 0.44f, rx = 0.025f),
                ColoringShapeItem("apple3", ColoringShapeType.CIRCLE, "تفاحة حمراء طازجة 3", cx = 0.48f, cy = 0.46f, rx = 0.025f),
                ColoringShapeItem("apple4", ColoringShapeType.CIRCLE, "تفاحة حمراء طازجة 4", cx = 0.52f, cy = 0.33f, rx = 0.025f),
                // Warm sun background
                ColoringShapeItem("sun", ColoringShapeType.CIRCLE, "شمس الصباح الساطعة والدافئة", cx = 0.78f, cy = 0.22f, rx = 0.075f)
            )
        ),
        ColoringPage(
            id = "nature_flower",
            title = "Happy Flower",
            titleAr = "الزهرة السعيدة النشيطة",
            category = CAT_NATURE,
            categoryAr = "طبيعة وجمال",
            iconEmoji = "🌻",
            shapes = listOf(
                // Flower Stem
                ColoringShapeItem("stem", ColoringShapeType.RECTANGLE, "ساق الزهرة الأخضر المروي", x1 = 0.48f, y1 = 0.56f, x2 = 0.52f, y2 = 0.85f),
                // Leaf Left
                ColoringShapeItem("leaf_l", ColoringShapeType.POLYGON, "ورقة خضراء يسرى لطيفة", points = listOf(PercentOffset(0.48f, 0.68f), PercentOffset(0.38f, 0.62f), PercentOffset(0.48f, 0.76f))),
                // Leaf Right
                ColoringShapeItem("leaf_r", ColoringShapeType.POLYGON, "ورقة خضراء يمنى لطيفة", points = listOf(PercentOffset(0.52f, 0.72f), PercentOffset(0.62f, 0.66f), PercentOffset(0.52f, 0.80f))),
                // Flower Petals Surrounding
                ColoringShapeItem("petal_t", ColoringShapeType.CIRCLE, "البتلة العلوية الزاهية", cx = 0.5f, cy = 0.32f, rx = 0.075f),
                ColoringShapeItem("petal_b", ColoringShapeType.CIRCLE, "البتلة السفلية الزاهية", cx = 0.5f, cy = 0.56f, rx = 0.075f),
                ColoringShapeItem("petal_l", ColoringShapeType.CIRCLE, "البتلة اليسرى الزاهية", cx = 0.38f, cy = 0.44f, rx = 0.075f),
                ColoringShapeItem("petal_r", ColoringShapeType.CIRCLE, "البتلة اليمنى الزاهية", cx = 0.62f, cy = 0.44f, rx = 0.075f),
                ColoringShapeItem("petal_tl", ColoringShapeType.CIRCLE, "البتلة الفرعية يسرى فوق", cx = 0.42f, cy = 0.35f, rx = 0.07f),
                ColoringShapeItem("petal_tr", ColoringShapeType.CIRCLE, "البتلة الفرعية يمنى فوق", cx = 0.58f, cy = 0.35f, rx = 0.07f),
                ColoringShapeItem("petal_bl", ColoringShapeType.CIRCLE, "البتلة الفرعية يسرى تحت", cx = 0.42f, cy = 0.53f, rx = 0.07f),
                ColoringShapeItem("petal_br", ColoringShapeType.CIRCLE, "البتلة الفرعية يمنى تحت", cx = 0.58f, cy = 0.53f, rx = 0.07f),
                // Core Center
                ColoringShapeItem("core_center", ColoringShapeType.CIRCLE, "قلب الزهرة الذهبي العطِر", cx = 0.5f, cy = 0.44f, rx = 0.10f),
                // Smiling face on core
                ColoringShapeItem("left_eye", ColoringShapeType.CIRCLE, "عين بتلة اليسرى", cx = 0.46f, cy = 0.42f, rx = 0.012f),
                ColoringShapeItem("right_eye", ColoringShapeType.CIRCLE, "عين بتلة اليمنى", cx = 0.54f, cy = 0.42f, rx = 0.012f),
                ColoringShapeItem("smile", ColoringShapeType.OVAL, "ابتسامة الزهرة العذبة", cx = 0.5f, cy = 0.47f, rx = 0.024f, ry = 0.012f, isOutlineOnly = true)
            )
        ),
        ColoringPage(
            id = "nature_mushroom",
            title = "Magic Mushroom",
            titleAr = "الفطر العجيب الكرتوني",
            category = CAT_NATURE,
            categoryAr = "طبيعة وجمال",
            iconEmoji = "🍄",
            shapes = listOf(
                // Mushroom Stem
                ColoringShapeItem("stem", ColoringShapeType.POLYGON, "ساق فطر الغابة الأبيض", points = listOf(
                    PercentOffset(0.43f, 0.54f), PercentOffset(0.57f, 0.54f),
                    PercentOffset(0.6f, 0.84f), PercentOffset(0.4f, 0.84f)
                )),
                // Large Cap
                ColoringShapeItem("mushroom_cap", ColoringShapeType.POLYGON, "قبعة الفطر الخيالية الكبيرة", points = listOf(
                    PercentOffset(0.24f, 0.54f), PercentOffset(0.31f, 0.32f),
                    PercentOffset(0.5f, 0.24f), PercentOffset(0.69f, 0.32f),
                    PercentOffset(0.76f, 0.54f)
                )),
                // Cap polka dots circles
                ColoringShapeItem("dot1", ColoringShapeType.CIRCLE, "بقعة فطر بيضاء عريضة 1", cx = 0.38f, cy = 0.38f, rx = 0.038f),
                ColoringShapeItem("dot2", ColoringShapeType.CIRCLE, "بقعة فطر بيضاء عريضة 2", cx = 0.62f, cy = 0.38f, rx = 0.038f),
                ColoringShapeItem("dot3", ColoringShapeType.CIRCLE, "بقعة فطر بيضاء عريضة 3", cx = 0.5f, cy = 0.32f, rx = 0.045f),
                ColoringShapeItem("dot4", ColoringShapeType.CIRCLE, "بقعة فطر بيضاء عريضة 4", cx = 0.48f, cy = 0.46f, rx = 0.03f),
                // Ground Grass patch
                ColoringShapeItem("grass", ColoringShapeType.POLYGON, "عشب المرج الأخضر الطيب", points = listOf(
                    PercentOffset(0.3f, 0.84f), PercentOffset(0.38f, 0.8f),
                    PercentOffset(0.52f, 0.84f), PercentOffset(0.58f, 0.79f),
                    PercentOffset(0.7f, 0.84f), PercentOffset(0.64f, 0.81f)
                ))
            )
        ),
        ColoringPage(
            id = "nature_rainbow",
            title = "Rainbow Cloud",
            titleAr = "السحابة وقوس قزح اللطيف",
            category = CAT_NATURE,
            categoryAr = "طبيعة وجمال",
            iconEmoji = "🌈",
            shapes = listOf(
                // Rainbow Arch 1 (Outer) - represented as larger oval
                ColoringShapeItem("arch_red", ColoringShapeType.OVAL, "قوس الألوان الأحمر", cx = 0.5f, cy = 0.44f, rx = 0.35f, ry = 0.22f, isOutlineOnly = false),
                ColoringShapeItem("arch_orange", ColoringShapeType.OVAL, "قوس الألوان البرتقالي", cx = 0.5f, cy = 0.45f, rx = 0.31f, ry = 0.19f),
                ColoringShapeItem("arch_yellow", ColoringShapeType.OVAL, "قوس الألوان الأصفر", cx = 0.5f, cy = 0.46f, rx = 0.27f, ry = 0.16f),
                ColoringShapeItem("arch_green", ColoringShapeType.OVAL, "قوس الألوان الأخضر المريح", cx = 0.5f, cy = 0.47f, rx = 0.23f, ry = 0.13f),
                // Core center block out
                ColoringShapeItem("sky_core", ColoringShapeType.OVAL, "مركز السماء الزهري", cx = 0.5f, cy = 0.54f, rx = 0.18f, ry = 0.10f),
                // Raindrop left
                ColoringShapeItem("drop1", ColoringShapeType.TRIANGLE, "قطرة مطر عذبة 1", points = listOf(PercentOffset(0.28f, 0.65f), PercentOffset(0.32f, 0.72f), PercentOffset(0.30f, 0.75f))),
                // Raindrop right
                ColoringShapeItem("drop2", ColoringShapeType.TRIANGLE, "قطرة مطر عذبة 2", points = listOf(PercentOffset(0.68f, 0.65f), PercentOffset(0.72f, 0.72f), PercentOffset(0.70f, 0.75f))),
                // Left Fluffy Cloud
                ColoringShapeItem("cloud_l_base", ColoringShapeType.OVAL, "سحابة يسرى قطنية", cx = 0.33f, cy = 0.58f, rx = 0.11f, ry = 0.08f),
                ColoringShapeItem("cloud_l_puff", ColoringShapeType.CIRCLE, "قمة سحابة يسرى ناعمة", cx = 0.33f, cy = 0.52f, rx = 0.07f),
                // Right Fluffy Cloud
                ColoringShapeItem("cloud_r_base", ColoringShapeType.OVAL, "سحابة يمنى قطنية", cx = 0.67f, cy = 0.58f, rx = 0.11f, ry = 0.08f),
                ColoringShapeItem("cloud_r_puff", ColoringShapeType.CIRCLE, "قمة سحابة يمنى ناعمة", cx = 0.67f, cy = 0.52f, rx = 0.07f)
            )
        ),
        ColoringPage(
            id = "nature_island",
            title = "Sunny Island",
            titleAr = "الجزيرة المشمسة الجميلة",
            category = CAT_NATURE,
            categoryAr = "طبيعة وجمال",
            iconEmoji = "🏝️",
            shapes = listOf(
                // Sun
                ColoringShapeItem("sun", ColoringShapeType.CIRCLE, "شمس الجزيرة المشرقة", cx = 0.25f, cy = 0.26f, rx = 0.08f),
                // Sea background water
                ColoringShapeItem("sea", ColoringShapeType.RECTANGLE, "البحر الواسع الصافي والعميق", x1 = 0.18f, y1 = 0.68f, x2 = 0.82f, y2 = 0.78f),
                // Sandy Island Mound
                ColoringShapeItem("island_sand", ColoringShapeType.OVAL, "رمال الشاطئ الذهبية للجزيرة", cx = 0.5f, cy = 0.68f, rx = 0.24f, ry = 0.09f),
                // Palm Tree Trunk
                ColoringShapeItem("palm_trunk", ColoringShapeType.POLYGON, "جذع نخلة الجزيرة المنحني الأنيق", points = listOf(
                    PercentOffset(0.48f, 0.66f), PercentOffset(0.53f, 0.66f),
                    PercentOffset(0.45f, 0.42f), PercentOffset(0.41f, 0.42f)
                )),
                // Palm Leaves
                ColoringShapeItem("leaf1", ColoringShapeType.OVAL, "ورقة نخلة يسارية طويلة", cx = 0.34f, cy = 0.38f, rx = 0.10f, ry = 0.038f),
                ColoringShapeItem("leaf2", ColoringShapeType.OVAL, "ورقة نخلة يمينية طويلة", cx = 0.52f, cy = 0.38f, rx = 0.10f, ry = 0.038f),
                ColoringShapeItem("leaf3", ColoringShapeType.OVAL, "سعفة قمة النخلة العلية", cx = 0.43f, cy = 0.32f, rx = 0.045f, ry = 0.08f),
                // Coconuts
                ColoringShapeItem("coconut1", ColoringShapeType.CIRCLE, "حبة جوز الهند اللذيذة", cx = 0.41f, cy = 0.44f, rx = 0.022f),
                ColoringShapeItem("coconut2", ColoringShapeType.CIRCLE, "ثمرة جوز الهند الثانية", cx = 0.45f, cy = 0.44f, rx = 0.022f)
            )
        ),

        // ==========================================
        // 5. CARTOON CHARACTERS (5 Pages)
        // ==========================================
        ColoringPage(
            id = "cartoon_robot",
            title = "Baby Robot",
            titleAr = "الرجل الآلي بيبي الصديق",
            category = CAT_CARTOON,
            categoryAr = "شخصيات كرتونية",
            iconEmoji = "🤖",
            shapes = listOf(
                // Antenna stem
                ColoringShapeItem("antenna_wire", ColoringShapeType.RECTANGLE, "سلك اتصال الهوائي", x1 = 0.49f, y1 = 0.2f, x2 = 0.51f, y2 = 0.28f),
                // Antenna yellow core ball
                ColoringShapeItem("antenna_ball", ColoringShapeType.CIRCLE, "كرة إشارة الهوائي الدائرية", cx = 0.5f, cy = 0.18f, rx = 0.03f),
                // Screen Head Cube
                ColoringShapeItem("head_screen", ColoringShapeType.RECTANGLE, "حاسوب رأس اللعبة الآلية", x1 = 0.34f, y1 = 0.28f, x2 = 0.66f, y2 = 0.48f),
                // Head Left ear connector bolt
                ColoringShapeItem("left_bolt", ColoringShapeType.OVAL, "برغي تركيب أيسر", cx = 0.32f, cy = 0.38f, rx = 0.018f, ry = 0.03f),
                // Head Right ear connector bolt
                ColoringShapeItem("right_bolt", ColoringShapeType.OVAL, "برغي تركيب أيمن", cx = 0.68f, cy = 0.38f, rx = 0.018f, ry = 0.03f),
                // Robot Main body body panel
                ColoringShapeItem("robot_body", ColoringShapeType.RECTANGLE, "هيكل الرجل الآلي المصنع", x1 = 0.36f, y1 = 0.52f, x2 = 0.64f, y2 = 0.74f),
                // Screen eyes (Round luminous buttons)
                ColoringShapeItem("left_eye", ColoringShapeType.CIRCLE, "عين شاشة يسارية مستديرة", cx = 0.43f, cy = 0.36f, rx = 0.03f),
                ColoringShapeItem("right_eye", ColoringShapeType.CIRCLE, "عين شاشة يمينية مستديرة", cx = 0.57f, cy = 0.36f, rx = 0.03f),
                // Screen cute mouth bar
                ColoringShapeItem("mouth", ColoringShapeType.RECTANGLE, "فم الشاشة المبتسم السعيد", x1 = 0.45f, y1 = 0.43f, x2 = 0.55f, y2 = 0.45f),
                // Chest power buttons indicators
                ColoringShapeItem("indicator1", ColoringShapeType.CIRCLE, "زر الطاقة الأحمر", cx = 0.44f, cy = 0.6f, rx = 0.025f),
                ColoringShapeItem("indicator2", ColoringShapeType.CIRCLE, "زر الطاقة الأخضر", cx = 0.56f, cy = 0.6f, rx = 0.025f),
                ColoringShapeItem("chest_gauge", ColoringShapeType.RECTANGLE, "مؤشر الشحن الأمامي", x1 = 0.42f, y1 = 0.66f, x2 = 0.58f, y2 = 0.70f),
                // Metal legs wheels
                ColoringShapeItem("leg_l", ColoringShapeType.RECTANGLE, "ساق معدنية يسرى مقاومة", x1 = 0.41f, y1 = 0.74f, x2 = 0.46f, y2 = 0.81f),
                ColoringShapeItem("leg_r", ColoringShapeType.RECTANGLE, "ساق معدنية يمنى مقاومة", x1 = 0.54f, y1 = 0.74f, x2 = 0.59f, y2 = 0.81f),
                ColoringShapeItem("foot_l", ColoringShapeType.CIRCLE, "عجلة حركة يسارية مرنة", cx = 0.435f, cy = 0.82f, rx = 0.035f),
                ColoringShapeItem("foot_r", ColoringShapeType.CIRCLE, "عجلة حركة يمينية مرنة", cx = 0.565f, cy = 0.82f, rx = 0.035f)
            )
        ),
        ColoringPage(
            id = "cartoon_monster",
            title = "Cute Candy Monster",
            titleAr = "وحش الحلوى اللطيف بابي",
            category = CAT_CARTOON,
            categoryAr = "شخصيات كرتونية",
            iconEmoji = "👾",
            shapes = listOf(
                // Left Cute Horn
                ColoringShapeItem("horn_l", ColoringShapeType.TRIANGLE, "قرن الوحش الأيسر اللعوب", points = listOf(PercentOffset(0.38f, 0.35f), PercentOffset(0.33f, 0.22f), PercentOffset(0.44f, 0.32f))),
                // Right Cute Horn
                ColoringShapeItem("horn_r", ColoringShapeType.TRIANGLE, "قرن الوحش الأيمن اللعوب", points = listOf(PercentOffset(0.62f, 0.35f), PercentOffset(0.67f, 0.22f), PercentOffset(0.56f, 0.32f))),
                // Round blob jelly body
                ColoringShapeItem("blob_body", ColoringShapeType.OVAL, "جسم وحش الهلام والسكاكر", cx = 0.5f, cy = 0.56f, rx = 0.20f, ry = 0.20f),
                // Hands/tentacles
                ColoringShapeItem("hand_l", ColoringShapeType.OVAL, "ذراع يسارية للترحيب", cx = 0.27f, cy = 0.56f, rx = 0.045f, ry = 0.07f),
                ColoringShapeItem("hand_r", ColoringShapeType.OVAL, "ذراع يمينية للترحيب", cx = 0.73f, cy = 0.56f, rx = 0.045f, ry = 0.07f),
                // Feet
                ColoringShapeItem("foot_l", ColoringShapeType.OVAL, "قدم وحش هلامية يسرى", cx = 0.41f, cy = 0.78f, rx = 0.06f, ry = 0.04f),
                ColoringShapeItem("foot_r", ColoringShapeType.OVAL, "قدم وحش هلامية يمنى", cx = 0.59f, cy = 0.78f, rx = 0.06f, ry = 0.04f),
                // Single huge Cyclops sweet eye
                ColoringShapeItem("eye_white", ColoringShapeType.CIRCLE, "العين الكبيرة المتفاجئة", cx = 0.5f, cy = 0.48f, rx = 0.075f),
                ColoringShapeItem("eye_pupil", ColoringShapeType.CIRCLE, "بؤبؤ وحش الحلوى الكحلي", cx = 0.5f, cy = 0.48f, rx = 0.035f),
                ColoringShapeItem("eye_gleam", ColoringShapeType.CIRCLE, "لمعة العين الذهبية الذكية", cx = 0.515f, cy = 0.465f, rx = 0.012f),
                // Smiley mouth with one tooth
                ColoringShapeItem("mouth", ColoringShapeType.OVAL, "الفم الكيوت الضاحك ومخزن السكاكر", cx = 0.5f, cy = 0.62f, rx = 0.06f, ry = 0.03f, isOutlineOnly = false),
                ColoringShapeItem("single_tooth", ColoringShapeType.RECTANGLE, "سن اللعبة الشقي الأوحد", x1 = 0.48f, y1 = 0.59f, x2 = 0.52f, y2 = 0.62f)
            )
        ),
        ColoringPage(
            id = "cartoon_star",
            title = "Smiling Star",
            titleAr = "النجمة المبتسمة تينكر",
            category = CAT_CARTOON,
            categoryAr = "شخصيات كرتونية",
            iconEmoji = "⭐",
            shapes = listOf(
                // Giant 5 pointed custom star
                ColoringShapeItem("main_star", ColoringShapeType.STAR, "جسم النجمة الذهبية المبهج", points = getStarPoints(0.5f, 0.5f, 0.35f, 0.15f)),
                // Right rosy cheek
                ColoringShapeItem("blush_r", ColoringShapeType.CIRCLE, "خد أيمن وردي للخجل", cx = 0.59f, cy = 0.52f, rx = 0.022f),
                // Left rosy cheek
                ColoringShapeItem("blush_l", ColoringShapeType.CIRCLE, "خد أيسر وردي للخجل", cx = 0.41f, cy = 0.52f, rx = 0.022f),
                // High contrast sparkling cartoon eyes
                ColoringShapeItem("eye_l", ColoringShapeType.CIRCLE, "عين النجمة اليسرى اللامعة", cx = 0.44f, cy = 0.46f, rx = 0.022f),
                ColoringShapeItem("eye_r", ColoringShapeType.CIRCLE, "عين النجمة اليمنى اللامعة", cx = 0.56f, cy = 0.46f, rx = 0.022f),
                ColoringShapeItem("sparkle_l", ColoringShapeType.CIRCLE, "لمعة العين اليسرى", cx = 0.45f, cy = 0.45f, rx = 0.008f),
                ColoringShapeItem("sparkle_r", ColoringShapeType.CIRCLE, "لمعة العين اليمنى", cx = 0.57f, cy = 0.45f, rx = 0.008f),
                // Super smiling face
                ColoringShapeItem("mouth", ColoringShapeType.OVAL, "فم النجمة الكيوت السعيد المغرد", cx = 0.5f, cy = 0.53f, rx = 0.035f, ry = 0.022f)
            )
        ),
        ColoringPage(
            id = "cartoon_ghost",
            title = "Friendly Ghost",
            titleAr = "الشبح الطيب الصغير بوو",
            category = CAT_CARTOON,
            categoryAr = "شخصيات كرتونية",
            iconEmoji = "👻",
            shapes = listOf(
                // Flying wispy bottom ribbon tail shapes
                ColoringShapeItem("wisp1", ColoringShapeType.TRIANGLE, "ذيل طائر متموج 1", points = listOf(PercentOffset(0.33f, 0.68f), PercentOffset(0.40f, 0.81f), PercentOffset(0.45f, 0.68f))),
                ColoringShapeItem("wisp2", ColoringShapeType.TRIANGLE, "ذيل طائر متموج 2", points = listOf(PercentOffset(0.45f, 0.68f), PercentOffset(0.51f, 0.83f), PercentOffset(0.57f, 0.68f))),
                ColoringShapeItem("wisp3", ColoringShapeType.TRIANGLE, "ذيل طائر متموج 3", points = listOf(PercentOffset(0.57f, 0.68f), PercentOffset(0.63f, 0.81f), PercentOffset(0.68f, 0.68f))),
                // Main wavy body ghost dome contour
                ColoringShapeItem("ghost_body", ColoringShapeType.POLYGON, "هيكل الشبح الأبيض اللطيف والمحلق", points = listOf(
                    PercentOffset(0.32f, 0.68f), PercentOffset(0.32f, 0.42f),
                    PercentOffset(0.5f, 0.22f), PercentOffset(0.68f, 0.42f),
                    PercentOffset(0.68f, 0.68f)
                )),
                // Float Hands outstretched
                ColoringShapeItem("float_hand_l", ColoringShapeType.OVAL, "يد شبحية يسارية للترحيب", cx = 0.27f, cy = 0.48f, rx = 0.06f, ry = 0.04f),
                ColoringShapeItem("float_hand_r", ColoringShapeType.OVAL, "يد شبحية يمينية للترحيب", cx = 0.73f, cy = 0.48f, rx = 0.06f, ry = 0.04f),
                // Eyes
                ColoringShapeItem("ghost_eye_l", ColoringShapeType.CIRCLE, "عين شبح مستديرة يسرى", cx = 0.44f, cy = 0.4f, rx = 0.025f),
                ColoringShapeItem("ghost_eye_r", ColoringShapeType.CIRCLE, "عين شبح مستديرة يمنى", cx = 0.56f, cy = 0.4f, rx = 0.025f),
                // Cheeks
                ColoringShapeItem("ghost_blush_l", ColoringShapeType.CIRCLE, "خدود دافئة ترحب بالطفل", cx = 0.39f, cy = 0.45f, rx = 0.015f),
                ColoringShapeItem("ghost_blush_r", ColoringShapeType.CIRCLE, "خدود دافئة ترحب بالطفل", cx = 0.61f, cy = 0.45f, rx = 0.015f),
                // Mouth yelling "BOO!" but laughing
                ColoringShapeItem("ghost_mouth", ColoringShapeType.OVAL, "فم الشبح الدائري السعيد يغني بوو", cx = 0.5f, cy = 0.46f, rx = 0.035f, ry = 0.035f)
            )
        ),
        ColoringPage(
            id = "cartoon_starfish",
            title = "Bubbles Starfish",
            titleAr = "نجمة البحر بابلز المرحة",
            category = CAT_CARTOON,
            categoryAr = "شخصيات كرتونية",
            iconEmoji = "🌟",
            shapes = listOf(
                // Thick rounded 5 arms starfish
                ColoringShapeItem("starfish_body", ColoringShapeType.STAR, "جسم نجمة البحر الوردي المائي", points = getStarPoints(0.5f, 0.5f, 0.34f, 0.17f, 5)),
                // Cute suction dot decorations on limbs
                ColoringShapeItem("suck_dot1", ColoringShapeType.CIRCLE, "نقطة تزيين مائية 1", cx = 0.50f, cy = 0.24f, rx = 0.018f),
                ColoringShapeItem("suck_dot2", ColoringShapeType.CIRCLE, "نقطة تزيين مائية 2", cx = 0.25f, cy = 0.43f, rx = 0.018f),
                ColoringShapeItem("suck_dot3", ColoringShapeType.CIRCLE, "نقطة تزيين مائية 3", cx = 0.75f, cy = 0.43f, rx = 0.018f),
                ColoringShapeItem("suck_dot4", ColoringShapeType.CIRCLE, "نقطة تزيين مائية 4", cx = 0.36f, cy = 0.72f, rx = 0.018f),
                ColoringShapeItem("suck_dot5", ColoringShapeType.CIRCLE, "نقطة تزيين مائية 5", cx = 0.64f, cy = 0.72f, rx = 0.018f),
                // Eyes
                ColoringShapeItem("eye_l", ColoringShapeType.CIRCLE, "مقلة العين اليسرى لثقب قاع البحر", cx = 0.45f, cy = 0.48f, rx = 0.02f),
                ColoringShapeItem("eye_r", ColoringShapeType.CIRCLE, "مقلة العين اليمنى لثقب قاع البحر", cx = 0.55f, cy = 0.48f, rx = 0.02f),
                // Cute open mouth smiling
                ColoringShapeItem("mouth", ColoringShapeType.OVAL, "فم نجمة البحر المبتسم والضاحك", cx = 0.5f, cy = 0.55f, rx = 0.035f, ry = 0.02f)
            )
        ),

        // ==========================================
        // 6. EDUCATIONAL OBJECTS (5 Pages)
        // ==========================================
        ColoringPage(
            id = "edu_book",
            title = "Book of Wisdom",
            titleAr = "كتاب المعرفة والنور العجيب",
            category = CAT_EDUCATIONAL,
            categoryAr = "أشياء تعليمية",
            iconEmoji = "📖",
            shapes = listOf(
                // Book Hardcover backdrop
                ColoringShapeItem("cover", ColoringShapeType.RECTANGLE, "غلاف الكتاب المقوى الحامي", x1 = 0.18f, y1 = 0.32f, x2 = 0.82f, y2 = 0.72f),
                // Left Wing Open Pages
                ColoringShapeItem("left_page", ColoringShapeType.POLYGON, "صفحات المعرفة اليسرى", points = listOf(
                    PercentOffset(0.22f, 0.35f), PercentOffset(0.48f, 0.35f),
                    PercentOffset(0.48f, 0.68f), PercentOffset(0.22f, 0.68f)
                )),
                // Right Wing Open Pages
                ColoringShapeItem("right_page", ColoringShapeType.POLYGON, "صفحات المعرفة اليمنى", points = listOf(
                    PercentOffset(0.52f, 0.35f), PercentOffset(0.78f, 0.35f),
                    PercentOffset(0.78f, 0.68f), PercentOffset(0.52f, 0.68f)
                )),
                // Wooden bookmarks ribbon
                ColoringShapeItem("bookmark", ColoringShapeType.POLYGON, "شريط الفاصل الملون الأنيق", points = listOf(
                    PercentOffset(0.49f, 0.35f), PercentOffset(0.51f, 0.35f),
                    PercentOffset(0.51f, 0.78f), PercentOffset(0.49f, 0.78f)
                )),
                // Wisdom Magic star dust
                ColoringShapeItem("magic_spark1", ColoringShapeType.STAR, "شرارة النور التعليمية 1", points = getStarPoints(0.2f, 0.22f, 0.03f, 0.012f)),
                ColoringShapeItem("magic_spark2", ColoringShapeType.STAR, "شرارة النور التعليمية 2", points = getStarPoints(0.8f, 0.22f, 0.03f, 0.012f))
            )
        ),
        ColoringPage(
            id = "edu_apple",
            title = "A is for Apple",
            titleAr = "تفاحة حرف الألف والحروف",
            category = CAT_EDUCATIONAL,
            categoryAr = "أشياء تعليمية",
            iconEmoji = "🍎",
            shapes = listOf(
                // Wood Stem
                ColoringShapeItem("stem", ColoringShapeType.RECTANGLE, "غصن التفاح البني الصغير", x1 = 0.48f, y1 = 0.18f, x2 = 0.52f, y2 = 0.33f),
                // Green Leaf
                ColoringShapeItem("leaf", ColoringShapeType.POLYGON, "ورقة التفاح الخضراء المنعشة", points = listOf(
                    PercentOffset(0.5f, 0.24f), PercentOffset(0.62f, 0.15f),
                    PercentOffset(0.58f, 0.28f)
                )),
                // Giant Apple Heart outline
                ColoringShapeItem("apple_body", ColoringShapeType.OVAL, "جسم التفاحة الغذائي اللذيذ والمطري", cx = 0.5f, cy = 0.58f, rx = 0.24f, ry = 0.22f),
                // Shiny highlight bubble
                ColoringShapeItem("gleam", ColoringShapeType.OVAL, "لمعة الفاكهة الطازجة لغسلها", cx = 0.38f, cy = 0.46f, rx = 0.045f, ry = 0.03f),
                // Giant Alphabet Letter "A" (or "أ" in Arabic) drawn as polygon in center!
                ColoringShapeItem("letter_a", ColoringShapeType.POLYGON, "حرف الألف الأول بمركز التعلم", points = listOf(
                    // Outer triangle A
                    PercentOffset(0.5f, 0.42f), PercentOffset(0.58f, 0.68f),
                    PercentOffset(0.54f, 0.68f), PercentOffset(0.52f, 0.61f),
                    PercentOffset(0.48f, 0.61f), PercentOffset(0.46f, 0.68f),
                    PercentOffset(0.42f, 0.68f)
                )),
                // Letter A inner hollow
                ColoringShapeItem("letter_a_hole", ColoringShapeType.TRIANGLE, "حفرة حرف الألف المعلقة فوق", points = listOf(PercentOffset(0.5f, 0.48f), PercentOffset(0.48f, 0.56f), PercentOffset(0.52f, 0.56f)))
            )
        ),
        ColoringPage(
            id = "edu_clock",
            title = "Schoolday Clock",
            titleAr = "ساعة الوقت والالتزام اليومي",
            category = CAT_EDUCATIONAL,
            categoryAr = "أشياء تعليمية",
            iconEmoji = "⏰",
            shapes = listOf(
                // Bell Left
                ColoringShapeItem("bell_l", ColoringShapeType.OVAL, "جرس التنبيه الصباحي الأيسر", cx = 0.32f, cy = 0.22f, rx = 0.065f, ry = 0.045f),
                // Bell Right
                ColoringShapeItem("bell_r", ColoringShapeType.OVAL, "جرس التنبيه الصباحي الأيمن", cx = 0.68f, cy = 0.22f, rx = 0.065f, ry = 0.045f),
                // Clock stand support feet
                ColoringShapeItem("foot_l", ColoringShapeType.TRIANGLE, "قاعدة الساعة الطاولة اليسرى", points = listOf(PercentOffset(0.33f, 0.72f), PercentOffset(0.26f, 0.81f), PercentOffset(0.38f, 0.76f))),
                // Clock stand support feet
                ColoringShapeItem("foot_r", ColoringShapeType.TRIANGLE, "قاعدة الساعة الطاولة اليمنى", points = listOf(PercentOffset(0.67f, 0.72f), PercentOffset(0.74f, 0.81f), PercentOffset(0.62f, 0.76f))),
                // Outer Metal Ring Dial
                ColoringShapeItem("clock_outer", ColoringShapeType.CIRCLE, "هيكل الساعة المعدني اللامع", cx = 0.5f, cy = 0.52f, rx = 0.26f),
                // Inner White Face
                ColoringShapeItem("clock_face", ColoringShapeType.CIRCLE, "لوحة الأرقام البيضاء والواضحة", cx = 0.5f, cy = 0.52f, rx = 0.22f),
                // Centermost pinning dot
                ColoringShapeItem("dial_pin", ColoringShapeType.CIRCLE, "دبوس تثبيت عقارب الساعة الأوسط", cx = 0.5f, cy = 0.52f, rx = 0.02f),
                // Hour hand pointing to 3
                ColoringShapeItem("hand_hour", ColoringShapeType.RECTANGLE, "عقرب الساعات القصير المشير إلى 3", x1 = 0.5f, y1 = 0.51f, x2 = 0.62f, y2 = 0.53f),
                // Minute hand pointing to 12
                ColoringShapeItem("hand_minute", ColoringShapeType.RECTANGLE, "عقرب الدقائق الطويل المشير إلى 12", x1 = 0.49f, y1 = 0.38f, x2 = 0.51f, y2 = 0.52f)
            )
        ),
        ColoringPage(
            id = "edu_pencil",
            title = "Artist Pencil",
            titleAr = "قلم الرسم والتلوين الذكي",
            category = CAT_EDUCATIONAL,
            categoryAr = "أشياء تعليمية",
            iconEmoji = "✏️",
            shapes = listOf(
                // Eraser tip back
                ColoringShapeItem("eraser", ColoringShapeType.RECTANGLE, "ممحاة القلم الزهرية لتصليح الأخطاء", x1 = 0.22f, y1 = 0.46f, x2 = 0.31f, y2 = 0.54f),
                // Metal band collar
                ColoringShapeItem("metal_band", ColoringShapeType.RECTANGLE, "حلقة معدن القلم الفضية الشدودة", x1 = 0.31f, y1 = 0.46f, x2 = 0.36f, y2 = 0.54f),
                // Pencil main hexagonal shaft
                ColoringShapeItem("pencil_body", ColoringShapeType.RECTANGLE, "عمود خشب القلم الأصفر الطويل", x1 = 0.36f, y1 = 0.46f, x2 = 0.68f, y2 = 0.54f),
                // Sharpened wood collar
                ColoringShapeItem("sharpened_wood", ColoringShapeType.TRIANGLE, "خشب سن القلم الدائري المبري", points = listOf(PercentOffset(0.68f, 0.46f), PercentOffset(0.68f, 0.54f), PercentOffset(0.79f, 0.5f))),
                // Graphite writing tip lead
                ColoringShapeItem("graphite_tip", ColoringShapeType.TRIANGLE, "رأس رصاص القلم لكتابة الواجب ودراسة النور", points = listOf(PercentOffset(0.74f, 0.48f), PercentOffset(0.74f, 0.52f), PercentOffset(0.79f, 0.5f))),
                // Sparking cartoon eyes on pencil body
                ColoringShapeItem("eye_l", ColoringShapeType.CIRCLE, "عين أيسر لقلم الرسم", cx = 0.48f, cy = 0.49f, rx = 0.015f),
                ColoringShapeItem("eye_r", ColoringShapeType.CIRCLE, "عين أيمن لقلم الرسم", cx = 0.54f, cy = 0.49f, rx = 0.015f),
                ColoringShapeItem("smile", ColoringShapeType.OVAL, "ابتسامة دافئة لخربشة وتلوين الكراسات", cx = 0.51f, cy = 0.52f, rx = 0.015f, ry = 0.010f, isOutlineOnly = true)
            )
        ),
        ColoringPage(
            id = "edu_castle",
            title = "Shape Building Blocks",
            titleAr = "شكل هرم الألعاب والتركيب",
            category = CAT_EDUCATIONAL,
            categoryAr = "أشياء تعليمية",
            iconEmoji = "🧱",
            shapes = listOf(
                // Bottom Solid rectangular block foundation
                ColoringShapeItem("base_block", ColoringShapeType.RECTANGLE, "أساس جدران التركيب المربع المتين", x1 = 0.22f, y1 = 0.58f, x2 = 0.78f, y2 = 0.82f),
                // Column Left support
                ColoringShapeItem("column_l", ColoringShapeType.RECTANGLE, "عامود دعم أيسر", x1 = 0.28f, y1 = 0.4f, x2 = 0.38f, y2 = 0.58f),
                // Column Right support
                ColoringShapeItem("column_r", ColoringShapeType.RECTANGLE, "عامود دعم أيمن", x1 = 0.62f, y1 = 0.4f, x2 = 0.72f, y2 = 0.58f),
                // Arch lintel bar
                ColoringShapeItem("arch_lintel", ColoringShapeType.RECTANGLE, "رابط جسر المباني الملون", x1 = 0.38f, y1 = 0.47f, x2 = 0.62f, y2 = 0.58f),
                // Middle Red Circular Window
                ColoringShapeItem("wheel_window", ColoringShapeType.CIRCLE, "نافذة هرم الألعاب المستديرة", cx = 0.50f, cy = 0.52f, rx = 0.038f),
                // Giant Triangular roof spire on top
                ColoringShapeItem("roof_spire", ColoringShapeType.TRIANGLE, "قرميد هرم السقف الأحمر بالأعلى حماية من الأمطار", points = listOf(
                    PercentOffset(0.38f, 0.40f), PercentOffset(0.62f, 0.40f), PercentOffset(0.50f, 0.21f)
                )),
                // Wooden castle arch door
                ColoringShapeItem("castle_door", ColoringShapeType.OVAL, "بوابة ألعاب قلعة الأطفال الخيالية", cx = 0.5f, cy = 0.73f, rx = 0.08f, ry = 0.09f)
            )
        )
    )
}
