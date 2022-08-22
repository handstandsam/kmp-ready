# KMP Readiness IS UNDER DEVELOPMENT

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
