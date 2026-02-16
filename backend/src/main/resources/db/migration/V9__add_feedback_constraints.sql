ALTER TABLE watch_history ADD CONSTRAINT chk_feedback
    CHECK (feedback IN ('HELPFUL', 'NOT_HELPFUL') OR feedback IS NULL);

CREATE INDEX idx_watch_history_feedback ON watch_history (feedback);
