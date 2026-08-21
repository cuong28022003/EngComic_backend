# Tasks: TOEIC Reader — Backend (003, v2)

> Simplified: PDF display-only, JSON-driven answer keys, all SQL.

---

## Phase 1: DB & Entities

- [ ] Viết `V3__create_reader_tables.sql` (Flyway migration)
- [ ] Tạo `Test.java` entity + `TestStatus.java` enum
- [ ] Tạo `AnswerKey.java` entity
- [ ] Tạo `UserAnswer.java` entity
- [ ] Tạo `Mistake.java` entity + `MistakeStatus.java` enum
- [ ] Tạo 4 JPA Repositories (port interfaces)
- [ ] Chạy migration, verify schema tạo đúng

## Phase 2: Create Test (Upload PDF + Import Answer Key)

- [ ] Tạo `CreateTestRequest.java` DTO (test_name + questions[])
- [ ] Config multipart file upload trong `application.yml` (max 50MB)
- [ ] Config serve static files `/uploads/**` (ResourceHttpRequestHandler)
- [ ] Tạo `CreateTestInteractor.java`:
  - [ ] Save PDF file vào `uploads/tests/{id}.pdf`
  - [ ] Persist `Test` entity
  - [ ] Batch insert `AnswerKey` entities
- [ ] Thêm `POST /api/v1/tests` vào `TestController.java`
- [ ] Test: upload PDF + JSON metadata → verify file saved + answer_keys inserted

## Phase 3: Test Detail & Submit

- [ ] Tạo `TestDetailDto.java` (KHÔNG có `correct_answer`)
- [ ] Tạo `GetTestDetailInteractor.java` (map AnswerKeys → strip correct_answer)
- [ ] Tạo `SubmitSessionRequest.java` + `SubmitSessionResponse.java` + `GradedResultDto.java`
- [ ] Tạo `SubmitSessionInteractor.java`:
  - [ ] Load + validate test ownership
  - [ ] Load AnswerKeys → Map<questionNumber, correctAnswer>
  - [ ] Grade: null answer = sai, compare userAnswer vs correctAnswer
  - [ ] Batch upsert UserAnswer records
  - [ ] Update Test: status=COMPLETED, raw_score
  - [ ] Build GradedResultDto[] + PartBreakdownDto[]
- [ ] Thêm `GET /:id` + `POST /:id/submit` vào `TestController.java`
- [ ] Security test: verify GET /:id KHÔNG có correct_answer trong response
- [ ] Unit test SubmitSessionInteractor: null→wrong, all correct, all wrong, partial

## Phase 4: Mistake Queue

- [ ] Tạo `MistakeDto.java` + `CreateMistakeBatchRequest.java` + `UpdateMistakeRequest.java`
- [ ] Tạo `CreateMistakeBatchInteractor.java` (set userId từ JWT, status=PENDING)
- [ ] Tạo `GetMistakeListInteractor.java` (filter userId + status, pageable)
- [ ] Tạo `UpdateMistakeInteractor.java`:
  - [ ] Validate ownership
  - [ ] Validate status transition (PENDING→EXPLAINED cần explanation, reject lùi)
- [ ] Tạo `MistakeController.java` (GET, POST /batch, PATCH /:id, DELETE /:id)
- [ ] Unit test UpdateMistakeInteractor: transition hợp lệ + reject backward

## Phase 5: Integration & Docs

- [x] Cập nhật `BACKEND_CONTRACT.md` với 003 APIs
- [x] Seed data: 2-3 test mẫu với answer keys (dev/test profile)
- [x] Verify CORS config cho `/uploads/**` path

## Phase 6: Timing, Pacing, Part-Practice & Google Translate (v3 Extension)

- [x] Cập nhật `ToeicUserSessionEntity` lưu `timeMode`, `selectedParts`, part targets & elapsed seconds, và `timeSpentSeconds` trong `UserAnswerRecord`.
- [x] Cập nhật `SubmitToeicSessionRequest` & `SubmitToeicSessionResponse` / `PartBreakdownDto` với timing fields.
- [x] Cập nhật `SubmitToeicSessionInteractor` hỗ trợ lọc câu hỏi theo `selectedParts`, chấm điểm chuẩn xác theo số câu được chọn.
- [x] Cập nhật `ToeicMistakeRepository` & `GetToeicMistakesInteractor` sắp xếp lỗi sai theo `questionNumber ASC`.
- [x] Tích hợp endpoint Google Translate `/api/translator/**` và cấu hình security public.
- [x] Biên dịch `mvn compile -DskipTests` đạt BUILD SUCCESS 0 errors.
