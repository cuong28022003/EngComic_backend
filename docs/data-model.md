# Data Model & Business Invariants: EngComic_backend

## 1. Persistence & MongoDB Conventions

- **Database**: MongoDB
- **Entity Annotation**: `@Document(collection = "<collection_name>")`
- **Primary Key**:
  - **New Clean Architecture Features**: `@MongoId(FieldType.OBJECT_ID) protected String id;` (String representations for IDs across API and Repository layers).
  - **Legacy Modules**: `@Id protected ObjectId id;` (Dần được migrate sang String ID).
- **Foreign Identifiers**:
  - New modules use `String` fields (e.g. `userId`, `deckId`, `sourceCardId`).
  - Embedded documents / value objects are used for composite structures (e.g. `WordRelation`, `ExampleSentence`).
- **Audit & Timestamps**: Managed using `Date createAt`, `Date updateAt` and `@CreatedDate` / `@LastModifiedDate`.
- **Sensitive Data**: Marked with `@JsonIgnore` (e.g. `password` in `User`).

---

## 2. Core Entities & Domain Models

### 2.1 User & Authorization
- **`User`** (`collection = "user"`):
  - Fields: `id (ObjectId)`, `username`, `email`, `password (JsonIgnore)`, `fullName`, `birthday (LocalDate)`, `createdate (Date)`, `image`, `active (Boolean)`, `status (String)`.
  - Roles: `@DBRef Set<Role> roles`.
- **`Role`** (`collection = "role"`):
  - Defines role names: `USER`, `ADMIN`, `TRANSLATOR`.
- **`UserStats`** (`collection = "user_stats"`):
  - Tracks user EXP, level, currency/coins, ranking points.

### 2.2 Feature 001: Vocabulary Vault & Flashcards (`mobile.databases.entities.*`)
- **`CardEntity`** (`collection = "card"`):
  - Primary Key: `@MongoId(FieldType.OBJECT_ID) String id`
  - References: `String userId`, `String deckId`
  - Vocabulary Fields: `front`, `back`, `ipa`, `partOfSpeech`, `definitionEn`, `topic`, `audioUrl`, `imageUrl`
  - Embedded Data: `List<ExampleSentence> examples`, `List<WordRelation> relations`
  - SRS Fields: `repetition (int)`, `interval (int)`, `easeFactor (double)`, `nextReview (Date)`, `lastReviewed (Date)`, `wrongCount (int)`, `stage (int)`, `status (String: "new" | "learning" | "mature" | "leech")`
- **`DeckEntity`** (`collection = "deck"`):
  - Primary Key: `@MongoId(FieldType.OBJECT_ID) String id`
  - References: `String userId`
  - Fields: `name`, `description`, `createAt`, `updateAt`
- **`PendingItemEntity`** (`collection = "pending_item"`):
  - Primary Key: `@MongoId(FieldType.OBJECT_ID) String id`
  - References: `String userId`, `String sourceCardId`
  - Fields: `content`, `sourceType ("comic" | "manual")`, `status ("pending" | "processed")`, `createdAt`

### 2.3 Comic, Novel & Chapter Domain (Legacy / Next Migration)
- **`Comic`** (`collection = "comic"`):
  - Fields: `id`, `name`, `artist`, `description`, `imageUrl`, `backgroundUrl`, `url`, `views`, `genre`, `status`, `englishLevel`, `ageRating`, `uploaderId`, `createdAt`, `updatedAt`.
- **`Chapter`** (`collection = "chapter"`):
  - Fields: `id`, `comicId`, `chapterNumber`, `title`, `content` / `images`, `views`, `createdAt`, `updatedAt`.
- **`Comment`**, **`Rating`**, **`Reading`**, **`Saved`**:
  - Đọc truyện, đánh giá, bookmark và lịch sử đọc.

### 2.4 Feature 003: TOEIC Reader & Pacing Engine (`mobile.databases.entities.reader.*`)
- **`ToeicTestEntity`** (`collection = "toeic_tests"`):
  - Primary Key: `@MongoId(FieldType.OBJECT_ID) String id`
  - References: `String userId`
  - Fields: `testName`, `pdfUrl`, `status ("not_started" | "in_progress" | "completed")`, `rawScore`, `scaledScore`, `List<ToeicQuestion> questions` (mỗi câu gồm `number`, `part`, `correctAnswer`), `createdAt`, `updatedAt`.
- **`ToeicUserSessionEntity`** (`collection = "toeic_user_sessions"`):
  - Primary Key: `@MongoId(FieldType.OBJECT_ID) String id`
  - References: `String userId`, `String testId`
  - Score & Scope Fields: `rawScore`, `scaledScore`, `totalQuestions`, `duration (seconds)`, `timeMode ("full_test" | "per_part" | "untimed")`, `List<Integer> selectedParts`
  - Timing Target & Elapsed Fields: `part5TargetSeconds`, `part6TargetSeconds`, `part7TargetSeconds`, `part5ElapsedSeconds`, `part6ElapsedSeconds`, `part7ElapsedSeconds`
  - Embedded Answers: `List<UserAnswerRecord> answers` (mỗi record gồm `questionNumber`, `part`, `userAnswer`, `correctAnswer`, `isCorrect`, `flagged`, `timeSpentSeconds`), `submittedAt`.
- **`ToeicMistakeEntity`** (`collection = "toeic_mistakes"`):
  - Primary Key: `@MongoId(FieldType.OBJECT_ID) String id`
  - References: `String userId`, `String testId`
  - Fields: `testName`, `questionNumber`, `part`, `userAnswer`, `correctAnswer`, `explanation`, `status ("pending" | "explained" | "resolved")`, `createdAt`, `updatedAt`.

---

## 3. Business Invariants & Domain Rules

### 3.1 Vocabulary & SRS Domain Rules (`mobile.domains.card.CardRules`)
1. **SM-2 Spaced Repetition Algorithm**:
   - `quality >= 3`: Tăng `repetition`, tính `interval = interval * easeFactor`, điều chỉnh `easeFactor`.
   - `quality < 3`: Reset `repetition = 0`, `interval = 1`, tăng `wrongCount` và `lapses`.
2. **Leech Detection**:
   - Nếu `wrongCount >= 8`: Thẻ được tự động đánh dấu trạng thái `"leech"` (từ vựng khó nhớ cần xem lại).
3. **Mastery Status**:
   - `interval >= 21`: Chuyển sang trạng thái `"mature"`.
   - `repetition > 0`: Trạng thái `"learning"`.
   - Mặc định: Trạng thái `"new"`.

### 3.2 Deck Statistics & Capacity Rules (`mobile.domains.deck.DeckRules`)
1. **Statistics Calculation**: Thống kê số lượng thẻ `totalCards`, `totalNew`, `totalEasy`, `totalHard`, `totalDue` (thẻ đến hạn ôn tập trước thời điểm hiện tại).
2. **Deck Capacity Check**: Giới hạn tối đa số lượng thẻ trong 1 Deck theo hạn mức gói tài khoản.

### 3.3 Security & Authentication
1. `username` và `email` phải là duy nhất. Mật khẩu mã hóa BCrypt.
2. Mọi truy cập vào tài nguyên cá nhân (`Card`, `Deck`, `PendingItem`) phải được xác thực qua JWT Bearer Token, trích xuất danh tính qua `SecurityUtils.getCurrentUserId()`.
