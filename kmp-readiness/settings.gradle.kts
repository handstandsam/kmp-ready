dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

include(":kmp-ready-common")
project(":kmp-ready-common").projectDir = file("../kmp-ready-common")
