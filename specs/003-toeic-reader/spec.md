# Feature Specification: TOEIC Reader — Backend (v2)

> **Perspective**: Business / Product Owner & Developer  
> **Purpose**: Answer the question **"WHAT & WHY"** (Domain, Data Contract, Business Rules)
> **Version**: v2 — Simplified schema (no passages/question_text, PDF is display-only)

---

## 1. Overview & Objective

- **Feature ID**: 003
- **Feature Name**: TOEIC Reader — PDF Viewer & Answer Sheet
- **Priority**: P1
- **Objective**: Backend API cho hệ thống làm bài TOEIC đơn giản — lưu trữ đề thi (PDF ref + answer keys), chấm điểm khi nộp bài, và quản lý hàng đợi lỗi (Mistake Queue). Không cần xử lý nội dung PDF, không cần lưu câu hỏi/đáp án bốn lựa chọn — chỉ cần lưu đáp án đúng (A/B/C/D) theo số câu.

> **Bỏ so với v1**:
> - ❌ Collection `passages` (không cần content câu hỏi trong DB)
> - ❌ `question_text`, `options_raw`, `explanation` trong schema
> - ❌ Auto-parse PDF
> - ❌ MongoDB cho session (chuyển sang SQL cho đơn giản)

---

## 2. Permissions & Preconditions

- **Target Audience**: `USER` (authenticated)
- **Auth**: JWT Bearer Token required cho mọi endpoint.

---

## 3. Domain Model

### 3.1 Schema DB (PostgreSQL — tất cả trong 1 datasource)

```sql
-- Bài thi
CREATE TABLE tests (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         VARCHAR(255) NOT NULL,
  test_name       VARCHAR(500) NOT NULL,
  pdf_file_ref    VARCHAR(1000),         -- path/URL đến file PDF
  raw_score       INTEGER,               -- điểm thô (số câu đúng)
  scaled_score    INTEGER,               -- điểm quy đổi TOEIC (nếu có)
  status          VARCHAR(20) NOT NULL DEFAULT 'not_started',
                                         -- not_started | in_progress | completed
  created_at      TIMESTAMP DEFAULT now(),
  updated_at      TIMESTAMP DEFAULT now()
);

-- Đáp án đúng (import từ JSON)
CREATE TABLE answer_keys (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  test_id         UUID NOT NULL REFERENCES tests(id) ON DELETE CASCADE,
  question_number INTEGER NOT NULL,      -- 101, 102, ..., 200
  part            INTEGER NOT NULL,      -- 5, 6, or 7
  correct_answer  VARCHAR(1) NOT NULL,   -- 'A', 'B', 'C', 'D'
  UNIQUE(test_id, question_number)
);

-- Đáp án của user
CREATE TABLE user_answers (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  test_id         UUID NOT NULL REFERENCES tests(id) ON DELETE CASCADE,
  user_id         VARCHAR(255) NOT NULL,
  question_number INTEGER NOT NULL,
  user_answer     VARCHAR(1),            -- null nếu bỏ qua câu
  is_correct      BOOLEAN,              -- null trước khi chấm, set sau submit
  flagged         BOOLEAN DEFAULT false,
  answered_at     TIMESTAMP,
  UNIQUE(test_id, user_id, question_number)
);

-- Lỗi sai tích lũy
CREATE TABLE mistakes (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id         VARCHAR(255) NOT NULL,
  test_id         UUID NOT NULL REFERENCES tests(id) ON DELETE CASCADE,
  test_name       VARCHAR(500) NOT NULL,
  question_number INTEGER NOT NULL,
  part            INTEGER NOT NULL,
  user_answer     VARCHAR(1),
  correct_answer  VARCHAR(1) NOT NULL,
  explanation     TEXT,                  -- paste từ AI
  status          VARCHAR(20) NOT NULL DEFAULT 'pending',
                                         -- pending | explained | resolved
  created_at      TIMESTAMP DEFAULT now(),
  updated_at      TIMESTAMP DEFAULT now()
);

-- Indexes
CREATE INDEX idx_tests_user ON tests(user_id);
CREATE INDEX idx_answer_keys_test ON answer_keys(test_id);
CREATE INDEX idx_user_answers_test_user ON user_answers(test_id, user_id);
CREATE INDEX idx_mistakes_user_status ON mistakes(user_id, status);
```

---

## 4. API Contract

### 4.1 Test APIs (`/api/v1/tests`)

| Method | Path | Auth | Description |
|:---|:---|:---|:---|
| GET | `/api/v1/tests` | Required | Danh sách tests của user |
| GET | `/api/v1/tests/:id` | Required | Chi tiết test (questions list — NO correct_answer) |
| POST | `/api/v1/tests` | Required | Tạo test mới (multipart: pdf + JSON metadata) |
| POST | `/api/v1/tests/:id/submit` | Required | Nộp bài — chấm điểm + lưu kết quả |
| GET | `/api/v1/tests/:id/result` | Required | Kết quả đã chấm (sau submit) |

#### POST `/api/v1/tests` — Tạo test mới
Request: `multipart/form-data`
```
pdf: <file>
metadata: {
  "test_name": "ETS 2024 Test 5",
  "questions": [
    { "number": 101, "part": 5, "correct_answer": "C" },
    { "number": 102, "part": 5, "correct_answer": "A" }
  ]
}
```

Response `201`:
```json
{
  "id": "uuid",
  "testName": "ETS 2024 Test 5",
  "pdfFileRef": "/uploads/tests/uuid.pdf",
  "questionCount": 100,
  "status": "not_started",
  "createdAt": "2024-08-20T07:00:00Z"
}
```

#### GET `/api/v1/tests/:id` — Chi tiết (để làm bài)
Response (KHÔNG có `correct_answer`):
```json
{
  "id": "uuid",
  "testName": "ETS 2024 Test 5",
  "pdfFileRef": "/uploads/tests/uuid.pdf",
  "questions": [
    { "number": 101, "part": 5 },
    { "number": 102, "part": 5 }
  ]
}
```

#### POST `/api/v1/tests/:id/submit`
Request:
```json
{
  "answers": [
    { "questionNumber": 101, "answer": "C" },
    { "questionNumber": 102, "answer": "B" }
  ],
  "duration": 3240
}
```

Response:
```json
{
  "testId": "uuid",
  "testName": "ETS 2024 Test 5",
  "rawScore": 82,
  "scaledScore": null,
  "totalQuestions": 100,
  "duration": 3240,
  "partBreakdown": [
    { "part": 5, "correct": 35, "total": 40 },
    { "part": 6, "correct": 15, "total": 16 },
    { "part": 7, "correct": 32, "total": 44 }
  ],
  "results": [
    { "questionNumber": 101, "part": 5, "userAnswer": "C", "correctAnswer": "C", "isCorrect": true },
    { "questionNumber": 102, "part": 5, "userAnswer": "B", "correctAnswer": "A", "isCorrect": false }
  ]
}
```

### 4.2 Mistake APIs (`/api/v1/mistakes`)

| Method | Path | Body/Params | Response |
|:---|:---|:---|:---|
| GET | `/api/v1/mistakes` | `?status=pending` | `Page<MistakeDto>` |
| POST | `/api/v1/mistakes/batch` | `MistakeDto[]` | `MistakeDto[]` |
| PATCH | `/api/v1/mistakes/:id` | `{status, explanation}` | `MistakeDto` |
| DELETE | `/api/v1/mistakes/:id` | - | `204` |

---

## 5. Business Rules

- **R1**: `correct_answer` KHÔNG được expose trong `GET /tests/:id` — chỉ trả về sau POST submit.
- **R2**: User chỉ thấy tests/mistakes của chính mình (filter by `userId` từ JWT).
- **R3**: Câu không trả lời (`answer = null`) tính là **SAI** khi chấm điểm.
- **R4**: Sau submit — `user_answers` được persist, `tests.status = completed`, `tests.raw_score` được cập nhật.
- **R5**: Mistake status transitions chỉ tiến: `pending → explained → resolved`.
- **R6**: PDF file được lưu trên server filesystem hoặc cloud storage — `pdf_file_ref` là URL để frontend load vào iframe.
- **R7**: `answer_keys` được import khi tạo test — không thể sửa sau khi test đã có `user_answers`.

---

## 6. File Storage

PDF files được lưu tại: `uploads/tests/{testId}.pdf`  
Serve static: `/uploads/**` qua Spring `ResourceHttpRequestHandler` hoặc Nginx.  
Với production: dùng S3-compatible storage, `pdf_file_ref` là presigned URL.

---

## 7. Security Rules

- Mọi endpoint yêu cầu JWT.
- `userId` lấy từ JWT principal — KHÔNG nhận từ request body.
- Validate: test phải thuộc userId hiện tại trước mọi thao tác.

---

## 8. Target Timer, Pacing Status & Part Practice Mode (v3 Extension)

### 8.1 Chế độ tính giờ mục tiêu & Luyện tập theo Part
Hỗ trợ đo lường nhịp độ làm bài (Pacing) và cho phép người dùng tùy chọn luyện tập theo từng Part hoặc toàn bộ 100 câu:
- **`selectedParts`**: Danh sách Part người dùng muốn làm trong session (ví dụ: `[5]` cho 30 câu Part 5, `[6]` cho 16 câu Part 6, `[7]` cho 54 câu Part 7, hoặc `[5, 6, 7]` cho toàn bộ đề).
- **`timeMode`**:
  - `full_test`: 75 phút chuẩn TOEIC.
  - `per_part`: Đặt giờ mục tiêu riêng từng Part (mặc định 20p / 10p / 45p, cho phép tùy chỉnh).
  - `untimed`: Không giới hạn thời gian (chỉ đo thời gian làm bài thực tế).
- **`timeSpentSeconds`**: Đo chính xác thời gian (giây) người dùng thao tác và chọn đáp án trên từng câu hỏi.

### 8.2 Quy tắc nghiệp vụ bổ sung
- **R8**: Khi `selectedParts` được cung cấp trong submit session payload, hệ thống CHỈ chấm điểm trên các câu thuộc các Part đó.
  - `totalQuestions` = tổng số câu của các Part được chọn (ví dụ: 30 câu nếu chỉ chọn Part 5).
  - `accuracyPercentage` = `(rawScore / totalQuestions) * 100`.
  - Các Part không được chọn KHÔNG bị tính là sai và KHÔNG tạo ra câu sai trong `ToeicMistakeEntity`.
- **R9**: Hàng đợi lỗi (`mistakes`) tự động sắp xếp theo `testName` và `questionNumber ASC`.
- **R10**: Endpoint Google Translate `/api/translator/**` phục vụ tra từ điển tức thì tốc độ cao và mở public trong security config.

### 8.3 Mở rộng Submit Session Contract (`POST /api/toeic/tests/{id}/submit`)
Request payload:
```json
{
  "duration": 1200,
  "timeMode": "per_part",
  "selectedParts": [5],
  "part5TargetSeconds": 1200,
  "part6TargetSeconds": 0,
  "part7TargetSeconds": 0,
  "part5ElapsedSeconds": 1150,
  "part6ElapsedSeconds": 0,
  "part7ElapsedSeconds": 0,
  "answers": [
    { "questionNumber": 101, "answer": "C", "flagged": false, "timeSpentSeconds": 25 },
    { "questionNumber": 102, "answer": "A", "flagged": false, "timeSpentSeconds": 30 }
  ]
}
```

Response payload:
```json
{
  "testId": "uuid",
  "testName": "ETS 2024 Test 5",
  "rawScore": 27,
  "totalQuestions": 30,
  "accuracyPercentage": 90.0,
  "duration": 1150,
  "partBreakdown": [
    {
      "part": 5,
      "correctCount": 27,
      "totalCount": 30,
      "accuracyPercentage": 90.0,
      "targetSeconds": 1200,
      "elapsedSeconds": 1150,
      "avgSecondsPerQuestion": 38.3
    }
  ],
  "results": [
    {
      "questionNumber": 101,
      "part": 5,
      "userAnswer": "C",
      "correctAnswer": "C",
      "isCorrect": true,
      "flagged": false,
      "timeSpentSeconds": 25
    }
  ],
  "newMistakes": []
}
```
