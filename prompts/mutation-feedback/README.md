# Phản hồi mutation P4b

`tools/generate_p4.py --run N --mutation-feedback` bắt đầu từ bộ P4 cuối của cùng lần chạy. Các giới hạn riêng nằm ở `p4.p4b` trong cấu hình. Mỗi vòng dùng mẫu `generate.md`.

| Biến | Nội dung |
|---|---|
| `{{SPEC}}` | API đóng băng và các file đặc tả nghiệp vụ theo thứ tự của P3. |
| `{{SOURCE}}` | Các file Java trong package `shop` của đối tượng, theo tên file. |
| `{{TEST_NAMES}}` | Tên phương thức test đang có trong bộ P4b. |
| `{{FEEDBACK}}` | Mutant PIT và tự tạo có trạng thái SURVIVED hoặc NO_COVERAGE, trừ mutant tương đương đã ghi nhận. Gồm lớp, phương thức, dòng, toán tử, trạng thái và mô tả. Với mutant tự tạo, mẫu hiển thị cả đoạn gốc và đoạn thay thế. |

Mỗi phép đo chạy bộ hiện tại cộng một ứng viên qua JaCoCo, PIT và bộ mutant tự tạo. Chọn tham lam theo số định danh mutant còn sống được diệt thêm, giữ các mutant đã diệt ở lượt trước. Cách tính mutant bị diệt và điểm số theo `tools/svv/pit.py`. Khi hòa, ưu tiên thứ tự sinh. Với k ứng viên hợp lệ trong vòng, tối đa k(k+1)/2 phép đo sau mốc ban đầu. `mutation_score` của mỗi vòng là điểm của bộ sau khi chọn. Chi phí cộng dồn bao gồm P3 và P4. Ngân sách P4b chỉ tính token đầu vào và đầu ra phát sinh trong P4b.
