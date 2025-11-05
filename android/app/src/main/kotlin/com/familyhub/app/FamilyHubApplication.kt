package com.familyhub.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * FamilyHub Application
 * Entry point for the application with Hilt dependency injection
 */
@HiltAndroidApp
class FamilyHubApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.d("FamilyHub Application initialized")
    }
}
