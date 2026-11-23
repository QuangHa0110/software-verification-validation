# Phản hồi coverage P4

`tools/generate_p4.py --run N` lấy bộ P3 cùng số lần chạy làm đầu vào. Mỗi vòng dùng mẫu `generate.md`.

| Biến | Nội dung |
|---|---|
| `{{SPEC}}` | API đóng băng, tiếp theo là đặc tả nghiệp vụ theo thứ tự của P3. |
| `{{SOURCE}}` | Các file Java của đối tượng trong package `shop`, theo tên file. |
| `{{TEST_NAMES}}` | Tên phương thức test kế thừa và phương thức đã giữ ở các vòng trước. |
| `{{FEEDBACK}}` | Dòng chưa phủ và nhánh còn thiếu, gộp theo phương thức, kèm số nhánh đã phủ/chưa phủ và ngữ cảnh có số dòng. |

Bộ lọc kiểm tra biên dịch, oracle và trùng thân hàm trước khi chọn. Mỗi lượt chọn chạy JaCoCo với bộ hiện tại cộng riêng từng ứng viên. Giữ ứng viên tăng tổng LINE + BRANCH nhiều nhất, dùng thứ tự sinh khi hòa. Đo lại đóng góp của các ứng viên còn tiềm năng sau mỗi lần giữ. Với k ứng viên hợp lệ trong một vòng, tối đa k(k+1)/2 lần đo ngoài phép đo ban đầu. Báo cáo tốt nhất được dùng làm mốc cho lượt tiếp theo.

`iterations[].kept_tests` đếm test mới được giữ trong vòng, `valid` đếm ứng viên qua bộ lọc và `tokens` là input + output của lời gọi trong vòng. `generation_s` và `llm` ở cấp gốc cộng cả chi phí kế thừa. `incremental` chỉ tính phần phát sinh của lần chạy hiện tại và là phần được so với ngân sách token.

`--demo` lưu bộ test dưới `.work/demo/<phương pháp>/run-XX/`. Có thể chọn thư mục bộ test bằng `--output`. Các cuộc gọi demo được đánh dấu bằng tiền tố `demo-` trong trường `purpose` của `results/llm-logs/<phương pháp>/run-XX/calls.jsonl`. `--dry-run` đo bộ đầu vào và in prompt đầu tiên.
