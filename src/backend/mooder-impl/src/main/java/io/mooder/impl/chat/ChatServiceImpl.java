package io.mooder.impl.chat;

import io.mooder.api.chat.ChatService;
import io.mooder.api.chat.dto.ConversationDto;
import io.mooder.api.chat.dto.MessageDto;
import io.mooder.api.user.UserContext;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Quarkus CDI implementation of {@link ChatService}.
 *
 * TODO: replace in-memory stubs with Panache repository calls once
 *       the entity layer is wired (EP-002 implementation sprint).
 */
@ApplicationScoped
public class ChatServiceImpl implements ChatService {

    @Override
    public List<ConversationDto> listConversations(UserContext caller, String visibilityMode) {
        // Stub — visibility-scoped query will filter by mode (ADR-020 / US-046)
        return List.of();
    }

    @Override
    public List<MessageDto> listMessages(String conversationId, UserContext caller, String visibilityMode) {
        // Stub — returns empty list; visibility filter applied server-side
        return List.of();
    }

    @Override
    public MessageDto sendMessage(String conversationId, String content, UserContext caller) {
        // Stub — persist and broadcast via WebSocket (ADR-008) in implementation sprint
        return new MessageDto(
            UUID.randomUUID().toString(),
            conversationId,
            caller.userId(),
            content,
            Instant.now(),
            "safe"
        );
    }
}
