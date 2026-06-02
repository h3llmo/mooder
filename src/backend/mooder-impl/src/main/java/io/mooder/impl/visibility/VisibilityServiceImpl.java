package io.mooder.impl.visibility;

import io.mooder.api.user.UserContext;
import io.mooder.api.visibility.VisibilityService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Quarkus CDI implementation of {@link VisibilityService}.
 *
 * Pattern codes are hashed with Argon2id before storage (ADR-020 / US-049).
 * Resolution is silent: wrong or missing pattern always defaults to "safe" view —
 * no error, no indication that a master pattern exists.
 *
 * TODO: wire to user_patterns table (US-049 implementation sprint).
 */
@ApplicationScoped
public class VisibilityServiceImpl implements VisibilityService {

    @Override
    public String resolveMode(UserContext caller, String patternHash) {
        if (patternHash == null || patternHash.isBlank()) {
            return "safe";
        }
        // TODO: look up caller's hashed patterns in user_patterns table
        // For now always return "safe" — no patterns registered yet
        return "safe";
    }

    @Override
    public void registerMasterPattern(UserContext caller, String patternHash) {
        // TODO: hash with Argon2id and persist to user_patterns (type='master')
        throw new UnsupportedOperationException("Not implemented — US-049");
    }

    @Override
    public void registerSafePattern(UserContext caller, String patternHash) {
        // TODO: hash with Argon2id and persist to user_patterns (type='safe')
        throw new UnsupportedOperationException("Not implemented — US-049");
    }
}
