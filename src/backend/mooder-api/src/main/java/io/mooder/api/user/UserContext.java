package io.mooder.api.user;

/**
 * Represents the authenticated user making the current request.
 * Injected by the security layer; consumed by service interfaces.
 *
 * @param userId   Keycloak / Auth0 subject claim
 * @param username Display name
 * @param roles    Set of roles assigned to this user
 */
public record UserContext(
    String userId,
    String username,
    java.util.Set<String> roles
) {
    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
