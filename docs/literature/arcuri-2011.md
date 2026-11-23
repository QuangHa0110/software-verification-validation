# A Practical Guide for Using Statistical Tests to Assess Randomized Algorithms in Software Engineering

## Thông tin thư mục

- **Khóa:** `arcuri-2011`
- **Tên đầy đủ:** A Practical Guide for Using Statistical Tests to Assess Randomized Algorithms in Software Engineering
- **Tác giả:** Andrea Arcuri, Lionel Briand
- **Năm:** 2011
- **Nơi công bố:** Proceedings of the 33rd International Conference on Software Engineering (ICSE '11), tr. 1–10
- **DOI / URL chính thức:** https://doi.org/10.1145/1985793.1985795
- **Đã xác minh từ:** https://doi.org/10.1145/1985793.1985795 và các nguồn học thuật (`simula.no`, `computer.org`, `ul.ie`)
- **Ghi chú đối chiếu đề xuất:** Khớp tên, 2 tác giả, năm và hội nghị (ICSE 2011) trong mục 11 của đề xuất; bổ sung số trang (1–10) và DOI.

## Vấn đề

Các thuật toán có yếu tố ngẫu nhiên trong kỹ nghệ phần mềm (như tìm kiếm tiến hóa, sinh test ngẫu nhiên — và ngày nay là sinh test bằng LLM) cho kết quả khác nhau giữa các lần chạy. Nhiều nghiên cứu đánh giá thiếu chặt chẽ: chạy quá ít lần, lạm dụng kiểm định tham số giả định phân phối chuẩn, hoặc chỉ báo cáo p-value mà bỏ qua độ lớn hiệu ứng thực tiễn.

## Phương pháp

Đưa ra bộ hướng dẫn thực hành phân tích thống kê cho thuật toán ngẫu nhiên trong kỹ nghệ phần mềm:
1. **Lặp nhiều lần độc lập:** Chạy lặp nhiều lần trên cùng bài toán để phản ánh phương sai; khuyến nghị lý tưởng $n \ge 1.000$ lần chạy (nếu chi phí buộc giảm số lần chạy, ví dụ $n = 30$ hoặc nhỏ hơn, phải giải trình rõ ràng chi phí/thời gian).
2. **Kiểm định phi tham số:** Dùng **Mann–Whitney U test** (Wilcoxon rank-sum) thay cho t-test vì dữ liệu thường không theo phân phối chuẩn; áp dụng hiệu chỉnh đa so sánh (như **Bonferroni**) khi so sánh nhiều cặp.
3. **Đo độ lớn hiệu ứng phi tham số (Effect Size):** Dùng hệ số **Vargha–Delaney $\hat{A}_{12}$** để lượng hóa xác suất thuật toán A cho kết quả tốt hơn thuật toán B ($\hat{A}_{12} = 0{,}5$ là tương đương).
4. **Thống kê mô tả phù hợp:** Khuyến nghị báo cáo thêm trung vị (median) và khoảng tứ phân vị (IQR) bên cạnh trung bình/độ lệch chuẩn.

## Hệ thống / benchmark đánh giá

- Công trình phương pháp luận về thiết kế thực nghiệm và phân tích thống kê cho các bài toán kỹ nghệ phần mềm ngẫu nhiên (không phải công cụ sinh test cụ thể).

## Kết quả chính

- Chuẩn hóa bộ quy tắc và ngưỡng diễn giải độ lớn hiệu ứng **Vargha–Delaney $\hat{A}_{12}$** (số hiệu bảng cụ thể trong bài chưa xác minh từ toàn văn; theo hướng dẫn chuẩn của bài):
  - Không đáng kể (negligible): gần **0,50**.
  - Nhỏ (small): **$\hat{A}_{12} \ge 0{,}56$** (và $< 0{,}64$).
  - Trung bình (medium): **$\hat{A}_{12} \ge 0{,}64$** (và $< 0{,}71$).
  - Lớn (large): **$\hat{A}_{12} \ge 0{,}71$**.

## Hạn chế

- Khuyến nghị $n = 1.000$ lần chạy khả thi với thuật toán nội bộ nhanh nhưng khó áp dụng trực tiếp với phương pháp gọi API LLM có chi phí tiền và độ trễ cao; đòi hỏi cân bằng thực tế và ghi nhận rõ trong phần *Threats to Validity*.

## Liên hệ với đề tài

- Là cơ sở phương pháp luận trực tiếp cho **Thiết kế thực nghiệm** (mục 8 đề xuất) và script thống kê `experiments/scripts/analyze.py`: chạy lặp **10 lần** cho các phương pháp ngẫu nhiên (**P2, P3, P4, P4b**), kiểm định sự khác biệt bằng **Mann–Whitney U** và lượng hóa mức độ cải thiện bằng **Vargha–Delaney $\hat{A}_{12}$**.
- Nhắc nhở phần *Threats to Validity* (mục 5.6 báo cáo) cần giải trình lý do chọn $n = 10$ lần chạy (do ngân sách token/chi phí API LLM) và xem xét hiệu chỉnh đa so sánh khi so cặp nhiều phương pháp.
