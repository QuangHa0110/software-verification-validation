# Bộ prompt P3 – Direct LLM (`prompts/direct/`)

- **Phiên bản:** `1.0`
- **Phương pháp:** `p3-direct-llm` (gửi toàn bộ ngữ cảnh một lần duy nhất cho LLM, không phản hồi lặp).
- **Script dựng và gọi:** `tools/generate_p3.py`

## 1. Cấu trúc và mục đích từng file

| File | Mục đích |
|---|---|
| `system.md` | Vai trò kỹ sư kiểm thử Java 17 / JUnit 5, luật cấm dùng công cụ/chạy lệnh/đọc file, nguyên tắc lấy đặc tả làm chuẩn oracle, mục tiêu phủ tối đa câu lệnh và nhánh, quy ước định dạng đầu ra tương thích `tools/svv/test_blocks.py` (mỗi test một khối ```` ```java ````, 1 phương thức tự chứa, không hàm phụ, không trường lớp, không gọi `now()`, không ấn định số lượng test cố định). |
| `generate.md` | Mẫu prompt chính chứa các quy tắc oracle bổ sung chốt tại M1, ba vị trí thay thế `{{API}}`, `{{SPEC}}`, `{{SOURCE}}` và nhắc lại định dạng đầu ra. |
| `README.md` | Tài liệu mô tả phiên bản, cấu trúc, biến thay thế, thứ tự ghép file cố định và lý do xử lý `docs/spec/open-questions.md`. |

Khi dựng prompt đầy đủ, `tools/generate_p3.py` ghép `system.md`, dòng phân cách `\n\n---\n\n`, rồi phần `generate.md` sau khi đã thay thế đầy đủ cả ba biến. Mọi nội dung đều được chuẩn hóa xuống dòng LF (`\n`) trước khi tính hash SHA-256 để bảo đảm tái lập giữa Windows và Linux.

## 2. Biến thay thế trong `generate.md` và thứ tự ghép cố định

| Biến | Nguồn dữ liệu | Thứ tự cố định |
|---|---|---|
| `{{API}}` | `docs/spec/api.md` | Một file duy nhất (hợp đồng chữ ký đóng băng tại M1). |
| `{{SPEC}}` | Các file `docs/spec/*.md` trừ `api.md` và `open-questions.md` | Thứ tự logic cố định: `README.md`, `domain.md`, `cart.md`, `pricing.md`, `shipping.md`, `order.md`, `branches.md`; nếu sau này có thêm file `.md` mới trong `docs/spec/` thì nối tiếp theo thứ tự từ điển tên file. |
| `{{SOURCE}}` | Toàn bộ `subject/src/main/java/shop/*.java` | Sắp xếp cố định theo thứ tự từ điển tên file (`Cart.java`, `CartItem.java`, ..., `ShippingCalculator.java`), mỗi file nằm trong một khối ```` ```java ```` có tiêu đề đường dẫn tương đối. |

## 3. Lý do chọn và xử lý `docs/spec/open-questions.md`

- File `docs/spec/open-questions.md` **không** được nạp nguyên văn vào `{{SPEC}}` đúng theo yêu cầu của T10, tránh lặp lại khung bảng hỏi–đáp nội bộ và dư thừa văn bản với các file đặc tả chính.
- Tuy nhiên, một số quyết định làm rõ tại M1 trong `open-questions.md` ảnh hưởng trực tiếp đến tính đúng của test oracle khi kiểm tra đối chiếu giữa `subject/` và `reference/` — đặc biệt là:
  - **Quyết định #12:** không quy định đồng nhất tham chiếu (`assertSame`) của `List<CartItem>` hay `CartItem` giữa hai lần đọc; chỉ đối chiếu dữ liệu qua getter và tham chiếu `Product` khi cần.
  - **Quyết định #10:** dịch vụ tiêm vào `OrderService` được gọi theo thứ tự `calculateDiscount` → `calculate` tối đa một lần mỗi hàm và truyền nguyên ngoại lệ.
  - **Quyết định #1, #4, #6, #7, #8, #9:** giữ nguyên chuỗi không `trim()` khi lưu, không kiểm tra trùng ID đơn hàng, mã giảm giá tái sử dụng được, phạm vi kiểm tra của `Order(...)` và `Order.setStatus(...)`, giữ nguyên độ chính xác `LocalDateTime`.
- Vì vậy, các quy tắc ảnh hưởng tới oracle từ `open-questions.md` được chắt lọc ngắn gọn thành mục **Quy tắc oracle bổ sung (chốt tại M1)** trong `generate.md`. Cách này vừa giữ `{{SPEC}}` sạch (không chứa file `open-questions.md`), vừa ngăn LLM sinh ra các assertion vi phạm hợp đồng ngầm (như `assertSame` trên danh sách chụp hoặc kiểm tra chuỗi message ngoại lệ).

## 4. Truy vết phiên bản và tái lập

Mỗi lần chạy `tools/generate_p3.py` ghi vào `test-suites/p3-direct-llm/run-XX/generation.json` khóa `prompt` gồm:

- `version`: phiên bản bộ prompt (`1.0`).
- `system_sha256`: SHA-256 của `prompts/direct/system.md` (LF, UTF-8).
- `generate_sha256`: SHA-256 của `prompts/direct/generate.md` (LF, UTF-8).
- `template_sha256`: SHA-256 tổ hợp hai file mẫu.
- `rendered_sha256`: SHA-256 của toàn bộ prompt sau khi thay thế biến.
- `chars`, `spec_files`, `source_files`: kích thước ký tự và danh sách chính xác các file đã nạp theo đúng thứ tự.
