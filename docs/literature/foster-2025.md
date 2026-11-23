# Mutation-Guided LLM-based Test Generation at Meta (ACH)

## Thông tin thư mục

- **Khóa:** `foster-2025`
- **Tên đầy đủ:** Mutation-Guided LLM-based Test Generation at Meta
- **Tác giả:** Christopher Foster, Abhishek Gulati, Mark Harman, Inna Harper, Ke Mao, Jillian Ritchey, Hervé Robert, Shubho Sengupta
- **Năm:** 2025
- **Nơi công bố:** Companion Proceedings of the 33rd ACM International Conference on the Foundations of Software Engineering (FSE Companion '25, Industry Papers Track), tr. 180–191 (preprint: `arXiv:2501.12862`, tháng 1/2025)
- **DOI / URL chính thức:** https://doi.org/10.1145/3696630.3728544 (preprint: https://arxiv.org/abs/2501.12862)
- **Đã xác minh từ:** https://doi.org/10.1145/3696630.3728544, DBLP (`dblp.org`), và https://arxiv.org/abs/2501.12862
- **Ghi chú:** Công trình bổ sung (2025), không có trong mục 11 đề xuất.

## Vấn đề

Kiểm thử đột biến truyền thống tạo hàng loạt mutant cú pháp chung chung với chi phí lớn và nhiều mutant không liên quan tới rủi ro thực tế; mặt khác, sinh test thuần theo coverage không đủ để gia cố phần mềm chống lại các lỗi hồi quy tinh vi (như vi phạm quyền riêng tư/tuân thủ).

## Phương pháp

Giới thiệu hệ thống **ACH (Automated Compliance Hardener)** triển khai tại Meta, kết hợp LLM với kiểm thử đột biến định hướng rủi ro:
1. Dùng LLM sinh **mutant mục tiêu** mô phỏng các lỗi thực tế liên quan tới một mối quan ngại cụ thể (ví dụ tuân thủ quyền riêng tư) thay vì chỉ dùng toán tử cú pháp đại trà.
2. Dùng một **tác nhân LLM lọc mutant tương đương (equivalent mutant detection agent)** kết hợp tiền xử lý đơn giản để loại bỏ mutant tương đương trước khi sinh test.
3. Với các mutant còn sống không tương đương, gọi LLM sinh test case nhằm **tiêu diệt mutant** đó, lọc tự động (biên dịch, chạy xanh ổn định trên mã gốc, diệt được mutant) rồi gửi kỹ sư duyệt.

Công trình có **phản hồi mutation** ở quy mô công nghiệp (không chạy vòng coverage-guided riêng). Oracle được kiểm tra qua bộ test chạy xanh trên mã gốc và **kỹ sư phần mềm duyệt tay**.

## Hệ thống / benchmark đánh giá

- Triển khai thực tế trên **10.795 lớp Android viết bằng Kotlin** thuộc **7 nền tảng phần mềm** tại Meta (gồm các ứng dụng lớn như Messenger và WhatsApp).

## Kết quả chính

- Theo tóm tắt và kết quả đánh giá chính của bài (số hiệu bảng cụ thể chưa xác minh từ toàn văn):
  - Từ 10.795 lớp Kotlin, ACH tạo ra **9.095 mutant** và sinh **571 test case** gia cố quyền riêng tư.
  - **Tác nhân phát hiện mutant tương đương:** đạt precision **0,79** và recall **0,47** ở cấu hình cơ sở; khi kết hợp tiền xử lý đơn giản tăng lên **precision 0,95** và **recall 0,96**.
  - Trong các đợt test-a-thon cùng đội ngũ kỹ sư Messenger và WhatsApp, các kỹ sư **chấp nhận 73%** test do ACH sinh ra (trong đó khoảng **36%** được đánh giá liên quan trực tiếp đến quyền riêng tư, phần còn lại hữu ích cho chống hồi quy nói chung).

## Hạn chế

- Chưa có cơ chế oracle độc lập tự động từ đặc tả chính thức; việc xác nhận tính đúng của hành vi mong đợi vẫn phụ thuộc vào đánh giá của kỹ sư.
- Tập trung vào bài toán công nghiệp đặc thù (Kotlin/Android, quy tắc nội bộ của Meta) nên khó đối chứng trực tiếp theo chuẩn học thuật mở với random testing hay manual testing từ đặc tả trên cùng hệ thống.

## Liên hệ với đề tài

- Là bằng chứng công nghiệp mới nhất (FSE 2025) khẳng định hướng đi của **P4b (phản hồi mutation với LLM)** và tầm quan trọng của việc xử lý **mutant tương đương** khi dùng mutation để dẫn hướng LLM.
- Trong đề tài, nhóm áp dụng ý tưởng phản hồi mutation ở quy mô bài toán mở tái lập được trên **Java** (với 5 toán tử đột biến chuẩn qua PIT + `mutate_custom.py`), **tách rõ tác dụng** của P4 và P4b qua thiết kế ghép cặp, rà soát thủ công `equivalent-mutants.csv`, và kiểm chứng **oracle tự động qua `reference/`**.
