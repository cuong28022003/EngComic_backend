# Task Breakdown: [Feature Name]

> **Perspective**: Developer / AI Agent  
> **Purpose**: Answer the question **"EXECUTION & CHECKLIST"**  
> **Instructions for AI**: Execute tasks sequentially. Run `./mvnw compile` after completing each task to verify syntax before checking `[x]` and moving to the next task.

---

### Phase 1: Database & Persistence Layer (`databases/`)
- [ ] **T001**: Create MongoDB Entity Document
  - **Where**: `src/main/java/mobile/databases/entities/[Feature]Entity.java`
  - **Details**: Define schema, `@Id ObjectId id`, `@Document(collection = "...")`, Lombok `@Getter`, `@Setter`.
- [ ] **T002**: Create Spring Data Mongo Repository
  - **Where**: `src/main/java/mobile/databases/repositories/[Feature]Repository.java`
  - **Details**: Extend `MongoRepository<[Feature]Entity, ObjectId>`, declare custom query methods.

---

### Phase 2: Business Logic & Use-Case Layer (`businesses/`)
- [ ] **T003**: Create Boundary Interface & Inner Data Models
  - **Where**: `src/main/java/mobile/businesses/boundaries/[feature]/[Action][Feature]Boundary.java`
  - **Details**: Define `execute(Request request) -> Response` with inner classes `Request` and `Response`.
- [ ] **T004**: Implement Use-Case Interactor
  - **Where**: `src/main/java/mobile/businesses/interactors/[feature]/[Action][Feature]Interactor.java`
  - **Details**: Implement all business invariants, check domain conditions, mutate state, and call Repository.

---

### Phase 3: Adapter & REST API Layer (`apis/`)
- [ ] **T005**: Create API Request & Response DTOs
  - **Where**: `src/main/java/mobile/apis/[feature]/dtos/[Action][Feature]RequestDto.java`
  - **Where**: `src/main/java/mobile/apis/[feature]/dtos/[Feature]ResponseDto.java`
  - **Details**: Add validation annotations (`@NotBlank`, `@NotNull`, `@Min`, `@Max`).
- [ ] **T006**: Create Mapper Component
  - **Where**: `src/main/java/mobile/apis/[feature]/[Feature]Mapper.java`
  - **Details**: Spring `@Component` mapper to transform Entity/Boundary Response to API Response DTO.
- [ ] **T007**: Create REST Controller Endpoint
  - **Where**: `src/main/java/mobile/apis/[feature]/[Feature]Controller.java`
  - **Details**: `@RestController`, `@RequestMapping`, validate `@Valid`, invoke Boundary and return `ResponseEntity`.

---

### Phase 4: Verification & Checkpoint
- [ ] **T008**: Build Compilation & Testing Checkpoint
  - **Command**: `./mvnw compile` (or `./mvnw test`)
  - **Requirement**: Build success 100% without compilation or type safety errors.
