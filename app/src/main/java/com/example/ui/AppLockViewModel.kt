package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppInfo
import com.example.data.AppLockPreferences
import com.example.data.AppDatabase
import com.example.data.VaultMediaItem
import com.example.data.IntruderRecord
import androidx.room.Room
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import android.provider.OpenableColumns
import android.content.ContentValues
import android.provider.MediaStore
import android.media.MediaMetadataRetriever
import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class AppLockViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val preferences = AppLockPreferences(context)
    private val pm: PackageManager = context.packageManager

    // تهيئة قاعدة البيانات للخزانة الآمنة
    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_lock_secure_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    private val appDao by lazy { database.appDao() }

    // حالة ملفات الخزانة الآمنة
    private val _vaultItems = MutableStateFlow<List<VaultMediaItem>>(emptyList())
    val vaultItems: StateFlow<List<VaultMediaItem>> = _vaultItems.asStateFlow()

    // حالة استخدام نفس كلمة المرور
    private val _useSamePasscodeForVault = MutableStateFlow(preferences.useSamePasscodeForVault)
    val useSamePasscodeForVault: StateFlow<Boolean> = _useSamePasscodeForVault.asStateFlow()

    // سيلفي الدخيل
    private val _intruderRecords = MutableStateFlow<List<IntruderRecord>>(emptyList())
    val intruderRecords: StateFlow<List<IntruderRecord>> = _intruderRecords.asStateFlow()

    private val _isIntruderSelfieEnabled = MutableStateFlow(preferences.isIntruderSelfieEnabled)
    val isIntruderSelfieEnabled: StateFlow<Boolean> = _isIntruderSelfieEnabled.asStateFlow()

    // حالات واجهة المستخدم
    private val _isSetupComplete = MutableStateFlow(preferences.isSetupComplete)
    val isSetupComplete: StateFlow<Boolean> = _isSetupComplete.asStateFlow()

    private val _lockType = MutableStateFlow(preferences.lockType)
    val lockType: StateFlow<String> = _lockType.asStateFlow()

    private val _isRandomKeyboardEnabled = MutableStateFlow(preferences.isRandomKeyboardEnabled)
    val isRandomKeyboardEnabled: StateFlow<Boolean> = _isRandomKeyboardEnabled.asStateFlow()

    private val _lockTimeoutMs = MutableStateFlow(preferences.lockTimeoutMs)
    val lockTimeoutMs: StateFlow<Long> = _lockTimeoutMs.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true) // داكن افتراضياً كما طلب المستخدم
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // حالة الحماية من إلغاء التثبيت
    private val _isAntiUninstallEnabled = MutableStateFlow(preferences.isAntiUninstallEnabled)
    val isAntiUninstallEnabled: StateFlow<Boolean> = _isAntiUninstallEnabled.asStateFlow()

    // النسق المختار
    private val _selectedThemeId = MutableStateFlow(preferences.selectedThemeId)
    val selectedThemeId: StateFlow<String> = _selectedThemeId.asStateFlow()

    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val filteredApps: StateFlow<List<AppInfo>> = _filteredApps.asStateFlow()

    private val _isLoadingApps = MutableStateFlow(false)
    val isLoadingApps: StateFlow<Boolean> = _isLoadingApps.asStateFlow()

    // منطق المحاولات الخاطئة
    private val _wrongAttempts = MutableStateFlow(0)
    val wrongAttempts: StateFlow<Int> = _wrongAttempts.asStateFlow()

    private val _lockoutTimeRemaining = MutableStateFlow(0)
    val lockoutTimeRemaining: StateFlow<Int> = _lockoutTimeRemaining.asStateFlow()

    private var lockoutJob: Job? = null

    init {
        loadInstalledApps()
        // مراقبة وحفظ ملفات الخزانة الآمنة
        viewModelScope.launch(Dispatchers.IO) {
            appDao.getAllVaultItems().collect { items ->
                _vaultItems.value = items
            }
        }
        // مراقبة سجلات سيلفي الدخيل
        viewModelScope.launch(Dispatchers.IO) {
            appDao.getAllIntruderRecords().collect { records ->
                _intruderRecords.value = records
            }
        }
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // جلب التطبيقات المثبتة التي تحتوي على واجهة تشغيل
    fun loadInstalledApps() {
        _isLoadingApps.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lockedSet = preferences.lockedApps
                val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                
                // جلب التطبيقات التي تظهر للمستخدم فقط وتجنب حزم الخلفية
                val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
                val appsList = mutableListOf<AppInfo>()
                val uniquePackages = mutableSetOf<String>()

                for (resolveInfo in resolveInfos) {
                    val packageName = resolveInfo.activityInfo.packageName
                    if (uniquePackages.add(packageName)) {
                        val label = resolveInfo.loadLabel(pm).toString()
                        val appInfo = resolveInfo.activityInfo.applicationInfo
                        val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                        val isLocked = lockedSet.contains(packageName)
                        
                        appsList.add(AppInfo(packageName, label, isSystem, isLocked))
                    }
                }

                // ترتيب أبجدي
                val sortedList = appsList.sortedBy { it.label.lowercase(Locale.ROOT) }
                _allApps.value = sortedList
                filterApps(_searchQuery.value)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingApps.value = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterApps(query)
    }

    private fun filterApps(query: String) {
        val list = _allApps.value
        if (query.isEmpty()) {
            _filteredApps.value = list
        } else {
            _filteredApps.value = list.filter {
                it.label.contains(query, ignoreCase = true) || it.packageName.contains(query, ignoreCase = true)
            }
        }
    }

    // تفعيل أو تعطيل القفل لتطبيق معين
    fun toggleAppLock(packageName: String) {
        val currentLocked = preferences.lockedApps.toMutableSet()
        val isLockedNow = currentLocked.contains(packageName)
        
        if (isLockedNow) {
            currentLocked.remove(packageName)
            preferences.unlockApp(packageName)
        } else {
            currentLocked.add(packageName)
            preferences.lockApp(packageName)
        }

        // تحديث القائمة فوراً في واجهة المستخدم
        _allApps.value = _allApps.value.map {
            if (it.packageName == packageName) {
                it.copy(isLocked = !isLockedNow)
            } else {
                it
            }
        }
        filterApps(_searchQuery.value)
    }

    // إتمام الإعداد الأول لقفل التطبيق
    fun completeSetup(chosenType: String, passcodeValue: String) {
        preferences.lockType = chosenType
        preferences.savePasscode(passcodeValue)
        preferences.isSetupComplete = true
        
        _lockType.value = chosenType
        _isSetupComplete.value = true
    }

    // التحقق من صحة الرقم السري أو النمط المدخل
    fun verifyPasscode(inputPasscode: String): Boolean {
        if (_lockoutTimeRemaining.value > 0) {
            return false
        }

        val saved = preferences.getPasscode()
        val isCorrect = saved == inputPasscode

        if (isCorrect) {
            _wrongAttempts.value = 0
            return true
        } else {
            val newAttempts = _wrongAttempts.value + 1
            _wrongAttempts.value = newAttempts
            if (newAttempts >= 5) {
                startLockoutTimer()
            }
            return false
        }
    }

    // بدء عداد الـ 30 ثانية عند تجاوز المحاولات الخاطئة
    private fun startLockoutTimer() {
        lockoutJob?.cancel()
        _lockoutTimeRemaining.value = 30
        lockoutJob = viewModelScope.launch {
            while (_lockoutTimeRemaining.value > 0) {
                delay(1000)
                _lockoutTimeRemaining.value -= 1
            }
            _wrongAttempts.value = 0
        }
    }

    // إعدادات لوحة المفاتيح والمهلة ونوع القفل
    fun setLockType(type: String) {
        preferences.lockType = type
        _lockType.value = type
    }

    fun setRandomKeyboardEnabled(enabled: Boolean) {
        preferences.isRandomKeyboardEnabled = enabled
        _isRandomKeyboardEnabled.value = enabled
    }

    fun setLockTimeoutMs(timeoutMs: Long) {
        preferences.lockTimeoutMs = timeoutMs
        _lockTimeoutMs.value = timeoutMs
    }

    fun changePasscode(newPasscode: String) {
        preferences.savePasscode(newPasscode)
    }

    fun setIntruderSelfieEnabled(enabled: Boolean) {
        preferences.isIntruderSelfieEnabled = enabled
        _isIntruderSelfieEnabled.value = enabled
    }

    fun deleteIntruderRecord(record: IntruderRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = java.io.File(record.imagePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            appDao.deleteIntruderRecord(record)
        }
    }

    fun deleteAllIntruderRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val intrudersDir = java.io.File(context.filesDir, "intruders")
                if (intrudersDir.exists()) {
                    intrudersDir.listFiles()?.forEach { file ->
                        file.delete()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            appDao.deleteAllIntruderRecords()
        }
    }

    // التحقق من تفعيل صلاحية الظهور فوق التطبيقات ومراقبة الاستخدام
    fun hasOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun hasUsageAccessPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            appOps.checkOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }

    fun setUseSamePasscodeForVault(enabled: Boolean) {
        preferences.useSamePasscodeForVault = enabled
        _useSamePasscodeForVault.value = enabled
    }

    fun saveVaultPasscode(passcode: String) {
        preferences.saveVaultPasscode(passcode)
    }

    fun getVaultPasscode(): String {
        return preferences.getVaultPasscode()
    }

    fun getAppPasscode(): String {
        return preferences.getPasscode()
    }

    // استيراد صورة أو فيديو إلى الخزانة الآمنة
    fun importMedia(
        uris: List<Uri>,
        isVideo: Boolean,
        context: Context,
        onComplete: (successCount: Int) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var successCount = 0
            val vaultDir = File(context.filesDir, "vault")
            if (!vaultDir.exists()) vaultDir.mkdirs()

            // إنشاء ملف .nomedia لمنع الصور من الظهور في المعارض الأخرى
            val nomedia = File(vaultDir, ".nomedia")
            if (!nomedia.exists()) {
                try {
                    nomedia.createNewFile()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            for (uri in uris) {
                try {
                    val originalName = getFileName(context, uri) ?: "file_${System.currentTimeMillis()}"
                    val extension = if (isVideo) "mp4" else "jpg"
                    val uniqueName = "vault_${System.currentTimeMillis()}_${(100..999).random()}.$extension"
                    val destFile = File(vaultDir, uniqueName)

                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        FileOutputStream(destFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    if (destFile.exists() && destFile.length() > 0) {
                        val newItem = VaultMediaItem(
                            name = originalName,
                            localPath = destFile.absolutePath,
                            originalPath = uri.toString(),
                            isVideo = isVideo
                        )
                        appDao.insertVaultItem(newItem)
                        successCount++

                        // محاولة حذف الملف الأصلي من المعرض العام
                        try {
                            context.contentResolver.delete(uri, null, null)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            // في أندرويد الحديث، قد يتطلب الحذف موافقة مسبقة أو يفشل لعدم توفر الصلاحيات، نتجاهله لضمان سلاسة التطبيق
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            onComplete(successCount)
        }
    }

    // حذف ملف نهائياً من الخزانة الآمنة وقاعدة البيانات
    fun deleteVaultMedia(
        items: List<VaultMediaItem>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            for (item in items) {
                try {
                    val file = File(item.localPath)
                    if (file.exists()) {
                        file.delete()
                    }
                    appDao.deleteVaultItem(item)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            onComplete()
        }
    }

    // استعادة الملفات إلى معرض الهاتف الأصلي
    fun restoreVaultMedia(
        items: List<VaultMediaItem>,
        context: Context,
        onComplete: (successCount: Int) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var successCount = 0
            for (item in items) {
                try {
                    val file = File(item.localPath)
                    if (file.exists()) {
                        val resolver = context.contentResolver
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, item.name)
                            put(MediaStore.MediaColumns.MIME_TYPE, if (item.isVideo) "video/mp4" else "image/jpeg")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                put(
                                    MediaStore.MediaColumns.RELATIVE_PATH,
                                    if (item.isVideo) "Movies/RestoredVault" else "Pictures/RestoredVault"
                                )
                            }
                        }

                        val externalUri = if (item.isVideo) {
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        } else {
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }

                        val insertedUri = resolver.insert(externalUri, contentValues)
                        if (insertedUri != null) {
                            resolver.openOutputStream(insertedUri)?.use { outputStream ->
                                file.inputStream().use { inputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                            }
                            
                            // بعد نجاح النسخ، نحذفه من الخزانة وقاعدة البيانات
                            file.delete()
                            appDao.deleteVaultItem(item)
                            successCount++
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            onComplete(successCount)
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var name: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        name = it.getString(index)
                    }
                }
            }
        }
        if (name == null) {
            name = uri.path
            val cut = name?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                name = name?.substring(cut + 1)
            }
        }
        return name
    }

    fun checkDeviceAdminActive(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
        val adminComponent = android.content.ComponentName(context, com.example.service.AppLockDeviceAdminReceiver::class.java)
        val active = dpm.isAdminActive(adminComponent)
        if (!active && preferences.isAntiUninstallEnabled) {
            preferences.isAntiUninstallEnabled = false
            _isAntiUninstallEnabled.value = false
        }
        return active
    }

    fun setAntiUninstallEnabled(enabled: Boolean, context: Context) {
        if (!enabled) {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
            val adminComponent = android.content.ComponentName(context, com.example.service.AppLockDeviceAdminReceiver::class.java)
            if (dpm.isAdminActive(adminComponent)) {
                dpm.removeActiveAdmin(adminComponent)
            }
            preferences.isAntiUninstallEnabled = false
            _isAntiUninstallEnabled.value = false
        } else {
            preferences.isAntiUninstallEnabled = true
            _isAntiUninstallEnabled.value = true
        }
    }

    fun setSelectedThemeId(themeId: String) {
        preferences.selectedThemeId = themeId
        _selectedThemeId.value = themeId
    }

    fun resetApp() {
        preferences.clearAll()
        _isSetupComplete.value = false
        _lockType.value = "NONE"
        _allApps.value = _allApps.value.map { it.copy(isLocked = false) }
        filterApps(_searchQuery.value)
    }
}
