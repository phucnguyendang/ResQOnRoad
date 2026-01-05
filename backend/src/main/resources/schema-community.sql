-- Schema for UC103: Community Support and Consultation
-- Bảng tin cộng đồng (Community Feed)

-- Table: community_posts
-- Stores community posts where users can share their incidents and ask for help
CREATE TABLE IF NOT EXISTS community_posts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    author_id INTEGER NOT NULL,
    incident_type VARCHAR(100),
    location VARCHAR(500),
    latitude REAL,
    longitude REAL,
    image_base64 TEXT,
    view_count INTEGER NOT NULL DEFAULT 0,
    is_resolved BOOLEAN NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- Table: community_comments
-- Stores comments/advice from community members on posts
CREATE TABLE IF NOT EXISTS community_comments (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    content TEXT NOT NULL,
    post_id INTEGER NOT NULL,
    author_id INTEGER NOT NULL,
    parent_comment_id INTEGER,
    is_helpful BOOLEAN NOT NULL DEFAULT 0,
    helpful_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES community_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES accounts(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES community_comments(id) ON DELETE SET NULL
);

-- Indexes for performance optimization
CREATE INDEX IF NOT EXISTS idx_community_posts_author ON community_posts(author_id);
CREATE INDEX IF NOT EXISTS idx_community_posts_created ON community_posts(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_community_posts_incident_type ON community_posts(incident_type);
CREATE INDEX IF NOT EXISTS idx_community_posts_resolved ON community_posts(is_resolved);
CREATE INDEX IF NOT EXISTS idx_community_posts_location ON community_posts(latitude, longitude);

CREATE INDEX IF NOT EXISTS idx_community_comments_post ON community_comments(post_id);
CREATE INDEX IF NOT EXISTS idx_community_comments_author ON community_comments(author_id);
CREATE INDEX IF NOT EXISTS idx_community_comments_parent ON community_comments(parent_comment_id);
CREATE INDEX IF NOT EXISTS idx_community_comments_helpful ON community_comments(is_helpful);
