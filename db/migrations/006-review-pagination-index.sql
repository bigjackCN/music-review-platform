-- Supports paged review lists in newest-first order and review aggregates by album.
CREATE INDEX IF NOT EXISTS idx_review_album_created_id
    ON review (album_id, created_at DESC, id DESC);
DROP INDEX IF EXISTS idx_review_album_id;
