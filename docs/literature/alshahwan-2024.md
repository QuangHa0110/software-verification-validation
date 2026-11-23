# Automated Unit Test Improvement using Large Language Models at Meta (TestGen-LLM)

## Thông tin thư mục

- **Khóa:** `alshahwan-2024`
- **Tên đầy đủ:** Automated Unit Test Improvement using Large Language Models at Meta
- **Tác giả:** Nadia Alshahwan, Jubin Chheda, Anastasia Finogenova, Beliz Gokkaya, Mark Harman, Inna Harper, Alexandru Marginean, Shubho Sengupta, Eddy Wang
- **Năm:** 2024
- **Nơi công bố:** Companion Proceedings of the 32nd ACM International Conference on the Foundations of Software Engineering (FSE Companion '24), Porto de Galinhas, Brazil, tr. 185–196 (preprint: `arXiv:2402.09171`)
- **DOI / URL chính thức:** https://doi.org/10.1145/3663529.3663839 (DBLP: https://dblp.org/rec/conf/sigsoft/AlshahwanCFGHHM24; preprint: https://arxiv.org/abs/2402.09171)
- **Đã xác minh từ:** https://dblp.org/rec/conf/sigsoft/AlshahwanCFGHHM24, https://doi.org/10.1145/3663529.3663839 và https://arxiv.org/abs/2402.09171
- **Ghi chú:** Công trình bổ sung (2024), không có trong mục 11 đề xuất.

## Vấn đề

Triển khai LLM sinh unit test trong môi trường công nghiệp dễ bị từ chối bởi kỹ sư phần mềm nếu test không biên dịch được, chạy chập chờn (flaky), hoặc không mang lại giá trị đo được cho bộ kiểm thử hiện có.

## Phương pháp

Giới thiệu **TestGen-LLM**, công cụ mở rộng tự động các lớp unit test do con người viết sẵn tại Meta theo nguyên tắc **đảm bảo cải thiện có kiểm chứng (Assured LLM-based Software Engineering)**:
- Gửi lớp test hiện có và mã nguồn liên quan cho LLM đề xuất test case bổ sung.
- Áp dụng chuỗi bộ lọc kiểm chứng tự động nhiều tầng: biên dịch được → chạy qua ổn định (loại test chập chờn) → thực sự làm **tăng độ bao phủ dòng/nhánh** so với bộ test gốc.
- Chỉ những test vượt qua mọi bộ lọc mới được trình cho lập trình viên duyệt đưa vào sản phẩm.

Công cụ dùng **phản hồi/lọc theo coverage** (không có vòng lặp phản hồi mutation). Oracle được kiểm định bằng việc chạy qua ổn định trên mã hiện tại kết hợp **kỹ sư phần mềm rà soát thủ công** trước khi hợp nhất.

## Hệ thống / benchmark đánh giá

- Các ứng dụng Android viết bằng **Kotlin** ở quy mô sản phẩm tại Meta (nổi bật là các tính năng Reels và Stories của Instagram, cùng Facebook).

## Kết quả chính

- Theo tóm tắt và các bảng đánh giá triển khai trong bài (số hiệu bảng cụ thể chưa xác minh từ toàn văn):
  - Trên các sản phẩm Reels và Stories của Instagram: **75%** ca sinh test **biên dịch thành công**, **57%** **chạy qua ổn định** qua đường ống kiểm lọc, và **25%** thực sự **làm tăng độ bao phủ mã nguồn** so với bộ test viết tay có sẵn.
  - Trên toàn bộ các lớp sản phẩm được áp dụng: TestGen-LLM cải thiện thành công **11,5%** số lớp.
  - Trong các đợt test-a-thon nội bộ, kỹ sư Meta **chấp nhận 73%** khuyến nghị test của TestGen-LLM đưa vào mã nguồn sản phẩm.

## Hạn chế

- Phụ thuộc vào bộ test nền có sẵn do con người viết (cải thiện/mở rộng thay vì sinh hoàn toàn từ đầu).
- Chưa dùng phản hồi mutation để nhắm vào lỗi ngữ nghĩa tinh vi; tính đúng của oracle cuối cùng vẫn dựa vào bước kỹ sư duyệt tay.
- Không công bố công khai bộ mã/mô hình nội bộ để cộng đồng tái lập trực tiếp.

## Liên hệ với đề tài

- Xác thực giá trị thực tiễn của kiến trúc **bộ lọc nhiều tầng (`test_filter.py`)** trong đề tài: chỉ giữ lại các test biên dịch được, chạy xanh ổn định và thực sự đóng góp tăng coverage.
- Khác với TestGen-LLM (chỉ dựa vào chạy xanh trên mã gốc và kỹ sư duyệt tay), đề tài tự động hóa khâu kiểm chứng **oracle bằng `reference/` độc lập**, bổ sung vòng lặp **P4b (phản hồi mutation)** và thực hiện đối chứng công bằng với **P1 (viết tay)** và **P2 (ngẫu nhiên)**.
