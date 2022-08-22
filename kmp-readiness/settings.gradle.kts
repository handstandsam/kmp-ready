dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

// pluginManagement {
//     repositories {
//         mavenCentral()
//         google()
//         gradlePluginPortal()
//         maven { setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
//     }
// }
