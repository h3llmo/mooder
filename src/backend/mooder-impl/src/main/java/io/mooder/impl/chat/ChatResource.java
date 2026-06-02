package io.mooder.impl.chat;

import io.mooder.api.chat.ChatService;
import io.mooder.api.chat.dto.ConversationDto;
import io.mooder.api.chat.dto.MessageDto;
import io.mooder.api.user.UserContext;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

/**
 * REST resource for chat operations.
 * All routes are under /api/v1/chat (ADR-009 / ADR-017).
 *
 * @RunOnVirtualThread: each request runs on a Java 21 virtual thread (ADR-021).
 * Blocking JDBC calls release the underlying OS thread during I/O — no Mutiny needed.
 *
 * Authentication: OIDC bearer token required.
 * Visibility mode: resolved from X-Mooder-Pattern header (see VisibilityFilter).
 */
@Path("/api/v1/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RunOnVirtualThread
@Tag(name = "Chat", description = "Phase 1 chat operations")
public class ChatResource {

    @Inject
    ChatService chatService;

    @GET
    @Path("/conversations")
    @Operation(summary = "List conversations visible to the caller")
    public List<ConversationDto> listConversations(
        @HeaderParam("X-Mooder-Pattern") String patternHash
    ) {
        UserContext caller = resolveCallerStub();
        String mode = resolveMode(patternHash);
        return chatService.listConversations(caller, mode);
    }

    @GET
    @Path("/conversations/{conversationId}/messages")
    @Operation(summary = "List messages in a conversation")
    public List<MessageDto> listMessages(
        @PathParam("conversationId") String conversationId,
        @HeaderParam("X-Mooder-Pattern") String patternHash
    ) {
        UserContext caller = resolveCallerStub();
        String mode = resolveMode(patternHash);
        return chatService.listMessages(conversationId, caller, mode);
    }

    @POST
    @Path("/conversations/{conversationId}/messages")
    @Operation(summary = "Send a message")
    public Response sendMessage(
        @PathParam("conversationId") String conversationId,
        SendMessageRequest request
    ) {
        UserContext caller = resolveCallerStub();
        MessageDto sent = chatService.sendMessage(conversationId, request.content(), caller);
        return Response.status(Response.Status.CREATED).entity(sent).build();
    }

    // ── Temporary stubs — replace with @Context SecurityContext injection ──

    private UserContext resolveCallerStub() {
        return new UserContext("stub-user-id", "stub-user", java.util.Set.of("user"));
    }

    private String resolveMode(String patternHash) {
        // TODO delegate to VisibilityService (US-046)
        return patternHash != null ? "full" : "safe";
    }

    public record SendMessageRequest(String content) {}
}
