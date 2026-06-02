-- Mooder — V1 Initial schema
-- Flyway migration: append-only. Never edit after merge (ADR-010).

-- ── Users ─────────────────────────────────────────────────────────────────
-- Lightweight profile table; authentication lives in the IdP (Keycloak/Auth0).
CREATE TABLE users (
    id          VARCHAR(36)  PRIMARY KEY,         -- matches IdP subject claim
    username    VARCHAR(100) NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL UNIQUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ── Conversations ─────────────────────────────────────────────────────────
CREATE TABLE conversations (
    id              VARCHAR(36)  PRIMARY KEY,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    last_message_at TIMESTAMPTZ,
    -- Selective visibility (ADR-020 / US-040)
    -- Values: 'safe' | 'private' | 'ai-flagged'
    visibility      VARCHAR(20)  NOT NULL DEFAULT 'safe'
);

-- Participants (1-on-1 for Phase 1; table supports N for future group chat)
CREATE TABLE conversation_participants (
    conversation_id VARCHAR(36) NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    user_id         VARCHAR(36) NOT NULL REFERENCES users(id)          ON DELETE CASCADE,
    joined_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (conversation_id, user_id)
);

-- ── Messages ──────────────────────────────────────────────────────────────
CREATE TABLE messages (
    id              VARCHAR(36)  PRIMARY KEY,
    conversation_id VARCHAR(36)  NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_id       VARCHAR(36)  NOT NULL REFERENCES users(id),
    content         TEXT         NOT NULL,          -- stored encrypted at rest (ADR-013)
    sent_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    -- Selective visibility (ADR-020 / US-040)
    visibility      VARCHAR(20)  NOT NULL DEFAULT 'safe'
);

CREATE INDEX idx_messages_conversation ON messages(conversation_id, sent_at DESC);
CREATE INDEX idx_messages_visibility   ON messages(conversation_id, visibility);

-- ── Draw patterns (ADR-020) ───────────────────────────────────────────────
-- Pattern codes are NEVER stored in plaintext — only Argon2id hashes.
CREATE TABLE user_patterns (
    id          VARCHAR(36)  PRIMARY KEY,
    user_id     VARCHAR(36)  NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    pattern_type VARCHAR(10) NOT NULL CHECK (pattern_type IN ('master', 'safe')),
    hash        VARCHAR(255) NOT NULL,              -- Argon2id hash
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (user_id, pattern_type)                  -- one master per user; safe allows multiple via separate rows
);

-- ── Roles ─────────────────────────────────────────────────────────────────
CREATE TABLE user_roles (
    user_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role    VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role)
);
