# Cloud Functions API Documentation

## Overview

FamilyHub Platform uses Firebase Cloud Functions (TypeScript) for backend logic, including:
- HTTP Callable Functions for client-server communication
- Firestore Triggers for real-time event processing
- Scheduled Functions for periodic tasks (cron jobs)
- FCM notification delivery

## Authentication

All callable functions require Firebase Authentication. The `context.auth` object contains the authenticated user's information.

## API Endpoints

### Task Management API

#### `createTask`
**Type**: HTTP Callable
**Auth**: Required

Creates a new task in a family.

**Request**:
```typescript
{
  familyId: string;
  title: string;
  description?: string;
  priority: 'low' | 'medium' | 'high' | 'urgent';
  assignedTo: string[];
  dueDate?: Date;
  tags?: string[];
  subtasks?: Array<{title: string; order: number}>;
}
```

**Response**:
```typescript
{
  success: boolean;
  taskId?: string;
  task?: Task;
  error?: string;
}
```

#### `updateTask`
**Type**: HTTP Callable
**Auth**: Required

Updates an existing task.

#### `getTasks`
**Type**: HTTP Callable
**Auth**: Required

Retrieves tasks with filtering and pagination.

---

### AI Integration API

#### `extractTaskFromText`
**Type**: HTTP Callable
**Auth**: Required

Extracts task information from natural language text using NLP.

**Request**:
```typescript
{
  familyId: string;
  text: string;
  sourceType: 'chat' | 'voice' | 'manual';
  sourceId?: string;
  context?: {
    chatHistory?: string[];
    currentUserId: string;
    familyMembers: Array<{uid: string; displayName: string}>;
  };
}
```

**Response**:
```typescript
{
  success: boolean;
  intent: 'create_task' | 'create_event' | 'add_shopping_item' | 'query_info' | 'unknown';
  extractedData?: {
    title?: string;
    description?: string;
    assignedTo?: string[];
    dueDate?: Date;
    priority?: string;
  };
  confidence: number;
  requiresConfirmation: boolean;
  suggestedResponse?: string;
  aiTaskId?: string;
  error?: string;
}
```

#### `getTaskSuggestions`
**Type**: HTTP Callable
**Auth**: Required

Returns AI-powered task suggestions based on user history and patterns.

---

### Chat API

#### `createChat`
**Type**: HTTP Callable
**Auth**: Required

Creates a new chat (group or direct message).

#### `sendMessage`
**Type**: HTTP Callable
**Auth**: Required

Sends a message to a chat.

#### `markMessagesAsRead`
**Type**: HTTP Callable
**Auth**: Required

Marks messages as read and updates read receipts.

---

## Firestore Triggers

### Task Triggers

#### `onTaskCreated`
**Trigger**: `tasks/{taskId}` onCreate

Automatically:
- Sends notifications to assigned users
- Logs task creation activity

#### `onTaskUpdated`
**Trigger**: `tasks/{taskId}` onUpdate

Automatically:
- Notifies on status changes (especially completion)
- Sends notifications to newly assigned users

### Notification Triggers

#### `onNotificationCreated`
**Trigger**: `notifications/{notificationId}` onCreate

Automatically:
- Retrieves user's FCM token
- Sends push notification via FCM
- Updates notification status
- Removes invalid FCM tokens

### Chat Triggers

(To be implemented)

---

## Scheduled Functions (Cron Jobs)

### `checkTasksDueSoon`
**Schedule**: Every hour (`0 * * * *`)
**Timezone**: America/New_York

Checks for tasks due within 24 hours and sends reminder notifications.

### `scheduledWeeklySummary`
**Schedule**: Every Sunday at 8 PM (`0 20 * * 0`)
**Timezone**: America/New_York

Generates weekly summaries for all families with AI insights.

### `scheduledDailyTaskSuggestions`
**Schedule**: Every day at 6 AM (`0 6 * * *`)
**Timezone**: America/New_York

Generates daily task suggestions based on user patterns.

### `cleanupOldNotifications`
**Schedule**: Every day at 2 AM (`0 2 * * *`)
**Timezone**: America/New_York

Deletes notifications older than 30 days.

---

## Error Handling

All callable functions use standard Firebase error codes:

- `unauthenticated`: User not authenticated
- `permission-denied`: User lacks required permissions
- `invalid-argument`: Invalid request parameters
- `not-found`: Resource not found
- `already-exists`: Resource already exists
- `failed-precondition`: Operation not allowed in current state
- `unimplemented`: Function not yet implemented

**Example Error Response**:
```typescript
{
  success: false,
  error: "Invalid task ID provided"
}
```

---

## Rate Limiting

- Callable functions: 100 requests/minute per user
- Trigger functions: No limit (automatic scaling)
- Scheduled functions: Configured per function

---

## Testing

### Unit Tests
Located in `functions/test/unit/`
- Test business logic in isolation
- Mock Firestore and Firebase Admin SDK

### Integration Tests
Located in `functions/test/integration/`
- Use Firebase Emulator Suite
- Test full function execution with real Firestore

### Running Tests
```bash
cd functions
npm test                    # Run all tests
npm run test:unit          # Unit tests only
npm run test:integration   # Integration tests with emulator
npm run test:coverage      # Coverage report
```

---

## Deployment

### Deploy All Functions
```bash
firebase deploy --only functions
```

### Deploy Specific Function
```bash
firebase deploy --only functions:createTask
```

### Environment Variables
Set via Firebase config:
```bash
firebase functions:config:set ai.api_key="YOUR_API_KEY"
```

Access in code:
```typescript
const apiKey = functions.config().ai.api_key;
```

---

## Monitoring

- **Cloud Console**: [Firebase Console](https://console.firebase.google.com)
- **Logs**: View in Cloud Functions dashboard
- **Metrics**: Monitor invocations, errors, and latency
- **Alerts**: Set up alerts for error rates and response times
