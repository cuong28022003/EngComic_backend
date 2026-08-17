# Feature Specification: Unified Vocabulary Learning System

> **Perspective**: Business / Product Owner & Developer  
> **Purpose**: Answer the question **"WHAT & WHY"**  
> **Replaces**: Previous "Combined Deck & Vocabulary Vault" spec (v1)

---

## 1. Overview & Objective
- **Feature ID**: 001
- **Feature Name**: Unified Vocabulary Learning System
- **Priority**: P1
- **Objective**: Provide a complete English vocabulary acquisition pipeline — from AI-powered batch import through 5-stage spaced repetition practice — with intelligent word-relation linking and a pending-items collector for organic vocabulary growth. Both single words ("decision") and multi-word expressions ("reach a decision") are treated identically as first-class learning cards.

### 1.1 Key Design Decisions (Final)

| Decision | Choice | Rationale |
| :--- | :--- | :--- |
| Single words vs Multi-word expressions | **No distinction** — all are `Card` | One pipeline, zero branching logic, maximum simplicity |
| word_families + collocations + synonyms | Merged into single `WordRelation` embedded list | One query, one UI pattern, same auto-link logic |
| Static content vs Learning progress | Co-located on same `Card` document | MongoDB favors embedded docs; progress fields co-located for atomic updates |
| Pending items from "+" buttons | `PendingItem` collection | Feeds the Word Collector → batch import loop |

---

## 2. Permissions & Preconditions
- **Target Audience**: `USER`
- **Preconditions**: User is authenticated (holds a valid JWT Access Token).

---

## 3. Core Concepts

### 3.1 AI Metadata Contract (JSON Schema)

This is the "data contract" between the AI prompt output and the system. All DB fields and UI derive from this structure. **Every entry follows the same schema — no branching between "word" and "collocation".**

```json
{
  "word": "decision",
  "ipa": "/dɪˈsɪʒ.ən/",
  "part_of_speech": "noun",
  "meaning_vi": "sự quyết định",
  "definition_en": "a choice or judgment made after considering options",
  "usage_note": null,
  "topic": "business",
  "audio_url": null,
  "examples": [
    { "text": "The manager made a quick decision about the budget.", "formality": "formal" },
    { "text": "I can't decide, just help me out here.", "formality": "informal" },
    { "text": "Please review the decision outlined in the attached memo.", "formality": "written" }
  ],
  "relations": [
    { "text": "decide", "type": "family", "pos": "verb" },
    { "text": "decisive", "type": "family", "pos": "adjective" },
    { "text": "decisively", "type": "family", "pos": "adverb" },
    { "text": "make a decision", "type": "collocation" },
    { "text": "reach a decision", "type": "collocation" },
    { "text": "choice", "type": "synonym" },
    { "text": "judgment", "type": "synonym" }
  ]
}
```

Multi-word expressions like collocations use the exact same schema — nullable fields are simply left null:

```json
{
  "word": "reach a decision",
  "ipa": null,
  "part_of_speech": null,
  "meaning_vi": "đi đến quyết định",
  "definition_en": null,
  "usage_note": "More formal than 'make a decision'; implies prior discussion",
  "topic": "business",
  "audio_url": null,
  "examples": [
    { "text": "After hours of debate, the board finally reached a decision.", "formality": "formal" }
  ],
  "relations": []
}
```

### 3.2 AI Prompt Template (Unified — One for All)

```
Với mỗi mục trong danh sách sau: [decision, reach a decision, judgment, ...]
Trả về JSON array, mỗi phần tử đúng theo schema:
{ word, ipa, part_of_speech, meaning_vi, definition_en,
  usage_note, topic,
  examples: [{text, formality: "formal"|"informal"|"written"}] (3 câu),
  relations: [{text, type: "family"|"collocation"|"synonym", pos (chỉ cho family)}] }
Nếu mục là cụm từ (collocation), ipa và part_of_speech có thể null.
Chỉ trả JSON thuần, không markdown, không giải thích thêm.
```

### 3.3 5-Stage Practice Model

| Stage | Name | Exercise Types | Condition to Advance |
| :---: | :--- | :--- | :--- |
| 0 | New | First exposure — show full card info | Auto-advance after first view |
| 1 | Recognition | Multiple-choice: pick correct Vietnamese meaning | 2 correct in a row |
| 2 | Recall | Type the English word/phrase given Vietnamese meaning | 2 correct in a row |
| 3 | Pronunciation | Listen & record / read aloud | 1 correct |
| 4 | Production | Fill-in-the-blank using example sentences | 2 correct in a row |
| 5 | Mastery | Self-assessed writing/usage | Maintained via SRS |

### 3.4 SRS (Spaced Repetition System)

- **Algorithm**: SM-2 variant (ease factor, interval, repetition count)
- **Default values**: `easeFactor = 2.5`, `interval = 0`, `stage = 0`, `status = "new"`
- **Leech threshold**: Card with `wrongCount >= 8` is flagged as `status = "leech"`
- **Status values**: `new` → `learning` → `mature` (interval ≥ 21 days) | `leech`

### 3.5 Auto-Link Mechanism

After every batch import, run an auto-link pass:
1. Scan all `WordRelation` entries across all cards where `relatedCardId` is null
2. If `relatedText` matches an existing card's `word` field (case-insensitive) → set `relatedCardId`
3. This resolves both forward references (new cards linking to old) and backward references (old cards linking to newly imported words)

---

## 4. User Stories & Acceptance Criteria

### User Story 1: AI-Powered Batch Import (Priority: P1)
> As a **User**, I want to **paste AI-generated JSON into the system** so that vocabulary cards are automatically created with full metadata (IPA, examples, word family, collocations, synonyms).

**Acceptance Criteria (BDD Format)**:
- **Scenario 1 (Successful batch import)**:
  - **Given**: The user submits valid JSON array to `POST /api/card/batch-import`.
  - **When**: System parses, validates, and persists each entry as a `Card` document with embedded `examples` and `relations`.
  - **Then**: Returns list of created cards + count; auto-creates SRS progress fields (stage=0, status=new); runs auto-link on all relations. HTTP 200 OK.
- **Scenario 2 (Partial validation failure)**:
  - **Given**: JSON array contains entries missing required fields (e.g., no `meaning_vi`).
  - **When**: System validates each entry individually.
  - **Then**: Valid entries are imported; invalid entries are returned with field-level error details. HTTP 200 OK with `imported` + `errors` arrays.
- **Scenario 3 (Duplicate detection)**:
  - **Given**: A word already exists in the user's card collection (same `word` + `userId`).
  - **When**: Batch import encounters the duplicate.
  - **Then**: System skips the duplicate and reports it in the `skipped` array.

### User Story 2: Word List Dashboard (Priority: P1)
> As a **User**, I want to **view all my vocabulary on a dashboard with filters and stats** so that I can track my learning progress at a glance.

**Acceptance Criteria (BDD Format)**:
- **Scenario 1 (Dashboard with stats)**:
  - **Given**: User requests `GET /api/card/dashboard`.
  - **When**: System aggregates card data.
  - **Then**: Returns summary stats (total cards, due today, mature count, learning count, leech count) + paginated card list with search/filter support. HTTP 200 OK.
- **Scenario 2 (Filter by status/topic/deck)**:
  - **Given**: User provides query params `status=leech&topic=business`.
  - **When**: System filters cards.
  - **Then**: Returns only matching cards, paginated.

### User Story 3: Word Detail View (Priority: P1)
> As a **User**, I want to **view the full detail of a word** including word family, collocations, synonyms, examples, and see which related words I've already learned.

**Acceptance Criteria (BDD Format)**:
- **Scenario 1 (View detail with linked relations)**:
  - **Given**: User requests `GET /api/card/{id}`.
  - **When**: System loads the card with its relations.
  - **Then**: Each relation includes `relatedCardId` (if linked) so UI can show "Đã học ✓" or "+ Thêm". HTTP 200 OK.
- **Scenario 2 (Reverse relations — "this word appears in family of...")**:
  - **Given**: Card "decide" is viewed, and "decision" has "decide" as a family relation.
  - **When**: System queries reverse relations.
  - **Then**: Response includes `reverseRelations` showing "decision" links back to this card.

### User Story 4: Pending Items Collector (Priority: P1)
> As a **User**, I want to **click "+" on any unlearned relation** (word family member, collocation, synonym) so that it's added to my pending import queue for the next batch.

**Acceptance Criteria (BDD Format)**:
- **Scenario 1 (Add pending item from relation)**:
  - **Given**: User clicks "+" on "decisive" (from "decision"'s word family).
  - **When**: System creates a `PendingItem` with `content="decisive"`, `sourceType="family"`, `sourceCardId=<decision's id>`.
  - **Then**: Returns the created pending item. HTTP 201 CREATED.
- **Scenario 2 (Add collocation as pending item)**:
  - **Given**: User clicks "+" on "reach a decision".
  - **When**: System creates `PendingItem` with `content="reach a decision"`, `sourceType="collocation"`.
  - **Then**: Returns created item. HTTP 201 CREATED.
- **Scenario 3 (Prevent duplicate pending)**:
  - **Given**: "decisive" is already in pending queue.
  - **When**: User clicks "+" again.
  - **Then**: System returns existing item without creating duplicate. HTTP 200 OK.

### User Story 5: Practice Session (Priority: P2)
> As a **User**, I want to **practice vocabulary through stage-appropriate exercises** using SRS scheduling so that I retain words long-term.

**Acceptance Criteria (BDD Format)**:
- **Scenario 1 (Get due cards for practice)**:
  - **Given**: User requests `GET /api/card/practice/due?limit=15`.
  - **When**: System queries cards where `nextReview <= now` or `status = "new"`.
  - **Then**: Returns up to 15 cards sorted by priority (leech first, then overdue, then new). HTTP 200 OK.
- **Scenario 2 (Submit practice result)**:
  - **Given**: User submits `POST /api/card/{id}/practice-result` with `{ quality: 0-5 }`.
  - **When**: System applies SM-2 algorithm.
  - **Then**: Updates `stage`, `easeFactor`, `interval`, `nextReview`, `wrongCount`, `status`. Advances or demotes stage based on quality. HTTP 200 OK.

### User Story 6: Deck Management (Priority: P2)
> As a **User**, I want to **organize cards into decks and transfer cards between decks** to structure my learning path.

**Acceptance Criteria**: Same as previous spec v1 (deck CRUD, card-deck association, move between decks).

---

## 5. Business Invariants

- [x] **Rule 1**: `Card.deckId` is optional (nullable). Cards without `deckId` are standalone, owned by `userId`.
- [ ] **Rule 2**: Unique constraint on `(userId, front)` — a user cannot have duplicate cards for the same word/phrase.
- [ ] **Rule 3**: Auto-link runs after every batch import to resolve `relatedCardId` across all user's cards.
- [ ] **Rule 4**: `PendingItem` is unique per `(userId, content)` — no duplicate pending entries.
- [ ] **Rule 5**: Stage advancement requires consecutive correct answers (count varies by stage, see §3.3).
- [ ] **Rule 6**: A card with `wrongCount >= 8` is automatically flagged as `status = "leech"`.
- [ ] **Rule 7**: `seenExampleIds` tracks which examples have been used in fill-blank exercises to avoid repetition until all are exhausted.

---

## 6. End-to-End Data Flow

```
User types words/phrases into Word Collector (or clicks "+" on relations)
    ↓
Pending items accumulate in PendingItem collection
    ↓
User clicks "Generate Prompt" → system builds unified prompt
    ↓
User copies prompt → pastes into ChatGPT/Claude → copies JSON result
    ↓
User pastes JSON into Import UI → Parse → Validate → Preview/Edit
    ↓
POST /api/card/batch-import
    ↓
Insert Cards (with embedded examples + relations) + Init SRS fields (stage=0, status='new')
    ↓
Auto-link: scan all relations, match relatedText to existing cards → set relatedCardId
    ↓
Update PendingItem status → 'imported' for matched items
    ↓
Cards appear in Dashboard → scheduled for review based on nextReview date
    ↓
Practice Session: generate exercises from static data + update SRS progress after each answer
```

---

## 7. UI Screens Overview

### A. Word List / Dashboard
- Search box (fuzzy search on `front`, `back`)
- Filters: Topic, Status (new/learning/mature/leech), Deck
- Stats bar: Due today, Total, Mature, Learning, Leech counts
- Card list items: word, IPA, POS, meaning, stage indicator (●●●○○), next review date

### B. Word Detail
- Full card metadata: word, IPA, audio, POS, meaning, definition, usage note
- Relations section (grouped by type: Family / Collocation / Synonym)
  - Each relation shows "Đã học ✓" (clickable → navigate to card) or "+ Thêm" (→ add to PendingItem)
- Reverse relations: "This word appears in the word family of: ..."
- Examples with formality tags
- "Bắt đầu luyện tập →" button

### C. Practice Session
- Progress bar (X/15 today, percentage)
- Timer
- Stage-appropriate exercise (see §3.3)
- Result feedback + SRS update

### D. Import UI
- Step 1: Copy prompt template (auto-filled with pending items)
- Step 2: Paste JSON result into textarea
- Parse & Preview: editable table with validation warnings
- Import button with count

### E. Word Collector
- List of all pending items (from "+" buttons + manual entry)
- Source attribution ("← từ word family của decision")
- Manual add input
- "Generate Prompt" button → builds and copies prompt to clipboard
