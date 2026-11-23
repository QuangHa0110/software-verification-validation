# Effective Test Generation Using Pre-trained Large Language Models and Mutation Testing

## Thông tin thư mục

- **Khóa:** `dakhel-2024`
- **Tên đầy đủ:** Effective Test Generation Using Pre-trained Large Language Models and Mutation Testing
- **Tác giả:** Arghavan Moradi Dakhel, Amin Nikanjam, Vahid Majdinasab, Foutse Khomh, Michel C. Desmarais
- **Năm:** 2024
- **Nơi công bố:** Information and Software Technology (Elsevier), Vol. 171, Article 107468 (17 trang)
- **DOI / URL chính thức:** https://doi.org/10.1016/j.infsof.2024.107468
- **Đã xác minh từ:** https://doi.org/10.1016/j.infsof.2024.107468, DBLP (`dagstuhl.de`), và bản preprint trên `arxiv.org` đọc qua kết quả tìm kiếm
- **Ghi chú đối chiếu đề xuất:** Khớp tên, tác giả đầu (`A. M. Dakhel`), năm và tạp chí (*Information and Software Technology*, 2024) trong mục 11 của đề xuất; bổ sung 4 đồng tác giả, Vol. 171, Article 107468 và DOI.

## Vấn đề

Độ bao phủ mã nguồn (line/branch coverage) thường tương quan yếu với khả năng phát hiện lỗi thực tế của bộ test do LLM sinh ra. Bộ test có thể phủ nhiều dòng mã nhưng vẫn bỏ sót lỗi do assertion yếu hoặc thiếu trường hợp biên.

## Phương pháp

- Đề xuất **MuTAP** (*Mutation Test case generation using Augmented Prompt*) cho ngôn ngữ **Python**, kết hợp LLM tiền huấn luyện (như Codex, LLaMA) với kiểm thử đột biến (**MutPy**):
  1. Sinh bộ test ban đầu từ LLM kèm bước hậu xử lý sửa lỗi cú pháp/chức năng cơ bản.
  2. Chạy kiểm thử đột biến (mutation testing) lên chương trình mục tiêu để tìm các **mutant còn sống (surviving mutants)**.
  3. Đưa chính các mutant còn sống vào **prompt tăng cường (augmented prompt)** để dẫn hướng LLM sinh thêm test case có assertion chặt hơn nhằm tiêu diệt các mutant đó.
- Có **phản hồi mutation** trực tiếp; **không** dùng vòng lặp phản hồi coverage. Oracle do LLM sinh (và được sửa/kiểm tra trên mã gốc ở giai đoạn sinh test, đánh giá phát hiện lỗi trên phiên bản có lỗi).

## Hệ thống / benchmark đánh giá

- **HumanEval** (kèm các biến thể mã có lỗi tổng hợp để đo mutation score) và **Refactory** (tập mã nộp của sinh viên có lỗi thật do người viết, bằng Python).
- Đối chứng với **Pynguin** (SBST cho Python) và LLM dùng zero-shot / few-shot prompt không có phản hồi mutation.

## Kết quả chính

- Theo tóm tắt và kết quả đánh giá chính của bài (số bảng cụ thể chưa xác minh từ toàn văn):
  - Trên tập đánh giá HumanEval (mã có lỗi tổng hợp): **MuTAP** đạt **Mutation Score 93,57%**.
  - Trên các đoạn mã có lỗi thật do con người viết (Refactory): **MuTAP** phát hiện nhiều hơn tới **28%** đoạn mã lỗi so với các phương pháp chuẩn / zero-shot / few-shot, và vượt trội công cụ tự động **Pynguin**.

## Hạn chế

- Test sinh ban đầu từ LLM vẫn gặp lỗi cú pháp hoặc chức năng, cần hậu xử lý tự động.
- Không phải mọi mutant còn sống đưa vào prompt đều giúp LLM sinh được test diệt nó; các mutant cứng đầu (stubborn) hoặc dư thừa làm giảm hiệu quả vòng lặp.
- Đánh giá chủ yếu trên các hàm Python nhỏ, độc lập (HumanEval, bài tập Refactory), chưa thử nghiệm trên hệ thống hướng đối tượng nhiều lớp hay kiểm chứng độc lập tính đúng oracle theo đặc tả.

## Liên hệ với đề tài

- Là tiền đề trực tiếp nhất cho cải tiến **P4b (phản hồi mutation)** của đề tài: chạy PIT lấy danh sách mutant còn sống (vị trí, toán tử, đoạn mã bị đổi) đưa vào prompt để LLM sinh test tiêu diệt chúng.
- Đề tài mở rộng ý tưởng MuTAP theo ba hướng: (1) áp dụng cho **Java hướng đối tượng nhiều lớp** (module giỏ hàng & thanh toán), (2) **kết hợp tuần tự coverage-guided (P4) rồi mutation-feedback (P4b)** theo thiết kế bóc tách, và (3) kiểm chứng tính đúng của **oracle bằng `reference/` độc lập** kèm rà soát thủ công mutant tương đương.
