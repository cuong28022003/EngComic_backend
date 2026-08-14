# Task Breakdown: [Feature Name]

> **Góc nhìn**: Developer / AI Agent  
> **Mục đích**: Trả lời câu hỏi **"BƯỚC NÀO LÀM TRƯỚC, BƯỚC NÀO LÀM SAU?" (EXECUTION & CHECKLIST)**  
> **Chỉ dẫn cho AI**: Thực hiện tuần tự từng task. Chạy `./mvnw compile` sau mỗi task để xác minh cú pháp trước khi tick `[x]` và chuyển sang task tiếp theo.

---

### Phase 1: Database & Persistence Layer (`databases/`)
- [ ] **T001**: Tạo MongoDB Entity Document
  - **Where**: `src/main/java/mobile/databases/entities/[Feature]Entity.java`
  - **Details**: Định nghĩa schema, `@Id ObjectId id`, `@Document(collection = "...")`, Lombok `@Getter`, `@Setter`.
- [ ] **T002**: Tạo Spring Data Mongo Repository
  - **Where**: `src/main/java/mobile/databases/repositories/[Feature]Repository.java`
  - **Details**: Kế thừa `MongoRepository<[Feature]Entity, ObjectId>`, khai báo query method nếu có.

---

### Phase 2: Business Logic & Use-Case Layer (`businesses/`)
- [ ] **T003**: Tạo Boundary Interface & Hợp đồng DTO nội bộ
  - **Where**: `src/main/java/mobile/businesses/boundaries/[feature]/[Action][Feature]Boundary.java`
  - **Details**: Định nghĩa `execute(Request request) -> Response` với inner classes `Request` và `Response`.
- [ ] **T004**: Viết Use-Case Interactor Implementation
  - **Where**: `src/main/java/mobile/businesses/interactors/[feature]/[Action][Feature]Interactor.java`
  - **Details**: Thực thi toàn bộ business invariants, kiểm tra điều kiện nghiệp vụ, cập nhật trạng thái và gọi Repository.

---

### Phase 3: Adapter & REST API Layer (`apis/`)
- [ ] **T005**: Tạo API Request & Response DTOs
  - **Where**: `src/main/java/mobile/apis/[feature]/dtos/[Action][Feature]RequestDto.java`
  - **Where**: `src/main/java/mobile/apis/[feature]/dtos/[Feature]ResponseDto.java`
  - **Details**: Thêm validation annotations (`@NotBlank`, `@NotNull`, `@Min`, `@Max`).
- [ ] **T006**: Tạo Mapper Component
  - **Where**: `src/main/java/mobile/apis/[feature]/[Feature]Mapper.java`
  - **Details**: Spring `@Component` chuyển đổi Entity/Boundary Response sang API Response DTO.
- [ ] **T007**: Tạo REST Controller Endpoint
  - **Where**: `src/main/java/mobile/apis/[feature]/[Feature]Controller.java`
  - **Details**: `@RestController`, `@RequestMapping`, validate `@Valid`, gọi Boundary và trả về `ResponseEntity`.

---

### Phase 4: Verification & Checkpoint
- [ ] **T008**: Biên dịch và kiểm thử
  - **Command**: `./mvnw compile` (hoặc `./mvnw test`)
  - **Requirement**: Build thành công 100% không phát sinh lỗi hoặc cảnh báo gãy kiểu dữ liệu.
