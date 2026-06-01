# SLY Clothing - Nền tảng thương mại điện tử tích hợp AI

SLY Clothing là hệ thống thương mại điện tử cho thương hiệu thời trang SLY. Repo này gồm storefront server-rendered cho
khách hàng, admin dashboard cho nhân viên, cùng AI chatbot và live support qua WebSocket.

## Thành phần chính

- Storefront (Spring Boot + Thymeleaf) cho khách hàng: duyệt sản phẩm, giỏ hàng, đặt hàng, hồ sơ người dùng.
- Admin dashboard (React + Vite) cho nhân viên: quản lý sản phẩm, đơn hàng, khách hàng, thống kê.
- Dịch vụ nền: MySQL, Redis, Azure Blob Storage, SePay, Twilio SMS, SMTP mail, Gemini AI.

## Tính năng

### Storefront (khách hàng)

- Duyệt sản phẩm, lọc theo danh mục, khoảng giá, từ khóa và sắp xếp.
- Giỏ hàng theo session và quy trình đặt hàng.
- Theo dõi đơn hàng với các trạng thái: UNPAID, PAID, PACKAGING, SHIPPING, COMPLETED, CANCELLED, REFUNDED.
- Hồ sơ người dùng, sổ địa chỉ, đổi mật khẩu, xác thực số điện thoại và email.
- Widget chat cho phép chuyển đổi giữa AI và nhân viên hỗ trợ.

### Admin dashboard

- CRUD danh mục và sản phẩm (nhiều ảnh, nhiều size, soft delete/restore).
- Quản lý đơn hàng và cập nhật trạng thái.
- Quản lý khách hàng và nhân viên (role ADMIN/EMPLOYEE/CUSTOMER, vô hiệu hóa tài khoản).
- Thống kê đơn hàng và doanh thu theo ngày/tháng.
- Upload ảnh qua Azure Blob Storage bằng SAS URL.
- Helpdesk chat: hàng chờ phiên hỗ trợ, phân công nhân viên, xem lịch sử chat.

### AI & realtime

- AI chatbot dùng LangChain4j + Gemini (OpenAI-compatible endpoint) với tool tra cứu sản phẩm, danh mục và trạng thái
  đơn.
- WebSocket/STOMP tại `/ws`, subscribe/publish qua `/topic/chat/{sessionId}` và `/topic/support/requests`.
- Lưu lịch sử chat trong bảng `chat_messages`.

### Hạ tầng & vận hành

- Docker Compose cho MySQL, Redis, Azurite, server và admin.
- Terraform triển khai Azure (MySQL, Redis, Storage, Web Apps).
- GitHub Actions build/push Docker image và apply Terraform.

## Kiến trúc & công nghệ

- Backend: Java 21, Spring Boot 4.0.6, Spring MVC, Spring Security (JWT cho `/api/**` + form login cho storefront),
  Spring Data JPA, Flyway.
- Storefront: Thymeleaf + Tailwind (build tại `server/src/main/frontend`).
- Admin dashboard: React 19, Vite, TypeScript, Tailwind, Recharts, STOMP.
- Database: MySQL.
- Cache/Session/Rate limit/Streams: Redis.
- Storage: Azure Blob Storage (SAS).
- Payment: SePay (QR + invoice).
- Messaging: Twilio SMS OTP, SMTP mail.

## API & WebSocket chính

- Auth (admin): `POST /api/auth/login`
- Users: `GET /api/users/profile`, `PUT /api/users/profile`, `POST /api/users/profile/email/verify`,
  `PUT /api/users/change-password`
- Products (admin): `GET|POST /api/products`, `PATCH|DELETE /api/products/{id}`
- Categories (admin): `GET|POST /api/categories`, `PATCH|DELETE /api/categories/{id}`
- Orders (admin): `GET /api/orders`, `PATCH /api/orders/{id}/status`
- Customers (admin): `GET /api/customers`, `PATCH /api/customers/{id}/status`
- Employees (admin): `GET|POST /api/employees`, `PATCH /api/employees/{id}`, `PATCH /api/employees/{id}/status`
- Dashboard (admin): `GET /api/admin/dashboard/stats`, `GET /api/admin/dashboard/order-stats`,
  `GET /api/admin/dashboard/revenue-stats`
- Storage (admin): `GET /api/blobs/{container}/sas`, `DELETE /api/blobs/{container}/{blob}`
- AI chat: `POST /api/chat`
- Support chat: `POST /api/support/request`, `POST /api/support/assign`, `GET /api/support/sessions`
- Chat history: `GET /api/chat/history/{sessionId}`
- Transactions: `POST /api/transactions`
- WebSocket: endpoint `/ws`, app destination `/app/chat.send`, topics `/topic/chat/{sessionId}`,
  `/topic/support/requests`

Lưu ý: `/api/**` yêu cầu JWT; một số endpoint giới hạn quyền ADMIN/EMPLOYEE theo `@PreAuthorize`.

## Route web chính (storefront)

- `GET /` (trang chủ), `GET /products`, `GET /products/{id}`
- `GET /cart`, `POST /cart/items`, `POST /cart/items/{itemId}/update`, `POST /cart/items/{itemId}/remove`
- `GET /orders`, `POST /orders`, `GET /orders/{id}`, `POST /orders/{id}/cancel`
- `GET /auth/login`, `GET|POST /auth/register`, `GET|POST /auth/change-password`, `GET|POST /auth/verify-phone`
- `GET /profile`, `GET /profile/update`, `POST /profile/update`, `POST /profile/email/verify`

## Cấu trúc thư mục

- `server/`: Spring Boot backend + storefront (Thymeleaf, static assets, migrations).
- `web/`: admin dashboard (React + Vite).
- `.docs/`: sơ đồ kiến trúc và tài liệu (PlantUML, HTML).
- `terraform/`: hạ tầng Azure.
- `.github/workflows/`: CI/CD.
- `compose.yaml`: Docker Compose cho local.

## Cấu hình môi trường

Tham khảo `server/.env.example` và `compose.yaml`. Các nhóm biến chính:

- MySQL: `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD`
- DB/App: `DATABASE_CONNECTION_STRING`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`
- Redis: `REDIS_CONNECTION_STRING`
- JWT: `JWT_SECRET`, `JWT_EXPIRATION_MS`
- CORS: `ALLOWED_ORIGINS`, `ALLOWED_METHODS`, `ALLOWED_HEADERS`, `ALLOW_CREDENTIALS`
- SePay: `SEPAY_*` (bank, account, invoice urls, username/password)
- Twilio: `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_VERIFY_SERVICE_SID`
- Mail: `MAIL_USERNAME`, `MAIL_PASSWORD`
- Azure Storage: `AZURE_STORAGE_CONNECTION_STRING`
- AI: `GEMINI_API_KEY`

Admin dashboard (build) dùng:

- `VITE_API_URL`
- `VITE_IMAGE_CONTAINER_URL`
- `VITE_STORAGE_URL`

## Chạy local

### Option A: Docker Compose toàn bộ

```bash
docker compose up -d
```

### Option B: Chạy dịch vụ nền + chạy ứng dụng thủ công

```bash
docker compose -f server/compose.yaml up -d
```

```bash
cd server
./mvnw spring-boot:run
```

```bash
cd web
npm install
npm run dev
```

Tùy chọn build CSS storefront (Tailwind):

```bash
cd server/src/main/frontend
npm install
npm run watch
```

Mặc định:

- Server: `http://localhost:8080`
- Admin: `http://localhost:5173`
