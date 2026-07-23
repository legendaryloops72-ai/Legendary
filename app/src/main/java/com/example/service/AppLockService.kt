package com.example.service

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.AppLockPreferences
import java.util.SortedMap
import java.util.TreeMap

class AppLockService : Service() {

    private lateinit var preferences: AppLockPreferences
    private val handler = Handler(Looper.getMainLooper())
    private var lastActiveApp: String? = null
    
    // قائمة لتتبع التطبيقات التي تم فتح قفلها مؤقتاً لتجنب تكرار القفل فوراً
    companion object {
        val temporarilyUnlockedApps = mutableMapOf<String, Long>()
    }

    private val monitorRunnable = object : Runnable {
        override fun run() {
            checkForegroundApp()
            // فحص كل ثانية لتوفير استهلاك البطارية ودقة القفل
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        preferences = AppLockPreferences(applicationContext)
        startNotification()
        handler.post(monitorRunnable)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(monitorRunnable)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startNotification() {
        val channelId = "app_lock_service_channel"
        val channelName = "خدمة قفل التطبيقات"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("حامي التطبيقات نشط")
            .setContentText("يقوم بمراقبة وحماية خصوصية تطبيقاتك بنجاح")
            .setSmallIcon(android.R.drawable.ic_secure)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(101, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(101, notification)
        }
    }

    private fun checkForegroundApp() {
        val currentApp = getForegroundPackageName() ?: return
        
        // لا نقفل تطبيقنا الخاص
        if (currentApp == packageName) {
            return
        }

        // إذا كان التطبيق مقفلاً في الإعدادات
        if (preferences.isAppLocked(currentApp)) {
            // التحقق من صلاحية فك القفل المؤقت بناء على إعدادات المهلة الزمنية
            val unlockTime = temporarilyUnlockedApps[currentApp]
            val timeout = preferences.lockTimeoutMs
            
            val isStillUnlocked = if (unlockTime != null) {
                if (timeout == 0L) {
                    // القفل فوراً عند الخروج (المغادرة تنهي القفل المؤقت)
                    false
                } else {
                    // التحقق من تجاوز المهلة المحددة (دقيقة، 5 دقائق)
                    System.currentTimeMillis() - unlockTime < timeout
                }
            } else {
                false
            }

            if (!isStillUnlocked && currentApp != lastActiveApp) {
                // إظهار شاشة القفل للتطبيق المطلوب
                launchLockOverlay(currentApp)
            }
        }
        
        lastActiveApp = currentApp
    }

    private fun getForegroundPackageName(): String? {
        var currentApp: String? = null
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        val appList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time)
        
        if (appList != null && appList.isNotEmpty()) {
            val sortedMap: SortedMap<Long, android.app.usage.UsageStats> = TreeMap()
            for (usageStats in appList) {
                sortedMap[usageStats.lastTimeUsed] = usageStats
            }
            if (!sortedMap.isEmpty()) {
                currentApp = sortedMap[sortedMap.lastKey()]?.packageName
            }
        } else {
            // كبديل للأجهزة القديمة أو في حالة عدم منح الصلاحية الكاملة حالياً
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasks = activityManager.getRunningTasks(1)
            if (tasks.isNotEmpty()) {
                currentApp = tasks[0].topActivity?.packageName
            }
        }
        return currentApp
    }

    private fun launchLockOverlay(targetPackage: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("is_lock_request", true)
            putExtra("target_package_name", targetPackage)
        }
        startActivity(intent)
    }
}
