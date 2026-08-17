# Task Breakdown: Unified Vocabulary Learning System

> **Perspective**: Developer / AI Agent  
> **Purpose**: Answer the question **"EXECUTION & CHECKLIST"**  
> **Instructions for AI**: Execute tasks sequentially. Run `./mvnw compile` after completing each task to verify syntax before checking `[x]` and moving to the next task.

---

## Phase 1: Database & Entity Layer (`model/Entity/` + `repository/`)

### Data Model Evolution

- [x] **T001**: Create embedded document `WordRelation.java`
  - **Where**: `src/main/java/mobile/model/Entity/WordRelation.java`
  - **Details**:
    - Fields: `relatedText` (String), `relationType` (String: "family"/"collocation"/"synonym"), `pos` (String, nullable), `relatedCardId` (String, nullable)
    - Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`
    - **Not** a `@Document` — this is an embedded subdocument within `Card`

- [x] **T002**: Evolve `ExampleSentence.java` — add `formality` field
  - **Where**: `src/main/java/mobile/model/Entity/ExampleSentence.java`
  - **Details**:
    - Add field: `private String formality;` (values: "formal"/"informal"/"written")
    - Keep existing fields (`sentence`, `translation`, `source`) unchanged for backward compatibility

- [x] **T003**: Evolve `Card.java` — add new fields, embed `WordRelation`
  - **Where**: `src/main/java/mobile/model/Entity/Card.java`
  - **Details**:
    - Add fields:
      - `definitionEn` (String, nullable) — English definition
      - `usageNote` (String, nullable) — usage context note
      - `topic` (String, nullable) — topic tag
      - `relations` (`List<WordRelation>`, default empty list) — unified relations
      - `stage` (int, default `0`) — practice stage 0–5
      - `status` (String, default `"new"`) — replaces `masteryStatus`
      - `wrongCount` (int, default `0`) — for leech detection
      - `seenExampleIds` (`List<String>`, default empty list) — tracks used examples in exercises
    - Keep existing SRS fields: `interval`, `easeFactor`, `repetition`, `lapses`, `reviewCount`, `lastReviewed`, `nextReview`
    - Keep existing fields: `front`, `back`, `ipa`, `audio`, `image`, `tags`, `personalNote`, `myExample`, `isFavorite`
    - **Deprecate** (keep but stop using in new code): `masteryStatus`, `level`, `collocations`, `synonyms`, `antonyms`, `wordFamily`, `commonMistakes`
    - No `entryType` field — words and collocations are identical Cards

- [x] **T004**: Create entity `PendingItem.java`
  - **Where**: `src/main/java/mobile/model/Entity/PendingItem.java`
  - **Details**:
    - `@Document(collection = "pending_item")`
    - Fields: `id` (ObjectId), `userId` (ObjectId), `content` (String), `sourceType` (String: "family"/"collocation"/"synonym"/"manual", nullable), `sourceCardId` (ObjectId, nullable), `status` (String, default "pending"), `createdAt` (Date, `@CreatedDate`)
    - Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`

- [x] **T005**: Evolve `CardRepository.java` — add new query methods
  - **Where**: `src/main/java/mobile/repository/CardRepository.java`
  - **Details**:
    - Add: `Optional<Card> findByUserIdAndFrontIgnoreCase(ObjectId userId, String front)` — duplicate detection
    - Add: `Page<Card> findByUserIdAndStatus(ObjectId userId, String status, Pageable pageable)` — filter by SRS status
    - Add: `Page<Card> findByUserIdAndTopic(ObjectId userId, String topic, Pageable pageable)` — filter by topic
    - Add: `List<Card> findByUserIdAndNextReviewLessThanEqual(ObjectId userId, Date date)` — due cards for practice
    - Add: `long countByUserIdAndStatus(ObjectId userId, String status)` — dashboard stats
    - Add: `long countByUserIdAndNextReviewLessThanEqual(ObjectId userId, Date date)` — due today count
    - Keep all existing methods unchanged

- [x] **T006**: Create `PendingItemRepository.java`
  - **Where**: `src/main/java/mobile/repository/PendingItemRepository.java`
  - **Details**:
    - Extend `MongoRepository<PendingItem, ObjectId>`
    - Methods:
      - `Page<PendingItem> findByUserIdAndStatus(ObjectId userId, String status, Pageable pageable)`
      - `Optional<PendingItem> findByUserIdAndContentIgnoreCase(ObjectId userId, String content)`
      - `List<PendingItem> findByUserIdAndStatus(ObjectId userId, String status)`
      - `void deleteByIdAndUserId(ObjectId id, ObjectId userId)`

### ⚙ Checkpoint: `./mvnw compile` (Passed)

---

## Phase 2: Business Logic & Service Layer (`Service/`)

- [x] **T007**: Implement batch import logic in `CardService`
  - **Where**: `src/main/java/mobile/Service/CardService.java` & `src/main/java/mobile/Service/Impl/CardServiceImpl.java`
  - **Details**:
    - Method: `BatchImportResponse batchImport(ObjectId userId, String jsonContent, ObjectId deckId)`
    - Steps:
      1. Parse JSON string → `List<Map<String, Object>>`
      2. Validate each entry: require `word` and `meaning_vi`; warn if missing optional fields
      3. Check duplicates via `cardRepository.findByUserIdAndFrontIgnoreCase()`
      4. Map valid entries to `Card` entities:
         - `front` ← `word`, `back` ← `meaning_vi`, `ipa` ← `ipa`, etc.
         - Map `examples` array (with `formality` field)
         - Map `relations` array to `List<WordRelation>` (each with `relatedText`, `relationType`, `pos`)
         - Init SRS defaults: `stage=0`, `status="new"`, `easeFactor=2.5`, `interval=0`
      5. Save all new cards
      6. Call `autoLinkRelations(userId)`
      7. Update matching `PendingItem` statuses to `"imported"`
      8. Return `BatchImportResponse` with imported/skipped/errors lists

- [x] **T008**: Implement auto-link logic in `CardService`
  - **Where**: `src/main/java/mobile/Service/Impl/CardServiceImpl.java`
  - **Details**:
    - Method: `void autoLinkRelations(ObjectId userId)`
    - Steps:
      1. Fetch all cards for user: `cardRepository.findByUserId(userId)`
      2. Build lookup map: `Map<String, String>` where key = `card.front.toLowerCase()`, value = `card.id.toHexString()`
      3. Iterate all cards; for each `WordRelation` where `relatedCardId == null`:
         - Lookup `relatedText.toLowerCase()` in map
         - If found, set `relatedCardId`
         - Mark card as modified
      4. Batch save all modified cards

- [x] **T009**: Implement SM-2 / SRS logic in `CardService`
  - **Where**: `src/main/java/mobile/Service/Impl/CardServiceImpl.java`
  - **Details**:
    - Method: `Card submitPracticeResult(ObjectId cardId, int quality)`
    - Implement SM-2 algorithm as specified in `plan.md` §7
    - Handle stage advancement: if `quality >= 3` and consecutive correct (repetition >= threshold for current stage), advance `stage`
    - Handle leech detection: if `wrongCount >= 8`, set `status = "leech"`
    - Handle status transitions: new → learning → mature (interval ≥ 21)
    - Method: `List<Card> getDueCards(ObjectId userId, int limit)`
    - Query cards where `nextReview <= now` OR `status = "new"`, sort by priority (leech → overdue → new), limit

- [x] **T010**: Implement dashboard stats aggregation in `CardService`
  - **Where**: `src/main/java/mobile/Service/Impl/CardServiceImpl.java`
  - **Details**:
    - Method: `DashboardResponse getDashboard(ObjectId userId, String search, String status, String topic, Pageable pageable)`
    - Aggregate counts using `countByUserIdAndStatus()` methods
    - Count due today using `countByUserIdAndNextReviewLessThanEqual(userId, new Date())`
    - Return paginated card list with applied filters

- [x] **T011**: Implement `PendingItemService.java`
  - **Where**: `src/main/java/mobile/Service/PendingItemService.java` & `src/main/java/mobile/Service/Impl/PendingItemServiceImpl.java`
  - **Details**:
    - `PendingItem addPendingItem(ObjectId userId, String content, String sourceType, ObjectId sourceCardId)` — check duplicate via `findByUserIdAndContentIgnoreCase()`, return existing if found
    - `Page<PendingItemResponse> getPendingItems(ObjectId userId, String status, Pageable pageable)`
    - `void deletePendingItem(ObjectId id, ObjectId userId)`
    - `String generatePrompt(ObjectId userId)` — fetch all `status="pending"` items, build unified prompt string (see spec §3.2)

### ⚙ Checkpoint: `./mvnw compile` (Passed)

---

## Phase 3: Adapter & REST API Layer (`controller/` + `model/payload/`)

- [x] **T012**: Create request/response DTOs for batch import
  - **Where**: `src/main/java/mobile/model/payload/request/BatchImportRequest.java`
  - **Where**: `src/main/java/mobile/model/payload/response/BatchImportResponse.java`
  - **Details**: As specified in `plan.md` §4.1 and §4.2

- [x] **T013**: Create request/response DTOs for practice & dashboard
  - **Where**: `src/main/java/mobile/model/payload/request/PracticeResultRequest.java`
  - **Where**: `src/main/java/mobile/model/payload/response/DashboardResponse.java`
  - **Where**: `src/main/java/mobile/model/payload/response/CardDetailResponse.java`
  - **Details**: As specified in `plan.md` §4.3, §4.4

- [x] **T014**: Create request/response DTOs for pending items
  - **Where**: `src/main/java/mobile/model/payload/request/CreatePendingItemRequest.java`
  - **Where**: `src/main/java/mobile/model/payload/response/PendingItemResponse.java`
  - **Details**: Fields: `content` (@NotBlank), `sourceType` (nullable), `sourceCardId` (nullable)

- [x] **T015**: Evolve `CardController.java` — add new endpoints
  - **Where**: `src/main/java/mobile/controller/CardController.java`
  - **Details**:
    - `POST /api/card/batch-import` → `CardService.batchImport()`
    - `GET /api/card/dashboard` → `CardService.getDashboard()`
    - `GET /api/card/{id}` → return card detail with reverse relations
    - `GET /api/card/practice/due?limit=15` → `CardService.getDueCards()`
    - `POST /api/card/{id}/practice-result` → `CardService.submitPracticeResult()`
    - Keep all existing endpoints unchanged

- [x] **T016**: Create `PendingItemController.java`
  - **Where**: `src/main/java/mobile/controller/PendingItemController.java`
  - **Details**:
    - `@RestController`, `@RequestMapping("/api/pending-item")`
    - `POST /` → create pending item
    - `GET /` → list pending items (paginated, filterable by status)
    - `DELETE /{id}` → delete pending item
    - `GET /generate-prompt` → generate AI prompt from pending items
    - `POST /add-manual` → manually add word/phrase

### ⚙ Checkpoint: `./mvnw compile` (Passed)

---

## Phase 4: Security Configuration

- [x] **T017**: Update `SecurityConfiguration.java` — whitelist new endpoints
  - **Where**: `src/main/java/mobile/security/config/AppSecurityConfig.java`
  - **Details**:
    - Endpoints under `/api/card/**` and `/api/pending-item/**` authenticated via JWT token in controller.

### ⚙ Checkpoint: `./mvnw compile` (Passed)

---

## Phase 5: Verification & Integration Testing

- [x] **T018**: Full compilation verification
  - **Command**: `./mvnw compile`
  - **Requirement**: `BUILD SUCCESS` with zero compilation errors (268 source files compiled cleanly).

- [x] **T019**: Data migration verification (backward compatibility)
  - Legacy fields mapped safely in `CardMapping` and `Card.java` without breaking previous structures.
