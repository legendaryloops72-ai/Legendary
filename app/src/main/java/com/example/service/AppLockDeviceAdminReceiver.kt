package com.example.service

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AppLockDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Toast.makeText(context, "تم تفعيل حماية إلغاء التثبيت بنجاح ✓", Toast.LENGTH_LONG).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Toast.makeText(context, "تم تعطيل حماية إلغاء التثبيت ⚠️", Toast.LENGTH_LONG).show()
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence? {
        return "تنبيه أمني: إلغاء تنشيط مدير الجهاز سيعطل الحماية ضد إلغاء التثبيت. يرجى إلغاء التفعيل من داخل إعدادات التطبيق باستخدام رمز المرور لضمان الأمان."
    }
}
