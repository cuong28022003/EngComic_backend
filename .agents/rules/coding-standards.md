# EngComic_backend Coding Standards & Architecture Rules

## 1. Technical Constraints
- **Java Version**: Java 11
- **Framework**: Spring Boot 2.6.2
- **Database**: MongoDB with Spring Data MongoDB (`@Document`, `ObjectId`, `@DBRef`, `MongoRepository`)
- **Security**: JWT (`com.auth0:java-jwt`, `io.jsonwebtoken:jjwt`), BCrypt password encoder
- **File Storage**: Cloudinary (`com.cloudinary:cloudinary-http5`)
- **Documentation**: Springfox Swagger UI 3.0.0
- **Validation**: `javax.validation.constraints.*` (`@NotBlank`, `@NotNull`, etc.)
- **Compile Verification**: `./mvnw compile`

## 2. Layered Architecture & Separation of Concerns
- **`mobile.controller`**:
  - Handles HTTP endpoints, input validation (`@Valid`), request parameters, and response wrapping.
  - No business logic or direct raw database queries.
- **`mobile.Service` & `mobile.Service.Impl`**:
  - Contains core business rules, transactional logic, and multi-service orchestration.
- **`mobile.repository`**:
  - Spring Data MongoDB interfaces extending `MongoRepository<T, ObjectId>` or custom template queries.
- **`mobile.mapping`**:
  - Mapper components (e.g. `ComicMapping`) converting entities to response DTOs.
- **`mobile.model.payload`**:
  - `request.*`: Request DTOs.
  - `response.*`: Response DTOs and envelopes (`SuccessResponse`, `SuccessResponseList`, `ErrorResponse`).
- **`mobile.Handler`**:
  - `CustomExceptionHandler` for centralized exception handling.

## 3. Coding Guidelines
- Use constructor-based injection with `@RequiredArgsConstructor` (Lombok) on `final` dependencies.
- Avoid using `var` or raw generic types without type parameters.
- Never catch exceptions silently without logging or rethrowing.
- Always use `ObjectId` for MongoDB entity identifiers.
