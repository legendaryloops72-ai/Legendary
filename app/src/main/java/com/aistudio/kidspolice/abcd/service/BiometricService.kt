package com.aistudio.kidspolice.abcd.service

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

enum class BiometricStatus {
    READY,
    NOT_ENROLLED,
    NOT_AVAILABLE,
    LOCKED_OUT
}

sealed class BiometricError {
    object NotAvailable : BiometricError()
    object NotEnrolled : BiometricError()
    object LockedOut : BiometricError()
    data class Other(val message: String) : BiometricError()
}

class BiometricService(private val context: Context) {

    fun getBiometricStatus(): BiometricStatus {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(Authenticators.BIOMETRIC_STRONG or Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.READY
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.NOT_AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricStatus.NOT_AVAILABLE
            else -> BiometricStatus.LOCKED_OUT
        }
    }

    fun canCheckBiometrics(): Boolean {
        return getBiometricStatus() == BiometricStatus.READY
    }

    fun authenticate(
        activity: FragmentActivity,
        reason: String = "قم بالمصادقة لفتح التطبيق",
        onSuccess: () -> Unit,
        onFailed: () -> Unit = {},
        onError: (BiometricError) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    activity.runOnUiThread {
                        onSuccess()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    activity.runOnUiThread {
                        onFailed()
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    activity.runOnUiThread {
                        val mappedError = when (errorCode) {
                            BiometricPrompt.ERROR_HW_NOT_PRESENT,
                            BiometricPrompt.ERROR_HW_UNAVAILABLE -> BiometricError.NotAvailable
                            BiometricPrompt.ERROR_NO_BIOMETRICS -> BiometricError.NotEnrolled
                            BiometricPrompt.ERROR_LOCKOUT,
                            BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> BiometricError.LockedOut
                            else -> BiometricError.Other(errString.toString())
                        }
                        onError(mappedError)
                    }
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("فك قفل التطبيق")
            .setSubtitle(reason)
            .setNegativeButtonText("استخدام الـ PIN كبديل")
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            onError(BiometricError.Other(e.message ?: "حدث خطأ أثناء المصادقة"))
        }
    }
}
