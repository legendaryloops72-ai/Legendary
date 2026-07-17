package com.example.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sound.CallSoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColoringScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedPage by remember { mutableStateOf<ColoringPage?>(null) }
    
    // Bottom tab filters
    var currentFilterTab by remember { mutableStateOf("all") } // "all", "favorites", "recents"
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    // Favorites & Recents states to trigger recompositions
    var favoriteKeys by remember { mutableStateOf(SaveManager.getFavorites(context)) }
    val recentKeys by remember { mutableStateOf(SaveManager.getRecents(context)) }

    // Sound Manager for chimes & feedback
    val soundManager = remember { CallSoundManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (selectedPage == null) "معرض التلوين الفني 🎨" else selectedPage!!.titleAr,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedPage != null) {
                                selectedPage = null
                            } else {
                                onBack()
                            }
                        },
                        modifier = Modifier.testTag("coloring_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1E1B4B)
                )
            )
        },
        containerColor = Color(0xFF0F172A) // Sleek space base theme
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (selectedPage == null) {
                // =============== LANDING PAGE FILTER GRID ===============
                Column(modifier = Modifier.fillMaxSize()) {
                    
                    // Filter tabs center: "الكل", "المفضلة", "الرسمات الأخيرة"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val filterTabs = listOf(
                            Pair("all", "كل الرسومات 🎨"),
                            Pair("favorites", "المفضلة ⭐"),
                            Pair("recents", "الرسمات الأخيرة 🕒")
                        )
                        filterTabs.forEach { (tag, label) ->
                            val isActive = currentFilterTab == tag
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isActive) Color(0xFFEF4444) else Color.Transparent)
                                    .clickable {
                                        currentFilterTab = tag
                                        if (tag != "all") selectedCategory = null
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isActive) Color.White else Color.White.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Categories slider (only shown if filtering "all" pages)
                    AnimatedVisibility(
                        visible = currentFilterTab == "all",
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            item {
                                val allActive = selectedCategory == null
                                FilterChip(
                                    selected = allActive,
                                    onClick = { selectedCategory = null },
                                    label = { Text("الكل 🔥", fontWeight = FontWeight.Black) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = Color.White.copy(alpha = 0.06f),
                                        selectedContainerColor = Color(0xFFA855F7),
                                        labelColor = Color.White.copy(alpha = 0.8f),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                            items(ColoringAssets.categories) { cat ->
                                val active = selectedCategory == cat.id
                                FilterChip(
                                    selected = active,
                                    onClick = { selectedCategory = cat.id },
                                    label = { Text("${cat.icon} ${cat.nameAr}", fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = Color.White.copy(alpha = 0.06f),
                                        selectedContainerColor = Color(0xFFA855F7),
                                        labelColor = Color.White.copy(alpha = 0.8f),
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Fetch actual requested pages list
                    val filteredPages = remember(currentFilterTab, selectedCategory, favoriteKeys) {
                        ColoringAssets.coloringPages.filter { page ->
                            val matchesTab = when (currentFilterTab) {
                                "favorites" -> favoriteKeys.contains(page.id)
                                "recents" -> recentKeys.contains(page.id)
                                else -> true
                            }
                            val matchesCategory = selectedCategory == null || page.category == selectedCategory
                            matchesTab && matchesCategory
                        }
                    }

                    if (filteredPages.isEmpty()) {
                        // EMPTY STATE
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(30.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎨🌟🔍",
                                fontSize = 54.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = when (currentFilterTab) {
                                    "favorites" -> "أوه! لا توجد لوحات فنية مفضلة حالياً\nانقر على رمز النجمة ⭐ داخل أي لصفحتها!"
                                    "recents" -> "لم تقم بتلوين أي لوحة فنية بعد\nابدأ رحلتك الإبداعية الآن بنقرة!"
                                    else -> "لا توجد لوحات فنية متوفرة بهذه الفئة!"
                                },
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                        }
                    } else {
                        // MAIN GRID LAYOUT
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(filteredPages) { page ->
                                var itemFav by remember(favoriteKeys) { mutableStateOf(favoriteKeys.contains(page.id)) }
                                var scale by remember { mutableStateOf(1.0f) }

                                val scaleAnim by animateFloatAsState(
                                    targetValue = scale,
                                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                                    label = "card_click"
                                )

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .graphicsLayer(scaleX = scaleAnim, scaleY = scaleAnim)
                                        .clickable {
                                            soundManager.playSynthSound("laser")
                                            selectedPage = page
                                        },
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF1E1B4B).copy(alpha = 0.6f)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        Color(0xFF818CF8).copy(alpha = 0.3f)
                                    )
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        
                                        // Quick favorite star tag corner
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(6.dp)
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color.Black.copy(alpha = 0.3f))
                                                .clickable {
                                                    SaveManager.toggleFavorite(context, page.id)
                                                    favoriteKeys = SaveManager.getFavorites(context)
                                                    soundManager.playSynthSound("bell")
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = if (itemFav) "⭐" else "☆",
                                                fontSize = 16.sp,
                                                color = if (itemFav) Color(0xFFFFD700) else Color.White
                                            )
                                        }

                                        Column(
                                            modifier = Modifier
                                                .padding(12.dp)
                                                .fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // Big preview icon
                                            Box(
                                                modifier = Modifier
                                                    .size(68.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White.copy(alpha = 0.08f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = page.iconEmoji, fontSize = 38.sp)
                                            }

                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = page.titleAr,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "فئة: " + page.categoryAr,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF818CF8),
                                                    textAlign = TextAlign.Center
                                                )
                                            }

                                            // Draw rating stars for kid motivation
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                repeat(page.difficultyStars) {
                                                    Text("⭐", fontSize = 10.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // =============== ACTIVE COLORING CANVAS ENGINE ===============
                val page = selectedPage!!
                ActiveColoringCanvas(
                    page = page,
                    viewModel = viewModel,
                    soundManager = soundManager,
                    onExit = {
                        selectedPage = null
                        // Refresh landing states
                        favoriteKeys = SaveManager.getFavorites(context)
                    }
                )
            }
        }
    }
}

@Composable
fun ActiveColoringCanvas(
    page: ColoringPage,
    viewModel: AppViewModel,
    soundManager: CallSoundManager,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 1. Drawing States
    var currentTool by remember { mutableStateOf(ColoringToolType.FILL) }
    var currentColor by remember { mutableStateOf(ColoringTools.palette[0].color) }
    var brushWidth by remember { mutableFloatStateOf(15f) }

    // Favorites
    var isFav by remember { mutableStateOf(SaveManager.isFavorite(context, page.id)) }

    // Loaded colors & strokes from saves
    val loadedState = remember(page.id) { SaveManager.loadPageColoring(context, page.id) }
    
    // Tap colors mapping
    var shapeColors by remember {
        mutableStateOf(
            page.shapes.associate { shape ->
                val loadedVal = loadedState.first[shape.id]
                shape.id to (if (loadedVal != null) Color(loadedVal) else Color.White)
            }.toMutableMap()
        )
    }

    // Brush freehand strokes list
    var strokesList by remember {
        mutableStateOf(
            ColoringTools.deserializeStrokes(loadedState.second).toMutableList()
        )
    }

    // Freehand drawing buffers
    var currentStrokePoints = remember { mutableStateListOf<DrawingPoint>() }

    // Undo / Redo histories stacks
    val undoHistoryColor = remember { mutableStateListOf<Map<String, Color>>() }
    val redoHistoryColor = remember { mutableStateListOf<Map<String, Color>>() }
    val undoHistoryStrokes = remember { mutableStateListOf<List<DrawingStroke>>() }
    val redoHistoryStrokes = remember { mutableStateListOf<List<DrawingStroke>>() }

    // Star popup celebrate dialog
    var showCelebrateDialog by remember { mutableStateOf(false) }

    // Background Lullaby procedural chimes toggler
    var isMusicPlaying by remember { mutableStateOf(false) }

    // Periodic procedural chime music generator
    LaunchedEffect(isMusicPlaying) {
        if (isMusicPlaying) {
            val beautifulNotes = listOf("lion", "dog", "bird", "cat", "bell", "funny")
            while (isMusicPlaying) {
                val randNote = beautifulNotes[Random.nextInt(beautifulNotes.size)]
                soundManager.playSynthSound(randNote)
                delay(3000)
            }
        }
    }

    // Trigger save states inside files
    fun autoSaveProgress() {
        val colorsIntMap = shapeColors.mapValues { it.value.toArgb() }
        val serializedStrokes = ColoringTools.serializeStrokes(strokesList)
        SaveManager.savePageColoring(context, page.id, colorsIntMap, serializedStrokes)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // TOP CONTROL STATUS LABELS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Replay helper sound
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = {
                        soundManager.speakDirect("يا بطل! لوّن المربعات الجميلة أو ارسم بحرية لحماية الغابة وتزيين رسوم وحش السكر والسيارات الطائرة!")
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "أرشدني", tint = Color.White)
                }

                // Sound music loop button
                IconButton(
                    onClick = {
                        isMusicPlaying = !isMusicPlaying
                        soundManager.playSynthSound("bell")
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isMusicPlaying) Color(0xFFEF4444) else Color.White.copy(alpha = 0.08f))
                ) {
                    Icon(
                        imageVector = if (isMusicPlaying) Icons.Filled.Notifications else Icons.Filled.PlayArrow,
                        contentDescription = "موسيقى مهدئة",
                        tint = Color.White
                    )
                }
            }

            // Page Title Center
            Text(
                text = page.titleAr,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )

            // Favoriting quick tag
            IconButton(
                onClick = {
                    isFav = SaveManager.toggleFavorite(context, page.id)
                    soundManager.playSynthSound("bell")
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Icon(
                    imageVector = if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "تفضيل",
                    tint = if (isFav) Color(0xFFEF4444) else Color.White
                )
            }
        }

        // ================= THE WHITEBOARD RESPONSIVE CANVAS SCREEN =================
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFFFFFDF6)) // Soft cream whiteboard background
                .border(4.dp, Color(0xFF1E1B4B), RoundedCornerShape(32.dp))
                .shadow(6.dp, RoundedCornerShape(32.dp))
                .pointerInput(currentTool, currentColor, brushWidth) {
                    detectTapGestures { offset ->
                        if (currentTool == ColoringToolType.FILL) {
                            val px = offset.x / size.width
                            val py = offset.y / size.height

                            // 1. Traverse list in REVERSE order to pick topmost shape clicked!
                            val clickedShape = page.shapes
                                .asReversed()
                                .firstOrNull { shape ->
                                    ColoringTools.hitTest(px, py, shape)
                                }

                            if (clickedShape != null) {
                                // Save Undo history
                                undoHistoryColor.add(shapeColors.toMap())
                                redoHistoryColor.clear()

                                val updatedColors = shapeColors.toMutableMap()
                                updatedColors[clickedShape.id] = currentColor
                                shapeColors = updatedColors

                                soundManager.playSynthSound("laser")
                                soundManager.speakDirect(clickedShape.labelAr)
                                autoSaveProgress()
                            }
                        }
                    }
                }
                .pointerInput(currentTool, currentColor, brushWidth) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            if (currentTool == ColoringToolType.BRUSH || currentTool == ColoringToolType.ERASER) {
                                val rx = offset.x / size.width
                                val ry = offset.y / size.height
                                currentStrokePoints.clear()
                                currentStrokePoints.add(DrawingPoint(rx, ry))
                            }
                        },
                        onDrag = { change, _ ->
                            if (currentTool == ColoringToolType.BRUSH || currentTool == ColoringToolType.ERASER) {
                                val rx = change.position.x / size.width
                                val ry = change.position.y / size.height
                                
                                // clamp within range
                                val cx = rx.coerceIn(0f, 1f)
                                val cy = ry.coerceIn(0f, 1f)
                                currentStrokePoints.add(DrawingPoint(cx, cy))
                            }
                        },
                        onDragEnd = {
                            if (currentStrokePoints.isNotEmpty()) {
                                // Save Undo state
                                undoHistoryStrokes.add(strokesList.toList())
                                redoHistoryStrokes.clear()

                                val newStroke = DrawingStroke(
                                    points = currentStrokePoints.toList(),
                                    colorArgb = currentColor.toArgb(),
                                    strokeWidth = brushWidth,
                                    isEraser = currentTool == ColoringToolType.ERASER
                                )
                                strokesList.add(newStroke)
                                currentStrokePoints.clear()
                                autoSaveProgress()
                            }
                        }
                    )
                }
                .testTag("coloring_canvas_touch_zone")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw filled vector shapes
                page.shapes.forEach { shape ->
                    val color = shapeColors[shape.id] ?: Color.White
                    val path = Path()

                    when (shape.shapeType) {
                        ColoringShapeType.CIRCLE -> {
                            val cx = shape.cx * canvasWidth
                            val cy = shape.cy * canvasHeight
                            val r = shape.rx * canvasWidth
                            if (!shape.isOutlineOnly) {
                                drawCircle(color = color, radius = r, center = Offset(cx, cy))
                            }
                        }
                        ColoringShapeType.OVAL -> {
                            val cx = shape.cx * canvasWidth
                            val cy = shape.cy * canvasHeight
                            val rx = shape.rx * canvasWidth
                            val ry = shape.ry * canvasHeight
                            if (!shape.isOutlineOnly) {
                                drawOval(color = color, topLeft = Offset(cx - rx, cy - ry), size = Size(rx * 2, ry * 2))
                            }
                        }
                        ColoringShapeType.RECTANGLE -> {
                            val l = shape.x1 * canvasWidth
                            val t = shape.y1 * canvasHeight
                            val r = shape.x2 * canvasWidth
                            val b = shape.y2 * canvasHeight
                            val w = r - l
                            val h = b - t
                            if (!shape.isOutlineOnly) {
                                drawRoundRect(
                                    color = color,
                                    topLeft = Offset(l, t),
                                    size = Size(w, h),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
                                )
                            }
                        }
                        ColoringShapeType.STAR, ColoringShapeType.POLYGON, ColoringShapeType.TRIANGLE -> {
                            if (shape.points.isNotEmpty()) {
                                path.moveTo(shape.points[0].x * canvasWidth, shape.points[0].y * canvasHeight)
                                for (i in 1 until shape.points.size) {
                                    val pt = shape.points[i]
                                    path.lineTo(pt.x * canvasWidth, pt.y * canvasHeight)
                                }
                                path.close()
                                if (!shape.isOutlineOnly) {
                                    drawPath(path = path, color = color)
                                }
                            }
                        }
                    }
                }

                // Draw outlines on top in clear black charcoal line vector ink
                page.shapes.forEach { shape ->
                    val path = Path()
                    val lineStrokeWidth = shape.strokeWidth
                    val outlineInkColor = Color(0xFF1E1B4B) // Premium Navy charcoal ink

                    when (shape.shapeType) {
                        ColoringShapeType.CIRCLE -> {
                            val cx = shape.cx * canvasWidth
                            val cy = shape.cy * canvasHeight
                            val r = shape.rx * canvasWidth
                            drawCircle(
                                color = outlineInkColor,
                                radius = r,
                                center = Offset(cx, cy),
                                style = Stroke(width = lineStrokeWidth)
                            )
                        }
                        ColoringShapeType.OVAL -> {
                            val cx = shape.cx * canvasWidth
                            val cy = shape.cy * canvasHeight
                            val rx = shape.rx * canvasWidth
                            val ry = shape.ry * canvasHeight
                            drawOval(
                                color = outlineInkColor,
                                topLeft = Offset(cx - rx, cy - ry),
                                size = Size(rx * 2, ry * 2),
                                style = Stroke(width = lineStrokeWidth)
                            )
                        }
                        ColoringShapeType.RECTANGLE -> {
                            val l = shape.x1 * canvasWidth
                            val t = shape.y1 * canvasHeight
                            val r = shape.x2 * canvasWidth
                            val b = shape.y2 * canvasHeight
                            val w = r - l
                            val h = b - t
                            drawRoundRect(
                                color = outlineInkColor,
                                topLeft = Offset(l, t),
                                size = Size(w, h),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f),
                                style = Stroke(width = lineStrokeWidth)
                            )
                        }
                        ColoringShapeType.STAR, ColoringShapeType.POLYGON, ColoringShapeType.TRIANGLE -> {
                            if (shape.points.isNotEmpty()) {
                                path.moveTo(shape.points[0].x * canvasWidth, shape.points[0].y * canvasHeight)
                                for (i in 1 until shape.points.size) {
                                    val pt = shape.points[i]
                                    path.lineTo(pt.x * canvasWidth, pt.y * canvasHeight)
                                }
                                path.close()
                                drawPath(
                                    path = path,
                                    color = outlineInkColor,
                                    style = Stroke(width = lineStrokeWidth)
                                )
                            }
                        }
                    }
                }

                // Draw finished strokes (brush drawings layer)
                strokesList.forEach { stroke ->
                    if (stroke.points.size < 2) return@forEach
                    
                    val path = Path()
                    path.moveTo(stroke.points[0].x * canvasWidth, stroke.points[0].y * canvasHeight)
                    for (i in 1 until stroke.points.size) {
                        path.lineTo(stroke.points[i].x * canvasWidth, stroke.points[i].y * canvasHeight)
                    }

                    val normalizedW = stroke.strokeWidth / 100f * canvasWidth
                    drawPath(
                        path = path,
                        color = if (stroke.isEraser) Color(0xFFFFFDF6) else Color(stroke.colorArgb),
                        style = Stroke(
                            width = normalizedW,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // Draw active currently dragging freehand outline stroke segment
                if (currentStrokePoints.size >= 2) {
                    val path = Path()
                    path.moveTo(currentStrokePoints[0].x * canvasWidth, currentStrokePoints[0].y * canvasHeight)
                    for (i in 1 until currentStrokePoints.size) {
                        path.lineTo(currentStrokePoints[i].x * canvasWidth, currentStrokePoints[i].y * canvasHeight)
                    }
                    val normalizedW = brushWidth / 100f * canvasWidth
                    drawPath(
                        path = path,
                        color = if (currentTool == ColoringToolType.ERASER) Color(0xFFFFFDF6) else currentColor,
                        style = Stroke(
                            width = normalizedW,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TOOLBAR BRUSH WIDTH AND UNDO REDO ACTIONS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Undo Redo buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Undo
                IconButton(
                    enabled = undoHistoryColor.isNotEmpty() || undoHistoryStrokes.isNotEmpty(),
                    onClick = {
                        soundManager.playSynthSound("funny")
                        if (currentTool == ColoringToolType.FILL && undoHistoryColor.isNotEmpty()) {
                            val topColor = undoHistoryColor.removeAt(undoHistoryColor.size - 1)
                            redoHistoryColor.add(shapeColors.toMap())
                            shapeColors = topColor.toMutableMap()
                        } else if (undoHistoryStrokes.isNotEmpty()) {
                            val topStrokes = undoHistoryStrokes.removeAt(undoHistoryStrokes.size - 1)
                            redoHistoryStrokes.add(strokesList.toList())
                            strokesList = topStrokes.toMutableList()
                        }
                        autoSaveProgress()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "تراجع", tint = Color.White)
                }

                // Redo
                IconButton(
                    enabled = redoHistoryColor.isNotEmpty() || redoHistoryStrokes.isNotEmpty(),
                    onClick = {
                        soundManager.playSynthSound("funny")
                        if (currentTool == ColoringToolType.FILL && redoHistoryColor.isNotEmpty()) {
                            val topRedo = redoHistoryColor.removeAt(redoHistoryColor.size - 1)
                            undoHistoryColor.add(shapeColors.toMap())
                            shapeColors = topRedo.toMutableMap()
                        } else if (redoHistoryStrokes.isNotEmpty()) {
                            val topRedo = redoHistoryStrokes.removeAt(redoHistoryStrokes.size - 1)
                            undoHistoryStrokes.add(strokesList.toList())
                            strokesList = topRedo.toMutableList()
                        }
                        autoSaveProgress()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "إعادة", tint = Color.White)
                }
            }

            // Brush Sliders size preview
            AnimatedVisibility(visible = currentTool == ColoringToolType.BRUSH || currentTool == ColoringToolType.ERASER) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "حجم الفرشاة:", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    Slider(
                        value = brushWidth,
                        onValueChange = { brushWidth = it },
                        valueRange = 5f..40f,
                        modifier = Modifier
                            .width(130.dp)
                            .testTag("coloring_size_slider")
                    )
                }
            }

            // Full clear page trigger
            IconButton(
                onClick = {
                    soundManager.playSynthSound("lightning")
                    // Clear all
                    shapeColors = page.shapes.associate { it.id to Color.White }.toMutableMap()
                    strokesList.clear()
                    undoHistoryColor.clear()
                    redoHistoryColor.clear()
                    undoHistoryStrokes.clear()
                    redoHistoryStrokes.clear()
                    autoSaveProgress()
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444).copy(alpha = 0.15f))
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "مسح الصفحة", tint = Color(0xFFEF4444))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // COLORING PALETTE SCROLLER
        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(ColoringTools.palette) { p ->
                val isActive = currentColor == p.color && currentTool != ColoringToolType.ERASER
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(p.color)
                        .border(
                            width = if (isActive) 4.dp else 1.5.dp,
                            color = if (isActive) Color.White else Color.White.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .clickable {
                            currentColor = p.color
                            if (currentTool == ColoringToolType.ERASER) {
                                currentTool = ColoringToolType.BRUSH
                            }
                            soundManager.playSynthSound("bird")
                            soundManager.speakDirect(p.nameAr)
                        }
                        .testTag("palette_color_${p.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = p.emoji, fontSize = 20.sp)
                }
            }
        }

        // CONTROL TOOLS AND EXPORT SAVER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Fill / Draw toggler
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(4.dp)
            ) {
                // Taping fill bucket icon
                IconButton(
                    onClick = {
                        currentTool = ColoringToolType.FILL
                        soundManager.playSynthSound("portal")
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (currentTool == ColoringToolType.FILL) Color(0xFF818CF8) else Color.Transparent)
                        .testTag("tool_fill")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "سكب الألوان", tint = Color.White)
                }

                // Drawing brush icon
                IconButton(
                    onClick = {
                        currentTool = ColoringToolType.BRUSH
                        soundManager.playSynthSound("portal")
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (currentTool == ColoringToolType.BRUSH) Color(0xFF818CF8) else Color.Transparent)
                        .testTag("tool_brush")
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "قلم رسم وحل", tint = Color.White)
                }

                // Eraser tool
                IconButton(
                    onClick = {
                        currentTool = ColoringToolType.ERASER
                        soundManager.playSynthSound("funny")
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (currentTool == ColoringToolType.ERASER) Color(0xFF818CF8) else Color.Transparent)
                        .testTag("tool_eraser")
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "ممحاة إلكترونية", tint = Color.White)
                }
            }

            // High Fidelity Save button triggering stars & gallery writing
            Button(
                onClick = {
                    soundManager.playSynthSound("bell")
                    soundManager.speakDirect("يا لك من فنان رائع! فزت بثلاث نجوم ذهبية!")
                    
                    // Award profiles stars!
                    viewModel.awardQuizStars(page.difficultyStars)
                    showCelebrateDialog = true

                    // Export to public gallery
                    scope.launch {
                        val strokeExportList = strokesList.map { stroke ->
                            DrawingStroke(
                                points = stroke.points,
                                colorArgb = stroke.colorArgb,
                                strokeWidth = stroke.strokeWidth,
                                isEraser = stroke.isEraser
                            )
                        }

                        val worked = SaveManager.saveDrawingToGallery(
                            context = context,
                            page = page,
                            shapeColors = shapeColors.mapValues { it.value.toArgb() },
                            strokes = strokeExportList
                        )
                        if (worked) {
                            Toast.makeText(context, "تم حفظ الرسمة في معرض الحفظ بالهاتف بنجاح! 🎨📸", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .testTag("coloring_save_reward_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("انتهيت! احفظ واكسب ⭐", fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }
    }

    // CELEBRATION STARS POPUP
    if (showCelebrateDialog) {
        AlertDialog(
            onDismissRequest = {
                showCelebrateDialog = false
                onExit()
            },
            title = {
                Text(
                    text = "أحسنت يا فنان! 🥳🌟",
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🎨🏆🚀",
                        fontSize = 54.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "لوحتك المذهلة 「${page.titleAr}」 تم حفظها في المعرض الخاص بالشرطة بنجاح!\n\nلقد حصلت على +${page.difficultyStars} نجوم ذهبية لملفك الشخصي!",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCelebrateDialog = false
                        onExit()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("يا له من أمر رائع! 😍✨", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
