/**
 * Task Management API - Cloud Functions
 * HTTP Callable Functions for Task Operations
 */

import * as functions from 'firebase-functions';
import { Task, TaskStatus, TaskPriority, Subtask } from '../models';

// ============================================================================
// REQUEST/RESPONSE INTERFACES
// ============================================================================

export interface CreateTaskRequest {
  familyId: string;
  title: string;
  description?: string;
  priority: TaskPriority;
  assignedTo: string[];
  dueDate?: Date;
  tags?: string[];
  subtasks?: Omit<Subtask, 'id' | 'isCompleted' | 'completedAt' | 'completedBy'>[];
}

export interface CreateTaskResponse {
  success: boolean;
  taskId?: string;
  task?: Task;
  error?: string;
}

export interface UpdateTaskRequest {
  taskId: string;
  updates: {
    title?: string;
    description?: string;
    status?: TaskStatus;
    priority?: TaskPriority;
    assignedTo?: string[];
    dueDate?: Date;
    tags?: string[];
  };
}

export interface UpdateTaskResponse {
  success: boolean;
  task?: Task;
  error?: string;
}

export interface DeleteTaskRequest {
  taskId: string;
  familyId: string;
}

export interface DeleteTaskResponse {
  success: boolean;
  error?: string;
}

export interface GetTasksRequest {
  familyId: string;
  filters?: {
    status?: TaskStatus[];
    assignedTo?: string;
    priority?: TaskPriority[];
    dueDateRange?: {
      start: Date;
      end: Date;
    };
    tags?: string[];
  };
  pagination?: {
    limit: number;
    startAfter?: string;
  };
}

export interface GetTasksResponse {
  success: boolean;
  tasks: Task[];
  nextPageToken?: string;
  error?: string;
}

export interface ToggleSubtaskRequest {
  taskId: string;
  subtaskId: string;
  isCompleted: boolean;
}

export interface ToggleSubtaskResponse {
  success: boolean;
  task?: Task;
  error?: string;
}

export interface AddTaskCommentRequest {
  taskId: string;
  text: string;
}

export interface AddTaskCommentResponse {
  success: boolean;
  commentId?: string;
  error?: string;
}

export interface AssignTaskRequest {
  taskId: string;
  assigneeIds: string[];
  notifyUsers: boolean;
}

export interface AssignTaskResponse {
  success: boolean;
  task?: Task;
  error?: string;
}

// ============================================================================
// FUNCTION EXPORTS (Implementation in services layer)
// ============================================================================

/**
 * Create a new task
 * @callable
 */
export const createTask = functions.https.onCall(
  async (data: CreateTaskRequest, context): Promise<CreateTaskResponse> => {
    // Implementation to be added
    throw new functions.https.HttpsError('unimplemented', 'Function not yet implemented');
  }
);

/**
 * Update an existing task
 * @callable
 */
export const updateTask = functions.https.onCall(
  async (data: UpdateTaskRequest, context): Promise<UpdateTaskResponse> => {
    // Implementation to be added
    throw new functions.https.HttpsError('unimplemented', 'Function not yet implemented');
  }
);

/**
 * Delete a task
 * @callable
 */
export const deleteTask = functions.https.onCall(
  async (data: DeleteTaskRequest, context): Promise<DeleteTaskResponse> => {
    // Implementation to be added
    throw new functions.https.HttpsError('unimplemented', 'Function not yet implemented');
  }
);

/**
 * Get tasks with filtering and pagination
 * @callable
 */
export const getTasks = functions.https.onCall(
  async (data: GetTasksRequest, context): Promise<GetTasksResponse> => {
    // Implementation to be added
    throw new functions.https.HttpsError('unimplemented', 'Function not yet implemented');
  }
);

/**
 * Toggle subtask completion status
 * @callable
 */
export const toggleSubtask = functions.https.onCall(
  async (data: ToggleSubtaskRequest, context): Promise<ToggleSubtaskResponse> => {
    // Implementation to be added
    throw new functions.https.HttpsError('unimplemented', 'Function not yet implemented');
  }
);

/**
 * Add comment to task
 * @callable
 */
export const addTaskComment = functions.https.onCall(
  async (data: AddTaskCommentRequest, context): Promise<AddTaskCommentResponse> => {
    // Implementation to be added
    throw new functions.https.HttpsError('unimplemented', 'Function not yet implemented');
  }
);

/**
 * Assign task to users
 * @callable
 */
export const assignTask = functions.https.onCall(
  async (data: AssignTaskRequest, context): Promise<AssignTaskResponse> => {
    // Implementation to be added
    throw new functions.https.HttpsError('unimplemented', 'Function not yet implemented');
  }
);
