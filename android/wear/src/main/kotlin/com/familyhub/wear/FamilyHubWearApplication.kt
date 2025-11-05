package com.familyhub.wear

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * FamilyHubWearApplication
 * Main application class for Wear OS app
 */
@HiltAndroidApp
class FamilyHubWearApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.d("FamilyHub Wear OS initialized")
    }
}
