pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FamilyHub"

// Main application module
include(":app")

// Core modules
include(":core:auth")
include(":core:data")
include(":core:domain")
include(":core:ui")
include(":core:common")

// Feature modules
include(":feature:chat")
include(":feature:tasks")
include(":feature:calendar")
include(":feature:shared_data")
include(":feature:profile")
