# Implementation Plan: TOEIC Reader Backend Clean Architecture (v3)

> **Feature ID**: 003  
> **Target**: `EngComic_backend` (Spring Boot + MongoDB)

---

## 1. Clean Architecture Phân Tầng

```
src/main/java/mobile/
├── apis/reader/
│   ├── controllers/
│   │   ├── ToeicTestController.java      # CRUD Tests, Upload PDF, Stream file
│   │   ├── ToeicAttemptController.java   # Submit test, History, Review session
│   │   └── MistakeQueueController.java   # Mistake queue, AI prompt, Import JSON
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
    │   ├── ToeicTestDocument.java
    │   ├── TestAttemptDocument.java
    │   └── MistakeQueueDocument.java
    └── repositories/
        ├── ToeicTestMongoRepository.java
        ├── TestAttemptMongoRepository.java
        └── MistakeQueueMongoRepository.java
```

---

## 2. Các Bước Thực Hiện

1. **Khởi tạo Domain Documents**:
   - `ToeicTestDocument` (collection: `toeic_tests`)
   - `TestAttemptDocument` (collection: `test_attempts`)
   - `MistakeQueueDocument` (collection: `mistake_queue`)
2. **Cấu hình Bảo Mật & Streaming**:
   - Khai báo whitelisted paths trong `PublicSecurityEndpoints.java`.
   - Vô hiệu hóa `X-Frame-Options` trong `AppSecurityConfig.java`.
3. **Xây dựng Nghiệp Vụ Chấm Điểm & Nộp Bài**:
   - `SubmitToeicTestInteractor`: Chấm điểm theo bảng ETS Reading (0-100 ➔ 5-495), tự động chèn câu sai vào `mistake_queue`.
4. **Xây dựng Lời Giải AI & Hàng Đợi Lỗi**:
   - `MistakeQueueController`: API lấy danh sách câu sai, API trích xuất System Prompt JSON, API import lời giải AI đồng bộ ngược vào `test_attempts`.
5. **Serialization Safety**:
   - Thêm `@JsonProperty("isCorrect")` vào `ToeicAttemptDto` và `GradedQuestionDto`.
