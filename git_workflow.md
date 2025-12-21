# Nội dung được sinh bởi Gemini 3 Pro
Chào bạn, làm việc nhóm trên GitHub đòi hỏi một quy trình (workflow) chặt chẽ để tránh việc code của người này đè lên người kia hoặc gây lỗi hệ thống (conflict).

Dưới đây là hướng dẫn chi tiết về **Quy trình Feature Branch** (Feature Branch Workflow) - mô hình phổ biến và hiệu quả nhất cho các nhóm làm việc chung.

---

### Giai đoạn 1: Chuẩn bị & Cập nhật (Trước khi code)

Trước khi bạn bắt đầu viết bất kỳ dòng code nào, hãy đảm bảo rằng bạn đang đứng trên nền tảng code mới nhất của cả nhóm.

**1. Chuyển về nhánh chính (thường là `main` hoặc `master`)**

```bash
git checkout main

```

**2. Kéo code mới nhất từ GitHub về máy**
Lệnh này giúp máy bạn cập nhật những thay đổi mà đồng đội đã đẩy lên trước đó.

```bash
git pull origin main

```

---

### Giai đoạn 2: Tạo nhánh riêng (Branching)

**Tuyệt đối không code trực tiếp trên nhánh `main`.** Hãy tạo một nhánh riêng cho tính năng bạn sắp làm.

**1. Tạo và chuyển sang nhánh mới**
Đặt tên nhánh rõ ràng (ví dụ: `feature/login-page`, `fix/header-bug`).

```bash
git checkout -b feature/ten-tinh-nang

```

---

### Giai đoạn 3: Code và Commit (Làm việc)

Tại đây, bạn thực hiện các thay đổi, viết code, sửa lỗi trên máy của mình.

**1. Kiểm tra trạng thái**
Xem file nào đã bị thay đổi.

```bash
git status

```

**2. Đưa file vào vùng chờ (Stage)**

```bash
git add . 
# Hoặc git add ten_file_cu_the.js nếu không muốn add tất cả

```

**3. Lưu thay đổi (Commit)**
Viết ghi chú (message) thật dễ hiểu để đồng đội biết bạn đã làm gì.

```bash
git commit -m "Thêm chức năng đăng nhập bằng Google"

```

> **Lưu ý:** Bạn có thể lặp lại bước Code -> Add -> Commit nhiều lần trong quá trình làm việc.

---

### Giai đoạn 4: Đồng bộ ngược (Quan trọng)

Trong lúc bạn đang code tính năng A, có thể đồng đội đã hoàn thành tính năng B và đẩy lên `main`. Bạn cần cập nhật code mới đó vào nhánh của mình để tránh xung đột sau này.

**1. Kéo code mới nhất từ main về nhánh của bạn**

```bash
git pull origin main

```

* Nếu không có lỗi: Tuyệt vời.
* Nếu có **Conflict**: Git sẽ báo lỗi. Bạn cần mở các file bị conflict, chọn code muốn giữ lại, xóa các ký tự lạ (`<<<<`, `====`, `>>>>`), sau đó `git add` và `git commit` lại.

---

### Giai đoạn 5: Đẩy code lên GitHub (Push)

Sau khi code xong và đã đồng bộ, hãy đẩy nhánh của bạn lên server.

```bash
git push origin feature/ten-tinh-nang

```

---

### Giai đoạn 6: Tạo Pull Request (PR) & Review

Đây là bước quan trọng nhất trong làm việc nhóm để đảm bảo chất lượng code.

1. Truy cập vào repository trên website GitHub.
2. Bạn sẽ thấy thông báo **"Compare & pull request"** hiện lên. Bấm vào đó.
3. **Tiêu đề & Mô tả:** Ghi rõ bạn đã làm gì, thay đổi những gì.
4. **Reviewers:** Tag tên đồng đội (người quản lý hoặc người làm cùng) vào mục bên phải để họ kiểm tra code.
5. Bấm **Create pull request**.

---

### Giai đoạn 7: Merge (Hợp nhất)

1. **Code Review:** Đồng đội sẽ vào xem code, để lại comment nhận xét.
2. **Sửa lỗi (nếu có):** Nếu đồng đội yêu cầu sửa, bạn sửa dưới máy local -> commit -> push lại lên nhánh cũ (PR sẽ tự động cập nhật, không cần tạo PR mới).
3. **Merge:** Khi mọi thứ đã ổn (Approved), người quản lý hoặc bạn sẽ bấm nút **Merge pull request** trên GitHub.
4. Code của bạn chính thức được hòa vào `main`.

---

### Tóm tắt các lệnh hay dùng (Cheat Sheet)

| Hành động | Lệnh Git |
| --- | --- |
| Lấy code mới nhất về | `git pull origin main` |
| Tạo nhánh mới | `git checkout -b ten-nhanh` |
| Xem trạng thái file | `git status` |
| Lưu thay đổi (tạm) | `git add .` |
| Đóng gói thay đổi | `git commit -m "Ghi chú"` |
| Đẩy lên server | `git push origin ten-nhanh` |

### 3 "Quy tắc vàng" khi làm việc nhóm

1. **Pull trước khi làm:** Luôn `git pull` ở nhánh chính trước khi tạo nhánh mới.
2. **Commit nhỏ:** Chia nhỏ các commit theo từng chức năng nhỏ, đừng đợi làm xong cả tuần mới commit một lần.
3. **Không force push:** Tuyệt đối tránh dùng `git push -f` trừ khi bạn hiểu rất rõ mình đang làm gì, vì nó có thể xóa bay code của người khác.

