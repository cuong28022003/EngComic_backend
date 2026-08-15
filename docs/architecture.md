# Architecture & Clean Architecture Package Structure: EngComic_backend

## 1. Overview & Architectural Pattern

`EngComic_backend` applies **Clean Architecture combined with Package-by-Feature**. This ensures strict separation of concerns, zero logic leakage between layers, and high testability for AI and developers.

```mermaid
graph TD
    subgraph Layer1[LAYER 1: ADAPTERS / APIS]
        API[mobile.apis.{feature}]
        Controller["{Feature}Controller"]
        Mapper["{Feature}Mapper"]
        DTOs["Request / Response DTOs"]
    end

    subgraph Layer2[LAYER 2: BUSINESS LOGIC / USE-CASES]
        Boundaries["mobile.businesses.boundaries.{feature} (Boundary Interfaces)"]
        Interactors["mobile.businesses.interactors.{feature} (Use-Case Interactors)"]
        Services["mobile.businesses.services (Domain Services & Search Criteria)"]
    end

    subgraph Layer3[LAYER 3: DATA ACCESS / DATABASES]
        Entities["mobile.databases.entities ({Feature}Entity)"]
        Repositories["mobile.databases.repositories ({Feature}Repository)"]
    end

    subgraph LayerCommon[COMMON LAYER: COMMON & CONFIG]
        Security["SecurityConfiguration & JWT"]
        Exceptions["ErrorHandlingAdvice & Custom Exceptions"]
    end

    Client[Mobile / Web Client] --> Controller
    Controller --> Boundaries
    Boundaries --> Interactors
    Interactors --> Repositories
    Repositories --> MongoDB[(MongoDB Database)]
    Interactors --> Entities
    Controller --> Mapper
```

---

## 2. 3 Immutable Architectural Principles

1. **Strict One-Way Dependency Flow**:
   $$\text{apis (Controller/Adapter)} \longrightarrow \text{businesses (Boundary / Interactor)} \longrightarrow \text{databases (Repository / Entity)}$$
   The `databases` and `entities` layers must NEVER reference or invoke calls backwards into `businesses` or `apis`.
2. **Boundary Interface Pattern**:
   - Every business action (Create, Update, OpenPack, ClaimReward, SyncProgress) is a separate **Boundary Interface** in `businesses.boundaries.{feature}`.
   - Request and Response parameters for each Use-Case are explicitly defined within or alongside that Boundary.
3. **Thin Controller (No Business Logic)**:
   - Controllers only receive HTTP requests, validate `@Valid`, extract JWT/Headers, invoke Boundary Interactors, and wrap `ResponseEntity`.
   - Controllers must NEVER contain database queries, business calculations, or domain algorithms.

---

## 3. Detailed Package Structure (`src/main/java/mobile/`)

```text
src/main/java/mobile/
│
├── apis/                                # [LAYER 1: ADAPTER / CONTROLLER]
│   └── {feature_name}/                  # Grouped by feature (e.g. gacha, comic, quest, payment, card)
│       ├── {Feature}Controller.java     # REST Controller: Handles HTTP requests & input validation
│       ├── {Feature}Mapper.java         # Mapper Component: Transforms Entity <-> DTO
│       └── dtos/                        # Request and Response DTO payloads
│           ├── Create{Feature}Request.java
│           └── {Feature}ResponseDto.java
│
├── businesses/                          # [LAYER 2: BUSINESS LOGIC / USE-CASES]
│   ├── boundaries/                      # Use-case Interfaces (Business contracts)
│   │   └── {feature_name}/
│   │       └── Create{Feature}Boundary.java  # Interface + Request/Response DTOs
│   ├── interactors/                     # Use-case Implementations (Core business logic)
│   │   └── {feature_name}/
│   │       └── Create{Feature}Interactor.java
│   └── services/                        # Domain Services + Advanced Search Criteria
│       ├── {Feature}Service.java
│       └── {Feature}SearchCriteria.java # Encapsulates filtering parameters
│
├── databases/                           # [LAYER 3: DATA ACCESS / INFRASTRUCTURE]
│   ├── entities/                        # Database Entities (MongoDB models: @Document, @Id ObjectId)
│   │   └── {Feature}Entity.java
│   ├── repositories/                    # Spring Data Repositories (MongoRepository<T, ObjectId>)
│   │   └── {Feature}Repository.java
│   └── migrations/                      # Seeders and database migration scripts
│
└── config/ (or common/)                 # [COMMON LAYER: SHARED UTILS & CONFIGS]
    ├── SecurityConfiguration.java       # Security configuration, JWT filter
    ├── ErrorHandlingAdvice.java         # Centralized Exception Handler (@RestControllerAdvice)
    └── exceptions/                      # Error codes and Custom Exceptions
```

---

## 4. Code Examples by Layer

### 4.1 Boundary Interface (`mobile.businesses.boundaries.quest.ClaimRewardBoundary.java`)
```java
package mobile.businesses.boundaries.quest;

import lombok.*;
import org.bson.types.ObjectId;

public interface ClaimRewardBoundary {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private ObjectId userId;
        private ObjectId questId;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private boolean success;
        private int expGained;
        private int coinsEarned;
        private String message;
    }
}
```

### 4.2 Interactor Implementation (`mobile.businesses.interactors.quest.ClaimRewardInteractor.java`)
```java
package mobile.businesses.interactors.quest;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.quest.ClaimRewardBoundary;
import mobile.databases.repositories.QuestRepository;
import mobile.databases.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClaimRewardInteractor implements ClaimRewardBoundary {
    private final QuestRepository questRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Response execute(Request request) {
        // Execute all business rules here
        return Response.builder()
                .success(true)
                .expGained(100)
                .coinsEarned(50)
                .message("Reward claimed successfully")
                .build();
    }
}
```

### 4.3 Controller (`mobile.apis.quest.QuestController.java`)
```java
package mobile.apis.quest;

import lombok.RequiredArgsConstructor;
import mobile.businesses.boundaries.quest.ClaimRewardBoundary;
import mobile.apis.quest.dtos.ClaimQuestRequestDto;
import org.bson.types.ObjectId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class QuestController {
    private final ClaimRewardBoundary claimRewardBoundary;

    @PostMapping("/{questId}/claim")
    public ResponseEntity<ClaimRewardBoundary.Response> claimReward(
            @PathVariable String questId,
            @Valid @RequestBody ClaimQuestRequestDto dto) {
        
        ClaimRewardBoundary.Request request = ClaimRewardBoundary.Request.builder()
                .questId(new ObjectId(questId))
                .userId(dto.getUserId())
                .build();

        ClaimRewardBoundary.Response response = claimRewardBoundary.execute(request);
        return ResponseEntity.ok(response);
    }
}
```
