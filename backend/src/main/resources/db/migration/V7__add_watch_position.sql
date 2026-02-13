ALTER TABLE watch_history
ADD COLUMN last_watched_position INT NOT NULL DEFAULT 0 CHECK (last_watched_position >= 0);
