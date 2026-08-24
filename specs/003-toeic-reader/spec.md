# Feature Specification: TOEIC Reader — Backend Clean Architecture (v3)

> **Perspective**: Business / Product Owner & Backend Developer  
> **Purpose**: Answer the question **"WHAT & WHY"** (Clean Architecture, MongoDB Schema, API Contract)  
> **Version**: v3 — MongoDB Collections, Clean Architecture 4 Tầng, Jackson `@JsonProperty("isCorrect")`, AI Review Import & Streaming Whitelist

---

## 1. Overview & Objective

- **Feature ID**: 003
- **Feature Name**: TOEIC Reader — Backend Clean Architecture & Data Engine
- **Priority**: P1
- **Objective**: Cung cấp bộ API hiệu năng cao phục vụ thi thử TOEIC Reading:
  1. Quản lý đề thi PDF, stream PDF nội bộ chống CORS & vô hiệu hóa `X-Frame-Options` cho `<iframe>`.
  2. Nộp bài, chấm điểm tự động chuẩn bảng điểm ETS TOEIC Reading (Raw Score 0-100 ➔ Scaled Score 5-495).
  3. Quản lý lịch sử các lần thi theo từng session (`test_attempts`).
  4. Tự động đẩy câu sai vào `mistake_queue`.
  5. Sinh System Prompt cho LLM và Import JSON lời giải AI vào từng câu hỏi.

---

## 2. Permissions & Security Configuration

- **Auth**: `@PreAuthorize("isAuthenticated()")` cho các endpoint nộp bài, xem lịch sử, import giải thích.
- **File Streaming & Whitelist**:
  - `GET /api/toeic/tests/file/**` & `GET /api/toeic/tests/proxy-pdf` được cấu hình trong `PublicSecurityEndpoints.java`.
  - `AppSecurityConfig.java` cấu hình `.headers(headers -> headers.frameOptions(frame -> frame.disable()))`.

---

## 3. Clean Architecture Package Layout

```
src/main/java/mobile/
├── apis/reader/
│   ├── controllers/
│   │   ├── ToeicTestController.java
│   │   ├── ToeicAttemptController.java
│   │   └── MistakeQueueController.java
│   ├── dtos/
│   │   ├── ToeicTestDto.java
│   │   ├── ToeicAttemptDto.java          # @JsonProperty("isCorrect")
│   │   ├── GradedQuestionDto.java        # @JsonProperty("isCorrect")
│   │   └── MistakeQueueItemDto.java
│   └── usecases/
│       ├── boundaries/
│       │   ├── SubmitToeicTestInputBoundary.java
│       │   ├── GetAttemptReviewInputBoundary.java
│       │   └── ImportAiReviewInputBoundary.java
│       └── interactors/
│           ├── SubmitToeicTestInteractor.java
│           ├── GetAttemptReviewInteractor.java
│           └── ImportAiReviewInteractor.java
└── core/database/
    ├── documents/
    │   ├── ToeicTestDocument.java        # Collection: toeic_tests
    │   ├── TestAttemptDocument.java      # Collection: test_attempts
    │   └── MistakeQueueDocument.java     # Collection: mistake_queue
    └── repositories/
        ├── ToeicTestMongoRepository.java
        ├── TestAttemptMongoRepository.java
        └── MistakeQueueMongoRepository.java
```

---

## 4. MongoDB Collections Design

### 4.1 `toeic_tests`
```json
{
  "_id": "68a1f2...",
  "title": "ETS 2024 Test 01 - Reading",
  "pdfUrl": "/uploads/toeic/ets2024_test1.pdf",
  "totalQuestions": 100,
  "totalParts": [5, 6, 7],
  "answerKeys": [
    { "questionNumber": 101, "part": 5, "correctAnswer": "C" },
    { "questionNumber": 102, "part": 5, "correctAnswer": "A" }
  ],
  "createdAt": "2026-08-20T10:00:00Z"
}
```

### 4.2 `test_attempts`
```json
{
  "_id": "68b4e7...",
  "userId": "681cce6406f2dd257c72e60c",
  "testId": "68a1f2...",
  "rawScore": 85,
  "scaledScore": 425,
  "totalQuestions": 100,
  "durationSeconds": 3600,
  "completedAt": "2026-08-21T09:15:00Z",
  "answers": [
    {
      "questionNumber": 101,
      "part": 5,
      "userAnswer": "C",
      "correctAnswer": "C",
      "isCorrect": true,
      "flagged": false,
      "timeSpentSeconds": 24,
      "aiExplanation": "Giải thích chi tiết..."
    }
  ]
}
```

### 4.3 `mistake_queue`
```json
{
  "_id": "68c9a1...",
  "userId": "681cce6406f2dd257c72e60c",
  "testId": "68a1f2...",
  "attemptId": "68b4e7...",
  "questionNumber": 105,
  "part": 5,
  "userAnswer": "B",
  "correctAnswer": "D",
  "flagged": true,
  "status": "explained",
  "aiExplanation": "Giải thích chi tiết...",
  "createdAt": "2026-08-21T09:15:00Z"
}
```

---

## 5. Danh Mục REST Endpoints

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/api/toeic/tests` | Lấy danh sách đề thi kèm thống kê số lần làm. |
| `POST` | `/api/toeic/tests` | Tạo đề thi mới (PDF upload + Answer Keys). |
| `GET` | `/api/toeic/tests/{id}` | Lấy chi tiết đề thi. |
| `GET` | `/api/toeic/tests/file/{filename}` | Stream file PDF đề thi. |
| `POST` | `/api/toeic/tests/{id}/submit` | Nộp bài, tính Raw/Scaled score, tự động lưu attempt và push mistake queue. |
| `GET` | `/api/toeic/attempts/test/{testId}` | Lấy danh sách các lần làm bài của user theo đề. |
| `GET` | `/api/toeic/attempts/{attemptId}/review` | Lấy bài thi đã chấm điểm và giải thích AI để review. |
| `GET` | `/api/toeic/mistakes` | Lấy danh sách câu sai trong Hàng đợi lỗi. |
| `GET` | `/api/toeic/mistakes/prompt` | Trích xuất System Prompt cho ChatGPT/Claude. |
| `POST` | `/api/toeic/mistakes/import-ai-review` | Import JSON giải thích từ AI vào database. |

---

## 6. Serialization Rules
- Tất cả các trường boolean `isCorrect` trên DTOs bắt buộc khai báo `@JsonProperty("isCorrect")` để đảm bảo Jackson serialization tương thích 100% với Frontend TypeScript.
