# Implementation Plan: [Feature Name]

> **Góc nhìn**: Tech Lead / System Architect  
> **Mục đích**: Trả lời câu hỏi **"LÀM NHƯ THẾ NÀO & THIẾT KẾ RA SAO?" (HOW & ARCHITECTURE)**

---

## 1. Technical Context & Constraints
- **Package Base**: `mobile.*`
- **Architecture Pattern**: Clean Architecture + Package-by-Feature (`apis` $\rightarrow$ `businesses` $\rightarrow$ `databases`)
- **Database**: MongoDB (`@Document`, `ObjectId`)
- **Validation**: `@Valid`, `@NotBlank`, `@NotNull`, `@Min`, `@Max`
- **Security**: JWT Bearer Token, Spring Security Context

---

## 2. Data Model & Database Schema

### 2.1 MongoDB Collection: `[collection_name]`
- **Entity Class**: `mobile.databases.entities.[Feature]Entity.java`
- **Fields**:
  | Field | Java Type | BSON Type | Constraints | Description |
  | :--- | :--- | :--- | :--- | :--- |
  | `id` | `ObjectId` | `ObjectId` | `@Id` | Khóa chính BSON |
  | `userId` | `ObjectId` | `ObjectId` | `@NotNull`, Indexed | Tham chiếu User |
  | `status` | `String` | `String` | `@NotBlank` | Trạng thái (PENDING, COMPLETED) |
  | `createdAt` | `Date` | `Date` | `@CreatedDate` | Thời điểm tạo |
  | `updatedAt` | `Date` | `Date` | `@LastModifiedDate` | Thời điểm cập nhật |

---

## 3. Boundary Contracts & DTOs

### 3.1 Boundary Interface (`mobile.businesses.boundaries.[feature].[Action][Feature]Boundary.java`)
```java
package mobile.businesses.boundaries.[feature];

import lombok.*;
import org.bson.types.ObjectId;

public interface [Action][Feature]Boundary {
    Response execute(Request request);

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Request {
        private ObjectId userId;
        private String targetField;
        private int amount;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    class Response {
        private boolean success;
        private String message;
        private Object data;
    }
}
```

### 3.2 API Request/Response DTOs (`mobile.apis.[feature].dtos`)
- **Request DTO**: `[Action][Feature]RequestDto.java`
- **Response DTO**: `[Feature]ResponseDto.java`

---

## 4. Business Flow & Sequence Diagram

```mermaid
sequenceDiagram
    autonumber
    actor Client as Mobile Client
    participant Controller as [Feature]Controller (apis)
    participant Boundary as [Action][Feature]Boundary (businesses)
    participant Interactor as [Action][Feature]Interactor (businesses)
    participant Repo as [Feature]Repository (databases)
    participant DB as MongoDB

    Client->>Controller: POST /api/[feature]/[action] (DTO + JWT)
    Controller->>Controller: Validate input (@Valid)
    Controller->>Boundary: execute(Request)
    Boundary->>Interactor: delegate
    Interactor->>Repo: findById / check invariants
    Repo->>DB: Query
    DB-->>Repo: Return entity
    Interactor->>Interactor: Execute Business Logic (Validate & Mutate)
    Interactor->>Repo: save(entity)
    Repo->>DB: Persist
    Interactor-->>Controller: Return Response DTO
    Controller-->>Client: 200 OK (Response Envelope)
```

---

## 5. Error Handling & HTTP Status Mapping
- `400 BAD_REQUEST`: Validation thất bại hoặc điều kiện nghiệp vụ không hợp lệ.
- `401 UNAUTHORIZED`: Chưa đăng nhập hoặc token hết hạn.
- `403 FORBIDDEN`: Không có quyền thực hiện.
- `404 NOT_FOUND`: Không tìm thấy tài nguyên trong cơ sở dữ liệu.
