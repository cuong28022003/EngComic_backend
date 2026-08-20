# Architecture Plan: Word Journey — Backend Implementation

> **Perspective**: Developer / AI Agent  
> **Purpose**: Answer the question **"HOW & ARCHITECTURE"**  
> **Extends**: Feature 001 backend  

---

## 1. Schema Changes (Minimal)

Only 2 new fields needed on `CardEntity`. `seenExampleIds` and `stage` already exist.

```java
// CardEntity.java — ADD these 2 fields to SRS Progress section
protected int confidenceScore = 0;      // 1-5; Stage 6 self-rating
protected String lastExerciseType = null; // alternates exercise variants
```

No new MongoDB collections needed. All progress is co-located on the `card` document.

---

## 2. Package Structure

Following the existing clean architecture (`apis → businesses → databases`):

```
src/main/java/mobile/
├── apis/vocab/
│   ├── VocabController.java                  [EXISTING — add new endpoints]
│   └── dtos/
│       ├── ExerciseResponseDto.java           [NEW] — stage-specific exercise payload
│       ├── SubmitAnswerRequest.java           [NEW] — quality + confidence + answer text
│       ├── SubmitAnswerResponse.java          [NEW] — SRS update result
│       └── DailyChallenge.java               [NEW] — daily challenge payload
│
├── businesses/interactors/vocab/
│   ├── GetPracticeQueueInteractor.java        [NEW] — ordered due cards (daily cap, leech filter)
│   ├── GetExerciseInteractor.java             [NEW] — builds stage-specific exercise data
│   ├── SubmitAnswerInteractor.java            [NEW] — SM-2 update + stage advance logic
│   ├── GetLeechCardsInteractor.java           [NEW] — leech status query
│   ├── ClearLeechInteractor.java             [NEW] — clears leech, resets to stage 1
│   ├── GetDailyChallengeInteractor.java       [NEW] — 3-5 random mature cards
│   └── UpdateConfidenceInteractor.java        [NEW] — updates confidenceScore + SRS adjust
│
└── databases/repositories/vocab/
    └── CardRepository.java                    [MODIFY — add 4 new query methods]
```

---

## 3. CardRepository — New Query Methods

```java
// Add to CardRepository.java

// Practice queue: due today, not leech, ordered by nextReview ASC
List<CardEntity> findByUserIdAndStatusNotAndNextReviewLessThanEqualOrderByNextReviewAsc(
    String userId, String status, Date date);

// Mature cards for Daily Challenge (random sample done in interactor)
List<CardEntity> findByUserIdAndStatus(String userId, String status);

// Leech cards
List<CardEntity> findByUserIdAndStatus(String userId, String status);
// ^ reuses same signature — differentiate by status value ('leech' vs 'mature')

// Random distractors (different card, same user)
List<CardEntity> findByUserIdAndIdNot(String userId, String excludeId);
// → interactor picks random 3 from result
```

> Note: MongoDB doesn't support `ORDER BY RANDOM()`. For random mature cards, fetch all mature IDs,
> shuffle in Java, take first N. Acceptable because mature list is usually small (< 500 cards).

---

## 4. Exercise Generation Logic (GetExerciseInteractor)

```
Input: cardId, userId
Output: ExerciseResponseDto (stage-specific)

1. Load card → validate ownership
2. Switch on card.stage:
   Stage 1 → build MCQ: 
       - Load 3 random cards (not this card) as distractors via CardRepository
       - Shuffle options, mark isCorrect
       - Alternate exerciseType: 'flashcard' | 'multiple_choice_meaning' | 'audio_choice'
         based on card.lastExerciseType
   Stage 2 → build context exercise:
       - If relations has type='collocation' → collocation_fill
       - Else if examples has ≥2 different formality values → formality_choice
       - Else → synonym_compare (from relations type='synonym')
   Stage 3 → pronunciation:
       - Return audio URL + example sentence
       - If no audio field → return {skip: true, reason: "no_audio"}
   Stage 4 → assisted production:
       - Pick example not in seenExampleIds (or reset if all seen)
       - exerciseType alternates: 'fill_blank' | 'word_order' | 'short_answer'
       - For fill_blank: replace card.word in example.text with "_____"
       - For word_order: shuffle example.text.split(" ") 
   Stage 5 → free production:
       - Return self-check checklist items (static)
       - Return all example sentences for comparison
   Stage 6 → real-world:
       - Build situation string from template (see §5)
       - Return 2–3 related mature card words as relatedWords
3. Set card.lastExerciseType = chosen exerciseType
4. Save card
5. Return ExerciseResponseDto
```

---

## 5. Situational Prompt Templates (Stage 6)

Stored as static strings in `GetExerciseInteractor.java` (no DB needed):

```java
private static final List<String> SITUATION_TEMPLATES = List.of(
    "Bạn đang họp với đồng nghiệp và cần dùng từ '{word}' để diễn tả ý kiến của mình.",
    "Trong một email chuyên nghiệp, bạn muốn dùng '{word}' một cách tự nhiên.",
    "Bạn đang giải thích với người bạn người Việt về nghĩa của '{word}' bằng ví dụ.",
    "Hãy dùng '{word}' trong một câu mô tả điều gì đó bạn đã làm gần đây.",
    "Bạn đang tranh luận quan điểm và cần dùng '{word}' để thuyết phục đối phương."
);
```

For Conversation Chain, pick 2–3 mature card words from user's collection randomly and combine into one prompt.

---

## 6. SubmitAnswer Logic (SubmitAnswerInteractor)

```
Input: cardId, userId, quality (0-5 SM-2 scale), answerText (nullable), confidenceScore (nullable)

1. Load card
2. Apply SM-2:
   IF quality >= 3:
       easeFactor = max(1.3, easeFactor + 0.1 - (5-quality)*(0.08 + (5-quality)*0.02))
       repetition += 1
       interval = (repetition == 1) ? 1 : (repetition == 2) ? 6 : round(interval * easeFactor)
       interval = min(interval, 365)
   ELSE:
       repetition = 0
       interval = 1
       wrongCount += 1
       lapses += 1
3. Leech check: IF wrongCount >= 4 → status='leech', stage=1
4. Stage advance: IF quality >= 3 AND stagePassCount++ >= stagePassThreshold(stage):
       stage = min(stage + 1, 6)
       stagePassCount = 0
       IF stage >= 5 AND status != 'leech' → status = 'mature'  // or after stage 6 review
5. Confidence adjustment (stage 6 only):
   IF stage == 6 AND confidenceScore < 3:
       interval = max(1, round(interval * 0.7))
       IF status == 'mature' → status = 'learning'  // pull back
6. Update: nextReview = today + interval days
7. Update: lastReviewed = today, reviewCount += 1
8. IF confidenceScore != null: card.confidenceScore = confidenceScore
9. Save card
10. Return SubmitAnswerResponse {newStage, newStatus, nextReviewDate, message}
```

**Stage Pass Thresholds:**
```java
private static final Map<Integer, Integer> STAGE_PASS_THRESHOLD = Map.of(
    1, 3,  // 3 consecutive correct
    2, 3,  // 3/4 correct
    3, 1,  // 1 self-rated pass (stars >= 3)
    4, 2,  // 2/3 correct
    5, 1,  // 1 self-rated pass
    6, 1   // 1 confidence rating >= 3
);
```

---

## 7. Daily Challenge Logic (GetDailyChallengeInteractor)

```
1. Load all mature cards for user (status='mature')
2. If count == 0: return empty list (controller returns 204)
3. Shuffle list in Java (Collections.shuffle)
4. Take first 3–5 (random in range)
5. For each card: build Stage 6 situational prompt (random template)
6. Return DailyChallenge list
```

---

## 8. API Endpoint Mapping

```java
// VocabController.java — add these routes

@GetMapping("/practice/queue")
// → GetPracticeQueueInteractor.execute(userId)
// Returns: List<CardResponseDto> (max 30, ordered by nextReview ASC, no leech)

@GetMapping("/{id}/exercise")
// → GetExerciseInteractor.execute(userId, cardId)
// Returns: ExerciseResponseDto

@PostMapping("/{id}/submit")
// Body: SubmitAnswerRequest {quality, answerText?, confidenceScore?}
// → SubmitAnswerInteractor.execute(userId, cardId, request)
// Returns: SubmitAnswerResponse

@GetMapping("/leech")
// → GetLeechCardsInteractor.execute(userId)
// Returns: List<CardResponseDto>

@PostMapping("/{id}/clear-leech")
// Body: {memoryTip: String}
// → ClearLeechInteractor.execute(userId, cardId, memoryTip)
// Returns: CardResponseDto

@GetMapping("/daily-challenge")
// → GetDailyChallengeInteractor.execute(userId)
// Returns: List<DailyChallenge> or 204

@PostMapping("/{id}/confidence")
// Body: {confidenceScore: int}
// → UpdateConfidenceInteractor.execute(userId, cardId, score)
// Returns: CardResponseDto
```

---

## 9. DTOs

### ExerciseResponseDto
```java
public class ExerciseResponseDto {
    private int stage;
    private String exerciseType;  // "flashcard", "multiple_choice_meaning", "fill_blank", "word_order", etc.
    private CardBriefDto target;  // id, word, ipa, audio, meaning
    private String question;
    private String sentence;      // for fill_blank / word_order
    private String answer;        // for self-check
    private List<ExerciseOption> options;  // for MCQ
    private List<String> shuffledWords;    // for word_order
    private String situation;     // for Stage 6
    private List<String> relatedWords;     // for Conversation Chain
    private boolean skip;         // Stage 3 skip flag
    private String skipReason;    // "no_audio"
    private List<ExampleBriefDto> examples;  // for Stage 5 comparison
}
```

### SubmitAnswerRequest
```java
public class SubmitAnswerRequest {
    private int quality;          // 0–5 SM-2 scale (required)
    private String answerText;    // optional (Stage 5 free write)
    private Integer confidenceScore; // optional (Stage 6, 1–5)
}
```

### SubmitAnswerResponse
```java
public class SubmitAnswerResponse {
    private int newStage;
    private String newStatus;
    private Date nextReviewDate;
    private int intervalDays;
    private boolean stageAdvanced;
    private String message;
}
```

---

## 10. Verification Plan

- `./mvnw compile` after each task
- Integration test: register → login → import 10 cards → GET /practice/queue → GET /{id}/exercise → POST /{id}/submit → verify stage+1 in DB
- Test leech: submit wrong 4x → verify status='leech', stage=1
- Test Stage 6 gate: submit for stage-6 exercise when status != 'mature' → verify 403
