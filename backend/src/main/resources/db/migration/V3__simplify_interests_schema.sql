-- V3: Simplify interests schema - remove interests table and use string names

-- Drop old structure (order matters due to FK constraints)
DROP TABLE IF EXISTS user_interests;
DROP TABLE IF EXISTS interests;

-- Create simplified user_interests table with string-based interest names
CREATE TABLE user_interests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    interest_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, interest_name)
);

-- Create index for efficient lookup by user
CREATE INDEX idx_user_interests_user_id ON user_interests(user_id);
