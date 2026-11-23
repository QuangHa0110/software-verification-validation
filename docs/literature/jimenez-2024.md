# SWE-bench: Can Language Models Resolve Real-world Github Issues?

## Thông tin thư mục

- **Khóa:** `jimenez-2024`
- **Tên đầy đủ:** SWE-bench: Can Language Models Resolve Real-world Github Issues?
- **Tác giả:** Carlos E. Jimenez, John Yang, Alexander Wettig, Shunyu Yao, Kexin Pei, Ofir Press, Karthik R. Narasimhan
- **Năm:** 2024
- **Nơi công bố:** Twelfth International Conference on Learning Representations (ICLR 2024, Oral Presentation)
- **DOI / URL chính thức:** https://openreview.net/forum?id=VTF8yNQM66 (preprint: https://arxiv.org/abs/2310.06770; DBLP: https://dblp.org/rec/journals/corr/abs-2310-06770)
- **Đã xác minh từ:** https://openreview.net/forum?id=VTF8yNQM66 và https://arxiv.org/abs/2310.06770
- **Ghi chú đối chiếu đề xuất:** Khớp tác giả, năm và hội nghị (ICLR 2024) trong mục 11 của đề xuất; bản chính thức viết hoa/thường `Real-world Github Issues?` (đề xuất ghi `Real-World GitHub Issues?`).

## Vấn đề

Các benchmark sinh mã mức hàm (như HumanEval) đã gần bão hòa và không phản ánh thách thức kỹ nghệ phần mềm thực tế: hiểu kho mã lớn, suy luận liên tệp và sinh bản vá (patch) thỏa mãn bộ kiểm thử thực thi.

## Phương pháp

- Xây dựng chuẩn **SWE-bench** gồm các cặp issue GitHub và pull request thực tế kèm môi trường thực thi cô lập (Docker).
- Đánh giá các LLM (Claude 2, GPT-4, và mô hình tinh chỉnh **SWE-Llama** 7B/13B) theo hai thiết lập cung cấp ngữ cảnh: **BM25 retrieval** (truy xuất thực tế) và **Oracle retrieval** (cấp đúng tệp lập trình viên đã sửa).
- Oracle đánh giá là các unit test **Fail-to-Pass** và test hồi quy tách từ chính pull request của dự án. Công trình không thực hiện sinh test có phản hồi coverage hay mutation.

## Hệ thống / benchmark đánh giá

- **2.294** bài toán kỹ nghệ phần mềm lấy từ các issue và pull request thực tế của **12 kho mã nguồn Python** phổ biến (như Django, SymPy, scikit-learn, requests). Riêng GPT-4 được đánh giá trên tập con ngẫu nhiên 25% do giới hạn ngân sách.

## Kết quả chính

- Theo **Bảng 2** (Table 2, truy xuất BM25 theo giới hạn ngữ cảnh 13k / 27k / 50k token):
  - **Claude 2:** **1,96%** (13k), **1,87%** (27k), **1,22%** (50k).
  - **SWE-Llama 7B:** **0,70%** (13k), **0,31%** (27k), **0,00%** (50k).
  - **SWE-Llama 13B:** **0,70%** (13k), **0,48%** (27k), **0,00%** (50k).
- Ở thiết lập **Oracle retrieval** (cấp đúng tệp cần sửa): **Claude 2** giải quyết tối đa **4,8%**, **GPT-4** khoảng **1,7%**.
- Theo **Bảng 3** (Table 3, phân tích recall truy xuất): BM25 ở ngưỡng 27.000 token bỏ sót hoàn toàn (không lấy được tệp oracle nào) trong gần **50%** trường hợp.

## Hạn chế

- LLM suy giảm mạnh khi ngữ cảnh chứa nhiều mã không liên quan (tăng cửa sổ 13k → 50k làm giảm tỷ lệ giải quyết của Claude 2 từ 1,96% xuống 1,22%).
- Gặp khó khăn lớn trong suy luận nhiều tệp và thiếu vòng lặp tương tác với môi trường thực thi (chạy test, đọc lỗi).

## Liên hệ với đề tài

- Chứng minh rằng **không thể mặc định LLM giỏi trong mọi tác vụ kỹ nghệ phần mềm** nếu chỉ gọi một lượt không có ngữ cảnh chọn lọc và không có vòng phản hồi thực thi.
- Củng cố thiết kế của **P4/P4b** (chỉ đưa đúng nhánh chưa phủ hoặc mutant còn sống kèm ngữ cảnh mã nguồn hẹp thay vì nhồi toàn bộ ngữ cảnh) và quan điểm đánh giá khách quan trong Chương 5–6 (nêu rõ cả điểm LLM không vượt trội).
