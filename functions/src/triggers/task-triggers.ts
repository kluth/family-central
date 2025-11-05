/**
 * Task Triggers - Firestore Event Handlers
 * Handle task creation, updates, and due date notifications
 */

import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import { Task, TaskStatus } from '../models';

/**
 * Trigger when a new task is created
 * - Send notifications to assigned users
 * - Log activity
 */
export const onTaskCreated = functions.firestore
  .document('tasks/{taskId}')
  .onCreate(async (snapshot, context) => {
    const task = snapshot.data() as Task;
    const taskId = context.params.taskId;

    console.log(`New task created: ${taskId}`);

    try {
      // Send notifications to assigned users
      const notifications = task.assignedTo.map(async (userId) => {
        const notificationRef = admin.firestore().collection('notifications').doc();

        await notificationRef.set({
          id: notificationRef.id,
          userId: userId,
          familyId: task.familyId,
          type: 'task_assigned',
          priority: 'normal',
          title: 'New Task Assigned',
          body: `You've been assigned to: ${task.title}`,
          data: {
            entityType: 'task',
            entityId: taskId,
            actionType: 'view',
          },
          isRead: false,
          createdAt: admin.firestore.FieldValue.serverTimestamp(),
          fcmStatus: 'pending',
        });

        return notificationRef.id;
      });

      await Promise.all(notifications);

      console.log(`Sent ${notifications.length} notifications for task ${taskId}`);
    } catch (error) {
      console.error('Error processing task creation:', error);
    }
  });

/**
 * Trigger when a task is updated
 * - Notify on status changes
 * - Send reminders if due date changed
 */
export const onTaskUpdated = functions.firestore
  .document('tasks/{taskId}')
  .onUpdate(async (change, context) => {
    const beforeTask = change.before.data() as Task;
    const afterTask = change.after.data() as Task;
    const taskId = context.params.taskId;

    console.log(`Task updated: ${taskId}`);

    try {
      // Check if task was completed
      if (
        beforeTask.status !== TaskStatus.COMPLETED &&
        afterTask.status === TaskStatus.COMPLETED
      ) {
        // Notify family members about task completion
        const familyDoc = await admin
          .firestore()
          .collection('families')
          .doc(afterTask.familyId)
          .get();

        if (familyDoc.exists) {
          const family = familyDoc.data();
          const memberIds = family?.members.map((m: any) => m.uid) || [];

          const notifications = memberIds.map(async (userId: string) => {
            const notificationRef = admin.firestore().collection('notifications').doc();

            await notificationRef.set({
              id: notificationRef.id,
              userId: userId,
              familyId: afterTask.familyId,
              type: 'task_completed',
              priority: 'low',
              title: 'Task Completed',
              body: `${afterTask.createdByName} completed: ${afterTask.title}`,
              data: {
                entityType: 'task',
                entityId: taskId,
                actionType: 'view',
              },
              isRead: false,
              createdAt: admin.firestore.FieldValue.serverTimestamp(),
              fcmStatus: 'pending',
            });
          });

          await Promise.all(notifications);
        }
      }

      // Check if new users were assigned
      const newAssignees = afterTask.assignedTo.filter(
        (id) => !beforeTask.assignedTo.includes(id)
      );

      if (newAssignees.length > 0) {
        const notifications = newAssignees.map(async (userId) => {
          const notificationRef = admin.firestore().collection('notifications').doc();

          await notificationRef.set({
            id: notificationRef.id,
            userId: userId,
            familyId: afterTask.familyId,
            type: 'task_assigned',
            priority: 'normal',
            title: 'Task Assigned to You',
            body: `You've been assigned to: ${afterTask.title}`,
            data: {
              entityType: 'task',
              entityId: taskId,
              actionType: 'view',
            },
            isRead: false,
            createdAt: admin.firestore.FieldValue.serverTimestamp(),
            fcmStatus: 'pending',
          });
        });

        await Promise.all(notifications);
      }

      console.log(`Processed task update for ${taskId}`);
    } catch (error) {
      console.error('Error processing task update:', error);
    }
  });

/**
 * Scheduled function to check for tasks due soon
 * Runs every hour
 */
export const checkTasksDueSoon = functions.pubsub
  .schedule('0 * * * *')
  .onRun(async (context) => {
    console.log('Checking for tasks due soon...');

    try {
      const now = admin.firestore.Timestamp.now();
      const twentyFourHoursLater = admin.firestore.Timestamp.fromDate(
        new Date(Date.now() + 24 * 60 * 60 * 1000)
      );

      // Query tasks due in next 24 hours that haven't sent reminders
      const tasksSnapshot = await admin
        .firestore()
        .collection('tasks')
        .where('status', '!=', TaskStatus.COMPLETED)
        .where('dueDate', '>=', now)
        .where('dueDate', '<=', twentyFourHoursLater)
        .where('remindersSent', '==', false)
        .get();

      console.log(`Found ${tasksSnapshot.size} tasks due soon`);

      const updates = tasksSnapshot.docs.map(async (doc) => {
        const task = doc.data() as Task;

        // Send notifications to assigned users
        const notifications = task.assignedTo.map(async (userId) => {
          const notificationRef = admin.firestore().collection('notifications').doc();

          await notificationRef.set({
            id: notificationRef.id,
            userId: userId,
            familyId: task.familyId,
            type: 'task_due_soon',
            priority: 'high',
            title: 'Task Due Soon',
            body: `"${task.title}" is due soon!`,
            data: {
              entityType: 'task',
              entityId: doc.id,
              actionType: 'view',
            },
            isRead: false,
            createdAt: admin.firestore.FieldValue.serverTimestamp(),
            fcmStatus: 'pending',
          });
        });

        await Promise.all(notifications);

        // Mark task as reminders sent
        await doc.ref.update({ remindersSent: true });
      });

      await Promise.all(updates);

      console.log(`Sent reminders for ${tasksSnapshot.size} tasks`);
    } catch (error) {
      console.error('Error checking tasks due soon:', error);
    }
  });
