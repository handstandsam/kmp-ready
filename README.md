# KMP Readiness IS UNDER DEVELOPMENT

Run the `kmpReadiness` task and get a result like this:
```
┌─────────────────────────────────┬──────────────────────┐
│ Module Path                     │ KMP Readiness Result │
├─────────────────────────────────┼──────────────────────┤
│ :samples:jvm                    │ Compatible           │
├─────────────────────────────────┼──────────────────────┤
│ :samples:jvm_kmp4free           │ Compatible           │
├─────────────────────────────────┼──────────────────────┤
│ :samples:multiplatform          │ AlreadyEnabled       │
├─────────────────────────────────┼──────────────────────┤
│ :samples:multiplatform_kmp4free │ Compatible           │
├─────────────────────────────────┼──────────────────────┤
│ :samples                        │ NotReady             │
├─────────────────────────────────┼──────────────────────┤
│ :samples:android_app            │ NotReady             │
├─────────────────────────────────┼──────────────────────┤
│ :samples:android_lib            │ NotReady             │
└─────────────────────────────────┴──────────────────────┘
```
