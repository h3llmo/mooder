package io.mooder.api.chat.dto;

import java.time.Instant;

/**
 * Immutable DTO representing a single chat message.
 * Visibility field is mandatory from day one (ADR-020 / US-040).
 *
 * @param id             Unique message identifier
 * @param conversationId Parent conversation
 * @param senderId       User ID of the sender
 * @param content        Plain text content (encrypted at rest — returned decrypted to authorised caller)
 * @param sentAt         UTC timestamp
 * @param visibility     Content visibility category: {@code safe}, {@code private}, {@code ai-flagged}
 */
public record MessageDto(
    String id,
    String conversationId,
    String senderId,
    String content,
    Instant sentAt,
    String visibility
) {}
