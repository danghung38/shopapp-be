# 🛍️ ShopApp Backend

REST API + WebSocket cho ứng dụng thương mại điện tử **ShopApp**: quản lý sản phẩm, giỏ hàng, đơn hàng, đánh giá, hỏi đáp, chat realtime, thông báo và thống kê doanh thu.

---

## 🧰 Công nghệ

| Thành phần | Phiên bản |
|------------|-----------|
| Java | 21 |
| Spring Boot | 3.2.5 |
| Spring Data JPA (Hibernate) | — |
| Spring Security + OAuth2 Resource Server (JWT) | — |
| Spring WebSocket (STOMP + SockJS) | — |
| MySQL | 8.x |
| MapStruct | 1.5.5 |
| Lombok | 1.18.30 |
| AWS S3 SDK | 1.12.728 (lưu ảnh) |
| SendGrid | 4.9.3 (gửi email) |
| springdoc-openapi (Swagger UI) | 2.2.0 |

---

## 📁 Cấu trúc thư mục

```
src/main/java/com/dxh/ShopappBe/
├── configuration/   # Security, CORS, JWT, WebSocket, OpenAPI, AuditorAware
├── controller/      # REST + WebSocket controllers
├── dto/
│   ├── request/     # DTO nhận từ client
│   └── response/    # DTO trả về client (ApiResponse, PageResponse, ...)
├── entity/          # JPA entities (AbstractEntity base)
├── enums/           # Gender, OrderStatus, QuestionStatus, NotificationType, Role
├── exception/       # AppException, ErrorCode, GlobalExceptionHandler
├── mapper/          # MapStruct mappers
├── repo/            # Spring Data repositories (+ specification)
├── service/
│   ├── interfac/    # Service interfaces
│   └── impl/        # Service implementations
├── utils/           # AppConstant, Utils (pageable, sort)
└── validator/       # Custom validators (Dob, Gender, Phone, OrderStatus)
```

---

## ⚙️ Yêu cầu môi trường

- **JDK 21**
- **MySQL** đang chạy, tạo sẵn database `shopapp_be`
- Maven (đã kèm `mvnw`)

### Biến môi trường cần thiết
| Biến | Mô tả |
|------|-------|
| `JWT_KEY` | Khoá ký JWT (chuỗi đủ dài cho HS512, >= 64 ký tự) |
| `SENDGRID_KEY` | API key SendGrid (gửi email xác thực / reset mật khẩu) |
| `S3_ACCESS_KEY` | AWS S3 access key (lưu ảnh sản phẩm/avatar) |
| `S3_SECRET_KEY` | AWS S3 secret key |

> Cấu hình khác (port, datasource, bucket...) nằm trong `src/main/resources/application.yaml`.

---

## 🚀 Chạy dự án

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

- API base URL: **http://localhost:8080/api**
- Swagger UI: **http://localhost:8080/api/swagger-ui.html**

> `spring.jpa.hibernate.ddl-auto=update` → Hibernate tự tạo/cập nhật bảng khi khởi động.

---

## 🔐 Xác thực & phân quyền

- Đăng nhập trả về **JWT** (HS512). Client gắn `Authorization: Bearer <token>` cho các request cần auth.
- Vai trò: `ADMIN`, `USER` (scope token dạng `ROLE_ADMIN`, `ROLE_USER`).
- Tài khoản phải **xác thực email** (`enabled = true`) mới đăng nhập được.
- Phân quyền theo method bằng `@PreAuthorize`.

---

## 📡 WebSocket (realtime)

- Endpoint: `ws://localhost:8080/api/ws` (SockJS, JWT truyền qua query `?token=`).
- App prefix: `/app` · Broker: `/topic`, `/queue` · User prefix: `/user`.
- **Chat**: client gửi `/app/chat`, nhận tại `/user/queue/messages`.
- **Thông báo**: server push tới `/user/queue/notifications` (đơn hàng mới, đổi trạng thái).

---

## 📚 Nhóm API chính (prefix `/api`)

| Nhóm | Endpoint tiêu biểu |
|------|--------------------|
| Auth | `POST /auth/login`, `/auth/refresh`, `/auth/logout`, `/auth/introspect` |
| User | `POST /users` (đăng ký), `PUT /users` (cập nhật + avatar), `GET /users/me`, `GET /users/list`, `/users/forgot-password`, `/users/reset-password` |
| Product | `GET /products/list` (tìm kiếm + lọc + phân trang), `GET /products/{id}`, `POST /products`, `PUT /products/update-product/{id}`, `DELETE /products/{id}` |
| Category | `GET/POST /categories`, `PUT/DELETE /categories/{id}` |
| Gallery | `GET /galleries/product/{id}`, `POST /galleries`, `PUT/DELETE /galleries/{id}` |
| Cart | `GET /carts`, `POST /cartitems`, `DELETE /cartitems/{id}` |
| Order | `POST /orders`, `GET /orders/{id}`, `/orders/list`, `/orders/myorder`, `/orders/status`, `/orders/users/{id}`, `PATCH /orders/{id}/status` |
| Discount | `GET /discounts/list`, `POST /discounts`, `DELETE /discounts/{id}`, `PATCH /discounts/{id}/change-status`, `PATCH /discounts/{id}/quantity` |
| Review | `POST /reviews`, `PUT/DELETE /reviews/{id}`, `GET /reviews/product/{id}`, `/reviews/me`, `/reviews/product/{id}/summary` |
| Question | `POST /questions`, `PUT /questions/{id}/answer`, `GET /questions/product/{id}`, `/questions/me`, `/questions/status`, `DELETE /questions/{id}` |
| Notification | `GET /notifications`, `/notifications/unread-count`, `PATCH /notifications/{id}/read`, `/notifications/read-all` |
| Chat | `GET /chats`, `/chats/all`, `/chats/users`, `/chats/{username}`, `PATCH /chats/mark-read` |
| Statistics (admin) | `GET /statistics/summary`, `/statistics/revenue-by-month`, `/statistics/revenue-by-year`, `/statistics/best-selling` |
| Role / Permission | `GET/POST/DELETE /roles`, `/permissions` |

---

## 📦 Format response chuẩn

```json
{
  "code": 200,
  "message": "...",
  "result": { /* dữ liệu */ }
}
```

Phân trang:
```json
{
  "code": 200,
  "result": {
    "pageNo": 1, "pageSize": 10, "totalPage": 5,
    "totalElements": 48, "items": [ ... ]
  }
}
```

Lỗi được xử lý tập trung tại `GlobalExceptionHandler`, mã lỗi định nghĩa trong `ErrorCode` (message tiếng Anh — frontend tự convert sang tiếng Việt).

---

## 🧠 Nghiệp vụ nổi bật

- **Đặt hàng**: trừ tồn kho, cộng `totalSold`, xoá giỏ, giảm lượt mã giảm giá, gửi thông báo realtime cho admin + khách.
- **Đánh giá**: chỉ cho phép khi user đã có đơn `DELIVERED` chứa sản phẩm đó; mỗi user 1 đánh giá / sản phẩm.
- **Địa chỉ**: xoá mềm (`enabled=false`) để giữ lịch sử đơn cũ.
- **Thống kê**: doanh thu theo tháng/năm (đơn `DELIVERED`), top sản phẩm bán chạy.

---

## 🛠️ Build

```bash
mvnw.cmd clean package   # tạo file .jar trong target/
java -jar target/ShopappBe-0.0.1-SNAPSHOT.jar
```
