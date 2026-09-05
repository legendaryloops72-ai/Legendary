plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.aistudio.kidspolice.abcd"
    compileSdk = 35
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "com.aistudio.kidspolice.abcd"
        minSdk = 24
        targetSdk = 35
        versionCode = 20
        versionName = "1.0.6"
    }

    signingConfigs {
        create("debugConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            val keystorePath = (project.findProperty("RELEASE_KEYSTORE_PATH") as? String)
                ?: System.getenv("RELEASE_KEYSTORE_PATH")
            if (keystorePath != null && file(keystorePath).exists()) {
                storeFile = file(keystorePath)
                storePassword = (project.findProperty("RELEASE_STORE_PASSWORD") as? String)
                    ?: System.getenv("RELEASE_STORE_PASSWORD")
                keyAlias = (project.findProperty("RELEASE_KEY_ALIAS") as? String)
                    ?: System.getenv("RELEASE_KEY_ALIAS")
                keyPassword = (project.findProperty("RELEASE_KEY_PASSWORD") as? String)
                    ?: System.getenv("RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val releaseSigning = signingConfigs.getByName("release")
            if (releaseSigning.storeFile != null) {
                signingConfig = releaseSigning
            }
        }
        debug {
            signingConfig = signingConfigs.getByName("debugConfig")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation("com.google.android.libraries.ads.mobile.sdk:ads-mobile-sdk:1.4.0")
}
