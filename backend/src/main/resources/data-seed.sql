-- ============================================
-- SEED DATA FOR UC202 TESTING
-- Rescue Company Search Feature
-- Location: Hanoi, Vietnam (around Ba Dinh, Hoan Kiem areas)
-- ============================================

-- Clean existing data (optional - comment out if you want to keep existing data)
-- DELETE FROM reviews;
-- DELETE FROM services;
-- DELETE FROM rescue_companies;
-- DELETE FROM accounts;

-- ============================================
-- INSERT TEST ACCOUNTS (for reviews)
-- Using INSERT OR REPLACE to avoid duplicate key errors on restart
-- Password for all test accounts: password123
-- BCrypt hash generated with cost factor 10
-- ============================================

INSERT OR REPLACE INTO accounts (id, username, password_hash, full_name, phone_number, email, role, created_at)
VALUES (1, 'user1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Nguyễn Văn A', '0901234567', 'user1@test.com', 'USER', CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO accounts (id, username, password_hash, full_name, phone_number, email, role, created_at)
VALUES (2, 'user2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Trần Thị B', '0901234568', 'user2@test.com', 'USER', CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO accounts (id, username, password_hash, full_name, phone_number, email, role, created_at)
VALUES (3, 'user3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Lê Văn C', '0901234569', 'user3@test.com', 'USER', CURRENT_TIMESTAMP);

-- Company accounts (for UC404 testing)
INSERT OR REPLACE INTO accounts (id, username, password_hash, full_name, phone_number, email, role, company_id, created_at)
VALUES (10, 'company1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Cứu Hộ Ba Đình 24/7', '0243123456', 'badinh247@rescue.vn', 'COMPANY', 1, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO accounts (id, username, password_hash, full_name, phone_number, email, role, company_id, created_at)
VALUES (11, 'company2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeOHbxJwqLHfp6d.zKa0Xj9dJwI1mTz8C', 'Cứu Hộ Hoàn Kiếm Express', '0243234567', 'hoankiem@rescue.vn', 'COMPANY', 2, CURRENT_TIMESTAMP);

-- ============================================
-- INSERT RESCUE COMPANIES
-- ============================================

-- Company 1: Cứu hộ Ba Đình (Very close to center)
INSERT OR REPLACE INTO rescue_companies (
    id, name, address, phone, email, 
    latitude, longitude, service_radius, 
    is_active, average_rating, total_reviews, 
    description, business_license, is_verified,
    created_at, updated_at
) VALUES (
    1, 'Cứu Hộ Ba Đình 24/7', 
    'Số 123 Đường Hoàng Hoa Thám, Ba Đình, Hà Nội', 
    '0243123456', 'badinh247@rescue.vn',
    21.0285, 105.8542, 50.0,
    1, 4.8, 156,
    'Chuyên cứu hộ ô tô, xe máy 24/7. Đội ngũ chuyên nghiệp, trang thiết bị hiện đại.',
    'GP-BD-2023-001', 1,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Company 2: Cứu hộ Hoàn Kiếm (Near Old Quarter)
INSERT OR REPLACE INTO rescue_companies (
    id, name, address, phone, email,
    latitude, longitude, service_radius,
    is_active, average_rating, total_reviews,
    description, business_license, is_verified,
    created_at, updated_at
) VALUES (
    2, 'Cứu Hộ Hoàn Kiếm Express',
    'Số 45 Phố Hàng Bài, Hoàn Kiếm, Hà Nội',
    '0243234567', 'hoankiem@rescue.vn',
    21.0245, 105.8516, 40.0,
    1, 4.5, 89,
    'Dịch vụ cứu hộ nhanh chóng tại khu vực trung tâm. Thời gian phản hồi dưới 15 phút.',
    'GP-HK-2023-002', 1,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Company 3: Cứu hộ Tây Hồ (West Lake area)
INSERT OR REPLACE INTO rescue_companies (
    id, name, address, phone, email,
    latitude, longitude, service_radius,
    is_active, average_rating, total_reviews,
    description, business_license, is_verified,
    created_at, updated_at
) VALUES (
    3, 'Cứu Hộ Tây Hồ Auto',
    'Số 789 Đường Âu Cơ, Tây Hồ, Hà Nội',
    '0243345678', 'tayho@rescue.vn',
    21.0583, 105.8200, 60.0,
    1, 4.6, 124,
    'Chuyên cứu hộ cao tốc và khu vực Tây Hồ. Xe cẩu chuyên dụng cho mọi loại xe.',
    'GP-TH-2023-003', 1,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Company 4: Cứu hộ Cầu Giấy (North area)
INSERT OR REPLACE INTO rescue_companies (
    id, name, address, phone, email,
    latitude, longitude, service_radius,
    is_active, average_rating, total_reviews,
    description, business_license, is_verified,
    created_at, updated_at
) VALUES (
    4, 'Cứu Hộ Cầu Giấy Premium',
    'Số 234 Đường Trần Duy Hưng, Cầu Giấy, Hà Nội',
    '0243456789', 'caugiay@rescue.vn',
    21.0370, 105.8019, 45.0,
    1, 4.9, 203,
    'Dịch vụ cao cấp với đội ngũ kỹ thuật viên chuyên nghiệp. Bảo hành dịch vụ 30 ngày.',
    'GP-CG-2023-004', 1,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Company 5: Cứu hộ Đống Đa (Central area)
INSERT OR REPLACE INTO rescue_companies (
    id, name, address, phone, email,
    latitude, longitude, service_radius,
    is_active, average_rating, total_reviews,
    description, business_license, is_verified,
    created_at, updated_at
) VALUES (
    5, 'Cứu Hộ Đống Đa Fast',
    'Số 567 Phố Láng Hạ, Đống Đa, Hà Nội',
    '0243567890', 'dongda@rescue.vn',
    21.0145, 105.8236, 35.0,
    1, 4.3, 67,
    'Phục vụ nhanh, giá cả hợp lý. Chuyên xử lý sự cố xe máy và ô tô.',
    'GP-DD-2023-005', 1,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Company 6: Cứu hộ Long Biên (Far from center - for distance testing)
INSERT OR REPLACE INTO rescue_companies (
    id, name, address, phone, email,
    latitude, longitude, service_radius,
    is_active, average_rating, total_reviews,
    description, business_license, is_verified,
    created_at, updated_at
) VALUES (
    6, 'Cứu Hộ Long Biên Pro',
    'Số 890 Đường Nguyễn Văn Cừ, Long Biên, Hà Nội',
    '0243678901', 'longbien@rescue.vn',
    21.0367, 105.9000, 70.0,
    1, 4.7, 142,
    'Phục vụ khu vực Long Biên và các tỉnh lân cận. Xe cứu hộ lớn, chuyên tuyến xa.',
    'GP-LB-2023-006', 1,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Company 7: Cứu hộ Thanh Xuân (South area)
INSERT OR REPLACE INTO rescue_companies (
    id, name, address, phone, email,
    latitude, longitude, service_radius,
    is_active, average_rating, total_reviews,
    description, business_license, is_verified,
    created_at, updated_at
) VALUES (
    7, 'Cứu Hộ Thanh Xuân 365',
    'Số 123 Đường Khuất Duy Tiến, Thanh Xuân, Hà Nội',
    '0243789012', 'thanhxuan@rescue.vn',
    20.9950, 105.8089, 50.0,
    1, 4.4, 98,
    'Hoạt động 365 ngày/năm. Chuyên sửa chữa tại chỗ và cứu hộ.',
    'GP-TX-2023-007', 1,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Company 8: Inactive company (for testing filter)
INSERT OR REPLACE INTO rescue_companies (
    id, name, address, phone, email,
    latitude, longitude, service_radius,
    is_active, average_rating, total_reviews,
    description, business_license, is_verified,
    created_at, updated_at
) VALUES (
    8, 'Cứu Hộ Ngừng Hoạt Động',
    'Số 999 Đường Test, Test District, Hà Nội',
    '0243999999', 'inactive@rescue.vn',
    21.0300, 105.8500, 30.0,
    0, 3.5, 45,
    'Công ty tạm ngừng hoạt động.',
    'GP-TEST-2023-008', 0,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- ============================================
-- INSERT SERVICES
-- ============================================

-- Services for Company 1 (Ba Đình)
INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (1, 'Dịch vụ cẩu xe', 'Cẩu xe ô tô các loại, khoảng cách dưới 50km', 'TOW_TRUCK', 500000, 'VND', 1, 30, 1, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (2, 'Vá lốp tại chỗ', 'Vá lốp xe ô tô, xe máy tại chỗ', 'TIRE_CHANGE', 150000, 'VND', 1, 20, 1, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (3, 'Cứu hộ ắc quy', 'Nổ máy ắc quy, thay ắc quy mới', 'BATTERY_JUMP', 200000, 'VND', 1, 15, 1, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (4, 'Giao nhiên liệu', 'Giao xăng, dầu khẩn cấp', 'FUEL_DELIVERY', 100000, 'VND + giá xăng', 1, 25, 1, CURRENT_TIMESTAMP);

-- Services for Company 2 (Hoàn Kiếm)
INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (5, 'Cẩu xe Express', 'Cẩu xe nhanh trong 15 phút', 'TOW_TRUCK', 600000, 'VND', 1, 15, 2, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (6, 'Thay lốp dự phòng', 'Thay lốp dự phòng tại chỗ', 'TIRE_CHANGE', 120000, 'VND', 1, 15, 2, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (7, 'Mở khóa xe', 'Mở khóa ô tô, xe máy khi để quên chìa', 'LOCKOUT', 250000, 'VND', 1, 20, 2, CURRENT_TIMESTAMP);

-- Services for Company 3 (Tây Hồ)
INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (8, 'Cẩu xe cao tốc', 'Chuyên cẩu xe trên cao tốc', 'TOW_TRUCK', 800000, 'VND', 1, 40, 3, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (9, 'Kéo xe sa lầy', 'Kéo xe bị sa lầy, kẹt bánh', 'WINCH_OUT', 400000, 'VND', 1, 35, 3, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (10, 'Sửa chữa cơ bản', 'Sửa chữa hệ thống điện, nước, phanh cơ bản', 'MECHANICAL_REPAIR', 300000, 'VND', 1, 45, 3, CURRENT_TIMESTAMP);

-- Services for Company 4 (Cầu Giấy)
INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (11, 'Cẩu xe Premium', 'Dịch vụ cẩu xe cao cấp, xe chuyên dụng', 'TOW_TRUCK', 1000000, 'VND', 1, 25, 4, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (12, 'Cứu hộ tai nạn', 'Xử lý hiện trường tai nạn, cẩu xe hư hỏng', 'ACCIDENT_RECOVERY', 1500000, 'VND', 1, 60, 4, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (13, 'Vá lốp Premium', 'Vá lốp chuyên nghiệp, bảo hành 3 tháng', 'TIRE_CHANGE', 200000, 'VND', 1, 25, 4, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (14, 'Thay ắc quy mới', 'Thay ắc quy chính hãng, bảo hành 12 tháng', 'BATTERY_JUMP', 800000, 'VND', 1, 20, 4, CURRENT_TIMESTAMP);

-- Services for Company 5 (Đống Đa)
INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (15, 'Cẩu xe giá rẻ', 'Cẩu xe giá cả phải chăng', 'TOW_TRUCK', 400000, 'VND', 1, 35, 5, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (16, 'Vá lốp nhanh', 'Vá lốp nhanh trong 10 phút', 'TIRE_CHANGE', 100000, 'VND', 1, 10, 5, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (17, 'Nổ máy ắc quy', 'Nổ máy khi hết ắc quy', 'BATTERY_JUMP', 150000, 'VND', 1, 15, 5, CURRENT_TIMESTAMP);

-- Services for Company 6 (Long Biên)
INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (18, 'Cẩu xe đường dài', 'Chuyên cẩu xe đường dài, liên tỉnh', 'TOW_TRUCK', 1200000, 'VND', 1, 90, 6, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (19, 'Giao xăng tận nơi', 'Giao xăng khẩn cấp khu vực xa', 'FUEL_DELIVERY', 150000, 'VND + giá xăng', 1, 40, 6, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (20, 'Kéo xe container', 'Chuyên kéo xe tải, container', 'WINCH_OUT', 2000000, 'VND', 1, 120, 6, CURRENT_TIMESTAMP);

-- Services for Company 7 (Thanh Xuân)
INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (21, 'Cẩu xe tiêu chuẩn', 'Dịch vụ cẩu xe tiêu chuẩn', 'TOW_TRUCK', 550000, 'VND', 1, 30, 7, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (22, 'Vá lốp ô tô', 'Vá lốp ô tô 4-7 chỗ', 'TIRE_CHANGE', 130000, 'VND', 1, 20, 7, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO services (id, name, description, type, base_price, price_unit, is_available, estimated_time, company_id, created_at)
VALUES (23, 'Sửa chữa tại chỗ', 'Sửa chữa nhỏ tại chỗ', 'MECHANICAL_REPAIR', 250000, 'VND', 1, 40, 7, CURRENT_TIMESTAMP);

-- ============================================
-- INSERT REVIEWS (Sample reviews from users)
-- Note: Assuming account_id 1, 2, 3 exist in accounts table
-- ============================================

-- Reviews for Company 1 (Ba Đình)
INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (1, 1, 1, 5, 'Dịch vụ tuyệt vời! Đến rất nhanh và giá cả hợp lý. Nhân viên thân thiện.', 1, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (2, 2, 1, 5, 'Cảm ơn anh tài xế! Giúp tôi trong lúc khó khăn. Rất chuyên nghiệp.', 1, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (3, 3, 1, 4, 'Tốt, nhưng phải chờ hơi lâu. Tổng thể là ổn.', 1, CURRENT_TIMESTAMP);

-- Reviews for Company 2 (Hoàn Kiếm)
INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (4, 1, 2, 5, 'Express thật sự! 15 phút đã có xe đến. Đáng đồng tiền.', 1, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (5, 2, 2, 4, 'Tốt, phục vụ nhanh nhưng giá hơi cao.', 1, CURRENT_TIMESTAMP);

-- Reviews for Company 3 (Tây Hồ)
INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (6, 3, 3, 5, 'Xe cẩu chuyên dụng, kéo xe container của tôi rất chuyên nghiệp.', 1, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (7, 1, 3, 4, 'Dịch vụ tốt, nhân viên nhiệt tình. Giá cả hợp lý.', 1, CURRENT_TIMESTAMP);

-- Reviews for Company 4 (Cầu Giấy)
INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (8, 2, 4, 5, 'Dịch vụ Premium thật sự xứng đáng! Chất lượng tuyệt vời.', 1, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (9, 3, 4, 5, 'Bảo hành dịch vụ rất tốt. Sẽ sử dụng lại.', 1, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (10, 1, 4, 5, 'Tốt nhất trong khu vực! Đội ngũ chuyên nghiệp.', 1, CURRENT_TIMESTAMP);

-- Reviews for Company 5 (Đống Đa)
INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (11, 2, 5, 4, 'Giá rẻ, chất lượng tạm ổn. Phù hợp với túi tiền sinh viên.', 1, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (12, 3, 5, 4, 'Dịch vụ nhanh, giá cả phải chăng.', 1, CURRENT_TIMESTAMP);

-- Reviews for Company 6 (Long Biên)
INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (13, 1, 6, 5, 'Cẩu xe từ Hà Nội về Hải Phòng rất chuyên nghiệp. Tuyệt vời!', 1, CURRENT_TIMESTAMP);

INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (14, 2, 6, 4, 'Dịch vụ tốt cho đường dài, giá hơi cao nhưng chất lượng.', 1, CURRENT_TIMESTAMP);

-- Reviews for Company 7 (Thanh Xuân)
INSERT OR REPLACE INTO reviews (id, user_id, company_id, rating, comment, is_verified, created_at)
VALUES (15, 3, 7, 4, 'Dịch vụ ổn định, luôn sẵn sàng phục vụ.', 1, CURRENT_TIMESTAMP);

-- ============================================
-- VERIFY DATA INSERTION
-- ============================================

-- Check inserted data
SELECT 'Rescue Companies Inserted:' as info, COUNT(*) as count FROM rescue_companies;
SELECT 'Services Inserted:' as info, COUNT(*) as count FROM services;
SELECT 'Reviews Inserted:' as info, COUNT(*) as count FROM reviews;

-- Show sample data
SELECT id, name, latitude, longitude, average_rating, is_active FROM rescue_companies ORDER BY id;
SELECT id, name, type, base_price, company_id FROM services ORDER BY id LIMIT 10;

-- ============================================
-- TEST QUERIES (COMMENTED OUT FOR SEED)
-- ============================================

-- Test 1: Find companies near Ba Dinh center (21.0285, 105.8542)
-- Expected: Should return companies sorted by distance
-- SELECT 
--     id, name, 
--     (6371 * acos(cos(radians(21.0285)) * cos(radians(latitude)) 
--     * cos(radians(longitude) - radians(105.8542)) 
--     + sin(radians(21.0285)) * sin(radians(latitude)))) AS distance_km
-- FROM rescue_companies 
-- WHERE is_active = 1
-- HAVING distance_km <= 50
-- ORDER BY distance_km ASC;

-- Test 2: Find companies with specific service type (TOW_TRUCK)
-- SELECT DISTINCT c.id, c.name, s.type, s.name as service_name
-- FROM rescue_companies c
-- INNER JOIN services s ON c.id = s.company_id
-- WHERE c.is_active = 1 
-- AND s.type = 'TOW_TRUCK'
-- AND s.is_available = 1
-- ORDER BY c.id;

-- ============================================
-- SEED DATA SUMMARY
-- ============================================
-- Total Companies: 8 (7 active, 1 inactive)
-- Total Services: 23 services across all companies
-- Total Reviews: 15 reviews
-- 
-- Location Coverage:
-- - Ba Đình (center): Company 1
-- - Hoàn Kiếm (old quarter): Company 2
-- - Tây Hồ (west): Company 3
-- - Cầu Giấy (north): Company 4
-- - Đống Đa (central): Company 5
-- - Long Biên (far east): Company 6
-- - Thanh Xuân (south): Company 7
--
-- Service Types Coverage:
-- - TOW_TRUCK: 7 companies
-- - TIRE_CHANGE: 5 companies
-- - BATTERY_JUMP: 4 companies
-- - FUEL_DELIVERY: 2 companies
-- - LOCKOUT: 1 company
-- - WINCH_OUT: 2 companies
-- - MECHANICAL_REPAIR: 2 companies
-- - ACCIDENT_RECOVERY: 1 company
-- ============================================
