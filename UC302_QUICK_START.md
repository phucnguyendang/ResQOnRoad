# UC302 - Quản lý Dịch vụ Cứu hộ (Service Management) 

## Quick Start Guide

Hướng dẫn nhanh để sử dụng UC302 - Quản lý dịch vụ cứu hộ của ResQOnRoad.

---

## 📋 Nội dung

1. [Overview](#overview)
2. [API Endpoints](#api-endpoints)
3. [Authentication](#authentication)
4. [Usage Examples](#usage-examples)
5. [Service Types](#service-types)
6. [Error Handling](#error-handling)
7. [Files Created](#files-created)

---

## Overview

UC302 cung cấp một bộ API đầy đủ để quản lý danh mục dịch vụ cứu hộ. 

**Chức năng chính:**
- ✅ Xem danh sách dịch vụ của công ty
- ✅ Tạo dịch vụ mới
- ✅ Cập nhật thông tin dịch vụ
- ✅ Xóa dịch vụ
- ✅ Lấy danh sách dịch vụ khả dụng
- ✅ Kiểm soát quyền truy cập theo account

---

## API Endpoints

| HTTP | Endpoint | Description | Auth Required |
|------|----------|-------------|---|
| GET | `/api/services/company/my` | Lấy dịch vụ của công ty hiện tại | ✅ Yes |
| GET | `/api/services/company/{companyId}` | Lấy dịch vụ của công ty theo ID | ❌ No |
| GET | `/api/services/{serviceId}` | Lấy chi tiết dịch vụ | ❌ No |
| POST | `/api/services` | Tạo dịch vụ mới | ✅ Yes |
| PUT | `/api/services/{serviceId}` | Cập nhật dịch vụ | ✅ Yes |
| DELETE | `/api/services/{serviceId}` | Xóa dịch vụ | ✅ Yes |
| GET | `/api/services/company/{companyId}/available` | Lấy dịch vụ khả dụng | ❌ No |

---

## Authentication

### Get JWT Token

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "company_admin",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "message": "Đăng nhập thành công",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "account_id": 10,
    "role": "COMPANY"
  }
}
```

---

## Usage Examples

### 1️⃣ Get My Company Services

```bash
curl -X GET "http://localhost:8080/api/services/company/my" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

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

### 2️⃣ Create New Service

```bash
curl -X POST "http://localhost:8080/api/services" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{
    "name": "Thay lốp mới",
    "description": "Thay lốp mới cho xe",
    "type": "TIRE_CHANGE",
    "basePrice": 300000,
    "priceUnit": "VND",
    "isAvailable": true,
    "estimatedTime": 45
  }'
```

**Response (201 Created):**
```json
{
  "code": 201,
  "message": "Tạo dịch vụ thành công",
  "data": {
    "id": 2,
    "name": "Thay lốp mới",
    "type": "TIRE_CHANGE",
    "basePrice": 300000,
    "isAvailable": true,
    "estimatedTime": 45,
    "createdAt": "2025-11-20T10:15:00Z"
  }
}
```

---

### 3️⃣ Update Service

```bash
curl -X PUT "http://localhost:8080/api/services/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{
    "basePrice": 200000,
    "estimatedTime": 25
  }'
```

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Cập nhật dịch vụ thành công",
  "data": {
    "id": 1,
    "name": "Vá lốp",
    "basePrice": 200000,
    "estimatedTime": 25,
    "isAvailable": true,
    "createdAt": "2025-11-20T10:00:00Z"
  }
}
```

---

### 4️⃣ Delete Service

```bash
curl -X DELETE "http://localhost:8080/api/services/1" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Xóa dịch vụ thành công",
  "data": null
}
```

---

### 5️⃣ Get Available Services

```bash
curl -X GET "http://localhost:8080/api/services/company/1/available"
```

**Response (200 OK):**
```json
{
  "code": 200,
  "message": "Lấy danh sách dịch vụ khả dụng thành công",
  "data": [
    {
      "id": 1,
      "name": "Vá lốp",
      "isAvailable": true
    }
  ]
}
```

---

## Service Types

Danh sách các loại dịch vụ (ServiceType enum):

```
TOW_TRUCK         - Cẩu xe
TIRE_CHANGE       - Vá lốp
BATTERY_JUMP      - Cứu hộ ắc quy
FUEL_DELIVERY     - Giao nhiên liệu
LOCKOUT           - Mở khóa xe
WINCH_OUT         - Kéo xe bị sa lầy
ACCIDENT_RECOVERY - Cứu hộ tai nạn
MECHANICAL_REPAIR - Sửa chữa cơ bản
```

Khi tạo/cập nhật dịch vụ, sử dụng một trong các giá trị trên cho field `type`.

---

## Error Handling

### Common Errors

**400 Bad Request** - Dữ liệu không hợp lệ
```json
{
  "error": {
    "code": 400,
    "message": "Dữ liệu không hợp lệ",
    "details": [
      "name không được để trống",
      "basePrice phải lớn hơn 0"
    ]
  }
}
```

**401 Unauthorized** - Token không hợp lệ
```json
{
  "error": {
    "code": 401,
    "message": "Token không hợp lệ"
  }
}
```

**403 Forbidden** - Không có quyền
```json
{
  "error": {
    "code": 400,
    "message": "Bạn không có quyền cập nhật dịch vụ này"
  }
}
```

**404 Not Found** - Không tìm thấy tài nguyên
```json
{
  "error": {
    "code": 404,
    "message": "Dịch vụ không tồn tại"
  }
}
```

---

## Files Created

### Backend Source Files
```
backend/src/main/java/com/rescue/system/
├── controller/
│   └── ServiceController.java
├── dto/
│   ├── request/
│   │   ├── CreateServiceRequest.java
│   │   └── UpdateServiceRequest.java
│   └── response/
│       └── ServiceDetailResponse.java
└── service/
    ├── ServiceService.java
    └── impl/
        └── ServiceServiceImpl.java
```

### Documentation Files
```
UC302_API_DOCUMENTATION.md    - Tài liệu API đầy đủ
UC302_IMPLEMENTATION.md        - Tóm tắt implementation
UC302_TEST_CASES.md           - Các trường hợp kiểm tra
UC302_QUICK_START.md          - File này
```

---

## Integration

### UC202 - Company Search
Khi tìm kiếm công ty, API trả về danh sách dịch vụ khả dụng:
```json
{
  "services": [
    { "name": "Vá lốp", "price": 150000 },
    { "name": "Thay lốp", "price": 300000 }
  ]
}
```

### UC201 - Create Rescue Request
Người dùng có thể xem danh sách dịch vụ của công ty được chọn.

### UC402 - User Profile
Hiển thị danh sách dịch vụ khi người dùng xem hồ sơ công ty.

---

## Key Features

✅ **Xác thực & Phân quyền**
- JWT token-based authentication
- Role-based access control (ADMIN, COMPANY)
- Ownership verification

✅ **Validation**
- Required field validation
- Data type validation
- Business logic validation

✅ **Partial Update**
- Chỉ cập nhật fields được cung cấp
- Bảo tồn giá trị cũ cho fields không được update

✅ **Error Handling**
- Comprehensive error messages
- HTTP status codes
- Detailed error details

✅ **Documentation**
- Full API documentation
- Test cases with examples
- Implementation guide

---

## Testing

### Using Postman

1. Import Postman collection từ test files
2. Set environment variables:
   - `base_url`: http://localhost:8080
   - `token`: Lấy từ login endpoint
3. Run các requests

### Using cURL

Xem chi tiết trong `UC302_TEST_CASES.md`

### Using PowerShell

Script PowerShell sẵn sàng trong `UC302_TEST_CASES.md`

---

## Troubleshooting

### Problem: "Token không hợp lệ"
- **Solution**: Đảm bảo token chưa hết hạn, format đúng: `Bearer {token}`

### Problem: "Bạn không có quyền"
- **Solution**: Kiểm tra account có thuộc công ty sở hữu dịch vụ không

### Problem: "Dịch vụ không tồn tại"
- **Solution**: Kiểm tra serviceId có chính xác không

### Problem: "Công ty cứu hộ không tồn tại"
- **Solution**: Kiểm tra account có được gán cho công ty không

---

## Support

Để thêm thông tin:
- Xem chi tiết: `UC302_API_DOCUMENTATION.md`
- Xem test cases: `UC302_TEST_CASES.md`
- Xem implementation: `UC302_IMPLEMENTATION.md`

---

## Summary

UC302 implementation cung cấp:
- ✅ 7 REST endpoints
- ✅ Full authentication & authorization
- ✅ Comprehensive validation
- ✅ Error handling
- ✅ Complete documentation
- ✅ Test cases with examples

Tất cả file đã được commit vào git repository.

**Happy coding! 🚀**
