-- ============================================
-- ADMIN ACCOUNT SETUP (UC401)
-- ============================================
-- Password: password123
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C

INSERT INTO accounts (id, username, password_hash, full_name, phone_number, email, role, created_at, updated_at)
VALUES (999, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Admin System', '0987654321', 'admin@resqonroad.vn', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT(id) DO NOTHING;

-- Test Users
INSERT INTO accounts (id, username, password_hash, full_name, phone_number, email, role, created_at, updated_at)
VALUES (1, 'user1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Nguyễn Văn A', '0901234567', 'user1@test.com', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT(id) DO NOTHING;

INSERT INTO accounts (id, username, password_hash, full_name, phone_number, email, role, created_at, updated_at)
VALUES (2, 'user2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Trần Thị B', '0901234568', 'user2@test.com', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT(id) DO NOTHING;

INSERT INTO accounts (id, username, password_hash, full_name, phone_number, email, role, created_at, updated_at)
VALUES (3, 'user3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Lê Văn C', '0901234569', 'user3@test.com', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT(id) DO NOTHING;

-- Company Accounts
INSERT INTO accounts (id, username, password_hash, full_name, phone_number, email, role, company_id, created_at, updated_at)
VALUES (10, 'company1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Cứu Hộ Ba Đình 24/7', '0243123456', 'badinh247@rescue.vn', 'COMPANY', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT(id) DO NOTHING;

INSERT INTO accounts (id, username, password_hash, full_name, phone_number, email, role, company_id, created_at, updated_at)
VALUES (11, 'company2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Cứu Hộ Hoàn Kiếm Express', '0243234567', 'hoankiem@rescue.vn', 'COMPANY', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT(id) DO NOTHING;
