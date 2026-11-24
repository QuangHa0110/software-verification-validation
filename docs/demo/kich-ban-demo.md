# Demo sinh test cho giỏ hàng và thanh toán

Chạy PowerShell từ gốc worktree bằng JDK 17, Maven và môi trường Python của dự án. Chuẩn bị sẵn các phụ thuộc Maven trong local repository khi trình diễn không có mạng.

```powershell
$py = 'python'
& $py experiments/scripts/demo.py --no-pause --dry-run
& $py experiments/scripts/demo.py --offline
```

Lệnh thứ hai dừng chờ Enter giữa các bước. Thêm `--no-pause` để chạy liền. Orchestrator chạy **`& $py experiments/scripts/demo.py`** để trình diễn có LLM thật ở bước 2 và 6. Nếu phải chuyển offline giữa buổi, chạy từng lệnh bên dưới theo thứ tự. Bước 3 cần chọn cùng chế độ với bước 2. Bước 4 đọc báo cáo của bước 3, bước 7 dùng kết quả bước 5 và 6.

### 1 · Chương trình đối tượng (1 phút)

**Mục tiêu.** Theo một đơn hàng từ lúc thêm sản phẩm đến lúc tính phí, thanh toán hoặc hủy. Chỉ ra nơi đặc tả quy định giá trị kỳ vọng.

**Lệnh** `& $py experiments/scripts/demo.py --step 1`

**Quan sát.** Mục lục đặc tả dẫn tới API, quy tắc giỏ hàng, giảm giá, vận chuyển và vòng đời đơn. **Dự phòng.** Đọc trực tiếp `docs/spec/README.md` tại máy.

### 2 · Sinh test JUnit từ một prompt (1 phút offline, vài phút trở lên khi gọi LLM)

**Mục tiêu.** Đọc một assertion và liên hệ với hợp đồng API. Phân biệt ứng viên được sinh với test đã vượt bộ lọc.

**Lệnh** `& $py experiments/scripts/demo.py --step 2`

**Quan sát.** Thư mục bộ P3, thống kê lọc và mã Java của một test được giữ. **Dự phòng.** Chạy `& $py experiments/scripts/demo.py --step 2 --offline` để đọc suite P3 run-01 cùng log tương ứng trong `results/llm-logs/p3-direct-llm/`.

### 3 · Thực thi bằng JUnit (1 phút)

**Mục tiêu.** Xác nhận bộ P3 chạy trên chương trình đối tượng. **Lệnh** `& $py experiments/scripts/demo.py --step 3`

**Quan sát.** Surefire báo số lượt test, lỗi, thất bại, bỏ qua. Tham số hóa có thể tạo nhiều lượt chạy từ một phương thức. **Dự phòng.** Dùng `& $py experiments/scripts/demo.py --step 3 --offline` với suite P3 đã lưu. Nếu thiếu Maven, đọc trường `tests` trong `results/comparison/raw/p3-direct-llm/run-01.json`.

### 4 · Coverage ban đầu của P3 (1 phút)

**Mục tiêu.** Giải thích tử số và mẫu số của từng chỉ báo. **Lệnh** `& $py experiments/scripts/demo.py --step 4`

**Quan sát.** Số dòng, nhánh và phương thức đã phủ trên tổng số. Mở `.work/demo/reports/direct/index.html` để chuyển từ package đến mã nguồn. **Dự phòng.** Mở `results/coverage/p3-direct-llm/run-01/index.html`.

### 5 · Tìm nhánh chưa phủ trên bộ smoke (1 phút)

**Mục tiêu.** Chuyển điểm xuất phát sang smoke vì P3 chính thức đã chạm trần. Chọn một dòng điều kiện và hỏi đường đi nào còn thiếu. **Lệnh** `& $py experiments/scripts/demo.py --step 5`

**Quan sát.** JUnit chạy smoke, JaCoCo cho coverage và đoạn mã có chú thích `branches covered=… missed=…`. **Dự phòng.** Khi thiếu Maven, dùng `results/coverage/p3w-weak-llm/run-01/index.html` để minh họa một bộ khác, nói rõ đây là P3w.

### 6 · Đưa khoảng trống coverage cho LLM (2–5 phút offline, trực tiếp tùy quota và số ứng viên)

**Mục tiêu.** Theo một vòng đo, tạo prompt, lọc ứng viên rồi chọn test tăng coverage. **Lệnh** `& $py tools/generate_p4.py --run 1 --demo --start-suite test-suites/smoke` (runner gọi cùng lệnh).

**Quan sát.** Mỗi vòng in coverage trước/sau và số test giữ lại. Cuối cùng có lý do dừng và `.work/demo/p4-coverage-guided/run-01/generation.json`. **Dự phòng.** Chạy `& $py experiments/scripts/demo.py --step 6 --offline`. Phản hồi đã ghi cho **P4w run-01** được phát lại theo thứ tự từ `results/llm-logs/p4w-coverage-guided/run-01/calls.jsonl`. Các phản hồi này vốn được sinh cho P3w. Vòng demo vẫn đo smoke, dựng prompt, lọc và đo lại ứng viên trên máy.

### 7 · Chạy lại và so trên cùng điểm xuất phát (1 phút)

**Mục tiêu.** Kiểm chứng bộ đã chọn bằng một lần chạy JUnit mới. **Lệnh** `& $py experiments/scripts/demo.py --step 7`

**Quan sát.** Hai bảng đếm của smoke ban đầu và smoke cộng test P4, cùng mẫu số. Có thể mở `.work/demo/reports/after/index.html` để xem từng dòng. **Dự phòng.** Trình bày cặp P3w/P4w đã lưu trong `results/comparison/coverage-before-after-weak-llm.md` và ghi rõ nguồn.

### 8 · Đọc kết quả mutation testing (1 phút)

**Mục tiêu.** Phân biệt chạy qua một nhánh với phát hiện thay đổi hành vi tại nhánh đó. **Lệnh** `& $py experiments/scripts/demo.py --step 8`

**Quan sát.** Bản ghi P3 run-01 ở M5 cho biết tổng mutant, số bị diệt, số tương đương, điểm và phân rã theo toán tử. **Dự phòng.** Đọc trực tiếp mục `mutation` trong `results/comparison/raw/p3-direct-llm/run-01.json`.

### 9 · So sánh các phương pháp (2 phút)

**Mục tiêu.** Đọc cỡ mẫu trước khi so trung bình độ phủ nhánh và điểm đột biến. **Lệnh** `& $py experiments/scripts/demo.py --step 9`

**Quan sát.** Bảng M5 cho đối chứng, chuỗi chính và hai chuỗi bóc tách. Phân biệt P1 đo một lần với phân bố qua các lần sinh. **Dự phòng.** Mở `results/comparison/summary.md`, rồi `pairwise.md` trong cùng thư mục nếu cần kiểm định.

### 10 · Hạn chế và đóng góp (1 phút)

**Mục tiêu.** Giải thích vì sao trần của P3 giới hạn kết luận về phản hồi. **Lệnh** `& $py experiments/scripts/demo.py --step 10`

**Quan sát.** Kết luận tách minh họa smoke khỏi thực nghiệm M5, nêu vai trò của oracle, bộ lọc và quy trình tái lập. **Dự phòng.** Dùng các slide cuối của `docs/demo/slides.md` cùng bảng M5 đã lưu.
