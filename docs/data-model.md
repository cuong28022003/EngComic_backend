# Data Model & Business Invariants: EngComic_backend

## 1. Persistence & MongoDB Conventions

- **Database**: MongoDB
- **Entity Annotation**: `@Document(collection = "<collection_name>")`
- **Primary Key**: `@Id protected ObjectId id;` (BSON ObjectId)
- **Relationships**:
  - Relational links use `@DBRef` (e.g. `Set<Role> roles` in `User`).
  - Foreign identifier references use `ObjectId` fields (e.g. `comicId` in `Chapter`, `userId` in `Reading`).
  - Embedded documents / value objects are used for composite structures (e.g. hitbox, stats, frame data).
- **Audit & Timestamps**: Managed using `Date createdate`, `Date createdAt`, `Date updatedAt` and `@CreatedDate`.
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

### 2.2 Comic, Novel & Chapter Domain
- **`Comic`** (`collection = "comic"`):
  - Fields: `id`, `name`, `artist`, `description`, `imageUrl`, `backgroundUrl`, `url`, `views`, `genre`, `status`, `englishLevel`, `ageRating`, `uploaderId (ObjectId)`, `createdAt`, `updatedAt`.
- **`Chapter`** (`collection = "chapter"`):
  - Fields: `id`, `comicId (ObjectId)`, `chapterNumber`, `title`, `content` / `images`, `views`, `createdAt`, `updatedAt`.
- **`Comment`** (`collection = "comment"`):
  - User comments on comics/chapters with timestamps, likes, and parent-child reply structure.
- **`Rating`** (`collection = "rating"`):
  - Stores user rating (1-5 stars) and review text per comic.
- **`Reading`** (`collection = "reading"`):
  - Tracks reading history: `userId`, `comicId`, `chapterId`, `lastReadPage`, `updatedAt`.
- **`Saved`** (`collection = "saved"`):
  - User bookmarks / favorites: `userId`, `comicId`, `createdAt`.

### 2.3 Cards, Packs, Gacha & Game Mechanics
- **`Card`** (`collection = "card"`):
  - Card entities with rarity, character association, stats, abilities.
- **`Deck`** (`collection = "deck"`):
  - User-built decks referencing multiple `Card` IDs.
- **`Pack`** (`collection = "pack"`):
  - Gacha pack types, price, drop rates.
- **`Character`, `CharacterStats`, `CharacterSkill`, `CharacterAnimation`**:
  - Mini-game character attributes, sprite frames, and collision hitboxes.
- **`Rank`, `Season`, `Topup`**:
  - Seasonal leaderboards and payment/coin top-up transactions.

---

## 3. Business Invariants & Rules

1. **Authentication & Identity**:
   - `username` and `email` must be unique across all active users.
   - Passwords must always be hashed with BCrypt prior to persistence.
2. **Comic Lifecycle & Stats**:
   - Deleting or updating a comic must cascade or clean up associated chapters, ratings, and saved entries.
   - `views` on comics increment atomically when chapters are read.
   - Comic average rating is calculated from individual `Rating` entries via `RatingService`.
3. **Reading Progress Invariant**:
   - Only one `Reading` record exists per `(userId, comicId)` pair; subsequent reads update `chapterId` and `updatedAt`.
4. **Card & Gacha Invariants**:
   - Opening packs checks user wallet balance before granting cards.
   - Deck slot limitations must strictly follow card rarity and capacity constraints.
