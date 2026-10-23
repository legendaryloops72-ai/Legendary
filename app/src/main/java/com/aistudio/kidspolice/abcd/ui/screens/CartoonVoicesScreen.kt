package com.aistudio.kidspolice.abcd.ui.screens

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.aistudio.kidspolice.abcd.R
import com.aistudio.kidspolice.abcd.cartoonvoices.CartoonVoiceCategory
import com.aistudio.kidspolice.abcd.cartoonvoices.CartoonVoice
import com.aistudio.kidspolice.abcd.cartoonvoices.CartoonVoicesRepository
import kotlinx.coroutines.launch

@Composable
fun CartoonVoicesScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Media Player state (strictly single active sound to avoid overlap)
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentlyPlayingId by remember { mutableStateOf<String?>(null) }

    // User Controls
    var isMuted by remember { mutableStateOf(false) }
    var soundVolume by remember { mutableFloatStateOf(0.9f) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    // Selected Category Filter
    var selectedCategory by remember { mutableStateOf(CartoonVoiceCategory.ALL) }

    // Safely stop and release player
    fun stopSound() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Throwable) {}
        mediaPlayer = null
        currentlyPlayingId = null
    }

    // Play a funny sound safely
    fun playSound(voice: CartoonVoice) {
        stopSound()
        if (isMuted) return

        try {
            val mp = MediaPlayer.create(context, voice.soundRes)
            if (mp != null) {
                mediaPlayer = mp
                currentlyPlayingId = voice.id
                mp.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build()
                )
                mp.setVolume(soundVolume, soundVolume)
                mp.setOnCompletionListener {
                    it.release()
                    if (mediaPlayer == it) {
                        mediaPlayer = null
                        currentlyPlayingId = null
                    }
                }
                mp.start()
            }
        } catch (_: Throwable) {
            stopSound()
        }
    }

    // Lifecycle observer to stop audio when entering background or leaving screen
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                stopSound()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            stopSound()
        }
    }

    BackHandler {
        stopSound()
        onBack()
    }

    // Filter voices based on category
    val displayedVoices = remember(selectedCategory) {
        if (selectedCategory == CartoonVoiceCategory.ALL) {
            CartoonVoicesRepository.voices
        } else {
            CartoonVoicesRepository.voices.filter { it.category == selectedCategory }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF8E1), // Warm sunny pastel
                            Color(0xFFFFF3E0),
                            Color(0xFFE8F5E9)
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
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            stopSound()
                            onBack()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع",
                            tint = Color(0xFF1E293B)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "أصوات الشخصيات الكرتونية",
                            color = Color(0xFF1E293B),
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "مقالب ونغمات كرتونية مرحة للأطفال",
                            color = Color(0xFF546E7A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { showInfoDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "معلومات التراخيص",
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        IconButton(
                            onClick = { showSettingsDialog = true },
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "الإعدادات ومستوى الصوت",
                                tint = Color(0xFF1E293B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Decorative Hero Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(R.drawable.funny_photo_04),
                            contentDescription = "أصوات الشخصيات الكرتونية للأطفال",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "اضغط على أي صوت للاستماع والمرح!",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Category Chips Selector
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(CartoonVoiceCategory.entries.toTypedArray()) { cat ->
                        val isSelected = cat == selectedCategory
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .clickable { selectedCategory = cat },
                            color = if (isSelected) cat.color else Color.White,
                            shape = RoundedCornerShape(50.dp),
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCFD8DC)),
                            shadowElevation = if (isSelected) 3.dp else 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(painter = painterResource(cat.iconRes), contentDescription = cat.title, modifier = Modifier.size(24.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = cat.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF37474F)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Sound Cards Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(displayedVoices, key = { it.id }) { voice ->
                        val isPlaying = currentlyPlayingId == voice.id
                        CartoonVoiceItemCard(
                            voice = voice,
                            isPlaying = isPlaying,
                            onClick = {
                                playSound(voice)
                            }
                        )
                    }
                }
            }
        }
    }

    // Settings Modal Dialog
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(
                    text = "إعدادات الصوت",
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
                    // Mute Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_sound_wave),
                                contentDescription = null,
                                tint = Color(0xFFE91E63),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("كتم الأصوات تماماً", fontSize = 14.sp)
                        }
                        Switch(
                            checked = isMuted,
                            onCheckedChange = {
                                isMuted = it
                                if (it) stopSound()
                            }
                        )
                    }

                    // Volume Slider
                    if (!isMuted) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "مستوى صوت المؤثرات: ${(soundVolume * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Slider(
                            value = soundVolume,
                            onValueChange = {
                                soundVolume = it
                                mediaPlayer?.setVolume(it, it)
                            },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(thumbColor = Color(0xFFE91E63))
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showSettingsDialog = false }) {
                    Text("تم")
                }
            }
        )
    }

    // Info & Licences Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(
                    text = "تراخيص الأصوات والشفافية",
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
                        text = "قسم «أصوات الشخصيات الكرتونية» يضم تسجيلات فعلية ومؤثرات مرخّصة محفوظة محلياً، ويعمل دون إنترنت. تفاصيل كل مصدر وترخيص موجودة في ملف ASSET_LICENSES.md.",
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "التراخيص والأصول المستخدمة:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "لا يستخدم هذا القسم تسجيلات شخصيات كرتونية محمية أو أصوات ممثلين من أعمال تجارية؛ استُخدمت أوصاف عامة لألعاب وحيوانات ومؤثرات أصلية المصدر.",
                        fontSize = 11.sp,
                        color = Color(0xFF455A64)
                    )
                    Spacer(Modifier.height(8.dp))
                    CartoonVoicesRepository.voices.forEach { s ->
                        Text(
                            text = "• ${s.title}: ${s.licenseNotice}",
                            fontSize = 11.sp,
                            color = Color(0xFF607D8B)
                        )
                        Spacer(Modifier.height(2.dp))
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
private fun CartoonVoiceItemCard(
    voice: CartoonVoice,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val scaleAnim = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .scale(scaleAnim.value)
            .clickable {
                scope.launch {
                    scaleAnim.animateTo(0.92f, spring(stiffness = 900f))
                    scaleAnim.animateTo(1f, spring(stiffness = 700f))
                }
                onClick()
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) voice.accentColor.copy(alpha = 0.22f) else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isPlaying) 2.dp else 1.dp,
            color = if (isPlaying) voice.accentColor else Color(0xFFECEFF1)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlaying) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Real photographic asset badge
                Image(
                    painter = painterResource(voice.imageRes),
                    contentDescription = voice.title,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                // Play / Ripple Indicator
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            if (isPlaying) voice.accentColor else Color(0xFFECEFF1),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "تشغيل",
                        tint = if (isPlaying) Color.White else Color(0xFF90A4AE),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = voice.title,
                    color = Color(0xFF1E293B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = voice.description,
                    color = Color(0xFF78909C),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 13.sp
                )
            }

            // Animated playing pulse line
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(voice.accentColor)
                )
            } else {
                Spacer(Modifier.height(3.dp))
            }
        }
    }
}
