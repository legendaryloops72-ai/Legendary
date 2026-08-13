plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  // alias(libs.plugins.secrets)
}

android {
  namespace = "com.aistudio.kidspolice.abcd"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.aistudio.kidspolice.abcd"
    minSdk = 24
    targetSdk = 36
    versionCode = 20
    versionName = "0.0.20"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      if (System.getenv("KEYSTORE_PATH") != null) {
        signingConfig = signingConfigs.getByName("release")
      } else {
        signingConfig = signingConfigs.getByName("debugConfig")
      }
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)
  implementation(libs.androidx.biometric)
  implementation(libs.coil.compose)
  implementation(libs.retrofit)
  implementation(libs.retrofit.converter.serialization)
  implementation(libs.converter.moshi)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.accompanist.permissions)
  implementation(libs.play.services.location)
  implementation(libs.androidx.camera.camera2)
  implementation(libs.androidx.camera.lifecycle)
  implementation(libs.androidx.camera.view)
  implementation(libs.androidx.camera.core)
  implementation(libs.logging.interceptor)
  implementation(libs.okhttp)
  implementation(libs.moshi.kotlin)
  ksp(libs.moshi.kotlin.codegen)
  implementation(libs.androidx.datastore.preferences)
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.vertexai)
  implementation(libs.firebase.auth)
  implementation(libs.play.services.ads)

  testImplementation(libs.junit)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.androidx.espresso.core)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.runner)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)

  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}

tasks.register<Copy>("copyReleaseOutputsToBuildOutputs") {
  val rootDirFile = rootProject.projectDir
  from(layout.buildDirectory.file("outputs/apk/release/app-release.apk"))
  from(layout.buildDirectory.file("outputs/bundle/release/app-release.aab"))
  into(rootDirFile.resolve(".build-outputs"))
}

tasks.configureEach {
  if (name == "assembleRelease" || name == "bundleRelease") {
    finalizedBy("copyReleaseOutputsToBuildOutputs")
  }
}

gradle.taskGraph.whenReady {
  val hasReleaseTask = allTasks.any { it.name.contains("Release") }
  if (hasReleaseTask) {
    val keystorePath = System.getenv("KEYSTORE_PATH")
    val storePassword = System.getenv("STORE_PASSWORD")
    val keyPassword = System.getenv("KEY_PASSWORD")
    if (keystorePath.isNullOrEmpty() || storePassword.isNullOrEmpty() || keyPassword.isNullOrEmpty()) {
      println("\n********************************************************************************")
      println("تحذير: يتم التوقيع بمفتاح debug - لا يصلح للرفع على Google Play")
      println("********************************************************************************\n")
    }
  }
}
