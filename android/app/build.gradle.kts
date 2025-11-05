plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.familyhub.app"
    compileSdk = rootProject.extra["compileSdk"] as Int

    defaultConfig {
        applicationId = "com.familyhub.app"
        minSdk = rootProject.extra["minSdk"] as Int
        targetSdk = rootProject.extra["targetSdk"] as Int
        versionCode = rootProject.extra["versionCode"] as Int
        versionName = rootProject.extra["versionName"] as String

        testInstrumentationRunner = "com.familyhub.app.HiltTestRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug") // TODO: Configure release signing
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["composeCompilerVersion"] as String
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    // Core modules
    implementation(project(":core:auth"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))
    implementation(project(":core:common"))

    // Feature modules
    implementation(project(":feature:chat"))
    implementation(project(":feature:tasks"))
    implementation(project(":feature:calendar"))
    implementation(project(":feature:shared_data"))
    implementation(project(":feature:profile"))

    // AndroidX Core
    implementation("androidx.core:core-ktx:${rootProject.extra["coreKtxVersion"]}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${rootProject.extra["lifecycleVersion"]}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${rootProject.extra["lifecycleVersion"]}")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:${rootProject.extra["lifecycleVersion"]}")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:${rootProject.extra["composeBomVersion"]}")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:${rootProject.extra["activityComposeVersion"]}")
    implementation("androidx.navigation:navigation-compose:${rootProject.extra["navigationComposeVersion"]}")

    // Firebase
    val firebaseBom = platform("com.google.firebase:firebase-bom:${rootProject.extra["firebaseBomVersion"]}")
    implementation(firebaseBom)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-functions-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // Google Play Services
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hiltVersion"]}")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.extra["hiltVersion"]}")
    implementation("androidx.hilt:hilt-navigation-compose:${rootProject.extra["hiltNavigationComposeVersion"]}")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${rootProject.extra["coroutinesVersion"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.extra["coroutinesVersion"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${rootProject.extra["coroutinesVersion"]}")

    // Image Loading
    implementation("io.coil-kt:coil-compose:${rootProject.extra["coilVersion"]}")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Unit Testing
    testImplementation("junit:junit:${rootProject.extra["junitVersion"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${rootProject.extra["junit5Version"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${rootProject.extra["junit5Version"]}")
    testImplementation("io.mockk:mockk:${rootProject.extra["mockkVersion"]}")
    testImplementation("io.mockk:mockk-android:${rootProject.extra["mockkVersion"]}")
    testImplementation("app.cash.turbine:turbine:${rootProject.extra["turbineVersion"]}")
    testImplementation("com.google.truth:truth:${rootProject.extra["truthVersion"]}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${rootProject.extra["coroutinesVersion"]}")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.robolectric:robolectric:${rootProject.extra["robolectricVersion"]}")

    // Compose Testing
    testImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Android Instrumented Testing
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.test.ext:junit:${rootProject.extra["androidxJunitVersion"]}")
    androidTestImplementation("androidx.test.espresso:espresso-core:${rootProject.extra["espressoVersion"]}")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("io.mockk:mockk-android:${rootProject.extra["mockkVersion"]}")
    androidTestImplementation("app.cash.turbine:turbine:${rootProject.extra["turbineVersion"]}")

    // Hilt Testing
    androidTestImplementation("com.google.dagger:hilt-android-testing:${rootProject.extra["hiltVersion"]}")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:${rootProject.extra["hiltVersion"]}")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
