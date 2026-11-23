# EvoSuite: Automatic Test Suite Generation for Object-Oriented Software

## Thông tin thư mục

- **Khóa:** `fraser-2011`
- **Tên đầy đủ:** EvoSuite: Automatic Test Suite Generation for Object-Oriented Software
- **Tác giả:** Gordon Fraser, Andrea Arcuri
- **Năm:** 2011
- **Nơi công bố:** Proceedings of the 19th ACM SIGSOFT Symposium and the 13th European Conference on Foundations of Software Engineering (ESEC/FSE '11), tr. 416–419 (bài trình diễn công cụ / tool demonstration)
- **DOI / URL chính thức:** https://doi.org/10.1145/2025113.2025179
- **Đã xác minh từ:** https://doi.org/10.1145/2025113.2025179 và trang công cụ EvoSuite (`evosuite.org`)
- **Ghi chú đối chiếu đề xuất:** Khớp tên, tác giả, năm và hội nghị (ESEC/FSE 2011) trong mục 11 của đề xuất; bổ sung số trang (416–419) và DOI.

## Vấn đề

Các công cụ sinh test dựa trên tìm kiếm (SBST) trước đó thường nhắm vào từng mục tiêu coverage riêng lẻ (ví dụ từng nhánh một), dễ lãng phí ngân sách tìm kiếm khi các mục tiêu phụ thuộc nhau hoặc không khả thi. Ngoài ra, bộ test sinh tự động thường thiếu assertion cô đọng hoặc tạo ra quá nhiều assertion dư thừa.

## Phương pháp

EvoSuite sinh bộ test JUnit tự động cho lớp Java theo hai ý tưởng chính:
1. **Sinh toàn bộ bộ test (Whole Test Suite Generation):** Dùng thuật toán di truyền (Genetic Algorithm) tiến hóa trực tiếp quần thể gồm các *bộ test hoàn chỉnh* với hàm thích nghi đo mức độ tiến gần tới toàn bộ mục tiêu coverage (chủ yếu là branch coverage, dùng khoảng cách nhánh).
2. **Sinh và tối thiểu hóa assertion dựa trên mutation:** Chạy bộ test trên chương trình gốc để ghi lại giá trị trả về / trạng thái làm các assertion hồi quy ứng viên, sau đó áp dụng phân tích mutation và chỉ giữ các assertion giúp tiêu diệt ít nhất một mutant.

## Hệ thống / benchmark đánh giá

- Là bài giới thiệu công cụ 4 trang; phần đánh giá tóm lược dựa trên khung thực nghiệm tạo và tối thiểu hóa assertion của nhóm tác giả trên **10 thư viện/dự án Java mã nguồn mở** (số liệu chi tiết từng bảng trong bài 4 trang chưa xác minh từ toàn văn).

## Kết quả chính

- Trình diễn khả năng sinh tự động bộ test JUnit hoàn chỉnh (gồm chuỗi lời gọi tạo đối tượng và assertion hồi quy được lọc bằng mutation) đạt branch coverage cao đồng thời giữ kích thước bộ test và số assertion gọn nhẹ (chưa xác minh số phần trăm cụ thể trong bài 4 trang này).

## Hạn chế

- **Vấn đề oracle hồi quy:** Assertion ghi nhận hành vi *hiện tại* của mã nguồn; nếu mã nguồn đang có lỗi, EvoSuite sẽ cố định hành vi sai đó làm chuẩn, đòi hỏi lập trình viên rà soát thủ công.
- **Khó tạo trạng thái đối tượng phức tạp và phụ thuộc môi trường ngoài** (CSDL, mạng, I/O) nếu không mock.
- **Chi phí thời gian tìm kiếm** trên lớp lớn và **khả năng tương thích phiên bản JDK** (như ghi nhận trong các nghiên cứu sau này như ChatUniTest 2024).

## Liên hệ với đề tài

- Là phương pháp **đối chứng bổ sung (EvoSuite)** được nêu trong đề xuất và thiết kế kỹ thuật (nếu tương thích JDK 17).
- Cho thấy cách dùng mutation để tăng chất lượng assertion, nhưng khác với **P4b** (dùng LLM sinh test mới diệt mutant còn sống) và khác cách xử lý **oracle** của đề tài (kiểm tra oracle bằng chương trình tham chiếu độc lập thay vì mặc định mã nguồn đang đúng).
