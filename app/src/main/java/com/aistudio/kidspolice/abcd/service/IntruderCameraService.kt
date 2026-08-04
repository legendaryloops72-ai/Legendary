package com.aistudio.kidspolice.abcd.service

import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.aistudio.kidspolice.abcd.data.AppDatabase
import com.aistudio.kidspolice.abcd.data.IntruderRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IntruderCameraService(private val context: Context) {
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    fun initialize(lifecycleOwner: LifecycleOwner, surfaceProvider: Preview.SurfaceProvider? = null) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                
                // Select front camera
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build()

                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                val preview = Preview.Builder().build()
                if (surfaceProvider != null) {
                    preview.setSurfaceProvider(surfaceProvider)
                }

                // Unbind all use cases before rebinding
                cameraProvider?.unbindAll()

                // Bind use cases to lifecycle
                if (surfaceProvider != null) {
                    cameraProvider?.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } else {
                    cameraProvider?.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        imageCapture
                    )
                }
                Log.d("IntruderCameraService", "CameraX initialized and bound to lifecycle successfully.")
            } catch (e: Exception) {
                Log.e("IntruderCameraService", "Failed to bind CameraX use cases", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun release() {
        try {
            cameraProvider?.unbindAll()
        } catch (e: Exception) {
            Log.e("IntruderCameraService", "Failed to unbind cameraProvider", e)
        }
        imageCapture = null
    }

    fun captureIntruderPhoto(targetPackageName: String, failedLockType: String, onComplete: () -> Unit = {}) {
        val capture = imageCapture
        if (capture == null) {
            Log.e("IntruderCameraService", "Cannot capture: imageCapture is null")
            onComplete()
            return
        }
        
        // Ensure intruders directory exists
        val intrudersDir = File(context.filesDir, "intruders")
        if (!intrudersDir.exists()) {
            intrudersDir.mkdirs()
        }

        val timestampStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val photoFile = File(intrudersDir, "intruder_$timestampStr.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d("IntruderCameraService", "Photo saved: ${photoFile.absolutePath}")
                    
                    // Insert record in DB
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val db = androidx.room.Room.databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                "app_lock_secure_db"
                            )
                            .fallbackToDestructiveMigration()
                            .build()
                            
                            val appLabel = try {
                                val pm = context.packageManager
                                val info = pm.getApplicationInfo(targetPackageName, 0)
                                pm.getApplicationLabel(info).toString()
                            } catch (e: Exception) {
                                targetPackageName
                            }

                            val record = IntruderRecord(
                                imagePath = photoFile.absolutePath,
                                timestamp = System.currentTimeMillis(),
                                targetAppName = appLabel,
                                failedLockType = failedLockType
                            )
                            
                            db.appDao().insertIntruderRecord(record)
                            db.close()
                            Log.d("IntruderCameraService", "Saved IntruderRecord into database.")
                        } catch (dbEx: Exception) {
                            Log.e("IntruderCameraService", "DB insert failed", dbEx)
                        }
                        
                        Handler(Looper.getMainLooper()).post {
                            onComplete()
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("IntruderCameraService", "Photo capture failed: ${exception.message}", exception)
                    onComplete()
                }
            }
        )
    }
}
