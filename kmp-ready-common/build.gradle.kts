// https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform")
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    kotlin("plugin.serialization") version "1.5.31"
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.ktor.client.core)
                api(libs.kotlin.coroutines)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test.common)
                implementation(libs.kotlin.test.annotations.common)
            }
        }

        val jvmMain by getting {
            dependsOn(commonMain)
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.truth)
                implementation(libs.kotlin.test.junit5)
            }
        }
    }
}




tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        // Because Gradle's Kotlin handling, this falls out of date quickly
        apiVersion = "1.5"
        languageVersion = "1.5"

        // We use class SAM conversions because lambdas compiled into invokedynamic are not
        // Serializable, which causes accidental headaches with Gradle configuration caching. It's
        // easier for us to just use the previous anonymous classes behavior
        @Suppress("SuspiciousCollectionReassignment")
        freeCompilerArgs += "-Xsam-conversion=class"
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
}

mavenPublish {
    sonatypeHost = com.vanniktech.maven.publish.SonatypeHost.S01
}