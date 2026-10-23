package com.aistudio.kidspolice.abcd.ui.screens

import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.aistudio.kidspolice.abcd.R
import com.aistudio.kidspolice.abcd.toys.EffectType
import com.aistudio.kidspolice.abcd.toys.SafeTorchController
import com.aistudio.kidspolice.abcd.toys.ToyWeapon
import com.aistudio.kidspolice.abcd.toys.ToyWeaponsRepository
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun ToySimulatorScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Safety controller for LED Flash
    val torchController = remember { SafeTorchController(context) }

    // Audio Player for short toy effects
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Vibrator for tactile haptic feedback
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    // Safety & User preference settings
    var isMuted by remember { mutableStateOf(false) }
    var soundVolume by remember { mutableFloatStateOf(0.85f) }
    var isScreenFlashEnabled by remember { mutableStateOf(true) }
    var isTorchEnabled by remember { mutableStateOf(false) } // Disabled by default for safety
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showCameraRationaleDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    // Selected toy weapon
    var selectedItem by remember { mutableStateOf(ToyWeaponsRepository.items.first()) }

    // Visual animation states
    val triggerScale = remember { Animatable(1f) }
    var isTriggering by remember { mutableStateOf(false) }
    val screenFlashAlpha = remember { Animatable(0f) }
    var effectCounter by remember { mutableStateOf(0) }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isTorchEnabled = true
        } else {
            isTorchEnabled = false
        }
    }

    // Safely turn off torch and release sounds when navigating back or pausing
    fun cleanupResources() {
        torchController.turnOffTorch()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (_: Throwable) {}
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                cleanupResources()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            cleanupResources()
        }
    }

    BackHandler {
        cleanupResources()
        onBack()
    }

    // Firing trigger action
    fun triggerToyAction() {
        if (isTriggering) return
        isTriggering = true
        effectCounter++

        scope.launch {
            // Button bounce animation
            launch {
                triggerScale.animateTo(0.88f, spring(stiffness = 800f))
                triggerScale.animateTo(1f, spring(stiffness = 600f))
            }

            // Screen flash animation (safe, gentle opacity)
            if (isScreenFlashEnabled) {
                launch {
                    screenFlashAlpha.snapTo(0.35f)
                    screenFlashAlpha.animateTo(0f, tween(durationMillis = 280, easing = LinearEasing))
                }
            }

            // Haptic feedback
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(45)
                }
            } catch (_: Throwable) {}

            // LED Camera Flash (optional, brief, and with cooldown)
            if (isTorchEnabled && torchController.isFlashSupported()) {
                torchController.triggerBriefFlash(scope, durationMs = 90L)
            }

            // Sound playback
            if (!isMuted) {
                try {
                    mediaPlayer?.release()
                    mediaPlayer = MediaPlayer.create(context, selectedItem.soundRes)?.apply {
                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .setUsage(AudioAttributes.USAGE_GAME)
                                .build()
                        )
                        setVolume(soundVolume, soundVolume)
                        setOnCompletionListener { mp ->
                            mp.release()
                            if (mediaPlayer == mp) mediaPlayer = null
                        }
                        start()
                    }
                } catch (_: Throwable) {}
            }

            kotlinx.coroutines.delay(260)
            isTriggering = false
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D1B2A),
                            Color(0xFF1B263B),
                            Color(0xFF0F172A)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            cleanupResources()
                            onBack()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.12f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع للرئيسية",
                            tint = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "محاكي ألعاب الأبطال",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "أدوات لعب خيالية مرحة وآمنة للأطفال",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { showInfoDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.12f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "معلومات التراخيص والسلامة",
                                tint = Color(0xFF64B5F6),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = { showSettingsDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.12f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "إعدادات المؤثرات",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Interactive Stage Card (Selected Toy View)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131E33)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    listOf(
                                        selectedItem.accentColor.copy(alpha = 0.7f),
                                        Color.Transparent,
                                        selectedItem.accentColor.copy(alpha = 0.3f)
                                    )
                                ),
                                shape = RoundedCornerShape(26.dp)
                            )
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Background particles/visual effect canvas
                        ToyVisualEffectCanvas(
                            effectType = selectedItem.effectType,
                            accentColor = selectedItem.accentColor,
                            triggerCount = effectCounter,
                            isTriggering = isTriggering
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Header badge with item name
                            Surface(
                                color = selectedItem.accentColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(50.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    selectedItem.accentColor.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(selectedItem.accentColor, CircleShape)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = selectedItem.name,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Center toy image with interactive trigger scale
                            Box(
                                modifier = Modifier
                                    .size(230.dp)
                                    .scale(triggerScale.value),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(selectedItem.imageRes),
                                    contentDescription = selectedItem.name,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            // Trigger Action Button
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = selectedItem.description,
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(10.dp))
                                Button(
                                    onClick = { triggerToyAction() },
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .height(54.dp)
                                        .scale(triggerScale.value),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = selectedItem.accentColor
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "تشغيل التأثير",
                                            tint = Color(0xFF0F172A),
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = "إطلاق التأثير الخيالي",
                                            color = Color(0xFF0F172A),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Bottom Shelf: Selection Grid of 4 Toys
                Text(
                    text = "اختر أداة اللعب المفضلة لديك:",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.Center,
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(ToyWeaponsRepository.items) { item ->
                        val isSelected = item.id == selectedItem.id
                        ToyGridItemCard(
                            toy = item,
                            isSelected = isSelected,
                            onClick = {
                                selectedItem = item
                            }
                        )
                    }
                }
            }

            // Screen flash overlay (smooth Compose animation)
            if (screenFlashAlpha.value > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(selectedItem.accentColor.copy(alpha = screenFlashAlpha.value))
                )
            }
        }
    }

    // Settings Modal Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(
                    text = "إعدادات المؤثرات والسلامة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Sound Mute Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_sound_wave),
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("كتم الأصوات", fontSize = 14.sp)
                        }
                        Switch(
                            checked = isMuted,
                            onCheckedChange = { isMuted = it }
                        )
                    }

                    // Sound Volume Slider
                    if (!isMuted) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "مستوى صوت اللعبة: ${(soundVolume * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Slider(
                            value = soundVolume,
                            onValueChange = { soundVolume = it },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFF1976D2))
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Screen Flash Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("وميض الشاشة البصري", fontSize = 14.sp)
                        Switch(
                            checked = isScreenFlashEnabled,
                            onCheckedChange = { isScreenFlashEnabled = it }
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Rear Camera Flash (Torch) Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "فلاش الكاميرا الخلفي",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "وميض خفيف مؤقت (اختياري ومعطل افتراضياً)",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = isTorchEnabled,
                            onCheckedChange = { enable ->
                                if (enable) {
                                    showCameraRationaleDialog = true
                                } else {
                                    isTorchEnabled = false
                                    torchController.turnOffTorch()
                                }
                            }
                        )
                    }

                    if (!torchController.isFlashSupported()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "ملاحظة: جهازك أو المحاكي الحالي لا يحتوي على فلاش كاميرا خلفي مدعوم، ستعمل المؤثرات على الشاشة بسلاسة.",
                            fontSize = 11.sp,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsDialog = false }
                ) {
                    Text("تم")
                }
            }
        )
    }

    // Camera Permission Rationale Dialog
    if (showCameraRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showCameraRationaleDialog = false },
            title = {
                Text(
                    text = "تفعيل فلاش اللعبة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "يتطلب تشغيل فلاش اللعبة الوصول إلى مصباح الكاميرا الخلفي (Torch Mode). نؤكد أن التطبيق لن يفتح الكاميرا، ولن يلتقط أي صور أو مقاطع فيديو على الإطلاق، وسيقتصر الاستخدام على نبضة ضوء قصيرة فقط.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCameraRationaleDialog = false
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("متابعة ومنح الإذن")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCameraRationaleDialog = false
                        isTorchEnabled = false
                    }
                ) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Info & Licensing Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(
                    text = "حول محاكي الألعاب والتراخيص",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "قسم «محاكي الأسلحة» هو ميزة ترفيهية خيالية مخصصة لألعاب أطفال الشرطة الرمزية، وتضم أدوات لعب كرتونية خيالية (مسدس ليزر، قاذف فقاعات، مدفع أوراق ملونة) وليست أسلحة حقيقية.",
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "سجل الأصول والتراخيص التجارية:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    ToyWeaponsRepository.items.forEach { toy ->
                        Text(
                            text = "• ${toy.name}: ${toy.assetLicenseInfo}",
                            fontSize = 11.sp,
                            color = Color(0xFF37474F)
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showInfoDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
private fun ToyGridItemCard(
    toy: ToyWeapon,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) toy.accentColor.copy(alpha = 0.22f) else Color(0xFF1E293B)
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, toy.accentColor)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(toy.imageRes),
                contentDescription = toy.name,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = toy.name,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.75f),
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ToyVisualEffectCanvas(
    effectType: EffectType,
    accentColor: Color,
    triggerCount: Int,
    isTriggering: Boolean
) {
    val progress = remember { Animatable(0f) }

    androidx.compose.runtime.LaunchedEffect(triggerCount) {
        if (triggerCount > 0) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(durationMillis = 350, easing = FastOutSlowInEasing))
        }
    }

    if (progress.value in 0.01f..0.99f) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val p = progress.value

            when (effectType) {
                EffectType.LASER_BEAM -> {
                    // Concentric expanding laser rings
                    drawCircle(
                        color = accentColor.copy(alpha = (1f - p) * 0.8f),
                        radius = p * size.minDimension * 0.45f,
                        center = center,
                        style = Stroke(width = 6.dp.toPx() * (1f - p))
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = (1f - p)),
                        radius = p * size.minDimension * 0.25f,
                        center = center,
                        style = Stroke(width = 4.dp.toPx() * (1f - p))
                    )
                }

                EffectType.BUBBLE_BURST -> {
                    // Multiple floating bubble spheres bursting outwards
                    val bubbleCount = 8
                    for (i in 0 until bubbleCount) {
                        val angle = (i * (360f / bubbleCount) + (triggerCount * 25f)) * (Math.PI / 180f)
                        val dist = p * size.minDimension * 0.35f
                        val bCenter = Offset(
                            x = center.x + (dist * Math.cos(angle)).toFloat(),
                            y = center.y + (dist * Math.sin(angle)).toFloat()
                        )
                        drawCircle(
                            color = accentColor.copy(alpha = (1f - p) * 0.7f),
                            radius = (14.dp.toPx() * (1f - p * 0.5f)),
                            center = bCenter,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }

                EffectType.SONIC_RINGS -> {
                    // Expanding elliptical sonic ripple pulses
                    val rings = 3
                    for (i in 0 until rings) {
                        val ringProgress = (p + (i * 0.2f)) % 1f
                        drawCircle(
                            color = accentColor.copy(alpha = (1f - ringProgress) * 0.6f),
                            radius = ringProgress * size.minDimension * 0.5f,
                            center = center,
                            style = Stroke(width = 5.dp.toPx() * (1f - ringProgress))
                        )
                    }
                }

                EffectType.CONFETTI_SHOWER -> {
                    // Cheerful celebratory star/burst sparks
                    val sparks = 12
                    val colors = listOf(Color.Yellow, Color.Cyan, Color(0xFFFF4081), Color.Green)
                    for (i in 0 until sparks) {
                        val angle = (i * (360f / sparks)) * (Math.PI / 180f)
                        val dist = p * size.minDimension * 0.4f
                        val sparkPos = Offset(
                            x = center.x + (dist * Math.cos(angle)).toFloat(),
                            y = center.y + (dist * Math.sin(angle)).toFloat()
                        )
                        drawCircle(
                            color = colors[i % colors.size].copy(alpha = (1f - p)),
                            radius = 6.dp.toPx() * (1f - p),
                            center = sparkPos
                        )
                    }
                }
            }
        }
    }
}
