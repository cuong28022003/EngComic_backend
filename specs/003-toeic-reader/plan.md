# Implementation Plan: TOEIC Reader — Backend (v2)

> **Perspective**: Backend Developer  
> **Purpose**: Answer the question **"HOW"** (Technical Architecture & Implementation)
> **Version**: v2 — All SQL (PostgreSQL), no MongoDB for this feature

---

## 1. Tech Stack

- **Framework**: Spring Boot — Clean Architecture (đúng chuẩn hiện tại)
- **Database**: PostgreSQL (4 bảng: tests, answer_keys, user_answers, mistakes)
- **File Storage**: Local filesystem (MVP) → S3-compatible (production)
- **Migration**: Flyway `V3__create_reader_tables.sql`
- **Layer pattern**: Controller → Interactor → Repository (Port + Adapter)

---

## 2. Package Structure

```
com.engcomic.reader/
├── domain/
│   ├── model/
│   │   ├── Test.java             (@Entity)
│   │   ├── AnswerKey.java        (@Entity)
│   │   ├── UserAnswer.java       (@Entity)
│   │   ├── Mistake.java          (@Entity)
│   │   └── MistakeStatus.java    (enum: PENDING, EXPLAINED, RESOLVED)
│   └── port/
│       ├── TestRepository.java
│       ├── AnswerKeyRepository.java
│       ├── UserAnswerRepository.java
│       └── MistakeRepository.java
│
├── application/
│   ├── dto/
│   │   ├── TestSummaryDto.java
│   │   ├── TestDetailDto.java            -- NO correct_answer
│   │   ├── CreateTestRequest.java        -- metadata + questions[]
│   │   ├── SubmitSessionRequest.java
│   │   ├── SubmitSessionResponse.java
│   │   ├── GradedResultDto.java
│   │   ├── PartBreakdownDto.java
│   │   ├── MistakeDto.java
│   │   ├── CreateMistakeBatchRequest.java
│   │   └── UpdateMistakeRequest.java
│   └── usecase/
│       ├── GetTestListInteractor.java
│       ├── GetTestDetailInteractor.java
│       ├── CreateTestInteractor.java
│       ├── SubmitSessionInteractor.java
│       ├── GetTestResultInteractor.java
│       ├── GetMistakeListInteractor.java
│       ├── CreateMistakeBatchInteractor.java
│       └── UpdateMistakeInteractor.java
│
└── adapter/
    ├── web/
    │   ├── TestController.java
    │   └── MistakeController.java
    └── persistence/
        ├── JpaTestAdapter.java
        ├── JpaAnswerKeyAdapter.java
        ├── JpaUserAnswerAdapter.java
        └── JpaMistakeAdapter.java
```

---

## 3. JPA Entities — Chi tiết

### Test.java
```java
@Entity @Table(name = "tests")
public class Test {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String userId;
    private String testName;
    private String pdfFileRef;
    private Integer rawScore;
    private Integer scaledScore;
    @Enumerated(EnumType.STRING)
    private TestStatus status;   // NOT_STARTED, IN_PROGRESS, COMPLETED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### AnswerKey.java
```java
@Entity @Table(name = "answer_keys",
    uniqueConstraints = @UniqueConstraint(columnNames = {"test_id", "question_number"}))
public class AnswerKey {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String testId;
    private int questionNumber;
    private int part;
    private String correctAnswer;  // "A", "B", "C", "D"
}
```

### UserAnswer.java
```java
@Entity @Table(name = "user_answers")
public class UserAnswer {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String testId;
    private String userId;
    private int questionNumber;
    private String userAnswer;   // nullable
    private Boolean isCorrect;  // null before submit
    private boolean flagged;
    private LocalDateTime answeredAt;
}
```

---

## 4. Key Interactors — Logic

### `SubmitSessionInteractor`
```
Input: testId, List<{questionNumber, answer}>, duration, userId

Steps:
  1. Load Test — validate exists + belongs to userId
  2. Load AnswerKeys cho testId (Map<questionNumber, correctAnswer>)
  3. Grade: với mỗi answer:
     - is_correct = (userAnswer != null && userAnswer.equals(correctAnswer))
     - Câu null → is_correct = false
  4. Persist UserAnswer records (batch insert / upsert)
  5. Tính rawScore = count(is_correct = true)
  6. Update Test: status = COMPLETED, raw_score = rawScore
  7. Build response: GradedResultDto[] + PartBreakdownDto[]
  
Return: SubmitSessionResponse (có correctAnswer trong results)
```

### `GetTestDetailInteractor`
```
Steps:
  1. Load Test
  2. Load AnswerKeys → map sang List<{number, part}> (KHÔNG có correctAnswer)
  3. Return TestDetailDto
```

### `CreateTestInteractor`
```
Steps:
  1. Save PDF file: uploads/tests/{generatedId}.pdf
  2. Create Test entity, set pdfFileRef
  3. Batch insert AnswerKey entities từ request.questions
  4. Return TestSummaryDto
```

### `UpdateMistakeInteractor`
```
Steps:
  1. Load Mistake, validate userId
  2. Validate status transition:
     PENDING → EXPLAINED: phải có explanation
     EXPLAINED → RESOLVED: ok
     Mọi chiều lùi → throw 400
  3. Update + save
```

---

## 5. Controller Endpoints

### TestController (`/api/v1/tests`)
```java
GET    /              → getTestList()
GET    /{id}          → getTestDetail(id)           // NO correct_answer
POST   /              → createTest(MultipartFile pdf, @RequestPart metadata)
POST   /{id}/submit   → submitSession(id, request)
GET    /{id}/result   → getTestResult(id)
```

### MistakeController (`/api/v1/mistakes`)
```java
GET    /              → getMistakeList(status, pageable)
POST   /batch         → createMistakeBatch(List<>)
PATCH  /{id}          → updateMistake(id, request)
DELETE /{id}          → deleteMistake(id)
```

---

## 6. File Upload Config

```yaml
# application.yml
spring:
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 55MB

app:
  upload:
    pdf-dir: ${PDF_UPLOAD_DIR:./uploads/tests}
    base-url: ${APP_BASE_URL:http://localhost:8080}
```

```java
// Serve static files
@Configuration
public class FileConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }
}
```

---

## 7. Flyway Migration

```sql
-- V3__create_reader_tables.sql

CREATE TABLE tests (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id VARCHAR(255) NOT NULL,
  test_name VARCHAR(500) NOT NULL,
  pdf_file_ref VARCHAR(1000),
  raw_score INTEGER,
  scaled_score INTEGER,
  status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE answer_keys (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  test_id UUID NOT NULL REFERENCES tests(id) ON DELETE CASCADE,
  question_number INTEGER NOT NULL,
  part INTEGER NOT NULL,
  correct_answer VARCHAR(1) NOT NULL,
  UNIQUE(test_id, question_number)
);

CREATE TABLE user_answers (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  test_id UUID NOT NULL REFERENCES tests(id) ON DELETE CASCADE,
  user_id VARCHAR(255) NOT NULL,
  question_number INTEGER NOT NULL,
  user_answer VARCHAR(1),
  is_correct BOOLEAN,
  flagged BOOLEAN DEFAULT false,
  answered_at TIMESTAMP,
  UNIQUE(test_id, user_id, question_number)
);

CREATE TABLE mistakes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id VARCHAR(255) NOT NULL,
  test_id UUID NOT NULL REFERENCES tests(id) ON DELETE CASCADE,
  test_name VARCHAR(500) NOT NULL,
  question_number INTEGER NOT NULL,
  part INTEGER NOT NULL,
  user_answer VARCHAR(1),
  correct_answer VARCHAR(1) NOT NULL,
  explanation TEXT,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP DEFAULT now(),
  updated_at TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_tests_user ON tests(user_id);
CREATE INDEX idx_user_answers_test_user ON user_answers(test_id, user_id);
CREATE INDEX idx_mistakes_user_status ON mistakes(user_id, status);
```

---

## 8. Implementation Order

### Phase 1: DB + Entities
- [ ] Flyway V3 migration script
- [ ] `Test.java`, `AnswerKey.java`, `UserAnswer.java`, `Mistake.java` entities
- [ ] `TestStatus.java`, `MistakeStatus.java` enums
- [ ] 4 JPA Repositories

### Phase 2: Create Test
- [ ] `CreateTestRequest.java` DTO
- [ ] `CreateTestInteractor.java` (save PDF file + batch insert answer_keys)
- [ ] `TestController` POST `/tests`
- [ ] File upload config + static serve

### Phase 3: Test Detail & Submit
- [ ] `GetTestDetailInteractor` (strip correct_answer)
- [ ] `SubmitSessionInteractor` (grade + save UserAnswer + update Test)
- [ ] `TestController` GET `/:id` + POST `/:id/submit`
- [ ] Test: correct_answer KHÔNG leak trong GET

### Phase 4: Mistake Queue
- [ ] `CreateMistakeBatchInteractor`
- [ ] `GetMistakeListInteractor` (filter by status + userId)
- [ ] `UpdateMistakeInteractor` (status transition validation)
- [ ] `MistakeController` full CRUD

### Phase 5: Unit Tests
- [ ] `SubmitSessionInteractor` — grading logic (null answer = wrong, all correct, all wrong)
- [ ] `UpdateMistakeInteractor` — status transition (PENDING→EXPLAINED→RESOLVED, reject backward)
- [ ] Security: verify correct_answer không có trong GET /tests/:id response

---

## 9. Target Timing, Pacing Engine & Part-Specific Practice Implementation (v3 Extension)

### 9.1 MongoDB Document Mapping (`toeic_user_sessions`)
- Bổ sung cấu trúc dữ liệu timing và practice scope vào `ToeicUserSessionEntity.java`:
```java
@Document(collection = "toeic_user_sessions")
public class ToeicUserSessionEntity {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String userId;
    private String testId;
    private String testName;
    
    private int rawScore;
    private Integer scaledScore;
    private int totalQuestions;
    private int duration; // seconds

    private String timeMode; // "full_test", "per_part", "untimed"
    @Builder.Default
    private List<Integer> selectedParts = new ArrayList<>();
    private int part5TargetSeconds;
    private int part6TargetSeconds;
    private int part7TargetSeconds;
    private int part5ElapsedSeconds;
    private int part6ElapsedSeconds;
    private int part7ElapsedSeconds;
    
    @Builder.Default
    private List<UserAnswerRecord> answers = new ArrayList<>();
    
    @CreatedDate
    private Date submittedAt;

    public static class UserAnswerRecord {
        private int questionNumber;
        private int part;
        private String userAnswer;
        private String correctAnswer;
        private boolean isCorrect;
        private boolean flagged;
        private int timeSpentSeconds;
    }
}
```

### 9.2 Custom Part-Practice Grading Logic (`SubmitToeicSessionInteractor.java`)
```
Input: testId, SubmitToeicSessionRequest (answers, duration, timeMode, selectedParts, partTargets, partElapsed)

Steps:
  1. Load ToeicTestEntity, validate userId.
  2. Lấy selectedParts = request.selectedParts != null ? request.selectedParts : [5, 6, 7].
  3. Lọc danh sách câu hỏi: questions = test.questions.filter(q -> selectedParts.contains(q.part)).
  4. totalQuestions = questions.size() (ví dụ: 30 câu nếu chỉ chọn Part 5).
  5. Chấm điểm từng câu trong questions:
     - Ghi nhận timeSpentSeconds từ UserAnswerItem.
     - Nếu sai: tạo ToeicMistakeEntity (chỉ thuộc các câu trong selectedParts).
  6. Tính rawScore và accuracyPercentage = (rawScore / totalQuestions) * 100.
  7. Tính PartBreakdownDto[] kèm targetSeconds, elapsedSeconds, và avgSecondsPerQuestion.
  8. Persist ToeicUserSessionEntity với đầy đủ thông tin timing và selectedParts.
```

### 9.3 Google Translate Integration
- Controller: `/api/translator/translate?text=...&from=en&to=vi`
- Service: `TranslatorClientServiceImpl.java` gọi trực tiếp Google Translate endpoint (`client=gtx&sl=en&tl=vi&dt=t`).
- Security: Cấu hình permitAll trong `PublicSecurityEndpoints.java`.
