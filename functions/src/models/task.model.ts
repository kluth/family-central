/**
 * Task Model - Firestore Schema
 * Collection: tasks
 */

export enum TaskStatus {
  TODO = 'todo',
  IN_PROGRESS = 'in_progress',
  COMPLETED = 'completed',
  CANCELLED = 'cancelled',
}

export enum TaskPriority {
  LOW = 'low',
  MEDIUM = 'medium',
  HIGH = 'high',
  URGENT = 'urgent',
}

export enum RecurrenceType {
  NONE = 'none',
  DAILY = 'daily',
  WEEKLY = 'weekly',
  MONTHLY = 'monthly',
  CUSTOM = 'custom',
}

export interface Task {
  id: string;
  familyId: string;
  title: string;
  description?: string;
  status: TaskStatus;
  priority: TaskPriority;
  assignedTo: string[];
  assignedToNames: string[];
  createdBy: string;
  createdByName: string;
  createdAt: FirebaseFirestore.Timestamp;
  updatedAt: FirebaseFirestore.Timestamp;
  dueDate?: FirebaseFirestore.Timestamp;
  completedAt?: FirebaseFirestore.Timestamp;
  completedBy?: string;
  tags: string[];
  subtasks: Subtask[];
  recurrence?: RecurrenceConfig;
  attachments: Attachment[];
  comments: Comment[];
  remindersSent: boolean;
  notificationSent: boolean;
}

export interface Subtask {
  id: string;
  title: string;
  isCompleted: boolean;
  completedAt?: FirebaseFirestore.Timestamp;
  completedBy?: string;
  order: number;
}

export interface RecurrenceConfig {
  type: RecurrenceType;
  interval: number;
  daysOfWeek?: number[];
  endDate?: FirebaseFirestore.Timestamp;
  lastGenerated?: FirebaseFirestore.Timestamp;
}

export interface Attachment {
  id: string;
  fileName: string;
  fileType: string;
  fileSize: number;
  storageURL: string;
  uploadedBy: string;
  uploadedAt: FirebaseFirestore.Timestamp;
}

export interface Comment {
  id: string;
  userId: string;
  userName: string;
  userPhotoURL?: string;
  text: string;
  createdAt: FirebaseFirestore.Timestamp;
  isEdited: boolean;
  editedAt?: FirebaseFirestore.Timestamp;
}

export interface TaskList {
  id: string;
  familyId: string;
  name: string;
  description?: string;
  icon?: string;
  color?: string;
  createdBy: string;
  createdAt: FirebaseFirestore.Timestamp;
  taskIds: string[];
  isArchived: boolean;
}

/**
 * Firestore Document Paths:
 * - tasks/{taskId}
 * - task_lists/{listId}
 *
 * Indexes Required:
 * - familyId + status + dueDate
 * - familyId + assignedTo + status
 * - familyId + priority + createdAt
 *
 * Security Rules:
 * - Only family members can read tasks
 * - Only assigned users and admins can update tasks
 * - All family members can create tasks
 */
export const TASK_COLLECTION = 'tasks';
export const TASK_LIST_COLLECTION = 'task_lists';
