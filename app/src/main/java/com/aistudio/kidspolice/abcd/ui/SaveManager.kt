package com.aistudio.kidspolice.abcd.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path as AndroidPath
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

data class SavedDrawingMetadata(
    val pageId: String,
    val savedAtTimestamp: Long
)

// Representations for drawing strokes
data class DrawingPoint(val x: Float, val y: Float)

data class DrawingStroke(
    val points: List<DrawingPoint>,
    val colorArgb: Int,
    val strokeWidth: Float,
    val isEraser: Boolean = false
)

object SaveManager {
    private const val PREFS_NAME = "kids_coloring_prefs"
    private const val KEY_FAVORITES = "coloring_favorites"
    private const val KEY_RECENTS = "coloring_recents"

    fun isFavorite(context: Context, pageId: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val favs = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
        return favs.contains(pageId)
    }

    fun toggleFavorite(context: Context, pageId: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val favs = (prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()).toMutableSet()
        val isFav = if (favs.contains(pageId)) {
            favs.remove(pageId)
            false
        } else {
            favs.add(pageId)
            true
        }
        prefs.edit().putStringSet(KEY_FAVORITES, favs).apply()
        return isFav
    }

    fun getFavorites(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun addToRecents(context: Context, pageId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val recents = (prefs.getStringSet(KEY_RECENTS, emptySet()) ?: emptySet()).toMutableList()
        
        recents.remove(pageId) // Remove if exists to bring to top
        recents.add(0, pageId) // Insert at top
        
        // Keep last 15 drawings maximum
        val trimmed = if (recents.size > 15) recents.subList(0, 15) else recents
        prefs.edit().putStringSet(KEY_RECENTS, trimmed.toSet()).apply()
    }

    fun getRecents(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawSet = prefs.getStringSet(KEY_RECENTS, emptySet()) ?: emptySet()
        return rawSet.toList()
    }

    // Saving and Loading coloring progress dynamically so kids can resume anytime!
    fun savePageColoring(
        context: Context,
        pageId: String,
        shapeColors: Map<String, Int>, // shapeId to Color Int
        strokesJsonString: String // stroke points represented as simple custom structures
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Save shape colors
        val colorPrefsKey = "colors_$pageId"
        val editor = prefs.edit()
        
        shapeColors.forEach { (shapeId, colorInt) ->
            editor.putInt("${colorPrefsKey}_$shapeId", colorInt)
        }
        
        // Also save the overall set of customized shape keys to read back
        editor.putStringSet("shapes_${pageId}", shapeColors.keys)
        editor.putString("strokes_$pageId", strokesJsonString)
        editor.apply()
        
        addToRecents(context, pageId)
    }

    fun loadPageColoring(context: Context, pageId: String): Pair<Map<String, Int>, String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val colorPrefsKey = "colors_$pageId"
        val shapeKeys = prefs.getStringSet("shapes_$pageId", emptySet()) ?: emptySet()
        
        val colorMap = mutableMapOf<String, Int>()
        shapeKeys.forEach { shapeId ->
            val colorVal = prefs.getInt("${colorPrefsKey}_$shapeId", android.graphics.Color.WHITE)
            colorMap[shapeId] = colorVal
        }
        
        val strokesJson = prefs.getString("strokes_$pageId", "") ?: ""
        return Pair(colorMap, strokesJson)
    }

    fun clearPageSavedColoring(context: Context, pageId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val shapeKeys = prefs.getStringSet("shapes_$pageId", emptySet()) ?: emptySet()
        
        val editor = prefs.edit()
        shapeKeys.forEach { shapeId ->
            editor.remove("colors_${pageId}_$shapeId")
        }
        editor.remove("shapes_$pageId")
        editor.remove("strokes_$pageId")
        editor.apply()
    }

    // High fidelity Bitmap Generator and MediaStore exporter
    suspend fun saveDrawingToGallery(
        context: Context,
        page: ColoringPage,
        shapeColors: Map<String, Int>,
        strokes: List<DrawingStroke>
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // Create a high resolution image bitmap for the coloring page
            val width = 1200
            val height = 1200
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Fill background with soft white cream sky
            canvas.drawColor(android.graphics.Color.parseColor("#FFFDF6"))

            // Initialize paints
            val fillPaint = Paint().apply {
                style = Paint.Style.FILL
                isAntiAlias = true
            }

            val outlinePaint = Paint().apply {
                style = Paint.Style.STROKE
                color = android.graphics.Color.parseColor("#1E1B4B") // Deep dark blue ink
                strokeWidth = 10f
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                isAntiAlias = true
            }

            val strokePaint = Paint().apply {
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                isAntiAlias = true
            }

            // 1. Draw shapes
            page.shapes.forEach { shape ->
                val colorInt = shapeColors[shape.id] ?: android.graphics.Color.WHITE
                fillPaint.color = colorInt

                // Draw specific geometrics
                when (shape.shapeType) {
                    ColoringShapeType.CIRCLE -> {
                        val cx = shape.cx * width
                        val cy = shape.cy * height
                        val r = shape.rx * width
                        if (!shape.isOutlineOnly) {
                            canvas.drawCircle(cx, cy, r, fillPaint)
                        }
                        canvas.drawCircle(cx, cy, r, outlinePaint)
                    }
                    ColoringShapeType.OVAL -> {
                        val cx = shape.cx * width
                        val cy = shape.cy * height
                        val rx = shape.rx * width
                        val ry = shape.ry * height
                        if (!shape.isOutlineOnly) {
                            canvas.drawOval(cx - rx, cy - ry, cx + rx, cy + ry, fillPaint)
                        }
                        canvas.drawOval(cx - rx, cy - ry, cx + rx, cy + ry, outlinePaint)
                    }
                    ColoringShapeType.RECTANGLE -> {
                        val x1 = shape.x1 * width
                        val y1 = shape.y1 * height
                        val x2 = shape.x2 * width
                        val y2 = shape.y2 * height
                        // Draw rounded rect by default for kid aesthetics
                        if (!shape.isOutlineOnly) {
                            canvas.drawRoundRect(x1, y1, x2, y2, 25f, 25f, fillPaint)
                        }
                        canvas.drawRoundRect(x1, y1, x2, y2, 25f, 25f, outlinePaint)
                    }
                    ColoringShapeType.STAR, ColoringShapeType.POLYGON, ColoringShapeType.TRIANGLE -> {
                        if (shape.points.isNotEmpty()) {
                            val path = AndroidPath()
                            val first = shape.points[0]
                            path.moveTo(first.x * width, first.y * height)
                            for (i in 1 until shape.points.size) {
                                val pt = shape.points[i]
                                path.lineTo(pt.x * width, pt.y * height)
                            }
                            path.close()

                            if (!shape.isOutlineOnly) {
                                canvas.drawPath(path, fillPaint)
                            }
                            canvas.drawPath(path, outlinePaint)
                        }
                    }
                }
            }

            // 2. Clear canvas with erase lines or draws brush lines
            strokes.forEach { stroke ->
                if (stroke.points.size < 2) return@forEach
                
                if (stroke.isEraser) {
                    strokePaint.color = android.graphics.Color.parseColor("#FFFDF6") // Matches canvas background
                } else {
                    strokePaint.color = stroke.colorArgb
                }
                strokePaint.strokeWidth = stroke.strokeWidth / 100f * width // proportional stroke width

                val path = AndroidPath()
                val first = stroke.points[0]
                path.moveTo(first.x * width, first.y * height)
                for (i in 1 until stroke.points.size) {
                    val p = stroke.points[i]
                    path.lineTo(p.x * width, p.y * height)
                }

                canvas.drawPath(path, strokePaint)
            }

            // Draw a cute watermark credits in the bottom corner
            val textPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#4A5568")
                textSize = 32f
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
            }
            canvas.drawText("صُنع بكل حب في شرطة الأطفال 👮✨", width - 40f, height - 40f, textPaint)

            // Save the bitmap to storage using MediaStore
            val filename = "KIDS_COLORING_${page.id}_${System.currentTimeMillis()}.png"
            var outputStream: OutputStream? = null
            var success = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/KidsPoliceColoring")
                }
                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    outputStream = resolver.openOutputStream(imageUri)
                    success = true
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/KidsPoliceColoring"
                val dir = File(imagesDir)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val file = File(dir, filename)
                outputStream = FileOutputStream(file)
                success = true
            }

            if (success && outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                Log.d("SaveManager", "Coloring page exported to gallery successfully.")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("SaveManager", "Failed to save picture to gallery: ${e.message}")
            false
        }
    }
}
