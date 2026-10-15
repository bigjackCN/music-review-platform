CREATE TABLE IF NOT EXISTS track (
    id BIGSERIAL PRIMARY KEY,
    album_id BIGINT NOT NULL REFERENCES album(id) ON DELETE CASCADE,
    track_number INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    duration_ms INTEGER,
    preview_url VARCHAR(500) NOT NULL,
    CONSTRAINT unique_album_track UNIQUE (album_id, track_number)
);
