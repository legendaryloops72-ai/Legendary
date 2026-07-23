package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.MainActivity
import com.aistudio.kidspolice.abcd.R
import com.example.data.AppInfo
import com.example.data.AppLockPreferences
import com.example.data.IntruderRecord
import com.example.service.AppLockService
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Close
import coil.compose.AsyncImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.wrapContentHeight
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import com.example.ui.theme.AppThemes
import com.example.ui.theme.AppThemeConfig


// ألوان مخصصة عالية الفخامة للثيم الكحلي والذهبي
val NavyDeep = Color(0xFF0F172A)
val NavyCard = Color(0xFF1E293B)
val GoldYellow = Color(0xFFD4AF37)
val GoldLight = Color(0xFFF5A623)
val TextLight = Color(0xFFF8FAFC)
val TextGray = Color(0xFF94A3B8)
val AccentTeal = Color(0xFF10B981)
val ErrorRed = Color(0xFFEF4444)

@Composable
fun AppLockNavigationWrapper(
    viewModel: AppLockViewModel,
    isLockRequest: Boolean = false,
    targetPackageName: String? = null,
    onLockVerified: (() -> Unit)? = null
) {
    // فرض تخطيط RTL العربي بشكل كامل لجميع الشاشات
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val context = LocalContext.current
        val isSetupComplete by viewModel.isSetupComplete.collectAsState()
        var currentScreen by remember { mutableStateOf("splash") }

        LaunchedEffect(isSetupComplete, isLockRequest) {
            if (isLockRequest) {
                currentScreen = "lock_screen"
            } else if (!isSetupComplete) {
                currentScreen = "setup"
            } else {
                // التحقق من الصلاحيات المطلوبة، إذا كانت مفقودة نوجه لشاشة الصلاحيات أولاً
                val hasOverlay = viewModel.hasOverlayPermission(context)
                val hasUsage = viewModel.hasUsageAccessPermission(context)
                currentScreen = if (!hasOverlay || !hasUsage) {
                    "permissions"
                } else {
                    "app_list"
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NavyDeep, Color(0xFF020617))
                    )
                )
        ) {
            when (currentScreen) {
                "splash" -> {
                    AppLockSplashScreen {
                        if (isLockRequest) {
                            currentScreen = "lock_screen"
                        } else if (!isSetupComplete) {
                            currentScreen = "setup"
                        } else {
                            currentScreen = "app_list"
                        }
                    }
                }
                "setup" -> {
                    SetupScreen(viewModel = viewModel) {
                        currentScreen = "permissions"
                    }
                }
                "permissions" -> {
                    PermissionsScreen(viewModel = viewModel) {
                        currentScreen = "app_list"
                    }
                }
                "app_list" -> {
                    AppListScreen(
                        viewModel = viewModel,
                        onNavigateToSettings = { currentScreen = "settings" },
                        onNavigateToPermissions = { currentScreen = "permissions" },
                        onNavigateToVault = { currentScreen = "vault_login" }
                    )
                }
                "lock_screen" -> {
                    LockOverlayScreen(
                        viewModel = viewModel,
                        targetPackageName = targetPackageName ?: "demo",
                        isSimulated = !isLockRequest
                    ) {
                        if (isLockRequest && onLockVerified != null) {
                            onLockVerified()
                        } else {
                            // العودة لقائمة التطبيقات بعد محاكاة ناجحة
                            currentScreen = "app_list"
                        }
                    }
                }
                "settings" -> {
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = "app_list" },
                        onNavigateToAntiUninstall = { currentScreen = "anti_uninstall" },
                        onNavigateToThemePicker = { currentScreen = "theme_picker" },
                        onNavigateToIntruderSelfie = { currentScreen = "intruder_selfie" }
                    )
                }
                "intruder_selfie" -> {
                    IntruderSelfieScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = "settings" }
                    )
                }
                "anti_uninstall" -> {
                    AntiUninstallScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = "settings" }
                    )
                }
                "theme_picker" -> {
                    ThemePickerScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = "settings" }
                    )
                }
                "vault_login" -> {
                    VaultLoginScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = "app_list" },
                        onUnlockSuccess = { currentScreen = "vault_home" }
                    )
                }
                "vault_home" -> {
                    VaultHomeScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = "app_list" },
                        onNavigateToSettings = { currentScreen = "vault_settings" }
                    )
                }
                "vault_settings" -> {
                    VaultSettingsScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = "vault_home" }
                    )
                }
            }
        }
    }
}

// 0. شاشة ترحيبية أنيقة
@Composable
fun AppLockSplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1800)
        onFinished()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "شعار التطبيق",
            tint = GoldYellow,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "قفل التطبيقات الذكي",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextLight,
            fontFamily = FontFamily.SansSerif
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "أمان مطلق لخصوصية عائلتك وهاتفك",
            fontSize = 14.sp,
            color = TextGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        CircularProgressIndicator(
            color = GoldYellow,
            strokeWidth = 3.dp,
            modifier = Modifier.size(32.dp)
        )
    }
}

// 1. شاشة الإعداد الأول (Setup Screen)
@Composable
fun SetupScreen(viewModel: AppLockViewModel, onComplete: () -> Unit) {
    var step by remember { mutableStateOf(1) } // 1: اختيار نوع القفل، 2: إدخال القفل، 3: تأكيد القفل
    var chosenLockType by remember { mutableStateOf("PIN") } // "PIN" أو "PATTERN"
    var firstPasscode by remember { mutableStateOf("") }
    var confirmPasscode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // مؤشر الخطوات
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "إعداد الحماية للأمان",
                color = TextGray,
                fontSize = 14.sp
            )
            Text(
                text = "الخطوة $step من 3",
                color = GoldYellow,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color(0xFF334155), RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(step / 3f)
                    .fillMaxHeight()
                    .background(GoldYellow, RoundedCornerShape(2.dp))
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (step == 1) {
            Text(
                text = "اختر نوع قفل التطبيقات المفضل",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "سيتم استخدام هذا الخيار لحماية تطبيقاتك من المتطفلين بشكل فوري",
                fontSize = 13.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // خيارات القفل
            Card(
                onClick = { chosenLockType = "PIN" },
                colors = CardDefaults.cardColors(
                    containerColor = if (chosenLockType == "PIN") NavyCard else Color(0xFF1E293B).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (chosenLockType == "PIN") 2.dp else 1.dp,
                        color = if (chosenLockType == "PIN") GoldYellow else Color(0xFF334155),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                if (chosenLockType == "PIN") GoldYellow.copy(alpha = 0.15f) else Color(0xFF334155),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "رمز PIN",
                            tint = if (chosenLockType == "PIN") GoldYellow else TextGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "رمز PIN السري",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                        Text(
                            text = "إدخال رمز سري مكون من 4 أرقام لحماية مخصصة",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                    RadioButton(
                        selected = chosenLockType == "PIN",
                        onClick = { chosenLockType = "PIN" },
                        colors = RadioButtonDefaults.colors(selectedColor = GoldYellow)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                onClick = { chosenLockType = "PATTERN" },
                colors = CardDefaults.cardColors(
                    containerColor = if (chosenLockType == "PATTERN") NavyCard else Color(0xFF1E293B).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (chosenLockType == "PATTERN") 2.dp else 1.dp,
                        color = if (chosenLockType == "PATTERN") GoldYellow else Color(0xFF334155),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                if (chosenLockType == "PATTERN") GoldYellow.copy(alpha = 0.15f) else Color(0xFF334155),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "نمط الرسم",
                            tint = if (chosenLockType == "PATTERN") GoldYellow else TextGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "رسم نمط مرن",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                        Text(
                            text = "توصيل النقاط على شاشة الرسم لحماية سريعة",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                    RadioButton(
                        selected = chosenLockType == "PATTERN",
                        onClick = { chosenLockType = "PATTERN" },
                        colors = RadioButtonDefaults.colors(selectedColor = GoldYellow)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { step = 2 },
                colors = ButtonDefaults.buttonColors(containerColor = GoldYellow, contentColor = NavyDeep),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("next_step_button")
            ) {
                Text(
                    text = "المتابعة والتعيين",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowBack, contentDescription = "التالي") // معكوس في RTL
            }
        } else if (step == 2) {
            Text(
                text = if (chosenLockType == "PIN") "تعيين رمز الـ PIN الخاص بك" else "ارسم نمط القفل المفضل لديك",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "تأكد من اختيار قفل تتذكره جيداً ومحمي من الآخرين",
                fontSize = 13.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (chosenLockType == "PIN") {
                // إدخال PIN
                PinDisplayWidget(pin = firstPasscode, expectedLength = 4)
                Spacer(modifier = Modifier.height(30.dp))
                CustomNumberKeyboard(
                    onDigitClick = { digit ->
                        if (firstPasscode.length < 4) {
                            firstPasscode += digit
                        }
                    },
                    onBackspace = {
                        if (firstPasscode.isNotEmpty()) {
                            firstPasscode = firstPasscode.dropLast(1)
                        }
                    }
                )
            } else {
                // إدخال نمط
                Text(text = "ارسم النمط بسحب إصبعك وتوصيل 3 نقاط على الأقل", color = GoldYellow, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))
                PatternLockView(
                    modifier = Modifier.size(300.dp),
                    onPatternComplete = { pattern ->
                        if (pattern.length >= 3) {
                            firstPasscode = pattern
                            errorMessage = ""
                        } else {
                            errorMessage = "النمط قصير جداً، يرجى توصيل 3 نقاط على الأقل."
                        }
                    }
                )
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = errorMessage, color = ErrorRed, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        firstPasscode = ""
                        step = 1
                    },
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextLight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Text("رجوع")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (firstPasscode.isNotEmpty() && errorMessage.isEmpty()) {
                            step = 3
                        } else if (firstPasscode.isEmpty()) {
                            errorMessage = "يرجى تعيين القفل أولاً للمتابعة."
                        }
                    },
                    enabled = firstPasscode.isNotEmpty() && errorMessage.isEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldYellow, contentColor = NavyDeep),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(54.dp)
                ) {
                    Text("التالي")
                }
            }
        } else if (step == 3) {
            Text(
                text = if (chosenLockType == "PIN") "تأكيد رمز الـ PIN" else "تأكيد رسم النمط السري",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "يرجى كتابة القفل مرة أخرى للتحقق من تطابقه ومتابعة الحفظ",
                fontSize = 13.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (chosenLockType == "PIN") {
                PinDisplayWidget(pin = confirmPasscode, expectedLength = 4)
                Spacer(modifier = Modifier.height(30.dp))
                CustomNumberKeyboard(
                    onDigitClick = { digit ->
                        if (confirmPasscode.length < 4) {
                            confirmPasscode += digit
                        }
                    },
                    onBackspace = {
                        if (confirmPasscode.isNotEmpty()) {
                            confirmPasscode = confirmPasscode.dropLast(1)
                        }
                    }
                )
            } else {
                Text(text = "ارسم نفس النمط السري المرسوم سابقاً لتأكيد التطابق", color = GoldYellow, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))
                PatternLockView(
                    modifier = Modifier.size(300.dp),
                    onPatternComplete = { pattern ->
                        confirmPasscode = pattern
                        if (confirmPasscode != firstPasscode) {
                            errorMessage = "النمط غير متطابق مع الرسمة الأولى، أعد المحاولة."
                        } else {
                            errorMessage = ""
                        }
                    }
                )
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = errorMessage, color = ErrorRed, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        confirmPasscode = ""
                        step = 2
                    },
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextLight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Text("إلغاء")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        if (confirmPasscode == firstPasscode) {
                            // حفظ وتجاوز
                            viewModel.completeSetup(chosenLockType, firstPasscode)
                            onComplete()
                        } else {
                            errorMessage = "القفل السري غير متطابق! يرجى التحقق وإعادة الإدخال."
                        }
                    },
                    enabled = confirmPasscode.isNotEmpty() && confirmPasscode == firstPasscode,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldYellow, contentColor = NavyDeep),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(54.dp)
                        .testTag("confirm_setup_button")
                ) {
                    Text("حفظ وتأكيد")
                }
            }
        }
    }
}

// ويدجت لعرض خانات الـ PIN المدخلة
@Composable
fun PinDisplayWidget(pin: String, expectedLength: Int = 4) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until expectedLength) {
            val isEntered = i < pin.length
            val color by animateColorAsState(
                targetValue = if (isEntered) GoldYellow else Color(0xFF334155),
                animationSpec = spring()
            )
            val size by animateDpAsState(
                targetValue = if (isEntered) 24.dp else 18.dp,
                animationSpec = spring()
            )

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .drawBehind {
                        if (isEntered) {
                            drawCircle(color = color, radius = size.toPx() / 2f)
                        } else {
                            drawCircle(
                                color = color,
                                radius = size.toPx() / 2f,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }
            )
        }
    }
}

// لوحة مفاتيح أرقام أنيقة ومحسنة
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomNumberKeyboard(
    onDigitClick: (String) -> Unit,
    onBackspace: () -> Unit,
    isRandomized: Boolean = false
) {
    // إنشاء أرقام من 0 لـ 9 مع إمكانية الترتيب العشوائي حسب إعدادات المستخدم للحماية الفخمة
    val digits = remember(isRandomized) {
        val list = (0..9).map { it.toString() }.toMutableList()
        if (isRandomized) {
            list.shuffle()
        }
        list
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 0..2) {
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (col in 0..2) {
                    val index = row * 3 + col
                    if (index < digits.size) {
                        val digit = digits[index]
                        KeypadButton(text = digit) { onDigitClick(digit) }
                    }
                }
            }
        }
        
        // الصف الأخير (حذف - الصفر - تأكيد/فارغ)
        Row(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // زر المسح (الخلفي)
            IconButton(
                onClick = onBackspace,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFF1E293B).copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف الرقم الأخير",
                    tint = ErrorRed
                )
            }

            // رقم الصفر الأخير في القائمة
            if (digits.size > 9) {
                val lastDigit = digits[9]
                KeypadButton(text = lastDigit) { onDigitClick(lastDigit) }
            }

            // مساحة فارغة أو أيقونة للخصوصية والأناقة
            Box(
                modifier = Modifier
                    .size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "قفل",
                    tint = GoldYellow.copy(alpha = 0.3f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun KeypadButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .background(NavyCard, CircleShape)
            .border(1.dp, Color(0xFF334155), CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextLight
        )
    }
}

// 2. شاشة رسم النمط التفاعلية باستخدام Canvas
@Composable
fun PatternLockView(
    modifier: Modifier = Modifier,
    dotRadius: Float = 14f,
    hitRadius: Float = 60f,
    onPatternComplete: (String) -> Unit
) {
    var activeDots = remember { mutableStateListOf<Int>() }
    var currentTouchPoint by remember { mutableStateOf<Offset?>(null) }
    
    // إحداثيات النقاط الـ 9 في تخطيط 3x3 لسهولة الحساب
    var gridPoints = remember { List(9) { Offset.Zero } }

    BoxWithConstraints(modifier = modifier) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        
        // حساب إحداثيات النقاط ديناميكياً لتلائم أي حجم شاشة
        gridPoints = remember(width, height) {
            val cellWidth = width / 4f
            val cellHeight = height / 4f
            List(9) { i ->
                val row = i / 3
                val col = i % 3
                Offset(cellWidth * (col + 1), cellHeight * (row + 1))
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            activeDots.clear()
                            currentTouchPoint = offset
                            // فحص إذا بدأت السحبة قريباً من نقطة
                            val clickedDot = findDotIndex(offset, gridPoints, hitRadius)
                            if (clickedDot != -1) {
                                activeDots.add(clickedDot)
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val currentPoint = change.position
                            currentTouchPoint = currentPoint
                            
                            val activeIndex = findDotIndex(currentPoint, gridPoints, hitRadius)
                            if (activeIndex != -1 && !activeDots.contains(activeIndex)) {
                                activeDots.add(activeIndex)
                            }
                        },
                        onDragEnd = {
                            val pattern = activeDots.joinToString("") { (it + 1).toString() }
                            onPatternComplete(pattern)
                            currentTouchPoint = null
                        }
                    )
                }
        ) {
            // 1. رسم الخطوط بين النقاط النشطة المحددة
            if (activeDots.isNotEmpty()) {
                for (i in 0 until activeDots.size - 1) {
                    val p1 = gridPoints[activeDots[i]]
                    val p2 = gridPoints[activeDots[i + 1]]
                    drawLine(
                        color = GoldYellow,
                        start = p1,
                        end = p2,
                        strokeWidth = 8f
                    )
                }
                
                // خط حر ممتد من آخر نقطة نشطة إلى نقطة لمس الإصبع الحالية
                currentTouchPoint?.let { touch ->
                    val lastPoint = gridPoints[activeDots.last()]
                    drawLine(
                        color = GoldYellow.copy(alpha = 0.6f),
                        start = lastPoint,
                        end = touch,
                        strokeWidth = 6f
                    )
                }
            }

            // 2. رسم الـ 9 دوائر بالكامل وتلوين النشط منها بالذهبي البراق
            for (i in 0..8) {
                val point = gridPoints[i]
                val isActive = activeDots.contains(i)
                
                // الدائرة الخارجية الكبيرة (هالة محيطة بالنشط)
                drawCircle(
                    color = if (isActive) GoldYellow.copy(alpha = 0.2f) else Color(0xFF334155),
                    radius = if (isActive) dotRadius * 2.5f else dotRadius * 1.5f,
                    center = point
                )
                
                // الدائرة الداخلية المركزية
                drawCircle(
                    color = if (isActive) GoldYellow else Color(0xFF64748B),
                    radius = dotRadius,
                    center = point
                )
            }
        }
    }
}

// دالة مساعدة لمعرفة أي نقطة قريبة من لمسة الإصبع
private fun findDotIndex(touchPoint: Offset, points: List<Offset>, threshold: Float): Int {
    for (i in points.indices) {
        val p = points[i]
        val distance = sqrt((touchPoint.x - p.x) * (touchPoint.x - p.x) + (touchPoint.y - p.y) * (touchPoint.y - p.y))
        if (distance < threshold) {
            return i
        }
    }
    return -1
}

// 3. شاشة منح الصلاحيات (Permissions Screen)
@Composable
fun PermissionsScreen(viewModel: AppLockViewModel, onGranted: () -> Unit) {
    val context = LocalContext.current
    var hasOverlay by remember { mutableStateOf(viewModel.hasOverlayPermission(context)) }
    var hasUsage by remember { mutableStateOf(viewModel.hasUsageAccessPermission(context)) }

    // التحقق مجدداً من الصلاحيات بمجرد عودة المستخدم للتطبيق
    LaunchedEffect(Unit) {
        while (true) {
            hasOverlay = viewModel.hasOverlayPermission(context)
            hasUsage = viewModel.hasUsageAccessPermission(context)
            if (hasOverlay && hasUsage) {
                // تفعيل خدمة المراقبة فور منح الصلاحيات
                val serviceIntent = Intent(context, AppLockService::class.java)
                try {
                    ContextCompat.startForegroundService(context, serviceIntent)
                } catch (e: Exception) {
                    context.startService(serviceIntent)
                }
                onGranted()
                break
            }
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.img_security_hero_1784470589276),
                contentDescription = "حماية",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, GoldYellow.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "منح صلاحيات الحماية",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "يحتاج التطبيق لصلاحيات النظام لمراقبة فتح التطبيقات الأخرى المقفلة وعرض شاشة الأمان بنجاح.",
                fontSize = 14.sp,
                color = TextGray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 1. صلاحية الظهور فوق التطبيقات
            PermissionCard(
                title = "الظهور فوق التطبيقات (Overlay Permission)",
                description = "تسمح للتطبيق بعرض شاشة القفل وتغطية محتوى التطبيقات المقفلة لمنع المتطفلين.",
                isGranted = hasOverlay,
                onGrantClick = {
                    try {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        context.startActivity(intent)
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. صلاحية الوصول لبيانات الاستخدام
            PermissionCard(
                title = "الوصول لبيانات الاستخدام (Usage Stats Access)",
                description = "تمكن التطبيق من معرفة التطبيق الذي تحاول فتحه حالياً لمعرفة إذا كان ضمن القائمة المقفلة.",
                isGranted = hasUsage,
                onGrantClick = {
                    try {
                        val intent = Intent(
                            Settings.ACTION_USAGE_ACCESS_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        context.startActivity(intent)
                    }
                }
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            if (!hasOverlay || !hasUsage) {
                Text(
                    text = "يرجى منح الصلاحيتين أعلاه للمتابعة لتفعيل حامي التطبيقات...",
                    fontSize = 12.sp,
                    color = GoldLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )
            }
            
            Button(
                onClick = {
                    if (hasOverlay && hasUsage) {
                        onGranted()
                    } else {
                        Toast.makeText(context, "الرجاء تفعيل الصلاحيات من إعدادات الهاتف للمتابعة", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = hasOverlay && hasUsage,
                colors = ButtonDefaults.buttonColors(containerColor = GoldYellow, contentColor = NavyDeep),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "بدء حماية هاتفي الآن",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun PermissionCard(
    title: String,
    description: String,
    isGranted: Boolean,
    onGrantClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (isGranted) AccentTeal else Color(0xFF334155),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = if (isGranted) "مفعل" else "معطل",
                    tint = if (isGranted) AccentTeal else ErrorRed,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = TextGray,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (!isGranted) {
                Button(
                    onClick = onGrantClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldYellow, contentColor = NavyDeep),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("تفعيل الصلاحية يدوياً", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            } else {
                Text(
                    text = "تم منح الصلاحية بنجاح ✓",
                    color = AccentTeal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// 4. شاشة قائمة التطبيقات (App List Screen)
@Composable
fun AppListScreen(
    viewModel: AppLockViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToVault: () -> Unit
) {
    val context = LocalContext.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredApps by viewModel.filteredApps.collectAsState()
    val isLoading by viewModel.isLoadingApps.collectAsState()
    
    // التحقق إذا كان هناك حاجة لمنح صلاحية إضافية وتحديث القائمة
    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps()
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyDeep)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "قفل",
                            tint = GoldYellow,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "قفل التطبيقات الذكي",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextLight
                        )
                    }
                    
                    Row {
                        IconButton(
                            onClick = onNavigateToPermissions,
                            modifier = Modifier
                                .background(NavyCard, CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "صلاحيات القفل",
                                tint = GoldYellow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier
                                .background(NavyCard, CircleShape)
                                .size(40.dp)
                                .testTag("settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "الإعدادات",
                                tint = GoldYellow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // حقل البحث
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("ابحث باسم التطبيق أو الحزمة...", color = TextGray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث", tint = GoldYellow) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight,
                        focusedBorderColor = GoldYellow,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = NavyCard,
                        unfocusedContainerColor = NavyCard
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = GoldYellow,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (filteredApps.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "لا توجد نتائج",
                        tint = GoldYellow.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "لم يتم العثور على أي تطبيقات مثبتة تطابق بحثك",
                        color = TextGray,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            onClick = onNavigateToVault,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .border(BorderStroke(1.5.dp, GoldYellow), RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = NavyCard),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .background(GoldYellow.copy(alpha = 0.15f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "الخزانة الآمنة",
                                            tint = GoldYellow,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = "الخزانة الآمنة 🔒",
                                            color = TextLight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            text = "إخفاء الصور ومقاطع الفيديو الخاصة بك بأمان وتشفير كامل",
                                            color = TextGray,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "دخول",
                                    tint = GoldYellow,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    item {
                        // إحصائية صغيرة ورأس القائمة
                        val lockedCount = filteredApps.count { it.isLocked }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "التطبيقات المثبتة (${filteredApps.size})",
                                color = TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "المحمية الآن: $lockedCount",
                                color = GoldYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    items(filteredApps, key = { it.packageName }) { appInfo ->
                        AppListItem(appInfo = appInfo) {
                            viewModel.toggleAppLock(appInfo.packageName)
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AppListItem(appInfo: AppInfo, onToggle: () -> Unit) {
    val context = LocalContext.current
    var appIcon by remember { mutableStateOf<Drawable?>(null) }

    // جلب أيقونة التطبيق ديناميكياً في الخلفية بدون حظر الواجهة لتوفير ذاكرة ممتازة
    LaunchedEffect(appInfo.packageName) {
        try {
            appIcon = context.packageManager.getApplicationIcon(appInfo.packageName)
        } catch (e: Exception) {
            appIcon = null
        }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (appInfo.isLocked) NavyCard else Color(0xFF1E293B).copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (appInfo.isLocked) 1.5.dp else 1.dp,
                color = if (appInfo.isLocked) GoldYellow else Color(0xFF334155),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onToggle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // عرض الأيقونة الحقيقية أو بديل افتراضي فخم
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF334155).copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (appIcon != null) {
                        val bitmap = remember(appIcon) { appIcon!!.toBitmap() }
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = appInfo.label,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "شعار افتراضي",
                            tint = GoldYellow,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = appInfo.label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextLight,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = appInfo.packageName,
                        fontSize = 11.sp,
                        color = TextGray,
                        textAlign = TextAlign.Start,
                        maxLines = 1
                    )
                }
            }

            // سويتش مخصص لتمثيل حالة القفل (ذهبي عند التفعيل)
            Switch(
                checked = appInfo.isLocked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NavyDeep,
                    checkedTrackColor = GoldYellow,
                    uncheckedThumbColor = TextGray,
                    uncheckedTrackColor = Color(0xFF334155)
                ),
                modifier = Modifier.testTag("lock_switch_${appInfo.packageName}")
            )
        }
    }
}

// 5. شاشة القفل عند محاولة فتح تطبيق مقفل (Lock overlay screen)
@Composable
fun LockOverlayScreen(
    viewModel: AppLockViewModel,
    targetPackageName: String,
    isSimulated: Boolean = false,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val lockType by viewModel.lockType.collectAsState()
    val isRandomKeyboardEnabled by viewModel.isRandomKeyboardEnabled.collectAsState()
    val lockoutTimeRemaining by viewModel.lockoutTimeRemaining.collectAsState()
    val wrongAttempts by viewModel.wrongAttempts.collectAsState()

    var inputPasscode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var appLabel by remember { mutableStateOf("") }
    var appIcon by remember { mutableStateOf<Drawable?>(null) }

    LaunchedEffect(targetPackageName) {
        try {
            val appInfo = context.packageManager.getApplicationInfo(targetPackageName, 0)
            appLabel = context.packageManager.getApplicationLabel(appInfo).toString()
            appIcon = context.packageManager.getApplicationIcon(appInfo)
        } catch (e: Exception) {
            appLabel = "تطبيق مقفل"
            appIcon = null
        }
    }

    val biometricService = remember { com.example.service.BiometricService(context) }
    val activity = context as? androidx.fragment.app.FragmentActivity

    val isIntruderSelfieEnabled by viewModel.isIntruderSelfieEnabled.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val intruderCameraService = remember { com.example.service.IntruderCameraService(context) }

    // التهيئة التلقائية والربط مع دورة حياة الشاشة
    LaunchedEffect(isIntruderSelfieEnabled, lifecycleOwner) {
        if (isIntruderSelfieEnabled) {
            val hasCameraPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (hasCameraPermission) {
                intruderCameraService.initialize(lifecycleOwner)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            intruderCameraService.release()
        }
    }

    fun triggerBiometricAuthentication() {
        if (activity != null) {
            biometricService.authenticate(
                activity = activity,
                reason = "قم بالمصادقة لفتح التطبيق",
                onSuccess = {
                    AppLockService.temporarilyUnlockedApps[targetPackageName] = System.currentTimeMillis()
                    onSuccess()
                },
                onFailed = {
                    if (isIntruderSelfieEnabled) {
                        val hasCameraPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        if (hasCameraPermission) {
                            intruderCameraService.captureIntruderPhoto(targetPackageName, "BIOMETRIC")
                        }
                    }
                },
                onError = { error ->
                    errorMessage = when (error) {
                        com.example.service.BiometricError.NotAvailable -> {
                            "مستشعر البصمة غير متوفر حالياً. يرجى استخدام PIN الاحتياطي."
                        }
                        com.example.service.BiometricError.NotEnrolled -> {
                            "لا توجد بصمة مسجلة، الرجاء إضافتها من إعدادات الجهاز."
                        }
                        com.example.service.BiometricError.LockedOut -> {
                            "تم قفل البصمة مؤقتًا بسبب محاولات فاشلة كثيرة، استخدم PIN."
                        }
                        is com.example.service.BiometricError.Other -> {
                            error.message
                        }
                    }
                }
            )
        } else {
            errorMessage = "عذراً، فشل تهيئة بيئة التحقق بالبصمة."
        }
    }

    // إطلاق البصمة تلقائياً إذا كان نوع القفل يدعم البصمة
    LaunchedEffect(lockType) {
        if (lockType == "BIOMETRIC") {
            triggerBiometricAuthentication()
        }
    }

    // التحقق من الرقم السري تلقائياً فور كتابة 4 أرقام
    LaunchedEffect(inputPasscode) {
        if (inputPasscode.length == 4) {
            delay(150)
            val correct = viewModel.verifyPasscode(inputPasscode)
            if (correct) {
                // حفظ وقت إلغاء القفل للتطبيق
                AppLockService.temporarilyUnlockedApps[targetPackageName] = System.currentTimeMillis()
                onSuccess()
            } else {
                errorMessage = "الرمز السري غير صحيح، تبقى لديك محاولات أخرى."
                inputPasscode = ""
                
                // التقاط سيلفي الدخيل عند الخطأ في الـ PIN
                if (isIntruderSelfieEnabled) {
                    val hasCameraPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    if (hasCameraPermission) {
                        intruderCameraService.captureIntruderPhoto(targetPackageName, "PIN")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // الهيدر الفخم للتطبيق المستهدف
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF1E293B), CircleShape)
                    .border(2.dp, GoldYellow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (appIcon != null) {
                    val bitmap = remember(appIcon) { appIcon!!.toBitmap() }
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = appLabel,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "مقفل",
                        tint = GoldYellow,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "هذا التطبيق مقفل للحماية",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "يرجى فك قفل $appLabel لمتابعة الاستخدام",
                fontSize = 13.sp,
                color = TextGray,
                textAlign = TextAlign.Center
            )
        }

        // شاشات الإدخال بناء على نوع القفل المختار
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (lockoutTimeRemaining > 0) {
                // تم القفل مؤقتاً بسبب 5 محاولات خاطئة
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "تحذير",
                    tint = ErrorRed,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "تم حظر المحاولة مؤقتاً!",
                    fontSize = 18.sp,
                    color = ErrorRed,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "يرجى الانتظار $lockoutTimeRemaining ثانية قبل المحاولة مجدداً.",
                    fontSize = 14.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center
                )
            } else {
                if (lockType == "PATTERN") {
                    // شاشة رسم النمط
                    Text(
                        text = "ارسم النمط السري لفك القفل",
                        fontSize = 14.sp,
                        color = GoldYellow,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    PatternLockView(
                        modifier = Modifier.size(280.dp),
                        onPatternComplete = { pattern ->
                            val correct = viewModel.verifyPasscode(pattern)
                            if (correct) {
                                AppLockService.temporarilyUnlockedApps[targetPackageName] = System.currentTimeMillis()
                                onSuccess()
                            } else {
                                errorMessage = "النمط السري المرسوم غير صحيح، أعد المحاولة."
                                
                                // التقاط سيلفي الدخيل عند الخطأ في النمط
                                if (isIntruderSelfieEnabled) {
                                    val hasCameraPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                                    if (hasCameraPermission) {
                                        intruderCameraService.captureIntruderPhoto(targetPackageName, "PATTERN")
                                    }
                                }
                            }
                        }
                    )
                } else {
                    // شاشة إدخال PIN (كذلك للـ Biometric كخيار احتياطي)
                    PinDisplayWidget(pin = inputPasscode, expectedLength = 4)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (lockType == "BIOMETRIC") {
                        IconButton(
                            onClick = {
                                triggerBiometricAuthentication()
                            },
                            modifier = Modifier
                                .background(GoldYellow.copy(alpha = 0.15f), CircleShape)
                                .size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "تشغيل البصمة",
                                tint = GoldYellow,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = {
                                Toast.makeText(context, "الرجاء كتابة رمز الـ PIN المكون من 4 أرقام مباشرة", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text(
                                text = "استخدام PIN بدلاً من ذلك 🔢",
                                color = GoldYellow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    CustomNumberKeyboard(
                        onDigitClick = { digit ->
                            if (inputPasscode.length < 4) {
                                inputPasscode += digit
                            }
                        },
                        onBackspace = {
                            if (inputPasscode.isNotEmpty()) {
                                inputPasscode = inputPasscode.dropLast(1)
                            }
                        },
                        isRandomized = isRandomKeyboardEnabled
                    )
                }
            }

            if (errorMessage.isNotEmpty() && lockoutTimeRemaining == 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = ErrorRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // خروج وإلغاء لسهولة الاستخدام
        TextButton(
            onClick = {
                if (isSimulated) {
                    onSuccess() // في التجريب نعتبره نجح عند الرغبة بالعودة
                } else {
                    // الخروج إلى اللانشر الرئيسي للهاتف إذا رفض فتح القفل
                    val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(homeIntent)
                }
            },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = if (isSimulated) "تجاوز المحاكاة (عودة)" else "إلغاء الخروج للشاشة الرئيسية",
                color = GoldYellow,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}


// 6. شاشة الإعدادات (Settings Screen)
@Composable
fun SettingsScreen(
    viewModel: AppLockViewModel,
    onBack: () -> Unit,
    onNavigateToAntiUninstall: () -> Unit,
    onNavigateToThemePicker: () -> Unit,
    onNavigateToIntruderSelfie: () -> Unit
) {
    val context = LocalContext.current
    val lockType by viewModel.lockType.collectAsState()
    val isRandomKeyboardEnabled by viewModel.isRandomKeyboardEnabled.collectAsState()
    val lockTimeoutMs by viewModel.lockTimeoutMs.collectAsState()
    
    val biometricService = remember { com.example.service.BiometricService(context) }
    val lockTypes = remember {
        if (biometricService.canCheckBiometrics()) {
            listOf("PIN", "PATTERN", "BIOMETRIC")
        } else {
            listOf("PIN", "PATTERN")
        }
    }

    LaunchedEffect(lockTypes) {
        if (!biometricService.canCheckBiometrics() && lockType == "BIOMETRIC") {
            viewModel.setLockType("PIN")
        }
    }
    
    var isChangingPasscode by remember { mutableStateOf(false) }
    var changeStep by remember { mutableStateOf(1) }
    var oldPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var changeError by remember { mutableStateOf("") }

    BackHandler {
        if (isChangingPasscode) {
            isChangingPasscode = false
            changeStep = 1
            oldPinInput = ""
            newPinInput = ""
            confirmPinInput = ""
            changeError = ""
        } else {
            onBack()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyDeep)
                    .statusBarsPadding()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (isChangingPasscode) {
                            isChangingPasscode = false
                            changeStep = 1
                            oldPinInput = ""
                            newPinInput = ""
                            confirmPinInput = ""
                            changeError = ""
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .background(NavyCard, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, // سيتجه بشكل صحيح في RTL
                        contentDescription = "رجوع",
                        tint = GoldYellow
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if (isChangingPasscode) "تغيير القفل السري" else "إعدادات الأمان وقفل التطبيقات",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isChangingPasscode) {
                // شاشة تغيير كلمة المرور والـ PIN
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (changeStep) {
                            1 -> "أدخل رمز الـ PIN القديم للتحقق"
                            2 -> "تعيين رمز PIN السري الجديد"
                            else -> "تأكيد رمز PIN السري الجديد"
                        },
                        color = GoldYellow,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val currentPinInput = when (changeStep) {
                        1 -> oldPinInput
                        2 -> newPinInput
                        else -> confirmPinInput
                    }

                    PinDisplayWidget(pin = currentPinInput, expectedLength = 4)
                    
                    if (changeError.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = changeError, color = ErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    CustomNumberKeyboard(
                        onDigitClick = { digit ->
                            when (changeStep) {
                                1 -> {
                                    if (oldPinInput.length < 4) {
                                        oldPinInput += digit
                                        if (oldPinInput.length == 4) {
                                            // التحقق من صحة القديم
                                            val preferences = AppLockPreferences(context)
                                            if (preferences.getPasscode() == oldPinInput) {
                                                changeStep = 2
                                                changeError = ""
                                            } else {
                                                oldPinInput = ""
                                                changeError = "رمز الـ PIN القديم خاطئ، يرجى المحاولة مرة أخرى."
                                            }
                                        }
                                    }
                                }
                                2 -> {
                                    if (newPinInput.length < 4) {
                                        newPinInput += digit
                                        if (newPinInput.length == 4) {
                                            changeStep = 3
                                            changeError = ""
                                        }
                                    }
                                }
                                3 -> {
                                    if (confirmPinInput.length < 4) {
                                        confirmPinInput += digit
                                        if (confirmPinInput.length == 4) {
                                            if (confirmPinInput == newPinInput) {
                                                // نجاح التغيير
                                                viewModel.changePasscode(newPinInput)
                                                Toast.makeText(context, "تم تغيير الرمز السري بنجاح ✓", Toast.LENGTH_SHORT).show()
                                                isChangingPasscode = false
                                                changeStep = 1
                                                oldPinInput = ""
                                                newPinInput = ""
                                                confirmPinInput = ""
                                                changeError = ""
                                            } else {
                                                confirmPinInput = ""
                                                changeError = "رمز الـ PIN الجديد غير متطابق، أعد الإدخال."
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        onBackspace = {
                            when (changeStep) {
                                1 -> if (oldPinInput.isNotEmpty()) oldPinInput = oldPinInput.dropLast(1)
                                2 -> if (newPinInput.isNotEmpty()) newPinInput = newPinInput.dropLast(1)
                                3 -> if (confirmPinInput.isNotEmpty()) confirmPinInput = confirmPinInput.dropLast(1)
                            }
                        }
                    )
                }
            } else {
                // شاشة الخيارات العادية
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 1. تغيير نوع القفل
                    SettingsSectionHeader(title = "نوع القفل وحماية البيانات")
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        lockTypes.forEach { type ->
                            val isSelected = lockType == type
                            val typeLabel = when (type) {
                                "PIN" -> "رمز PIN"
                                "PATTERN" -> "النمط"
                                else -> "البصمة"
                            }
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSelected) GoldYellow else NavyCard, RoundedCornerShape(12.dp))
                                    .border(1.dp, if (isSelected) GoldYellow else Color(0xFF334155), RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (type == "BIOMETRIC") {
                                            if (biometricService.canCheckBiometrics()) {
                                                viewModel.setLockType(type)
                                            } else {
                                                Toast.makeText(context, "هاتفك لا يحتوي على بصمة مفعلة حالياً أو مستشعر متوافق", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            viewModel.setLockType(type)
                                        }
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = typeLabel,
                                    color = if (isSelected) NavyDeep else TextLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // 2. خيارات تغيير الرقم السري
                    Card(
                        onClick = { isChangingPasscode = true },
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("change_pin_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("تغيير القفل السري الحالي", color = TextLight, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("تعديل الرمز الخاص بك لمنع الآخرين من فتحه", color = TextGray, fontSize = 11.sp)
                            }
                            Icon(Icons.Default.ArrowBack, contentDescription = "تغيير", tint = GoldYellow)
                        }
                    }

                    // 3. خيارات لوحة المفاتيح العشوائية
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("لوحة مفاتيح عشوائية للأرقام", color = TextLight, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("إعادة ترتيب الأرقام عشوائياً عند إدخال الـ PIN لتجنب تجسس الآخرين على حركة إصبعك.", color = TextGray, fontSize = 11.sp)
                            }
                            Switch(
                                checked = isRandomKeyboardEnabled,
                                onCheckedChange = { viewModel.setRandomKeyboardEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NavyDeep,
                                    checkedTrackColor = GoldYellow,
                                    uncheckedThumbColor = TextGray,
                                    uncheckedTrackColor = Color(0xFF334155)
                                ),
                                modifier = Modifier.testTag("random_keyboard_switch")
                            )
                        }
                    }

                    // ميزات الأمان المتقدمة والتخصيص
                    SettingsSectionHeader(title = "الأمان المتقدم والتخصيص")

                    // كارد الحماية من إلغاء التثبيت
                    Card(
                        onClick = onNavigateToAntiUninstall,
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("anti_uninstall_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("الحماية من إلغاء التثبيت 🛡️", color = TextLight, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("منع حذف التطبيق أو تعطيل صلاحياته من قبل المتطفلين", color = TextGray, fontSize = 11.sp)
                            }
                            Icon(Icons.Default.ArrowBack, contentDescription = "انتقال", tint = GoldYellow)
                        }
                    }

                    // كارد تخصيص الثيمات
                    Card(
                        onClick = onNavigateToThemePicker,
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("theme_picker_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("تخصيص النُسُق والألوان 🎨", color = TextLight, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("اختر شكل شاشة القفل والألوان المفضلة لديك", color = TextGray, fontSize = 11.sp)
                            }
                            Icon(Icons.Default.ArrowBack, contentDescription = "انتقال", tint = GoldYellow)
                        }
                    }

                    // كارد سيلفي الدخيل
                    Card(
                        onClick = onNavigateToIntruderSelfie,
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("intruder_selfie_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("سيلفي الدخيل 📸", color = TextLight, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("التقاط صورة واضحة للشخص عند إدخال قفل خاطئ وتتبع سجل المحاولات", color = TextGray, fontSize = 11.sp)
                            }
                            Icon(Icons.Default.ArrowBack, contentDescription = "انتقال", tint = GoldYellow)
                        }
                    }

                    // 4. خيارات إعادة القفل
                    SettingsSectionHeader(title = "وقت إعادة قفل التطبيقات بعد المغادرة")

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val timeouts = listOf(
                            Pair(0L, "فوراً عند إغلاق الشاشة أو مغادرة التطبيق"),
                            Pair(60000L, "بعد دقيقة واحدة من المغادرة"),
                            Pair(300000L, "بعد 5 دقائق من المغادرة")
                        )

                        timeouts.forEach { (timeMs, label) ->
                            val isSelected = lockTimeoutMs == timeMs
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NavyCard, RoundedCornerShape(12.dp))
                                    .border(1.dp, if (isSelected) GoldYellow else Color(0xFF334155), RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setLockTimeoutMs(timeMs) }
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = label, color = TextLight, fontSize = 13.sp)
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.setLockTimeoutMs(timeMs) },
                                    colors = RadioButtonDefaults.colors(selectedColor = GoldYellow)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 5. زر مسح البيانات وتصفير التطبيق
                    OutlinedButton(
                        onClick = {
                            viewModel.resetApp()
                            Toast.makeText(context, "تم مسح كافة البيانات وتصفير التطبيق بنجاح ✓", Toast.LENGTH_LONG).show()
                            onBack()
                        },
                        border = BorderStroke(1.dp, ErrorRed),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("reset_app_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "تصفير", tint = ErrorRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("مسح الإعدادات وتصفير التطبيق بالكامل", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = GoldYellow,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}

@Composable
fun AntiUninstallScreen(
    viewModel: AppLockViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isEnabled by viewModel.isAntiUninstallEnabled.collectAsState()
    val isDeviceAdminActive = viewModel.checkDeviceAdminActive(context)

    var showPasswordPrompt by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf("") }

    val adminLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        val active = viewModel.checkDeviceAdminActive(context)
        viewModel.setAntiUninstallEnabled(active, context)
    }

    BackHandler {
        if (showPasswordPrompt) {
            showPasswordPrompt = false
            pinInput = ""
            pinError = ""
        } else {
            onBack()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyDeep)
                    .statusBarsPadding()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (showPasswordPrompt) {
                            showPasswordPrompt = false
                            pinInput = ""
                            pinError = ""
                        } else {
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .background(NavyCard, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "رجوع",
                        tint = GoldYellow
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "الحماية من إلغاء التثبيت 🛡️",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (showPasswordPrompt) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "أدخل رمز الـ PIN الحالي لتأكيد التعطيل",
                        color = GoldYellow,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    PinDisplayWidget(pin = pinInput, expectedLength = 4)

                    if (pinError.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = pinError, color = ErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    CustomNumberKeyboard(
                        onDigitClick = { digit ->
                            if (pinInput.length < 4) {
                                pinInput += digit
                                if (pinInput.length == 4) {
                                    val preferences = AppLockPreferences(context)
                                    if (preferences.getPasscode() == pinInput) {
                                        viewModel.setAntiUninstallEnabled(false, context)
                                        showPasswordPrompt = false
                                        pinInput = ""
                                        pinError = ""
                                        Toast.makeText(context, "تم إلغاء تفعيل حماية إلغاء التثبيت بنجاح ✓", Toast.LENGTH_SHORT).show()
                                    } else {
                                        pinInput = ""
                                        pinError = "رمز الـ PIN الذي أدخلته غير صحيح."
                                    }
                                }
                            }
                        },
                        onBackspace = {
                            if (pinInput.isNotEmpty()) pinInput = pinInput.dropLast(1)
                        }
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(GoldYellow.copy(alpha = 0.15f), CircleShape)
                                    .align(Alignment.CenterHorizontally),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Shield",
                                    tint = GoldYellow,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "كيف تعمل الميزة؟",
                                color = TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "تستخدم هذه الميزة صلاحية 'مدير الجهاز' (Device Administrator) لمنع النظام من السماح بحذف التطبيق أو تعطيله بواسطة المتطفلين دون إدخال كلمة المرور الصحيحة أولاً.",
                                color = TextGray,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                textAlign = TextAlign.Justify
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لماذا نحتاج هذه الصلاحية؟",
                                color = GoldYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "نحتاج هذه الصلاحية فقط وحصرياً لمنع إلغاء تثبيت التطبيق. لن يقوم التطبيق بالوصول إلى أي بيانات شخصية أو تعديل إعدادات أخرى في جهازك مطلقاً.",
                                color = TextGray,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "الحماية من إلغاء التثبيت",
                                    color = TextLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (isDeviceAdminActive) "مفعّل ونشط حالياً ✓" else "غير مفعّل حالياً ⚠️",
                                    color = if (isDeviceAdminActive) AccentTeal else ErrorRed,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Switch(
                                checked = isDeviceAdminActive,
                                onCheckedChange = { checkState ->
                                    if (!checkState) {
                                        showPasswordPrompt = true
                                    } else {
                                        val componentName = ComponentName(context, com.example.service.AppLockDeviceAdminReceiver::class.java)
                                        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                                            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                                            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "قم بتنشيط ميزة حماية التطبيق من إلغاء التثبيت لحماية صورك وملفاتك.")
                                        }
                                        adminLauncher.launch(intent)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NavyDeep,
                                    checkedTrackColor = GoldYellow,
                                    uncheckedThumbColor = TextGray,
                                    uncheckedTrackColor = Color(0xFF334155)
                                )
                            )
                        }
                    }

                    Text(
                        text = "• هذه الصلاحية متوفرة وحساسة في نظام Android فقط، ولا تتوفر لنظام iOS نظراً للقيود المفروضة على الأنظمة هناك.",
                        color = TextGray.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ThemePickerScreen(
    viewModel: AppLockViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentThemeId by viewModel.selectedThemeId.collectAsState()
    var previewThemeId by remember { mutableStateOf(currentThemeId) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyDeep)
                    .statusBarsPadding()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(NavyCard, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "رجوع",
                        tint = GoldYellow
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "تخصيص النُسُق والألوان 🎨",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextLight
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
            Text(
                text = "اختر نسق شاشة القفل والألوان للتطبيق:",
                color = TextLight,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(AppThemes.size) { index ->
                    val theme = AppThemes[index]
                    val isSelected = theme.id == previewThemeId
                    val isActive = theme.id == currentThemeId

                    Card(
                        onClick = {
                            previewThemeId = theme.id
                            viewModel.setSelectedThemeId(theme.id)
                        },
                        colors = CardDefaults.cardColors(containerColor = theme.background),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) theme.primary else theme.surface.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(theme.surface, RoundedCornerShape(8.dp))
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = theme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        repeat(4) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(theme.primary, CircleShape)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = theme.name,
                                color = theme.onBackground,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            if (isActive) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "النسق النشط ✓",
                                    color = theme.primary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.setSelectedThemeId(previewThemeId)
                    Toast.makeText(context, "تم تطبيق وحفظ النسق بنجاح ✓", Toast.LENGTH_SHORT).show()
                    onBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "حفظ وتأكيد الاختيار",
                    color = NavyDeep,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun IntruderSelfieScreen(
    viewModel: AppLockViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isEnabled by viewModel.isIntruderSelfieEnabled.collectAsState()
    val records by viewModel.intruderRecords.collectAsState()
    
    val localGreen = Color(0xFF10B981)
    val localRed = Color(0xFFEF4444)

    var showPermissionExplanation by remember { mutableStateOf(false) }
    var showDeleteAllConfirmation by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<IntruderRecord?>(null) }
    var fullScreenImageRecord by remember { mutableStateOf<IntruderRecord?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.setIntruderSelfieEnabled(true)
            Toast.makeText(context, "تم تفعيل ميزة سيلفي الدخيل بنجاح ✓", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.setIntruderSelfieEnabled(false)
            Toast.makeText(context, "لم يتم تفعيل الميزة لعدم الموافقة على صلاحية الكاميرا.", Toast.LENGTH_LONG).show()
        }
    }

    // Check actual camera permission status
    val hasCameraPermission = remember(isEnabled) {
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    // Auto-disable if permission was revoked in settings but enabled in preferences
    LaunchedEffect(hasCameraPermission) {
        if (!hasCameraPermission && isEnabled) {
            viewModel.setIntruderSelfieEnabled(false)
        }
    }

    Scaffold(
        containerColor = NavyDeep,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyDeep)
                    .statusBarsPadding()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(NavyCard, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "العودة",
                        tint = GoldYellow,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "سيلفي الدخيل 📸",
                    color = TextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. كارت شرح الميزة
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "معلومات",
                                tint = GoldYellow,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "كيف تعمل الميزة؟",
                                color = TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "يقوم التطبيق بالتقاط صورة صامتة وتلقائية باستخدام الكاميرا الأمامية فور قيام أي متطفل بإدخال رمز PIN خاطئ، أو رسم نمط غير صحيح، أو فشل التحقق ببصمة الإصبع. يتم حفظ الصور محلياً وبشكل آمن تماماً للرجوع إليها ومعرفة هوية الدخيل والمستهدف.",
                            color = TextGray,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // 2. كارت تشغيل الميزة والتحكم بالصلاحيات
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Camera,
                                    contentDescription = "كاميرا",
                                    tint = if (isEnabled) GoldYellow else TextGray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "التقاط سيلفي الدخيل",
                                        color = TextLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = if (isEnabled) "الميزة نشطة وحامية لجهازك" else "الميزة معطلة حالياً",
                                        color = if (isEnabled) localGreen else TextGray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Switch(
                                checked = isEnabled,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        if (hasCameraPermission) {
                                            viewModel.setIntruderSelfieEnabled(true)
                                            Toast.makeText(context, "تم تفعيل سيلفي الدخيل بنجاح ✓", Toast.LENGTH_SHORT).show()
                                        } else {
                                            showPermissionExplanation = true
                                        }
                                    } else {
                                        viewModel.setIntruderSelfieEnabled(false)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = GoldYellow,
                                    checkedTrackColor = GoldYellow.copy(alpha = 0.5f),
                                    uncheckedThumbColor = TextGray,
                                    uncheckedTrackColor = NavyDeep
                                )
                            )
                        }

                        if (!hasCameraPermission && !isEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(localRed.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "تحذير",
                                    tint = localRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "يرجى العلم بأنه تم تعطيل الميزة لعدم توفر صلاحية الكاميرا. يمكنك تفعيل الميزة عن طريق منح صلاحية الكاميرا للتطبيق.",
                                    color = localRed,
                                    fontSize = 11.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // 3. قسم السجلات والصور الملتقطة
            if (isEnabled) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المحاولات المسجلة (${records.size})",
                            color = TextLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (records.isNotEmpty()) {
                            TextButton(
                                onClick = { showDeleteAllConfirmation = true },
                                colors = ButtonDefaults.textButtonColors(contentColor = localRed)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف الكل", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("حذف الكل", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                if (records.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = NavyCard),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Camera,
                                    contentDescription = "لا توجد صور",
                                    tint = TextGray.copy(alpha = 0.5f),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "لا توجد محاولات دخول خاطئة حالياً",
                                    color = TextLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "جهازك محمي وآمن تماماً، ولم يتم تسجيل أي محاولة فتح خاطئة للتطبيقات المقفلة.",
                                    color = TextGray,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                } else {
                    items(records) { record ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = NavyCard),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { fullScreenImageRecord = record }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // الصورة المصغرة
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.size(70.dp),
                                    border = BorderStroke(1.dp, GoldYellow.copy(alpha = 0.3f))
                                ) {
                                    val file = remember(record.imagePath) { File(record.imagePath) }
                                    if (file.exists()) {
                                        AsyncImage(
                                            model = file,
                                            contentDescription = "سيلفي الدخيل",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(NavyDeep),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = "صورة غير متوفرة",
                                                tint = localRed
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // بيانات المحاولة
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "محاولة فتح: ${record.targetAppName}",
                                        color = TextLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = java.text.SimpleDateFormat("yyyy/MM/dd - hh:mm a", java.util.Locale("ar")).format(java.util.Date(record.timestamp)),
                                        color = TextGray,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val lockTypeLabel = when (record.failedLockType) {
                                        "PIN" -> "رمز PIN"
                                        "PATTERN" -> "النمط"
                                        "BIOMETRIC" -> "بصمة الإصبع"
                                        else -> record.failedLockType
                                    }
                                    Text(
                                        text = "نوع الحماية الفاشلة: $lockTypeLabel",
                                        color = GoldYellow,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                // زر الحذف الفردي
                                IconButton(onClick = { recordToDelete = record }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف",
                                        tint = localRed.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavyCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "ميزة غير مفعلة",
                                tint = GoldYellow,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "قم بتفعيل الميزة لعرض السجلات",
                                color = TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "بعد التفعيل، سيتم تسجيل وعرض أي محاولات دخول خاطئة هنا بالتفصيل والصور.",
                                color = TextGray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // 1. شاشة شرح صلاحية الكاميرا (Explanation Dialog)
    if (showPermissionExplanation) {
        AlertDialog(
            onDismissRequest = { showPermissionExplanation = false },
            title = {
                Text(
                    text = "مطلوب صلاحية الكاميرا 📸",
                    color = TextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "نحتاج إذن الكاميرا لالتقاط صورة لأي شخص يحاول فتح تطبيقاتك المقفلة بدون إذن لمساعدتك في تتبع المتطفلين وحماية خصوصيتك.",
                    color = TextGray,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionExplanation = false
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldYellow)
                ) {
                    Text("منح الإذن", color = NavyDeep, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionExplanation = false }) {
                    Text("إلغاء", color = TextGray)
                }
            },
            containerColor = NavyCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 2. تأكيد حذف محاولة فردية
    recordToDelete?.let { record ->
        AlertDialog(
            onDismissRequest = { recordToDelete = null },
            title = {
                Text(
                    text = "حذف السجل؟",
                    color = TextLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "هل تريد حذف هذه الصورة المسجلة ومحاولة الدخول الخاصة بـ ${record.targetAppName} نهائياً؟",
                    color = TextGray,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteIntruderRecord(record)
                        recordToDelete = null
                        Toast.makeText(context, "تم حذف السجل بنجاح ✓", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = localRed)
                ) {
                    Text("حذف", color = TextLight, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordToDelete = null }) {
                    Text("إلغاء", color = TextGray)
                }
            },
            containerColor = NavyCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 3. تأكيد حذف كافة المحاولات
    if (showDeleteAllConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteAllConfirmation = false },
            title = {
                Text(
                    text = "حذف جميع السجلات؟ ⚠️",
                    color = localRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "هل أنت متأكد من رغبتك في حذف جميع الصور والتقارير نهائياً؟ لا يمكن التراجع عن هذا الإجراء بعد تنفيذه.",
                    color = TextGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAllIntruderRecords()
                        showDeleteAllConfirmation = false
                        Toast.makeText(context, "تم حذف كافة السجلات بنجاح ✓", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = localRed)
                ) {
                    Text("نعم، حذف الكل", color = TextLight, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllConfirmation = false }) {
                    Text("تراجع", color = TextGray)
                }
            },
            containerColor = NavyCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 4. عرض الصورة بالحجم الكامل (Fullscreen Dialog Preview)
    fullScreenImageRecord?.let { record ->
        Dialog(
            onDismissRequest = { fullScreenImageRecord = null }
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDeep),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سيلفي الدخيل",
                            color = TextLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = { fullScreenImageRecord = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = GoldYellow
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        border = BorderStroke(1.dp, GoldYellow)
                    ) {
                        val file = remember(record.imagePath) { File(record.imagePath) }
                        if (file.exists()) {
                            AsyncImage(
                                model = file,
                                contentDescription = "سيلفي الدخيل بالحجم الكامل",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(NavyCard),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("عذراً، الصورة غير موجودة على الجهاز", color = localRed, fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "تم الالتقاط أثناء محاولة فتح: ${record.targetAppName}",
                        color = TextLight,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale("ar")).format(Date(record.timestamp)),
                        color = TextGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

