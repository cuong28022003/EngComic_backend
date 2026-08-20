# Task Breakdown: Word Journey — Backend Implementation

> **Perspective**: Developer / AI Agent  
> **Purpose**: Answer the question **"EXECUTION & CHECKLIST"**  
> **Instructions for AI**: Execute tasks sequentially. Run `./mvnw compile` after completing each Phase to verify syntax before checking `[x]` and moving to the next task.

---

## Phase 1: Schema Evolution

- [ ] **T001**: Add 2 new fields to `CardEntity.java`
  - **Where**: `src/main/java/mobile/databases/entities/vocab/CardEntity.java`
  - **Details**:
    - Add to `// --- SRS Progress ---` section:
      ```java
      protected int confidenceScore = 0;
      protected String lastExerciseType = null;
      ```
    - `stage` field already exists (range just extends to 6 now — no code change needed)
    - Do NOT add any migration logic; existing stage=5 cards are treated as stage=5

### ⚙ Checkpoint: `./mvnw compile`

---

## Phase 2: Repository Layer

- [ ] **T002**: Add new query methods to `CardRepository.java`
  - **Where**: `src/main/java/mobile/databases/repositories/vocab/CardRepository.java`
  - **Details** — add these methods:
    ```java
    // Due cards excluding leech, ordered by due date
    List<CardEntity> findByUserIdAndStatusNotAndNextReviewLessThanEqualOrderByNextReviewAsc(
        String userId, String excludeStatus, Date date);

    // All cards by status (used for mature/leech queries)
    List<CardEntity> findByUserIdAndStatus(String userId, String status);

    // All cards excluding one specific card (for distractors)
    List<CardEntity> findByUserIdAndIdNot(String userId, String excludeId);
    ```
  - NOTE: `findByUserIdAndStatus` may already exist — check first, only add if missing

### ⚙ Checkpoint: `./mvnw compile`

---

## Phase 3: DTOs

- [ ] **T003**: Create `ExerciseResponseDto.java`
  - **Where**: `src/main/java/mobile/apis/vocab/dtos/ExerciseResponseDto.java`
  - **Details**:
    ```java
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public class ExerciseResponseDto {
        private int stage;
        private String exerciseType;
        private CardBriefDto target;        // id, word, ipa, audio, meaning
        private String question;
        private String sentence;
        private String answer;
        private List<ExerciseOptionDto> options;
        private List<String> shuffledWords;
        private String situation;
        private List<String> relatedWords;
        private boolean skip;
        private String skipReason;
        private List<ExampleSentenceDto> examples;
    }
    ```
  - Also create inner DTOs if not existing: `ExerciseOptionDto` (id, text, isCorrect), `CardBriefDto` (id, word, ipa, audio, meaning), `ExampleSentenceDto` (id, text, translation, formality)

- [ ] **T004**: Create `SubmitAnswerRequest.java`
  - **Where**: `src/main/java/mobile/apis/vocab/dtos/SubmitAnswerRequest.java`
  - **Details**:
    ```java
    @Getter @Setter @NoArgsConstructor
    public class SubmitAnswerRequest {
        private int quality;           // 0–5 required
        private String answerText;     // optional
        private Integer confidenceScore; // optional, 1–5
    }
    ```

- [ ] **T005**: Create `SubmitAnswerResponse.java`
  - **Where**: `src/main/java/mobile/apis/vocab/dtos/SubmitAnswerResponse.java`
  - **Details**:
    ```java
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public class SubmitAnswerResponse {
        private int newStage;
        private String newStatus;
        private Date nextReviewDate;
        private int intervalDays;
        private boolean stageAdvanced;
        private String message;
    }
    ```

- [ ] **T006**: Create `DailyChallengeDto.java`
  - **Where**: `src/main/java/mobile/apis/vocab/dtos/DailyChallengeDto.java`
  - **Details**:
    ```java
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public class DailyChallengeDto {
        private String cardId;
        private String word;
        private String meaning;
        private String situation;
        private List<String> relatedWords;
    }
    ```

### ⚙ Checkpoint: `./mvnw compile`

---

## Phase 4: Business Logic — Interactors

- [ ] **T007**: Implement `GetPracticeQueueInteractor.java`
  - **Where**: `src/main/java/mobile/businesses/interactors/vocab/GetPracticeQueueInteractor.java`
  - **Logic**:
    1. Call `cardRepository.findByUserIdAndStatusNotAndNextReviewLessThanEqualOrderByNextReviewAsc(userId, "leech", new Date())`
    2. Limit to first 30 results
    3. Map to `CardResponseDto` list via `CardMapper`
    4. Return list

- [ ] **T008**: Implement `GetExerciseInteractor.java`
  - **Where**: `src/main/java/mobile/businesses/interactors/vocab/GetExerciseInteractor.java`
  - **Logic** (see plan.md §4):
    - Load card, verify userId matches
    - Switch on `card.getStage()`:
      - Stage 1: load 3 distractors via `cardRepository.findByUserIdAndIdNot()`, shuffle → MCQ
      - Stage 2: check relations/examples for collocation_fill or formality_choice
      - Stage 3: return audio URL + one example; if no audio → set `skip=true`
      - Stage 4: pick unused example (not in seenExampleIds); choose exercise type based on lastExerciseType
      - Stage 5: return all examples + self-check items
      - Stage 6: gate check (status must be 'mature', else throw 403); random template + related mature words
    - Update `card.lastExerciseType`, save
    - Return `ExerciseResponseDto`
  - **Static templates**: define `SITUATION_TEMPLATES` as a `private static final List<String>` (5 templates as in plan.md §5)

- [ ] **T009**: Implement `SubmitAnswerInteractor.java`
  - **Where**: `src/main/java/mobile/businesses/interactors/vocab/SubmitAnswerInteractor.java`
  - **Logic** (see plan.md §6):
    - SM-2 update
    - Leech detection (wrongCount >= 4)
    - Stage advance (use `STAGE_PASS_THRESHOLD` map)
    - Confidence adjustment for Stage 6
    - Update `nextReview`, `lastReviewed`, `reviewCount`, `seenExampleIds` if applicable
    - Save card
    - Return `SubmitAnswerResponse`
  - **STAGE_PASS_THRESHOLD**: `{1:3, 2:3, 3:1, 4:2, 5:1, 6:1}` as static map
  - **Status promotion**: when stage advances from 4 to 5 → set `status='learning'`; stage 5 pass → `status='mature'`

- [ ] **T010**: Implement `GetLeechCardsInteractor.java`
  - **Where**: `src/main/java/mobile/businesses/interactors/vocab/GetLeechCardsInteractor.java`
  - **Logic**:
    1. `cardRepository.findByUserIdAndStatus(userId, "leech")`
    2. Map to `CardResponseDto` list
    3. Return

- [ ] **T011**: Implement `ClearLeechInteractor.java`
  - **Where**: `src/main/java/mobile/businesses/interactors/vocab/ClearLeechInteractor.java`
  - **Logic**:
    1. Load card, verify userId
    2. Set `status = "learning"`, `stage = 1`, `wrongCount = 0`, `lapses = 0`
    3. If `memoryTip` not blank: append to `card.personalNote`
    4. Set `nextReview = tomorrow`
    5. Save and return `CardResponseDto`

- [ ] **T012**: Implement `GetDailyChallengeInteractor.java`
  - **Where**: `src/main/java/mobile/businesses/interactors/vocab/GetDailyChallengeInteractor.java`
  - **Logic**:
    1. `cardRepository.findByUserIdAndStatus(userId, "mature")`
    2. If empty → return empty list
    3. `Collections.shuffle(cards)` with `new Random()`
    4. Take first `Math.min(5, cards.size())` — but random count 3–5
    5. For each: pick random situation template, replace `{word}` with `card.getWord()`
    6. relatedWords: pick 2 other words from shuffled mature list
    7. Map to `DailyChallengeDto` and return

- [ ] **T013**: Implement `UpdateConfidenceInteractor.java`
  - **Where**: `src/main/java/mobile/businesses/interactors/vocab/UpdateConfidenceInteractor.java`
  - **Logic**:
    1. Load card, verify userId
    2. Validate `confidenceScore` is 1–5
    3. `card.setConfidenceScore(score)`
    4. If score < 3: `interval = max(1, (int)(interval * 0.7))`; if status='mature' → status='learning'
    5. Recalculate `nextReview = today + interval days`
    6. Save and return `CardResponseDto`

### ⚙ Checkpoint: `./mvnw compile`

---

## Phase 5: Controller Layer

- [ ] **T014**: Add new endpoints to `VocabController.java`
  - **Where**: `src/main/java/mobile/apis/vocab/VocabController.java`
  - **Endpoints to add** (base path `/api/card`):
    ```java
    @GetMapping("/practice/queue")
    // inject GetPracticeQueueInteractor

    @GetMapping("/{id}/exercise")
    // inject GetExerciseInteractor

    @PostMapping("/{id}/submit")
    // @RequestBody SubmitAnswerRequest
    // inject SubmitAnswerInteractor

    @GetMapping("/leech")
    // inject GetLeechCardsInteractor

    @PostMapping("/{id}/clear-leech")
    // @RequestBody Map<String, String> body (key: "memoryTip")
    // inject ClearLeechInteractor

    @GetMapping("/daily-challenge")
    // inject GetDailyChallengeInteractor
    // Return ResponseEntity: 200 with list, or 204 if empty

    @PostMapping("/{id}/confidence")
    // @RequestBody Map<String, Integer> body (key: "confidenceScore")
    // inject UpdateConfidenceInteractor
    ```
  - Extract userId from JWT in each handler (same pattern as existing endpoints)

### ⚙ Checkpoint: `./mvnw compile`

---

## Phase 6: Integration Verification

- [ ] **T015**: Manual integration test (using curl or Postman)
  - Register + login (existing flow)
  - Import 5 cards (existing flow)
  - `GET /api/card/practice/queue` → verify 5 cards returned (all have `nextReview = null` or past)
  - `GET /api/card/{id}/exercise` for a stage=0 card → verify returns stage 1 MCQ
    - NOTE: Stage 0 means not started; exercise for stage 0 should default to stage 1 exercise
  - `POST /api/card/{id}/submit` with quality=5 → verify stage advances, nextReview set
  - Submit wrong 4 times → verify status='leech'
  - `GET /api/card/leech` → verify leech card in list
  - `POST /api/card/{id}/clear-leech` → verify status='learning', stage=1
  - Import enough cards, manually set one to status='mature' in DB → `GET /api/card/daily-challenge` → verify payload

- [ ] **T016**: Update `BACKEND_CONTRACT.md` in `EngComic_angular`
  - Add all new endpoints with exact request/response shapes
  - Add note: "Stage 6 returns 403 if status != mature"
  - Add note: "GET /daily-challenge returns 204 if no mature cards"
