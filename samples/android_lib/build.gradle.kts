plugins {
  id("com.android.library")
  kotlin("android")
}

android {
  compileSdk = libs.versions.android.compile.sdk.get().toInt()
  defaultConfig {
    minSdk = libs.versions.android.min.sdk.get().toInt()
    targetSdk = libs.versions.android.target.sdk.get().toInt()
  }
}

dependencies {
  implementation(libs.kotlin.stdlib)
  testImplementation(libs.truth)
}


project.extensions.findByType(org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension::class.java)?.apply {
  if (project.findProperty("ios") == "true") {
    iosSimulatorArm64 {
      binaries.framework {
        baseName = project.name
      }
    }
  }
  if (project.findProperty("js") == "true") {
    js(IR) {
      browser()
    }
  }
}
