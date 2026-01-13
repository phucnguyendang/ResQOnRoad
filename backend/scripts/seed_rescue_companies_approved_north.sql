-- Seed sample rescue companies (North VN) with APPROVED profile_status
-- Constraints:
--  - longitude: 105.8341598 ± 1  (104.8341598 .. 106.8341598)
--  - latitude : 21.0277644 ± 1  (20.0277644 .. 22.0277644)
--  - addresses: Ha Noi / Bac Ninh / Hung Yen / Phu Tho / Hoa Binh
--  - each company has an account (role=COMPANY) linked via accounts.company_id
--  - all companies profile_status = 'APPROVED'

PRAGMA foreign_keys = ON;

BEGIN;

-- BCrypt hash used across existing seed scripts (password: password123)
-- $2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C

-- Companies (IDs chosen to avoid conflicts with existing seed data)
INSERT OR REPLACE INTO rescue_companies (
    id, name, address, phone, email,
    latitude, longitude, service_radius,
    is_active, average_rating, total_reviews,
    description, business_license, is_verified,
    profile_status, tax_code, hotline, operating_hours, license_expiry_date, license_document_url, rejection_reason,
    created_at, updated_at
) VALUES
-- Ha Noi
(101, 'Cứu Hộ Hà Nội Trung Tâm 24/7',
 'Số 12 Phố Tràng Tiền, Hoàn Kiếm, Hà Nội',
 '0243000101', 'hanoi-center@rescue.vn',
 21.0240, 105.8560, 50.0,
 1, 4.7, 120,
 'Cứu hộ ô tô/xe máy khu vực trung tâm Hà Nội, hỗ trợ 24/7.',
 'GP-HN-2026-101', 1,
 'APPROVED', '0101100101', '19001001', '24/7', '2027-12-31 23:59:59', 'https://example.com/licenses/GP-HN-2026-101.pdf', NULL,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Bac Ninh
(102, 'Cứu Hộ Bắc Ninh Nhanh',
 'Số 88 Đường Lý Thái Tổ, TP. Bắc Ninh, Bắc Ninh',
 '0222000102', 'bacninh@rescue.vn',
 21.1865, 106.0763, 60.0,
 1, 4.6, 85,
 'Phục vụ cứu hộ tại TP. Bắc Ninh và khu vực lân cận.',
 'GP-BN-2026-102', 1,
 'APPROVED', '2302200102', '19001002', '24/7', '2027-12-31 23:59:59', 'https://example.com/licenses/GP-BN-2026-102.pdf', NULL,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Hung Yen
(103, 'Cứu Hộ Hưng Yên 365',
 'Số 45 Đường Nguyễn Văn Linh, TP. Hưng Yên, Hưng Yên',
 '0221000103', 'hungyen@rescue.vn',
 20.6558, 106.0559, 70.0,
 1, 4.5, 64,
 'Cứu hộ 365 ngày/năm tại Hưng Yên, hỗ trợ tuyến quốc lộ.',
 'GP-HY-2026-103', 1,
 'APPROVED', '1702100103', '19001003', '24/7', '2027-12-31 23:59:59', 'https://example.com/licenses/GP-HY-2026-103.pdf', NULL,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Phu Tho
(104, 'Cứu Hộ Phú Thọ Việt Trì',
 'Số 20 Đường Hùng Vương, TP. Việt Trì, Phú Thọ',
 '0210000104', 'phutho@rescue.vn',
 21.3220, 105.4010, 80.0,
 1, 4.4, 40,
 'Hỗ trợ cứu hộ tại Việt Trì và các tuyến đường qua Phú Thọ.',
 'GP-PT-2026-104', 1,
 'APPROVED', '2602100104', '19001004', '24/7', '2027-12-31 23:59:59', 'https://example.com/licenses/GP-PT-2026-104.pdf', NULL,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Hoa Binh
(105, 'Cứu Hộ Hòa Bình Cao Tốc',
 'Số 5 Đường Trần Hưng Đạo, TP. Hòa Bình, Hòa Bình',
 '0218000105', 'hoabinh@rescue.vn',
 20.8170, 105.3376, 90.0,
 1, 4.6, 55,
 'Chuyên cứu hộ tuyến cao tốc và khu vực Hòa Bình.',
 'GP-HB-2026-105', 1,
 'APPROVED', '5402180105', '19001005', '24/7', '2027-12-31 23:59:59', 'https://example.com/licenses/GP-HB-2026-105.pdf', NULL,
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Company accounts (link via accounts.company_id)
INSERT OR REPLACE INTO accounts (
    id, username, password_hash, full_name, phone_number, email, role, company_id, created_at
) VALUES
(301, 'company101', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Cứu Hộ Hà Nội Trung Tâm 24/7', '0243000101', 'hanoi-center@rescue.vn', 'COMPANY', 101, CURRENT_TIMESTAMP),
(302, 'company102', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Cứu Hộ Bắc Ninh Nhanh', '0222000102', 'bacninh@rescue.vn', 'COMPANY', 102, CURRENT_TIMESTAMP),
(303, 'company103', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Cứu Hộ Hưng Yên 365', '0221000103', 'hungyen@rescue.vn', 'COMPANY', 103, CURRENT_TIMESTAMP),
(304, 'company104', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Cứu Hộ Phú Thọ Việt Trì', '0210000104', 'phutho@rescue.vn', 'COMPANY', 104, CURRENT_TIMESTAMP),
(305, 'company105', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Cứu Hộ Hòa Bình Cao Tốc', '0218000105', 'hoabinh@rescue.vn', 'COMPANY', 105, CURRENT_TIMESTAMP);

-- Company registration requests (for Admin -> Duyệt đăng ký công ty)
-- We mark them APPROVED and link created_company_id to the seeded rescue_companies.
INSERT OR REPLACE INTO company_registration_requests (
    id,
    account_id,
    status,
    name,
    address,
    phone,
    email,
    latitude,
    longitude,
    service_radius,
    tax_code,
    hotline,
    operating_hours,
    business_license,
    license_document_url,
    description,
    rejection_reason,
    created_company_id,
    reviewed_by_account_id,
    reviewed_at,
    submitted_at,
    updated_at
) VALUES
(101, 301, 'APPROVED', 'Cứu Hộ Hà Nội Trung Tâm 24/7', 'Số 12 Phố Tràng Tiền, Hoàn Kiếm, Hà Nội', '0243000101', 'hanoi-center@rescue.vn', 21.0240, 105.8560, 50.0, '0101100101', '19001001', '24/7', 'GP-HN-2026-101', 'https://example.com/licenses/GP-HN-2026-101.pdf', 'Cứu hộ ô tô/xe máy khu vực trung tâm Hà Nội, hỗ trợ 24/7.', NULL, 101, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(102, 302, 'APPROVED', 'Cứu Hộ Bắc Ninh Nhanh', 'Số 88 Đường Lý Thái Tổ, TP. Bắc Ninh, Bắc Ninh', '0222000102', 'bacninh@rescue.vn', 21.1865, 106.0763, 60.0, '2302200102', '19001002', '24/7', 'GP-BN-2026-102', 'https://example.com/licenses/GP-BN-2026-102.pdf', 'Phục vụ cứu hộ tại TP. Bắc Ninh và khu vực lân cận.', NULL, 102, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(103, 303, 'APPROVED', 'Cứu Hộ Hưng Yên 365', 'Số 45 Đường Nguyễn Văn Linh, TP. Hưng Yên, Hưng Yên', '0221000103', 'hungyen@rescue.vn', 20.6558, 106.0559, 70.0, '1702100103', '19001003', '24/7', 'GP-HY-2026-103', 'https://example.com/licenses/GP-HY-2026-103.pdf', 'Cứu hộ 365 ngày/năm tại Hưng Yên, hỗ trợ tuyến quốc lộ.', NULL, 103, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(104, 304, 'APPROVED', 'Cứu Hộ Phú Thọ Việt Trì', 'Số 20 Đường Hùng Vương, TP. Việt Trì, Phú Thọ', '0210000104', 'phutho@rescue.vn', 21.3220, 105.4010, 80.0, '2602100104', '19001004', '24/7', 'GP-PT-2026-104', 'https://example.com/licenses/GP-PT-2026-104.pdf', 'Hỗ trợ cứu hộ tại Việt Trì và các tuyến đường qua Phú Thọ.', NULL, 104, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(105, 305, 'APPROVED', 'Cứu Hộ Hòa Bình Cao Tốc', 'Số 5 Đường Trần Hưng Đạo, TP. Hòa Bình, Hòa Bình', '0218000105', 'hoabinh@rescue.vn', 20.8170, 105.3376, 90.0, '5402180105', '19001005', '24/7', 'GP-HB-2026-105', 'https://example.com/licenses/GP-HB-2026-105.pdf', 'Chuyên cứu hộ tuyến cao tốc và khu vực Hòa Bình.', NULL, 105, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

COMMIT;

-- Optional: verify constraints quickly
-- SELECT id, name, latitude, longitude, profile_status FROM rescue_companies WHERE id BETWEEN 101 AND 105;
