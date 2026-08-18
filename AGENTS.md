# PROJECT RULES & AGENT INSTRUCTIONS: EngComic_backend

> **Repository**: `EngComic_backend` (Mobile Comic & Novel Reading Backend)  
> **Documentation Index**:
> - [docs/ai-coding-guide.md](file:///c:/Data/code/tieu_luan_chuyen_nganh/source_code/back-end/docs/ai-coding-guide.md) — Developer handbook for prompting, controlling, and stopping AI.
> - [docs/architecture.md](file:///c:/Data/code/tieu_luan_chuyen_nganh/source_code/back-end/docs/architecture.md) — Clean Architecture package breakdown, boundary pattern, 3 immutable principles.
> - [docs/data-model.md](file:///c:/Data/code/tieu_luan_chuyen_nganh/source_code/back-end/docs/data-model.md) — MongoDB collections, fields, relationships, and business invariants.
> - [docs/api-contracts.md](file:///c:/Data/code/tieu_luan_chuyen_nganh/source_code/back-end/docs/api-contracts.md) — Endpoints, request/response DTOs, HTTP status codes, error handling.
> - [docs/spec-driven-guide.md](file:///c:/Data/code/tieu_luan_chuyen_nganh/source_code/back-end/docs/spec-driven-guide.md) — Spec-Driven workflow with the 3-file backbone (`spec.md`, `plan.md`, `tasks.md`).
> - [docs/tech-stack-upgrade-plan.md](file:///c:/Data/code/tieu_luan_chuyen_nganh/source_code/back-end/docs/tech-stack-upgrade-plan.md) — Migration roadmap to Java 21 + Spring Boot 3.3 + Springdoc OpenAPI.

---

## 1. Context Structure & AI Operating Rules

This project operates with a **2-Level Structure**:
1. **Project-level Documentation & Specs (`docs/`, `specs/`)**: Where AI reads rules, context, data models, and feature tasks.
2. **Clean Architecture Source Code Structure (`src/main/java/mobile/`)**: Where AI generates code into strict Clean Architecture layers.

```text
EngComic_backend/
├── AGENTS.md                          # Core instructions & technical constraints
├── docs/                              # Project-wide architecture & contracts
│   ├── ai-coding-guide.md             # Developer handbook for instructing AI
│   ├── architecture.md
│   ├── data-model.md
│   ├── api-contracts.md
│   ├── spec-driven-guide.md
│   └── tech-stack-upgrade-plan.md     # Java 21 & Spring Boot 3.3 roadmap
├── scripts/
│   └── smoke-test/                    # Automated Black-Box Smoke Test Suite
├── specs/                             # Feature-specific specs (Spec-Driven)
│   ├── 000-template/                  # Template (spec.md, plan.md, tasks.md)
│   └── {feature-id}-{feature-name}/   # Individual feature specs
└── src/main/java/mobile/              # Clean Architecture Source Code Layers
    ├── apis/                          # [LAYER 1: ADAPTER / CONTROLLER - Thin, @PreAuthorize]
    ├── businesses/                    # [LAYER 2: BUSINESS LOGIC / USE-CASES (Boundaries & Interactors)]
    ├── domains/                       # [LAYER DOMAIN: PURE DOMAIN RULES (SM-2, Pure Functions, Records)]
    ├── databases/                     # [LAYER 3: DATA ACCESS / INFRASTRUCTURE - String IDs]
    ├── security/                      # [SECURITY CONTEXT - SecurityUtils, JWT Filter]
    └── config/                        # [COMMON LAYER: CONFIG & EXCEPTION ADVICE]
```

---

## 2. Technical Constraints & Environment

### 2.1 Tech Stack & Versions
- **Language**: Java 11 (Upgrading to Java 21 LTS — see [docs/tech-stack-upgrade-plan.md](file:///c:/Data/code/tieu_luan_chuyen_nganh/source_code/back-end/docs/tech-stack-upgrade-plan.md))
- **Framework**: Spring Boot 2.6.2 (Upgrading to Spring Boot 3.3.x)
- **Persistence**: Spring Data MongoDB (`spring-boot-starter-data-mongodb`), String IDs (`@MongoId(FieldType.OBJECT_ID) String id` and `String userId`, `String deckId` instead of `ObjectId`)
- **Security & Authentication**: Spring Security + JWT (`com.auth0:java-jwt:3.18.3`, `io.jsonwebtoken:jjwt:0.9.1`)
- **Third-party Services**: Cloudinary (`com.cloudinary:cloudinary-http5:2.0.0`), Spring Mail (`spring-boot-starter-mail:2.6.4`), Thymeleaf
- **Documentation**: Springfox Swagger UI 3.0.0 (Migrating to `springdoc-openapi`)
- **Build Tool**: Maven (`./mvnw` on Unix / `mvnw.cmd` on Windows)
- **Boilerplate**: Project Lombok (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`, `@RequiredArgsConstructor`)
- **Logging**: Log4j2 (`org.apache.logging.log4j.LogManager`, `org.apache.logging.log4j.Logger`)

### 2.2 Allowed & Prohibited Libraries
- **ALLOWED**:
  - `java.time.*` (e.g. `LocalDate`, `LocalDateTime`, `Date`) for date/time handling.
  - `lombok.*` for getters, setters, constructors, builders.
  - `org.springframework.data.domain.Page`, `Pageable`, `PageRequest`, `Sort` for pagination and sorting.
  - `com.google.common.*` (Guava) for utility methods.
  - `javax.validation.constraints.*` (`@NotBlank`, `@NotNull`, `@Min`, `@Max`, `@Email`, etc.) for DTO validation.
- **PROHIBITED**:
  - ❌ Do NOT add SQL ORM frameworks for MongoDB collections.
  - ❌ Do NOT introduce new legacy date libraries (e.g., Joda-Time).
  - ❌ Do NOT introduce external mapper libraries unless approved; use Spring Component mappers.
  - ❌ Do NOT add alternative unapproved JSON libraries.

### 2.3 Valid Build & Verification Commands
- Check syntax & compile classes:
  ```powershell
  ./mvnw compile
  ```
- Compile test classes without running tests:
  ```powershell
  ./mvnw test-compile
  ```
- Run unit & integration tests:
  ```powershell
  ./mvnw test
  ```

---

## 3. Clean Architecture: 4 Immutable Principles

### 3.1 Four Immutable Principles
1. **Strict One-Way Dependency Flow**:
   $$\text{apis (Controller)} \longrightarrow \text{businesses (Boundary / Interactor)} \longrightarrow \text{domains (Pure Rules)} \longrightarrow \text{databases (Repository / Entity)}$$
   The `databases`, `domains` or `entities` layers must NEVER invoke calls backwards into `businesses` or `apis`.
2. **Boundary Interface Pattern**:
   Every business action (Create, Update, OpenPack, ClaimReward, SubmitPracticeResult) is a separate **Boundary Interface** in `businesses.boundaries.{feature}`. Request/Response DTOs are defined within or alongside the package.
3. **Pure Domain Layer (Zero Framework Annotations)**:
   All business invariants, formulas, algorithms (e.g., SM-2 SRS, leech detection, capacity checks) reside in `mobile.domains.{feature}.{Feature}Rules` as pure Java functions and records. No Spring annotations (`@Service`, `@Component`), no repository access.
4. **Thin Controller & Declarative Security**:
   Controllers only receive HTTP requests, validate `@Valid`, use `SecurityUtils.getCurrentUserId()`, secure via `@PreAuthorize("isAuthenticated()")`, invoke Boundary Interactors, and wrap `ResponseEntity`.

### 3.2 Clean Architecture Package Breakdown (`src/main/java/mobile/`)
- `mobile.apis.{feature}`:
  - `{Feature}Controller.java`: REST endpoints (thin, uses `SecurityUtils` and `@PreAuthorize`).
  - `dtos/`: Client request & response payload classes.
- `mobile.businesses.boundaries.{feature}`:
  - `{Action}{Feature}.java`: Use-case interface and inner `Request`/`Response` data models.
- `mobile.businesses.interactors.{feature}`:
  - `{Action}{Feature}Interactor.java`: Primary business logic implementation service.
  - `{Feature}Mapper.java`: Entity <-> DTO mappers.
- `mobile.domains.{feature}`:
  - `{Feature}Rules.java`: Pure functions & records for domain calculations and state transitions.
- `mobile.databases.entities.{feature}`:
  - `{Feature}Entity.java`: MongoDB documents (`@Document`, `@MongoId(FieldType.OBJECT_ID) String id`).
- `mobile.databases.repositories.{feature}`:
  - `{Feature}Repository.java`: Spring Data Mongo Repositories (`MongoRepository<Entity, String>`).
- `mobile.security`:
  - `SecurityUtils.java`, `AuthTokenFilter.java`, `AppSecurityConfig.java`.
- `mobile.Handler`:
  - `CustomExceptionHandler.java` (centralized handling for 403 Forbidden, 400 Validation, 404 Not Found).

---

## 4. Spec-Driven Task Breakdown & AI Workflow

When implementing new features or major enhancements:
1. **Read the Feature Spec**: Read `specs/{feature-id}/spec.md` (What & Why), `plan.md` (How & Schema), and `tasks.md` (Step-by-step).
2. **Execute One Task at a Time from `tasks.md`**:
   ```
   [Phase 1: Database]   Entities in mobile.databases.entities -> Repositories in mobile.databases.repositories
   [Phase 2: Domain]     Pure logic rules in mobile.domains.{feature} (with Unit Tests)
   [Phase 3: Business]   Boundaries in mobile.businesses.boundaries -> Interactors in mobile.businesses.interactors
   [Phase 4: Adapters]   DTOs & Controllers in mobile.apis.{feature} (with SecurityUtils & @PreAuthorize)
   [Phase 5: Checkpoint] Verification Checkpoint (Run ./mvnw test && ./scripts/smoke-test/run-smoke.sh)
   ```
3. **Checkpoints**: Run `./mvnw test` after every task. Never proceed if compilation or tests fail.
