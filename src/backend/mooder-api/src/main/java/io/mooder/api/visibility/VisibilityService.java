package io.mooder.api.visibility;

import io.mooder.api.user.UserContext;

/**
 * Contract for the draw-pattern selective visibility system (ADR-020).
 *
 * Determines which visibility mode the caller is in based on the
 * drawn pattern hash presented with their request.
 */
public interface VisibilityService {

    /**
     * Resolves the visibility mode for a given caller.
     *
     * @param caller      Authenticated user
     * @param patternHash SHA-256 hash of the drawn pattern code (never plaintext)
     * @return {@code "full"} if the hash matches the master pattern,
     *         {@code "safe"} if it matches a safe pattern,
     *         {@code "safe"} if no pattern is provided (default)
     */
    String resolveMode(UserContext caller, String patternHash);

    /**
     * Registers the master drawn pattern for a user.
     * Called once during the opt-in setup flow.
     *
     * @param caller      Authenticated user
     * @param patternHash Argon2id hash of the drawn pattern
     */
    void registerMasterPattern(UserContext caller, String patternHash);

    /**
     * Registers an additional safe pattern for a user.
     * Never surfaced in onboarding — user-initiated only.
     *
     * @param caller      Authenticated user
     * @param patternHash Argon2id hash of the new safe pattern
     */
    void registerSafePattern(UserContext caller, String patternHash);
}
