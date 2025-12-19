# Frontend

## Cấu trúc thư mục `src`

| Thư mục   | Mục đích                                                                                 |
|-----------|------------------------------------------------------------------------------------------|
| assets    | Nơi lưu trữ tài nguyên toàn cục (global) như hình ảnh (images), âm thanh (audio), video. |
| common    | Chứa các component có thể tái sử dụng (reusable components) dùng ở nhiều nơi.            |
| routes    | Định nghĩa các tuyến đường (router) của website.                                         |
| store     | Cấu hình quản lý trạng thái (state management), thường dùng cho Redux.                   |
| styles    | Viết các file CSS/Sass toàn cục (Global CSS).                                            |
| utils     | Chứa các hàm tiện ích chung (function chung).                                            |
| views     | Chứa các trang web chính của dự án (pages).                                              |
| service   | Thực hiện các cuộc gọi API.                                                              |

### Mô tả chi tiết:
- **assets/**: Lưu trữ tài nguyên công khai như hình ảnh, âm thanh, video.
- **common/**: Các component tái sử dụng như button, modal, wrapper...
- **routes/**: Định nghĩa các tuyến đường, cấu hình router.
- **store/**: Quản lý state, thường dùng Redux hoặc Context API.
- **styles/**: Chứa các file CSS/Sass dùng chung cho toàn dự án.
- **utils/**: Các hàm tiện ích, helper function dùng chung.
- **views/**: Các trang chính, mỗi file là một page.
- **service/**: Xử lý các request tới API, tách biệt logic gọi API khỏi component.

# Backend
```text
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── rescue/
│   │   │           └── system/
│   │   │               ├── config/             # Cấu hình (Security, CORS, Swagger)
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   └── WebConfig.java
│   │   │               ├── controller/         # Xử lý API request
│   │   │               │   ├── AuthController.java
│   │   │               │   └── RescueRequestController.java
│   │   │               ├── dto/                # Data Transfer Objects (Request/Response)
│   │   │               │   ├── request/
│   │   │               │   │   ├── LoginRequest.java
│   │   │               │   │   └── RegisterRequest.java
│   │   │               │   └── response/
│   │   │               │       └── JwtResponse.java
│   │   │               ├── entity/             # Các bảng trong Database (ORM)
│   │   │               │   ├── User.java
│   │   │               │   └── RescueRequest.java
│   │   │               ├── repository/         # Tương tác với Database (JPA)
│   │   │               │   └── UserRepository.java
│   │   │               ├── service/            # Logic nghiệp vụ
│   │   │               │   ├── AuthService.java
│   │   │               │   └── impl/
│   │   │               │       └── AuthServiceImpl.java
│   │   │               ├── security/           # JWT Utilities, Filters
│   │   │               │   ├── JwtTokenProvider.java
│   │   │               │   └── JwtAuthFilter.java
│   │   │               └── RescueSystemApplication.java
│   │   └── resources/
│   │       └── application.properties          # Cấu hình DB, Server port
│   └── test/
└── pom.xml                                     # Maven dependencies
```