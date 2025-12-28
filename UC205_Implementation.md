# UC205 - Xử Lý Yêu Cầu (Company Handling Rescue Requests)

## Frontend Implementation Status: ✅ COMPLETED

### Giao diện đã được xây dựng:
1. **CompanyDashboard** - Bảng điều khiển công ty
   - Danh sách yêu cầu được giao
   - Thống kê theo trạng thái
   - Bộ lọc theo trạng thái
   - Nút tải lại dữ liệu

2. **RescueRequestCard** - Thẻ hiển thị yêu cầu
   - Thông tin cơ bản (ID, địa chỉ, loại dịch vụ)
   - Trạng thái với màu sắc
   - Click để xem chi tiết

3. **RescueRequestDetail** - Chi tiết yêu cầu cụ thể
   - Thông tin người gửi yêu cầu
   - Vị trí (địa chỉ, tọa độ)
   - Mô tả chi tiết
   - **Nút mở Google Maps** với tọa độ
   - Các nút cập nhật trạng thái:
     - "Chấp Nhận Yêu Cầu" (từ PENDING_CONFIRMATION)
     - "Đang Tới" (IN_TRANSIT)
     - "Đang Xử Lý" (IN_PROGRESS)
     - "Hoàn Thành" (COMPLETED)
     - "Từ Chối" (với modal nhập lý do)
   - Modal từ chối với textarea để nhập lý do

### Navigation:
- Menu Header: "Bảng Điều Khiển" (chỉ hiển thị cho COMPANY)
- Đường dẫn: App.jsx → currentView === 'company-dashboard'

---

## Backend API Status

### ✅ APIs Đã Có (Working):

1. **GET /api/rescue-requests/company/assigned**
   - Lấy danh sách yêu cầu được giao cho công ty
   - Requires: COMPANY role, Bearer token
   - Response: List<RescueRequestDto>

2. **GET /api/rescue-requests/{id}**
   - Lấy chi tiết một yêu cầu
   - Response: RescueRequestDto

3. **POST /api/rescue-requests/{id}/accept**
   - Công ty tiếp nhận yêu cầu
   - Status: PENDING_CONFIRMATION → ACCEPTED
   - Response: RescueRequestDto

4. **PATCH /api/rescue-requests/{id}/status**
   - Cập nhật trạng thái yêu cầu
   - Body: { "status": "IN_TRANSIT|IN_PROGRESS|COMPLETED", "reason": "..." }
   - Response: RescueRequestDto

5. **POST /api/rescue-requests/{id}/reject**
   - Từ chối yêu cầu
   - Body: { "reason": "..." }
   - Response: RescueRequestDto

---

## RescueStatus Enum (Trạng thái)

```
PENDING_CONFIRMATION - Chờ xác nhận
ACCEPTED - Đã tiếp nhận
IN_TRANSIT - Đang di chuyển/Đang tới
IN_PROGRESS - Đang xử lý
COMPLETED - Hoàn thành
REJECTED_BY_COMPANY - Bị từ chối bởi công ty
CANCELLED_BY_USER - Hủy bởi người dùng
```

---

## Luồng Hoạt Động UC205

### 1. Công ty xem danh sách yêu cầu:
```
Company Dashboard (CompanyDashboard.jsx)
  ↓ GET /api/rescue-requests/company/assigned
  ↓ Hiển thị danh sách (RescueRequestCard)
  ↓ Click chọn yêu cầu
  ↓ RescueRequestDetail
```

### 2. Công ty tiếp nhận:
```
RescueRequestDetail
  ↓ Click "Chấp Nhận Yêu Cầu" (nếu status = PENDING_CONFIRMATION)
  ↓ POST /api/rescue-requests/{id}/accept
  ↓ Status: PENDING_CONFIRMATION → ACCEPTED
  ↓ Nút thay đổi → "Đang Tới", "Đang Xử Lý", "Hoàn Thành"
```

### 3. Cập nhật tiến độ:
```
RescueRequestDetail
  ↓ Click "Đang Tới" / "Đang Xử Lý" / "Hoàn Thành"
  ↓ PATCH /api/rescue-requests/{id}/status
  ↓ Status cập nhật: IN_TRANSIT → IN_PROGRESS → COMPLETED
```

### 4. Xem vị trí trên bản đồ:
```
RescueRequestDetail (Vị trí yêu cầu section)
  ↓ Click "Mở Google Maps"
  ↓ Chuyển hướng: https://maps.google.com/?q=21.0285,105.8542
  ↓ Mở Google Maps (web hoặc app mobile)
```

### 5. Từ chối yêu cầu:
```
RescueRequestDetail
  ↓ Click "Từ Chối"
  ↓ Modal: Nhập lý do từ chối
  ↓ POST /api/rescue-requests/{id}/reject
  ↓ Status: REJECTED_BY_COMPANY
```

---

## Tính Năng Chính Đã Thực Hiện

✅ Danh sách yêu cầu được giao  
✅ Thống kê theo trạng thái  
✅ Bộ lọc theo trạng thái  
✅ Xem chi tiết yêu cầu  
✅ Cập nhật trạng thái  
✅ **Mở Google Maps với tọa độ**  
✅ Từ chối với lý do  
✅ Hiển thị thông tin người gửi  
✅ Responsive design  
✅ Error handling  

---

## Cách Sử Dụng

### Cho Developer Backend:

1. Đảm bảo APIs trên đã sẵn sàng
2. API endpoints:
   - `GET /api/rescue-requests/company/assigned`
   - `GET /api/rescue-requests/{id}`
   - `POST /api/rescue-requests/{id}/accept`
   - `PATCH /api/rescue-requests/{id}/status`
   - `POST /api/rescue-requests/{id}/reject`

3. Authorization: Yêu cầu JWT token với role = COMPANY

### Cho User (Company):

1. Đăng nhập với tài khoản COMPANY
2. Click "Bảng Điều Khiển" trong header
3. Xem danh sách yêu cầu được giao
4. Click yêu cầu để xem chi tiết
5. Cập nhật trạng thái hoặc từ chối
6. Click "Mở Google Maps" để xem vị trí

---

## File Structure

```
frontend/src/
├── service/
│   └── rescueRequestService.js (NEW)
├── components/
│   ├── RescueRequestCard.jsx (NEW)
│   └── RescueRequestDetail.jsx (NEW)
├── views/
│   └── CompanyDashboard.jsx (NEW)
└── App.jsx (UPDATED)
```

---

## Notes

- Frontend đã hoàn toàn độc lập với backend
- Giao diện responsive cho cả desktop và mobile
- Google Maps tự động chuyển hướng tới app nếu trên mobile
- Error handling toàn bộ
- Loading states
- Token-based authentication
