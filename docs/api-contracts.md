# API Contracts & Error Handling: EngComic_backend

## 1. Authentication & Headers

- **Authentication Scheme**: JWT Bearer Token.
- **Header**:
  ```http
  Authorization: Bearer <jwt_access_token>
  ```
- **Content Type**: `application/json` (or `multipart/form-data` for file uploads like comic covers and character sprites).

---

## 2. Response Envelopes

### 2.1 Single Object / Mutation Success (`SuccessResponse`)
```json
{
  "success": true,
  "status": 200,
  "message": "Operation successful",
  "data": {
    "key": "value"
  }
}
```

### 2.2 List Response (`SuccessResponseList`)
```json
{
  "success": true,
  "status": 200,
  "message": "Success",
  "data": [
    { ... },
    { ... }
  ]
}
```

### 2.3 Paginated Response (`org.springframework.data.domain.Page<T>`)
```json
{
  "content": [ ... ],
  "pageable": {
    "sort": { "sorted": true, "unsorted": false, "empty": false },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 5,
  "totalElements": 48,
  "last": false,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 10,
  "empty": false
}
```

### 2.4 Error Response Envelope (`ErrorResponse` / `ErrorResponseMap`)
- **Standard Error**:
  ```json
  {
    "success": false,
    "status": 404,
    "message": "Record Not Found",
    "details": [
      "Comic with ID 62a1b2c3d4e5f67890123456 not found"
    ]
  }
  ```
- **Validation Error (`ErrorResponseMap`)**:
  ```json
  {
    "success": false,
    "status": 400,
    "message": "Validation Error: ",
    "details": {
      "username": "Username is required",
      "email": "Email must be a valid email format"
    }
  }
  ```

---

## 3. Standard HTTP Status Codes & Exception Mapping

Handled globally via `mobile.Handler.CustomExceptionHandler`:

| Status Code | Description | Thrown Exception |
|:---|:---|:---|
| `200 OK` | Request succeeded | N/A |
| `201 CREATED` | Resource successfully created | N/A |
| `400 BAD_REQUEST` | Validation failed or unreadable JSON | `MethodArgumentNotValidException`, `HttpMessageNotReadableException` |
| `401 UNAUTHORIZED` | Invalid or expired token, wrong password | `BadCredentialsException`, JWT parse errors |
| `403 FORBIDDEN` | Missing required role/permission | `AccessDeniedException` |
| `404 NOT_FOUND` | Entity not found | `RecordNotFoundException` |
| `500 INTERNAL_SERVER_ERROR` | Unhandled server exception | `Exception.class` |

---

## 4. Key Endpoints Overview

### Authentication (`/api/auth`)
- `POST /api/auth/login`: Authenticate and obtain JWT token.
- `POST /api/auth/register`: Register a new user account.
- `POST /api/auth/refreshtoken`: Refresh expired access token.

### Comics (`/api/comics`)
- `GET /api/comics`: Paginated list of comics (filters: `genre`, `status`, `sort`, `page`, `size`).
- `GET /api/comics/{id}`: Detailed comic information with rating aggregate and chapters.
- `POST /api/comics`: Create a new comic (Admin/Translator).
- `PUT /api/comics/{id}`: Update comic metadata.
- `DELETE /api/comics/{id}`: Delete comic and cascade resources.

### Chapters (`/api/chapters`)
- `GET /api/chapters/comic/{comicId}`: List chapters for a comic.
- `GET /api/chapters/{id}`: Get chapter content & images.
- `POST /api/chapters`: Upload new chapter.

### Reading & Saved (`/api/reading`, `/api/saved`)
- `GET /api/reading`: Get current user's reading history.
- `POST /api/reading`: Update reading progress.
- `GET /api/saved`: Get user's bookmarked comics.
- `POST /api/saved/{comicId}`: Bookmark/unbookmark a comic.

### Cards, Packs & Gacha (`/api/cards`, `/api/packs`, `/api/gacha`, `/api/decks`)
- `GET /api/cards`: List cards in catalog.
- `GET /api/packs`: List available gacha packs.
- `POST /api/gacha/open/{packId}`: Open a pack and draw cards.
- `GET /api/decks`: Get user decks.
- `POST /api/decks`: Save/update deck configuration.
