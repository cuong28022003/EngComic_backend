# Task Checklist: TOEIC Reader Backend Clean Architecture (v3)

> **Feature ID**: 003  
> **Trạng thái**: Hoàn thành & Đã kiểm thử

---

- [x] **Task 1: MongoDB Entities & Repositories**
  - [x] Tạo `ToeicTestDocument` + `ToeicTestMongoRepository`.
  - [x] Tạo `TestAttemptDocument` + `TestAttemptMongoRepository`.
  - [x] Tạo `MistakeQueueDocument` + `MistakeQueueMongoRepository`.

- [x] **Task 2: File Streaming & Security Configuration**
  - [x] Cấu hình `/api/toeic/tests/file/**` và `/api/toeic/tests/proxy-pdf` trong `PublicSecurityEndpoints.java`.
  - [x] Cấu hình disable `X-Frame-Options` trong `AppSecurityConfig.java` cho `<iframe>`.

- [x] **Task 3: Nộp bài, Chấm điểm & Lưu Session**
  - [x] Viết `SubmitToeicTestInteractor` chấm điểm chuẩn TOEIC Reading Scaled Score (5-495).
  - [x] Tự động chèn câu sai (`!isCorrect || flagged`) vào `mistake_queue`.
  - [x] Viết endpoint lấy lịch sử các lần thi theo `testId`: `GET /api/toeic/attempts/test/{testId}`.
  - [x] Viết endpoint xem lại bài làm theo `attemptId`: `GET /api/toeic/attempts/{attemptId}/review`.

- [x] **Task 4: Hàng đợi lỗi sai (Mistake Queue) & Import AI JSON**
  - [x] Viết API lấy danh sách câu sai: `GET /api/toeic/mistakes` (hỗ trợ phân trang lớn).
  - [x] Viết API trích xuất System Prompt cho LLM: `GET /api/toeic/mistakes/prompt`.
  - [x] Viết API import JSON phân tích từ AI: `POST /api/toeic/mistakes/import-ai-review` (đồng bộ vào `test_attempts` và cập nhật `mistake_queue`).

- [x] **Task 5: Fix Boolean Serialization (Jackson)**
  - [x] Thêm `@JsonProperty("isCorrect")` vào `ToeicAttemptDto` và `GradedQuestionDto`.

- [x] **Task 6: Kiểm tra biên dịch**
  - [x] Chạy `mvn compile -DskipTests` ➔ **BUILD SUCCESS (0 errors)**.
