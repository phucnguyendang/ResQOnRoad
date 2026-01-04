# UC302 - Quản lý Dịch vụ Cứu hộ (Service Management)

**Implementation Complete** ✅

## 📋 Quick Summary

UC302 implementation cung cấp đầy đủ backend API và frontend component để quản lý danh mục dịch vụ cứu hộ của công ty. Admin/Công ty cứu hộ có thể xem, tạo, cập nhật và xóa các dịch vụ.

## 📁 Files Created

### Backend
```
backend/src/main/java/com/rescue/system/
├── controller/
│   └── ServiceController.java           (7 REST endpoints)
├── dto/
│   ├── request/
│   │   ├── CreateServiceRequest.java
│   │   └── UpdateServiceRequest.java
│   └── response/
│       └── ServiceDetailResponse.java
└── service/
    ├── ServiceService.java              (Interface)
    └── impl/
        └── ServiceServiceImpl.java       (Implementation)
```

### Frontend
```
frontend/src/
├── service/
│   └── serviceService.js                (7 methods for API)
└── views/
    ├── ServiceManagementView.jsx        (React component)
    └── ServiceManagementView.css        (Styling)
```

### Documentation
```
UC302_API_DOCUMENTATION.md               (Detailed API docs)
UC302_IMPLEMENTATION.md                  (Implementation summary)
UC302_TEST_CASES.md                      (Test cases & examples)
UC302_QUICK_START.md                     (Quick start guide)
UC302_COMPLETE_SUMMARY.md                (This file)
```

---

## 🚀 Features

### Backend Features
- ✅ **7 REST Endpoints** for full CRUD operations
- ✅ **JWT Authentication** for secure access
- ✅ **Role-Based Access Control** (ADMIN, COMPANY)
- ✅ **Ownership Verification** for data protection
- ✅ **Partial Update Support** for flexible modifications
- ✅ **Comprehensive Validation** for data integrity
- ✅ **Error Handling** with meaningful messages
- ✅ **Service Types Enum** with 8 predefined types
- ✅ **Transactional Operations** for data consistency

### Frontend Features
- ✅ **Service Service Class** with validation utilities
- ✅ **React Component** for managing services
- ✅ **Responsive Design** (mobile, tablet, desktop)
- ✅ **Form Validation** with error messages
- ✅ **CRUD UI** (Create, Read, Update, Delete)
- ✅ **Loading States** and spinners
- ✅ **Error Handling** and user feedback
- ✅ **Service Type Selection** with display names

---

## 🔌 API Endpoints

### 1. Get My Company Services
```
GET /api/services/company/my
Authorization: Bearer {token}
```

### 2. Get Company Services (Public)
```
GET /api/services/company/{companyId}
```

### 3. Get Service Detail
```
GET /api/services/{serviceId}
```

### 4. Create Service
```
POST /api/services
Authorization: Bearer {token}
Content-Type: application/json
{
  "name": "Vá lốp",
  "description": "...",
  "type": "TIRE_CHANGE",
  "basePrice": 150000
}
```

### 5. Update Service
```
PUT /api/services/{serviceId}
Authorization: Bearer {token}
Content-Type: application/json
{
  "basePrice": 200000
}
```

### 6. Delete Service
```
DELETE /api/services/{serviceId}
Authorization: Bearer {token}
```

### 7. Get Available Services
```
GET /api/services/company/{companyId}/available
```

---

## 🎯 Service Types

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

---

## 🧪 Testing

### Backend Tests
Run all endpoints using the examples in `UC302_TEST_CASES.md`:

```bash
# Test 1: Get services
curl -X GET "http://localhost:8080/api/services/company/1"

# Test 2: Create service
curl -X POST "http://localhost:8080/api/services" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"name":"Vá lốp","type":"TIRE_CHANGE","basePrice":150000}'

# Test 3: Update service
curl -X PUT "http://localhost:8080/api/services/1" \
  -H "Authorization: Bearer {token}" \
  -d '{"basePrice":200000}'

# Test 4: Delete service
curl -X DELETE "http://localhost:8080/api/services/1" \
  -H "Authorization: Bearer {token}"
```

### Frontend Tests
```jsx
import ServiceManagementView from './views/ServiceManagementView';

// In your component
<ServiceManagementView companyId={1} isAdmin={true} />
```

---

## 📖 Usage Examples

### Backend - Java
```java
// ServiceService is autowired
serviceService.getServicesByCompanyId(1);
serviceService.createService(accountId, createRequest);
serviceService.updateService(serviceId, accountId, updateRequest);
serviceService.deleteService(serviceId, accountId);
```

### Frontend - JavaScript
```javascript
import ServiceService from './service/serviceService';

// Get services
const response = await ServiceService.getServicesByCompanyId(1);
const services = response.data;

// Create service
const newService = await ServiceService.createService({
  name: 'Vá lốp',
  type: 'TIRE_CHANGE',
  basePrice: 150000
});

// Update service
await ServiceService.updateService(1, {
  basePrice: 200000
});

// Delete service
await ServiceService.deleteService(1);

// Validate service data
const errors = ServiceService.validateService(data);
```

---

## 🔐 Security

### Authentication
- **JWT Token** required for write operations (POST, PUT, DELETE)
- **Token included** in Authorization header: `Bearer {token}`

### Authorization
- **Public Endpoints**: GET requests (read-only)
- **Protected Endpoints**: POST, PUT, DELETE require JWT token
- **Role Requirements**: ADMIN or COMPANY role
- **Ownership Verification**: Users can only modify their company's services

### Validation
- **Required Fields**: name, type, basePrice (for creation)
- **Data Types**: String, Number, Boolean, Enum
- **Business Rules**: Price > 0, valid service type, max lengths

---

## 🛠️ Implementation Details

### Backend Architecture
```
Controller (HTTP Layer)
    ↓
Service (Business Logic)
    ↓
Repository (Data Access)
    ↓
Entity (Database)
```

### Key Classes
- **ServiceController**: REST endpoints
- **ServiceService**: Business logic interface
- **ServiceServiceImpl**: Business logic implementation
- **ServiceRepository**: Data access layer (JPA)
- **Service Entity**: Database model
- **DTOs**: Request/Response objects

### Frontend Architecture
```
Component (UI Layer)
    ↓
Service (API Layer)
    ↓
apiClient (HTTP Client)
    ↓
Backend API
```

### Key Files
- **serviceService.js**: Wrapper around API calls
- **ServiceManagementView.jsx**: Main component
- **ServiceManagementView.css**: Styling

---

## 📊 Data Model

### Service Entity
```
{
  id: Long,
  name: String,
  description: String,
  type: ServiceType (enum),
  basePrice: Double,
  priceUnit: String,
  isAvailable: Boolean,
  estimatedTime: Integer,
  company: RescueCompany,
  createdAt: LocalDateTime
}
```

### Request/Response
```
CreateServiceRequest {
  name: String (required),
  description: String,
  type: String (required),
  basePrice: Double (required),
  priceUnit: String,
  isAvailable: Boolean,
  estimatedTime: Integer
}

ServiceDetailResponse {
  id: Long,
  name: String,
  description: String,
  type: String,
  typeDisplayName: String,
  basePrice: Double,
  priceUnit: String,
  isAvailable: Boolean,
  estimatedTime: Integer,
  createdAt: LocalDateTime
}
```

---

## 🔗 Integration

### With Other Use Cases
- **UC202** - Company Search: Shows available services
- **UC201** - Create Rescue Request: Selects service from company
- **UC402** - User Profile: Views company services

### Database Integration
- Uses existing **Service** entity
- References **RescueCompany** entity
- Links to **Account** entity for authentication

---

## 📝 Error Responses

| Code | Message | Cause |
|------|---------|-------|
| 200 | Lấy/cập nhật thành công | Success |
| 201 | Tạo dịch vụ thành công | Service created |
| 400 | Dữ liệu không hợp lệ | Invalid input |
| 401 | Token không hợp lệ | Auth failed |
| 403 | Không có quyền | No permission |
| 404 | Không tìm thấy | Resource not found |
| 500 | Lỗi server | Server error |

---

## 📚 Documentation Files

1. **UC302_API_DOCUMENTATION.md**
   - Complete API specification
   - Request/response examples
   - Error handling details
   - Integration guide

2. **UC302_TEST_CASES.md**
   - 7 test cases
   - cURL examples
   - PowerShell script
   - Expected responses

3. **UC302_QUICK_START.md**
   - Quick start guide
   - Basic examples
   - Troubleshooting

4. **UC302_IMPLEMENTATION.md**
   - Implementation summary
   - Architecture overview
   - Technology stack
   - File descriptions

---

## 🎓 Learning Resources

### Backend Developers
- Start with: `UC302_IMPLEMENTATION.md`
- Then read: `UC302_API_DOCUMENTATION.md`
- Test with: `UC302_TEST_CASES.md`

### Frontend Developers
- Use: `serviceService.js` for API calls
- Display UI: `ServiceManagementView.jsx`
- Style it: `ServiceManagementView.css`

### DevOps/Testers
- Reference: `UC302_TEST_CASES.md`
- Use provided curl/PowerShell scripts
- Check error responses section

---

## ✅ Checklist

- [x] Backend controller with 7 endpoints
- [x] Service interface and implementation
- [x] DTOs for request/response
- [x] JWT authentication
- [x] Role-based access control
- [x] Ownership verification
- [x] Comprehensive validation
- [x] Error handling
- [x] Frontend service class
- [x] React component with CRUD
- [x] Responsive styling
- [x] Complete documentation
- [x] Test cases with examples
- [x] Integration with existing entities
- [x] Git commits with proper messages

---

## 🚀 Quick Start

### 1. Backend Setup
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

### 2. Get Token
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"company_user","password":"password123"}'
```

### 3. Use API
```bash
curl -X GET "http://localhost:8080/api/services/company/1"
```

### 4. Frontend Setup
```bash
cd frontend
npm install
npm start
```

### 5. Use Component
```jsx
import ServiceManagementView from './views/ServiceManagementView';

<ServiceManagementView companyId={1} isAdmin={true} />
```

---

## 📞 Support

For more information:
- **API Docs**: See `UC302_API_DOCUMENTATION.md`
- **Quick Start**: See `UC302_QUICK_START.md`
- **Tests**: See `UC302_TEST_CASES.md`
- **Implementation**: See `UC302_IMPLEMENTATION.md`

---

## 🎉 Summary

✅ **UC302 is fully implemented!**

**Backend:**
- 7 REST endpoints
- JWT authentication
- Full validation
- Error handling
- 5 Java files

**Frontend:**
- Service class with 7 methods
- React component with CRUD UI
- Responsive design
- 3 JavaScript/JSX files

**Documentation:**
- Comprehensive API docs
- Test cases with examples
- Quick start guide
- Implementation details

All files have been committed to git and are ready for integration and testing!

---

**Last Updated**: January 4, 2026  
**Status**: ✅ Complete and Ready for Testing
