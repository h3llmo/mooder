/**
 * Shared TypeScript types — mirrors backend DTOs (mooder-api).
 * Keep in sync with Java records in mooder-api module.
 */

export interface MessageDto {
  id: string;
  conversationId: string;
  senderId: string;
  content: string;
  sentAt: string; // ISO-8601
  visibility: "safe" | "private" | "ai-flagged";
}

export interface ConversationDto {
  id: string;
  participantIds: string[];
  lastMessageAt: string | null; // ISO-8601
  visibility: "safe" | "private" | "ai-flagged";
}

export type VisibilityMode = "full" | "safe";
