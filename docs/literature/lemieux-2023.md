# CodaMosa: Escaping Coverage Plateaus in Test Generation with Pre-trained Large Language Models

## Thông tin thư mục

- **Khóa:** `lemieux-2023`
- **Tên đầy đủ:** CodaMosa: Escaping Coverage Plateaus in Test Generation with Pre-trained Large Language Models
- **Tác giả:** Caroline Lemieux, Jeevana Priya Inala, Shuvendu K. Lahiri, Siddhartha Sen
- **Năm:** 2023
- **Nơi công bố:** Proceedings of the 45th IEEE/ACM International Conference on Software Engineering (ICSE 2023), tr. 919–931
- **DOI / URL chính thức:** https://doi.org/10.1109/ICSE48619.2023.00085
- **Đã xác minh từ:** DBLP (`dblp.org`) cho số trang 919–931 và https://doi.org/10.1109/ICSE48619.2023.00085 / trang tác giả (`carolemieux.com`)
- **Ghi chú đối chiếu đề xuất:** Khớp tên, 4 tác giả, năm và hội nghị (ICSE 2023) trong mục 11 của đề xuất; bổ sung số trang (919–931) và DOI.

## Vấn đề

Kiểm thử dựa trên tìm kiếm (SBST, như Pynguin cho Python) thường rơi vào **ngưỡng bão hòa coverage (coverage plateau)** khi cần sinh đầu vào có cấu trúc hoặc tiền điều kiện phức tạp mà phép đột biến ngẫu nhiên gần như không thể tạo ra.

## Phương pháp

- **CodaMosa** là thuật toán lai ghép giữa SBST (thuật toán MIO/MOSA trong Pynguin) và LLM tiền huấn luyện (Codex).
- Khi quá trình tìm kiếm SBST bị chững lại không tăng coverage, CodaMosa chọn hàm còn thiếu coverage, gọi LLM sinh test mẫu cho hàm đó, phân tích và đưa các test mẫu vào quần thể tìm kiếm để SBST tiếp tục tiến hóa từ điểm khởi đầu có ích hơn.
- Có sử dụng tín hiệu coverage để kích hoạt LLM khi bị chững; **không** dùng phản hồi mutation. Oracle chủ yếu kế thừa cơ chế regression assertion của Pynguin (không kiểm chứng độc lập bằng đặc tả).

## Hệ thống / benchmark đánh giá

- Tập benchmark tuyển chọn từ **35 dự án Python** (nguồn Pynguin và BugsInPy), sau khi lọc các mô-đun lỗi hoặc dễ phủ 100% trong 1 phút còn lại **486 mô-đun** thuộc **27 dự án** (tóm lược đặc trưng mô-đun ở **Bảng I / Table I**).
- Ngân sách thời gian chạy: **10 phút** mỗi mô-đun; đối chứng với Pynguin thuần SBST và phương pháp chỉ dùng LLM.

## Kết quả chính

- Trên 486 mô-đun đánh giá (số phần trăm cụ thể theo từng bảng trong bài chưa xác minh đầy đủ từ toàn văn; theo tóm tắt chính thức): CodaMosa đạt coverage cao hơn có ý nghĩa thống kê trên nhiều mô-đun hơn đáng kể so với cả SBST thuần (Pynguin) và LLM thuần, đồng thời chỉ bị giảm coverage ở rất ít mô-đun.
- (Trong so sánh chéo sau này của CoverUp — Pizzorno & Berger 2025 — CodaMosa đạt trung vị line+branch coverage 47%, line 62%, branch 35% trên bộ đánh giá tương ứng.)

## Hạn chế

- Chủ yếu cải thiện độ bao phủ cấu trúc (line/branch); **không** đánh giá trực tiếp mutation score và **không** giải quyết bài toán tính đúng của oracle (assertion ghi nhận hành vi hiện tại hoặc dễ yếu/sai ngữ nghĩa).
- Một phần mã test do LLM sinh không phân tích cú pháp được sang biểu diễn nội bộ của Pynguin.

## Liên hệ với đề tài

- Cho thấy sức mạnh của việc dùng **tín hiệu coverage** để dẫn hướng LLM vượt qua các nhánh khó mà tìm kiếm ngẫu nhiên/tiến hóa bế tắc — cơ sở trực tiếp cho **P4 (Coverage-guided LLM)** và ý tưởng ghép cặp P3 → P4 trong đề tài.
- Cho thấy khoảng trống mà đề tài bổ sung: đánh giá trên **Java**, thêm **phản hồi mutation (P4b)** và kiểm chứng **oracle theo đặc tả độc lập**.
