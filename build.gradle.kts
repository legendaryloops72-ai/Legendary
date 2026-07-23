// Top-level build file where you can add configuration options common to all sub-projects/modules.
// applicationId = "com.aistudio.kidspolice.abcd"
// versionCode = 18
// versionName = "1.8.0"
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.google.devtools.ksp) apply false
  alias(libs.plugins.roborazzi) apply false
  alias(libs.plugins.secrets) apply false
}
