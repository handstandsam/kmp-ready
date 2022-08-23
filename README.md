# KMP Readiness IS UNDER DEVELOPMENT

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
Could work if...
* No `res` folder
* No `assets` folder
* Uses no `android.` packages in the source
* `AndroidManifest.xml` only defines package name.


# `kmpReadiness` Gradle Task
Could be applied to a specific module or the root so that all modules are scanned.

Run the `kmpReadiness` task and get a result like this:
```
┌─────────────────────────────────┬────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│ Module Path                     │ KMP Readiness Result                                                                                                                                   │
├─────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ :samples:jvm                    │ Ready                                                                                                                                                  │
│                                 │ {                                                                                                                                                      │
│                                 │     "projectName": ":samples:jvm",                                                                                                                     │
│                                 │     "dependencyAnalysisResult": {                                                                                                                      │
│                                 │         "compatible": [                                                                                                                                │
│                                 │             "org.jetbrains.kotlin:kotlin-stdlib:1.6.21"                                                                                                │
│                                 │         ],                                                                                                                                             │
│                                 │         "incompatible": [                                                                                                                              │
│                                 │         ]                                                                                                                                              │
│                                 │     },                                                                                                                                                 │
│                                 │     "gradlePlugins": {                                                                                                                                 │
│                                 │         "kotlin": true,                                                                                                                                │
│                                 │         "multiplatform": false,                                                                                                                        │
│                                 │         "java": true                                                                                                                                   │
│                                 │     },                                                                                                                                                 │
│                                 │     "sourceSetSearcherResult": {                                                                                                                       │
│                                 │         "sourceSetToFiles": {                                                                                                                          │
│                                 │             "kotlin-main": [                                                                                                                           │
│                                 │                 "/Users/samedwards/src/kmp-readiness/samples/jvm/src/main/java/kmp4free/samples/JvmLib.kt"                                             │
│                                 │             ],                                                                                                                                         │
│                                 │             "java-main": [                                                                                                                             │
│                                 │             ]                                                                                                                                          │
│                                 │         }                                                                                                                                              │
│                                 │     }                                                                                                                                                  │
│                                 │ }                                                                                                                                                      │
├─────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ :samples:jvm_kmp4free           │ Ready                                                                                                                                                  │
│                                 │ {                                                                                                                                                      │
│                                 │     "projectName": ":samples:jvm_kmp4free",                                                                                                            │
│                                 │     "dependencyAnalysisResult": {                                                                                                                      │
│                                 │         "compatible": [                                                                                                                                │
│                                 │             "org.jetbrains.kotlin:kotlin-stdlib:1.6.21"                                                                                                │
│                                 │         ],                                                                                                                                             │
│                                 │         "incompatible": [                                                                                                                              │
│                                 │         ]                                                                                                                                              │
│                                 │     },                                                                                                                                                 │
│                                 │     "gradlePlugins": {                                                                                                                                 │
│                                 │         "kotlin": true,                                                                                                                                │
│                                 │         "multiplatform": false,                                                                                                                        │
│                                 │         "java": true                                                                                                                                   │
│                                 │     },                                                                                                                                                 │
│                                 │     "sourceSetSearcherResult": {                                                                                                                       │
│                                 │         "sourceSetToFiles": {                                                                                                                          │
│                                 │             "kotlin-main": [                                                                                                                           │
│                                 │                 "/Users/samedwards/src/kmp-readiness/samples/jvm_kmp4free/src/main/kotlin/kmp4free/samples/JvmMain2.java",                             │
│                                 │                 "/Users/samedwards/src/kmp-readiness/samples/jvm_kmp4free/src/main/kotlin/kmp4free/samples/JvmMain.kt"                                 │
│                                 │             ],                                                                                                                                         │
│                                 │             "java-main": [                                                                                                                             │
│                                 │             ]                                                                                                                                          │
│                                 │         }                                                                                                                                              │
│                                 │     }                                                                                                                                                  │
│                                 │ }                                                                                                                                                      │
├─────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ :samples:multiplatform          │ Ready                                                                                                                                                  │
│                                 │ {                                                                                                                                                      │
│                                 │     "projectName": ":samples:multiplatform",                                                                                                           │
│                                 │     "dependencyAnalysisResult": {                                                                                                                      │
│                                 │         "compatible": [                                                                                                                                │
│                                 │         ],                                                                                                                                             │
│                                 │         "incompatible": [                                                                                                                              │
│                                 │         ]                                                                                                                                              │
│                                 │     },                                                                                                                                                 │
│                                 │     "gradlePlugins": {                                                                                                                                 │
│                                 │         "kotlin": false,                                                                                                                               │
│                                 │         "multiplatform": true,                                                                                                                         │
│                                 │         "java": false                                                                                                                                  │
│                                 │     },                                                                                                                                                 │
│                                 │     "sourceSetSearcherResult": {                                                                                                                       │
│                                 │         "sourceSetToFiles": {                                                                                                                          │
│                                 │         }                                                                                                                                              │
│                                 │     }                                                                                                                                                  │
│                                 │ }                                                                                                                                                      │
├─────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ :samples:multiplatform_kmp4free │ Ready                                                                                                                                                  │
│                                 │ {                                                                                                                                                      │
│                                 │     "projectName": ":samples:multiplatform_kmp4free",                                                                                                  │
│                                 │     "dependencyAnalysisResult": {                                                                                                                      │
│                                 │         "compatible": [                                                                                                                                │
│                                 │             "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21",                                                                                          │
│                                 │             "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1"                                                                                      │
│                                 │         ],                                                                                                                                             │
│                                 │         "incompatible": [                                                                                                                              │
│                                 │         ]                                                                                                                                              │
│                                 │     },                                                                                                                                                 │
│                                 │     "gradlePlugins": {                                                                                                                                 │
│                                 │         "kotlin": true,                                                                                                                                │
│                                 │         "multiplatform": false,                                                                                                                        │
│                                 │         "java": true                                                                                                                                   │
│                                 │     },                                                                                                                                                 │
│                                 │     "sourceSetSearcherResult": {                                                                                                                       │
│                                 │         "sourceSetToFiles": {                                                                                                                          │
│                                 │             "kotlin-main": [                                                                                                                           │
│                                 │                 "/Users/samedwards/src/kmp-readiness/samples/multiplatform_kmp4free/src/commonMain/kotlin/kmp4free/samples/MultiplatformCommonMain.kt" │
│                                 │             ],                                                                                                                                         │
│                                 │             "java-main": [                                                                                                                             │
│                                 │             ]                                                                                                                                          │
│                                 │         }                                                                                                                                              │
│                                 │     }                                                                                                                                                  │
│                                 │ }                                                                                                                                                      │
├─────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ :samples:android_app            │ NotReady                                                                                                                                               │
│                                 │ {                                                                                                                                                      │
│                                 │     "projectName": ":samples:android_app",                                                                                                             │
│                                 │     "dependencyAnalysisResult": {                                                                                                                      │
│                                 │         "compatible": [                                                                                                                                │
│                                 │         ],                                                                                                                                             │
│                                 │         "incompatible": [                                                                                                                              │
│                                 │         ]                                                                                                                                              │
│                                 │     },                                                                                                                                                 │
│                                 │     "gradlePlugins": {                                                                                                                                 │
│                                 │         "kotlin": false,                                                                                                                               │
│                                 │         "multiplatform": false,                                                                                                                        │
│                                 │         "java": false                                                                                                                                  │
│                                 │     },                                                                                                                                                 │
│                                 │     "sourceSetSearcherResult": {                                                                                                                       │
│                                 │         "sourceSetToFiles": {                                                                                                                          │
│                                 │         }                                                                                                                                              │
│                                 │     }                                                                                                                                                  │
│                                 │ }                                                                                                                                                      │
├─────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ :samples:android_lib            │ NotReady                                                                                                                                               │
│                                 │ {                                                                                                                                                      │
│                                 │     "projectName": ":samples:android_lib",                                                                                                             │
│                                 │     "dependencyAnalysisResult": {                                                                                                                      │
│                                 │         "compatible": [                                                                                                                                │
│                                 │         ],                                                                                                                                             │
│                                 │         "incompatible": [                                                                                                                              │
│                                 │         ]                                                                                                                                              │
│                                 │     },                                                                                                                                                 │
│                                 │     "gradlePlugins": {                                                                                                                                 │
│                                 │         "kotlin": false,                                                                                                                               │
│                                 │         "multiplatform": false,                                                                                                                        │
│                                 │         "java": false                                                                                                                                  │
│                                 │     },                                                                                                                                                 │
│                                 │     "sourceSetSearcherResult": {                                                                                                                       │
│                                 │         "sourceSetToFiles": {                                                                                                                          │
│                                 │         }                                                                                                                                              │
│                                 │     }                                                                                                                                                  │
│                                 │ }                                                                                                                                                      │
└─────────────────────────────────┴────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘


```
