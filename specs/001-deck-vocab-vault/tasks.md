# Task Checklist: Combined Deck & Vocabulary Vault System

> **Instructions**: Execute each Task sequentially following the 4 Phases. Run `./mvnw compile` (or `mvn compile`) after completing each Task to ensure zero compilation errors.

---

## Phase 1: Database & Persistence Layer (`databases/`)
- [x] `T001` Implement MongoDB Entity `Card.java` in `mobile.databases.entities` (or `mobile.model.Entity`) with `userId`, `deckId` (optional), `isFavorite`, `masteryStatus`, `wordFamily`.
- [x] `T002` Implement MongoRepository `CardRepository.java` with `@Query` methods `findByUserId`, `findByUserIdAndSearch`, `findByDeckIdInOrDeckIdNull`.

---

## Phase 2: Business Logic & Use-Case Layer (`businesses/`)
- [x] `T003` Implement `CardServiceImpl.java` with safe `processAutoWordFamily` exception handling and optional `deckId` support.
- [x] `T004` Integrate local WordNet Microservice fallback for 100% accurate grammatical derivation.

---

## Phase 3: Adapter & REST API Layer (`apis/` & `controller/`)
- [x] `T005` Implement `CreateCardRequest.java` DTO with `@JsonAlias({"IPA", "ipa"})` and `@JsonIgnoreProperties(ignoreUnknown = true)`.
- [x] `T006` Implement `CardResponse.java` DTO with `@JsonProperty("isFavorite")` and `@JsonProperty("ipa")`.
- [x] `T007` Implement `CardController.java` REST Endpoints (`GET /user/{userId}`, `POST /batch`, `PUT /{id}/toggle-favorite`, `PUT /{id}/move-deck`).

---

## Phase 4: Verification & Frontend Modular Integration
- [x] `T008` Verify clean Java compilation using `mvn compile` (Pass 100% `BUILD SUCCESS`).
- [x] `T009` Refactor Frontend into modular `VocabCard` component and `BatchImportModal` with optional deck selection.
