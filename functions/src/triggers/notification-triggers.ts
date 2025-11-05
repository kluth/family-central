/**
 * Notification Triggers - FCM Message Sending
 * Handle sending FCM notifications when notification documents are created
 */

import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import { Notification, NotificationPriority } from '../models';

/**
 * Trigger when a new notification is created
 * Send FCM push notification to user
 */
export const onNotificationCreated = functions.firestore
  .document('notifications/{notificationId}')
  .onCreate(async (snapshot, context) => {
    const notification = snapshot.data() as Notification;
    const notificationId = context.params.notificationId;

    console.log(`New notification created: ${notificationId} for user: ${notification.userId}`);

    try {
      // Get user's FCM token
      const userDoc = await admin
        .firestore()
        .collection('users')
        .doc(notification.userId)
        .get();

      if (!userDoc.exists) {
        console.error(`User ${notification.userId} not found`);
        return;
      }

      const userData = userDoc.data();
      const fcmToken = userData?.notificationPreferences?.fcmToken;

      if (!fcmToken) {
        console.log(`No FCM token found for user ${notification.userId}`);
        await snapshot.ref.update({
          fcmStatus: 'failed',
          fcmSentAt: admin.firestore.FieldValue.serverTimestamp(),
        });
        return;
      }

      // Construct FCM message
      const message: admin.messaging.Message = {
        token: fcmToken,
        notification: {
          title: notification.title,
          body: notification.body,
          imageUrl: notification.imageURL,
        },
        data: {
          notificationId: notificationId,
          entityType: notification.data.entityType,
          entityId: notification.data.entityId,
          actionType: notification.data.actionType || 'view',
          ...notification.data.additionalData,
        },
        android: {
          priority: notification.priority === NotificationPriority.URGENT ? 'high' : 'normal',
          notification: {
            channelId: getChannelId(notification.type),
            sound: 'default',
            color: '#6200EE',
            clickAction: 'FLUTTER_NOTIFICATION_CLICK',
            tag: notification.data.entityId,
          },
        },
      };

      // Send FCM message
      const response = await admin.messaging().send(message);

      console.log(`Successfully sent FCM message: ${response}`);

      // Update notification status
      await snapshot.ref.update({
        fcmStatus: 'sent',
        fcmMessageId: response,
        fcmSentAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    } catch (error: any) {
      console.error('Error sending FCM notification:', error);

      // Update notification status with error
      await snapshot.ref.update({
        fcmStatus: 'failed',
        fcmSentAt: admin.firestore.FieldValue.serverTimestamp(),
      });

      // If token is invalid, remove it from user profile
      if (
        error.code === 'messaging/invalid-registration-token' ||
        error.code === 'messaging/registration-token-not-registered'
      ) {
        console.log(`Removing invalid FCM token for user ${notification.userId}`);
        await admin
          .firestore()
          .collection('users')
          .doc(notification.userId)
          .update({
            'notificationPreferences.fcmToken': admin.firestore.FieldValue.delete(),
          });
      }
    }
  });

/**
 * Get Android notification channel ID based on notification type
 */
function getChannelId(notificationType: string): string {
  const channelMap: Record<string, string> = {
    task_assigned: 'tasks',
    task_due_soon: 'tasks',
    task_completed: 'tasks',
    task_comment: 'tasks',
    chat_message: 'messages',
    chat_mention: 'mentions',
    event_reminder: 'events',
    event_invitation: 'events',
    shopping_item_added: 'shopping',
    family_invite: 'family',
    weekly_summary: 'summaries',
    system: 'system',
  };

  return channelMap[notificationType] || 'default';
}

/**
 * Batch send notifications
 * Process pending notification batches
 */
export const processPendingNotificationBatch = functions.firestore
  .document('notification_batches/{batchId}')
  .onCreate(async (snapshot, context) => {
    const batch = snapshot.data();
    const batchId = context.params.batchId;

    console.log(`Processing notification batch: ${batchId}`);

    try {
      await snapshot.ref.update({ status: 'processing' });

      const recipientIds: string[] = batch.recipientIds || [];
      let successCount = 0;
      let failureCount = 0;
      const errors: any[] = [];

      // Get FCM tokens for all recipients
      const userDocs = await Promise.all(
        recipientIds.map((userId) =>
          admin.firestore().collection('users').doc(userId).get()
        )
      );

      const tokens: string[] = [];
      const validUserIds: string[] = [];

      userDocs.forEach((doc, index) => {
        if (doc.exists) {
          const fcmToken = doc.data()?.notificationPreferences?.fcmToken;
          if (fcmToken) {
            tokens.push(fcmToken);
            validUserIds.push(recipientIds[index]);
          }
        }
      });

      if (tokens.length === 0) {
        console.log('No valid FCM tokens found');
        await snapshot.ref.update({
          status: 'completed',
          successCount: 0,
          failureCount: recipientIds.length,
          processedAt: admin.firestore.FieldValue.serverTimestamp(),
        });
        return;
      }

      // Send to multiple devices
      const payload = batch.payload[0]; // Use first payload as template
      const message: admin.messaging.MulticastMessage = {
        tokens: tokens,
        notification: payload.notification,
        data: payload.data,
        android: payload.android,
      };

      const response = await admin.messaging().sendMulticast(message);

      console.log(`Batch send result: ${response.successCount} success, ${response.failureCount} failures`);

      // Process responses
      response.responses.forEach((resp, idx) => {
        if (resp.success) {
          successCount++;
        } else {
          failureCount++;
          errors.push({
            userId: validUserIds[idx],
            fcmToken: tokens[idx],
            errorCode: resp.error?.code || 'unknown',
            errorMessage: resp.error?.message || 'Unknown error',
            timestamp: admin.firestore.FieldValue.serverTimestamp(),
          });

          // Remove invalid tokens
          if (
            resp.error?.code === 'messaging/invalid-registration-token' ||
            resp.error?.code === 'messaging/registration-token-not-registered'
          ) {
            admin
              .firestore()
              .collection('users')
              .doc(validUserIds[idx])
              .update({
                'notificationPreferences.fcmToken': admin.firestore.FieldValue.delete(),
              })
              .catch((err) => console.error('Error removing invalid token:', err));
          }
        }
      });

      // Update batch status
      await snapshot.ref.update({
        status: 'completed',
        successCount: successCount,
        failureCount: failureCount,
        errors: errors,
        processedAt: admin.firestore.FieldValue.serverTimestamp(),
      });

      console.log(`Batch ${batchId} completed: ${successCount} success, ${failureCount} failures`);
    } catch (error) {
      console.error('Error processing notification batch:', error);

      await snapshot.ref.update({
        status: 'failed',
        processedAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    }
  });

/**
 * Clean up old notifications
 * Runs daily at 2 AM
 */
export const cleanupOldNotifications = functions.pubsub
  .schedule('0 2 * * *')
  .timeZone('America/New_York')
  .onRun(async (context) => {
    console.log('Cleaning up old notifications...');

    try {
      const thirtyDaysAgo = admin.firestore.Timestamp.fromDate(
        new Date(Date.now() - 30 * 24 * 60 * 60 * 1000)
      );

      // Delete notifications older than 30 days
      const oldNotifications = await admin
        .firestore()
        .collection('notifications')
        .where('createdAt', '<', thirtyDaysAgo)
        .get();

      console.log(`Found ${oldNotifications.size} old notifications to delete`);

      const batch = admin.firestore().batch();
      oldNotifications.docs.forEach((doc) => {
        batch.delete(doc.ref);
      });

      await batch.commit();

      console.log(`Deleted ${oldNotifications.size} old notifications`);
    } catch (error) {
      console.error('Error cleaning up notifications:', error);
    }
  });
