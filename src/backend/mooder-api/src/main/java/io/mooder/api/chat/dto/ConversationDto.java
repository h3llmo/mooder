package io.mooder.api.chat.dto;

import java.time.Instant;

/**
 * Immutable DTO representing a conversation (1-on-1 thread).
 *
 * @param id          Unique conversation identifier
 * @param participantIds  Both participant user IDs
 * @param lastMessageAt   UTC timestamp of the most recent message
 * @param visibility      Conversation-level visibility category
 */
public record ConversationDto(
    String id,
    java.util.List<String> participantIds,
    Instant lastMessageAt,
    String visibility
) {}
