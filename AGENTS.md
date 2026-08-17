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
├── specs/                             # Feature-specific specs (Spec-Driven)
│   ├── 000-template/                  # Template (spec.md, plan.md, tasks.md)
│   └── {feature-id}-{feature-name}/   # Individual feature specs
└── src/main/java/mobile/              # Clean Architecture Source Code Layers
    ├── apis/                          # [LAYER 1: ADAPTER / CONTROLLER]
    ├── businesses/                    # [LAYER 2: BUSINESS LOGIC / USE-CASES]
    ├── databases/                     # [LAYER 3: DATA ACCESS / INFRASTRUCTURE]
    └── config/                        # [COMMON LAYER: COMMON & CONFIG]
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

## 3. Clean Architecture: 3 Immutable Principles

### 3.1 Three Immutable Principles
1. **Strict One-Way Dependency Flow**:
   $$\text{apis (Controller)} \longrightarrow \text{businesses (Boundary / Interactor)} \longrightarrow \text{databases (Repository / Entity)}$$
   The `databases` or `entities` layers must NEVER invoke calls backwards into `businesses` or `apis`.
2. **Boundary Interface Pattern**:
   Every business action (Create, Update, OpenPack, ClaimReward) is a separate **Boundary Interface** in `businesses.boundaries.{feature}`. Request/Response DTOs are defined within or alongside the package.
3. **Thin Controller (No Business Logic)**:
   Controllers only receive HTTP requests, validate `@Valid`, extract JWT/Headers, invoke Boundary Interactors, and wrap `ResponseEntity`. They must NEVER contain DB query calls or complex calculations.

### 3.2 Clean Architecture Package Breakdown (`src/main/java/mobile/`)
- `mobile.apis.{feature}`:
  - `{Feature}Controller.java`: REST endpoints.
  - `{Feature}Mapper.java`: Entity <-> DTO mappers.
  - `dtos/`: Client request & response payload classes.
- `mobile.businesses.boundaries.{feature}`:
  - `{Action}{Feature}Boundary.java`: Use-case interface and inner `Request`/`Response` data models.
- `mobile.businesses.interactors.{feature}`:
  - `{Action}{Feature}Interactor.java`: Primary business logic implementation service.
- `mobile.businesses.services`:
  - `{Feature}Service.java`, `{Feature}SearchCriteria.java`: Domain services & advanced search criteria.
- `mobile.databases.entities`:
  - `{Feature}Entity.java` (or `Entity`): MongoDB documents (`@Document`, `@Id ObjectId`).
- `mobile.databases.repositories`:
  - `{Feature}Repository.java`: Spring Data Mongo Repositories (`MongoRepository<Entity, ObjectId>`).
- `mobile.config` / `mobile.common`:
  - `SecurityConfiguration.java`, `ErrorHandlingAdvice.java`, custom exceptions.

---

## 4. Spec-Driven Task Breakdown & AI Workflow

When implementing new features or major enhancements:
1. **Read the Feature Spec**: Read `specs/{feature-id}/spec.md` (What & Why), `plan.md` (How & Schema), and `tasks.md` (Step-by-step).
2. **Execute One Task at a Time from `tasks.md`**:
   ```
   [Phase 1: Database]   Entities in mobile.databases.entities -> Repositories in mobile.databases.repositories
   [Phase 2: Business]   Boundaries in mobile.businesses.boundaries -> Interactors in mobile.businesses.interactors
   [Phase 3: Adapters]   DTOs & Controllers in mobile.apis.{feature}
   [Phase 4: Checkpoint] Verification Checkpoint (Run ./mvnw compile)
   ```
3. **Checkpoints**: Run `./mvnw compile` after every task. Never proceed if compilation fails.
