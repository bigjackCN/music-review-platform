-- Demo users for a fresh database (sample data, not real people).
-- All share the password: demo12345. alice, bob and carol are the accounts to log in with.
INSERT INTO app_user (username, password, email) VALUES
  ('alice', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'alice@example.com'),
  ('bob', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'bob@example.com'),
  ('carol', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'carol@example.com'),
  ('mara_k', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'mara_k@example.com'),
  ('jonahplays', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'jonahplays@example.com'),
  ('tess.w', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'tess_w@example.com'),
  ('dmitri', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'dmitri@example.com'),
  ('nightbus', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'nightbus@example.com'),
  ('ola_b', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'ola_b@example.com'),
  ('priya.s', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'priya_s@example.com'),
  ('sam_vinyl', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'sam_vinyl@example.com'),
  ('hyunwoo', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'hyunwoo@example.com'),
  ('leila', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'leila@example.com'),
  ('tom_in_to', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'tom_in_to@example.com'),
  ('wen_li', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'wen_li@example.com'),
  ('ravi', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'ravi@example.com'),
  ('cal_44', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'cal_44@example.com'),
  ('ellie_m', '$2a$10$m5K1C9SIoIGwKUUAegr7wOZLEipfpTN0zBxuysgDiFBMV7TzIfjtC', 'ellie_m@example.com')
ON CONFLICT DO NOTHING;
