package com.aistudio.kidspolice.abcd.toys

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Controller for optional, safe rear LED camera flash effect.
 * Disabled by default. Never opens a camera preview.
 * Strictly turns off flash on pause, screen exit, or error.
 */
class SafeTorchController(context: Context) {

    companion object {
        private const val TAG = "SafeTorchController"
        private const val MIN_INTERVAL_MS = 600L // Prevents rapid flashing (safety constraint)
    }

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private var cameraIdWithFlash: String? = null
    private var isTorchOn = false
    private var lastFlashTimestamp = 0L
    private var activeFlashJob: Job? = null

    init {
        detectFlashSupport()
    }

    private fun detectFlashSupport() {
        try {
            val manager = cameraManager ?: return
            for (id in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(id)
                val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraIdWithFlash = id
                    Log.d(TAG, "Rear camera with flash detected: $id")
                    break
                }
            }
            if (cameraIdWithFlash == null) {
                // Fallback to any camera with flash if rear not explicitly tagged
                for (id in (manager?.cameraIdList ?: emptyArray())) {
                    val hasFlash = manager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                    if (hasFlash) {
                        cameraIdWithFlash = id
                        break
                    }
                }
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Failed detecting flash support: ${e.message}")
            cameraIdWithFlash = null
        }
    }

    fun isFlashSupported(): Boolean = cameraIdWithFlash != null

    /**
     * Triggers a safe, ultra-brief flash pulse (e.g. 100ms) with mandatory cooldown.
     */
    fun triggerBriefFlash(scope: CoroutineScope, durationMs: Long = 100L) {
        val camId = cameraIdWithFlash ?: return
        val now = System.currentTimeMillis()
        if (now - lastFlashTimestamp < MIN_INTERVAL_MS) {
            Log.d(TAG, "Flash suppressed due to cooldown safety threshold.")
            return
        }
        lastFlashTimestamp = now

        activeFlashJob?.cancel()
        activeFlashJob = scope.launch(Dispatchers.Default) {
            try {
                cameraManager?.setTorchMode(camId, true)
                isTorchOn = true
                delay(durationMs)
            } catch (e: Throwable) {
                Log.w(TAG, "Exception setting torch mode on: ${e.message}")
            } finally {
                turnOffTorch()
            }
        }
    }

    fun turnOffTorch() {
        activeFlashJob?.cancel()
        activeFlashJob = null
        val camId = cameraIdWithFlash ?: return
        if (isTorchOn) {
            try {
                cameraManager?.setTorchMode(camId, false)
            } catch (e: Throwable) {
                Log.w(TAG, "Exception turning off torch mode: ${e.message}")
            } finally {
                isTorchOn = false
            }
        }
    }
}
