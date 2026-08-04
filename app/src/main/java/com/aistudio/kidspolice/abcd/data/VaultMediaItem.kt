package com.aistudio.kidspolice.abcd.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_media_items")
data class VaultMediaItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val localPath: String, // المسار الجديد داخل المجلد الآمن بالتطبيق
    val originalPath: String?, // المسار الأصلي في المعرض للاستعادة
    val isVideo: Boolean, // هل هو مقطع فيديو؟
    val dateAdded: Long = System.currentTimeMillis()
)
