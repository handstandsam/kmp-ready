@Suppress("DSL_SCOPE_VIOLATION") // https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("multiplatform")
    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    js(IR) {
        browser {
            testTask {
                enabled = false
            }
            binaries.executable()
        }
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":kmp-ready-common"))
                implementation(libs.ktor.client.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jsMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ktor.client.js)
                implementation(libs.jetbrains.compose.web.core)
                implementation(libs.jetbrains.compose.runtime)
            }
        }
        val jsTest by getting {
            dependsOn(commonTest)
        }
    }
}