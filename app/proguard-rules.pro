# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#

# Keep app model classes and database models
-keep class com.aistudio.kidspolice.abcd.data.** { *; }
-keepclassmembers class com.aistudio.kidspolice.abcd.data.** { *; }

# Moshi Proguard Rules
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**
-keepclassmembers class * {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}
# Keep Moshi JsonAdapters
-keep class * extends com.squareup.moshi.JsonAdapter { *; }
-keep class * implements com.squareup.moshi.JsonAdapter { *; }
-keep class **_JsonAdapter { *; }

# Retrofit Proguard Rules
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepclassmembernames class * {
    @retrofit2.http.* <methods>;
}

# Room Proguard Rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**
-keep class androidx.room.db.SupportSQLite* { *; }
-keep class * implements androidx.room.RoomDatabase$Callback

# Kotlinx Serialization Rules
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
    <fields>;
}
-keepclassmembers class * {
    *** Companion;
}
-keep class *$$serializer { *; }
-keepclassmembers class * {
    *** write$Self(...);
    *** deserialize(...);
}

# Keep line numbers for more detailed stack traces in crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
