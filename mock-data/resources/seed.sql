-- ─────────────────────────────────────────────────────────────────────────────
-- Mooder — Local development seed data
-- Runs via the db-seeder service AFTER Flyway migrations complete.
-- IDs are stable and match the Keycloak realm (mock-data/keycloak/mooder-realm.json).
--
-- Data split:
--   IdP (Keycloak) : id, username, firstName, lastName, email, phone, locale, roles
--   DB (PostgreSQL): id, username, email + app-specific profile data
--
-- Profile/preferences tables (bio, city, avatarColor, etc.) are seeded here
-- as comments for now — those tables will be added in a future migration (EP-002).
-- ─────────────────────────────────────────────────────────────────────────────

-- Prevent duplicate seeding on container restart
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM users WHERE id = 'a0000000-0000-0000-0000-000000000001') THEN
    RAISE NOTICE 'Seed data already present — skipping.';
    RETURN;
  END IF;

  -- ── Users ────────────────────────────────────────────────────────────────
  INSERT INTO users (id, username, email, created_at, updated_at) VALUES
    (
      'a0000000-0000-0000-0000-000000000001',
      'alice.martin',
      'alice.martin@mooder.local',
      '2026-01-01 10:00:00+00',
      '2026-01-01 10:00:00+00'
    ),
    (
      'a0000000-0000-0000-0000-000000000002',
      'thomas.dubois',
      'thomas.dubois@mooder.local',
      '2026-01-02 11:00:00+00',
      '2026-01-02 11:00:00+00'
    ),
    (
      'a0000000-0000-0000-0000-000000000003',
      'sofia.garcia',
      'sofia.garcia@mooder.local',
      '2026-01-03 09:30:00+00',
      '2026-01-03 09:30:00+00'
    ),
    (
      'a0000000-0000-0000-0000-000000000099',
      'admin',
      'admin@mooder.local',
      '2026-01-01 08:00:00+00',
      '2026-01-01 08:00:00+00'
    );

  -- ── User roles ───────────────────────────────────────────────────────────
  INSERT INTO user_roles (user_id, role) VALUES
    ('a0000000-0000-0000-0000-000000000001', 'user'),
    ('a0000000-0000-0000-0000-000000000002', 'user'),
    ('a0000000-0000-0000-0000-000000000003', 'user'),
    ('a0000000-0000-0000-0000-000000000099', 'user'),
    ('a0000000-0000-0000-0000-000000000099', 'admin');

  -- ── Sample conversations (alice ↔ thomas, alice ↔ sofia) ─────────────────
  INSERT INTO conversations (id, created_at, last_message_at, visibility) VALUES
    ('c0000000-0000-0000-0000-000000000001', '2026-01-10 09:00:00+00', '2026-01-10 09:05:00+00', 'safe'),
    ('c0000000-0000-0000-0000-000000000002', '2026-01-11 14:00:00+00', '2026-01-11 14:10:00+00', 'safe');

  INSERT INTO conversation_participants (conversation_id, user_id) VALUES
    ('c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001'),
    ('c0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000002'),
    ('c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000001'),
    ('c0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000003');

  -- ── Sample messages ───────────────────────────────────────────────────────
  -- alice ↔ thomas
  INSERT INTO messages (id, conversation_id, sender_id, content, sent_at, visibility) VALUES
    (
      'm0000000-0000-0000-0000-000000000001',
      'c0000000-0000-0000-0000-000000000001',
      'a0000000-0000-0000-0000-000000000001',
      'Hey Thomas! Are you coming to the meetup on Friday?',
      '2026-01-10 09:00:00+00',
      'safe'
    ),
    (
      'm0000000-0000-0000-0000-000000000002',
      'c0000000-0000-0000-0000-000000000001',
      'a0000000-0000-0000-0000-000000000002',
      'Hey! Yes, I should be there around 7pm. You?',
      '2026-01-10 09:03:00+00',
      'safe'
    ),
    (
      'm0000000-0000-0000-0000-000000000003',
      'c0000000-0000-0000-0000-000000000001',
      'a0000000-0000-0000-0000-000000000001',
      'Perfect, me too. See you there!',
      '2026-01-10 09:05:00+00',
      'safe'
    );

  -- alice ↔ sofia
  INSERT INTO messages (id, conversation_id, sender_id, content, sent_at, visibility) VALUES
    (
      'm0000000-0000-0000-0000-000000000004',
      'c0000000-0000-0000-0000-000000000002',
      'a0000000-0000-0000-0000-000000000003',
      'Alice! I just finished the new design mockups 🎨',
      '2026-01-11 14:00:00+00',
      'safe'
    ),
    (
      'm0000000-0000-0000-0000-000000000005',
      'c0000000-0000-0000-0000-000000000002',
      'a0000000-0000-0000-0000-000000000001',
      'No way, already?? Send them!!',
      '2026-01-11 14:05:00+00',
      'safe'
    ),
    (
      'm0000000-0000-0000-0000-000000000006',
      'c0000000-0000-0000-0000-000000000002',
      'a0000000-0000-0000-0000-000000000003',
      'Sending now. Tell me what you think honestly 🙂',
      '2026-01-11 14:10:00+00',
      'safe'
    );

END $$;
