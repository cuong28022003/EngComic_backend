# Implementation Plan: Word Journey Backend Clean Architecture (v3)

> **Feature ID**: 002  
> **Target**: `EngComic_backend` (Spring Boot + MongoDB)

---

## 1. Clean Architecture Phân Tầng

```
src/main/java/mobile/
├── apis/vocab/
│   ├── CardController.java               # Endpoints for practice prompt, import json, queue, submit, leech
│   └── dtos/
│       ├── CardExercisePackageDto.java   # Level 1 to 4 exercise DTOs
│       ├── PracticePromptResponseDto.java
│       ├── ImportPracticeJsonRequest.java
│       ├── SubmitLevelAnswerRequest.java
│       ├── SubmitLevelAnswerResponseDto.java
│       └── PracticeQueueItemDto.java
├── businesses/
│   ├── boundaries/vocab/
│   │   ├── GeneratePracticePromptBoundary.java
│   │   ├── ImportPracticeJsonBoundary.java
│   │   ├── GetPracticeQueueBoundary.java
│   │   ├── SubmitLevelAnswerBoundary.java
│   │   ├── GetLeechCardsBoundary.java
│   │   └── ClearLeechStatusBoundary.java
│   └── interactors/vocab/
│       ├── GeneratePracticePromptInteractor.java
│       ├── ImportPracticeJsonInteractor.java
│       ├── GetPracticeQueueInteractor.java
│       ├── SubmitLevelAnswerInteractor.java
│       ├── GetLeechCardsInteractor.java
│       └── ClearLeechStatusInteractor.java
└── databases/
    ├── entities/vocab/
    │   ├── CardEntity.java               # +masteryLevel, +confidenceScore, +exercisePackage, +wrongCount, +memoryTip
    │   └── CardExercisePackage.java      # Embedded 4-Level AI Exercise Package
    └── repositories/vocab/
        └── CardRepository.java
```

---

## 2. Các Bước Triển Khai

1. **Khởi tạo Domain Entity & Embedded Documents**:
   - `CardExercisePackage` (Level1Recognition, Level2Context, Level3Production, Level4Realworld).
   - Cập nhật `CardEntity` với `masteryLevel` (1-4), `confidenceScore` (1-5), `wrongCount`, `memoryTip`, `exercisePackage`.
2. **Xây dựng Interactors AI Bridge**:
   - `GeneratePracticePromptInteractor`: Sinh System Prompt chuẩn hóa cho ChatGPT/Claude kèm schema JSON mẫu.
   - `ImportPracticeJsonInteractor`: Validate mảng JSON từ AI, lưu vào `CardEntity.exercisePackage`.
3. **Xây dựng Interactors Phòng Luyện Tập**:
   - `GetPracticeQueueInteractor`: Lấy danh sách từ đến hạn ôn tập kèm bài tập của Level hiện tại, loại trừ từ Leech.
   - `SubmitLevelAnswerInteractor`: Tính toán thăng hạng Level, thuật toán SRS SM-2 và phát hiện từ kẹt (`wrongCount >= 4 ➔ Leech`).
4. **Xây dựng Interactors Leech Center**:
   - `GetLeechCardsInteractor`: Lấy danh sách từ kẹt.
   - `ClearLeechStatusInteractor`: Lưu mẹo ghi nhớ và đưa từ vựng về lại Level 1.
