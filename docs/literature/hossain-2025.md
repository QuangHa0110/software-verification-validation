# TOGLL: Correct and Strong Test Oracle Generation with LLMs

## Thông tin thư mục

- **Khóa:** `hossain-2025`
- **Tên đầy đủ:** TOGLL: Correct and Strong Test Oracle Generation with LLMs
- **Tác giả:** Soneya Binta Hossain, Matthew B. Dwyer
- **Năm:** 2025 (preprint arXiv công bố tháng 5/2024)
- **Nơi công bố:** Proceedings of the 47th IEEE/ACM International Conference on Software Engineering (ICSE 2025), Ottawa, ON, Canada, tr. 1475–1487
- **DOI / URL chính thức:** https://doi.org/10.1109/ICSE55347.2025.00098 (DBLP: https://dblp.org/rec/conf/icse/HossainD25)
- **Đã xác minh từ:** https://dblp.org/rec/conf/icse/HossainD25, https://doi.org/10.1109/ICSE55347.2025.00098 và bản arXiv đọc qua kết quả tìm kiếm
- **Ghi chú:** Công trình bổ sung (2025), không có trong mục 11 đề xuất.

## Vấn đề

Các bộ sinh test oracle bằng mạng nơ-ron trước đây (như TOGA) thường có tỷ lệ dương tính giả (false positive) cao — sinh nhiều assertion sai so với hành vi đúng — hoặc sinh assertion quá yếu nên không phát hiện được lỗi thực tế và mutant.

## Phương pháp

- **TOGLL** tập trung riêng vào bài toán **sinh test oracle** (gồm cả assertion oracle lẫn exception oracle) cho các tiền tố kiểm thử Java bằng cách tinh chỉnh (fine-tune) các LLM mã nguồn cỡ nhỏ–trung bình:
  - Thử nghiệm tinh chỉnh **7 code LLM** (kích thước từ **110M đến 2,7B** tham số, gồm CodeGPT, CodeParrot, CodeGen, PolyCoder, Phi-1, v.v.) với **6 dạng prompt khác nhau** trên tập dữ liệu huấn luyện từ **SF110**.
  - Tổ hợp mô hình và prompt tinh chỉnh tốt nhất tạo thành **TOGLL**.
- Đánh giá đồng thời cả **tính đúng (correctness)** của oracle lẫn **độ mạnh (strength)** thông qua khả năng tiêu diệt mutant và phát hiện lỗi thật. Công trình **không** sinh tiền tố đầu vào theo vòng lặp coverage hay mutation.

## Hệ thống / benchmark đánh giá

- **SF110** (110 dự án Java mã nguồn mở) để huấn luyện/tinh chỉnh.
- **25 dự án Java lớn chưa thấy khi huấn luyện** để kiểm tra khả năng khái quát và độ mạnh mutation.
- **Defects4J** để đánh giá khả năng phát hiện lỗi thực tế.
- Đối chứng với **TOGA** (bộ sinh oracle nơ-ron tiêu biểu) và **EvoSuite**.

## Kết quả chính

- Theo tóm tắt và kết quả đánh giá chính của bài (số hiệu bảng cụ thể chưa xác minh từ toàn văn):
  - **Tính đúng của oracle:** TOGLL sinh số **assertion oracle đúng nhiều gấp 3,8 lần** và **exception oracle đúng nhiều gấp 4,9 lần** so với TOGA.
  - **Độ mạnh phát hiện mutant và lỗi:** TOGLL phát hiện được **1.023 mutant duy nhất** mà EvoSuite không tiêu diệt được — cao gấp khoảng **10 lần** so với số mutant tương tự mà TOGA phát hiện; đồng thời vượt rõ rệt các mô hình đối chứng nơ-ron trước đó về phát hiện lỗi thực tế trên **Defects4J**.

## Hạn chế

- Chỉ sinh phần **oracle** cho tiền tố test đã có sẵn, không giải quyết đồng thời bài toán sinh đầu vào bao phủ nhánh khó.
- Yêu cầu bước **fine-tune** trên tập dữ liệu lớn (SF110) và vẫn có tỷ lệ oracle sai nhất định nên chưa thay thế hoàn toàn nhu cầu đối chiếu với đặc tả khi cần đảm bảo tuyệt đối.

## Liên hệ với đề tài

- Nhấn mạnh trực diện luận điểm cốt lõi của đề tài: **tính đúng (correctness)** và **độ mạnh diệt mutant (strength)** của **test oracle** là hai tiêu chí riêng biệt mà độ bao phủ dòng/nhánh không đo được.
- Trong khi TOGLL giải bài toán bằng fine-tune mô hình riêng để sinh oracle, đề tài sử dụng LLM đa dụng qua API kèm đặc tả đầu vào/đầu ra và **chứng minh tường minh tính đúng của oracle** bằng **chương trình tham chiếu `reference/` độc lập**, đồng thời dùng **P4b (phản hồi mutation)** để nâng độ mạnh diệt mutant.
