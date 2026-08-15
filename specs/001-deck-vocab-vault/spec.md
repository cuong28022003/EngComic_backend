# Feature Specification: Combined Deck & Vocabulary Vault System

> **Perspective**: Business / Product Owner & Developer  
> **Purpose**: Detailed specification for **Unified Deck & Multidimensional Vocabulary Vault System**

---

## 1. Overview & Objective
- **Feature ID**: 001
- **Feature Name**: Combined Deck & Vocabulary Vault System
- **Priority**: P1
- **Objective**: Provide a comprehensive English vocabulary management solution for EngComic readers. Users can manage vocabulary per Deck or inspect the entire Vocabulary Vault, with automated IPA pronunciation/Audio/Example lookup and WordNet Word Family extraction.

---

## 2. Permissions & Preconditions
- **Target Audience**: `USER`
- **Preconditions**: User is authenticated (holds a valid JWT Access Token).

---

## 3. User Stories & Acceptance Criteria

### User Story 1: Unified Deck & Vocabulary Vault Management (Priority: P1)
> As a **User**, I want to **view all learned vocabulary on a single page or filter by specific Decks** so that I can easily review and search vocabulary items.

**Acceptance Criteria (BDD Format)**:
- **Scenario 1 (Successfully fetch vocabulary list)**:
  - **Given**: The user sends a valid JWT Token to `GET /api/card/user/{userId}`.
  - **When**: System receives request with filters `search`, `page`, `size`.
  - **Then**: System returns a paginated list of cards belonging to `userId` (including cards with `deckId` and standalone cards without `deckId`) with HTTP 200 OK.

### User Story 2: Batch Vocabulary Import (Priority: P1)
> As a **User**, I want to **paste a raw list of English words so the system automatically generates complete cards**, saving manual entry effort.

**Acceptance Criteria (BDD Format)**:
- **Scenario 1 (Successful batch import)**:
  - **Given**: The user submits word list `["resilient", "create", "beauty"]` to `POST /api/card/batch`.
  - **When**: System queries WordNet + Dictionary API for IPA, Audio, Examples, Word Family, and saves all new cards into MongoDB.
  - **Then**: Returns list of newly created cards with HTTP 200 OK.

### User Story 3: Favorite Marking & Deck Transfer (Priority: P2)
> As a **User**, I want to **mark cards as favorite (⭐) and transfer cards between decks** to optimize my learning path.

---

## 4. Business Invariants
- [x] **Rule 1**: The `deckId` field on `Card` is optional (Nullable). Cards without `deckId` remain owned by the creator's `userId`.
- [x] **Rule 2**: WordNet Word Family derivation failure must never interrupt or fail the main card persistence pipeline.
