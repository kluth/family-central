/**
 * AI Integration API - Cloud Functions
 * NLP-based task extraction and AI suggestions
 */

import * as functions from 'firebase-functions';
import { AIIntent, AITaskRequest, ExtractedTaskData } from '../models';

// ============================================================================
// REQUEST/RESPONSE INTERFACES
// ============================================================================

export interface ExtractTaskFromTextRequest {
  familyId: string;
  text: string;
  sourceType: 'chat' | 'voice' | 'manual';
  sourceId?: string;
  context?: {
    chatHistory?: string[];
    currentUserId: string;
    familyMembers: Array<{ uid: string; displayName: string }>;
  };
}

export interface ExtractTaskFromTextResponse {
  success: boolean;
  intent: AIIntent;
  extractedData?: ExtractedTaskData;
  confidence: number;
  requiresConfirmation: boolean;
  suggestedResponse?: string;
  aiTaskId?: string;
  error?: string;
}

export interface ConfirmAITaskRequest {
  aiTaskId: string;
  confirmed: boolean;
  modifications?: Partial<ExtractedTaskData>;
}

export interface ConfirmAITaskResponse {
  success: boolean;
  taskId?: string;
  error?: string;
}

export interface GetTaskSuggestionsRequest {
  familyId: string;
  userId: string;
  limit?: number;
}

export interface GetTaskSuggestionsResponse {
  success: boolean;
  suggestions: Array<{
    id: string;
    title: string;
    description?: string;
    assignee?: string;
    dueDate?: Date;
    confidence: number;
    basedOn: string[];
  }>;
  error?: string;
}

export interface GenerateWeeklySummaryRequest {
  familyId: string;
  weekStartDate: Date;
}

export interface GenerateWeeklySummaryResponse {
  success: boolean;
  summaryId?: string;
  summary?: {
    text: string;
    stats: any;
    highlights: any[];
  };
  error?: string;
}

export interface ChatWithAIRequest {
  familyId: string;
  chatId: string;
  messageText: string;
  conversationHistory?: Array<{
    role: 'user' | 'assistant';
    content: string;
  }>;
}

export interface ChatWithAIResponse {
  success: boolean;
  reply: string;
  suggestedActions?: Array<{
    type: 'create_task' | 'create_event' | 'add_shopping_item';
    data: any;
  }>;
  error?: string;
}

// ============================================================================
// FUNCTION EXPORTS
// ============================================================================

/**
 * Extract task information from natural language text
 * Uses NLP to identify intent and extract structured data
 * @callable
 */
export const extractTaskFromText = functions.https.onCall(
  async (data: ExtractTaskFromTextRequest, context): Promise<ExtractTaskFromTextResponse> => {
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }

    // Validate input
    if (!data.text || data.text.trim().length === 0) {
      throw new functions.https.HttpsError('invalid-argument', 'Text cannot be empty');
    }

    // Implementation will use AI/NLP service (OpenAI, Vertex AI, etc.)
    // For TDD, this will be implemented after tests are written
    throw new functions.https.HttpsError('unimplemented', 'Function not yet implemented');
  }
);

/**
 * Confirm and create task from AI extraction
 * @callable
 */
export const confirmAITask = functions.https.onCall(
  async (data: ConfirmAITaskRequest, context): Promise<ConfirmAITaskResponse> => {
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }

    // Implementation to be added
    throw new functions.https.HttpsError('unimplemented', 'Function not yet implemented');
  }
);

/**
 * Get AI-powered task suggestions based on user history and patterns
 * @callable
 */
export const getTaskSuggestions = functions.https.onCall(
  async (data: GetTaskSuggestionsRequest, context): Promise<GetTaskSuggestionsResponse> => {
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }

    // Implementation to be added
    throw new functions.https.HttpsError('unimplemented', 'Function not yet implemented');
  }
);

/**
 * Generate weekly summary with AI insights
 * @callable
 */
export const generateWeeklySummary = functions.https.onCall(
  async (data: GenerateWeeklySummaryRequest, context): Promise<GenerateWeeklySummaryResponse> => {
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }

    // Implementation to be added
    throw new functions.https.HttpsError('unimplemented', 'Function not yet implemented');
  }
);

/**
 * Chat with AI assistant for family management help
 * @callable
 */
export const chatWithAI = functions.https.onCall(
  async (data: ChatWithAIRequest, context): Promise<ChatWithAIResponse> => {
    if (!context.auth) {
      throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
    }

    // Implementation to be added
    throw new functions.https.HttpsError('unimplemented', 'Function not yet implemented');
  }
);

// ============================================================================
// SCHEDULED FUNCTIONS
// ============================================================================

/**
 * Generate weekly summaries for all families (Cron job)
 * Runs every Sunday at 8 PM
 */
export const scheduledWeeklySummary = functions.pubsub
  .schedule('0 20 * * 0')
  .timeZone('America/New_York')
  .onRun(async (context) => {
    // Implementation to be added
    console.log('Scheduled weekly summary generation started');
  });

/**
 * Generate daily task suggestions (Cron job)
 * Runs every day at 6 AM
 */
export const scheduledDailyTaskSuggestions = functions.pubsub
  .schedule('0 6 * * *')
  .timeZone('America/New_York')
  .onRun(async (context) => {
    // Implementation to be added
    console.log('Scheduled daily task suggestions started');
  });
