// https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `kotlin-dsl`
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.binaryCompatibilityValidator)
    kotlin("plugin.serialization") version "1.6.21"
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

    implementation("com.jakewharton.picnic:picnic:0.6.0")

    val ktor_version = "2.1.0"
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation(libs.kotlin.stdlib)
    testImplementation(libs.kotlin.test.common)
    testImplementation(libs.truth)
}

tasks.register("printVersionName") {
    doLast {
        println(VERSION_NAME)
    }
}
