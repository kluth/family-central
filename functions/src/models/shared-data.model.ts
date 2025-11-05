/**
 * Shared Data Models - Firestore Schema
 * Collections: calendar_events, shopping_lists, shared_contacts, documents
 */

// ============================================================================
// CALENDAR EVENTS
// ============================================================================

export enum EventType {
  FAMILY_EVENT = 'family_event',
  BIRTHDAY = 'birthday',
  ANNIVERSARY = 'anniversary',
  APPOINTMENT = 'appointment',
  REMINDER = 'reminder',
  TASK_DEADLINE = 'task_deadline',
}

export enum RSVPStatus {
  GOING = 'going',
  NOT_GOING = 'not_going',
  MAYBE = 'maybe',
  NO_RESPONSE = 'no_response',
}

export interface CalendarEvent {
  id: string;
  familyId: string;
  title: string;
  description?: string;
  location?: string;
  eventType: EventType;
  startDate: FirebaseFirestore.Timestamp;
  endDate: FirebaseFirestore.Timestamp;
  isAllDay: boolean;
  createdBy: string;
  createdByName: string;
  createdAt: FirebaseFirestore.Timestamp;
  updatedAt: FirebaseFirestore.Timestamp;
  attendees: EventAttendee[];
  reminders: EventReminder[];
  recurrence?: RecurrenceConfig;
  color?: string;
  linkedTaskId?: string;
}

export interface EventAttendee {
  uid: string;
  displayName: string;
  photoURL?: string;
  rsvpStatus: RSVPStatus;
  rsvpAt?: FirebaseFirestore.Timestamp;
  note?: string;
}

export interface EventReminder {
  minutesBefore: number;
  sent: boolean;
  sentAt?: FirebaseFirestore.Timestamp;
}

export interface RecurrenceConfig {
  frequency: 'daily' | 'weekly' | 'monthly' | 'yearly';
  interval: number;
  daysOfWeek?: number[];
  endDate?: FirebaseFirestore.Timestamp;
}

// ============================================================================
// SHOPPING LISTS
// ============================================================================

export enum ItemCategory {
  GROCERIES = 'groceries',
  HOUSEHOLD = 'household',
  PHARMACY = 'pharmacy',
  ELECTRONICS = 'electronics',
  CLOTHING = 'clothing',
  OTHER = 'other',
}

export interface ShoppingList {
  id: string;
  familyId: string;
  name: string;
  description?: string;
  icon?: string;
  createdBy: string;
  createdAt: FirebaseFirestore.Timestamp;
  updatedAt: FirebaseFirestore.Timestamp;
  items: ShoppingItem[];
  isArchived: boolean;
  sharedWith: string[];
}

export interface ShoppingItem {
  id: string;
  name: string;
  quantity: number;
  unit?: string;
  category: ItemCategory;
  note?: string;
  price?: number;
  imageURL?: string;
  isPurchased: boolean;
  purchasedBy?: string;
  purchasedAt?: FirebaseFirestore.Timestamp;
  addedBy: string;
  addedByName: string;
  addedAt: FirebaseFirestore.Timestamp;
  priority: number;
}

// ============================================================================
// SHARED CONTACTS
// ============================================================================

export enum ContactType {
  FAMILY = 'family',
  FRIEND = 'friend',
  DOCTOR = 'doctor',
  SCHOOL = 'school',
  SERVICE_PROVIDER = 'service_provider',
  EMERGENCY = 'emergency',
  OTHER = 'other',
}

export interface SharedContact {
  id: string;
  familyId: string;
  firstName: string;
  lastName: string;
  displayName: string;
  contactType: ContactType;
  organization?: string;
  jobTitle?: string;
  photoURL?: string;
  phoneNumbers: PhoneNumber[];
  emails: Email[];
  addresses: Address[];
  notes?: string;
  tags: string[];
  isFavorite: boolean;
  createdBy: string;
  createdAt: FirebaseFirestore.Timestamp;
  updatedAt: FirebaseFirestore.Timestamp;
}

export interface PhoneNumber {
  type: 'mobile' | 'home' | 'work' | 'other';
  number: string;
  isPrimary: boolean;
}

export interface Email {
  type: 'personal' | 'work' | 'other';
  address: string;
  isPrimary: boolean;
}

export interface Address {
  type: 'home' | 'work' | 'other';
  street: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
  isPrimary: boolean;
}

// ============================================================================
// DOCUMENT VAULT
// ============================================================================

export enum DocumentType {
  PDF = 'pdf',
  IMAGE = 'image',
  SPREADSHEET = 'spreadsheet',
  TEXT = 'text',
  OTHER = 'other',
}

export enum DocumentCategory {
  MEDICAL = 'medical',
  FINANCIAL = 'financial',
  LEGAL = 'legal',
  EDUCATION = 'education',
  INSURANCE = 'insurance',
  PERSONAL = 'personal',
  OTHER = 'other',
}

export interface Document {
  id: string;
  familyId: string;
  name: string;
  description?: string;
  documentType: DocumentType;
  category: DocumentCategory;
  fileURL: string;
  thumbnailURL?: string;
  fileName: string;
  fileSize: number;
  mimeType: string;
  uploadedBy: string;
  uploadedByName: string;
  uploadedAt: FirebaseFirestore.Timestamp;
  updatedAt: FirebaseFirestore.Timestamp;
  tags: string[];
  sharedWith: string[];
  isArchived: boolean;
  expiryDate?: FirebaseFirestore.Timestamp;
  metadata?: DocumentMetadata;
}

export interface DocumentMetadata {
  pageCount?: number;
  ocrText?: string;
  aiSummary?: string;
  relatedContacts?: string[];
  relatedEvents?: string[];
}

// ============================================================================
// COLLECTION NAMES
// ============================================================================

export const CALENDAR_COLLECTION = 'calendar_events';
export const SHOPPING_LIST_COLLECTION = 'shopping_lists';
export const SHARED_CONTACT_COLLECTION = 'shared_contacts';
export const DOCUMENT_COLLECTION = 'documents';

/**
 * Indexes Required:
 * - calendar_events: familyId + startDate
 * - shopping_lists: familyId + isArchived
 * - shared_contacts: familyId + contactType + displayName
 * - documents: familyId + category + uploadedAt
 *
 * Security Rules:
 * - All collections: Only family members can read/write
 * - Documents: Check sharedWith array for access control
 * - Contacts: Emergency contacts readable by all family members
 */
