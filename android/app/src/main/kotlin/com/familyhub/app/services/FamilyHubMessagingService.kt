package com.familyhub.app.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

/**
 * Firebase Cloud Messaging Service
 * Handles push notifications
 */
class FamilyHubMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Timber.d("FCM message received from: ${remoteMessage.from}")

        // Handle notification payload
        remoteMessage.notification?.let { notification ->
            Timber.d("Notification: ${notification.title} - ${notification.body}")
            // TODO: Display notification
        }

        // Handle data payload
        remoteMessage.data.isNotEmpty().let {
            Timber.d("Data payload: ${remoteMessage.data}")
            // TODO: Handle data
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("New FCM token: $token")
        // TODO: Send token to server
    }
}
