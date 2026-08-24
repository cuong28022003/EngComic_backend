# Feature Specification: Word Journey — 4-Level AI Practice System (v3)

> **Perspective**: Business / Product Owner & Backend Developer  
> **Purpose**: Answer the question **"WHAT & WHY"** (AI Prompt & JSON Import, 4-Level Mastery Pipeline, Clean Architecture)  
> **Feature ID**: 002  
> **Version**: v3 — AI Chat Prompt ➔ JSON Import Engine + 4-Level Mastery Architecture

---

## 1. Overview & Objective

- **Feature ID**: 002
- **Feature Name**: Word Journey — 4-Level AI Mastery Practice System
- **Priority**: P1
- **Objective**: Cung cấp môi trường luyện tập từ vựng chuyên sâu dựa trên ngân hàng bài tập chất lượng cao do AI (ChatGPT/Claude) tạo ra thông qua cơ chế **AI Chat Prompt ➔ JSON Import**:
  1. Trích xuất System Prompt cho toàn bộ từ trong Deck để sinh bộ bài tập 4 Level.
  2. Import JSON bài tập vào `CardEntity.exercisePackage` trong MongoDB.
  3. Luyện tập theo hàng đợi SRS SM-2 và thăng cấp qua 4 Level Tinh Thông.
  4. Quản lý từ vựng hay sai trong Leech Center (`wrongCount >= 4`).

---

## 2. Mô Hình 4 Level Tinh Thông (4-Level Mastery Pipeline)

- **Level 1: Nhận Biết (Recognition)**: Trắc nghiệm 4 nghĩa (MCQ) với đáp án nhiễu thông minh.
- **Level 2: Ngữ Cảnh (Context & Collocation)**: Điền từ vào chỗ trống trong câu ngữ cảnh thực tế kèm ghi chú cụm từ cố định.
- **Level 3: Tái Hiện (Active Production)**: Sắp xếp các mảnh từ thành câu hội thoại hoàn chỉnh (Word Ordering).
- **Level 4: Thực Tế & Tự Tin (Real-World & Confidence)**: Xử lý tình huống giao tiếp thực tế + Tự đánh giá mức độ tự tin (1–5 ⭐) ➔ Tác động chu kỳ SRS SM-2.

---

## 3. Schema Dữ Liệu `CardEntity` (MongoDB)

```java
public class CardEntity {
    private String id;
    private String userId;
    private String deckId;
    private String word;
    private String meaning;
    private String ipa;
    private String audio;
    
    // Feature 002 New Fields:
    private int masteryLevel = 1;      // 1 to 4
    private int confidenceScore = 0;   // 1 to 5 (updated at Level 4)
    private CardExercisePackage exercisePackage; // AI Imported Exercise Package
    private int wrongCount = 0;        // Consecutive wrong count (>= 4 -> Leech)
    private String memoryTip;          // Memory tip when rescued from Leech
    
    // SRS SM-2 Fields:
    private double easeFactor = 2.5;
    private int interval = 1;
    private Date nextReview;
    private String status = "learning"; // new | learning | review | mature | leech
}
```

---

## 4. Danh Mục REST Endpoints (`/api/card`)

| Method | Endpoint | Quyền hạn | Mô tả |
|---|---|---|---|
| `GET` | `/api/card/deck/{deckId}/practice-prompt` | `HAS_CARD_READ` | Trích xuất System Prompt cho ChatGPT/Claude kèm template JSON mẫu. |
| `POST` | `/api/card/deck/{deckId}/import-practice-json` | `HAS_CARD_WRITE` | Import mảng JSON bài tập AI, lưu vào `CardEntity.exercisePackage`. |
| `GET` | `/api/card/practice/queue` | `HAS_CARD_READ` | Lấy danh sách thẻ từ đến hạn ôn tập hôm nay kèm bài tập Level hiện tại. |
| `POST` | `/api/card/{id}/submit-level` | `HAS_CARD_WRITE` | Nộp kết quả Level, tự động thăng Level và cập nhật chu kỳ SRS SM-2. |
| `GET` | `/api/card/leech` | `HAS_CARD_READ` | Lấy danh sách các từ bị kẹt (làm sai ≥ 4 lần). |
| `POST` | `/api/card/{id}/clear-leech` | `HAS_CARD_WRITE` | Nhập mẹo ghi nhớ và gỡ trạng thái Leech để quay lại Level 1. |
