/**
 * Family Model - Firestore Schema
 * Collection: families
 */

import { UserRole } from './user.model';

export interface Family {
  id: string;
  name: string;
  description?: string;
  avatarURL?: string;
  createdAt: FirebaseFirestore.Timestamp;
  createdBy: string;
  members: FamilyMember[];
  settings: FamilySettings;
  inviteCode: string;
  inviteCodeExpiry: FirebaseFirestore.Timestamp;
}

export interface FamilyMember {
  uid: string;
  email: string;
  displayName: string;
  photoURL?: string;
  role: UserRole;
  joinedAt: FirebaseFirestore.Timestamp;
  isActive: boolean;
}

export interface FamilySettings {
  allowMemberInvites: boolean;
  requireApprovalForNewMembers: boolean;
  defaultTaskVisibility: 'all' | 'assigned_only';
  enableAI: boolean;
  enableWeeklySummary: boolean;
}

export interface FamilyInvite {
  id: string;
  familyId: string;
  familyName: string;
  inviteCode: string;
  createdBy: string;
  createdByName: string;
  createdAt: FirebaseFirestore.Timestamp;
  expiresAt: FirebaseFirestore.Timestamp;
  maxUses: number;
  currentUses: number;
  isActive: boolean;
}

/**
 * Firestore Document Paths:
 * - families/{familyId}
 * - families/{familyId}/invites/{inviteId}
 *
 * Security Rules:
 * - Only family members can read family data
 * - Only admins can update family settings
 * - Only admins can create invites
 */
export const FAMILY_COLLECTION = 'families';
export const INVITE_SUBCOLLECTION = 'invites';
