// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.50")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    }
}

plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1" apply false
    id("com.diffplug.spotless") version "6.23.3" apply false
}

// Global configuration
// Code quality plugins disabled for CI builds
// subprojects {
//     apply(plugin = "org.jlleitschuh.gradle.ktlint")
//     apply(plugin = "com.diffplug.spotless")
// }

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

// Version catalog for dependency management
extra.apply {
    set("compileSdk", 34)
    set("minSdk", 24)
    set("targetSdk", 34)
    set("versionCode", 1)
    set("versionName", "1.0.0")

    // Kotlin
    set("kotlinVersion", "1.9.20")
    set("kspVersion", "1.9.20-1.0.14")
    set("coroutinesVersion", "1.7.3")

    // AndroidX
    set("coreKtxVersion", "1.12.0")
    set("lifecycleVersion", "2.6.2")
    set("activityComposeVersion", "1.8.2")
    set("navigationComposeVersion", "2.7.6")

    // Compose
    set("composeVersion", "1.5.4")
    set("composeBomVersion", "2023.10.01")
    set("composeCompilerVersion", "1.5.4")

    // Firebase
    set("firebaseBomVersion", "32.7.0")

    // Hilt (Updated to 2.50 for KSP support)
    set("hiltVersion", "2.50")
    set("hiltNavigationComposeVersion", "1.1.0")

    // Networking
    set("retrofitVersion", "2.9.0")
    set("okhttpVersion", "4.12.0")

    // Image Loading
    set("coilVersion", "2.5.0")

    // Testing
    set("junitVersion", "4.13.2")
    set("junit5Version", "5.10.1")
    set("mockkVersion", "1.13.8")
    set("turbineVersion", "1.0.0")
    set("truthVersion", "1.1.4")
    set("espressoVersion", "3.5.1")
    set("androidxTestVersion", "1.5.0")
    set("androidxJunitVersion", "1.1.5")
    set("robolectricVersion", "4.11.1")
}
