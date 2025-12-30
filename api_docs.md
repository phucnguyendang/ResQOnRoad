
# RESTful API Documentation: ResQOnRoad

**Thông tin chung:**

* **Base URL:** `https://api.cuuho247.vn/v1`
* **Content-Type:** `application/json`
* **Authentication:** Bearer Token (JWT)

---

## 1. Authentication (Xác thực)

### 1.1. Đăng nhập (Login)

Dùng cho tất cả các role (User, Admin, Company) .

* **Endpoint:** `POST /auth/login`
* **Request Body:**
```json
{
  "username": "nguyenvana",
  "password": "password123",
}

```


* **Response (200 OK):**
```json
{
  "message": "Đăng nhập thành công",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "account_id": 101,
    "role": "USER", // Các giá trị: USER, COMPANY, ADMIN
    "profile": {
      "full_name": "Nguyễn Văn A",
      "avatar_base64": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD..."
    }
  }
}

```



---

## 2. Module Người dùng (User)

### 2.1. Lấy thông tin cá nhân (Get Profile)

Dựa trên Use Case 402 .

* **Endpoint:** `GET /users/profile`
* **Header:** `Authorization: Bearer {token}`
* **Response (200 OK):**
```json
{
  "data": {
    "id": 101,
    "username": "nguyenvana",
    "full_name": "Nguyễn Văn A",
    "phone_number": "0987654321",
    "email": "nguyenvana@email.com",
    "avatar_base64": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD...",
    "created_at": "2025-01-15T08:30:00Z"
  }
}

```



---

## 3. Module Công ty Cứu hộ (Rescue Company)

### 3.1. Tìm kiếm công ty cứu hộ (Search Nearby)

Dựa trên Use Case 202 . API trả về danh sách công ty kèm khoảng cách tính toán từ vị trí người dùng.

* **Endpoint:** `GET /companies/search`
* **Query Params:**
* `lat`: 21.0285 (Vĩ độ người dùng)
* `lng`: 105.8542 (Kinh độ người dùng)
* `service_type`: "Vá lốp" (Tùy chọn)


* **Response (200 OK):**
```json
{
  "data": [
    {
      "id": 50,
      "company_name": "Cứu hộ Ba Đình",
      "phone": "0243123456",
      "rating_avg": 4.8,
      "distance_km": 1.2, // Khoảng cách tính toán
      "location": {
        "lat": 21.0300,
        "lng": 105.8500
      },
      "services": [
        { "name": "Vá lốp", "price": 150000 },
        { "name": "Kích bình", "price": 100000 }
      ]
    },
    {
      "id": 52,
      "company_name": "Gara Ô tô 247",
      "phone": "0909000111",
      "rating_avg": 4.2,
      "distance_km": 3.5,
      "location": {
        "lat": 21.0500,
        "lng": 105.8600
      }
    }
  ]
}

```



---

## 3.2. Đánh giá công ty cứu hộ (Review & Rating)

Hiện tại backend đã có model `Review` và hiển thị rating trung bình trong tìm kiếm công ty. Để phục vụ chức năng người dùng đánh giá sau khi hoàn thành yêu cầu cứu hộ, cần các API sau.

### 3.2.1. Tạo/Cập nhật đánh giá theo yêu cầu cứu hộ

Chỉ cho phép khi yêu cầu cứu hộ đã ở trạng thái `COMPLETED`.

* **Endpoint:** `POST /api/reviews`
* **Header:** `Authorization: Bearer {token}`
* **Request Body:**
```json
{
  "requestId": 1001,
  "rating": 5,
  "comment": "Tới nhanh, xử lý chuyên nghiệp."
}
```

* **Response (201 Created):**
```json
{
  "code": 201,
  "message": "Gửi đánh giá thành công",
  "data": {
    "id": 501,
    "userName": "Nguyễn Văn A",
    "rating": 5,
    "comment": "Tới nhanh, xử lý chuyên nghiệp.",
    "isVerified": true,
    "createdAt": "2025-11-20T12:00:00Z"
  }
}
```

### 3.2.2. (Tuỳ chọn) Lấy danh sách đánh giá của công ty

* **Endpoint:** `GET /api/companies/{companyId}/reviews?page=1&limit=10`
* **Response (200 OK):**
```json
{
  "code": 200,
  "message": "Lấy danh sách đánh giá thành công",
  "data": {
    "items": [
      {
        "id": 501,
        "userName": "Nguyễn Văn A",
        "rating": 5,
        "comment": "Tới nhanh, xử lý chuyên nghiệp.",
        "isVerified": true,
        "createdAt": "2025-11-20T12:00:00Z"
      }
    ],
    "pagination": {
      "current_page": 1,
      "total_pages": 1
    }
  }
}
```

## 4. Module Yêu cầu Cứu hộ (Rescue Request) - Core Feature

### 4.1. Tạo yêu cầu cứu hộ (Create Request)

Dựa trên Use Case 201 . Người dùng gửi thông tin sự cố và chọn công ty cứu hộ.

* **Endpoint:** `POST /rescue-requests`
* **Request Body:**
```json
{
  "company_id": 50,
  "incident_desc": "Xe bị thủng lốp sau bên phải, không có lốp dự phòng.",
  "location_address": "Số 1 Đại Cồ Việt, Hai Bà Trưng, Hà Nội",
  "latitude": 21.0073,
  "longitude": 105.8431,
  "images_base64": [
    "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD...",
    "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD..."
  ]
}

```


* **Response (201 Created):**
```json
{
  "message": "Gửi yêu cầu thành công",
  "data": {
    "id": 1001,
    "status": "Pending", // Trạng thái ban đầu 
    "created_at": "2025-11-20T10:00:00Z",
    "company": {
      "id": 50,
      "name": "Cứu hộ Ba Đình"
    }
  }
}

```



### 4.2. Cập nhật trạng thái yêu cầu (Update Status)

Dựa trên Use Case 205 . Dành cho Công ty cứu hộ cập nhật tiến trình.

* **Endpoint:** `PATCH /rescue-requests/{id}/status`
* **Request Body:**
```json
{
  "status": "Moving", // Enum: Pending, Accepted, Moving, Completed, Cancelled 
  "note": "Nhân viên đang di chuyển, dự kiến 15 phút nữa tới."
}

```


* **Response (200 OK):**
```json
{
  "message": "Cập nhật trạng thái thành công",
  "data": {
    "request_id": 1001,
    "current_status": "Moving",
    "updated_at": "2025-11-20T10:05:00Z",
    "history": [
      { "status": "Pending", "time": "10:00:00" },
      { "status": "Accepted", "time": "10:02:00" },
      { "status": "Moving", "time": "10:05:00" }
    ]
  }
}

```



### 4.3. Chi tiết yêu cầu (Get Request Detail)

Dùng cho màn hình theo dõi trạng thái .

* **Endpoint:** `GET /rescue-requests/{id}`
* **Response (200 OK):**
```json
{
  "data": {
    "id": 1001,
    "user": {
      "full_name": "Nguyễn Văn A",
      "phone": "0987654321"
    },
    "incident": {
      "desc": "Xe bị thủng lốp...",
      "images_base64": ["data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD...", "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD..."]
      "address": "Số 1 Đại Cồ Việt"
    },
    "status": "Moving",
    "company": {
      "name": "Cứu hộ Ba Đình",
      "hotline": "0243123456"
    },
    "timeline": [
      // Lịch sử trạng thái lấy từ bảng LichSuTrangThai 
      { "status": "Pending", "updated_at": "...", "note": "" },
      { "status": "Accepted", "updated_at": "...", "note": "" }
    ]
  }
}

```



---

## 5. Module Cộng đồng (Community)

### 5.1. Lấy danh sách bài đăng (Get Posts)

Dựa trên Use Case 103 .

* **Endpoint:** `GET /community/posts`
* **Query Params:** `page=1&limit=10`
* **Response (200 OK):**
```json
{
  "data": [
    {
      "id": 201,
      "title": "Hỏi về gara uy tín khu vực Cầu Giấy",
      "content": "Xe mình bị kêu lạ ở gầm...",
      "author": {
        "name": "Trần Văn B",
        "avatar_base64": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD..."
      },
      "comment_count": 5,
      "created_at": "2025-11-19T14:20:00Z",
      "status": "Open" // Enum: Open, Resolved, Closed 
    }
  ],
  "pagination": {
    "current_page": 1,
    "total_pages": 5
  }
}

```



### 5.2. Bình luận bài viết (Comment)

Dựa trên bảng `BinhLuan` .

* **Endpoint:** `POST /community/posts/{postId}/comments`
* **Request Body:**
```json
{
  "content": "Bạn thử qua Gara X ở đường Y xem, mình hay sửa ở đó."
}

```


* **Response (201 Created):**
```json
{
  "data": {
    "id": 505,
    "post_id": 201,
    "content": "Bạn thử qua Gara X...",
    "created_at": "Now"
  }
}

```



---

## 6. Module Tin nhắn (Chat)

### 6.1. Gửi tin nhắn (Send Message)

Dựa trên Use Case 101 và bảng `TinNhan` .

* **Endpoint:** `POST /messages`
* **Request Body:**
```json
{
  "request_id": 1001, // Gắn liền với một yêu cầu cứu hộ cụ thể
  "receiver_id": 50,  // ID người nhận
  "content": "Tôi đang đứng ở gốc cây to đối diện số nhà."
}

```


* **Response (201 Created):**
```json
{
  "data": {
    "id": 99,
    "content": "Tôi đang đứng ở gốc cây to...",
    "sent_at": "2025-11-20T10:15:00Z",
    "is_read": false
  }
}

```



---

## 7. Xử lý lỗi (Error Handling)

Hệ thống sử dụng các mã lỗi HTTP tiêu chuẩn.

* **Response Lỗi Mẫu (400 Bad Request):**
```json
{
  "error": {
    "code": 400,
    "message": "Dữ liệu đầu vào không hợp lệ",
    "details": [
      "location_address là bắt buộc",
      "latitude phải là số thực"
    ]
  }
}

```


* **401 Unauthorized:** Token không hợp lệ hoặc hết hạn.
* **403 Forbidden:** Không có quyền thực hiện hành động (VD: User thường cố gắng xóa bài đăng của người khác).
* **404 Not Found:** Không tìm thấy tài nguyên (VD: ID công ty không tồn tại).