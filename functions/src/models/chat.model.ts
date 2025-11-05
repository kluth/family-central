/**
 * Chat Model - Firestore Schema
 * Collections: chats, messages
 */

export enum ChatType {
  GROUP = 'group',
  DIRECT = 'direct',
}

export enum MessageType {
  TEXT = 'text',
  IMAGE = 'image',
  FILE = 'file',
  AUDIO = 'audio',
  SYSTEM = 'system',
}

export enum MessageStatus {
  SENDING = 'sending',
  SENT = 'sent',
  DELIVERED = 'delivered',
  READ = 'read',
  FAILED = 'failed',
}

export interface Chat {
  id: string;
  familyId: string;
  type: ChatType;
  name: string;
  description?: string;
  avatarURL?: string;
  participantIds: string[];
  participants: ChatParticipant[];
  createdBy: string;
  createdAt: FirebaseFirestore.Timestamp;
  updatedAt: FirebaseFirestore.Timestamp;
  lastMessage?: LastMessage;
  isArchived: boolean;
  pinnedBy: string[];
}

export interface ChatParticipant {
  uid: string;
  displayName: string;
  photoURL?: string;
  joinedAt: FirebaseFirestore.Timestamp;
  lastReadAt: FirebaseFirestore.Timestamp;
  unreadCount: number;
  isMuted: boolean;
  isTyping: boolean;
  lastTypingAt?: FirebaseFirestore.Timestamp;
}

export interface LastMessage {
  text: string;
  senderId: string;
  senderName: string;
  timestamp: FirebaseFirestore.Timestamp;
  type: MessageType;
}

export interface Message {
  id: string;
  chatId: string;
  familyId: string;
  senderId: string;
  senderName: string;
  senderPhotoURL?: string;
  type: MessageType;
  content: MessageContent;
  status: MessageStatus;
  timestamp: FirebaseFirestore.Timestamp;
  editedAt?: FirebaseFirestore.Timestamp;
  isEdited: boolean;
  isDeleted: boolean;
  deletedAt?: FirebaseFirestore.Timestamp;
  replyTo?: MessageReply;
  reactions: MessageReaction[];
  readBy: ReadReceipt[];
  deliveredTo: string[];
  mentions: string[];
}

export interface MessageContent {
  text?: string;
  mediaURL?: string;
  mediaType?: string;
  fileName?: string;
  fileSize?: number;
  thumbnailURL?: string;
  duration?: number;
}

export interface MessageReply {
  messageId: string;
  text: string;
  senderId: string;
  senderName: string;
}

export interface MessageReaction {
  emoji: string;
  userId: string;
  userName: string;
  timestamp: FirebaseFirestore.Timestamp;
}

export interface ReadReceipt {
  userId: string;
  userName: string;
  readAt: FirebaseFirestore.Timestamp;
}

export interface TypingIndicator {
  chatId: string;
  userId: string;
  userName: string;
  startedAt: FirebaseFirestore.Timestamp;
  expiresAt: FirebaseFirestore.Timestamp;
}

/**
 * Firestore Document Paths:
 * - chats/{chatId}
 * - chats/{chatId}/messages/{messageId}
 * - typing_indicators/{chatId}/users/{userId}
 *
 * Indexes Required:
 * - chatId + timestamp (descending) for message pagination
 * - familyId + participantIds for chat discovery
 * - chatId + senderId + timestamp for user message history
 *
 * Security Rules:
 * - Only participants can read/write chat messages
 * - Only sender can edit/delete their own messages
 * - All participants can add reactions
 */
export const CHAT_COLLECTION = 'chats';
export const MESSAGE_SUBCOLLECTION = 'messages';
export const TYPING_INDICATOR_COLLECTION = 'typing_indicators';
