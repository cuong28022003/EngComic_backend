# EngComic Backend Master Guide & Hub

Tài liệu này là trung tâm điều hướng và đặc tả quy chuẩn phát triển cho toàn bộ ứng dụng `EngComic_backend` (Spring Boot & MongoDB).

---

## 🧭 Mục Lục Tài Liệu Đặc Tả Kỹ Thuật:
- 🏗️ **[Kiến Trúc Clean Architecture](./docs/architecture.md)**: Phân tầng Controller, Boundary, Interactor, Entity, Repository, Transaction Management.
- 🎯 **[Đặc Tả Kỹ Thuật Backend Feature 003 (TOEIC Exam Workspace)](./specs/003-toeic-reader/spec.md)**: Đặc tả Clean Architecture cho module TOEIC test streaming, submit attempt, mistake queue và import AI JSON.
- 🗄️ **[Mô Hình Dữ Liệu & MongoDB Schema](./docs/data-model.md)**: Thiết kế Document MongoDB, `@MongoId`, `@DBRef`, index tối ưu hóa truy vấn.
- 🔌 **[Hợp Đồng API & Payload Contracts](./docs/api-contracts.md)**: Chuẩn hóa Response DTOs, Envelope (`SuccessResponse`, `ErrorResponse`), mã lỗi nghiệp vụ.
- 🤖 **[Hướng Dẫn AI Coding](./docs/ai-coding-guide.md)**: Các quy tắc đặt tên, dependency injection qua Lombok `@RequiredArgsConstructor`, logging Slf4j.
- 📋 **[Quy Trình Phát Triển Theo Spec (Spec-Driven)](./docs/spec-driven-guide.md)**: Quy trình từ Spec -> Boundary -> Entity -> Interactor -> Controller.

---

## 📌 Quy Chuẩn Phát Triển Chi Tiết

### 1. Phân Tách Trách Nhiệm (Clean Architecture)
- **Controller Layer (`mobile.apis.*` hoặc `mobile.controller`)**:
  - Chỉ nhận HTTP request, validate `@Valid`, gọi Boundary Interactor và trả về `ResponseEntity`.
  - **KHÔNG** viết logic nghiệp vụ phức tạp, tính toán điểm số hoặc query MongoDB trực tiếp trong Controller.
- **Business Layer (`mobile.businesses.interactors.*` hoặc `mobile.Service`)**:
  - Chứa 100% nghiệp vụ logic, tính toán, xử lý file, transaction.
  - Sử dụng Boundary Interface (`mobile.businesses.boundaries.*`) để phân tách rõ ràng Input/Output.
- **Data Layer (`mobile.databases.repositories.*` & `mobile.databases.entities.*`)**:
  - Khai báo Spring Data MongoRepository.
  - Định nghĩa Entity đúng quy chuẩn (`@Document`, `@MongoId(FieldType.OBJECT_ID)` hoặc `ObjectId`).

### 2. Bảo Mật & Phân Quyền
- Khai báo `@PreAuthorize(AppAuthorities.HAS_CARD_WRITE)` / `@PreAuthorize(AppAuthorities.HAS_ADMIN)` trên các Controller methods cần xác thực.
- Các endpoint công khai hoặc endpoint streaming tài liệu phục vụ `<iframe>` (`/api/toeic/tests/file/**`, `/api/toeic/tests/proxy-pdf`, `/uploads/**`) phải được cấu hình trong `PublicSecurityEndpoints.java`.
- Cấu hình vô hiệu hóa `X-Frame-Options` (`headers.frameOptions.disable()`) trong `AppSecurityConfig.java` để cho phép `<iframe>` hiển thị tài liệu PDF mượt mà.

### 3. Xử Lý Tệp Tin (File Storage)
- Với các tệp PDF hoặc tài liệu tải lên: Luôn lưu bản sao cục bộ vào `uploads/toeic_pdfs/` và ưu tiên cấp phát đường dẫn streaming nội bộ (`/api/toeic/tests/file/{filename}`) để tránh bị chặn bởi tính năng Tracking Prevention của trình duyệt đối với các CDN bên thứ ba.

### 4. Quy Trình Kiểm Tra & Biên Dịch
- Sau khi thêm hoặc sửa đổi code Java/Spring Boot, **BẮT BUỘC** phải chạy xác thực biên dịch:
  ```bash
  mvn compile -DskipTests
  ```
- Đảm bảo **BUILD SUCCESS (0 error)** trước khi kết thúc task.
