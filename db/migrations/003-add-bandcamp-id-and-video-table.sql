-- Albums that are not on iTunes (e.g. Bandcamp-only releases) play through Bandcamp's official embed.
ALTER TABLE album ADD COLUMN IF NOT EXISTS bandcamp_id BIGINT;

CREATE TABLE IF NOT EXISTS video (
    id BIGSERIAL PRIMARY KEY,
    album_id BIGINT NOT NULL REFERENCES album(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    youtube_id VARCHAR(20) NOT NULL,
    CONSTRAINT unique_album_video UNIQUE (album_id, youtube_id)
);
