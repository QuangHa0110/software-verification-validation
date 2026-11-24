# Chương 6. Kết luận và hướng phát triển

## 6.1 Kết luận

Trên module giỏ hàng và thanh toán `shop`, sinh test bằng LLM từ đặc tả nghiệp vụ, kiểm tra oracle trên bản tham chiếu rồi đo coverage và mutation trên bản đối tượng đã tạo được các bộ test hợp lệ và phân biệt được nhiều lỗi mô phỏng. P3 đạt 174/174 nhánh và diệt 120/120 mutant không tương đương trong cả 10 lần chạy. P2 sinh ngẫu nhiên diệt 70–85 mutant và có lợi thế về thời gian sinh. Bộ viết tay P1 cũng phủ đủ nhánh và diệt 119/120 mutant. Chênh lệch giữa P3 và P1 nằm ở một biên có ý nghĩa nghiệp vụ cụ thể: tiền hàng bằng 0 vẫn là đầu vào hợp lệ khi tính phí vận chuyển. [Kết quả tổng hợp](../../results/comparison/summary.md) · [Số mutant bị diệt theo lần chạy](../../results/comparison/raw/) · [Rà soát mutant của P1](../../results/mutation/equivalent-review.md)

Đóng góp của bước phản hồi phụ thuộc vào chất lượng bộ test khởi đầu.

- **Đủ ngữ cảnh.** P3 đạt trần cả hai chỉ số. P4 và P4b xác nhận bộ nền đã đạt ngưỡng rồi dừng ở vòng 0 trong cả 10 lần. Dữ liệu cho thấy có thể tránh gọi LLM thêm ở bước này. [So sánh P3 → P4 → P4b](../../results/comparison/coverage-before-after.md)
- **Ngữ cảnh hạn chế.** Với cùng LLM, P3r chỉ nhận đặc tả và API vẫn đạt trần trong cả 10 lần. P4r và P4br cũng dừng ở vòng 0. Đặc tả đủ chi tiết để mô hình tạo bộ test đạt các mục tiêu đã đo ngay cả khi không có mã nguồn trong prompt khởi đầu. [So sánh P3r → P4r → P4br](../../results/comparison/coverage-before-after-reduced.md)
- **LLM yếu hơn, đủ ngữ cảnh.** P4w phủ thêm nhánh ở 2/10 lần nhưng giữ nguyên số mutant bị diệt ở từng lần. P4bw diệt thêm mutant ở 10/10 lần và kết thúc với 118–119/120, trong khi coverage nhánh giữ nguyên. Kết quả này cho thấy khi vấn đề còn lại là khả năng phân biệt hành vi ở đoạn mã đã chạy qua, phản hồi về chính mutant đó có thể giúp chọn được test tốt hơn. [So sánh P3w → P4w → P4bw](../../results/comparison/coverage-before-after-weak-llm.md) · [Số mutant theo lần chạy](../../results/comparison/raw/) · [Các vòng phản hồi](../../results/comparison/iterations.csv)

Đánh đổi rõ nhất xuất hiện ở chuỗi cuối. Trung vị thời gian sinh tích lũy là 213,7384 giây ở P4w và 978,9383 giây ở P4bw. Do đó, khi chọn chiến lược cho module này, cần xét cả mục tiêu còn thiếu và chi phí đo, sinh, lọc test để đạt mục tiêu đó. [summary.md](../../results/comparison/summary.md)

## 6.2 Hạn chế

Kết quả giới hạn ở một chương trình Java với 235 dòng thực thi và một đặc tả nêu sẵn nhiều ca biên cùng kết quả kỳ vọng. Hiệu ứng trần ở hai chuỗi đầu làm mất khả năng đánh giá phản hồi khi mô hình mạnh gặp bài toán khó hơn. Chuỗi yếu hơn chỉ khảo sát một cấu hình mô hình và CLI khác. Cỡ mẫu của P1 chỉ cho phép mô tả một bộ thủ công, còn 10 lần ở mỗi phương pháp còn lại chưa đủ để khái quát sang dự án hoặc mô hình khác. Quyền thực thi của môi trường cũng giới hạn đối chứng tự động ở P2 do EvoSuite chưa được chạy. [Mục 5.6](chuong-5-ket-qua-va-thao-luan.md#56-các-mối-đe-dọa-đến-độ-giá-trị) · [D-013](../orchestration/decisions.md)

Hai bản cài đặt có thể cùng hiểu sai một yêu cầu. Oracle chạy qua bản tham chiếu và điểm mutation cao vì thế chỉ cung cấp bằng chứng trong phạm vi đặc tả và tập mutant đã chọn. Đặc biệt, hai mutant boolean quá ít để đánh giá riêng nhóm này. Về chi phí, số token gồm phần nền của CLI, thời gian trên máy cá nhân chịu nhiễu, còn đơn giá USD và số giờ viết bộ P1 chưa được ghi nhận. [summary.md](../../results/comparison/summary.md) · [D-016 và D-020](../orchestration/decisions.md)

## 6.3 Hướng phát triển

1. **Tách ảnh hưởng của đặc tả khỏi ảnh hưởng của mã nguồn.** Trên nhiều module với quy mô và số phụ thuộc khác nhau, giữ cố định mô hình rồi so sánh cùng API khi có và khi bỏ bảng tình huống `branches.md`. Thiết kế này kiểm tra trực tiếp lời giải thích cho hiệu ứng trần ở P3r.
2. **Đánh giá khả năng nhận ra lỗi thực.** Dùng các phiên bản có lỗi và bản sửa đã xác nhận, đồng thời giữ riêng một tập mutant không đưa vào prompt phản hồi. Đo xem những test được chọn ở P4bw còn phát hiện được lỗi ngoài tập đã dùng để hướng dẫn hay không. Bổ sung đối chứng sinh test dựa trên tìm kiếm khi có môi trường chạy EvoSuite phù hợp.
3. **Đánh giá quy tắc dừng theo lợi ích và chi phí.** So sánh mức tăng ở từng cặp cùng bộ nền với token và thời gian phát sinh ở từng vòng, nhất là các vòng P4bw không giữ thêm được test. Ghi riêng thời gian viết tay, thời gian gọi mô hình và thời gian đo mutation trên máy có cấu hình cố định. Những phép đo này cho phép xác định điều kiện mà chi phí tìm thêm một test phân biệt được mutant còn chấp nhận được.
