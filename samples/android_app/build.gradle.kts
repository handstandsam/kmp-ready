// https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.application")
    alias(libs.plugins.dependency.guard)
}

android {
    compileSdk = libs.versions.android.compile.sdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
        targetSdk = libs.versions.android.target.sdk.get().toInt()
    }
}

dependencies {
    implementation(libs.androidx.activity)
    implementation(libs.kotlin.stdlib)
    implementation(project(":samples:android_lib"))
    implementation(project(":samples:jvm"))
    implementation(project(":samples:multiplatform"))
}

dependencyGuard {
    configuration("releaseRuntimeClasspath")
}
