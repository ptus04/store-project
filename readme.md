# SLY Clothing - Website Thương mại Điện tử Tích hợp AI

Chào mừng bạn đến với dự án **SLY Clothing**. Đây là một hệ thống thương mại điện tử dành cho thương hiệu thời trang
SLY, được tích hợp AI Chatbot và hỗ trợ giao tiếp chăm sóc khách hàng theo thời gian thực.

---

## 🌟 Các tính năng chính (Key Features)

### 🛒 Dành cho Khách hàng (Storefront)

- **Tìm kiếm & Lọc thông minh**: Cho phép lọc sản phẩm theo danh mục, mức giá, kích cỡ (size), chất liệu, và form dáng.
- **Giỏ hàng & Đặt hàng khép kín**: Xử lý mượt mà từ lúc thêm sản phẩm, cập nhật số lượng đến bước điền địa chỉ giao
  hàng và thanh toán.
- **Theo dõi đơn hàng**: Xem lịch sử mua hàng và cập nhật trạng thái đơn hàng (Chờ duyệt, Đang giao, Hoàn thành).
- **Hồ sơ cá nhân**: Quản lý thông tin, đổi mật khẩu bảo mật và sổ địa chỉ giao hàng.

### 💼 Dành cho Quản trị viên & Nhân viên (Admin Dashboard)

- **Quản lý Hàng hóa**: CRUD (Thêm/Sửa/Xóa) Danh mục và Sản phẩm (hỗ trợ nhiều ảnh, nhiều size).
- **Quản lý Đơn hàng & Kho**: Duyệt đơn hàng, cập nhật trạng thái giao hàng, tự động trừ số lượng hàng tồn kho.
- **Thống kê & Báo cáo**: Biểu đồ trực quan theo dõi doanh thu và hiệu suất bán hàng.
- **Quản lý Người dùng**: Phân quyền tài khoản (Admin, Staff, Customer), vô hiệu hóa (ban) tài khoản.

### 🤖 Trí tuệ nhân tạo (AI) & Tương tác Realtime

- **AI Chatbot (Google Gemini + LangChain4j)**: Chatbot thông minh (song ngữ Anh/Việt) có khả năng đọc và hiểu trực tiếp
  Database để tư vấn size, tìm kiếm sản phẩm cho khách.
- **Live Chat (WebSocket/STOMP)**: Khách hàng có thể chuyển tiếp (handoff) từ AI sang chat 1-1 với nhân viên thật mà
  không có độ trễ.
- **Hàng chờ Hỗ trợ (Support Queue)**: Nhân viên quản lý đồng thời nhiều phiên chat thông qua giao diện Helpdesk.

---

## 🏗️ Kiến trúc công nghệ (Tech Stack)

Hệ thống được phân tách rõ ràng theo mô hình **Client - Server**:

- **Frontend (Web)**: React.js, TypeScript, Vite, Tailwind CSS. Giao tiếp qua Fetch API.
- **Backend (Server)**: Java 17, Spring Boot 3, Spring Security (JWT), Spring Data JPA.
- **Database**: PostgreSQL / MySQL (Relational DB) để đảm bảo tính nhất quán (ACID).
- **AI Integration**: LangChain4j kết hợp với LLM Google Gemini.
- **Realtime**: WebSockets (STOMP protocol).

---

## 🗄️ Cấu trúc Cơ sở dữ liệu (Database Schema)

Dữ liệu được tổ chức thành các Entity (Bảng) chính:

- **Người dùng**: `User` (Khách, Nhân viên, Admin), `UserAddress`.
- **Sản phẩm**: `Category`, `Product`, `ProductImage`, `ProductSize`.
- **Đơn hàng & Giao dịch**: `Order`, `OrderDetail`, `OrderShippingAddress`, `Transaction`.
- **Hỗ trợ & Tương tác**: `ChatMessage` (Lưu lịch sử hội thoại), `Carousel` (Quản lý banner quảng cáo).

---

## 🔌 Các API cốt lõi (Core Endpoints)

Dự án cung cấp bộ RESTful API tiêu chuẩn:

- **Auth & Users**:
    - `POST /api/auth/login`, `POST /api/auth/register`: Xác thực & cấp phát JWT Token.
    - `GET /api/users/profile`, `PUT /api/users/change-password`: Quản lý hồ sơ.
- **Products & Categories**:
    - `GET /api/products`: Danh sách sản phẩm (có Hỗ trợ filter/search/pagination).
    - `POST /api/products`: Thêm sản phẩm mới (Yêu cầu quyền Admin/Staff).
- **Order Flow**:
    - `POST /cart/add`: Thêm vào giỏ hàng (sử dụng Session hoặc Database tùy cấu hình).
    - `POST /order/checkout`: Chốt đơn hàng và lưu địa chỉ giao nhận.
- **AI & Chat**:
    - `POST /api/chat`: Gửi câu hỏi cho Gemini AI.
    - WebSocket `ws://.../ws-stomp` -> Topic `/topic/chat/{sessionId}`, `/topic/support/requests`.

---

## 📁 Cấu trúc thư mục dự án (Monorepo)

- 📁 **`server/`**: Mã nguồn Backend (Spring Boot). Chứa Controllers, Services, Repositories, Entities và Security
  configs.
- 📁 **`web/`**: Mã nguồn Frontend (React). Chứa Pages, Components, utils và cấu hình Tailwind.
- 📁 **`docs/`**: Tài liệu kỹ thuật. Các file Markdown đặc tả Use Case (UML Activity, Sequence Diagrams).
- 📁 **`terraform/`**: Infrastructure as Code. Các script để tự động triển khai hạ tầng Cloud.
- 📁 **`.github/`**: Các luồng CI/CD (GitHub Actions) tự động kiểm thử và deploy.

---

## 🛠️ Hướng dẫn Cài đặt & Chạy dự án (Local Development)

### Yêu cầu môi trường

- **Java**: JDK 17+
- **Node.js**: v18.x+
- **Database**: Cài đặt sẵn MySQL hoặc PostgreSQL (Cập nhật thông tin kết nối trong `application.properties`).

### 1. Khởi động Backend

```bash
cd server
# (Tùy chọn) Điền GEMINI_API_KEY và DB URL trong src/main/resources/application.properties
./mvnw spring-boot:run
```

> Server sẽ khởi chạy tại: `http://localhost:8080`

### 2. Khởi động Frontend

```bash
cd web
npm install
npm run dev
```

> Giao diện web sẽ khởi chạy tại: `http://localhost:5173`

### 3. Các lệnh hữu ích (Frontend)

- `npm run build`: Đóng gói ứng dụng để đưa lên server (Production).
- `npm run lint`: Chạy trình kiểm tra cú pháp ESLint.
- `npm run fix`: Tự động sửa các lỗi format code (Prettier + ESLint).

---
*Dự án được phát triển và tối ưu liên tục nhằm mang lại trải nghiệm thương mại điện tử hiện đại nhất.*
