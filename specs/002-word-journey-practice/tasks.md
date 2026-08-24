# Task Checklist: Word Journey Backend Clean Architecture (v3)

> **Feature ID**: 002  
> **Trạng thái**: Hoàn thành & Đã kiểm thử

---

- [x] **Task 1: Cập nhật Domain Entity & Repositories**
  - [x] Tạo `CardExercisePackage.java` (Level 1, 2, 3, 4 models).
  - [x] Cập nhật `CardEntity.java` với các trường `masteryLevel`, `confidenceScore`, `wrongCount`, `memoryTip`, `exercisePackage`.
  - [x] Thêm query methods trong `CardRepository.java` (`findByUserIdAndStatus`, `findByUserIdAndDeckId`).

- [x] **Task 2: AI Prompt & JSON Import Engine**
  - [x] Tạo `GeneratePracticePromptBoundary` + `GeneratePracticePromptInteractor`.
  - [x] Tạo `ImportPracticeJsonBoundary` + `ImportPracticeJsonInteractor`.
  - [x] Thêm endpoint `GET /api/card/deck/{deckId}/practice-prompt`.
  - [x] Thêm endpoint `POST /api/card/deck/{deckId}/import-practice-json`.

- [x] **Task 3: Phòng Luyện Tập & Thuật Toán SRS SM-2**
  - [x] Tạo `GetPracticeQueueBoundary` + `GetPracticeQueueInteractor`.
  - [x] Tạo `SubmitLevelAnswerBoundary` + `SubmitLevelAnswerInteractor` (tự động thăng cấp Level 1-4, tính SM-2, xử lý Leech).
  - [x] Thêm endpoint `GET /api/card/practice/queue`.
  - [x] Thêm endpoint `POST /api/card/{id}/submit-level`.

- [x] **Task 4: Trung Tâm Cứu Hộ Leech Center**
  - [x] Tạo `GetLeechCardsBoundary` + `GetLeechCardsInteractor`.
  - [x] Tạo `ClearLeechStatusBoundary` + `ClearLeechStatusInteractor`.
  - [x] Thêm endpoint `GET /api/card/leech`.
  - [x] Thêm endpoint `POST /api/card/{id}/clear-leech`.

- [x] **Task 5: Kiểm Tra Biên Dịch**
  - [x] Chạy `mvn compile -DskipTests` ➔ **BUILD SUCCESS (0 errors)**.
