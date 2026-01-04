# UC302 - Quản lý Dịch vụ Cứu hộ - Implementation Summary

## Overview
UC302 implementation cung cấp đầy đủ API để quản lý danh mục dịch vụ cứu hộ. Admin/Công ty cứu hộ có thể xem, tạo, cập nhật và xóa các dịch vụ như vá lốp, thay lốp, nạp nhiên liệu, kéo xe, sửa chữa.

---

## Files Created

### 1. DTOs (Data Transfer Objects)

#### CreateServiceRequest.java
- **Location**: `backend/src/main/java/com/rescue/system/dto/request/`
- **Purpose**: DTO để tạo dịch vụ mới
- **Fields**:
  - `name` (String, bắt buộc): Tên dịch vụ
  - `description` (String, tùy chọn): Mô tả chi tiết
  - `type` (String, bắt buộc): Loại dịch vụ (enum)
  - `basePrice` (Double, bắt buộc): Giá cơ bản
  - `priceUnit` (String, tùy chọn): Đơn vị giá (mặc định: "VND")
  - `isAvailable` (Boolean, tùy chọn): Trạng thái khả dụng (mặc định: true)
  - `estimatedTime` (Integer, tùy chọn): Thời gian ước tính (phút)

#### UpdateServiceRequest.java
- **Location**: `backend/src/main/java/com/rescue/system/dto/request/`
- **Purpose**: DTO để cập nhật dịch vụ
- **Note**: Tất cả các trường đều tùy chọn (cho phép cập nhật từng phần)

#### ServiceDetailResponse.java
- **Location**: `backend/src/main/java/com/rescue/system/dto/response/`
- **Purpose**: DTO cho phản hồi chi tiết dịch vụ
- **Fields**:
  - `id` (Long): ID dịch vụ
  - `name` (String): Tên dịch vụ
  - `description` (String): Mô tả
  - `type` (String): Loại dịch vụ (enum)
  - `typeDisplayName` (String): Tên hiển thị của loại
  - `basePrice` (Double): Giá cơ bản
  - `priceUnit` (String): Đơn vị giá
  - `isAvailable` (Boolean): Trạng thái khả dụng
  - `estimatedTime` (Integer): Thời gian ước tính
  - `createdAt` (LocalDateTime): Thời gian tạo

### 2. Service Layer

#### ServiceService Interface
- **Location**: `backend/src/main/java/com/rescue/system/service/`
- **Methods**:
  - `getServicesByCompanyId(Long companyId)` - Lấy tất cả dịch vụ của công ty
  - `getServiceById(Long serviceId)` - Lấy chi tiết dịch vụ
  - `createService(Long accountId, CreateServiceRequest)` - Tạo dịch vụ mới
  - `updateService(Long serviceId, Long accountId, UpdateServiceRequest)` - Cập nhật dịch vụ
  - `deleteService(Long serviceId, Long accountId)` - Xóa dịch vụ
  - `getAvailableServicesByCompanyId(Long companyId)` - Lấy dịch vụ khả dụng

#### ServiceServiceImpl Implementation
- **Location**: `backend/src/main/java/com/rescue/system/service/impl/`
- **Features**:
  - Xác thực quyền sở hữu dịch vụ (account phải thuộc công ty sở hữu dịch vụ)
  - Cập nhật từng phần (partial update)
  - Gọi AccountRepository để lấy thông tin công ty từ account
  - Sử dụng @Transactional cho các operation sửa đổi dữ liệu

### 3. Controller

#### ServiceController
- **Location**: `backend/src/main/java/com/rescue/system/controller/`
- **Base URL**: `/api/services`
- **Endpoints**:
  ```
  GET    /api/services/company/my                    - Lấy dịch vụ của công ty hiện tại (Auth)
  GET    /api/services/company/{companyId}           - Lấy dịch vụ của công ty (Public)
  GET    /api/services/{serviceId}                   - Lấy chi tiết dịch vụ (Public)
  POST   /api/services                               - Tạo dịch vụ mới (Auth)
  PUT    /api/services/{serviceId}                   - Cập nhật dịch vụ (Auth)
  DELETE /api/services/{serviceId}                   - Xóa dịch vụ (Auth)
  GET    /api/services/company/{companyId}/available - Lấy dịch vụ khả dụng (Public)
  ```

- **Security**:
  - Sử dụng `@PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY')")` cho write operations
  - Kiểm tra JWT token để lấy accountId
  - Xác thực quyền truy cập trên service layer

---

## Database Integration

### Entity Used
- **Service**: Entity đã tồn tại, chứa thông tin dịch vụ
- **RescueCompany**: Công ty cứu hộ, có quan hệ 1-N với Service
- **Account**: Tài khoản người dùng, có field `companyId` để xác định công ty

### Repository
- **ServiceRepository**: Đã tồn tại, hỗ trợ:
  - `findByCompanyId(Long companyId)` - Lấy tất cả dịch vụ
  - `findByCompanyIdAndIsAvailableTrue(Long companyId)` - Lấy dịch vụ khả dụng
  - CRUD operations tiêu chuẩn

---

## Business Logic

### Luồng chính (Main Flow - UC302)
1. Admin/Công ty truy cập giao diện quản lý dịch vụ
2. Hệ thống hiển thị danh sách dịch vụ hiện tại (`GET /services/company/my`)
3. Admin/Công ty cập nhật thông tin dịch vụ
4. Hệ thống hiển thị cửa sổ xác nhận
5. Admin xác nhận thay đổi (`PUT /services/{id}`)
6. Hệ thống hiển thị thông tin sau thay đổi

### Luồng thay thế
- Nếu không có nhu cầu thay đổi, không cần thực hiện bất kỳ chỉnh sửa nào

### Xác thực & Phân quyền
- **Lấy dịch vụ (GET)**: Công khai, không yêu cầu authentication
- **Tạo dịch vụ (POST)**: Yêu cầu JWT token, role ADMIN hoặc COMPANY
- **Cập nhật/Xóa (PUT/DELETE)**: 
  - Yêu cầu JWT token, role ADMIN hoặc COMPANY
  - Account phải thuộc công ty sở hữu dịch vụ

---

## Service Types Enum
```java
TOW_TRUCK("Cẩu xe")
TIRE_CHANGE("Vá lốp")
BATTERY_JUMP("Cứu hộ ắc quy")
FUEL_DELIVERY("Giao nhiên liệu")
LOCKOUT("Mở khóa xe")
WINCH_OUT("Kéo xe bị sa lầy")
ACCIDENT_RECOVERY("Cứu hộ tai nạn")
MECHANICAL_REPAIR("Sửa chữa cơ bản")
```

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

| HTTP Code | Message | Cause |
|-----------|---------|-------|
| 200 | Thành công | Lấy/cập nhật dữ liệu thành công |
| 201 | Tạo thành công | Dịch vụ được tạo thành công |
| 400 | Bad Request | Dữ liệu không hợp lệ hoặc không có quyền |
| 401 | Unauthorized | Token không hợp lệ hoặc hết hạn |
| 404 | Not Found | Dịch vụ hoặc công ty không tồn tại |
| 500 | Internal Error | Lỗi server |

---

## Integration with Other Features

### UC202 - Company Search (Tìm kiếm công ty)
- Khi tìm kiếm, API sẽ hiển thị danh sách dịch vụ khả dụng của công ty
- Sử dụng `getAvailableServicesByCompanyId()` để lấy dịch vụ

### UC402 - User Profile (Hồ sơ người dùng)
- Người dùng có thể xem danh sách dịch vụ khi chọn công ty cứu hộ
- Sử dụng `getServicesByCompanyId()` hoặc `getServiceById()`

### UC201 - Create Rescue Request (Tạo yêu cầu cứu hộ)
- Người dùng xem danh sách dịch vụ của công ty được chọn
- Dịch vụ được liên kết với yêu cầu cứu hộ

---

## Testing

### Test Cases Provided
File `UC302_TEST_CASES.md` chứa 7 test cases chi tiết:
1. Get Company Services (Public)
2. Get My Company Services (Authenticated)
3. Get Service Detail
4. Create New Service
5. Update Service
6. Delete Service
7. Get Available Services

### PowerShell Test Script
Kèm theo file test cases, có script PowerShell để test tất cả endpoints

---

## Documentation Files Created

1. **UC302_API_DOCUMENTATION.md** - Đầy đủ tài liệu API
2. **UC302_TEST_CASES.md** - Các trường hợp kiểm tra với curl/PowerShell
3. **UC302_IMPLEMENTATION.md** - File này, tóm tắt implementation

---

## How to Use

### 1. Build & Run Backend
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

### 2. Get JWT Token
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "company_user",
    "password": "password123"
  }'
```

### 3. Use Service Endpoints
```bash
# Get services for company
curl -X GET "http://localhost:8080/api/services/company/1"

# Create new service (with token)
curl -X POST "http://localhost:8080/api/services" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"name": "Vá lốp", "type": "TIRE_CHANGE", "basePrice": 150000}'
```

---

## Notes

### Important Implementation Details
1. **Service vs Annotation Conflict**: Sử dụng `@Component` thay vì `@Service` trong ServiceServiceImpl để tránh conflict với Service entity
2. **Partial Update**: UpdateServiceRequest cho phép update từng phần - chỉ cập nhật fields không null
3. **Ownership Verification**: Kiểm tra account thuộc công ty trước khi thực hiện update/delete
4. **JWT Token Extraction**: Lấy accountId từ token, rồi từ account lấy companyId

### Future Enhancements
1. Thêm pagination cho danh sách dịch vụ (get many)
2. Thêm filtering theo type hoặc trạng thái khả dụng
3. Thêm audit logging cho các thay đổi dịch vụ
4. Thêm validation phức tạp hơn (VD: kiểm tra giá hợp lý)
5. Thêm soft delete thay vì hard delete

---

## Checklist

- [x] Create DTOs (CreateServiceRequest, UpdateServiceRequest, ServiceDetailResponse)
- [x] Create ServiceService interface
- [x] Create ServiceServiceImpl implementation
- [x] Create ServiceController with 7 endpoints
- [x] Implement proper validation
- [x] Implement security & authorization
- [x] Write comprehensive API documentation
- [x] Write test cases with examples
- [x] Handle errors appropriately
- [x] Verify compilation (except pre-existing errors)

---

## Support & Documentation

For detailed information:
- **API Documentation**: See `UC302_API_DOCUMENTATION.md`
- **Test Cases**: See `UC302_TEST_CASES.md`
- **API Spec**: See root `api_docs.md`
- **Use Case Spec**: See UC302 specification provided

All files are located in `/home/tlam/codes/ResQOnRoad/`
