# KMP Ready IS 🚧 UNDER DEVELOPMENT 🚧
[![LICENSE](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/handstandsam/kmp-ready/blob/main/LICENSE)
[![Latest Snapshot](https://img.shields.io/badge/dynamic/xml?url=https://s01.oss.sonatype.org/content/repositories/snapshots/com/handstandsam/kmp-ready/kmp-ready/maven-metadata.xml&label=Latest%20Snapshot&color=orange&query=.//versioning/latest)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/handstandsam/kmp-ready/com.handstandsam.kmp-ready.gradle.plugin/)
[![CI](https://github.com/handstandsam/kmp-ready/workflows/CI/badge.svg)](https://github.com/handstandsam/kmp-ready/actions?query=branch%3Amain)


# Decisioning Logic
## Positive Signals ✅
### Only Kotlin `.kt` Source Files
### Using Kotlin JVM Plugin
### Uses the Kotlin Multiplatform Plugin
* NOTE: We could check the configuration in the future to see if it has multiple targets if deemed important.

## Negative Signals ❌
### Are there any java imports in the source files?
Imports starting with `java.`, etc.
* Iterate through all `main` sourcesets for a simple `.contains("import java.")` List of all java stdlib packages: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/module-summary.html
* NOTE: This wouldn't find fully qualified references, but that could be added later if deemed important.
### Any non-multiplatform dependencies?
Search for transitive artifacts that are not multiplatform compatible
* Use a list of well known libraries that are known to be multiplatform compatible, but then using the Maven Search API to look for the presence of the `kotlin-tooling-metadata.json` object via their JSON API at https://search.maven.org/.
  * Example request for `ktor-client-core`: https://search.maven.org/solrsearch/select?q=g%3Aio.ktor+AND+a%3Aktor-client-core+AND+v%3A2.1.0+AND+p%3Ajar+AND+l%3Akotlin-tooling-metadata&rows=1&wt=json
* NOTE: These results would be cached, the same way dependencies are today.
* NOTE: We could traverse local module dependencies, and check theirs as well if deemed important.  
### Is this an Android Library Module?
This tool works on `kotlin("jvm")` modules.  If you are looking to move Android Libraries to Kotlin Multiplatform, the [dependency-analysis-android-gradle-plugin](https://github.com/autonomousapps/dependency-analysis-android-gradle-plugin) will help give you advice on which modules can become `kotlin("jvm")` modules.  After that, come back and use `kmp-ready`.


# `kmpReady` Gradle Task
Could be applied to a specific module or the root so that all modules are scanned.

Run the `kmpReady` task and get a result like this:
```
┌─────────────────────────────────┬───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│ Module                          │ KMP Ready Result                                                                                                      │
├─────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ :samples:android_app            │ Not KMP Ready                                                                                                         │
│                                 │ ---                                                                                                                   │
│                                 │ ✅ HasOnlyMultiplatformCompatibleDependencies                                                                          │
│                                 │ ✅ HasOnlyKotlinFiles                                                                                                  │
│                                 │ ❌ IsAndroidApplication                                                                                                │
│                                 │ ❌ DoesNotHaveKotlinJvmOrMultiplatformPlugin                                                                           │
│                                 │ Applied Plugins                                                                                                       │
│                                 │  * com.android.build.gradle.AppPlugin                                                                                 │
│                                 │  * com.android.build.gradle.api.AndroidBasePlugin                                                                     │
│                                 │  * com.android.build.gradle.internal.plugins.AppPlugin                                                                │
│                                 │  * com.android.build.gradle.internal.plugins.VersionCheckPlugin                                                       │
│                                 │  * com.dropbox.gradle.plugins.dependencyguard.DependencyGuardPlugin                                                   │
│                                 │  * org.gradle.kotlin.dsl.provider.plugins.KotlinScriptBasePlugin                                                      │
│                                 │                                                                                                                       │
│                                 │                                                                                                                       │
├─────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ :samples:android_lib            │ Not KMP Ready                                                                                                         │
│                                 │ ---                                                                                                                   │
│                                 │ ✅ HasOnlyMultiplatformCompatibleDependencies                                                                          │
│                                 │ ✅ HasOnlyKotlinFiles                                                                                                  │
│                                 │ ❌ IsAndroidLibrary                                                                                                    │
│                                 │ ❌ UsesJavaBaseImports                                                                                                 │
│                                 │ Java Base Library Usages                                                                                              │
│                                 │  * import java.util.Date                                                                                              │
│                                 │    /Users/samedwards/src/kmp-readiness/samples/android_lib/src/main/java/kmp4free/samples/android.lib/AndroidLib.kt:3 │
│                                 │ ❌ DoesNotHaveKotlinJvmOrMultiplatformPlugin                                                                           │
│                                 │ Applied Plugins                                                                                                       │
│                                 │  * com.android.build.gradle.LibraryPlugin                                                                             │
│                                 │  * com.android.build.gradle.api.AndroidBasePlugin                                                                     │
│                                 │  * com.android.build.gradle.internal.plugins.LibraryPlugin                                                            │
│                                 │  * com.android.build.gradle.internal.plugins.VersionCheckPlugin                                                       │
│                                 │  * org.gradle.kotlin.dsl.provider.plugins.KotlinScriptBasePlugin                                                      │
│                                 │  * org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper                                                      │
│                                 │                                                                                                                       │
│                                 │                                                                                                                       │
├─────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ :samples:jvm                    │ Not KMP Ready                                                                                                         │
│                                 │ ---                                                                                                                   │
│                                 │ ✅ HasOnlyKotlinFiles                                                                                                  │
│                                 │ ✅ KotlinPluginEnabled                                                                                                 │
│                                 │ ❌ IncompatibleDependencies                                                                                            │
│                                 │ * com.squareup.okhttp3:okhttp:4.9.3                                                                                   │
│                                 │ ❌ UsesJavaBaseImports                                                                                                 │
│                                 │ Java Base Library Usages                                                                                              │
│                                 │  * import java.util.Arrays                                                                                            │
│                                 │    /Users/samedwards/src/kmp-readiness/samples/jvm/src/main/java/kmp4free/samples/JvmLib.kt:3                         │
│                                 │  * val instant: java.time.Instant? = null                                                                             │
│                                 │    /Users/samedwards/src/kmp-readiness/samples/jvm/src/main/java/kmp4free/samples/JvmLib.kt:9                         │
│                                 │                                                                                                                       │
│                                 │                                                                                                                       │
├─────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ :samples:jvm_kmp4free           │ Not KMP Ready                                                                                                         │
│                                 │ ---                                                                                                                   │
│                                 │ ✅ HasOnlyMultiplatformCompatibleDependencies                                                                          │
│                                 │ ✅ KotlinPluginEnabled                                                                                                 │
│                                 │ ❌ HasJavaFiles                                                                                                        │
│                                 │  * /Users/samedwards/src/kmp-readiness/samples/jvm_kmp4free/src/main/java/kmp4free/samples/ThisIsAJavaClass.java      │
│                                 │                                                                                                                       │
│                                 │                                                                                                                       │
├─────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ :samples:multiplatform          │ KMP Ready                                                                                                             │
│                                 │ ---                                                                                                                   │
│                                 │ ✅ HasOnlyMultiplatformCompatibleDependencies                                                                          │
│                                 │ ✅ HasOnlyKotlinFiles                                                                                                  │
│                                 │ ✅ MultiplatformPluginAlreadyEnabled                                                                                   │
│                                 │                                                                                                                       │
│                                 │                                                                                                                       │
├─────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ :samples:multiplatform_kmp4free │ KMP Ready                                                                                                             │
│                                 │ ---                                                                                                                   │
│                                 │ ✅ HasOnlyMultiplatformCompatibleDependencies                                                                          │
│                                 │ ✅ HasOnlyKotlinFiles                                                                                                  │
│                                 │ ✅ KotlinPluginEnabled                                                                                                 │
│                                 │                                                                                                                       │
│                                 │                                                                                                                       │
└─────────────────────────────────┴───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

## Info/Debug Logging
Just pass use the info flag like `./gradlew kmpReady --info` when you run the Gradle task and all the information collected that is used for decisioning is printed.  Warning: This can be a TON of info on a large project.


## Plugin Installation
Add the Snapshot Repo in your project's `settings.gradle.kts`
```kotlin
pluginManagement {
    repositories {
        // ...
        maven { url = "https://s01.oss.sonatype.org/content/repositories/snapshots/" }
    }
}
```

Add the Plugin on your project's `build.gradle`
```kotlin
plugins {
    id("com.handstandsam.kmp-ready") version "0.1.0-SNAPSHOT"
}
```
