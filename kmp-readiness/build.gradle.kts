// https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `kotlin-dsl`
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.binaryCompatibilityValidator)
    kotlin("plugin.serialization") version "1.5.31"
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

val VERSION_NAME: String by project
version = VERSION_NAME

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
}

kotlin {
    explicitApi()
}

gradlePlugin {
    plugins {
        plugins.create("kmp-readiness") {
            id = "com.handstandsam.kmp-readiness"
            implementationClass = "com.handstandsam.kmpreadiness.KmpReadinessPlugin"
        }
    }
}

mavenPublish {
    sonatypeHost = com.vanniktech.maven.publish.SonatypeHost.S01
}

dependencies {
    compileOnly(gradleApi())
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.gradle.plugin)

    implementation(libs.picnic)
    implementation(libs.kotlin.tooling.metadata)

    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlin.coroutines)
    testImplementation(libs.kotlin.test.common)
    testImplementation(libs.truth)
}

tasks.register("printVersionName") {
    doLast {
        println(VERSION_NAME)
    }
}
