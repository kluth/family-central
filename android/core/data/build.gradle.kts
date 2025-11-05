plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.familyhub.core.data"
    compileSdk = rootProject.extra["compileSdk"] as Int

    defaultConfig {
        minSdk = rootProject.extra["minSdk"] as Int

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))

    implementation("androidx.core:core-ktx:${rootProject.extra["coreKtxVersion"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutinesVersion"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${rootProject.extra["coroutinesVersion"]}")

    // Firebase
    val firebaseBom = platform("com.google.firebase:firebase-bom:${rootProject.extra["firebaseBomVersion"]}")
    implementation(firebaseBom)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Hilt
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hiltVersion"]}")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra["hiltVersion"]}")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Testing
    testImplementation("junit:junit:${rootProject.extra["junitVersion"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${rootProject.extra["junit5Version"]}")
    testImplementation("io.mockk:mockk:${rootProject.extra["mockkVersion"]}")
    testImplementation("com.google.truth:truth:${rootProject.extra["truthVersion"]}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${rootProject.extra["coroutinesVersion"]}")
    testImplementation("app.cash.turbine:turbine:${rootProject.extra["turbineVersion"]}")
}

kapt {
    correctErrorTypes = true
}
