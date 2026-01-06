-- Schema for UC405: Content Moderation (Kiểm duyệt nội dung)
-- This schema creates the moderatable_contents table for tracking content moderation

-- Drop table if exists (for development/testing)
-- DROP TABLE IF EXISTS moderatable_contents;

-- Create moderatable_contents table
CREATE TABLE IF NOT EXISTS moderatable_contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- Content type: POST, COMMENT, REVIEW
    content_type VARCHAR(20) NOT NULL,
    
    -- Reference to the actual content (post_id, comment_id, review_id)
    reference_id BIGINT NOT NULL,
    
    -- Author of the content
    author_id BIGINT NOT NULL,
    
    -- Snapshot of content at time of submission
    content_title VARCHAR(500),
    content_text TEXT NOT NULL,
    content_image TEXT,
    
    -- Moderation status: PENDING, APPROVED, REJECTED, REMOVED
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    
    -- Admin who moderated this content
    moderator_id BIGINT,
    
    -- Reason for rejection or removal
    moderation_reason VARCHAR(1000),
    
    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    moderated_at TIMESTAMP,
    
    -- Foreign keys
    CONSTRAINT fk_modcontent_author FOREIGN KEY (author_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT fk_modcontent_moderator FOREIGN KEY (moderator_id) REFERENCES accounts(id) ON DELETE SET NULL,
    
    -- Unique constraint to prevent duplicate moderation entries
    CONSTRAINT uk_content_type_reference UNIQUE (content_type, reference_id)
);

-- Create indexes for better query performance
CREATE INDEX idx_modcontent_status ON moderatable_contents(status);
CREATE INDEX idx_modcontent_type ON moderatable_contents(content_type);
CREATE INDEX idx_modcontent_author ON moderatable_contents(author_id);
CREATE INDEX idx_modcontent_created ON moderatable_contents(created_at);
CREATE INDEX idx_modcontent_type_status ON moderatable_contents(content_type, status);

-- Sample data for testing (optional)
-- Note: Requires existing accounts with IDs 1, 2, 3

-- INSERT INTO moderatable_contents (content_type, reference_id, author_id, content_title, content_text, status)
-- VALUES 
--     ('POST', 1, 2, 'Test Post Title', 'This is a test post content for moderation.', 'PENDING'),
--     ('COMMENT', 1, 3, NULL, 'This is a test comment for moderation.', 'PENDING'),
--     ('REVIEW', 1, 2, NULL, 'This is a test review with rating 5 stars.', 'PENDING');
