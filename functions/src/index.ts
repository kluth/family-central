/**
 * FamilyHub Platform - Cloud Functions Entry Point
 *
 * This file exports all Cloud Functions:
 * - HTTP Callable Functions (API)
 * - Firestore Triggers
 * - Scheduled Functions (Cron Jobs)
 */

import * as admin from 'firebase-admin';

// Initialize Firebase Admin SDK
admin.initializeApp();

// Configure Firestore settings
const firestore = admin.firestore();
firestore.settings({
  ignoreUndefinedProperties: true,
});

// ============================================================================
// TASK MANAGEMENT API
// ============================================================================
export {
  createTask,
  updateTask,
  deleteTask,
  getTasks,
  toggleSubtask,
  addTaskComment,
  assignTask,
} from './api/task-api';

// ============================================================================
// AI INTEGRATION API
// ============================================================================
export {
  extractTaskFromText,
  confirmAITask,
  getTaskSuggestions,
  generateWeeklySummary,
  chatWithAI,
  scheduledWeeklySummary,
  scheduledDailyTaskSuggestions,
} from './api/ai-api';

// ============================================================================
// CHAT API
// ============================================================================
export {
  createChat,
  sendMessage,
  updateTypingStatus,
  markMessagesAsRead,
  addReaction,
  getChatHistory,
  searchMessages,
} from './api/chat-api';

// ============================================================================
// FIRESTORE TRIGGERS
// ============================================================================
export {
  onTaskCreated,
  onTaskUpdated,
  checkTasksDueSoon,
} from './triggers/task-triggers';

export {
  onNotificationCreated,
  processPendingNotificationBatch,
  cleanupOldNotifications,
} from './triggers/notification-triggers';

// ============================================================================
// HEALTH CHECK
// ============================================================================
import * as functions from 'firebase-functions';

export const healthCheck = functions.https.onRequest((request, response) => {
  response.status(200).json({
    status: 'healthy',
    timestamp: new Date().toISOString(),
    version: '1.0.0',
    environment: process.env.NODE_ENV || 'production',
  });
});
