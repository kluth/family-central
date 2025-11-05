/**
 * User Model - Firestore Schema
 * Collection: users
 */

export enum UserRole {
  ADMIN = 'admin',
  MEMBER = 'member',
}

export interface UserProfile {
  uid: string;
  email: string;
  displayName: string;
  photoURL?: string;
  phoneNumber?: string;
  createdAt: FirebaseFirestore.Timestamp;
  updatedAt: FirebaseFirestore.Timestamp;
  familyMemberships: FamilyMembership[];
  notificationPreferences: NotificationPreferences;
  isActive: boolean;
}

export interface FamilyMembership {
  familyId: string;
  familyName: string;
  role: UserRole;
  joinedAt: FirebaseFirestore.Timestamp;
  invitedBy?: string;
}

export interface NotificationPreferences {
  taskAssignments: boolean;
  taskReminders: boolean;
  chatMessages: boolean;
  calendarEvents: boolean;
  weeklyDigest: boolean;
  fcmToken?: string;
}

/**
 * Firestore Document Path: users/{userId}
 *
 * Security Rules:
 * - User can read/write their own document
 * - Family members can read basic profile info
 */
export const USER_COLLECTION = 'users';
