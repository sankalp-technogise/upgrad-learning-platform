-- Add onboarding_completed flag to users table
ALTER TABLE users ADD COLUMN onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE;

-- Set existing users as having completed onboarding (backward compatibility)
UPDATE users SET onboarding_completed = TRUE WHERE id IS NOT NULL;

-- Create interests table
CREATE TABLE interests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    icon_name VARCHAR(100),
    display_order INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create user_interests join table (many-to-many)
CREATE TABLE user_interests (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    interest_id UUID NOT NULL REFERENCES interests(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, interest_id)
);

-- Create index for efficient lookup
CREATE INDEX idx_user_interests_user_id ON user_interests(user_id);
CREATE INDEX idx_user_interests_interest_id ON user_interests(interest_id);

-- Seed initial interests matching the UI reference
INSERT INTO interests (name, description, icon_name, display_order) VALUES
    ('Python Programming', 'Learn Python programming fundamentals and advanced concepts', 'puzzle', 1),
    ('Data Science', 'Master data analysis, visualization, and machine learning', 'chart', 2),
    ('UI/UX Design', 'Create beautiful and user-friendly interfaces', 'palette', 3),
    ('Digital Marketing', 'Learn digital marketing strategies and analytics', 'megaphone', 4),
    ('Cloud Computing', 'Explore cloud platforms and distributed systems', 'server', 5),
    ('Cybersecurity', 'Understand security principles and best practices', 'shield', 6),
    ('React Framework', 'Build modern web applications with React', 'atom', 7),
    ('Personal Finance', 'Manage your finances and investments effectively', 'dollar', 8);
