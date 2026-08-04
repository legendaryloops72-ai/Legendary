package com.aistudio.kidspolice.abcd.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import java.lang.StringBuilder
import kotlin.math.pow

enum class ColoringToolType {
    FILL, BRUSH, ERASER
}

data class ColoringToolColor(
    val id: String,
    val nameAr: String,
    val emoji: String,
    val color: Color
)

object ColoringTools {
    val palette = listOf(
        ColoringToolColor("red", "أحمر الفراولة", "🍓", Color(0xFFFF3B30)),
        ColoringToolColor("pink", "زهري الحلاوة", "🍬", Color(0xFFFF2D55)),
        ColoringToolColor("orange", "برتقالي الجسر", "🥕", Color(0xFFFF9500)),
        ColoringToolColor("yellow", "أصفر الموز", "🍌", Color(0xFFFFCC00)),
        ColoringToolColor("light_blue", "أزرق السماء", "☁️", Color(0xFF5AC8FA)),
        ColoringToolColor("blue", "أزرق عميق", "🌊", Color(0xFF007AFF)),
        ColoringToolColor("purple", "بنفسجي السحر", "🔮", Color(0xFFAF52DE)),
        ColoringToolColor("green", "أخضر العشب", "🌿", Color(0xFF34C759)),
        ColoringToolColor("mint", "أخضر النعناع", "🌱", Color(0xFF4CD964)),
        ColoringToolColor("brown", "بني الشوكولاتة", "🍫", Color(0xFF8B5A2B)),
        ColoringToolColor("cream", "أبيض الكريمة", "🍦", Color(0xFFFFFDD0)),
        ColoringToolColor("dark", "أسود الفحم", "🕶️", Color(0xFF1C1C1E))
    )

    // Detect if point is inside a polygon (ray-casting algorithm)
    fun isPointInPolygon(px: Float, py: Float, points: List<PercentOffset>): Boolean {
        if (points.isEmpty()) return false
        var intersectCount = 0
        for (i in points.indices) {
            val p1 = points[i]
            val p2 = points[(i + 1) % points.size]
            if (((p1.y > py) != (p2.y > py)) &&
                (px < (p2.x - p1.x) * (py - p1.y) / (p2.y - p1.y + 0.000001f) + p1.x)
            ) {
                intersectCount++
            }
        }
        return intersectCount % 2 != 0
    }

    // Detect if point is inside an oval (ellipse formula: (x-cx)^2/rx^2 + (y-cy)^2/ry^2 <= 1)
    fun isPointInOval(px: Float, py: Float, cx: Float, cy: Float, rx: Float, ry: Float): Boolean {
        if (rx <= 0f || ry <= 0f) return false
        return ((px - cx) / rx).pow(2) + ((py - cy) / ry).pow(2) <= 1.0f
    }

    // Detect if point is inside a circle
    fun isPointInCircle(px: Float, py: Float, cx: Float, cy: Float, radius: Float): Boolean {
        val dx = px - cx
        val dy = py - cy
        return dx * dx + dy * dy <= radius * radius
    }

    // Hit-testing general shapes
    fun hitTest(px: Float, py: Float, shape: ColoringShapeItem): Boolean {
        return when (shape.shapeType) {
            ColoringShapeType.CIRCLE -> {
                isPointInCircle(px, py, shape.cx, shape.cy, shape.rx)
            }
            ColoringShapeType.OVAL -> {
                isPointInOval(px, py, shape.cx, shape.cy, shape.rx, shape.ry)
            }
            ColoringShapeType.RECTANGLE -> {
                val left = Math.min(shape.x1, shape.x2)
                val right = Math.max(shape.x1, shape.x2)
                val top = Math.min(shape.y1, shape.y2)
                val bottom = Math.max(shape.y1, shape.y2)
                px in left..right && py in top..bottom
            }
            ColoringShapeType.STAR, ColoringShapeType.POLYGON, ColoringShapeType.TRIANGLE -> {
                isPointInPolygon(px, py, shape.points)
            }
        }
    }

    // Robust custom string serialization to save strokes state 100% safely without dependencies
    fun serializeStrokes(strokes: List<DrawingStroke>): String {
        val sb = StringBuilder()
        strokes.forEachIndexed { i, stroke ->
            if (i > 0) sb.append("#")
            
            // Build points part: x,y;x,y;...
            val ptsSb = StringBuilder()
            stroke.points.forEachIndexed { ptIdx, pt ->
                if (ptIdx > 0) ptsSb.append(";")
                ptsSb.append(String.format("%.4f", pt.x)).append(",").append(String.format("%.4f", pt.y))
            }
            
            sb.append(ptsSb.toString()).append("|")
              .append(stroke.colorArgb).append("|")
              .append(stroke.strokeWidth).append("|")
              .append(if (stroke.isEraser) "1" else "0")
        }
        return sb.toString()
    }

    // Parse back strokes safely
    fun deserializeStrokes(data: String): List<DrawingStroke> {
        if (data.trim().isEmpty()) return emptyList()
        val list = mutableListOf<DrawingStroke>()
        try {
            val chunks = data.split("#")
            chunks.forEach { chunk ->
                val tokens = chunk.split("|")
                if (tokens.size >= 4) {
                    val ptsRaw = tokens[0]
                    val colorArgb = tokens[1].toIntOrNull() ?: android.graphics.Color.BLACK
                    val strokeW = tokens[2].toFloatOrNull() ?: 12f
                    val isEraser = tokens[3] == "1"

                    // Parse points
                    val pointsList = mutableListOf<DrawingPoint>()
                    if (ptsRaw.isNotEmpty()) {
                        val ptPairs = ptsRaw.split(";")
                        ptPairs.forEach { pair ->
                            val coords = pair.split(",")
                            if (coords.size == 2) {
                                val x = coords[0].toFloatOrNull() ?: 0f
                                val y = coords[1].toFloatOrNull() ?: 0f
                                pointsList.add(DrawingPoint(x, y))
                            }
                        }
                    }
                    if (pointsList.isNotEmpty()) {
                        list.add(DrawingStroke(pointsList, colorArgb, strokeW, isEraser))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
