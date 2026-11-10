# ĐỀ XUẤT ĐĂNG KÝ ĐỀ TÀI BÀI TẬP LỚN

**Học phần:** Kiểm chứng và Thẩm định Phần mềm (Software Verification and Validation)
**Định hướng:** Số 1 – LLM-based Test Generation
**Học viên:** [Họ và tên] – **Mã học viên:** [……] – **Lớp:** [……]
**Hạn nộp bài tập lớn:** 30/11/2026

---

## 1. Tên đề tài

**LLM-based Test Generation: Sinh kiểm thử tự động bằng mô hình ngôn ngữ nhằm tối ưu hóa độ bao phủ mã nguồn**

*Áp dụng cho module giỏ hàng và thanh toán của hệ thống bán hàng trực tuyến; hướng mở rộng: sinh test lặp có phản hồi coverage kết hợp phản hồi mutation.*

## 2. Đặt vấn đề

Mô hình ngôn ngữ lớn (LLM) có thể đọc mã nguồn, đặc tả và sinh test case nhanh chóng. Tuy nhiên, test do LLM sinh trực tiếp thường gặp các vấn đề: không biên dịch hoặc không chạy được, trùng lặp, bỏ sót các nhánh điều kiện khó, và đặc biệt là **oracle sai** (expected output được suy ra từ chính mã nguồn nên có thể "hợp thức hóa" lỗi của chương trình).

**Câu hỏi trọng tâm:** Làm thế nào để sử dụng LLM sinh ra bộ test hợp lệ, đạt độ bao phủ mã nguồn cao và có khả năng phát hiện lỗi tốt, và hiệu quả đó so với các phương pháp sinh test truyền thống như thế nào?

## 3. Mục tiêu

1. Xây dựng chương trình đối tượng kiểm thử (Java) có đặc tả rõ ràng và bộ test chuẩn để đối chiếu.
2. Triển khai hai chiến lược sinh test bằng LLM: **Direct LLM Generation** và **Coverage-guided LLM Generation**.
3. Đề xuất cải tiến: bổ sung **phản hồi mutation** (đưa mutant còn sống cho LLM) và **bộ lọc test** (tính hợp lệ, tính đúng của oracle, loại test trùng).
4. Đo lường statement/line, branch, function coverage và mutation score của các bộ test.
5. So sánh thực nghiệm với các phương pháp đối chứng, trả lời các câu hỏi nghiên cứu RQ1–RQ6 bằng số liệu và phân tích hạn chế.

## 4. Hệ thống được áp dụng

Hệ thống được sử dụng thử nghiệm là **hệ thống bán hàng trực tuyến**, phạm vi là **module giỏ hàng và thanh toán**, xây dựng bằng **Java**. Các chức năng được kiểm thử dự kiến:

| Chức năng | Nhánh / tình huống cần kiểm thử |
|---|---|
| Thêm / cập nhật sản phẩm trong giỏ | Số lượng âm hoặc bằng 0, vượt tồn kho, sản phẩm không tồn tại, cộng dồn số lượng |
| Tính tạm tính đơn hàng | Giỏ rỗng, nhiều sản phẩm, làm tròn tiền |
| Áp dụng mã giảm giá | Giảm theo %, giảm số tiền cố định, giá trị đơn tối thiểu, mức giảm tối đa, mã hết hạn, mã không hợp lệ |
| Tính phí vận chuyển | Theo khu vực, theo khối lượng, ngưỡng miễn phí vận chuyển |
| Đặt / hủy đơn hàng | Trạng thái đơn không hợp lệ, hủy quá thời hạn, hoàn tồn kho khi hủy |

Hệ thống có nhiều điều kiện lồng nhau, giá trị biên và xử lý ngoại lệ, phù hợp để đánh giá coverage và mutation score. Mỗi chức năng đi kèm **đặc tả đầu vào/đầu ra**, dùng làm căn cứ kiểm tra tính đúng của oracle.

## 5. Phương pháp đề xuất

| Mã | Phương pháp | Vai trò |
|---|---|---|
| P1 | Manual Testing – test viết tay theo đặc tả | Đối chứng, đồng thời là bộ test chuẩn |
| P2 | Random Testing – sinh đầu vào ngẫu nhiên, oracle lấy từ đặc tả | Đối chứng |
| P3 | Direct LLM Generation – gửi mã nguồn và đặc tả cho LLM một lần | Đối chứng |
| P4 | Coverage-guided LLM Generation (có cải tiến) | Phương pháp đề xuất |
| (tùy chọn) | EvoSuite – sinh test dựa trên tìm kiếm | Đối chứng bổ sung, nếu tương thích phiên bản Java |

**Quy trình P4:**

1. LLM sinh bộ test ban đầu từ mã nguồn và đặc tả.
2. Chạy test bằng JUnit 5, đo coverage bằng JaCoCo.
3. Trích các dòng và nhánh chưa được phủ kèm điều kiện nhánh tương ứng, đưa vào prompt để LLM sinh test bổ sung.
4. **Lọc test:** loại test không biên dịch hoặc không chạy được, test có oracle mâu thuẫn với đặc tả, và test không làm tăng coverage.
5. **Phản hồi mutation (cải tiến):** chạy PIT, đưa các mutant còn sống cho LLM để sinh test tiêu diệt chúng.
6. Lặp lại đến khi đạt ngưỡng coverage hoặc hết số vòng lặp / ngân sách token.

## 6. Câu hỏi nghiên cứu

| Mã | Câu hỏi |
|---|---|
| RQ1 | LLM sinh test đạt statement và branch coverage ở mức nào? |
| RQ2 | Coverage-guided có cải thiện coverage so với Direct LLM không? |
| RQ3 | Test do LLM sinh có phát hiện lỗi tốt hơn Random Testing không? |
| RQ4 | Cung cấp thông tin nhánh chưa phủ có cải thiện chất lượng test không? |
| RQ5 | Mối quan hệ giữa số lượng test, coverage và mutation score? |
| RQ6 | Chi phí và thời gian sử dụng LLM có tương xứng với hiệu quả đạt được không? |

## 7. Công cụ và môi trường

- **Ngôn ngữ / build:** Java 17, Maven
- **Kiểm thử:** JUnit 5
- **Đo coverage:** JaCoCo (line, branch, method)
- **Mutation testing:** PIT (Pitest)
- **LLM:** mô hình thương mại qua API (tên model, phiên bản, nhiệt độ, số token và chi phí sẽ được ghi đầy đủ)
- **Quản lý mã nguồn và kết quả:** GitHub

## 8. Thiết kế thực nghiệm

- Cả bốn phương pháp áp dụng trên **cùng chương trình** và **cùng quy trình đo**.
- Tiêu chí: statement/line coverage, branch coverage, function coverage, mutation score, số test case, thời gian thực thi, thời gian sinh test, chi phí LLM. Đo thêm **tỷ lệ test chạy được** và **tỷ lệ oracle đúng theo đặc tả**.
- Mutant còn sống được xem xét thủ công để loại mutant tương đương khỏi mẫu số; bổ sung thủ công các toán tử đột biến mà PIT không hỗ trợ (ví dụ statement deletion) nếu cần.
- Các phương pháp có yếu tố ngẫu nhiên (P2, P3, P4) chạy **10 lần**, báo cáo trung bình và độ lệch chuẩn; so sánh bằng kiểm định **Mann–Whitney U** và hệ số **Vargha–Delaney Â12**.

## 9. Sản phẩm dự kiến

1. Mã nguồn chương trình đối tượng kiểm thử và đặc tả
2. Bộ prompt sử dụng cho LLM
3. Bộ test do LLM sinh ra (P3, P4)
4. Kết quả thực thi test
5. Báo cáo coverage (JaCoCo)
6. Báo cáo mutation testing (PIT)
7. Bảng so sánh các phương pháp
8. Script tái lập thực nghiệm
9. Báo cáo khoa học (6 chương theo mẫu của đề)

## 10. Kế hoạch thực hiện

| Thời gian | Công việc |
|---|---|
| 10/11 – 12/11 | Đăng ký đề tài; chốt LLM; bắt đầu nghiên cứu tổng quan (song song đến 20/11) |
| 11/11 – 15/11 | Xây dựng chương trình đối tượng, đặc tả, bộ test P1; dựng quy trình đo (Maven, JaCoCo, PIT) |
| 16/11 – 20/11 | Triển khai P2 Random, P3 Direct LLM và vòng lặp coverage-guided của P4 |
| 21/11 – 23/11 | Bổ sung bộ lọc test và phản hồi mutation; thử EvoSuite (tùy chọn) |
| 24/11 – 26/11 | Chạy thực nghiệm 10 lần, tổng hợp số liệu, kiểm định thống kê |
| 27/11 – 29/11 | Viết báo cáo, chuẩn bị demo, đóng gói repo và hướng dẫn tái lập |
| 30/11 | Nộp bài |

## 11. Tài liệu tham khảo sơ bộ

*Ghi chú: cần kiểm tra lại năm và nơi công bố chính thức trước khi đưa vào báo cáo.*

1. P. Godefroid, N. Klarlund, K. Sen. *DART: Directed Automated Random Testing.* PLDI 2005.
2. G. Fraser, A. Arcuri. *EvoSuite: Automatic Test Suite Generation for Object-Oriented Software.* ESEC/FSE 2011.
3. M. Chen et al. *Evaluating Large Language Models Trained on Code.* arXiv 2021.
4. C. E. Jimenez et al. *SWE-bench: Can Language Models Resolve Real-World GitHub Issues?* ICLR 2024.
5. M. Schäfer, S. Nadi, A. Eghbali, F. Tip. *An Empirical Evaluation of Using Large Language Models for Automated Unit Test Generation.* IEEE TSE 2024.
6. C. Lemieux, J. P. Inala, S. K. Lahiri, S. Sen. *CodaMosa: Escaping Coverage Plateaus in Test Generation with Pre-trained Large Language Models.* ICSE 2023.
7. J. A. Pizzorno, E. D. Berger. *CoverUp: Coverage-Guided LLM-Based Test Generation.* arXiv 2024.
8. Y. Chen et al. *ChatUniTest: A Framework for LLM-Based Test Generation.* FSE Companion 2024.
9. Z. Yuan et al. *No More Manual Tests? Evaluating and Improving ChatGPT for Unit Test Generation.* FSE 2024.
10. A. M. Dakhel et al. *Effective Test Generation Using Pre-trained Large Language Models and Mutation Testing.* Information and Software Technology, 2024.
11. Y. Jia, M. Harman. *An Analysis and Survey of the Development of Mutation Testing.* IEEE TSE 2011.
12. A. Arcuri, L. Briand. *A Practical Guide for Using Statistical Tests to Assess Randomized Algorithms in Software Engineering.* ICSE 2011.
