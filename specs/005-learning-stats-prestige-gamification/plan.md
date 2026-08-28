# Technical Implementation Plan: Feature 005 - Learning Stats & Prestige Gamification

> **Perspective**: Tech Lead / Software Engineer  
> **Purpose**: Answer the question **"HOW"**

---

## 1. Architecture & Design Decisions

### 1.1. Clean Architecture Compliance (Backend)
- **Controller Layer**:
  - `LearningStatsController.java` (`@RequestMapping("/api/learning-stats")`): Validate `@CurrentUserId`, định tuyến HTTP. CẤM viết logic hay query DB.
- **Boundary Layer**:
  - `GetLearningStats.java`: Lấy thông tin thống kê học tập, Cấp bậc, Streak, Danh hiệu đã trang bị.
  - `RecordLearningActivity.java`: Ghi nhận hoạt động học (từ vựng, TOEIC, điểm danh), tính Streak, thăng hạng tự động, thưởng Kim Cương.
  - `ClaimAchievement.java`: Nhận thưởng thành tích và danh hiệu.
  - `EquipPrestigeItem.java`: Trang bị Danh hiệu hoặc Khung Avatar.
- **Interactor Layer**:
  - `GetLearningStatsInteractor.java`
  - `RecordLearningActivityInteractor.java`
  - `ClaimAchievementInteractor.java`
  - `EquipPrestigeItemInteractor.java`
- **Database Layer**:
  - Entity: `UserLearningStatsEntity.java` (`@Document(collection = "user_learning_stats")`).
  - Repository: `UserLearningStatsRepository.java`.

---

## 2. Database Schema (`user_learning_stats`)

```json
{
  "_id": "ObjectId",
  "userId": "64b7f8...",
  "xp": 1850,
  "diamond": 120,
  "rank": {
    "name": "VÀNG (Gold)",
    "tier": 3,
    "minXp": 1500,
    "maxXp": 3500,
    "badgeUrl": "assets/badges/gold.png"
  },
  "currentStreak": 12,
  "longestStreak": 25,
  "lastStudyDate": "2026-08-28",
  "totalWordsMastered": 45,
  "totalSessionsCompleted": 18,
  "equippedTitle": "⚡ Bậc Thầy Trí Nhớ",
  "equippedAvatarFrame": "frame_gold_particles",
  "unlockedTitles": [
    "Khởi Đầu Rực Rỡ",
    "Ý Chí Thép",
    "⚡ Bậc Thầy Trí Nhớ"
  ],
  "unlockedAvatarFrames": [
    "frame_default",
    "frame_gold_particles"
  ],
  "claimedAchievementIds": [
    "ach_streak_3",
    "ach_streak_7",
    "ach_xp_500"
  ],
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```

---

## 3. API Contracts (`docs/api-contracts.md` integration)

### 3.1. `GET /api/learning-stats/me` & `GET /api/learning-stats/{userId}`
- **Security**: `@PreAuthorize("isAuthenticated()")`
- **Response**: `200 OK`
```json
{
  "id": "64b7f8...",
  "userId": "user123",
  "xp": 1850,
  "diamond": 120,
  "rank": {
    "name": "VÀNG (Gold)",
    "tier": 3,
    "minXp": 1500,
    "maxXp": 3500,
    "icon": "fa-solid fa-crown",
    "color": "#eab308"
  },
  "currentStreak": 12,
  "longestStreak": 25,
  "lastStudyDate": "2026-08-28",
  "studiedToday": true,
  "totalWordsMastered": 45,
  "equippedTitle": "⚡ Bậc Thầy Trí Nhớ",
  "equippedAvatarFrame": "frame_gold_particles",
  "unlockedTitles": ["Khởi Đầu Rực Rỡ", "⚡ Bậc Thầy Trí Nhớ"],
  "unlockedAvatarFrames": ["frame_default", "frame_gold_particles"]
}
```

### 3.2. `POST /api/learning-stats/record-activity`
- **Security**: `@PreAuthorize("isAuthenticated()")`
- **Request Body**:
```json
{
  "xpEarned": 15,
  "activityType": "practice", // "practice" | "reading" | "test" | "checkin"
  "wordsMasteredIncrement": 1
}
```
- **Response**: `200 OK`
```json
{
  "stats": { ... },
  "streakIncreased": true,
  "rankPromoted": false,
  "message": "Tuyệt vời! Đã hoàn thành phiên luyện tập (+15 XP, Chuỗi 12 ngày)!"
}
```

### 3.3. `POST /api/learning-stats/equip-item`
- **Request Body**:
```json
{
  "itemType": "title", // "title" | "avatar_frame"
  "itemId": "⚡ Bậc Thầy Trí Nhớ"
}
```

---

## 4. Frontend Architecture (`EngComic_angular`)

1. **`UserStatsApiService`**:
   - `BASE = '/learning-stats'`.
   - `getMyStats()`, `recordActivity()`, `checkIn()`, `equipItem()`.
2. **`StreakModalComponent` (`src/app/shared/components/streak-modal/`)**:
   - Hiển thị ngọn lửa Hero, lịch tuần 7 ngày, mốc phần thưởng chuỗi.
3. **`RankComponent` (`src/app/features/account/rank/`)**:
   - Tab 1: 7 Cấp Bậc Danh Vọng (Tier roadmap).
   - Tab 2: Nhiệm vụ & Thành Tích (nhận thưởng XP & Kim Cương).
   - Tab 3: Mùa giải & Danh hiệu độc quyền (Prestige Showcase).
4. **Header Profile Display**:
   - Hiển thị Danh hiệu (`equippedTitle`) và Khung Avatar (`equippedAvatarFrame`) cạnh Username.
