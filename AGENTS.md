# PROJECT RULES & AGENT INSTRUCTIONS: EngComic_backend

> **Repository**: `EngComic_backend` (Mobile Comic & Novel Reading Backend)  
> **Documentation Index**:
> - [docs/ai-coding-guide.md](file:///d:/Others/my-projects/EngComic_backend/docs/ai-coding-guide.md) — Cẩm nang hướng dẫn Developer cách ra lệnh, kiểm soát và dừng AI.
> - [docs/architecture.md](file:///d:/Others/my-projects/EngComic_backend/docs/architecture.md) — Clean Architecture package breakdown, boundary pattern, 3 immutable principles.
> - [docs/data-model.md](file:///d:/Others/my-projects/EngComic_backend/docs/data-model.md) — MongoDB collections, fields, relationships, and business invariants.
> - [docs/api-contracts.md](file:///d:/Others/my-projects/EngComic_backend/docs/api-contracts.md) — Endpoints, request/response DTOs, HTTP status codes, error handling.
> - [docs/spec-driven-guide.md](file:///d:/Others/my-projects/EngComic_backend/docs/spec-driven-guide.md) — Spec-Driven workflow with the 3-file backbone (`spec.md`, `plan.md`, `tasks.md`).
> - [docs/tech-stack-upgrade-plan.md](file:///d:/Others/my-projects/EngComic_backend/docs/tech-stack-upgrade-plan.md) — Migration roadmap to Java 21 + Spring Boot 3.3 + Springdoc OpenAPI.

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
    ├── apis/                          # [TẦNG 1: ADAPTER / CONTROLLER]
    ├── businesses/                    # [TẦNG 2: BUSINESS LOGIC / USE-CASES]
    ├── databases/                     # [TẦNG 3: DATA ACCESS / INFRASTRUCTURE]
    └── config/                        # [TẦNG CHUNG: COMMON & CONFIG]
```

---

## 2. Technical Constraints & Environment

### 2.1 Tech Stack & Versions
- **Language**: Java 11 (Upgrading to Java 21 LTS — see [docs/tech-stack-upgrade-plan.md](file:///d:/Others/my-projects/EngComic_backend/docs/tech-stack-upgrade-plan.md))
- **Framework**: Spring Boot 2.6.2 (Upgrading to Spring Boot 3.3.x)
- **Persistence**: Spring Data MongoDB (`spring-boot-starter-data-mongodb`), MongoDB BSON `ObjectId`
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
   Tầng `databases` hoặc `entities` tuyệt đối không gọi ngược lên `businesses` hoặc `apis`.
2. **Boundary Interface Pattern**:
   Mỗi hành động nghiệp vụ (Create, Update, OpenPack, ClaimReward) là một **Boundary Interface** riêng biệt trong `businesses.boundaries.{feature}`. Request/Response DTOs được định nghĩa kèm bên trong hoặc cùng package.
3. **Thin Controller (No Business Logic)**:
   Controller chỉ đón nhận HTTP request, validate `@Valid`, trích xuất JWT/Header, gọi Boundary Interactor và đóng gói `ResponseEntity`. Tuyệt đối không chứa câu truy vấn DB hay logic tính toán phức tạp.

### 3.2 Clean Architecture Package Breakdown (`src/main/java/mobile/`)
- `mobile.apis.{feature}`:
  - `{Feature}Controller.java`: REST endpoints.
  - `{Feature}Mapper.java`: Entity <-> DTO mappers.
  - `dtos/`: Client request & response payload classes.
- `mobile.businesses.boundaries.{feature}`:
  - `{Action}{Feature}Boundary.java`: Use-case interface và inner `Request`/`Response` data models.
- `mobile.businesses.interactors.{feature}`:
  - `{Action}{Feature}Interactor.java`: Service thực thi logic nghiệp vụ chính.
- `mobile.businesses.services`:
  - `{Feature}Service.java`, `{Feature}SearchCriteria.java`: Domain services & tìm kiếm nâng cao.
- `mobile.databases.entities`:
  - `{Feature}Entity.java` (hoặc `Entity`): MongoDB documents (`@Document`, `@Id ObjectId`).
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
