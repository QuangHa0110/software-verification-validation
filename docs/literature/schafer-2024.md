# An Empirical Evaluation of Using Large Language Models for Automated Unit Test Generation

## Thông tin thư mục

- **Khóa:** `schafer-2024`
- **Tên đầy đủ:** An Empirical Evaluation of Using Large Language Models for Automated Unit Test Generation
- **Tác giả:** Max Schäfer, Sarah Nadi, Aryaz Eghbali, Frank Tip
- **Năm:** 2024 (bản trực tuyến sớm cuối năm 2023; số chính thức tháng 1/2024)
- **Nơi công bố:** IEEE Transactions on Software Engineering (TSE), Vol. 50, No. 1, tr. 85–105
- **DOI / URL chính thức:** https://doi.org/10.1109/TSE.2023.3334955
- **Đã xác minh từ:** https://doi.org/10.1109/TSE.2023.3334955 và trang tác giả (`franktip.org`) / bản arXiv đọc qua kết quả tìm kiếm
- **Ghi chú đối chiếu đề xuất:** Khớp tên, 4 tác giả, năm và tạp chí (IEEE TSE 2024) trong mục 11 của đề xuất; bổ sung Vol. 50, No. 1, tr. 85–105 và DOI.

## Vấn đề

Các công cụ sinh test tự động truyền thống cho ngôn ngữ động như JavaScript thường đạt độ bao phủ thấp và thiếu khả năng sử dụng đúng API phức tạp. Câu hỏi đặt ra là liệu LLM tiền huấn luyện (không cần fine-tune) có thể sinh unit test hiệu quả hơn cho các gói thực tế hay không.

## Phương pháp

- Xây dựng **TestPilot**, công cụ sinh unit test thích ứng cho JavaScript/TypeScript không cần huấn luyện bổ sung.
- **Thiết kế prompt:** Kết hợp chữ ký hàm, mã nguồn cài đặt và các ví dụ sử dụng trích từ tài liệu (documentation) của gói.
- **Vòng lặp sửa lỗi thực thi:** Chạy test sinh ra; nếu thất bại, gửi lại test lỗi kèm thông báo lỗi (error message) cho LLM để sửa lặp.
- Không sử dụng phản hồi coverage theo nhánh chưa phủ hay phản hồi mutation; oracle do LLM tự sinh và được kiểm tra bằng cách chạy qua trên chính cài đặt hiện tại (pass/fail).

## Hệ thống / benchmark đánh giá

- **25 gói npm** (JavaScript/TypeScript) gồm **1.684 hàm API**, thử nghiệm chính với `gpt-3.5-turbo` (cùng các mô hình khác như `code-cushman-002` và StarCoder).
- Đối chứng với **Nessie** (kỹ thuật sinh test JavaScript có định hướng phản hồi).

## Kết quả chính

- Theo tóm tắt và kết quả đánh giá chính của bài (số bảng cụ thể chưa xác minh từ toàn văn):
  - **TestPilot (`gpt-3.5-turbo`):** đạt trung vị **statement coverage 70,2%** và trung vị **branch coverage 52,8%**.
  - **Nessie (đối chứng):** đạt **51,3% statement coverage** và **25,6% branch coverage**.
  - **Tính mới / không sao chép thuộc lòng:** **92,8%** test sinh ra có độ tương đồng không quá 50% (theo khoảng cách chỉnh sửa chuẩn hóa) so với test có sẵn, và **0%** là bản sao chính xác.

## Hạn chế

- Chỉ phản hồi theo lỗi thực thi để làm test chạy qua, không nhắm trực tiếp các nhánh chưa phủ hay mutant còn sống.
- Chưa có cơ chế độc lập để kiểm chứng tính đúng ngữ nghĩa của oracle/assertion (test chạy qua trên mã gốc vẫn có thể có assertion yếu hoặc hợp thức hóa lỗi).
- Phụ thuộc vào độ ổn định và chi phí của API mô hình thương mại; khó xử lý trạng thái bất đồng bộ và mock phức tạp.

## Liên hệ với đề tài

- Là tài liệu thực nghiệm chuẩn mực cho **P3 (Direct LLM)** và việc tận dụng cả mã nguồn lẫn tài liệu/đặc tả trong prompt.
- Đề tài mở rộng vượt lên TestPilot ở ba điểm: (1) chuyển sang **Java** với vòng lặp **P4 (coverage-guided)** nhắm thẳng nhánh chưa phủ từ JaCoCo, (2) bổ sung **P4b (phản hồi mutation)** với PIT, và (3) kiểm chứng tính đúng của **oracle** bằng chương trình tham chiếu độc lập thay vì chỉ xem test có chạy xanh trên mã gốc hay không.
