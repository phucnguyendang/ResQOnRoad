# UC302 - Quản lý Dịch vụ Cứu hộ (Service Management)

## Overview

UC302 cung cấp các API để quản lý danh mục dịch vụ cứu hộ của công ty. Admin/Công ty cứu hộ có thể xem, tạo, cập nhật và xóa các dịch vụ như vá lốp, thay lốp, nạp nhiên liệu, kéo xe, sửa chữa tại chỗ.

## API Endpoints

### 1. Lấy danh sách dịch vụ của công ty hiện tại (Authenticated)
```
GET /api/services/company/my
Authorization: Bearer {token}
```

**Description:** Lấy danh sách tất cả dịch vụ của công ty hiện tại (từ token)

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Lấy danh sách dịch vụ thành công",
  "data": [
    {
      "id": 1,
      "name": "Vá lốp",
      "description": "Sửa chữa lốp xe bị thủng",
      "type": "TIRE_CHANGE",
      "typeDisplayName": "Vá lốp",
      "basePrice": 150000,
      "priceUnit": "VND",
      "isAvailable": true,
      "estimatedTime": 30,
      "createdAt": "2025-11-20T10:00:00Z"
    },
    {
      "id": 2,
      "name": "Thay lốp",
      "description": "Thay lốp mới cho xe",
      "type": "TIRE_CHANGE",
      "typeDisplayName": "Vá lốp",
      "basePrice": 300000,
      "priceUnit": "VND",
      "isAvailable": true,
      "estimatedTime": 45,
      "createdAt": "2025-11-20T10:05:00Z"
    }
  ]
}
```

**Error Responses:**
- `401 Unauthorized`: Token không hợp lệ
- `404 Not Found`: Công ty không tồn tại
- `500 Internal Server Error`: Lỗi server

---

### 2. Lấy danh sách dịch vụ của công ty (Public)
```
GET /api/services/company/{companyId}
```

**Description:** Lấy danh sách dịch vụ của công ty theo company ID (công khai)

**Parameters:**
- `companyId` (path): ID của công ty cứu hộ

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Lấy danh sách dịch vụ thành công",
  "data": [
    {
      "id": 1,
      "name": "Vá lốp",
      "description": "Sửa chữa lốp xe bị thủng",
      "type": "TIRE_CHANGE",
      "typeDisplayName": "Vá lốp",
      "basePrice": 150000,
      "priceUnit": "VND",
      "isAvailable": true,
      "estimatedTime": 30,
      "createdAt": "2025-11-20T10:00:00Z"
    }
  ]
}
```

---

### 3. Lấy chi tiết dịch vụ
```
GET /api/services/{serviceId}
```

**Description:** Lấy chi tiết thông tin của một dịch vụ cụ thể

**Parameters:**
- `serviceId` (path): ID của dịch vụ

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Lấy chi tiết dịch vụ thành công",
  "data": {
    "id": 1,
    "name": "Vá lốp",
    "description": "Sửa chữa lốp xe bị thủng",
    "type": "TIRE_CHANGE",
    "typeDisplayName": "Vá lốp",
    "basePrice": 150000,
    "priceUnit": "VND",
    "isAvailable": true,
    "estimatedTime": 30,
    "createdAt": "2025-11-20T10:00:00Z"
  }
}
```

---

### 4. Tạo dịch vụ mới (UC302 - Step 3)
```
POST /api/services
Authorization: Bearer {token}
Content-Type: application/json
```

**Description:** Tạo dịch vụ mới cho công ty. Chỉ Admin hoặc Công ty cứu hộ mới có thể thực hiện.

**Request Body:**
```json
{
  "name": "Vá lốp",
  "description": "Sửa chữa lốp xe bị thủng",
  "type": "TIRE_CHANGE",
  "basePrice": 150000,
  "priceUnit": "VND",
  "isAvailable": true,
  "estimatedTime": 30
}
```

**Request Validation:**
- `name`: Bắt buộc, không được để trống (max 100 ký tự)
- `type`: Bắt buộc, phải là một trong các giá trị hợp lệ
- `basePrice`: Bắt buộc, phải > 0
- `priceUnit`: Tùy chọn (mặc định: "VND")
- `isAvailable`: Tùy chọn (mặc định: true)
- `estimatedTime`: Tùy chọn (đơn vị: phút)

**Service Types (Enum):**
- `TOW_TRUCK` - Cẩu xe
- `TIRE_CHANGE` - Vá lốp
- `BATTERY_JUMP` - Cứu hộ ắc quy
- `FUEL_DELIVERY` - Giao nhiên liệu
- `LOCKOUT` - Mở khóa xe
- `WINCH_OUT` - Kéo xe bị sa lầy
- `ACCIDENT_RECOVERY` - Cứu hộ tai nạn
- `MECHANICAL_REPAIR` - Sửa chữa cơ bản

**Response (201 Created):**
```json
{
  "code": 201,
  "message": "Tạo dịch vụ thành công",
  "data": {
    "id": 1,
    "name": "Vá lốp",
    "description": "Sửa chữa lốp xe bị thủng",
    "type": "TIRE_CHANGE",
    "typeDisplayName": "Vá lốp",
    "basePrice": 150000,
    "priceUnit": "VND",
    "isAvailable": true,
    "estimatedTime": 30,
    "createdAt": "2025-11-20T10:00:00Z"
  }
}
```

**Error Responses:**
- `400 Bad Request`: Dữ liệu không hợp lệ
- `401 Unauthorized`: Token không hợp lệ
- `403 Forbidden`: Không có quyền tạo dịch vụ

---

### 5. Cập nhật dịch vụ (UC302 - Step 3, 4, 5)
```
PUT /api/services/{serviceId}
Authorization: Bearer {token}
Content-Type: application/json
```

**Description:** Cập nhật thông tin dịch vụ. Hệ thống hiển thị xác nhận, admin xác nhận thay đổi, hệ thống hiển thị thông tin sau thay đổi.

**Parameters:**
- `serviceId` (path): ID của dịch vụ cần cập nhật

**Request Body (tất cả trường là tùy chọn):**
```json
{
  "name": "Vá lốp nhanh",
  "description": "Sửa chữa lốp xe bị thủng - dịch vụ cao cấp",
  "type": "TIRE_CHANGE",
  "basePrice": 200000,
  "priceUnit": "VND",
  "isAvailable": true,
  "estimatedTime": 20
}
```

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Cập nhật dịch vụ thành công",
  "data": {
    "id": 1,
    "name": "Vá lốp nhanh",
    "description": "Sửa chữa lốp xe bị thủng - dịch vụ cao cấp",
    "type": "TIRE_CHANGE",
    "typeDisplayName": "Vá lốp",
    "basePrice": 200000,
    "priceUnit": "VND",
    "isAvailable": true,
    "estimatedTime": 20,
    "createdAt": "2025-11-20T10:00:00Z"
  }
}
```

**Error Responses:**
- `400 Bad Request`: Dữ liệu không hợp lệ hoặc không có quyền
- `401 Unauthorized`: Token không hợp lệ
- `404 Not Found`: Dịch vụ không tồn tại

---

### 6. Xóa dịch vụ
```
DELETE /api/services/{serviceId}
Authorization: Bearer {token}
```

**Description:** Xóa dịch vụ khỏi danh mục

**Parameters:**
- `serviceId` (path): ID của dịch vụ cần xóa

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Xóa dịch vụ thành công",
  "data": null
}
```

**Error Responses:**
- `400 Bad Request`: Không có quyền xóa
- `401 Unauthorized`: Token không hợp lệ
- `404 Not Found`: Dịch vụ không tồn tại

---

### 7. Lấy danh sách dịch vụ khả dụng
```
GET /api/services/company/{companyId}/available
```

**Description:** Lấy danh sách các dịch vụ khả dụng (isAvailable = true) của công ty

**Parameters:**
- `companyId` (path): ID của công ty cứu hộ

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Lấy danh sách dịch vụ khả dụng thành công",
  "data": [
    {
      "id": 1,
      "name": "Vá lốp",
      "description": "Sửa chữa lốp xe bị thủng",
      "type": "TIRE_CHANGE",
      "typeDisplayName": "Vá lốp",
      "basePrice": 150000,
      "priceUnit": "VND",
      "isAvailable": true,
      "estimatedTime": 30,
      "createdAt": "2025-11-20T10:00:00Z"
    }
  ]
}
```

---

## Data Model

### Service Entity
```java
{
  "id": Long,                    // Service ID
  "name": String,                // Tên dịch vụ
  "description": String,         // Mô tả chi tiết
  "type": ServiceType,           // Loại dịch vụ (enum)
  "typeDisplayName": String,     // Tên hiển thị của loại dịch vụ
  "basePrice": Double,           // Giá cơ bản
  "priceUnit": String,           // Đơn vị giá (VND, USD, per km, per hour)
  "isAvailable": Boolean,        // Trạng thái khả dụng
  "estimatedTime": Integer,      // Thời gian ước tính (phút)
  "company": RescueCompany,      // Công ty cung cấp dịch vụ
  "createdAt": LocalDateTime     // Thời gian tạo
}
```

---

## Business Logic

### Luồng chính (Main Flow)
1. Admin/Công ty truy cập vào giao diện quản lý dịch vụ
2. Hệ thống hiển thị danh sách dịch vụ hiện tại
3. Admin/Công ty cập nhật thông tin dịch vụ
4. Hệ thống hiển thị cửa sổ xác nhận
5. Admin xác nhận thay đổi
6. Hệ thống hiển thị thông tin sau thay đổi

### Luồng thay thế (Alternative Flow)
- Nếu không có nhu cầu thay đổi, không cần thực hiện bất kỳ chỉnh sửa nào

### Tiền điều kiện
- Admin/Công ty cứu hộ phải đăng nhập hệ thống
- Phải có quyền truy cập để quản lý dịch vụ
- Dữ liệu công ty phải tồn tại trong hệ thống

### Hậu điều kiện
- Thông tin dịch vụ cứu hộ được lưu trữ
- Thay đổi được phản ánh trong toàn bộ hệ thống
- Người dùng có thể xem dịch vụ đã cập nhật khi tìm kiếm công ty

---

## Validation Rules

| Field | Rule | Message |
|-------|------|---------|
| name | Bắt buộc, max 100 | "Tên dịch vụ không được để trống" |
| type | Bắt buộc, hợp lệ | "Loại dịch vụ không được để trống" |
| basePrice | Bắt buộc, > 0 | "Giá dịch vụ phải lớn hơn 0" |
| priceUnit | Tùy chọn | Mặc định: "VND" |
| isAvailable | Tùy chọn | Mặc định: true |
| estimatedTime | Tùy chọn | - |

---

## Error Handling

| Code | Status | Message | Cause |
|------|--------|---------|-------|
| 400 | Bad Request | Dữ liệu không hợp lệ | Dữ liệu đầu vào không đúng định dạng |
| 401 | Unauthorized | Token không hợp lệ | Token hết hạn hoặc không tồn tại |
| 403 | Forbidden | Không có quyền | Người dùng không phải admin/công ty hoặc không sở hữu dịch vụ |
| 404 | Not Found | Không tìm thấy tài nguyên | Dịch vụ hoặc công ty không tồn tại |
| 500 | Internal Server Error | Lỗi server | Lỗi không mong muốn trên server |

---

## Integration with Other Features

### UC202 - Company Search
- Khi tìm kiếm công ty, API sẽ trả về danh sách dịch vụ khả dụng

### UC302 - Service Management
- Admin/Công ty có thể quản lý các dịch vụ được cung cấp

### UC402 - User Profile
- Người dùng có thể xem danh sách dịch vụ khi chọn công ty cứu hộ

---

## Implementation Notes

### Technology Stack
- **Framework**: Spring Boot 3.3.5
- **Database**: SQLite
- **ORM**: Hibernate JPA
- **Security**: JWT Token

### Files Created
1. `CreateServiceRequest.java` - DTO cho tạo dịch vụ
2. `UpdateServiceRequest.java` - DTO cho cập nhật dịch vụ
3. `ServiceDetailResponse.java` - DTO cho phản hồi chi tiết dịch vụ
4. `ServiceService.java` - Service interface
5. `ServiceServiceImpl.java` - Service implementation
6. `ServiceController.java` - REST Controller

### Repository Methods Used
- `ServiceRepository.findByCompanyId(Long companyId)` - Lấy tất cả dịch vụ
- `ServiceRepository.findByCompanyIdAndIsAvailableTrue(Long companyId)` - Lấy dịch vụ khả dụng
- `ServiceRepository.findById(Long id)` - Lấy dịch vụ theo ID
- `ServiceRepository.save(Service service)` - Lưu dịch vụ
- `ServiceRepository.delete(Service service)` - Xóa dịch vụ

---

## Testing Scenarios

### Test Case 1: Tạo dịch vụ mới
```
POST /api/services
Headers: Authorization: Bearer {valid_token}
Body: {
  "name": "Vá lốp",
  "type": "TIRE_CHANGE",
  "basePrice": 150000
}
Expected: 201 Created
```

### Test Case 2: Cập nhật dịch vụ
```
PUT /api/services/1
Headers: Authorization: Bearer {valid_token}
Body: {
  "basePrice": 200000,
  "isAvailable": false
}
Expected: 200 OK
```

### Test Case 3: Lấy danh sách dịch vụ
```
GET /api/services/company/{companyId}
Expected: 200 OK, trả về danh sách dịch vụ
```

### Test Case 4: Xóa dịch vụ
```
DELETE /api/services/1
Headers: Authorization: Bearer {valid_token}
Expected: 200 OK
```
