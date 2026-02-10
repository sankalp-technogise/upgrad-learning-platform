-- Seed sample content for local development/testing.
-- Run via: make seed-db

INSERT INTO contents (title, description, thumbnail_url, video_url, category) VALUES
    ('Introduction to Data Science', 'Learn the fundamentals of data analysis, visualization, and machine learning in this comprehensive series.', 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=400&h=200&fit=crop', 'https://example.com/videos/data-science-intro', 'DATA_SCIENCE'),
    ('Advanced Python Concepts', 'Explore decorators, generators, and asynchronous programming to elevate your Python skills.', 'https://images.unsplash.com/photo-1526379095098-d400fd0bf935?w=400&h=200&fit=crop', 'https://example.com/videos/advanced-python', 'PYTHON_PROGRAMMING'),
    ('UI/UX Design Principles', 'Master the core principles of user-centric design, from wireframing to high-fidelity prototyping.', 'https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400&h=200&fit=crop', 'https://example.com/videos/uiux-principles', 'UI_UX_DESIGN'),
    ('Cloud Computing Fundamentals', 'Build and deploy scalable applications using major cloud platforms like AWS and GCP.', 'https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=400&h=200&fit=crop', 'https://example.com/videos/cloud-fundamentals', 'CLOUD_COMPUTING'),
    ('Digital Marketing Mastery', 'Learn SEO, content marketing, and social media strategies to grow any business online.', 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=400&h=200&fit=crop', 'https://example.com/videos/digital-marketing', 'DIGITAL_MARKETING'),
    ('Cybersecurity Essentials', 'Understand threat landscapes, vulnerability assessment, and secure coding practices.', 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=400&h=200&fit=crop', 'https://example.com/videos/cybersecurity', 'CYBERSECURITY'),
    ('React From Scratch', 'Build modern, performant web applications using React hooks, context, and routing.', 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=400&h=200&fit=crop', 'https://example.com/videos/react-scratch', 'REACT_FRAMEWORK'),
    ('Personal Finance 101', 'Master budgeting, investing, and retirement planning for long-term financial health.', 'https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=400&h=200&fit=crop', 'https://example.com/videos/personal-finance', 'PERSONAL_FINANCE'),
    ('Python for Data Engineering', 'Use Python to build robust ETL pipelines and data infrastructure.', 'https://images.unsplash.com/photo-1518186285589-2f7649de83e0?w=400&h=200&fit=crop', 'https://example.com/videos/python-data-eng', 'PYTHON_PROGRAMMING'),
    ('Machine Learning with Python', 'Hands-on guide to building ML models using scikit-learn and TensorFlow.', 'https://images.unsplash.com/photo-1527474305487-b87b222841cc?w=400&h=200&fit=crop', 'https://example.com/videos/ml-python', 'DATA_SCIENCE')
ON CONFLICT DO NOTHING;

-- Update existing rows that still have placeholder thumbnails
UPDATE contents SET thumbnail_url = 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=400&h=200&fit=crop' WHERE title = 'Introduction to Data Science' AND thumbnail_url LIKE '%placehold%';
UPDATE contents SET thumbnail_url = 'https://images.unsplash.com/photo-1526379095098-d400fd0bf935?w=400&h=200&fit=crop' WHERE title = 'Advanced Python Concepts' AND thumbnail_url LIKE '%placehold%';
UPDATE contents SET thumbnail_url = 'https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400&h=200&fit=crop' WHERE title = 'UI/UX Design Principles' AND thumbnail_url LIKE '%placehold%';
UPDATE contents SET thumbnail_url = 'https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=400&h=200&fit=crop' WHERE title = 'Cloud Computing Fundamentals' AND thumbnail_url LIKE '%placehold%';
UPDATE contents SET thumbnail_url = 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=400&h=200&fit=crop' WHERE title = 'Digital Marketing Mastery' AND thumbnail_url LIKE '%placehold%';
UPDATE contents SET thumbnail_url = 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=400&h=200&fit=crop' WHERE title = 'Cybersecurity Essentials' AND thumbnail_url LIKE '%placehold%';
UPDATE contents SET thumbnail_url = 'https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=400&h=200&fit=crop' WHERE title = 'React From Scratch' AND thumbnail_url LIKE '%placehold%';
UPDATE contents SET thumbnail_url = 'https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=400&h=200&fit=crop' WHERE title = 'Personal Finance 101' AND thumbnail_url LIKE '%placehold%';
UPDATE contents SET thumbnail_url = 'https://images.unsplash.com/photo-1518186285589-2f7649de83e0?w=400&h=200&fit=crop' WHERE title = 'Python for Data Engineering' AND thumbnail_url LIKE '%placehold%';
UPDATE contents SET thumbnail_url = 'https://images.unsplash.com/photo-1527474305487-b87b222841cc?w=400&h=200&fit=crop' WHERE title = 'Machine Learning with Python' AND thumbnail_url LIKE '%placehold%';
