/**
 * AI & Notification Models - Firestore Schema
 * Collections: ai_tasks, notifications, weekly_summaries
 */

// ============================================================================
// AI TASK EXTRACTION
// ============================================================================

export enum AIIntent {
  CREATE_TASK = 'create_task',
  CREATE_EVENT = 'create_event',
  ADD_SHOPPING_ITEM = 'add_shopping_item',
  SET_REMINDER = 'set_reminder',
  QUERY_INFO = 'query_info',
  UNKNOWN = 'unknown',
}

export enum AIProcessingStatus {
  PENDING = 'pending',
  PROCESSING = 'processing',
  COMPLETED = 'completed',
  FAILED = 'failed',
}

export interface AITaskRequest {
  id: string;
  familyId: string;
  userId: string;
  originalText: string;
  sourceType: 'chat' | 'voice' | 'manual';
  sourceId?: string;
  intent: AIIntent;
  status: AIProcessingStatus;
  createdAt: FirebaseFirestore.Timestamp;
  processedAt?: FirebaseFirestore.Timestamp;
  extractedData?: ExtractedTaskData;
  confidence: number;
  requiresConfirmation: boolean;
  confirmedBy?: string;
  confirmedAt?: FirebaseFirestore.Timestamp;
}

export interface ExtractedTaskData {
  title?: string;
  description?: string;
  assignedTo?: string[];
  dueDate?: FirebaseFirestore.Timestamp;
  priority?: 'low' | 'medium' | 'high' | 'urgent';
  tags?: string[];
  location?: string;
  category?: string;
}

export interface AITaskSuggestion {
  id: string;
  familyId: string;
  userId: string;
  suggestedTaskTitle: string;
  suggestedTaskDescription?: string;
  suggestedAssignee?: string;
  suggestedDueDate?: FirebaseFirestore.Timestamp;
  basedOnHistory: boolean;
  basedOnPatterns: string[];
  confidence: number;
  createdAt: FirebaseFirestore.Timestamp;
  isAccepted: boolean;
  acceptedAt?: FirebaseFirestore.Timestamp;
  isDismissed: boolean;
  dismissedAt?: FirebaseFirestore.Timestamp;
}

// ============================================================================
// NOTIFICATIONS
// ============================================================================

export enum NotificationType {
  TASK_ASSIGNED = 'task_assigned',
  TASK_DUE_SOON = 'task_due_soon',
  TASK_COMPLETED = 'task_completed',
  TASK_COMMENT = 'task_comment',
  CHAT_MESSAGE = 'chat_message',
  CHAT_MENTION = 'chat_mention',
  EVENT_REMINDER = 'event_reminder',
  EVENT_INVITATION = 'event_invitation',
  SHOPPING_ITEM_ADDED = 'shopping_item_added',
  FAMILY_INVITE = 'family_invite',
  WEEKLY_SUMMARY = 'weekly_summary',
  SYSTEM = 'system',
}

export enum NotificationPriority {
  LOW = 'low',
  NORMAL = 'normal',
  HIGH = 'high',
  URGENT = 'urgent',
}

export interface Notification {
  id: string;
  userId: string;
  familyId: string;
  type: NotificationType;
  priority: NotificationPriority;
  title: string;
  body: string;
  data: NotificationData;
  imageURL?: string;
  actionURL?: string;
  isRead: boolean;
  readAt?: FirebaseFirestore.Timestamp;
  createdAt: FirebaseFirestore.Timestamp;
  expiresAt?: FirebaseFirestore.Timestamp;
  fcmMessageId?: string;
  fcmStatus?: 'pending' | 'sent' | 'failed';
  fcmSentAt?: FirebaseFirestore.Timestamp;
}

export interface NotificationData {
  entityType: 'task' | 'chat' | 'event' | 'shopping_list' | 'document' | 'family';
  entityId: string;
  actionType?: 'view' | 'edit' | 'respond' | 'accept' | 'decline';
  additionalData?: Record<string, any>;
}

export interface FCMPayload {
  token: string;
  notification: {
    title: string;
    body: string;
    imageUrl?: string;
  };
  data: Record<string, string>;
  android: {
    priority: 'high' | 'normal';
    notification: {
      channelId: string;
      sound: string;
      color?: string;
      clickAction: string;
    };
  };
}

// ============================================================================
// WEEKLY SUMMARY
// ============================================================================

export interface WeeklySummary {
  id: string;
  familyId: string;
  weekStartDate: FirebaseFirestore.Timestamp;
  weekEndDate: FirebaseFirestore.Timestamp;
  generatedAt: FirebaseFirestore.Timestamp;
  stats: WeeklySummaryStats;
  highlights: WeeklySummaryHighlight[];
  upcomingEvents: UpcomingEventSummary[];
  taskInsights: TaskInsight[];
  aiSummary?: string;
}

export interface WeeklySummaryStats {
  tasksCreated: number;
  tasksCompleted: number;
  taskCompletionRate: number;
  messagesExchanged: number;
  eventsCreated: number;
  shoppingItemsPurchased: number;
  documentsAdded: number;
  mostActiveMembers: MemberActivity[];
}

export interface MemberActivity {
  uid: string;
  displayName: string;
  photoURL?: string;
  activityScore: number;
  tasksCompleted: number;
  messagesSent: number;
}

export interface WeeklySummaryHighlight {
  type: 'achievement' | 'milestone' | 'trend' | 'reminder';
  title: string;
  description: string;
  icon?: string;
  relatedEntityId?: string;
}

export interface UpcomingEventSummary {
  eventId: string;
  title: string;
  date: FirebaseFirestore.Timestamp;
  attendeeCount: number;
}

export interface TaskInsight {
  category: string;
  totalTasks: number;
  completedTasks: number;
  averageCompletionTime: number;
  trend: 'up' | 'down' | 'stable';
}

// ============================================================================
// NOTIFICATION BATCH QUEUE
// ============================================================================

export interface NotificationBatch {
  id: string;
  familyId: string;
  notificationType: NotificationType;
  recipientIds: string[];
  payload: FCMPayload[];
  status: 'queued' | 'processing' | 'completed' | 'failed';
  createdAt: FirebaseFirestore.Timestamp;
  processedAt?: FirebaseFirestore.Timestamp;
  successCount: number;
  failureCount: number;
  errors?: NotificationError[];
}

export interface NotificationError {
  userId: string;
  fcmToken: string;
  errorCode: string;
  errorMessage: string;
  timestamp: FirebaseFirestore.Timestamp;
}

// ============================================================================
// COLLECTION NAMES
// ============================================================================

export const AI_TASK_COLLECTION = 'ai_tasks';
export const AI_SUGGESTION_COLLECTION = 'ai_suggestions';
export const NOTIFICATION_COLLECTION = 'notifications';
export const WEEKLY_SUMMARY_COLLECTION = 'weekly_summaries';
export const NOTIFICATION_BATCH_COLLECTION = 'notification_batches';

/**
 * Indexes Required:
 * - ai_tasks: familyId + status + createdAt
 * - ai_suggestions: familyId + userId + isAccepted
 * - notifications: userId + isRead + createdAt (desc)
 * - weekly_summaries: familyId + weekStartDate (desc)
 *
 * Security Rules:
 * - ai_tasks: Only requester and admins can read
 * - notifications: Only recipient can read/update
 * - weekly_summaries: All family members can read
 */
