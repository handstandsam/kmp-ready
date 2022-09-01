import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("com.handstandsam.kmp4free")
}

dependencies {
    implementation(project(":samples:jvm"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.datetime)
    testImplementation(libs.kotlin.test.common)
    testImplementation(libs.truth)
}

project.extensions.findByType(KotlinMultiplatformExtension::class.java)?.apply {
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
