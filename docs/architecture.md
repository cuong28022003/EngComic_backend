# Architecture & Clean Architecture Package Structure: EngComic_backend

## 1. Overview & Architectural Pattern

`EngComic_backend` áp dụng mô hình **Clean Architecture kết hợp Package-by-Feature & Pure Domain Layer**. Mô hình này đảm bảo phân tách rõ ràng trách nhiệm giữa các tầng (Layered Separation of Concerns), ngăn chặn việc rò rỉ logic nghiệp vụ, và chuẩn hóa quy trình phát triển cho cả lập trình viên và AI Agents.

> **Lộ trình áp dụng (Migration Strategy)**:
>
> - **Module tiên phong (Pilot)**: Module `001-deck-vocab-vault` (`card`, `deck`, `pendingitem`) đã được chuẩn hóa và áp dụng **100% theo Package Structure mới, Pure Domain Layer, Declarative Security & String IDs**.
> - **Các module hiện tại (Legacy)**: Tạm thời giữ nguyên cấu trúc cũ (`controller/`, `Service/`, `model/Entity/`, `repository/`) để duy trì độ ổn định và sẽ được migrate dần theo từng phase khi phát triển tính năng mới (`comic`, `novel`, `gacha`, `payment`...).

```mermaid
graph TD
    subgraph Layer1[LAYER 1: ADAPTER / PRESENTATION]
        API[mobile.apis.{feature}]
        Controller["{Feature}Controller.java (@PreAuthorize, Thin)"]
        DTOs["dtos (Request / Response DTOs)"]
        CommonAPI["mobile.apis.common (PageResponse.java)"]
    end

    subgraph Layer2[LAYER 2: APPLICATION / USE CASES]
        Boundaries["mobile.businesses.boundaries.{feature} (Use-Case Contracts)"]
        Interactors["mobile.businesses.interactors.{feature} (Use-Case Logic)"]
        Mappers["{Feature}Mapper.java"]
    end

    subgraph LayerDomain[LAYER DOMAIN: PURE BUSINESS DOMAIN]
        DomainRules["mobile.domains.{feature}.{Feature}Rules (Pure Functions, Records, Zero Annotations)"]
    end

    subgraph Layer3[LAYER 3: INFRASTRUCTURE / PERSISTENCE]
        Entities["mobile.databases.entities.{feature} ({Feature}Entity - String IDs)"]
        Repositories["mobile.databases.repositories.{feature} ({Feature}Repository)"]
        DbServices["mobile.databases.services.{feature} ({Feature}DatabaseService)"]
    end

    subgraph LayerSecurity[SECURITY & CONTEXT]
        SecurityUtils["mobile.security.SecurityUtils (Principal Resolver)"]
        SecurityConfig["mobile.security.config.AppSecurityConfig (@EnableMethodSecurity)"]
        ExceptionHandling["mobile.Handler.CustomExceptionHandler (AccessDeniedException 403)"]
    end

    Client[Mobile / Web Client] --> Controller
    Controller --> SecurityUtils
    Controller --> Boundaries
    Boundaries --> Interactors
    Interactors --> DomainRules
    Interactors --> Repositories
    Interactors --> DbServices
    Repositories --> MongoDB[(MongoDB Database)]
    Interactors --> Mappers
```

---

## 2. 4 Immutable Architectural Principles

1. **Strict One-Way Dependency Flow**:
   $$
   \text{apis} \longrightarrow \text{businesses} \longrightarrow \text{domains} \longrightarrow \text{databases}
   $$

   - Lớp `databases` (Entities, Repositories, DatabaseServices) và `domains` tuyệt đối **KHÔNG ĐƯỢC PHÉP** gọi ngược về `businesses` hoặc `apis`.
   - Lớp `apis` chỉ phụ thuộc vào `businesses.boundaries` (thông qua interface contract), DTOs và `SecurityUtils`.
2. **Boundary Interface Pattern**:
   - Mỗi hành động nghiệp vụ (Create, Update, Get, Search, BatchImport, SubmitPracticeResult...) là một **Boundary Interface** riêng biệt trong `businesses.boundaries.{feature}`.
   - Request và Response Data Models của Use-Case được định nghĩa trực tiếp bên trong hoặc cùng package với Boundary Interface.
3. **Pure Domain Layer (Zero Framework Dependency)**:
   - Các class `mobile.domains.{feature}.{Feature}Rules` chứa toàn bộ thuật toán nghiệp vụ thuần túy (Spaced Repetition SM-2, Leech Detection, Mastery Calculation, Deck Capacity).
   - **Tuyệt đối không chứa annotation Spring (`@Service`, `@Component`), không inject Repository, không gọi HTTP API.** Chỉ dùng Pure Functions, Records và kiểu dữ liệu chuẩn của Java.
4. **Thin Controller & Declarative Security**:
   - Controller chỉ làm nhiệm vụ tiếp nhận HTTP request, validate `@Valid`, trích xuất `userId` từ `SecurityUtils.getCurrentUserId()`, phân quyền bằng `@PreAuthorize("isAuthenticated()")` / `@PreAuthorize("hasRole(...)")`, gọi Boundary Interactor, và đóng gói `ResponseEntity`.

---

## 3. Target Package Structure (`src/main/java/mobile/`)

```text
src/main/java/mobile/
│
├── apis/                                      # [LAYER 1: ADAPTER / PRESENTATION]
│   ├── {feature_name}/                        # e.g., card, deck, pendingitem
│   │   ├── {Feature}Controller.java           # REST Endpoints (Thin, @PreAuthorize)
│   │   └── dtos/                              # Client-facing Request/Response DTOs
│   │       ├── Create{Feature}Request.java
│   │       ├── Update{Feature}Request.java
│   │       └── {Feature}ResponseDto.java
│   └── common/
│       └── PageResponse.java                  # Generic pagination wrapper DTO
│
├── businesses/                                # [LAYER 2: APPLICATION / USE CASE]
│   ├── boundaries/                            # Use-case contracts (Interfaces)
│   │   └── {feature_name}/
│   │       ├── Create{Feature}.java
│   │       └── SubmitPracticeResult.java
│   ├── interactors/                           # Use-case implementations & Mappers
│   │   └── {feature_name}/
│   │       ├── Create{Feature}Interactor.java
│   │       └── {Feature}Mapper.java           # Entity <-> DTO transformation
│   └── services/                              # Optional domain/application services
│
├── domains/                                   # [LAYER DOMAIN: PURE BUSINESS LOGIC]
│   └── {feature_name}/
│       └── {Feature}Rules.java                # Pure domain functions & records (SM-2, Leech, Stats)
│
├── databases/                                 # [LAYER 3: INFRASTRUCTURE / PERSISTENCE]
│   ├── entities/                              # MongoDB Documents (String IDs)
│   │   └── {feature_name}/
│   │       └── {Feature}Entity.java           # @MongoId(FieldType.OBJECT_ID) String id
│   ├── repositories/                          # Spring Data Mongo Repositories
│   │   └── {feature_name}/
│   │       └── {Feature}Repository.java       # MongoRepository<Entity, String>
│   └── services/                              # Database helper query services
│       └── {feature_name}/
│           └── {Feature}DatabaseService.java
│
├── searchcriteria/                            # [QUERY CRITERIA]
│   └── {feature_name}/
│       └── {Feature}SearchCriteria.java       # Dynamic search parameters
│
├── security/                                  # [SECURITY CONTEXT & RESOLVER]
│   ├── SecurityUtils.java                     # Trích xuất getCurrentUserId(), getCurrentUsername()
│   ├── filter/AuthTokenFilter.java            # JWT authentication filter
│   └── config/AppSecurityConfig.java          # @EnableMethodSecurity(prePostEnabled = true)
│
└── Handler/                                   # [GLOBAL EXCEPTION HANDLING]
    └── CustomExceptionHandler.java            # 403 AccessDeniedException, 400 Validation, 404
```

---

## 4. Package Mapping Thực Tế: Feature `001-deck-vocab-vault`

Toàn bộ feature 001 đã được tổ chức hoàn chỉnh theo cấu trúc chuẩn:

```text
mobile/
├── apis/
│   ├── card/
│   │   ├── CardController.java
│   │   └── dtos/ (CreateCardRequest, BatchImportRequest, PracticeResultRequest, CardResponseDto...)
│   ├── deck/
│   │   ├── DeckController.java
│   │   └── dtos/ (CreateDeckRequest, DeckResponseDto, DeckStatisticsResponse...)
│   └── pendingitem/
│       ├── PendingItemController.java
│       └── dtos/ (CreatePendingItemRequest, PendingItemResponseDto...)
│
├── businesses/
│   ├── boundaries/
│   │   ├── card/ (BatchImportCard, GetCardDashboard, GetCardDetail, GetDuePracticeCards, SubmitPracticeResult, CreateCard, UpdateCard...)
│   │   └── pendingitem/ (AddPendingItem, GetPendingItems, DeletePendingItem, GeneratePrompt...)
│   └── interactors/
│       ├── card/ (BatchImportCardInteractor, GetCardDashboardInteractor, SubmitPracticeResultInteractor, CardMapper...)
│       └── pendingitem/ (AddPendingItemInteractor, GetPendingItemsInteractor, PendingItemMapper...)
│
├── domains/
│   ├── card/
│   │   └── CardRules.java                     # Pure SM-2 SRS Algorithm, Leech rules, Status classification
│   └── deck/
│       └── DeckRules.java                     # Pure Deck statistics calculation & Capacity rules
│
├── databases/
│   ├── entities/
│   │   ├── card/ (CardEntity, WordRelation, ExampleSentence)
│   │   ├── deck/ (DeckEntity)
│   │   └── pendingitem/ (PendingItemEntity)
│   ├── repositories/
│   │   ├── card/ (CardRepository - extends MongoRepository<CardEntity, String>)
│   │   ├── deck/ (DeckRepository - extends MongoRepository<DeckEntity, String>)
│   │   └── pendingitem/ (PendingItemRepository - extends MongoRepository<PendingItemEntity, String>)
│   └── services/
│       └── card/ (CardDatabaseService)
│
└── security/
    └── SecurityUtils.java                     # Static helper lấy userId từ SecurityContext
```

---

## 5. Code Examples by Layer

### 5.1 Pure Domain Layer: `mobile.domains.card.CardRules.java`

```java
package mobile.domains.card;

import java.util.Calendar;
import java.util.Date;

public final class CardRules {
    private CardRules() {}

    public static SrsResult calculateSM2(int currentRepetition, int currentInterval, double currentEaseFactor, int quality) {
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

        return new SrsResult(nextRepetition, nextInterval, nextEaseFactor, nextReviewDate);
    }

    public record SrsResult(int repetition, int interval, double easeFactor, Date nextReviewDate) {}
}
```

### 5.2 Boundary Interface: `mobile.businesses.boundaries.card.SubmitPracticeResult.java`

```java
package mobile.businesses.boundaries.card;

import lombok.*;
import mobile.apis.card.dtos.CardResponseDto;

public interface SubmitPracticeResult {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private String cardId;
        private int quality; // 0 to 5
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private CardResponseDto card;
    }
}
```

### 5.3 Interactor Implementation: `mobile.businesses.interactors.card.SubmitPracticeResultInteractor.java`

```java
package mobile.businesses.interactors.card;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.card.SubmitPracticeResult;
import mobile.databases.entities.card.CardEntity;
import mobile.databases.repositories.card.CardRepository;
import mobile.domains.card.CardRules;
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

        CardRules.SrsResult result = CardRules.calculateSM2(
                card.getRepetition(), card.getInterval(), card.getEaseFactor(), request.getQuality()
        );

        card.setRepetition(result.repetition());
        card.setInterval(result.interval());
        card.setEaseFactor(result.easeFactor());
        card.setNextReview(result.nextReviewDate());
        card.setLastReviewed(new Date());
        card.setReviewCount(card.getReviewCount() + 1);

        CardEntity saved = cardRepository.save(card);
        return Response.builder().card(cardMapper.toResponse(saved)).build();
    }
}
```

### 5.4 Thin Controller: `mobile.apis.card.CardController.java`

```java
package mobile.apis.card;

import lombok.RequiredArgsConstructor;
import mobile.apis.card.dtos.PracticeResultRequest;
import mobile.businesses.boundaries.card.SubmitPracticeResult;
import mobile.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class CardController {
    private final SubmitPracticeResult submitPracticeResult;

    @PostMapping("/{id}/practice-result")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> submitPracticeResult(@PathVariable String id, @Valid @RequestBody PracticeResultRequest resultRequest) {
        SubmitPracticeResult.Request req = SubmitPracticeResult.Request.builder()
                .cardId(id)
                .quality(resultRequest.getQuality())
                .build();

        SubmitPracticeResult.Response response = submitPracticeResult.execute(req);
        if (response != null && response.getCard() != null) {
            return ResponseEntity.ok(response.getCard());
        }
        return ResponseEntity.notFound().build();
    }
}
```

---

## 6. Testing Strategy

Hệ thống được bảo vệ bởi **2 tầng kiểm thử**:

1. **White-Box Domain Unit Tests (`src/test/java/mobile/domains/`)**:
   - `CardRulesTest.java`: Kiểm thử độc lập thuật toán SM-2, chuyển stage, leech detection (< 50ms, không cần Spring context).
   - `DeckRulesTest.java`: Kiểm thử thống kê độ thành thạo và capacity.
2. **Black-Box Automation Smoke Tests (`scripts/smoke-test/`)**:
   - Chạy qua `bash ./scripts/smoke-test/run-smoke.sh`.
   - Kiểm tra liên hoàn từ Server Ping $\rightarrow$ Auth JWT $\rightarrow$ 403 Forbidden $\rightarrow$ Toàn bộ CRUD & Ôn tập SRS.
