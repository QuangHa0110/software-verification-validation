# DART: Directed Automated Random Testing

## Thông tin thư mục

- **Khóa:** `godefroid-2005`
- **Tên đầy đủ:** DART: Directed Automated Random Testing
- **Tác giả:** Patrice Godefroid, Nils Klarlund, Koushik Sen
- **Năm:** 2005
- **Nơi công bố:** Proceedings of the 2005 ACM SIGPLAN Conference on Programming Language Design and Implementation (PLDI '05), tr. 213–223
- **DOI / URL chính thức:** https://doi.org/10.1145/1064978.1065036
- **Đã xác minh từ:** https://doi.org/10.1145/1064978.1065036 (kết quả tìm kiếm thư mục PLDI '05 và bản tóm tắt đánh giá thực nghiệm trên oSIP)
- **Ghi chú đối chiếu đề xuất:** Khớp tên, tác giả, năm và hội nghị (PLDI 2005) trong mục 11 của đề xuất; bổ sung số trang (213–223) và DOI.

## Vấn đề

Kiểm thử ngẫu nhiên thuần túy khó đi sâu vào các nhánh có điều kiện ràng buộc hẹp, đồng thời việc viết trình điều khiển kiểm thử (test driver) thủ công cho từng hàm tốn nhiều công sức.

## Phương pháp

DART kết hợp ba thành phần:
1. Trích xuất giao diện tự động để tạo test driver cho các hàm đầu vào mà không cần viết tay.
2. Sinh đầu vào ngẫu nhiên cho lần chạy đầu tiên.
3. Thực thi tượng trưng động (dynamic symbolic execution / concolic testing): thu thập ràng buộc đường đi (path constraints) trong quá trình chạy thực và dùng bộ giải ràng buộc để đảo nhánh, sinh đầu vào mới hướng tới các đường thực thi chưa được phủ.

DART không dùng phản hồi coverage theo kiểu prompt LLM hay phản hồi mutation; oracle chủ yếu dựa vào phát hiện lỗi tổng quát (segmentation fault/crash, không dừng).

## Hệ thống / benchmark đánh giá

- Thư viện C mã nguồn mở **oSIP** (cài đặt giao thức SIP), với khoảng **600 hàm** nhìn thấy từ bên ngoài được trích xuất tự động làm hàm kiểm thử cấp cao nhất, giới hạn tối đa **1.000 lần chạy** cho mỗi hàm.

## Kết quả chính

- Trên thư viện oSIP (phần đánh giá thực nghiệm; số bảng cụ thể chưa xác minh từ toàn văn): DART gây crash cho **65%** số hàm oSIP trong giới hạn 1.000 lần chạy mỗi hàm, phát hiện hàng trăm trường hợp crash.
- Phần lớn các lỗi bắt nguồn từ việc hàm nhận con trỏ và giải tham chiếu mà không kiểm tra `NULL`.

## Hạn chế

- **Thiếu đặc tả / oracle chức năng:** Chỉ dựa vào crash và không dừng nên không phân biệt được đâu là lỗi thực sự và đâu là vi phạm tiền điều kiện ngầm của API (gây báo động giả / khó đánh giá mức độ nghiêm trọng).
- **Bùng nổ không gian trạng thái:** Thực thi tượng trưng động gặp khó khăn khi mở rộng lên hệ thống lớn với ràng buộc đường đi phức tạp.

## Liên hệ với đề tài

- Là nền tảng kinh điển về hạn chế của kiểm thử ngẫu nhiên thuần túy và nhu cầu định hướng theo nhánh chưa phủ — động lực trực tiếp cho thiết kế đối chứng **P2 (Random Testing)** và cơ chế định hướng theo nhánh của **P4 (Coverage-guided LLM)**.
- Minh họa rõ vấn đề **oracle**: chỉ tìm lỗi crash là không đủ để kiểm chứng tính đúng nghiệp vụ; đề tài khắc phục bằng chương trình tham chiếu độc lập viết từ đặc tả.
