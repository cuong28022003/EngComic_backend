# Architecture & Clean Architecture Package Structure: EngComic_backend

## 1. Overview & Architectural Pattern

`EngComic_backend` áp dụng mô hình **Clean Architecture kết hợp Package-by-Feature**. Mô hình này đảm bảo phân tách rõ ràng trách nhiệm giữa các tầng (Layered Separation of Concerns), ngăn chặn việc rò rỉ logic nghiệp vụ, và chuẩn hóa quy trình phát triển cho cả lập trình viên và AI Agents.

> **Lộ trình áp dụng (Migration Strategy)**:
> - **Module tiên phong (Pilot)**: Module `001-deck-vocab-vault` (`card`, `deck`, `pending-item`) được tái cấu trúc và áp dụng **100% theo Package Structure mới**.
> - **Các module hiện tại (Legacy)**: Tạm thời giữ nguyên cấu trúc cũ (`controller/`, `Service/`, `model/Entity/`, `repository/`) để duy trì độ ổn định hệ thống và sẽ được migrate dần theo từng phase.

```mermaid
graph TD
    subgraph Layer1[LAYER 1: ADAPTER / PRESENTATION]
        API[mobile.apis.{feature}]
        Controller["{Feature}Controller.java"]
        DTOs["dtos (Request / Response DTOs)"]
        CommonAPI["mobile.apis.common (PageResponse.java)"]
    end

    subgraph Layer2[LAYER 2: APPLICATION / USE CASES]
        Boundaries["mobile.businesses.boundaries.{feature} (Use-Case Contracts)"]
        Interactors["mobile.businesses.interactors.{feature} (Use-Case Logic)"]
        AppServices["mobile.businesses.services.{feature} (Feature Services)"]
        Mappers["{Feature}Mapper.java"]
    end

    subgraph LayerDomain[LAYER DOMAIN: PURE BUSINESS DOMAIN]
        Rules["mobile.domains.{feature}.{Feature}Rules"]
        Validators["mobile.domains.{feature}.{Feature}Validator"]
        StateMachines["mobile.domains.{feature}.{Feature}StateMachine"]
        DomainEnums["mobile.domains.common.enums"]
    end

    subgraph Layer3[LAYER 3: INFRASTRUCTURE / PERSISTENCE]
        Entities["mobile.databases.entities.{feature} ({Feature}Entity)"]
        Repositories["mobile.databases.repositories.{feature} ({Feature}Repository)"]
        DbServices["mobile.databases.services.{feature} ({Feature}DatabaseService)"]
        Migrations["mobile.databases.migrations"]
    end

    subgraph LayerExt[LAYER EXTERNAL: INTEGRATIONS & SAGA & SEARCH]
        Integrations["mobile.integrations.{client} (UserClient, PaymentClient...)"]
        Saga["mobile.saga (handlers, publisher)"]
        SearchCriteria["mobile.searchcriteria.{feature} ({Feature}SearchCriteria)"]
    end

    subgraph LayerCommon[CROSS-CUTTING / CONFIG]
        Security["mobile.config.SecurityConfiguration & JWT"]
        Exceptions["mobile.config.ErrorHandlingAdvice & CustomException"]
    end

    Client[Mobile / Web Client] --> Controller
    Controller --> Boundaries
    Boundaries --> Interactors
    Interactors --> AppServices
    Interactors --> Rules
    Interactors --> Validators
    Interactors --> Repositories
    Interactors --> DbServices
    Interactors --> SearchCriteria
    Interactors --> Integrations
    Repositories --> MongoDB[(MongoDB Database)]
    Interactors --> Mappers
```

---

## 2. 3 Immutable Architectural Principles

1. **Strict One-Way Dependency Flow**:
   $$\text{apis} \longrightarrow \text{businesses} \longrightarrow \text{domains} \longrightarrow \text{databases}$$
   - Lớp `databases` (Entities, Repositories, DatabaseServices) và `domains` tuyệt đối **KHÔNG ĐƯỢC PHÉP** gọi ngược về `businesses` hoặc `apis`.
   - Lớp `apis` chỉ phụ thuộc vào `businesses.boundaries` (thông qua interface contract) và DTOs.
2. **Boundary Interface Pattern**:
   - Mỗi hành động nghiệp vụ (Create, Update, Get, Search, BatchImport, SubmitPracticeResult...) là một **Boundary Interface** riêng biệt trong `businesses.boundaries.{feature}` (ví dụ: `Create{Feature}.java`, `Search{Feature}.java`).
   - Request và Response Data Models của Use-Case được định nghĩa trực tiếp bên trong hoặc cùng package với Boundary Interface.
3. **Thin Controller (Không chứa Business Logic)**:
   - Controller chỉ làm nhiệm vụ tiếp nhận HTTP request, validate `@Valid`, trích xuất thông tin JWT/Headers, gọi Boundary Interactor, và đóng gói `ResponseEntity` / `PageResponse`.
   - Controller tuyệt đối không chứa logic tính toán nghiệp vụ, query DB trực tiếp hoặc thuật toán miền (Domain Algorithm).

---

## 3. Detailed Target Package Structure (`src/main/java/mobile/`)

```text
src/main/java/mobile/
│
├── apis/                                      # [ADAPTER / PRESENTATION]
│   ├── {feature_name}/                        # e.g., card, deck, pendingitem
│   │   ├── {Feature}Controller.java           # REST Endpoints
│   │   └── dtos/                              # Client-facing Request/Response DTOs
│   │       ├── Create{Feature}Request.java
│   │       ├── Update{Feature}Request.java
│   │       └── {Feature}ResponseDto.java
│   │
│   └── common/
│       └── PageResponse.java                  # Generic pagination wrapper DTO
│
├── businesses/                                # [APPLICATION / USE CASE]
│   │
│   ├── boundaries/                            # Use-case contracts (Interfaces)
│   │   └── {feature_name}/
│   │       ├── Create{Feature}.java
│   │       ├── Update{Feature}.java
│   │       ├── Get{Feature}.java
│   │       └── Search{Feature}.java
│   │
│   ├── interactors/                           # Use-case implementations & Mappers
│   │   └── {feature_name}/
│   │       ├── Create{Feature}Interactor.java
│   │       ├── Update{Feature}Interactor.java
│   │       ├── Get{Feature}Interactor.java
│   │       ├── Search{Feature}Interactor.java
│   │       └── {Feature}Mapper.java           # Entity <-> DTO transformation
│   │
│   └── services/                              # Application/domain-facing services
│       └── {feature_name}/
│           └── {Feature}Service.java
│
├── domains/                                   # [DOMAIN / PURE BUSINESS LOGIC]
│   │
│   ├── {feature_name}/
│   │   ├── {Feature}Rules.java                # Business invariants & calculations (e.g., SM-2 SRS Algorithm)
│   │   ├── {Feature}Validator.java            # Domain validation rules
│   │   └── {Feature}StateMachine.java         # Lifecycle / State transitions (new -> learning -> mature / leech)
│   │
│   └── common/
│       └── enums/                             # Shared domain enumerations
│
├── databases/                                 # [INFRASTRUCTURE / PERSISTENCE]
│   │
│   ├── entities/                              # MongoDB Documents
│   │   └── {feature_name}/
│   │       └── {Feature}Entity.java           # e.g., CardEntity.java, PendingItemEntity.java
│   │
│   ├── repositories/                          # Spring Data Mongo Repositories
│   │   └── {feature_name}/
│   │       └── {Feature}Repository.java       # MongoRepository<Entity, ObjectId>
│   │
│   ├── services/                              # Database helper services
│   │   └── {feature_name}/
│   │       └── {Feature}DatabaseService.java
│   │
│   └── migrations/                            # Database seeders and migration helpers
│       └── ...
│
├── integrations/                              # [EXTERNAL SYSTEMS / CLIENT ADAPTERS]
│   ├── user/
│   │   ├── UserClient.java
│   │   └── UserClientAdapter.java
│   ├── payment/
│   │   ├── PaymentClient.java
│   │   └── PaymentClientAdapter.java
│   └── notification/
│       └── NotificationClient.java
│
├── saga/                                      # [EVENT / ASYNC WORKFLOWS]
│   ├── handlers/
│   │   └── ...
│   └── publisher/
│       └── ...
│
├── searchcriteria/                            # [QUERY OBJECTS / CRITERIA]
│   └── {feature_name}/
│       └── {Feature}SearchCriteria.java       # Dynamic search parameters encapsulation
│
└── config/                                    # [CROSS-CUTTING / INFRASTRUCTURE CONFIG]
    ├── SecurityConfiguration.java
    ├── ErrorHandlingAdvice.java
    └── exceptions/
        ├── ErrorCode.java
        └── CustomException.java
```

---

## 4. Package Mapping for Feature `001-deck-vocab-vault`

Dưới đây là sơ đồ chi tiết các component của tính năng **001 (Unified Vocabulary Learning System)** được sắp xếp theo đúng Target Structure:

```text
mobile/
├── apis/card/
│   ├── CardController.java
│   └── dtos/
│       ├── CreateCardRequest.java
│       ├── BatchImportRequest.java
│       ├── PracticeResultRequest.java
│       ├── CardResponseDto.java
│       ├── CardDetailResponseDto.java
│       └── DashboardResponseDto.java
├── apis/pendingitem/
│   ├── PendingItemController.java
│   └── dtos/
│       ├── CreatePendingItemRequest.java
│       └── PendingItemResponseDto.java
├── apis/common/
│   └── PageResponse.java
│
├── businesses/boundaries/card/
│   ├── BatchImportCard.java
│   ├── GetCardDashboard.java
│   ├── GetCardDetail.java
│   ├── GetDuePracticeCards.java
│   ├── SubmitPracticeResult.java
│   ├── CreateCard.java
│   └── UpdateCard.java
├── businesses/boundaries/pendingitem/
│   ├── AddPendingItem.java
│   ├── GetPendingItems.java
│   ├── DeletePendingItem.java
│   └── GeneratePrompt.java
│
├── businesses/interactors/card/
│   ├── BatchImportCardInteractor.java
│   ├── GetCardDashboardInteractor.java
│   ├── GetCardDetailInteractor.java
│   ├── GetDuePracticeCardsInteractor.java
│   ├── SubmitPracticeResultInteractor.java
│   ├── CreateCardInteractor.java
│   └── CardMapper.java
├── businesses/interactors/pendingitem/
│   ├── AddPendingItemInteractor.java
│   ├── GetPendingItemsInteractor.java
│   ├── DeletePendingItemInteractor.java
│   ├── GeneratePromptInteractor.java
│   └── PendingItemMapper.java
│
├── businesses/services/card/
│   └── CardService.java
│
├── domains/card/
│   ├── SrsAlgorithmRules.java                 # SM-2 calculation, ease factor, interval logic
│   ├── LeechDetectionRules.java               # wrongCount >= 8 leech flagging rules
│   └── CardValidator.java                     # Validate import payload & invariants
│
├── databases/entities/card/
│   ├── CardEntity.java                        # Collection "card"
│   ├── WordRelation.java                      # Embedded relation document
│   └── ExampleSentence.java                   # Embedded example document
├── databases/entities/pendingitem/
│   └── PendingItemEntity.java                 # Collection "pending_item"
│
├── databases/repositories/card/
│   └── CardRepository.java
├── databases/repositories/pendingitem/
│   └── PendingItemRepository.java
│
├── databases/services/card/
│   └── CardDatabaseService.java               # Auto-linking query & batch aggregation helper
│
└── searchcriteria/card/
    └── CardSearchCriteria.java                # Encapsulate topic, status, search, deckId filtering
```

---

## 5. Code Examples by Layer (Target Structure)

### 5.1 Domain Layer: Pure Business Rule (`mobile.domains.card.SrsAlgorithmRules.java`)
```java
package mobile.domains.card;

import java.util.Calendar;
import java.util.Date;

public class SrsAlgorithmRules {

    public static SrsCalculationResult calculateSM2(int currentRepetition, int currentInterval, double currentEaseFactor, int quality) {
        int nextRepetition;
        int nextInterval;

        if (quality >= 3) {
            if (currentRepetition == 0) {
                nextInterval = 1;
            } else if (currentRepetition == 1) {
                nextInterval = 6;
            } else {
                nextInterval = (int) Math.round(currentInterval * currentEaseFactor);
            }
            nextRepetition = currentRepetition + 1;
        } else {
            nextRepetition = 0;
            nextInterval = 1;
        }

        double nextEaseFactor = currentEaseFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
        nextEaseFactor = Math.max(1.3, nextEaseFactor);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, Math.max(1, nextInterval));
        Date nextReviewDate = cal.getTime();

        return new SrsCalculationResult(nextRepetition, nextInterval, nextEaseFactor, nextReviewDate);
    }

    public record SrsCalculationResult(int repetition, int interval, double easeFactor, Date nextReviewDate) {}
}
```

### 5.2 Boundary Interface (`mobile.businesses.boundaries.card.SubmitPracticeResult.java`)
```java
package mobile.businesses.boundaries.card;

import lombok.*;
import org.bson.types.ObjectId;

public interface SubmitPracticeResult {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private String cardId;
        private String userId;
        private int quality; // 0 to 5
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private String cardId;
        private int stage;
        private String status;
        private int interval;
        private double easeFactor;
        private int wrongCount;
        private String nextReview;
    }
}
```

### 5.3 Interactor Implementation (`mobile.businesses.interactors.card.SubmitPracticeResultInteractor.java`)
```java
package mobile.businesses.interactors.card;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.card.SubmitPracticeResult;
import mobile.databases.entities.card.CardEntity;
import mobile.databases.repositories.card.CardRepository;
import mobile.domains.card.SrsAlgorithmRules;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class SubmitPracticeResultInteractor implements SubmitPracticeResult {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public Response execute(Request request) {
        CardEntity card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        if (request.getQuality() >= 3) {
            if (card.getStage() == 0) {
                card.setStage(1);
            } else if (card.getStage() < 5 && card.getRepetition() >= 2) {
                card.setStage(card.getStage() + 1);
            }
        } else {
            card.setLapses(card.getLapses() + 1);
            card.setWrongCount(card.getWrongCount() + 1);
        }

        SrsAlgorithmRules.SrsCalculationResult result = SrsAlgorithmRules.calculateSM2(
                card.getRepetition(), card.getInterval(), card.getEaseFactor(), request.getQuality()
        );

        card.setRepetition(result.repetition());
        card.setInterval(result.interval());
        card.setEaseFactor(result.easeFactor());
        card.setNextReview(result.nextReviewDate());
        card.setLastReviewed(new Date());
        card.setReviewCount(card.getReviewCount() + 1);

        if (card.getWrongCount() >= 8) {
            card.setStatus("leech");
        } else if (card.getInterval() >= 21) {
            card.setStatus("mature");
        } else if (card.getRepetition() > 0) {
            card.setStatus("learning");
        }

        CardEntity saved = cardRepository.save(card);
        return cardMapper.toPracticeResultResponse(saved);
    }
}
```

### 5.4 REST Controller (`mobile.apis.card.CardController.java`)
```java
package mobile.apis.card;

import lombok.RequiredArgsConstructor;
import mobile.apis.card.dtos.PracticeResultRequest;
import mobile.businesses.boundaries.card.SubmitPracticeResult;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class CardController {
    private final SubmitPracticeResult submitPracticeResult;

    @PostMapping("/{id}/practice-result")
    public ResponseEntity<SubmitPracticeResult.Response> submitResult(
            @PathVariable String id,
            @Valid @RequestBody PracticeResultRequest requestDto) {
        
        SubmitPracticeResult.Request request = SubmitPracticeResult.Request.builder()
                .cardId(id)
                .quality(requestDto.getQuality())
                .build();

        SubmitPracticeResult.Response response = submitPracticeResult.execute(request);
        return ResponseEntity.ok(response);
    }
}
```

---

## 6. Coexistence & Migration Plan

1. **Current Coexistence**:
   - Thư mục legacy (`mobile.controller`, `mobile.Service`, `mobile.model.Entity`, `mobile.repository`) được duy trì cho các feature chưa chuyển đổi (comic, novel, user, rating, gacha...).
   - Feature mới `001-deck-vocab-vault` được tổ chức trực tiếp hoặc di chuyển vào các package chuẩn Clean Architecture (`apis/`, `businesses/`, `domains/`, `databases/`, `searchcriteria/`).
2. **Next Steps**:
   - Khi phát triển hoặc nâng cấp tính năng nào (ví dụ: `002-gacha-deck`, `003-reading-progress`), thực hiện chuyển đổi feature đó sang cấu trúc package mới tương tự như feature 001.
