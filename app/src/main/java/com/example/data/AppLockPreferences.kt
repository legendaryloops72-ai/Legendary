package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64

class AppLockPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_lock_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PASSCODE = "secure_passcode" // PIN or Pattern representation
        private const val KEY_LOCK_TYPE = "lock_type" // "PIN", "PATTERN", "BIOMETRIC", "NONE"
        private const val KEY_LOCKED_APPS = "locked_apps_packages" // Set of package names
        private const val KEY_LOCK_TIMEOUT = "lock_timeout_ms" // "0" (immediate), "60000" (1 min), "300000" (5 mins)
        private const val KEY_RANDOM_KEYBOARD = "random_keyboard_enabled"
        private const val KEY_IS_SETUP_COMPLETE = "is_setup_complete"
        private const val KEY_VAULT_PASSCODE = "secure_vault_passcode"
        private const val KEY_USE_SAME_PASSCODE_FOR_VAULT = "use_same_passcode_for_vault"
        private const val KEY_ANTI_UNINSTALL = "anti_uninstall_enabled"
        private const val KEY_SELECTED_THEME = "selected_theme_id"
        private const val KEY_INTRUDER_SELFIE = "intruder_selfie_enabled"
        
        // Secret key for simple XOR to mimic secure storage encryption safely
        private const val CRYPTO_SALT = 101
    }

    // تشفير وفك تشفير بسيط لمحاكاة flutter_secure_storage بشكل آمن وسهل
    private fun encrypt(value: String): String {
        val bytes = value.toByteArray(Charsets.UTF_8)
        val encrypted = ByteArray(bytes.size)
        for (i in bytes.indices) {
            encrypted[i] = (bytes[i].toInt() xor CRYPTO_SALT).toByte()
        }
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    private fun decrypt(encryptedValue: String): String {
        return try {
            val decoded = Base64.decode(encryptedValue, Base64.DEFAULT)
            val decrypted = ByteArray(decoded.size)
            for (i in decoded.indices) {
                decrypted[i] = (decoded[i].toInt() xor CRYPTO_SALT).toByte()
            }
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }

    // التحقق من اكتمال الإعداد الأول
    var isSetupComplete: Boolean
        get() = prefs.getBoolean(KEY_IS_SETUP_COMPLETE, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_SETUP_COMPLETE, value).apply()

    // حفظ كلمة المرور المشفرة (PIN أو النمط كمسار نصي)
    fun savePasscode(passcode: String) {
        prefs.edit().putString(KEY_PASSCODE, encrypt(passcode)).apply()
    }

    fun getPasscode(): String {
        val encrypted = prefs.getString(KEY_PASSCODE, "") ?: ""
        return if (encrypted.isNotEmpty()) decrypt(encrypted) else ""
    }

    // نوع القفل النشط
    var lockType: String
        get() = prefs.getString(KEY_LOCK_TYPE, "NONE") ?: "NONE"
        set(value) = prefs.edit().putString(KEY_LOCK_TYPE, value).apply()

    // قائمة حزم التطبيقات المقفلة
    var lockedApps: Set<String>
        get() = prefs.getStringSet(KEY_LOCKED_APPS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_LOCKED_APPS, value).apply()

    fun lockApp(packageName: String) {
        val current = lockedApps.toMutableSet()
        current.add(packageName)
        lockedApps = current
    }

    fun unlockApp(packageName: String) {
        val current = lockedApps.toMutableSet()
        current.remove(packageName)
        lockedApps = current
    }

    fun isAppLocked(packageName: String): Boolean {
        return lockedApps.contains(packageName)
    }

    // إعدادات إعادة القفل (بالملي ثانية)
    // 0 = فوراً، 60000 = بعد دقيقة، 300000 = بعد 5 دقائق
    var lockTimeoutMs: Long
        get() = prefs.getLong(KEY_LOCK_TIMEOUT, 0L)
        set(value) = prefs.edit().putLong(KEY_LOCK_TIMEOUT, value).apply()

    // تفعيل/تعطيل لوحة المفاتيح العشوائية
    var isRandomKeyboardEnabled: Boolean
        get() = prefs.getBoolean(KEY_RANDOM_KEYBOARD, false)
        set(value) = prefs.edit().putBoolean(KEY_RANDOM_KEYBOARD, value).apply()

    // إعدادات الخزانة الآمنة
    var useSamePasscodeForVault: Boolean
        get() = prefs.getBoolean(KEY_USE_SAME_PASSCODE_FOR_VAULT, true)
        set(value) = prefs.edit().putBoolean(KEY_USE_SAME_PASSCODE_FOR_VAULT, value).apply()

    fun saveVaultPasscode(passcode: String) {
        prefs.edit().putString(KEY_VAULT_PASSCODE, encrypt(passcode)).apply()
    }

    fun getVaultPasscode(): String {
        val encrypted = prefs.getString(KEY_VAULT_PASSCODE, "") ?: ""
        return if (encrypted.isNotEmpty()) decrypt(encrypted) else ""
    }

    // تفعيل/تعطيل الحماية من إلغاء التثبيت
    var isAntiUninstallEnabled: Boolean
        get() = prefs.getBoolean(KEY_ANTI_UNINSTALL, false)
        set(value) = prefs.edit().putBoolean(KEY_ANTI_UNINSTALL, value).apply()

    // معرف النسق المختار
    var selectedThemeId: String
        get() = prefs.getString(KEY_SELECTED_THEME, "navy_gold") ?: "navy_gold"
        set(value) = prefs.edit().putString(KEY_SELECTED_THEME, value).apply()

    // تفعيل/تعطيل سيلفي الدخيل
    var isIntruderSelfieEnabled: Boolean
        get() = prefs.getBoolean(KEY_INTRUDER_SELFIE, false)
        set(value) = prefs.edit().putBoolean(KEY_INTRUDER_SELFIE, value).apply()

    // مسح كافة الإعدادات لإعادة التعيين
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
