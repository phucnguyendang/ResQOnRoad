

hệ thống đầy đủ bao gồm 4 nhóm chức năng lớn: **Cứu hộ (Core)**, **Tương tác (Chat/Review)**, **Cộng đồng (Forum)**, và **Quản trị (Admin/Company Manager)**.

---

### TỔNG QUAN LỘ TRÌNH (AGILE ROADMAP)

* **Sprint 1: Luồng Cứu Hộ Cốt Lõi (Core Rescue Flow)** – *Ưu tiên cao nhất*
* **Sprint 2: Tương Tác & Hoàn Thiện Quy Trình (Interaction & Workflow)**
* **Sprint 3: Quản Lý Tài Nguyên Công Ty & Cộng Đồng (Resources & Community)**
* **Sprint 4: Quản Trị Hệ Thống & Báo Cáo (Administration & Analytics)**

---

### SPRINT 1: LUỒNG CỨU HỘ CỐT LÕI (CORE)

**Mục tiêu:** User tìm được xe, đặt xe; Company nhận đơn và cập nhật trạng thái.
| STT | Chức năng / Use Case | API Backend cần có | Giao diện (Frontend) | Ghi chú | 
| :--- | :--- | :--- | :--- | :--- | 
| 1 | **Đăng nhập (Auth)** <br> *(Chung cho cả Admin, User, Company)* | `POST /auth/login` | Login Screen | Token JWT dùng chung cho toàn hệ thống. |
| 2 | **Tìm kiếm cứu hộ (UC202)** <br> *(Tìm theo GPS, lọc dịch vụ)* | `GET /companies/search?lat=...&lng=...` | Home/Map Screen | Hiển thị List/Map các công ty gần nhất. |
| 3 | **Gửi yêu cầu (UC201)** <br> *(Tạo đơn, upload ảnh)* | `POST /rescue-requests` | Request Form | Form nhập thông tin sự cố. |
| 4 | **Xử lý yêu cầu (UC205)** <br> *(Company tiếp nhận)* | `PATCH /rescue-requests/{id}/status` | Company Dashboard | Company bấm "Chấp nhận", "Đang tới". |
| 5 | **Theo dõi đơn (User)** | `GET /rescue-requests/{id}` | Tracking Screen | User xem xe đang đi tới đâu. |
---

### SPRINT 2: TƯƠNG TÁC & HOÀN THIỆN QUY TRÌNH

**Mục tiêu:** Tăng độ tin cậy, cho phép giao tiếp và xử lý các tình huống ngoại lệ (Hủy, Đánh giá).
| STT | Chức năng / Use Case | API Backend cần có | Giao diện (Frontend) | Ghi chú |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **Nhắn tin (UC101)** <br> *(Chat realtime/log)* | `POST /messages` <br> `GET /messages/{requestId}` | Chat Screen | Giao diện chat gắn liền với đơn hàng. |
| 2 | **Hủy yêu cầu (UC203)** | `POST /rescue-requests/{id}/cancel` | Popup Hủy | User hủy khi chờ quá lâu. |
| 3 | **Đánh giá & Phản hồi (UC102)** | `POST /reviews` | Review Modal | Hiện ra sau khi trạng thái là "Completed". |
| 4 | **Hồ sơ Người dùng (UC402)** | `GET /users/profile` <br> `PUT /users/profile` | User Profile | Cập nhật Avatar, SĐT. |
| 5 | **Hồ sơ Công ty (UC404)** | `GET /companies/profile` <br> `PUT /companies/profile` | Company Profile | Cập nhật Giấy phép, địa chỉ, hotline. |
---

### SPRINT 3: TÀI NGUYÊN CÔNG TY & CỘNG ĐỒNG

**Mục tiêu:** Công ty tự quản lý dịch vụ/xe cộ của mình. User có thêm không gian trao đổi (Forum).
| STT | Chức năng / Use Case | API Backend cần có | Giao diện (Frontend) | Ghi chú |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **Quản lý Dịch vụ (UC302)** <br> *(Vá lốp, cẩu xe...)* | `GET /company/services` <br> `POST /company/services` <br> `PUT /company/services/{id}` | Manage Services | Công ty thêm/sửa giá dịch vụ. |
| 2 | **Quản lý Phương tiện (UC303)** <br> *(Xe cẩu, xe máy...)* | `GET /company/vehicles` <br> `POST /company/vehicles` | Manage Vehicles | Thêm xe cứu hộ vào đội xe. |
| 3 | **Đăng bài cộng đồng (UC103)** | `POST /community/posts` | Community Feed | User đăng hỏi đáp sự cố. |
| 4 | **Bình luận/Tư vấn (UC103)** | `POST /community/posts/{id}/comments` | Post Detail | User khác vào tư vấn. |
---

### SPRINT 4: QUẢN TRỊ HỆ THỐNG (ADMIN DASHBOARD)

**Mục tiêu:** Các chức năng dành riêng cho Admin để kiểm soát hệ thống (thường làm trên Web Admin).
| STT | Chức năng / Use Case | API Backend cần có | Giao diện (Web Admin) | Ghi chú |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **QL Tài khoản User (UC401)** | `GET /admin/users` <br> `PUT /admin/users/{id}/lock` | Admin User List | Khóa/Mở khóa tài khoản User. |
| 2 | **QL Tài khoản Company (UC403)** | `GET /admin/companies` <br> `PUT /admin/companies/{id}/verify` | Admin Company List | Duyệt giấy phép kinh doanh của công ty. |
| 3 | **Kiểm duyệt nội dung (UC405)** | `GET /admin/moderation` <br> `DELETE /admin/posts/{id}` | Moderation View | Xóa bài viết/đánh giá vi phạm. |
| 4 | **Báo cáo thống kê (UC406)** | `GET /admin/reports/stats` | Dashboard Charts | Biểu đồ doanh thu, số lượng đơn. |
| 5 | **QL Quy trình (UC304)** | `GET /admin/workflows` | Workflow View | Xem log quy trình hệ thống. |
---
3. **Dữ liệu (Data):**
* Cần nhập liệu trước (Seed Data) cho **Dịch vụ (UC302)** và **Phương tiện (UC303)** vào Database để khi làm **Sprint 1 (Tìm kiếm)** đã có dữ liệu hiển thị mà không cần đợi làm xong chức năng quản lý của Sprint 3.

