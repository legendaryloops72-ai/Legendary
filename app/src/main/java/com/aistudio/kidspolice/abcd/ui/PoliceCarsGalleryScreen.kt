package com.aistudio.kidspolice.abcd.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.aistudio.kidspolice.abcd.data.PoliceVehicle
import com.aistudio.kidspolice.abcd.data.policeVehicles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliceCarsGalleryScreen(
    onNavigateBack: () -> Unit,
    onVehicleSelected: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("معرض سيارات الشرطة") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A8A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(policeVehicles) { vehicle ->
                PoliceVehicleCard(vehicle = vehicle, onClick = { onVehicleSelected(vehicle.id) })
            }
        }
    }
}

@Composable
fun PoliceVehicleCard(vehicle: PoliceVehicle, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1f, label = "scale")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                val context = LocalContext.current
                val imageResId = remember(vehicle.imageAssetPath) {
                    try {
                        context.resources.getIdentifier(vehicle.imageAssetPath, "drawable", context.packageName)
                    } catch (e: Exception) {
                        0
                    }
                }
                
                var isImageError by remember { mutableStateOf(false) }
                
                if (imageResId != 0 && !isImageError) {
                    AsyncImage(
                        model = imageResId,
                        contentDescription = vehicle.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        onError = { isImageError = true }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.LocalPolice,
                        contentDescription = vehicle.name,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF1E3A8A)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = vehicle.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1E3A8A),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliceCarDetailScreen(
    vehicleId: Int,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val soundManager = remember { com.aistudio.kidspolice.abcd.sound.CallSoundManager(context) }
    
    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
        }
    }

    val vehicle = remember(vehicleId) {
        policeVehicles.find { it.id == vehicleId }
    }

    // Speak description and play siren automatically upon entering
    LaunchedEffect(vehicleId) {
        vehicle?.let {
            soundManager.playSynthSound("siren")
            kotlinx.coroutines.delay(800)
            soundManager.speakDirect("هذه هي ${it.name}. ${it.description}")
        }
    }

    if (vehicle == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("خطأ في البيانات") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1E3A8A),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            containerColor = Color(0xFFF3F4F6)
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "عذرًا، لم يتم العثور على تفاصيل هذه المركبة.",
                    fontSize = 18.sp,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(vehicle.name) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A8A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Hero Image Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.2f)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val imageResId = remember(vehicle.imageAssetPath) {
                        try {
                            context.resources.getIdentifier(vehicle.imageAssetPath, "drawable", context.packageName)
                        } catch (e: Exception) {
                            0
                        }
                    }
                    
                    var isImageError by remember { mutableStateOf(false) }
                    
                    if (imageResId != 0 && !isImageError) {
                        AsyncImage(
                            model = imageResId,
                            contentDescription = vehicle.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                            onError = { isImageError = true }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.LocalPolice,
                            contentDescription = vehicle.name,
                            modifier = Modifier.size(120.dp),
                            tint = Color(0xFF1E3A8A)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = vehicle.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color(0xFF1E3A8A),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = vehicle.description,
                        fontSize = 18.sp,
                        color = Color(0xFFDC2626),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }

            // Interactive Audio Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        soundManager.playSynthSound("siren")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("🚨 تشغيل الصفارة", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = {
                        soundManager.speakDirect("هذه هي ${vehicle.name}. ${vehicle.description}")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("🗣️ الاستماع للشرح", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
