package io.mooder.api.chat;

import io.mooder.api.chat.dto.ConversationDto;
import io.mooder.api.chat.dto.MessageDto;
import io.mooder.api.user.UserContext;

import java.util.List;

/**
 * Contract for chat operations.
 *
 * Implementations live in mooder-impl. A fake/mock implementation
 * can be provided in test modules without pulling in Quarkus or a database.
 */
public interface ChatService {

    /**
     * Returns conversations visible to the caller.
     * When the caller is in safe-view mode, hidden conversations are excluded.
     *
     * @param caller      Authenticated user context (determines visibility scope)
     * @param visibilityMode  {@code "full"} or {@code "safe"}
     */
    List<ConversationDto> listConversations(UserContext caller, String visibilityMode);

    /**
     * Returns messages in a conversation visible to the caller.
     *
     * @param conversationId  Target conversation
     * @param caller          Authenticated user
     * @param visibilityMode  {@code "full"} or {@code "safe"}
     */
    List<MessageDto> listMessages(String conversationId, UserContext caller, String visibilityMode);

    /**
     * Sends a new message.
     *
     * @param conversationId Target conversation
     * @param content        Message text
     * @param caller         Sender context
     * @return Persisted message DTO
     */
    MessageDto sendMessage(String conversationId, String content, UserContext caller);
}
