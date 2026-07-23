package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.R
import com.example.data.VaultMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// ألوان الثيم الفخم (الكحلي والذهبي) المتناسقة بالكامل
private val VaultNavyDeep = Color(0xFF0B132B)
private val VaultNavyCard = Color(0xFF1C2541)
private val VaultGold = Color(0xFFD4AF37)
private val VaultGoldLight = Color(0xFFF5A623)
private val VaultTextLight = Color(0xFFF8FAFC)
private val VaultTextGray = Color(0xFF94A3B8)
private val VaultError = Color(0xFFEF4444)

/**
 * شاشة الدخول والتأكيد لخزنة الصور والفيديوهات الآمنة
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VaultLoginScreen(
    viewModel: AppLockViewModel,
    onBack: () -> Unit,
    onUnlockSuccess: () -> Unit
) {
    val context = LocalContext.current
    val useSamePasscode by viewModel.useSamePasscodeForVault.collectAsState()
    
    // تحديد رمز المرور المطلوب بناءً على الإعدادات
    val requiredPasscode = if (useSamePasscode) {
        viewModel.getAppPasscode()
    } else {
        viewModel.getVaultPasscode()
    }

    var isSettingUpCustomPasscode by remember { 
        mutableStateOf(!useSamePasscode && viewModel.getVaultPasscode().isEmpty()) 
    }

    var inputCode by remember { mutableStateOf("") }
    var setupStep by remember { mutableStateOf(1) } // 1: Enter new, 2: Confirm new
    var firstEnteredPasscode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    BackHandler { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(VaultNavyDeep, Color(0xFF010409))))
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // الهيدر واللوغو الدائري الفاخر
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "عودة",
                        tint = VaultGold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "الخزانة الآمنة 🔒",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = VaultGold
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(48.dp))
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(VaultGold.copy(alpha = 0.15f), CircleShape)
                    .border(2.dp, VaultGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "خزنة",
                    tint = VaultGold,
                    modifier = Modifier.size(45.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val titleText = if (isSettingUpCustomPasscode) {
                if (setupStep == 1) "إنشاء رمز مرور جديد للخزانة" else "تأكيد رمز مرور الخزانة"
            } else {
                "أدخل رمز المرور لفتح الخزانة"
            }
            
            Text(
                text = titleText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = VaultTextLight
            )

            Text(
                text = if (isSettingUpCustomPasscode) "أدخل 4 أرقام لحماية ملفاتك الخاصة" else "الملفات مخفية ومشفرة بالكامل داخل التطبيق",
                fontSize = 13.sp,
                color = VaultTextGray,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        // عرض النقط (الأرقام المدخلة)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 24.dp)
            ) {
                repeat(4) { index ->
                    val isFilled = index < inputCode.length
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(
                                color = if (isFilled) VaultGold else VaultNavyCard,
                                shape = CircleShape
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (isFilled) VaultGold else VaultTextGray.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                    )
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = VaultError,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        // لوحة المفاتيح الرقمية الفاخرة
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("مسح", "0", "حذف")
            )

            for (row in keys) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (key in row) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.5f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(VaultNavyCard)
                                .border(1.dp, VaultTextGray.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                .combinedClickable(
                                    onClick = {
                                        errorMessage = ""
                                        when (key) {
                                            "مسح" -> {
                                                inputCode = ""
                                            }
                                            "حذف" -> {
                                                if (inputCode.isNotEmpty()) {
                                                    inputCode = inputCode.dropLast(1)
                                                }
                                            }
                                            else -> {
                                                if (inputCode.length < 4) {
                                                    inputCode += key
                                                    if (inputCode.length == 4) {
                                                        // اكتمال إدخال الرمز
                                                        if (isSettingUpCustomPasscode) {
                                                            if (setupStep == 1) {
                                                                firstEnteredPasscode = inputCode
                                                                inputCode = ""
                                                                setupStep = 2
                                                            } else {
                                                                if (inputCode == firstEnteredPasscode) {
                                                                    viewModel.saveVaultPasscode(inputCode)
                                                                    Toast.makeText(context, "تم تعيين الرمز السري بنجاح ✓", Toast.LENGTH_SHORT).show()
                                                                    isSettingUpCustomPasscode = false
                                                                    onUnlockSuccess()
                                                                } else {
                                                                    errorMessage = "الرموز غير متطابقة، أعد المحاولة"
                                                                    inputCode = ""
                                                                    setupStep = 1
                                                                }
                                                            }
                                                        } else {
                                                            if (inputCode == requiredPasscode) {
                                                                onUnlockSuccess()
                                                            } else {
                                                                errorMessage = "رمز المرور خاطئ ❌"
                                                                inputCode = ""
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (key == "حذف") {
                                Icon(
                                    imageVector = Icons.Default.Backspace,
                                    contentDescription = "حذف رقم",
                                    tint = VaultGold
                                )
                            } else {
                                Text(
                                    text = key,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (key == "مسح") VaultError else VaultTextLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * شاشة شرح وطلب الصلاحيات بشكل صريح قبل طلبها من نظام أندرويد
 */
@Composable
fun VaultPermissionsOnboardingScreen(
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(VaultNavyDeep, Color(0xFF020617))))
            .statusBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(VaultGold.copy(alpha = 0.15f), CircleShape)
                    .border(2.dp, VaultGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "حماية",
                    tint = VaultGold,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "صلاحية الوصول للوسائط 📁",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = VaultTextLight,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "تحتاج الخزانة الآمنة لصلاحية الوصول للصور ومقاطع الفيديو من أجل قراءتها وتشفيرها داخل الخزانة، ثم حذف النسخة الأصلية لحماية خصوصيتك بنجاح.",
                fontSize = 15.sp,
                color = VaultTextGray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = VaultNavyCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "تنبيه",
                        tint = VaultGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "نحن نحترم خصوصيتك بالكامل. جميع ملفاتك مشفرة ومحفوظة داخلياً في جهازك ولا يتم مشاركتها أو رفعها لأي خادم خارجي نهائياً.",
                        fontSize = 12.sp,
                        color = VaultTextLight,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(containerColor = VaultGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(
                    text = "متابعة ومنح الصلاحية ➔",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = VaultNavyDeep
                )
            }

            OutlinedButton(
                onClick = onDecline,
                border = BorderStroke(1.dp, VaultTextGray.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(
                    text = "إلغاء",
                    fontSize = 15.sp,
                    color = VaultTextLight
                )
            }
        }
    }
}

/**
 * الشاشة الرئيسية للخزانة الآمنة (عرض شبكي مع تبويبات وأزرار تحكم مجمعة)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VaultHomeScreen(
    viewModel: AppLockViewModel,
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val vaultItems by viewModel.vaultItems.collectAsState()

    var activeTabIsVideo by remember { mutableStateOf(false) }
    
    // وضع التحديد المتعدد
    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<VaultMediaItem>() }

    // تصفية العناصر بناءً على التبويب النشط
    val filteredItems = remember(vaultItems, activeTabIsVideo) {
        vaultItems.filter { it.isVideo == activeTabIsVideo }
    }

    // شاشة عرض الوسائط بالكامل
    var viewMediaItem by remember { mutableStateOf<VaultMediaItem?>(null) }

    // التحكم بصلاحيات الصور والفيديوهات
    var hasCheckedPermissions by remember { mutableStateOf(false) }
    var hasPermissions by remember { mutableStateOf(false) }

    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            hasPermissions = result.values.all { it }
            hasCheckedPermissions = true
            if (!hasPermissions) {
                Toast.makeText(context, "الخزانة تحتاج لصلاحيات الملفات لتعمل بشكل صحيح ❌", Toast.LENGTH_LONG).show()
                onBack()
            }
        }
    )

    // التحقق الفوري من الصلاحيات
    LaunchedEffect(Unit) {
        val allGranted = permissionsToRequest.all {
            androidx.core.content.ContextCompat.checkSelfPermission(context, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        if (allGranted) {
            hasPermissions = true
            hasCheckedPermissions = true
        }
    }

    // شاشة طلب الصلاحية المخصصة
    if (!hasCheckedPermissions) {
        VaultPermissionsOnboardingScreen(
            onAccept = {
                permissionLauncher.launch(permissionsToRequest)
            },
            onDecline = {
                onBack()
            }
        )
        return
    }

    // نافذة عرض ملف بملء الشاشة
    if (viewMediaItem != null) {
        val initialIndex = filteredItems.indexOf(viewMediaItem)
        VaultMediaViewerScreen(
            items = filteredItems,
            initialIndex = if (initialIndex != -1) initialIndex else 0,
            onBack = { viewMediaItem = null },
            onRestore = { item ->
                viewModel.restoreVaultMedia(listOf(item), context) { count ->
                    coroutineScope.launch(Dispatchers.Main) {
                        Toast.makeText(context, "تم استعادة $count ملف بنجاح وإعادته للمعرض ✓", Toast.LENGTH_SHORT).show()
                        viewMediaItem = null
                    }
                }
            },
            onDelete = { item ->
                viewModel.deleteVaultMedia(listOf(item)) {
                    coroutineScope.launch(Dispatchers.Main) {
                        Toast.makeText(context, "تم حذف الملف نهائياً ✓", Toast.LENGTH_SHORT).show()
                        viewMediaItem = null
                    }
                }
            }
        )
        return
    }

    // لاقط الوسائط التفاعلي من أندرويد
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                Toast.makeText(context, "جاري استيراد وتأمين الملفات... ⏳", Toast.LENGTH_LONG).show()
                viewModel.importMedia(uris, activeTabIsVideo, context) { count ->
                    coroutineScope.launch(Dispatchers.Main) {
                        if (count > 0) {
                            Toast.makeText(
                                context,
                                "تم استيراد وحماية $count من الملفات بنجاح. يرجى مراجعة المعرض لحذف الملف الأصلي يدوياً إذا لم يحذف تلقائياً لأمان خصوصيتك ✓",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(context, "فشل استيراد الملفات، يرجى المحاولة لاحقاً", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    )

    BackHandler {
        if (isSelectionMode) {
            isSelectionMode = false
            selectedItems.clear()
        } else {
            onBack()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(VaultNavyDeep, Color(0xFF020617)))),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VaultNavyDeep)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // شريط الأدوات العلوي التفاعلي
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelectionMode) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                isSelectionMode = false
                                selectedItems.clear()
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "إلغاء التحديد", tint = VaultGold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تم تحديد ${selectedItems.size} عنصر",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = VaultTextLight
                            )
                        }

                        // إجراءات سريعة للتحديد المتعدد
                        Row {
                            // 1. استعادة
                            IconButton(onClick = {
                                if (selectedItems.isNotEmpty()) {
                                    Toast.makeText(context, "جاري استعادة الملفات المحددة... ⏳", Toast.LENGTH_SHORT).show()
                                    viewModel.restoreVaultMedia(selectedItems.toList(), context) { count ->
                                        coroutineScope.launch(Dispatchers.Main) {
                                            Toast.makeText(context, "تم استعادة $count ملف بنجاح ✓", Toast.LENGTH_SHORT).show()
                                            isSelectionMode = false
                                            selectedItems.clear()
                                        }
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Unarchive, contentDescription = "استعادة", tint = VaultGold)
                            }

                            // 2. مشاركة
                            IconButton(onClick = {
                                if (selectedItems.isNotEmpty()) {
                                    shareMultipleVaultMedia(context, selectedItems.toList())
                                }
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "مشاركة", tint = VaultGold)
                            }

                            // 3. حذف نهائي
                            IconButton(onClick = {
                                if (selectedItems.isNotEmpty()) {
                                    viewModel.deleteVaultMedia(selectedItems.toList()) {
                                        coroutineScope.launch(Dispatchers.Main) {
                                            Toast.makeText(context, "تم حذف الملفات المحددة نهائياً ✓", Toast.LENGTH_SHORT).show()
                                            isSelectionMode = false
                                            selectedItems.clear()
                                        }
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف نهائي", tint = VaultError)
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "عودة لقفل التطبيقات", tint = VaultGold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "الخزانة الآمنة 🔒",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = VaultGold
                            )
                        }

                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier
                                .background(VaultNavyCard, CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "إعدادات الخزانة",
                                tint = VaultGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // التبويبات العلوية الفخمة والمخصصة (صور / فيديوهات)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VaultNavyCard)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!activeTabIsVideo) VaultGold else Color.Transparent)
                            .clickable {
                                activeTabIsVideo = false
                                isSelectionMode = false
                                selectedItems.clear()
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Photo,
                                contentDescription = "صور",
                                tint = if (!activeTabIsVideo) VaultNavyDeep else VaultTextGray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "الصور",
                                fontWeight = FontWeight.Bold,
                                color = if (!activeTabIsVideo) VaultNavyDeep else VaultTextGray,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTabIsVideo) VaultGold else Color.Transparent)
                            .clickable {
                                activeTabIsVideo = true
                                isSelectionMode = false
                                selectedItems.clear()
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "فيديوهات",
                                tint = if (activeTabIsVideo) VaultNavyDeep else VaultTextGray,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "الفيديوهات",
                                fontWeight = FontWeight.Bold,
                                color = if (activeTabIsVideo) VaultNavyDeep else VaultTextGray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(
                    onClick = {
                        val filterType = if (activeTabIsVideo) {
                            ActivityResultContracts.PickVisualMedia.VideoOnly
                        } else {
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        }
                        pickerLauncher.launch(PickVisualMediaRequest(filterType))
                    },
                    containerColor = VaultGold,
                    contentColor = VaultNavyDeep,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة ملفات")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (activeTabIsVideo) "إخفاء مقاطع فيديو" else "إخفاء صور",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (filteredItems.isEmpty()) {
                // شاشة عدم وجود عناصر
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (activeTabIsVideo) Icons.Default.Videocam else Icons.Default.Photo,
                        contentDescription = "فارغ",
                        tint = VaultGold.copy(alpha = 0.3f),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (activeTabIsVideo) "لا توجد مقاطع فيديو مخفية" else "لا توجد صور مخفية حتى الآن",
                        color = VaultTextLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "اضغط على الزر أدناه لنقل ملفاتك الهامة للخزنة الآمنة والمشفرة 🔒",
                        color = VaultTextGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            } else {
                // شبكة الملفات الفخمة ذات الأركان المنحنية وتأثير القفل البصري
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        val isSelected = selectedItems.contains(item)
                        
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(VaultNavyCard)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) VaultGold else VaultTextGray.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .combinedClickable(
                                    onClick = {
                                        if (isSelectionMode) {
                                            if (isSelected) {
                                                selectedItems.remove(item)
                                                if (selectedItems.isEmpty()) {
                                                    isSelectionMode = false
                                                }
                                            } else {
                                                selectedItems.add(item)
                                            }
                                        } else {
                                            viewMediaItem = item
                                        }
                                    },
                                    onLongClick = {
                                        if (!isSelectionMode) {
                                            isSelectionMode = true
                                            selectedItems.add(item)
                                        }
                                    }
                                )
                        ) {
                            if (item.isVideo) {
                                // عرض مصغر للفيديو
                                VideoThumbnail(
                                    videoPath = item.localPath,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // عرض مصغر للصورة
                                AsyncImage(
                                    model = item.localPath,
                                    contentDescription = "صورة",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // تدرج داكن للتأكيد البصري
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                                        )
                                    )
                            )

                            // أيقونة القفل الفخمة للتأكيد البصري على حماية الملف
                            Box(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(20.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .align(Alignment.BottomStart),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "محمي",
                                    tint = VaultGold,
                                    modifier = Modifier.size(11.dp)
                                )
                            }

                            // مؤشر الاختيار المتعدد النشط
                            if (isSelectionMode) {
                                Box(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(22.dp)
                                        .background(
                                            color = if (isSelected) VaultGold else Color.Black.copy(alpha = 0.4f),
                                            shape = CircleShape
                                        )
                                        .border(1.dp, VaultTextLight, CircleShape)
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "محدد",
                                            tint = VaultNavyDeep,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * شاشة عرض وتكبير الصور وتشغيل الفيديوهات بملء الشاشة مع دعم التنقل بالسحب يميناً ويساراً
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VaultMediaViewerScreen(
    items: List<VaultMediaItem>,
    initialIndex: Int,
    onBack: () -> Unit,
    onRestore: (VaultMediaItem) -> Unit,
    onDelete: (VaultMediaItem) -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = initialIndex) { items.size }
    val coroutineScope = rememberCoroutineScope()

    var showControls by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = Color.Black,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "رجوع",
                            tint = VaultTextLight
                        )
                    }

                    val currentItem = items.getOrNull(pagerState.currentPage)
                    Text(
                        text = currentItem?.name ?: "",
                        color = VaultTextLight,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(onClick = {
                        currentItem?.let { shareSingleVaultMedia(context, it) }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة",
                            tint = VaultGold
                        )
                    }
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .navigationBarsPadding()
                        .padding(vertical = 12.dp, horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentItem = items.getOrNull(pagerState.currentPage)

                    // زر استعادة الملف للمعرض
                    Button(
                        onClick = { currentItem?.let { onRestore(it) } },
                        colors = ButtonDefaults.buttonColors(containerColor = VaultGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).padding(horizontal = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Unarchive, contentDescription = "استعادة", tint = VaultNavyDeep)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("استعادة للمعرض", color = VaultNavyDeep, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    // زر الحذف النهائي من النظام
                    Button(
                        onClick = { currentItem?.let { onDelete(it) } },
                        colors = ButtonDefaults.buttonColors(containerColor = VaultError),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).padding(horizontal = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف نهائي", tint = VaultTextLight)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("حذف نهائي", color = VaultTextLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { index ->
            val item = items.getOrNull(index)
            if (item != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { showControls = !showControls },
                    contentAlignment = Alignment.Center
                ) {
                    if (item.isVideo) {
                        VideoPlayerView(
                            videoPath = item.localPath,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                        )
                    } else {
                        AsyncImage(
                            model = item.localPath,
                            contentDescription = "عرض الصورة",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

/**
 * مشغل الفيديوهات المدمج والتفاعلي بملء الشاشة مع شريط التحكم بدون مكتبات خارجية لتوفير حجم التطبيق والسرعة
 */
@Composable
fun VideoPlayerView(videoPath: String, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            VideoView(context).apply {
                val mediaController = MediaController(context)
                mediaController.setAnchorView(this)
                setMediaController(mediaController)
                setVideoPath(videoPath)
                setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.isLooping = true
                    start()
                }
            }
        },
        update = { videoView ->
            videoView.setVideoPath(videoPath)
        },
        modifier = modifier
    )
}

/**
 * منشئ صور مصغرة للفيديوهات ديناميكياً لتسريع استعراض المعرض بكفاءة
 */
@Composable
fun VideoThumbnail(videoPath: String, modifier: Modifier = Modifier) {
    var thumbnail by remember(videoPath) { mutableStateOf<Bitmap?>(null) }
    
    LaunchedEffect(videoPath) {
        withContext(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(videoPath)
                // جلب الإطار الأول من الثانية الأولى لتوفير دقة وتوفير ذاكرة ممتاز
                thumbnail = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                retriever.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    if (thumbnail != null) {
        Image(
            bitmap = thumbnail!!.asImageBitmap(),
            contentDescription = "فيديو مصغر",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "فيديو", tint = VaultGold, modifier = Modifier.size(32.dp))
        }
    }
}

/**
 * مشاركة ملف فردي عن طريق الـ FileProvider المعتمد لتفادي مشاكل الحماية
 */
fun shareSingleVaultMedia(context: Context, item: VaultMediaItem) {
    try {
        val file = File(item.localPath)
        if (file.exists()) {
            val authority = "${context.packageName}.fileprovider"
            val uri = androidx.core.content.FileProvider.getUriForFile(context, authority, file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = if (item.isVideo) "video/mp4" else "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "مشاركة الملف عبر"))
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "فشل تجهيز الملف للمشاركة ❌", Toast.LENGTH_SHORT).show()
    }
}

/**
 * مشاركة مجموعة ملفات متعددة دفعة واحدة بأمان
 */
fun shareMultipleVaultMedia(context: Context, items: List<VaultMediaItem>) {
    try {
        val uris = ArrayList<Uri>()
        val authority = "${context.packageName}.fileprovider"
        for (item in items) {
            val file = File(item.localPath)
            if (file.exists()) {
                val uri = androidx.core.content.FileProvider.getUriForFile(context, authority, file)
                uris.add(uri)
            }
        }
        
        if (uris.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "*/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "مشاركة الملفات المحددة عبر"))
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "فشل تجهيز الملفات للمشاركة ❌", Toast.LENGTH_SHORT).show()
    }
}

/**
 * شاشة إعدادات الخزانة الآمنة الخاصة
 */
@Composable
fun VaultSettingsScreen(
    viewModel: AppLockViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val useSamePasscode by viewModel.useSamePasscodeForVault.collectAsState()
    var isChangingPasscode by remember { mutableStateOf(false) }

    if (isChangingPasscode) {
        VaultLoginScreen(
            viewModel = viewModel,
            onBack = { isChangingPasscode = false },
            onUnlockSuccess = {
                isChangingPasscode = false
                Toast.makeText(context, "تم تغيير الرمز السري بنجاح ✓", Toast.LENGTH_SHORT).show()
            }
        )
        return
    }

    BackHandler { onBack() }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(VaultNavyDeep, Color(0xFF020617)))),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "عودة", tint = VaultGold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "إعدادات الخزانة الآمنة",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = VaultGold
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = VaultNavyCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "رمز مرور موحد",
                                color = VaultTextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "استخدم نفس رمز مرور قفل التطبيقات لفتح الخزانة",
                                color = VaultTextGray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Switch(
                            checked = useSamePasscode,
                            onCheckedChange = { viewModel.setUseSamePasscodeForVault(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VaultNavyDeep,
                                checkedTrackColor = VaultGold,
                                uncheckedThumbColor = VaultTextGray,
                                uncheckedTrackColor = VaultNavyDeep
                            )
                        )
                    }

                    if (!useSamePasscode) {
                        HorizontalDivider(
                            color = VaultTextGray.copy(alpha = 0.1f),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        Button(
                            onClick = { isChangingPasscode = true },
                            colors = ButtonDefaults.buttonColors(containerColor = VaultGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "تغيير الرمز", tint = VaultNavyDeep)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تغيير رمز الخزانة المخصص", color = VaultNavyDeep, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // إرشادات أمنية
            Card(
                colors = CardDefaults.cardColors(containerColor = VaultNavyCard.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "نصائح أمنية وإرشادات هامّة 💡",
                        color = VaultGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val tips = listOf(
                        "تأكد من عدم حذف مجلد التطبيق الآمن لتفادي فقدان صورك المخفية.",
                        "الاستعادة تقوم بنقل الملفات إلى مجلد 'RestoredVault' بالمعرض العام.",
                        "حجم تشفير الملفات ونقلها يعتمد على مساحة جهازك الشاغرة.",
                        "الخزانة تعمل 100% بدون إنترنت لضمان أقصى حماية وخصوصية تامة."
                    )

                    tips.forEach { tip ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "•", color = VaultGold, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                            Text(text = tip, color = VaultTextGray, fontSize = 13.sp, lineHeight = 18.sp)
                        }
                    }
                }
            }
        }
    }
}
