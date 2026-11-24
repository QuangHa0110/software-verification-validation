---
marp: true
theme: default
paginate: true
size: 16:9
style: |
  section { font-family: "Segoe UI", sans-serif; color: #183549; }
  h1, h2, strong { color: #005c62; }
  section p > em:only-child { font-size: 0.7em; }
---

# Sinh test JUnit bằng mô hình ngôn ngữ

**Khi nào phản hồi coverage và mutation giúp một bộ test phát hiện thêm lỗi?**

Thực nghiệm trên chương trình giỏ hàng và thanh toán Java 17

---

## Đi qua điều kiện chưa đủ để phát hiện lỗi

Một test đặt hàng có thể đi qua đoạn tính phí vận chuyển nhưng chỉ kiểm tra đơn hàng được tạo thành công.

Nếu phép tính phụ phí bị thay đổi, assertion nào sẽ thất bại?

**JaCoCo** cho biết đường đi đã thực thi. **Mutation testing** kiểm tra liệu bộ test có phân biệt được hành vi sau khi mã bị thay đổi.

---

## Sáu câu hỏi nghiên cứu

| | Câu hỏi | Chỉ báo |
|---|---|---|
| RQ1–2 | LLM phủ đến đâu? Phản hồi giúp tăng bao nhiêu? | LINE, BRANCH và chênh lệch P3 → P4 |
| RQ3–4 | LLM phát hiện lỗi tốt hơn random? Phản hồi nào giúp ích? | Mutation score, ứng viên giữ lại |
| RQ5 | Số test, coverage và khả năng diệt mutant liên hệ thế nào? | Tương quan hạng Spearman |
| RQ6 | Mức cải thiện đi kèm chi phí nào? | Thời gian sinh/chạy, số lượt gọi và token |

---

## Chương trình đối tượng

**Giỏ hàng → giảm giá → phí giao hàng → vòng đời đơn**

Những điểm đáng thử gồm số lượng vượt tồn kho, mức trần giảm giá, ngưỡng phụ phí và yêu cầu hủy đơn theo thời điểm.

Hai bản Java cùng thực hiện một đặc tả. `subject/` là đối tượng đo. `reference/` cung cấp hành vi để kiểm tra giá trị kỳ vọng.

---

## Hai mốc đối chứng

| P1 · Thủ công | P2 · Ngẫu nhiên |
|---|---|
| Viết từ đặc tả, phân vùng đầu vào và chọn giá trị biên. | Sinh đầu vào từ seed và lấy kết quả kỳ vọng từ reference. |
| Một bộ cố định cho biết người thiết kế test đạt tới đâu. | Nhiều seed cho biết kết quả thay đổi thế nào giữa các lần sinh. |

Hai bộ đi qua cùng chương trình và hệ đo với các bộ do LLM sinh.

---

## P3 sinh một lần từ đặc tả và mã nguồn

1. Ghép hợp đồng API, đặc tả nghiệp vụ và mã `subject` vào prompt.
2. Yêu cầu mô hình trả về các phương thức JUnit có assertion.
3. Tách các khối Java, loại trùng, kiểm tra biên dịch và chạy trên reference.

Nhật ký giữ cả prompt, phản hồi và mức dùng token. Metadata cho biết ứng viên bị loại ở đâu.

---

## P4 chọn test lấp khoảng trống coverage

**Bộ P3 cùng run → JaCoCo → vị trí chưa phủ → LLM → ứng viên**

P4 đo phần tăng thêm của từng ứng viên đã qua bộ lọc. Sau mỗi lần chọn, phần đóng góp của ứng viên tiếp theo được tính trên bộ hiện tại.

Vòng lặp dừng theo mục tiêu coverage, ngân sách, giới hạn vòng hoặc số vòng liên tiếp không tăng.

---

## P4b nhắm vào mutant còn sống

P4b bắt đầu từ bộ P4 cùng run. Phản hồi mô tả vị trí, toán tử và thay đổi còn lọt qua assertion.

Ma trận diệt mutant cho biết test nào phát hiện thay đổi nào. Thuật toán chọn test diệt thêm nhiều mutant nhất và đo bộ cuối.

Một test mới có thể phát hiện lỗi ngay trên nhánh bộ cũ đã đi qua.

---

## Kiến trúc thực nghiệm

| Thành phần | Trách nhiệm | Dấu vết để kiểm tra |
|---|---|---|
| Python, prompt và bộ sinh | Ghép ngữ cảnh, gọi mô hình, lọc test | Log lời gọi, metadata của bộ test |
| Maven, JUnit, reference | Biên dịch và đối chiếu oracle | Báo cáo Surefire |
| Subject, JaCoCo, PIT và mutant tự tạo | Đo đường đi và khả năng phát hiện thay đổi | Báo cáo theo dòng và theo mutant |
| `measure.py` → `analyze.py` | Lưu phép đo từng run, tổng hợp các phương pháp | JSON thô, bảng và hình M5 |

---

## Từ một lần chạy đến bảng so sánh

Mỗi run dùng thư mục Maven riêng. LINE đại diện độ phủ câu lệnh, BRANCH đo các nhánh, METHOD đo phương thức.

**Mutation score = số mutant bị diệt / (tổng mutant − số tương đương) × 100%.**

Giữ nhãn run khi so trước/sau. Báo cáo cả trung vị và IQR, dùng Mann–Whitney U và độ lớn hiệu ứng Â₁₂ cho các nhóm đủ mẫu.

---

## Chuỗi chính đạt trần ngay ở P3

| Phương pháp | Số run | Branch trung bình | Mutation score trung bình |
|---|---:|---:|---:|
| P1 | 1 | 100,00% | 99,17% |
| P2 | 10 | 76,78% | 64,33% |
| P3 / P4 / P4b | 10 mỗi phương pháp | 100,00% | 100,00% |

Trên module này, phép đo không còn dư địa để định lượng phần tăng sau P3.

_Nguồn M5, làm tròn hai chữ số. [SỐ LIỆU: results/comparison/summary.md]_

---

## Bóc tách D-024 · Bỏ mã nguồn khỏi prompt ban đầu

**P3r** chỉ nhận đặc tả và API. **P4r** nhận đoạn mã quanh vị trí chưa phủ. **P4br** tiếp tục từ P4r với phản hồi mutant.

Giữ nguyên mô hình và ngân sách. Độ phủ nhánh và mutation score trung bình của P3r vẫn đạt 100%. Hạn chế ngữ cảnh ban đầu chưa tạo khoảng trống trên đối tượng này.

_Nguồn M5. [SỐ LIỆU: results/comparison/summary.md]_

---

## Bóc tách D-025 · Đổi sang mô hình yếu hơn

Giữ đủ ngữ cảnh, dùng `gemini-3.8-flash-low`. Trung bình trên 10 run của mỗi phương pháp:

| | Branch | Mutation score |
|---|---:|---:|
| P3w · Sinh trực tiếp | 99,66% | 95,00% |
| P4w · Phản hồi coverage | 99,94% | 95,00% |
| P4bw · Phản hồi mutation | 99,94% | 98,50% |

_Nguồn M5, làm tròn hai chữ số. [SỐ LIỆU: results/comparison/summary.md]_

---

## Phần tăng phụ thuộc vào tín hiệu phản hồi

Trong chuỗi mô hình yếu, P4w cải thiện trung bình độ phủ nhánh. Điểm đột biến trung bình tăng ở bước P4bw khi mô hình nhận mô tả mutant còn sống.

Để trả lời RQ5–6, đọc tương quan trong `rq5-correlation.csv`, rồi đối chiếu số test hợp lệ, thời gian sinh và token trong bảng tổng hợp. Chi phí tiền tệ thiếu được ghi là thiếu.

_[SỐ LIỆU: results/comparison/summary.md]_

---

## Trình diễn vòng phản hồi bằng smoke

**Bước 2–4** · Xem test P3, chạy JUnit và đọc coverage ban đầu.

**Bước 5–7** · Đo lại từ smoke, chọn một điều kiện còn thiếu nhánh, quan sát P4 giữ test rồi chạy lại chính bộ vừa sinh.

Ở chế độ offline, câu trả lời đến từ log P4w đã lưu. JUnit, bộ lọc và JaCoCo vẫn chạy thật. Số liệu của minh họa nằm riêng dưới `.work/demo/`.

---

## Phạm vi kết luận

Một module trong bộ nhớ với đặc tả chi tiết tạo điều kiện thuận lợi cho cả người viết test và LLM. Hệ thống phân tán hay API có trạng thái ngoài tiến trình sẽ đặt ra bài toán khác.

Hiệu ứng trần che khuất lợi ích tiềm năng của phản hồi. Mutation score còn phụ thuộc tập toán tử và việc xác định mutant tương đương. P1 chỉ có một bộ cố định.

---

## Đóng góp

- Một đối tượng Java có đặc tả và bản tham chiếu độc lập để kiểm tra oracle.
- Hai vòng phản hồi có thể truy từ nhánh hoặc mutant tới test được giữ.
- Bộ dữ liệu theo từng run và hai thí nghiệm bóc tách giúp xác định điều kiện xuất hiện mức tăng.

Khi coverage gần bão hòa, khả năng diệt mutant vẫn phân biệt được các bộ test trong chuỗi mô hình yếu.
