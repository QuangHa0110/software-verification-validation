# Chương 1. Giới thiệu

## 1.1 Bối cảnh

Kiểm thử đơn vị (unit testing) là tuyến phòng thủ cốt lõi trong quy trình kiểm chứng và thẩm định phần mềm (Software Verification and Validation), giúp phát hiện sớm sai sót ở mức lớp và phương thức trước khi tích hợp hệ thống. Tuy nhiên, việc viết và bảo trì bộ kiểm thử thủ công theo đặc tả đòi hỏi nhiều thời gian, công sức và phụ thuộc lớn vào kinh nghiệm của kỹ sư phần mềm. Để giảm gánh nặng này, các phương pháp sinh kiểm thử tự động (automated test generation) truyền thống như kiểm thử ngẫu nhiên (random testing), thực thi tượng trưng động (dynamic symbolic execution) `[godefroid-2005]` và sinh kiểm thử dựa trên tìm kiếm (Search-Based Software Testing — SBST) `[fraser-2011]` đã được phát triển mạnh mẽ. Dẫu vậy, các kỹ thuật truyền thống thường gặp hạn chế khi cần tạo chuỗi lời gọi có ngữ nghĩa nghiệp vụ phức tạp, dễ rơi vào ngưỡng bão hòa độ bao phủ (coverage plateau) `[lemieux-2023]` và sinh ra mã kiểm thử khó đọc đối với con người `[yuan-2024]`.

Sự phát triển của các mô hình ngôn ngữ lớn (Large Language Models — LLMs) được huấn luyện trên kho mã nguồn khổng lồ `[chen-2021]` đã mở ra hướng tiếp cận mới cho bài toán sinh kiểm thử tự động. Khác với các công cụ phân tích thuần cú pháp hoặc tìm kiếm ngẫu nhiên, LLM có khả năng đọc hiểu đồng thời mã nguồn, chữ ký hàm và đặc tả ngôn ngữ tự nhiên để tạo ra các ca kiểm thử (test cases) dễ đọc, mang tên gọi và dữ liệu đầu vào gần với nghiệp vụ thực tế `[schafer-2024]`, `[yuan-2024]`. Nhờ đó, nhiều hệ thống kết hợp LLM với kiểm thử phần mềm đã được đề xuất cả trong học thuật lẫn môi trường công nghiệp `[chen-2024]`, `[alshahwan-2024]`, `[pizzorno-2025]`, `[foster-2025]`.

Trong bối cảnh đó, đề tài áp dụng và đánh giá các chiến lược sinh kiểm thử bằng LLM trên một bài toán nghiệp vụ quen thuộc nhưng giàu quy tắc điều kiện: **module giỏ hàng và thanh toán của hệ thống bán hàng trực tuyến**, được xây dựng bằng **Java 17** (package `shop`). Module này bao gồm các chức năng cốt lõi như quản lý giỏ hàng, tính giảm giá theo mã khuyến mãi, tính phí vận chuyển theo vùng và khối lượng, cùng vòng đời đặt – thanh toán – giao – hủy đơn hàng (`docs/spec/`). Các quy tắc có nhiều nhánh điều kiện lồng nhau, giá trị biên, thứ tự kiểm tra ngoại lệ chặt chẽ và tính nguyên tử của tồn kho, tạo môi trường phù hợp để đánh giá đồng thời độ bao phủ mã nguồn (code coverage), sức mạnh phát hiện lỗi (mutation score) và tính đúng đắn của test oracle.

---

## 1.2 Vấn đề nghiên cứu

Mặc dù LLM có thể sinh nhanh một lượng lớn ca kiểm thử chỉ từ một lượt nhắc (prompt), việc sử dụng LLM theo cách trực tiếp (Direct LLM Generation) bộc lộ bốn vấn đề kỹ thuật nghiêm trọng đã được ghi nhận trong thực tế nghiên cứu:

1. **Tỷ lệ không biên dịch và lỗi thực thi cao:** Khi sinh mã kiểm thử trực tiếp cho ngôn ngữ định kiểu tĩnh như Java, LLM thường nhầm lẫn chữ ký phương thức, kiểu dữ liệu, phạm vi truy cập hoặc tạo ca kiểm thử thất bại khi chạy `[yuan-2024]`, `[chen-2024]`.
2. **Bỏ sót nhánh điều kiện phức tạp và sinh test trùng lặp:** Gọi LLM một lượt không cung cấp tín hiệu thực thi về những đoạn mã đã được chạy qua. Trên các phương thức có nhiều nhánh lồng nhau, vòng lặp hoặc điều kiện kết hợp nhiều biến, LLM có xu hướng lặp lại các tình huống phổ biến và bỏ sót các nhánh biên khó phủ `[wang-2024]`, `[pizzorno-2025]`.
3. **Độ bao phủ cao không đồng nghĩa với phát hiện lỗi tốt:** Một bộ kiểm thử có thể phủ nhiều dòng và nhánh mã nguồn nhưng vẫn có câu khẳng định (assertion) yếu hoặc thiếu kiểm tra các tác dụng phụ nghiệp vụ, dẫn tới bỏ lọt các biến thể lỗi (mutants) quan trọng `[dakhel-2024]`, `[foster-2025]`.
4. **Bài toán test oracle (Test Oracle Problem):** Khi LLM suy luận giá trị đầu ra kỳ vọng (expected output) từ chính mã nguồn đối tượng, các ca kiểm thử sinh ra có nguy cơ "hợp thức hóa" chính lỗi đang tồn tại trong mã cài đặt nếu chỉ kiểm tra bằng tiêu chí chạy qua (pass) trên chương trình gốc `[hossain-2025]`, `[yuan-2024]`.

Từ các thách thức trên, **câu hỏi trọng tâm** của đề tài được đặt ra (nguyên văn từ `docs/de-xuat-de-tai-1.md`):

> *Làm thế nào để sử dụng LLM sinh ra bộ test hợp lệ, đạt độ bao phủ mã nguồn cao và có khả năng phát hiện lỗi tốt, và hiệu quả đó so với các phương pháp sinh test truyền thống như thế nào?*

---

## 1.3 Mục tiêu

Để giải quyết câu hỏi trọng tâm trên một cách có hệ thống và tái lập được, đề tài xác định năm mục tiêu cụ thể (theo `docs/de-xuat-de-tai-1.md` mục 3 và `docs/thiet-ke-ky-thuat.md`):

1. **Xây dựng đối tượng kiểm thử và chuẩn đối chiếu độc lập:** Thiết kế đặc tả đầu vào/đầu ra chặt chẽ (`docs/spec/`), cài đặt chương trình đối tượng bằng Java 17 (`subject/`), xây dựng chương trình tham chiếu độc lập từ đặc tả (`reference/`) để kiểm chứng tự động tính đúng của test oracle, và biên soạn bộ kiểm thử viết tay theo đặc tả (P1) làm chuẩn đối chiếu.
2. **Triển khai hai chiến lược sinh kiểm thử bằng LLM:** Xây dựng pipeline tự động cho chiến lược sinh trực tiếp một lượt (**P3 — Direct LLM Generation**) và chiến lược sinh lặp có phản hồi độ bao phủ (**P4 — Coverage-guided LLM Generation**).
3. **Đề xuất cơ chế cải tiến kết hợp lọc kiểm chứng và phản hồi đột biến:** Thiết kế bộ lọc test bốn tầng tự động (biên dịch, kiểm chứng oracle trên `reference/`, chạy xác nhận trên `subject/`, loại trùng lặp), chọn test tham lam theo mức đóng góp, và mở rộng vòng lặp với **phản hồi đột biến (P4b — Mutation-feedback LLM Generation)** nhằm đưa các mutant còn sống vào prompt để LLM sinh ca kiểm thử tiêu diệt chúng.
4. **Đo lường định lượng toàn diện:** Xây dựng quy trình đo cô lập, thống nhất để đo độ bao phủ câu lệnh/dòng (statement/line), nhánh (branch), hàm (function/method coverage), điểm đột biến (mutation score) trên 5 toán tử đột biến, cùng tỷ lệ biên dịch và tỷ lệ oracle đúng.
5. **Đánh giá thực nghiệm khách quan và phân tích hạn chế:** So sánh thực nghiệm P3, P4 và P4b với các phương pháp đối chứng (kiểm thử thủ công P1 và kiểm thử ngẫu nhiên P2) trên cùng chương trình và cùng điều kiện đo, áp dụng kiểm định thống kê chuẩn để trả lời các câu hỏi nghiên cứu RQ1–RQ6, đồng thời nêu rõ chi phí, thời gian và các giới hạn nơi LLM không vượt trội.

---

## 1.4 Câu hỏi nghiên cứu

Nghiên cứu tập trung trả lời sáu câu hỏi nghiên cứu (Research Questions — RQ1 đến RQ6), giữ nguyên văn từ đề xuất đề tài (`docs/de-xuat-de-tai-1.md` mục 6):

| Mã | Câu hỏi nghiên cứu | Trọng tâm đánh giá trong thiết kế thực nghiệm |
|---|---|---|
| **RQ1** | LLM sinh test đạt statement và branch coverage ở mức nào? | Đo `LINE` (đại diện statement) và `BRANCH` coverage của P3, P4, P4b qua 10 lần chạy và đặt cạnh đối chứng P1, P2. |
| **RQ2** | Coverage-guided có cải thiện coverage so với Direct LLM không? | So sánh theo cặp giữa P4 và P3 (cùng điểm khởi đầu `run-XX`) bằng kiểm định Mann–Whitney U và độ lớn hiệu ứng $\hat{A}_{12}$, kèm đường tiến triển coverage theo từng vòng lặp. |
| **RQ3** | Test do LLM sinh có phát hiện lỗi tốt hơn Random Testing không? | So sánh điểm đột biến (mutation score) trên 5 toán tử đột biến giữa P3, P4, P4b với P2 (Random Testing dùng oracle tham chiếu), kèm phân tích từng toán tử. |
| **RQ4** | Cung cấp thông tin nhánh chưa phủ có cải thiện chất lượng test không? | Đánh giá đồng thời tác động của phản hồi nhánh chưa phủ (P4 so với P3) và phản hồi mutant còn sống (P4b so với P4) lên branch coverage, mutation score, tỷ lệ hợp lệ và số test giữ lại. |
| **RQ5** | Mối quan hệ giữa số lượng test, coverage và mutation score? | Phân tích tương quan giữa kích thước bộ test hợp lệ, độ bao phủ mã nguồn và điểm đột biến trên các phương pháp P1–P4b, làm rõ hiện tượng bão hòa coverage so với sức mạnh diệt mutant. |
| **RQ6** | Chi phí và thời gian sử dụng LLM có tương xứng với hiệu quả đạt được không? | Đánh giá đánh đổi giữa thời gian sinh, thời gian thực thi, số lượt gọi LLM, tổng lượng token (tách bạch lưu ý token nền của CLI) và chi phí ước tính với mức tăng coverage và mutation score đạt được. |

---

## 1.5 Đóng góp và cấu trúc báo cáo

### Các đóng góp chính

1. **Bộ đối tượng thực nghiệm Java 17 đóng băng đặc tả kèm chuẩn oracle độc lập:** Cung cấp module `shop` (14 kiểu dữ liệu/dịch vụ, 235 dòng thực thi, 174 nhánh, 55 phương thức theo JaCoCo) với các nhánh khó phủ có chủ ý, bộ đặc tả chính thức (`docs/spec/`), chương trình tham chiếu `reference/` cài đặt độc lập (khác họ mô hình, không tiếp cận mã `subject/`) và bộ kiểm thử thủ công P1 truy vết đầy đủ đặc tả.
2. **Kiến trúc sinh – lọc – phản hồi hai giai đoạn có thiết kế bóc tách:** Kết hợp bộ lọc bốn tầng chứng minh tính đúng oracle một cách tự động (`tools/svv/test_filter.py`) với thiết kế ghép cặp tuần tự **P3 → P4 → P4b**, cho phép tách bạch rõ đóng góp của phản hồi độ bao phủ và đóng góp gia tăng của phản hồi đột biến.
3. **Hạ tầng đo lường cô lập và bộ đột biến lai (PIT + mã nguồn) cho đủ 5 toán tử:** Khắc phục khoảng trống của PIT trên bytecode (thiếu phép đổi trực tiếp `&&` ↔ `||` và một phần xóa câu lệnh) thông qua bộ sinh mutant mã nguồn `tools/mutate_custom.py`, thống nhất công thức tính điểm đột biến có loại mutant tương đương và giữ `NO_COVERAGE` trong mẫu số.
4. **Khung thực nghiệm lặp có thể tái lập:** Chuẩn hóa toàn bộ tham số vào cấu hình (`experiments/config/default.yaml`), ghi nhật ký đầy đủ mọi lời gọi LLM (`calls.jsonl`), áp dụng kiểm định phi tham số Mann–Whitney U và độ lớn hiệu ứng Vargha–Delaney $\hat{A}_{12}$ `[arcuri-2011]`.

### Cấu trúc báo cáo

Báo cáo được tổ chức thành 6 chương theo đúng cấu trúc chuẩn của đề tài:

- **Chương 1 — Giới thiệu:** Trình bày bối cảnh, vấn đề nghiên cứu, mục tiêu, sáu câu hỏi nghiên cứu (RQ1–RQ6), đóng góp chính và cấu trúc báo cáo.
- **Chương 2 — Cơ sở lý thuyết và công trình liên quan:** Hệ thống hóa lý thuyết về tiêu chí bao phủ (và cách đo của JaCoCo), kiểm thử đột biến, các phương pháp sinh test truyền thống, tổng quan các công trình sinh test bằng LLM, bài toán test oracle và bảy khoảng trống nghiên cứu.
- **Chương 3 — Phương pháp đề xuất:** Mô tả chi tiết kiến trúc tổng thể, chiến lược sinh test trực tiếp (P3), thuật toán sinh test lặp có phản hồi coverage (P4) và phản hồi mutation (P4b), cấu trúc thiết kế prompt, cơ chế thực thi – ghi log – phản hồi, cùng bộ lọc bốn tầng và chọn test tham lam.
- **Chương 4 — Thiết kế thực nghiệm:** Mô tả chi tiết chương trình đối tượng và các giới hạn thiết kế đã biết, môi trường và phiên bản công cụ, các phương pháp đối chứng (P1–P4b và lý do không thực hiện EvoSuite), mười chỉ số đánh giá cùng bảng ánh xạ 5 toán tử đột biến, và quy trình chạy lặp – phân tích thống kê.
- **Chương 5 — Kết quả và thảo luận** *(sẽ bổ sung sau khi hoàn tất chạy thực nghiệm chính thức)*: Trình bày kết quả đo lường, so sánh thống kê các phương pháp, trả lời từng câu hỏi RQ1–RQ6 và phân tích các mối đe dọa đến độ giá trị (Threats to Validity).
- **Chương 6 — Kết luận và hướng phát triển** *(sẽ bổ sung sau Chương 5)*: Tổng kết các phát hiện chính, hạn chế của phương pháp và hướng mở rộng trong tương lai.
