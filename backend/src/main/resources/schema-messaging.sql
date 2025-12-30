-- ============================================
-- SCHEMA FOR UC101 - MESSAGING FEATURE
-- Tables: conversations, messages
-- ============================================

-- ============================================
-- CONVERSATIONS TABLE
-- Stores chat sessions between user and rescue company
-- ============================================
CREATE TABLE IF NOT EXISTS conversations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    rescue_request_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    company_id INTEGER NOT NULL,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    agreed_cost DECIMAL(15, 2),
    cost_notes TEXT,
    
    FOREIGN KEY (rescue_request_id) REFERENCES rescue_requests(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES accounts(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES accounts(id) ON DELETE CASCADE,
    
    CONSTRAINT unique_conversation UNIQUE (rescue_request_id)
);

-- Index for faster lookups
CREATE INDEX IF NOT EXISTS idx_conversations_rescue_request ON conversations(rescue_request_id);
CREATE INDEX IF NOT EXISTS idx_conversations_user ON conversations(user_id);
CREATE INDEX IF NOT EXISTS idx_conversations_company ON conversations(company_id);
CREATE INDEX IF NOT EXISTS idx_conversations_status ON conversations(status);

-- ============================================
-- MESSAGES TABLE
-- Stores individual messages in conversations
-- ============================================
CREATE TABLE IF NOT EXISTS messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    conversation_id INTEGER NOT NULL,
    sender_id INTEGER NOT NULL,
    content TEXT NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT 0,
    attachment_url VARCHAR(500),
    attachment_type VARCHAR(50),
    
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- Index for faster message retrieval
CREATE INDEX IF NOT EXISTS idx_messages_conversation ON messages(conversation_id);
CREATE INDEX IF NOT EXISTS idx_messages_sender ON messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_messages_sent_at ON messages(sent_at);
CREATE INDEX IF NOT EXISTS idx_messages_is_read ON messages(is_read);

-- ============================================
-- SAMPLE DATA FOR TESTING
-- ============================================

-- Sample conversation for testing (assumes rescue_request id=1 exists)
-- INSERT INTO conversations (rescue_request_id, user_id, company_id, status)
-- VALUES (1, 1, 10, 'ACTIVE');

-- Sample messages
-- INSERT INTO messages (conversation_id, sender_id, content, is_read)
-- VALUES (1, 1, 'Xin chào, tôi cần hỗ trợ cứu hộ xe tại vị trí đã gửi.', 1);
-- INSERT INTO messages (conversation_id, sender_id, content, is_read)
-- VALUES (1, 10, 'Chào bạn, chúng tôi đã nhận được yêu cầu. Xin hỏi loại xe của bạn là gì?', 1);
-- INSERT INTO messages (conversation_id, sender_id, content, is_read)
-- VALUES (1, 1, 'Xe ô tô 4 chỗ Honda City, bị hết xăng.', 1);
-- INSERT INTO messages (conversation_id, sender_id, content, is_read)
-- VALUES (1, 10, 'Vâng, chi phí cứu hộ sẽ là 500.000đ. Bạn đồng ý không?', 0);
