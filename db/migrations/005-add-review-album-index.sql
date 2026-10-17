-- Reviews are always looked up by album (stats, ranking, review list). The unique
-- constraint on (user_id, album_id) cannot serve that, so the table was scanned.
CREATE INDEX IF NOT EXISTS idx_review_album_id ON review (album_id);
