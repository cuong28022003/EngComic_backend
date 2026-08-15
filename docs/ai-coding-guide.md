# CẨM NANG HƯỚNG DẪN LÀM VIỆC VỚI AI (AI CODING HANDBOOK)

> **Dành cho**: Developer làm việc cùng AI trên dự án `EngComic_backend`  
> **Mục tiêu**: Hướng dẫn từng bước cách ra lệnh, kiểm soát và dừng/uốn nắn AI khi lập trình để đạt hiệu quả cao nhất, không bị lỗi lan man hoặc phá vỡ cấu trúc code.

---

## 🧭 1. Sơ đồ Quy trình Tổng quan

```mermaid
graph TD
    Start[Bắt đầu công việc] --> CheckType{Loại công việc?}
    
    %% Nhánh công việc nhỏ
    CheckType -- Fix bug nhỏ / Thêm 1 field --> QuickPrompt[Prompt trực tiếp 1 câu<br>Ví dụ: Fix NPE ở ComicMapping.java]
    QuickPrompt --> QuickVerify[Chạy ./mvnw compile]
    
    %% Nhánh tính năng mới
    CheckType -- Tính năng mới / Module lớn --> Step0[Bước 0: Copy specs/000-template sang specs/00X-feature/]
    Step0 --> Step1[Bước 1: Chạy Prompt sinh spec.md]
    Step1 --> Step2[Bước 2: Chạy Prompt sinh plan.md]
    Step2 --> Step3[Bước 3: Chạy Prompt sinh tasks.md]
    Step3 --> Step4[Bước 4: Ra lệnh AI code từng task T001, T002...]
    Step4 --> StepVerify{AI có đi chệch hướng không?}
    StepVerify -- Có --> Intervene[Can thiệp & Chặn AI lại]
    StepVerify -- Không --> Done[Tick checkbox [x] & Chuyển task tiếp theo]
```

---

## 🛠️ 2. Hướng dẫn Từng Bước (Step-by-Step)

### 📌 Trường hợp A: Phát triển Tính năng Mới (New Feature)

#### Bước 0: Tạo thư mục Spec
Copy thư mục `specs/000-template/` thành `specs/001-[tên-tính-năng]/` (Ví dụ: `specs/001-daily-quest/`).

#### Bước 1: Yêu cầu AI viết `spec.md` (Đặc tả nghiệp vụ)
> **Prompt:**
> ```text
> Tôi muốn làm tính năng mới: "[Tên tính năng]".
> Mô tả: [Dán ý tưởng / yêu cầu sơ bộ vào đây].
> Hãy đọc AGENTS.md và tạo file specs/001-[tên-tính-năng]/spec.md với các User Stories (P1, P2), Tiêu chí nghiệm thu BDD (Given-When-Then) và Business Invariants.
> ```

#### Bước 2: Yêu cầu AI thiết kế `plan.md` (Kiến trúc kỹ thuật)
> **Prompt:**
> ```text
> Dựa vào file specs/001-[tên-tính-năng]/spec.md, hãy thiết kế file specs/001-[tên-tính-năng]/plan.md tuân thủ Clean Architecture (apis, businesses, databases) và MongoDB schema.
> ```

#### Bước 3: Yêu cầu AI lập danh sách `tasks.md` (Checklist thực thi)
> **Prompt:**
> ```text
> Dựa vào spec.md và plan.md vừa tạo, hãy viết file specs/001-[tên-tính-năng]/tasks.md chia nhỏ các task theo 4 Phase (databases -> businesses -> apis -> verification). Mỗi task phải có Where, How, Refs.
> ```

#### Bước 4: Thực thi từng task một (Execution)
> **Prompt chuẩn (Copy-paste):**
> ```text
> Hãy đọc specs/001-[tên-tính-năng]/ và thực hiện duy nhất Task T001 trong tasks.md.
> Sau khi xong:
> 1. Tự động tick [x] vào tasks.md.
> 2. Chạy './mvnw compile' để xác minh cú pháp.
> 3. DỪNG LẠI và báo cáo, không tự ý làm tiếp task tiếp theo.
> ```

---

### 📌 Trường hợp B: Fix Bug nhỏ hoặc Tinh chỉnh Code

Không cần tạo spec, chỉ cần prompt rõ ràng 3 yếu tố: **File bị lỗi + Hiện tượng lỗi + Yêu cầu sửa**.

> **Prompt mẫu:**
> ```text
> File 'src/main/java/mobile/mapping/ComicMapping.java' bị lỗi NullPointerException khi 'comic.getUploaderId()' là null.
> Hãy kiểm tra null an toàn trước khi gọi userService.findById(). Sau đó chạy './mvnw compile' để kiểm tra.
> ```

---

## 🛑 3. Khi nào CẦN DỪNG và CAN THIỆP AI?

| Hiện tượng bất thường | Nguyên nhân | Cách xử lý / Câu lệnh can thiệp |
| :--- | :--- | :--- |
| **AI tự ý sửa các file cũ ngoài phạm vi** | AI bị context drift, cố sửa toàn bộ dự án | 🛑 **DỪNG LẠI NGAY**: *"Hãy hoàn tác các thay đổi ngoài phạm vi. Bạn CHỈ ĐƯỢC PHÉP chỉnh sửa file trong phạm vi của Task T00X."* |
| **AI viết logic nghiệp vụ vào Controller** | Vi phạm nguyên tắc Thin Controller | 🛑 **NHẮC NHỞ**: *"Vi phạm AGENTS.md: Controller không được chứa logic nghiệp vụ. Hãy chuyển toàn bộ logic tính toán sang Interactor trong `businesses/`."* |
| **AI sinh mã Lombok dài dòng thay vì dùng Record (Java 21)** | AI quen code cũ | 🛑 **CHỈ ĐỊNH**: *"Hãy dùng cú pháp `record` của Java 21 cho Boundary Request/Response này thay vì tạo class thông thường."* |
| **AI báo lỗi compile lặp đi lặp lại 3 lần** | AI bị kẹt vòng lặp | 🛑 **DỪNG LẠI**: *"Dừng lại. Đọc kỹ file lỗi tại dòng X, kiểm tra xem đã import đúng thư viện chưa, không tự ý đoán mò."* |

---

## 💡 4. "Thần chú" 5 Nguyên tắc Vàng khi Prompt AI

1. **Nguyên tắc "1 Prompt = 1 Task Nhỏ"**: Không bao giờ bảo AI *"Hãy viết toàn bộ module X cho tôi"*.
2. **Nguyên tắc "Luôn có Checkpoint Compile"**: Luôn yêu cầu AI chạy `./mvnw compile` sau mỗi file/task.
3. **Nguyên tắc "Chỉ định File cụ thể"**: Đưa đường dẫn file chính xác (`Where: src/main/java/mobile/...`) để AI không tạo file lung tung.
4. **Nguyên tắc "Không nuốt lỗi"**: Cấm AI viết `try { ... } catch (Exception e) {}` rỗng.
5. **Nguyên tắc "Giữ nguyên Code cũ"**: Nhắc AI không refactor những file đang chạy ổn định của dự án.
