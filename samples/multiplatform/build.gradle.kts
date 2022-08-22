plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    sourceSets {
        maybeCreate("commonMain").apply {
            dependencies {
                implementation(libs.kotlin.coroutines)
            }
        }
        maybeCreate("commonTest").apply {
            dependencies {
                implementation(libs.kotlin.test.common)
                implementation(libs.kotlin.test.annotations.common)
            }
        }
        maybeCreate("jvmTest").apply {
            dependencies {
                implementation(libs.truth)
                implementation(libs.kotlin.test.junit5)
            }
        }
    }
}
