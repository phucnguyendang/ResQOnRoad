# Test Cases for UC302 - Quản lý Dịch vụ Cứu hộ

## Test Setup
```
Base URL: http://localhost:8080/api
Valid Company ID: 1
Valid Service ID: 1
Valid Account ID: 10 (belongs to company 1)
Valid Token: Bearer {JWT_TOKEN_FOR_ACCOUNT_10}
```

---

## TC 1: Get Company Services (Public Endpoint)
```bash
curl -X GET "http://localhost:8080/api/services/company/1" \
  -H "Content-Type: application/json"
```

**Expected Response (200 OK):**
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

## TC 2: Get My Company Services (Authenticated)
```bash
curl -X GET "http://localhost:8080/api/services/company/my" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

**Expected Response (200 OK):**
Same as TC 1

**Expected Response (401 Unauthorized):**
```json
{
  "error": {
    "code": 401,
    "message": "Token không hợp lệ"
  }
}
```

---

## TC 3: Get Service Detail
```bash
curl -X GET "http://localhost:8080/api/services/1" \
  -H "Content-Type: application/json"
```

**Expected Response (200 OK):**
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

## TC 4: Create New Service
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

**Expected Response (201 Created):**
```json
{
  "code": 201,
  "message": "Tạo dịch vụ thành công",
  "data": {
    "id": 2,
    "name": "Thay lốp mới",
    "description": "Thay lốp mới cho xe",
    "type": "TIRE_CHANGE",
    "typeDisplayName": "Vá lốp",
    "basePrice": 300000,
    "priceUnit": "VND",
    "isAvailable": true,
    "estimatedTime": 45,
    "createdAt": "2025-11-20T10:15:00Z"
  }
}
```

**Expected Response (400 Bad Request) - Missing required field:**
```json
{
  "error": {
    "code": 400,
    "message": "Dữ liệu không hợp lệ"
  }
}
```

---

## TC 5: Update Service
```bash
curl -X PUT "http://localhost:8080/api/services/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{
    "basePrice": 200000,
    "estimatedTime": 25,
    "isAvailable": true
  }'
```

**Expected Response (200 OK):**
```json
{
  "code": 200,
  "message": "Cập nhật dịch vụ thành công",
  "data": {
    "id": 1,
    "name": "Vá lốp",
    "description": "Sửa chữa lốp xe bị thủng",
    "type": "TIRE_CHANGE",
    "typeDisplayName": "Vá lốp",
    "basePrice": 200000,
    "priceUnit": "VND",
    "isAvailable": true,
    "estimatedTime": 25,
    "createdAt": "2025-11-20T10:00:00Z"
  }
}
```

**Expected Response (403 Forbidden) - Different company:**
```json
{
  "error": {
    "code": 400,
    "message": "Bạn không có quyền cập nhật dịch vụ này"
  }
}
```

---

## TC 6: Delete Service
```bash
curl -X DELETE "http://localhost:8080/api/services/1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

**Expected Response (200 OK):**
```json
{
  "code": 200,
  "message": "Xóa dịch vụ thành công",
  "data": null
}
```

---

## TC 7: Get Available Services
```bash
curl -X GET "http://localhost:8080/api/services/company/1/available" \
  -H "Content-Type: application/json"
```

**Expected Response (200 OK):**
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

## Service Types Enum Values
- `TOW_TRUCK` - Cẩu xe
- `TIRE_CHANGE` - Vá lốp
- `BATTERY_JUMP` - Cứu hộ ắc quy
- `FUEL_DELIVERY` - Giao nhiên liệu
- `LOCKOUT` - Mở khóa xe
- `WINCH_OUT` - Kéo xe bị sa lầy
- `ACCIDENT_RECOVERY` - Cứu hộ tai nạn
- `MECHANICAL_REPAIR` - Sửa chữa cơ bản

---

## PowerShell Test Script
```powershell
# Set your variables
$token = "YOUR_JWT_TOKEN"
$baseUrl = "http://localhost:8080/api"
$companyId = 1

# Test 1: Get services by company
Write-Host "Test 1: Get services by company"
$response = Invoke-WebRequest -Uri "$baseUrl/services/company/$companyId" -Method Get
Write-Host $response.Content

# Test 2: Get available services
Write-Host "Test 2: Get available services"
$response = Invoke-WebRequest -Uri "$baseUrl/services/company/$companyId/available" -Method Get
Write-Host $response.Content

# Test 3: Create new service
Write-Host "Test 3: Create new service"
$body = @{
    name = "Vá lốp"
    description = "Sửa chữa lốp xe bị thủng"
    type = "TIRE_CHANGE"
    basePrice = 150000
    priceUnit = "VND"
    isAvailable = $true
    estimatedTime = 30
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "$baseUrl/services" `
  -Method Post `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token" } `
  -Body $body
Write-Host $response.Content

# Test 4: Update service
Write-Host "Test 4: Update service"
$serviceId = 1
$body = @{
    basePrice = 200000
    estimatedTime = 25
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "$baseUrl/services/$serviceId" `
  -Method Put `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token" } `
  -Body $body
Write-Host $response.Content

# Test 5: Delete service
Write-Host "Test 5: Delete service"
$response = Invoke-WebRequest -Uri "$baseUrl/services/$serviceId" `
  -Method Delete `
  -Headers @{ Authorization = "Bearer $token" }
Write-Host $response.StatusCode
```

---

## Notes
- All authenticated endpoints require a valid JWT token with `ADMIN` or `COMPANY` role
- Service ownership verification is performed on update/delete operations
- Empty/null fields in update request are preserved (partial update)
- Service type must match one of the predefined enum values
