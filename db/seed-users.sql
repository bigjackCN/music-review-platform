-- Demo users for a fresh database. All share the password: demo12345
-- (BCrypt hash). seed-reviews*.sql expect alice, bob and carol to exist.
INSERT INTO app_user (username, password, email) VALUES
  ('alice', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'alice@example.com'),
  ('bob',   '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'bob@example.com'),
  ('carol', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'carol@example.com')
ON CONFLICT DO NOTHING;
