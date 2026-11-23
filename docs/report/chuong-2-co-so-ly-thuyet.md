# Chương 2. Cơ sở lý thuyết và công trình liên quan

Chương này trình bày nền tảng lý thuyết về kiểm thử đơn vị, tiêu chí độ bao phủ mã nguồn, kiểm thử đột biến, bài toán test oracle và tổng hợp có hệ thống 17 công trình nghiên cứu liên quan đã được xác minh thư mục tại `docs/literature/references.bib`.

---

## 2.1 Kiểm thử phần mềm và tiêu chí bao phủ

Kiểm thử đơn vị (unit testing) nhằm xác minh hành vi của từng đơn vị phần mềm độc lập (lớp, phương thức) thông qua các ca kiểm thử gồm hai thành phần: *tiền tố đầu vào* (test prefix — chuỗi khởi tạo đối tượng và lời gọi hàm) và *câu khẳng định đầu ra* (test oracle — giá trị trả về, thay đổi trạng thái hoặc ngoại lệ kỳ vọng). Hai cách tiếp cận thiết kế ca kiểm thử cơ bản là kiểm thử hộp đen (black-box testing) dựa trên đặc tả đầu vào/đầu ra và kiểm thử hộp trắng (white-box testing) dựa trên cấu trúc điều khiển bên trong mã nguồn.

Để định lượng mức độ mã nguồn được thực thi bởi một bộ kiểm thử, kỹ nghệ phần mềm sử dụng các tiêu chí độ bao phủ mã nguồn (code coverage criteria), trong đó ba tiêu chí phổ biến nhất là:

1. **Bao phủ câu lệnh (Statement Coverage):** Tỷ lệ phần trăm các câu lệnh thực thi được chạy ít nhất một lần khi thực thi bộ kiểm thử.
2. **Bao phủ nhánh (Branch Coverage):** Tỷ lệ phần trăm các nhánh quyết định (cả hai nhánh đúng và sai của biểu thức điều kiện trong câu lệnh `if`, vòng lặp, `switch`, toán tử logic ngắn mạch `&&`/`||`) đã được thực thi. Bao phủ nhánh chặt hơn bao phủ câu lệnh vì yêu cầu mỗi điều kiện phải được đánh giá theo mọi hướng rẽ.
3. **Bao phủ hàm / phương thức (Function / Method Coverage):** Tỷ lệ phần trăm các hàm, phương thức hoặc hàm khởi tạo (constructor) được gọi ít nhất một lần.

### Cách đo của JaCoCo trên nền tảng JVM

Trên hệ sinh thái Java, **JaCoCo** là công cụ đo độ bao phủ chuẩn thực hiện gắn mã theo dõi (instrumentation) trực tiếp ở mức **bytecode JVM** thay vì phân tích cây cú pháp trừu tượng (AST) của mã nguồn. Do đó, JaCoCo không có bộ đếm "statement" đúng nghĩa theo cú pháp Java mà cung cấp các bộ đếm ở mức bytecode gồm `INSTRUCTION`, `LINE`, `BRANCH`, `METHOD` và `CLASS` (`docs/thiet-ke-ky-thuat.md` mục 1.2). Trong đề tài này, việc ánh xạ ba tiêu chí bao phủ sang bộ đếm của JaCoCo được chuẩn hóa như sau (cài đặt tại `tools/svv/jacoco.py`):

- **Statement coverage $\approx$ `LINE`:** Một dòng mã nguồn chứa một hoặc nhiều chỉ thị bytecode được tính là *đã phủ* khi có ít nhất một chỉ thị của dòng đó được thực thi (`ci > 0`), và *chưa phủ* khi có chỉ thị nhưng chưa có chỉ thị nào chạy (`mi > 0` và `ci == 0`). Việc dùng `LINE` làm đại diện cho statement là quy ước phổ biến trong thực nghiệm Java và cần được nêu rõ khi báo cáo.
- **Branch coverage $=$ `BRANCH`:** JaCoCo thống kê số nhánh rẽ đã phủ (`cb`) và bị bỏ sót (`mb`) tại các chỉ thị nhảy có điều kiện trong bytecode. Một dòng có biểu thức điều kiện kết hợp nhiều mệnh đề ngắn mạch (như `a && b`) sinh nhiều nhánh bytecode riêng biệt; dòng chỉ đạt 100% nhánh khi `mb == 0`.
- **Function coverage $=$ `METHOD`:** Đếm mọi phương thức và constructor có mã thực thi trong package `shop`, bao gồm cả các phương thức do trình biên dịch sinh cho kiểu `enum` (`values()`, `valueOf()`).

Mặc dù độ bao phủ mã nguồn là thước đo hữu ích để phát hiện vùng mã chưa được chạm tới, nhiều nghiên cứu thực nghiệm chỉ ra rằng độ bao phủ cấu trúc cao chỉ là điều kiện cần chứ **không đủ** để bảo đảm khả năng phát hiện lỗi: một bộ kiểm thử vẫn có thể phủ hầu hết dòng và nhánh nhưng bỏ lọt lỗi nghiệp vụ nếu không có assertion đủ mạnh hoặc thiếu ca biên `[dakhel-2024]`, `[hossain-2025]`.

---

## 2.2 Kiểm thử đột biến

Kiểm thử đột biến (mutation testing) là kỹ thuật đánh giá độ mạnh phát hiện lỗi của bộ kiểm thử dựa trên lỗi nhân tạo (fault-based testing), được hệ thống hóa toàn diện trong khảo sát kinh điển của Jia và Harman `[jia-2011]`.

### Khái niệm và toán tử đột biến

Từ chương trình gốc $P$, công cụ kiểm thử đột biến áp dụng các **toán tử đột biến (mutation operators)** — tức các quy tắc biến đổi cú pháp nhỏ mô phỏng sai sót thường gặp của lập trình viên (như đổi phép so sánh `>` thành `>=`, đổi `+` thành `-`, đổi `&&` thành `||`, đảo hằng boolean hoặc xóa một câu lệnh) — để sinh ra tập các phiên bản sửa đổi gọi là **mutant**.

Khi chạy một bộ kiểm thử $T$ (vốn phải chạy xanh hoàn toàn trên chương trình gốc $P$) lên từng mutant $m$:

- **$m$ bị tiêu diệt (`KILLED`):** Có ít nhất một ca kiểm thử trong $T$ cho kết quả khác so với khi chạy trên $P$ (thất bại assertion hoặc phát sinh lỗi/ngoại lệ khác). Theo quy ước của PIT, các mutant gây treo quá thời gian (`TIMED_OUT`) hoặc tràn bộ nhớ (`MEMORY_ERROR`) cũng được tính là đã tiêu diệt (`tools/svv/pit.py`).
- **$m$ còn sống (`SURVIVED`):** Mã đột biến được thực thi nhưng mọi ca kiểm thử đều chạy qua, cho thấy bộ kiểm thử thiếu sức mạnh phân biệt hành vi sai tại vị trí đó.
- **$m$ không được thực thi (`NO_COVERAGE`):** Không có ca kiểm thử nào trong $T$ đi qua dòng chứa mutant $m$.

### Bài toán mutant tương đương và công thức Mutation Score

Một thách thức cố hữu của kiểm thử đột biến là sự tồn tại của **mutant tương đương (equivalent mutant)**: mutant khác chương trình gốc về mặt cú pháp nhưng tạo ra cùng đầu ra với $P$ trên mọi đầu vào hợp lệ nên về nguyên tắc không thể bị tiêu diệt bởi bất kỳ bộ kiểm thử nào `[jia-2011]`. Jia và Harman `[jia-2011]` khẳng định việc tự động nhận diện toàn bộ mutant tương đương là bài toán bất khả quyết (undecidable); dù các hướng tiếp cận gần đây sử dụng tác nhân LLM kết hợp tiền xử lý để hỗ trợ lọc mutant tương đương ở quy mô công nghiệp `[foster-2025]`, trong thiết kế học thuật có kiểm soát, việc rà soát thủ công và công khai danh sách loại trừ vẫn cần thiết để bảo đảm tính khách quan.

Để tránh làm sai lệch phép đo, các mutant tương đương phải được loại khỏi mẫu số khi tính **điểm đột biến (Mutation Score)** `[jia-2011]`:

$$\text{Mutation Score} = \frac{\text{Killed}}{\text{Total} - \text{Equivalent}} \times 100\%$$

Trong công thức trên, mọi mutant không được chạy tới (`NO_COVERAGE`) vẫn được **giữ trong mẫu số `Total`** để phản ánh trung thực cả lỗ hổng độ bao phủ lẫn độ yếu của assertion (`docs/thiet-ke-ky-thuat.md` mục 5.3).

### Giảm chi phí kiểm thử đột biến

Vì số lượng mutant tăng nhanh theo quy mô chương trình, Jia và Harman `[jia-2011]` phân loại các chiến lược giảm chi phí thành ba nhóm: *Do Faster* (tối ưu thực thi như lược đồ đột biến, chạy song song), *Do Fewer* (lấy mẫu mutant, chọn lọc tập toán tử — selective mutation, đột biến bậc cao — Higher-Order Mutation), và *Do Smarter* (ưu tiên thứ tự chạy kiểm thử). Trong thực hành Java hiện đại, việc chọn lọc một tập toán tử đại diện kết hợp thực thi song song giúp cân bằng giữa chi phí đo và độ tin cậy đánh giá.

---

## 2.3 Sinh kiểm thử tự động truyền thống

Trước khi LLM được ứng dụng rộng rãi, lĩnh vực sinh kiểm thử đơn vị tự động phát triển theo ba hướng chính:

### Kiểm thử ngẫu nhiên (Random Testing)

Kiểm thử ngẫu nhiên tạo nhanh các giá trị đầu vào và chuỗi lời gọi phương thức theo phân phối ngẫu nhiên với hạt giống (seed) xác định. Ưu điểm của phương pháp là tốc độ sinh rất nhanh, chi phí triển khai thấp và dễ tái lập. Tuy nhiên, như Godefroid và cộng sự `[godefroid-2005]` chỉ ra, kiểm thử ngẫu nhiên thuần túy rất khó chạm tới các nhánh có điều kiện ràng buộc hẹp (như kiểm tra đẳng thức chính xác, biên giá trị hoặc điều kiện kết hợp nhiều biến) và không tự tạo được oracle nghiệp vụ nếu không có mô hình đặc tả đi kèm.

### Thực thi tượng trưng động (Dynamic Symbolic Execution / Concolic Testing)

Để khắc phục nhược điểm của kiểm thử ngẫu nhiên, **DART** (*Directed Automated Random Testing*) `[godefroid-2005]` kết hợp ba cơ chế: trích xuất giao diện tự động để tạo trình điều khiển kiểm thử (test driver), sinh đầu vào ngẫu nhiên ban đầu, và thực thi tượng trưng động (concolic testing) thu thập ràng buộc đường đi (path constraints) trong quá trình chạy thực rồi dùng bộ giải ràng buộc đảo điều kiện nhánh để sinh đầu vào hướng tới đường thực thi mới. Thử nghiệm trên khoảng 600 hàm của thư viện C oSIP cho thấy DART gây crash cho 65% số hàm trong giới hạn 1.000 lần chạy mỗi hàm `[godefroid-2005]`. Dẫu vậy, thực thi tượng trưng động đối mặt với hiện tượng bùng nổ không gian trạng thái trên hệ thống phức tạp, đồng thời chỉ phát hiện lỗi tổng quát (crash, không dừng) nên không kiểm chứng được tính đúng nghiệp vụ khi thiếu đặc tả `[godefroid-2005]`.

### Sinh kiểm thử dựa trên tìm kiếm (Search-Based Software Testing — SBST)

SBST phát biểu bài toán sinh kiểm thử dưới dạng bài toán tối ưu hóa metaheuristic. Công cụ tiêu biểu cho Java là **EvoSuite** `[fraser-2011]`, sử dụng thuật toán di truyền (Genetic Algorithm) tiến hóa đồng thời **toàn bộ bộ kiểm thử (Whole Test Suite Generation)** theo hàm thích nghi dựa trên khoảng cách nhánh (branch distance) để tránh lãng phí ngân sách vào các mục tiêu không khả thi. Bên cạnh đó, EvoSuite tự động sinh assertion hồi quy bằng cách ghi nhận trạng thái hiện tại của chương trình rồi dùng phân tích đột biến để lọc, chỉ giữ các assertion giúp tiêu diệt ít nhất một mutant `[fraser-2011]`.

Dù đạt hiệu quả cao trên nhiều thư viện Java cổ điển, các công trình sau này ghi nhận bốn hạn chế rõ rệt của SBST:

- Dễ rơi vào **ngưỡng bão hòa coverage (coverage plateau)** khi cần sinh đối tượng có cấu trúc hoặc tiền điều kiện phức tạp `[lemieux-2023]`.
- Mã kiểm thử sinh ra khó đọc và thiếu tự nhiên so với kiểm thử do người viết `[yuan-2024]`.
- Assertion hồi quy mặc định hành vi hiện tại của mã nguồn là đúng nên có thể cố định lỗi nếu chương trình đang sai `[fraser-2011]`.
- Gặp vấn đề tương thích phiên bản JDK mới trong thực tế (ví dụ EvoSuite không chạy được trên dự án *Ecommerce-microservice* trong thực nghiệm của ChatUniTest `[chen-2024]`).

### Phương pháp luận thống kê cho thuật toán ngẫu nhiên

Do các thuật toán sinh test dựa trên ngẫu nhiên, tìm kiếm tiến hóa — và nay là LLM — đều mang tính bất định giữa các lần chạy, Arcuri và Briand `[arcuri-2011]` đề xuất bộ hướng dẫn chuẩn hóa đánh giá thực nghiệm: (1) lặp nhiều lần độc lập và giải trình rõ số lần chạy khi bị giới hạn bởi chi phí/thời gian; (2) sử dụng kiểm định phi tham số **Mann–Whitney U** thay cho kiểm định giả định phân phối chuẩn, kèm hiệu chỉnh đa so sánh (như Bonferroni) khi so nhiều cặp; (3) lượng hóa ý nghĩa thực tiễn bằng hệ số độ lớn hiệu ứng phi tham số **Vargha–Delaney $\hat{A}_{12}$** với các ngưỡng diễn giải chuẩn (gần 0,50 là không đáng kể; $\ge 0{,}56$ là nhỏ; $\ge 0{,}64$ là trung bình; $\ge 0{,}71$ là lớn); và (4) báo cáo thêm trung vị và khoảng tứ phân vị bên cạnh trung bình và độ lệch chuẩn `[arcuri-2011]`.

---

## 2.4 Sinh kiểm thử tự động bằng mô hình ngôn ngữ lớn

Sự xuất hiện của các mô hình ngôn ngữ huấn luyện trên mã nguồn đã thay đổi căn bản cách tiếp cận bài toán sinh kiểm thử tự động. Nghiên cứu về **Codex** và chuẩn đánh giá **HumanEval** (164 bài toán Python) `[chen-2021]` cho thấy việc lấy mẫu lặp cải thiện mạnh tỷ lệ giải đúng (Codex đạt `pass@1` 28,8% và tăng lên `pass@100` 70,2%), nhưng mô hình vẫn gặp khó khăn với chuỗi thao tác logic dài và nhạy cảm với cách diễn đạt prompt. Trên quy mô kho mã thực tế, chuẩn **SWE-bench** (2.294 issue từ 12 kho Python) `[jimenez-2024]` chứng minh rằng LLM suy giảm rõ rệt khi ngữ cảnh chứa nhiều mã không liên quan (tỷ lệ giải của Claude 2 với truy xuất BM25 giảm từ 1,96% ở cửa sổ 13k token xuống 1,22% ở 50k token) và thiếu vòng lặp tương tác với môi trường thực thi.

Trong giai đoạn 2023–2025, các nghiên cứu chuyên biệt về sinh unit test bằng LLM phát triển theo ba nhánh chính:

### Nhánh 1: Sinh trực tiếp kèm vòng sửa lỗi biên dịch và thực thi

- **TestPilot** `[schafer-2024]` sử dụng LLM tiền huấn luyện (chính là `gpt-3.5-turbo`, không fine-tune) để sinh unit test cho 25 gói npm JavaScript/TypeScript (1.684 hàm API). Prompt kết hợp chữ ký hàm, mã nguồn cài đặt và ví dụ sử dụng từ tài liệu; khi test thất bại, thông báo lỗi được gửi ngược để LLM sửa lặp. TestPilot đạt trung vị 70,2% statement coverage và 52,8% branch coverage (vượt Nessie ở mức 51,3% và 25,6%), đồng thời 92,8% test sinh ra không tương đồng quá 50% với test có sẵn (0% sao chép chính xác) `[schafer-2024]`. Tuy nhiên, công cụ chỉ sửa lỗi chạy qua mà không nhắm vào nhánh chưa phủ hay mutant còn sống.
- **ChatUniTest** `[chen-2024]` đề xuất khung cho **Java** gồm cơ chế *Adaptive Focal Context* (trích chọn lọc ngữ cảnh phụ thuộc vừa cửa sổ token) và quy trình *Generation–Validation–Repair* (kết hợp quy tắc sửa nhanh lỗi cú pháp và sửa lặp bằng LLM). Trên 4 dự án Java, ChatUniTest đạt tổng thể 59,6% line coverage, cao hơn TestSpark (42,1%) và EvoSuite (38,2%) `[chen-2024]`, nhưng vẫn thiếu phản hồi coverage động và phản hồi mutation.
- **ChatTester** `[yuan-2024]` tiến hành đánh giá thực nghiệm trên 1.000 phương thức Java và phát hiện một thực tế đáng chú ý: với ChatGPT mặc định gọi trực tiếp, chỉ **42,1%** phương thức test biên dịch thành công và chỉ **24,8%** chạy qua không lỗi, trong đó assertion sai là nguyên nhân lớn. Bằng kiến trúc hai bước (*Initial Test Generator* hiểu mục đích hàm + *Iterative Test Refiner* sửa lặp theo stack trace), ChatTester sinh thêm 34,3% test biên dịch được và thêm 18,7% test có assertion đúng so với ChatGPT mặc định `[yuan-2024]`.

### Nhánh 2: Sinh kiểm thử định hướng theo độ bao phủ (Coverage-guided) và phân rã mã

- **CodaMosa** `[lemieux-2023]` lai ghép SBST (Pynguin) với LLM (Codex) trên 486 mô-đun Python: khi tìm kiếm tiến hóa rơi vào ngưỡng bão hòa coverage, CodaMosa gọi LLM sinh test mẫu cho hàm còn thiếu coverage rồi nạp vào quần thể để SBST tiếp tục tiến hóa, đạt độ bao phủ cao hơn có ý nghĩa thống kê trên nhiều mô-đun hơn so với cả SBST thuần lẫn LLM thuần `[lemieux-2023]`.
- **TestGen-LLM** `[alshahwan-2024]` tại Meta mở rộng các lớp unit test Kotlin (Android) có sẵn theo nguyên tắc *Assured LLM-based Software Engineering*: mọi test do LLM đề xuất đều phải vượt qua chuỗi bộ lọc tự động gồm biên dịch thành công, chạy xanh ổn định và **thực sự làm tăng line/branch coverage**. Trên các sản phẩm Reels và Stories của Instagram, 75% ca biên dịch được, 57% chạy ổn định, 25% làm tăng coverage, cải thiện 11,5% số lớp sản phẩm và đạt tỷ lệ kỹ sư chấp nhận 73% `[alshahwan-2024]`.
- **SymPrompt** `[ryan-2024]` áp dụng chiến lược nhắc nhận thức mã (code-aware prompting) trên 897 phương thức Python bằng cách dùng TreeSitter phân tích tĩnh các ràng buộc đường đi rồi phân rã thành chuỗi prompt cho từng đường thực thi. Kết quả tăng số test đúng gấp 5 lần và coverage tương đối thêm 26% với CodeGen2, đồng thời tăng coverage tương đối 105% với GPT-4 so với prompt một lượt `[ryan-2024]`, nhưng phân tích tĩnh chỉ xấp xỉ đường đi và kéo theo chi phí token cao.
- **HITS** `[wang-2024]` giải quyết hiện tượng sụt giảm mạnh độ bao phủ của ChatUniTest trên các phương thức Java phức tạp (ví dụ trên Commons-CLI giảm từ 93,52% line / 96,36% branch xuống 51,17% / 45,83%). HITS dùng phân tích tĩnh và Chain-of-Thought chia phương thức thành các lát cắt (method slices), sinh test lần lượt cho từng lát cắt kèm tự sửa lỗi, đạt 73,00% branch coverage trên các phương thức phức tạp của Commons-CLI (so với 52,50% của EvoSuite và 45,83%–47,17% của các đối chứng LLM khác) `[wang-2024]`.
- **CoverUp** `[pizzorno-2025]` triển khai vòng lặp hội thoại tương tác có phản hồi coverage động (SlipCover) cho Python: đo dòng/nhánh chưa phủ, gửi ngữ cảnh đoạn mã bỏ sót cho LLM, chạy thử và tiếp tục hội thoại nếu test lỗi hoặc chưa tăng coverage. CoverUp đạt trung vị 80% line+branch coverage so với 47% của CodaMosa (line 81% vs 62%, branch 53% vs 35%) và 89%–90% so với 77% của MuTAP; phân tích bóc tách cho thấy chính vòng lặp phản hồi coverage đóng góp 40%–50% các ca thành công `[pizzorno-2025]`.

### Nhánh 3: Sinh kiểm thử định hướng theo kiểm thử đột biến (Mutation-guided)

- **MuTAP** `[dakhel-2024]` kết hợp LLM tiền huấn luyện với kiểm thử đột biến (MutPy) cho Python bằng cách đưa trực tiếp các **mutant còn sống** vào prompt tăng cường để LLM viết test có assertion chặt hơn. Trên HumanEval (mã lỗi tổng hợp), MuTAP đạt Mutation Score 93,57%; trên tập mã có lỗi thực do sinh viên viết (Refactory), MuTAP phát hiện nhiều hơn tới 28% đoạn mã lỗi so với các prompt zero-shot/few-shot và vượt trội Pynguin `[dakhel-2024]`. Tuy nhiên, đánh giá mới giới hạn ở các hàm Python ngắn, độc lập và chưa kết hợp vòng phản hồi coverage.
- **ACH** (*Automated Compliance Hardener*) `[foster-2025]` triển khai ở quy mô công nghiệp tại Meta trên 10.795 lớp Kotlin (Android): dùng LLM sinh 9.095 mutant mục tiêu mô phỏng rủi ro tuân thủ quyền riêng tư, lọc mutant tương đương bằng tác nhân LLM kết hợp tiền xử lý (đạt precision 0,95 và recall 0,96), sau đó sinh 571 test case diệt mutant với tỷ lệ kỹ sư chấp nhận 73% `[foster-2025]`.

### Bảng tổng hợp các công trình liên quan

Bảng 2.1 tổng hợp 17 công trình nền tảng và liên quan trực tiếp (trích chuẩn hóa từ `docs/literature/README.md`).

| Khóa trích dẫn | Năm | Nơi công bố | Ngôn ngữ đích | Phản hồi coverage | Phản hồi mutation | Xử lý oracle | Chỉ số chính |
|---|---:|---|---|---|---|---|---|
| `[godefroid-2005]` DART | 2005 | PLDI '05 | C | Đảo nhánh tượng trưng động | Không | Lỗi tổng quát (crash / không dừng) | % hàm crash, số lỗi |
| `[fraser-2011]` EvoSuite | 2011 | ESEC/FSE '11 | Java | Có (GA theo branch) | Lọc assertion | Assertion hồi quy theo mã gốc | Branch coverage, kích thước bộ test |
| `[jia-2011]` Mutation Survey | 2011 | IEEE TSE | Đa ngôn ngữ | Không áp dụng (khảo sát) | Không áp dụng (khảo sát) | Phân tích lý thuyết oracle & tương đương | Phân loại >390 công trình, Mutation Score |
| `[arcuri-2011]` Statistical Guide | 2011 | ICSE '11 | Chung | Không áp dụng (thống kê) | Không áp dụng (thống kê) | Không áp dụng | Mann–Whitney U, $\hat{A}_{12}$, số lần chạy |
| `[chen-2021]` Codex / HumanEval | 2021 | arXiv | Python | Không | Không | Unit test ẩn viết tay | `pass@k` |
| `[lemieux-2023]` CodaMosa | 2023 | ICSE '23 | Python | Có (kích hoạt LLM khi bão hòa) | Không | Kế thừa regression assertion Pynguin | Line/branch coverage |
| `[jimenez-2024]` SWE-bench | 2024 | ICLR '24 | Python | Không | Không | Test Fail-to-Pass từ PR thực tế | % issue giải quyết, recall BM25 |
| `[schafer-2024]` TestPilot | 2024 | IEEE TSE | JS / TS | Không (chỉ sửa lỗi thực thi) | Không | Chạy xanh trên mã gốc | Statement/branch coverage, tính mới |
| `[chen-2024]` ChatUniTest | 2024 | FSE Comp. '24 | Java | Không (chỉ sửa lỗi biên dịch/chạy) | Không | Chạy xanh trên mã gốc | Line/branch coverage |
| `[yuan-2024]` ChatTester | 2024 | PACMSE (FSE '24) | Java | Không (sửa lặp lỗi thực thi) | Không | Sửa lặp theo mã gốc; đo % assertion đúng | % biên dịch, % chạy qua, % assertion đúng |
| `[dakhel-2024]` MuTAP | 2024 | Inf. Softw. Technol. | Python | Không | Có (mutant sống vào prompt) | Sửa cú pháp/chức năng theo mã gốc | Mutation Score, % phát hiện mã lỗi thật |
| `[alshahwan-2024]` TestGen-LLM | 2024 | FSE Comp. '24 | Kotlin | Có (lọc giữ test tăng coverage) | Không | Chạy xanh ổn định + kỹ sư duyệt tay | % biên dịch, % ổn định, % tăng coverage, % chấp nhận |
| `[ryan-2024]` SymPrompt | 2024 | PACMSE (FSE '24) | Python | Có (ràng buộc đường đi tĩnh) | Không | Hồi quy theo hành vi mã hiện tại | Số test đúng, line/branch coverage |
| `[wang-2024]` HITS | 2024 | ASE '24 | Java | Có (lát cắt phương thức) | Không | Chạy xanh trên mã gốc | Line/branch coverage |
| `[pizzorno-2025]` CoverUp | 2025 | PACMSE (FSE '25) | Python | Có (hội thoại lặp theo dòng/nhánh) | Không | Chạy xanh trên mã gốc | Line, branch, line+branch coverage |
| `[foster-2025]` ACH | 2025 | FSE Comp. '25 | Kotlin | Không | Có (LLM mutant + lọc tương đương + diệt) | Chạy xanh mã gốc + kỹ sư duyệt tay | Số mutant/test, P/R lọc tương đương, % chấp nhận |
| `[hossain-2025]` TOGLL | 2025 | ICSE '25 | Java | Không | Đo độ mạnh oracle | Fine-tune LLM sinh assertion/exception | Số oracle đúng, số mutant duy nhất diệt được, Defects4J |

---

## 2.5 Bài toán test oracle

Bài toán test oracle (Test Oracle Problem) — xác định cơ chế đáng tin cậy để phân định kết quả thực thi của chương trình là đúng hay sai với một đầu vào cho trước — là một trong những thách thức khó nhất của kiểm thử tự động `[jia-2011]`.

Trong các công cụ truyền thống, DART `[godefroid-2005]` né bài toán oracle chức năng bằng cách chỉ tìm lỗi crash, còn EvoSuite `[fraser-2011]` ghi lại hành vi hiện có của chương trình làm assertion hồi quy. Cách xử lý tương tự vẫn lặp lại ở phần lớn các khung sinh test bằng LLM hiện nay: TestPilot `[schafer-2024]`, ChatUniTest `[chen-2024]`, SymPrompt `[ryan-2024]`, HITS `[wang-2024]` và CoverUp `[pizzorno-2025]` đều chấp nhận ca kiểm thử khi nó chạy xanh trên chính mã nguồn hiện tại. Cách làm này mang theo hai rủi ro lớn: (1) nếu mã nguồn đang chứa lỗi, test do LLM sinh có thể khẳng định lại chính hành vi sai đó; và (2) ngay cả khi mã nguồn đúng, LLM vẫn thường sinh assertion sai logic hoặc assertion quá yếu không phát hiện được biến đổi hành vi `[yuan-2024]`. Ở môi trường công nghiệp, TestGen-LLM `[alshahwan-2024]` và ACH `[foster-2025]` buộc phải đặt kỹ sư phần mềm vào bước duyệt cuối cùng để thẩm định tính đúng ngữ nghĩa.

Công trình **TOGLL** `[hossain-2025]` đi trực diện vào bài toán này trên Java bằng cách tinh chỉnh (fine-tune) 7 code LLM (cỡ 110M–2,7B tham số) trên SF110 và tách bạch rõ hai thuộc tính của oracle: *tính đúng (correctness)* và *độ mạnh phát hiện lỗi/mutant (strength)*. Trên 25 dự án Java chưa thấy và Defects4J, TOGLL sinh số assertion oracle đúng nhiều gấp 3,8 lần, exception oracle đúng nhiều gấp 4,9 lần so với TOGA và tiêu diệt được 1.023 mutant duy nhất mà EvoSuite bỏ sót `[hossain-2025]`. Tuy nhiên, TOGLL chỉ sinh oracle cho tiền tố kiểm thử có sẵn, yêu cầu quá trình fine-tune tốn kém và vẫn còn tỷ lệ oracle sai nhất định nên chưa tự chứng minh tuyệt đối tính tuân thủ đặc tả.

Để giải quyết trực tiếp bài toán chứng minh tính đúng của oracle trong khuôn khổ không cần fine-tune mô hình, đề tài sử dụng một **đặc tả đầu vào/đầu ra đóng băng** (`docs/spec/`) làm nguồn chân lý và xây dựng một **chương trình tham chiếu cài đặt độc lập (`reference/`)** (thiết kế 4.1, D-006) để kiểm chứng tự động mọi ca kiểm thử do LLM sinh ra.

---

## 2.6 Khoảng trống nghiên cứu

Tổng hợp từ 17 công trình ở Bảng 2.1 (`docs/literature/README.md`), bảy khoảng trống nghiên cứu sau đây định hình trực tiếp thiết kế phương pháp ở Chương 3 và thiết kế thực nghiệm ở Chương 4:

1. **Tách rời giữa phản hồi coverage và phản hồi mutation:** Các công trình hiện có thường chỉ tối ưu độ bao phủ (`[lemieux-2023]`, `[alshahwan-2024]`, `[ryan-2024]`, `[wang-2024]`, `[pizzorno-2025]`) hoặc chỉ dùng phản hồi mutation riêng lẻ (`[dakhel-2024]`, `[foster-2025]`), chưa kết hợp tuần tự hai vòng phản hồi kèm thí nghiệm bóc tách (**P3 → P4 → P4b**) để lượng hóa riêng đóng góp của phản hồi mutation sau khi độ bao phủ đã cao.
2. **Thiếu cơ chế tự động kiểm chứng tính đúng của oracle độc lập với mã nguồn:** Gần như toàn bộ công trình sinh test tự động mặc định test chạy xanh trên mã gốc là hợp lệ hoặc dựa vào kỹ sư duyệt tay (`[alshahwan-2024]`, `[foster-2025]`); riêng TOGLL `[hossain-2025]` cần fine-tune và không sinh đầu vào. Đề tài bổ sung **chương trình tham chiếu `reference/` độc lập** trong bộ lọc để đo tường minh tỷ lệ biên dịch và tỷ lệ oracle đúng.
3. **Thiếu đánh giá tích hợp coverage động + mutation động trên Java hướng đối tượng:** Các công trình có vòng phản hồi coverage động hoặc mutation-feedback chủ yếu đánh giá trên Python (`[lemieux-2023]`, `[pizzorno-2025]`, `[ryan-2024]`, `[dakhel-2024]`), JavaScript/TypeScript (`[schafer-2024]`) hoặc Kotlin nội bộ (`[alshahwan-2024]`, `[foster-2025]`), trong khi các công trình Java (`[chen-2024]`, `[yuan-2024]`, `[wang-2024]`) chưa tích hợp đồng thời phản hồi JaCoCo và phản hồi PIT.
4. **Thiếu đối chứng đồng thời với kiểm thử viết tay theo đặc tả và kiểm thử ngẫu nhiên có oracle chuẩn:** Các nghiên cứu thường chỉ so LLM với SBST hoặc giữa các biến thể prompt, hiếm khi đặt song song **Manual Testing theo đặc tả (P1)**, **Random Testing có oracle tham chiếu (P2)**, **Direct LLM (P3)**, **Coverage-guided LLM (P4)** và **Mutation-feedback LLM (P4b)** trên cùng một chương trình và cùng quy trình đo.
5. **Đo lường độ mạnh phát hiện lỗi chưa đồng bộ với bài toán mutant tương đương:** Nhiều công trình LLM chỉ báo cáo line/branch coverage mà không đo mutation score (`[schafer-2024]`, `[lemieux-2023]`, `[pizzorno-2025]`, `[ryan-2024]`, `[wang-2024]`, `[alshahwan-2024]`); ngược lại, ACH `[foster-2025]` không công khai mã/benchmark để tái lập. Đề tài áp dụng đầy đủ công thức chuẩn của Jia & Harman `[jia-2011]`, giữ `NO_COVERAGE` trong mẫu số và công khai sổ rà soát `equivalent-mutants.csv` cho 5 toán tử đột biến.
6. **Thiếu phân tích cân đối chi phí–hiệu quả gắn với kiểm định thống kê chuẩn:** Dù các vòng lặp nhiều lượt đều làm tăng chi phí token và thời gian (`[pizzorno-2025]`, `[yuan-2024]`, `[ryan-2024]`, `[wang-2024]`), ít công trình báo cáo ngân sách token song song với chạy lặp nhiều lần, kiểm định **Mann–Whitney U** và độ lớn hiệu ứng **Vargha–Delaney $\hat{A}_{12}$** `[arcuri-2011]` để đánh giá khách quan câu hỏi đánh đổi chi phí–hiệu quả (RQ6).
7. **Nguy cơ ghi nhớ dữ liệu huấn luyện (data contamination) ở benchmark lâu năm:** Các dự án Java mã nguồn mở phổ biến (như Commons-CLI, Defects4J) có khả năng đã xuất hiện trong tập huấn luyện của LLM, đồng thời công cụ cũ như EvoSuite còn gặp lỗi tương thích JDK mới `[chen-2024]`. Việc xây dựng module đối tượng Java 17 mới với đặc tả chặt chẽ và các nhánh khó phủ có chủ ý giúp đánh giá khách quan hơn cả năng lực thực lẫn giới hạn của LLM.
