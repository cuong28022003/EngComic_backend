# 🧪 Automated Smoke Test Suite - EngComic_backend

Hệ thống **Black-Box Automation Smoke Test** cho `EngComic_backend` (chạy bằng `bash` + `curl.exe`).  
Smoke Test là bộ kiểm thử nhanh các **luồng sống còn (Critical Path)** của backend ngay sau khi khởi động app nhằm phát hiện lỗi sớm trong vòng **5–10 giây**.

---

## 🏬 1. Luồng Hoạt Động (Life-Cycle & Flow)

Quy trình hoạt động tuần tự qua **4 giai đoạn chính**:

```mermaid
sequenceDiagram
    autonumber
    actor Runner as Smoke Test (Bash)
    participant Server as Spring Boot (Port 8080)
    participant DB as MongoDB Atlas

    Note over Runner, Server: GIAI ĐOẠN 1: PREFLIGHT (Khởi động & Lấy Token)
    Runner->>Server: GET /swagger-ui.html (Ping server UP)
    Server-->>Runner: 302 Found (Server Sẵn sàng)
    Runner->>Server: POST /api/auth/register (Tạo User ngẫu nhiên kèm timestamp)
    Server->>DB: Lưu User mới vào DB
    Server-->>Runner: 200 OK (Đăng ký thành công)
    Runner->>Server: POST /api/auth/login (Đăng nhập lấy Token)
    Server-->>Runner: 200 OK (Trả về accessToken & userId)

    Note over Runner, Server: GIAI ĐOẠN 2: BẢO MẬT & PHÂN QUYỀN (Security Suite)
    Runner->>Server: GET /api/card/dashboard (KHÔNG truyền Bearer Token)
    Server-->>Runner: 403 Forbidden (Chặn thành công)

    Note over Runner, DB: GIAI ĐOẠN 3: LUỒNG NGHIỆP VỤ (Feature 001 Suite)
    Runner->>Server: POST /api/deck (Tạo Deck mới kèm Token)
    Server->>DB: Lưu Deck mới
    Server-->>Runner: 200 OK (Trả về deckId)
    
    Runner->>Server: POST /api/card (Tạo Flashcard thuộc deckId vừa tạo)
    Server->>DB: Lưu Card mới
    Server-->>Runner: 200 OK (Trả về cardId)

    Runner->>Server: GET /api/card/{cardId} (Kiểm tra chi tiết thẻ)
    Server-->>Runner: 200 OK

    Runner->>Server: GET /api/card/dashboard & /api/card/practice/due
    Server-->>Runner: 200 OK

    Runner->>Server: POST /api/card/{cardId}/practice-result (Ôn tập SRS Quality 4)
    Server->>DB: Tính toán SM-2 & Cập nhật nextReview
    Server-->>Runner: 200 OK

    Runner->>Server: POST /api/card/batch-import (Import JSON từ vựng)
    Server-->>Runner: 200 OK

    Runner->>Server: POST /api/pending-item & GET /api/pending-item/generate-prompt
    Server-->>Runner: 201 Created & 200 OK

    Note over Runner: GIAI ĐOẠN 4: BÁO CÁO (Summary Report)
    Runner-->>Runner: Tổng hợp PASS / FAIL -> In console màu
```

---

## 📂 2. Cấu Trúc File & Trách Nhiệm

```text
scripts/smoke-test/
├── run-smoke.sh             # [ENTRY POINT] Điều phối toàn bộ vòng đời test
├── config.env               # Cấu hình môi trường (BASE_URL, thông tin tài khoản mẫu)
├── README.md                # Tài liệu mô tả kiến trúc & luồng hoạt động
│
├── lib/
│   ├── common.sh            # Hàm helper: log màu, đếm kết quả, hàm wrapper `http_step`
│   └── preflight.sh         # Ping kiểm tra server & tự động đăng ký/đăng nhập lấy JWT Token
│
└── suites/
    ├── security.sh          # Suite kiểm tra bảo mật (truy cập trái phép bị chặn 403)
    └── feature-001.sh       # Suite nghiệp vụ: Deck -> Card -> SRS Ôn tập -> Batch Import -> Collector
```

---

## 🛠️ 3. Cơ Chế Chi Tiết của Hàm `http_step`

Hàm `http_step` trong `common.sh` là trái tim của việc gọi và kiểm tra:

```bash
http_step "Tên bước" METHOD PATH [JSON_BODY] [EXPECTED_HTTP_CODE] [TOKEN]
```

1. **Chuẩn bị Header:** Nếu có `$TOKEN`, tự động gắn `-H "Authorization: Bearer $TOKEN"`.
2. **Gửi HTTP Request:** Dùng `curl.exe` truyền JSON payload qua stdin để tương thích tối đa trên cả Windows / Git Bash / Linux / WSL.
3. **Bóc tách Response:** 
   - Dòng cuối cùng chứa HTTP Status Code (`%{http_code}`).
   - Các dòng trên là JSON Response Body (lưu vào biến `$LAST_BODY`).
4. **Đánh giá Assert:** So sánh Status Code trả về với `$EXPECTED_HTTP_CODE`:
   - Nếu khớp: In `[PASS]` màu xanh và tăng biến `$PASS`.
   - Nếu lệch: In `[FAIL]` màu đỏ, in ra Response Body lỗi và tăng biến `$FAIL`.

---

## 🚀 4. Hướng Dẫn Chạy Smoke Test

### Bước 1: Khởi động Spring Boot Backend
Đảm bảo ứng dụng backend đang chạy tại cổng `8080`:
```powershell
./mvnw spring-boot:run
```

### Bước 2: Thực thi Smoke Test
Mở terminal **Git Bash** (trên Windows) hoặc WSL và chạy:
```bash
./scripts/smoke-test/run-smoke.sh
```

---

## ➕ 5. Cách Thêm Test Suite Cho Tính Năng Mới

Khi làm các feature tiếp theo (ví dụ: `comic`, `novel`, `gacha`), chỉ cần:
1. Tạo file mới trong `suites/feature-xxx.sh` chứa hàm `run_feature_xxx_suite()`.
2. Dùng các lệnh `http_step` để mô tả chuỗi API cần kiểm tra.
3. Import (`source`) và gọi hàm đó trong `run-smoke.sh`.
