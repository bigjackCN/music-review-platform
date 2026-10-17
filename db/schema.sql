-- Current database schema (single source of truth).
-- Used to initialise a fresh database and by the integration tests.
-- Seed data lives in the seed*.sql files; incremental changes in db/migrations/.

CREATE TABLE album (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    artist       VARCHAR(255) NOT NULL,
    release_year INTEGER,
    genre        VARCHAR(100),
    cover_url    VARCHAR(500),
    bandcamp_id  BIGINT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE app_user (
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE review (
    id         BIGSERIAL PRIMARY KEY,
    album_id   BIGINT   NOT NULL REFERENCES album(id) ON DELETE CASCADE,
    user_id    BIGINT   NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    rating     SMALLINT NOT NULL,
    comment    TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT review_rating_check CHECK (rating >= 1 AND rating <= 5),
    CONSTRAINT unique_user_album_review UNIQUE (user_id, album_id)
);

CREATE INDEX idx_review_album_id ON review (album_id);

CREATE TABLE track (
    id           BIGSERIAL PRIMARY KEY,
    album_id     BIGINT       NOT NULL REFERENCES album(id) ON DELETE CASCADE,
    track_number INTEGER      NOT NULL,
    title        VARCHAR(255) NOT NULL,
    duration_ms  INTEGER,
    preview_url  VARCHAR(500) NOT NULL,
    CONSTRAINT unique_album_track UNIQUE (album_id, track_number)
);
