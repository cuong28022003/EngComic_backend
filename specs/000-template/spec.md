# Feature Specification: [Feature Name]

> **Góc nhìn**: Business / Product Owner  
> **Mục đích**: Trả lời câu hỏi **"CẦN LÀM CÁI GÌ & TẠI SAO?" (WHAT & WHY)**

---

## 1. Tổng quan & Mục tiêu
- **Feature ID**: [001]
- **Feature Name**: [Tên tính năng]
- **Độ ưu tiên**: [P1 / P2 / P3]
- **Mục tiêu**: [Mô tả ngắn gọn giá trị nghiệp vụ của tính năng mang lại cho người dùng]

---

## 2. Phân quyền & Điều kiện tiên quyết (Preconditions)
- **Đối tượng sử dụng**: `USER` / `ADMIN` / `TRANSLATOR`
- **Điều kiện tiên quyết**: [VD: Đã đăng nhập, số dư coin >= giá trị gói, tài khoản ở trạng thái ACTIVE]

---

## 3. Danh sách User Stories & Tiêu chí Nghiệm thu (Acceptance Criteria)

### User Story 1: [Tiêu đề User Story chính] (Độ ưu tiên: P1)
> Là một **[Người dùng]**, tôi muốn **[thực hiện hành động]** để có thể **[đạt được kết quả mong muốn]**.

**Acceptance Criteria (BDD Format)**:
- **Scenario 1 (Thành công)**:
  - **Given**: Người dùng đã đăng nhập và có đủ điều kiện.
  - **When**: Gửi yêu cầu thực hiện hành động với dữ liệu hợp lệ.
  - **Then**: Hệ thống xử lý thành công, cập nhật trạng thái cơ sở dữ liệu và trả về kết quả 200 OK.
- **Scenario 2 (Lỗi thiếu dữ liệu / dữ liệu không hợp lệ)**:
  - **Given**: Người dùng gửi payload thiếu trường bắt buộc hoặc sai định dạng.
  - **When**: Gửi request tới hệ thống.
  - **Then**: Hệ thống từ chối và trả về lỗi `400 BAD_REQUEST` kèm thông báo chi tiết.
- **Scenario 3 (Lỗi vi phạm quy tắc nghiệp vụ)**:
  - **Given**: Người dùng không đủ số dư hoặc điều kiện nghiệp vụ không thỏa mãn.
  - **When**: Gửi request thực hiện.
  - **Then**: Hệ thống từ chối với lỗi `400 BAD_REQUEST` (hoặc `409 CONFLICT`), không thay đổi dữ liệu trong database.

---

## 4. Quy tắc Nghiệp vụ Bất biến (Business Invariants)
- [ ] **Rule 1**: [Ví dụ: Số dư tài khoản không được âm sau giao dịch]
- [ ] **Rule 2**: [Ví dụ: Mỗi tài khoản chỉ được nhận thưởng 1 lần trong ngày]
