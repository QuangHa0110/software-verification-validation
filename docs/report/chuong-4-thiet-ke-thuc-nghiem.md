# Chương 4. Thiết kế thực nghiệm

Chương này mô tả chi tiết đối tượng thực nghiệm, môi trường và phiên bản công cụ, các phương pháp đối chứng (gồm cả nhánh thí nghiệm bóc tách ngữ cảnh hạn chế theo D-024), hệ thống chỉ số đánh giá cùng bảng ánh xạ năm toán tử đột biến, và quy trình chạy thực nghiệm – phân tích thống kê nhằm bảo đảm tính khách quan và khả năng tái lập. Mọi thông số cấu trúc và cấu hình đều trích trực tiếp từ mã nguồn và tài liệu trong kho lưu trữ; chương này **không** trình bày số liệu kết quả thực nghiệm (thuộc Chương 5).

---

## 4.1 Chương trình đối tượng

Chương trình đối tượng kiểm thử (`subject/`) cài đặt **module giỏ hàng và thanh toán** của hệ thống bán hàng trực tuyến trong package `shop` (Java 17). Chương trình được thiết kế thuần nghiệp vụ trong bộ nhớ (không dùng cơ sở dữ liệu, mạng, đồng hồ hệ thống hay trạng thái toàn cục): mọi số tiền biểu diễn bằng kiểu số nguyên `long` (đơn vị VND, phép chia lấy phần nguyên làm tròn xuống), mọi mốc thời gian được truyền tường minh qua `LocalDate` / `LocalDateTime` để bảo đảm tính xác định (deterministic), và thứ tự kiểm tra điều kiện cùng kiểu ngoại lệ không kiểm tra (unchecked exceptions) là một phần bắt buộc của hợp đồng đặc tả (`docs/spec/README.md`, `docs/spec/open-questions.md`).

### Cấu trúc lớp và phương thức

Bảng 4.1 liệt kê toàn bộ **14 kiểu cấp cao nhất** (3 `enum`, 3 ngoại lệ nghiệp vụ, 4 lớp miền dữ liệu `final` và 4 lớp dịch vụ/nghiệp vụ) cùng các thành viên công khai và package-private đã đóng băng tại mốc M1 (`docs/spec/api.md`).

| Nhóm | Kiểu trong package `shop` | Hằng số / Phương thức chính (theo `docs/spec/api.md`) | Vai trò nghiệp vụ |
|---|---|---|---|
| Enum | `Region` | `INNER_CITY`, `SUBURB`, `PROVINCE`, `REMOTE` (`values()`, `valueOf()`) | Vùng giao hàng quyết định phí cơ bản và điều kiện miễn phí |
| Enum | `CouponType` | `PERCENT`, `FIXED` (`values()`, `valueOf()`) | Loại mã giảm giá theo phần trăm hoặc số tiền cố định |
| Enum | `OrderStatus` | `PENDING`, `PAID`, `SHIPPED`, `CANCELLED` (`values()`, `valueOf()`) | Các trạng thái trong vòng đời đơn hàng |
| Ngoại lệ | `InsufficientStockException` | `InsufficientStockException(String message)` | Báo lỗi khi số lượng yêu cầu vượt tồn kho hiện tại |
| Ngoại lệ | `CouponExpiredException` | `CouponExpiredException(String message)` | Báo lỗi khi ngày áp dụng muộn hơn hạn sử dụng mã |
| Ngoại lệ | `CouponNotApplicableException` | `CouponNotApplicableException(String message)` | Báo lỗi khi giá trị tạm tính thấp hơn `minOrder` |
| Dữ liệu | `final class Product` | `Product(id, name, price, weightGrams, stock)`; 5 getter; `reduceStock(q)`; `restoreStock(q)` | Lưu thông tin sản phẩm và quản lý tồn kho khả biến |
| Dữ liệu | `final class Coupon` | `Coupon(code, type, value, minOrder, maxDiscount, expiry)`; 6 getter | Biểu diễn mã giảm giá bất biến kèm ràng buộc phần trăm/ngưỡng/trần |
| Dữ liệu | `final class CartItem` | `CartItem(product, quantity)`; `getProduct()`, `getQuantity()`, `getLineTotal()` | Dòng hàng bất biến trong giỏ và bản chụp đơn hàng |
| Dữ liệu | `final class Order` | Constructor package-private `Order(...)`; 9 getter; package-private `setStatus(status)` | Bản chụp bất biến của đơn hàng (trừ trạng thái) kèm kiểm tra nhất quán tiền |
| Dịch vụ | `class Cart` | `MAX_QTY_PER_ITEM = 50`; `Cart()`; `addItem(p, qty)`; `updateQuantity(id, qty)`; `removeItem(id)`; `getSubtotal()`; `getTotalWeightGrams()`; `isEmpty()`; `getItems()` *(kèm hàm nội bộ `putItem`)* | Quản lý dòng hàng theo thứ tự thêm, giới hạn 50/dòng, kiểm tra tồn kho |
| Dịch vụ | `class PricingService` | `PricingService()`; `calculateDiscount(subtotal, coupon, today)` | Kiểm tra hiệu lực mã, ngưỡng đơn tối thiểu, tính giảm `%` có trần hoặc `FIXED` |
| Dịch vụ | `class ShippingCalculator` | `ShippingCalculator()`; `calculate(region, weightGrams, orderValue)` | Miễn phí vận chuyển theo vùng/giá trị sau giảm, `switch` phí cơ bản, vòng lặp phụ phí mỗi 500 g vượt 1.000 g |
| Dịch vụ | `class OrderService` | `OrderService()`; `OrderService(pricing, shipping)`; `placeOrder(...)`; `pay(order)`; `ship(order)`; `cancelOrder(order, now)` | Điều phối đặt đơn nguyên tử, chuyển trạng thái và tính tiền hoàn/hoàn kho khi hủy |

### Quy mô cấu trúc theo JaCoCo

Theo kết quả phân tích cấu trúc của JaCoCo 0.8.14 trên toàn bộ package `shop` trong `subject/` (`docs/orchestration/progress.md`), tổng số phần tử cần bao phủ của chương trình đối tượng là:

- **Tổng số dòng thực thi (`LINE`, đại diện statement):** **235**
- **Tổng số nhánh quyết định (`BRANCH`):** **174**
- **Tổng số phương thức (`METHOD`):** **55** (gồm các constructor, phương thức nghiệp vụ, phương thức nội bộ `Cart.putItem` và các phương thức `enum` do trình biên dịch sinh)

### Các nhánh khó phủ có chủ ý

Để tạo không gian đánh giá rõ sự khác biệt giữa sinh trực tiếp (P3), phản hồi coverage (P4) và phản hồi mutation (P4b), chương trình đối tượng được thiết kế chủ động bao hàm tám nhóm tình huống phức tạp (`docs/thiet-ke-ky-thuat.md` mục 3.7, `docs/spec/branches.md`, `docs/orchestration/progress.md`):

1. **Ngưỡng miễn phí vận chuyển tính trên giá trị sau giảm giá:** Đơn có `subtotal >= 500.000` VND nhưng `goods = subtotal - discount < 500.000` VND vẫn phải chịu phí vận chuyển.
2. **Biên mã giảm giá `PERCENT`:** Phân biệt trần `maxDiscount == 0` (không giới hạn), chiết khấu dưới trần, bằng trần và vượt trần tại đúng ngưỡng `minOrder`, kết hợp làm tròn nguyên xuống.
3. **Biên ngày hết hạn của mã giảm giá:** `today.equals(expiry)` vẫn hợp lệ, chỉ ném `CouponExpiredException` khi `today.isAfter(expiry)`.
4. **Biên khối lượng và vòng lặp phụ phí vận chuyển:** Các điểm chuyển nấc 1.000 g, 1.001 g, 1.500 g, 1.501 g, 20.000 g và 20.001 g (ném `IllegalArgumentException` kể cả khi đơn đạt ngưỡng miễn phí).
5. **Biên 24 giờ khi hủy đơn `PAID`:** Hủy đúng tại `createdAt.plusHours(24)` được hoàn 100% `total` (gồm phí vận chuyển); muộn hơn 1 giây chỉ hoàn `(subtotal - discount) * 80 / 100` (chia nguyên xuống, không hoàn phí vận chuyển).
6. **Tính nguyên tử (atomicity) của `placeOrder`:** Nếu kiểm tra tồn kho thất bại ở dòng sau, mã lỗi hoặc tổng khối lượng vượt 20.000 g, không một sản phẩm nào được phép bị trừ tồn kho.
7. **Thứ tự kiểm tra ngoại lệ khi vi phạm đồng thời nhiều điều kiện:** Ví dụ `addItem` kiểm tra `newQty > 50` trước thiếu tồn; `updateQuantity` kiểm tra `qty < 0` trước tra ID vắng mặt; `calculateDiscount` kiểm tra hết hạn trước dưới ngưỡng `minOrder`; `cancelOrder` kiểm tra trạng thái `SHIPPED`/`CANCELLED` trước lỗi thời điểm `now.isBefore(createdAt)`.
8. **Nhánh phòng thủ ở mức package-private:** Constructor `Order(...)` kiểm tra nhất quán `subtotal`, `discount`, `shipping`, `total`, danh sách dòng chứa phần tử `null`, và `Order.setStatus(null)` — các nhánh này không thể chạm tới nếu chỉ gọi qua `OrderService`, mà đòi hỏi ca kiểm thử cùng package `shop` gọi trực tiếp.

### Giới hạn thiết kế đã biết của chương trình đối tượng

Hai hạn chế nội tại của chương trình đối tượng được ghi nhận thẳng ngay ở giai đoạn thiết kế để làm nền cho phần bàn luận độ giá trị (Chương 5):

- **Hiệu ứng trần độ bao phủ (coverage ceiling effect):** Do quy mô module vừa phải (235 dòng, 174 nhánh) và bộ kiểm thử thủ công P1 đã đạt độ bao phủ tối đa **100%** trên cả ba bộ đếm (`LINE` 235/235, `BRANCH` 174/174, `METHOD` 55/55 — `docs/orchestration/progress.md`, D-020), khoảng cách coverage giữa các phương pháp LLM mạnh trong điều kiện đủ ngữ cảnh có thể bị nén sát ngưỡng trần. Thực tế thí nghiệm sơ bộ đã xác nhận hiện tượng này và dẫn tới quyết định bổ sung nhánh thí nghiệm bóc tách **ngữ cảnh hạn chế** (P3r, P4r, P4br — D-024, trình bày tại mục 4.3 và 4.5), đồng thời giữ nguyên nhánh chính để đánh giá trung thực cả mutation score, tỷ lệ biên dịch, tỷ lệ oracle đúng và chi phí token/thời gian.
- **Không có hằng literal boolean (`true`/`false`) trong mã nguồn `subject/` (D-016):** Kết quả rà soát toàn bộ `subject/src/main/java/shop/*.java` ghi nhận **0** literal `true`/`false`. Do đặc tả và mã nguồn đã đóng băng tại mốc M1 (không chỉnh sửa lại `subject/`, `reference/` và P1), toán tử đột biến thay hằng boolean chỉ tác động được lên giá trị trả về của phương thức boolean duy nhất là `Cart.isEmpty()`, tạo ra đúng **2 mutant** (xem mục 4.4).

---

## 4.2 Môi trường thực nghiệm

Toàn bộ phiên bản công cụ, thư viện và cấu hình LLM được ghim cố định trong kho lưu trữ nhằm bảo đảm khả năng tái lập. Bảng 4.2 tổng hợp chi tiết môi trường thực nghiệm.

| Thành phần | Phiên bản / Giá trị | Nguồn xác minh trong kho lưu trữ |
|---|---|---|
| Hệ điều hành | Windows 11 x64 | `experiments/spikes/s1-s2/KET-QUA.md` |
| JDK | Oracle JDK **17.0.12 LTS** (`maven.compiler.release=17`) | D-004, `subject/pom.xml`, `experiments/spikes/s1-s2/KET-QUA.md` |
| Build tool | Apache Maven **3.9.15** | D-004, `experiments/spikes/s1-s2/KET-QUA.md` |
| Maven Plugins | `maven-compiler-plugin` **3.14.1**, `maven-surefire-plugin` **3.5.4**, `build-helper-maven-plugin` **3.6.1** (`subject/`) / **3.6.0** (`reference/`) | `subject/pom.xml`, `reference/pom.xml`, `docs/orchestration/progress.md` |
| Framework kiểm thử | JUnit Jupiter **5.13.4** | `subject/pom.xml` |
| Công cụ đo coverage | JaCoCo (`jacoco-maven-plugin`) **0.8.14** | `subject/pom.xml` |
| Công cụ mutation | PIT (`pitest-maven`) **1.20.3** + `pitest-junit5-plugin` **1.2.3** | `subject/pom.xml` |
| Python | Python **3.12.16** (môi trường conda `svv`) | D-004 |
| Thư viện Python | `anthropic==2.12.0`, `pyyaml==6.0.4`, `scipy==1.18.1`, `numpy==2.5.5`, `matplotlib==3.11.2`, `pytest==9.2.0`, `jsonschema==4.26.0` | `requirements.txt` |
| LLM chính & CLI | Provider `codex-cli` (`codex-cli` **0.171.0**), model `gpt-6.2-astra`, `effort = medium` (thay cho `temperature`), `max_output_tokens = 16000`, `llm.timeout_s = 300` (ghi đè `1800` s cho P3/P4/P4b) | `experiments/config/default.yaml`, D-001, D-005 |
| LLM dự phòng & CLI | Provider `agy-cli` (Antigravity CLI **1.5.14**), mức suy luận nằm trong tên model (kiểm khói S3 trên `gemini-3.8-flash-low`) | D-001, D-015, D-021, `experiments/spikes/s3/KET-QUA.md` |
| Provider API tùy chọn | `anthropic` (đọc `ANTHROPIC_API_KEY` từ `.env`, không dùng mặc định) | D-005, `tools/svv/llm_client.py` |
| Đơn giá quy đổi USD | `price_per_mtok_input: null`, `price_per_mtok_output: null` | `experiments/config/default.yaml` `[CHỜ: cập nhật đơn giá quy đổi nếu áp dụng]` |
| Phần cứng máy chạy | `[CHỜ: cấu hình CPU/RAM máy chạy đợt thực nghiệm chính thức T14]` | — |

**Hạn chế môi trường đã biết:** Như đã phân tích tại mục 3.5, việc gọi LLM thông qua CLI (`codex-cli`, `agy-cli`) thay vì API trực tiếp (D-005) kéo theo lượng **token nền cố định** trong mỗi lời gọi ($\approx$ 22.000 token ở `codex-cli` và $\approx$ 15.500 token ở `agy-cli`) cũng như ràng buộc hạn mức theo cửa sổ thời gian (D-015). Vì `price_per_mtok_*` đang để `null` khi chạy bằng gói quota CLI, chi phí LLM được đánh giá chủ yếu qua tổng số lượt gọi (`calls`), `input_tokens`, `output_tokens` và thời gian sinh (`generation_s`); trường `cost_usd` sẽ được tính bổ sung nếu áp bảng giá tham chiếu công khai ở T14.

---

## 4.3 Phương pháp đối chứng

Thực nghiệm chính thức so sánh **năm phương pháp** trên cùng chương trình đối tượng `subject/` và cùng quy trình đo (`experiments/scripts/measure.py`), gồm hai phương pháp đối chứng truyền thống/thủ công và ba chiến lược LLM đủ ngữ cảnh (D-014); đồng thời mở rộng thêm **ba cấu hình bóc tách ngữ cảnh hạn chế** theo quyết định D-024 (Bảng 4.3).

| Nhánh | Mã | Định danh hệ thống | Cách sinh test | Nguồn oracle | Số lần chạy | Vai trò |
|---|---|---|---|---|---:|---|
| Chính | **P1** | `p1-manual` | Viết tay theo đặc tả `docs/spec/`, **không xem mã `subject/`** (D-006); truy vết 32/32 nhóm tình huống trong `branches.md` (181 ca: 54 `@Test` + 127 ca tham số) | Con người suy từ đặc tả | 1 (`run-01`) | Đối chứng hộp đen chuẩn & bộ kiểm chứng `subject ≡ reference` |
| Chính | **P2** | `p2-random` | Bộ sinh Java `tools/random-gen/` (`p2.Generator`) điều phối qua `tools/generate_p2.py`; sinh 100 ca kiểm thử/lần từ seed cố định `1000 + run` | Thực thi kịch bản trực tiếp trên `reference/` để phát sinh `assertEquals`/`assertThrows` cụ thể | 10 (`run-01`..`run-10`) | Đối chứng tự động truyền thống (kiểm thử ngẫu nhiên có oracle chuẩn) |
| Chính | **P3** | `p3-direct-llm` | Gọi LLM một lượt từ hợp đồng API + đặc tả + toàn bộ mã nguồn (`prompts/direct/`), lọc bốn tầng qua `test_filter.py` | LLM tự sinh, kiểm chứng trên `reference/` | 10 (`run-01`..`run-10`) | Đối chứng LLM cơ sở (đủ ngữ cảnh, không phản hồi) |
| Chính | **P4** | `p4-coverage-guided` | Khởi đầu từ P3 `run-XX`, lặp phản hồi nhánh/dòng chưa phủ từ JaCoCo (`prompts/coverage-guided/`) + lọc bốn tầng + chọn tham lam lười | LLM tự sinh, kiểm chứng trên `reference/` | 10 (`run-01`..`run-10`) | Phương pháp đề xuất (phản hồi coverage, đủ ngữ cảnh) |
| Chính | **P4b** | `p4b-mutation-feedback` | Khởi đầu từ bộ cuối P4 `run-XX`, lặp phản hồi mutant còn sống từ PIT + `mutate_custom.py` (`prompts/mutation-feedback/`) + lọc + chọn tham lam lười | LLM tự sinh, kiểm chứng trên `reference/` | 10 (`run-01`..`run-10`) | Phương pháp cải tiến (bóc tách phản hồi mutation, đủ ngữ cảnh) |
| Bóc tách (D-024) | **P3r** | `p3r-spec-only` | Gọi LLM một lượt **chỉ từ hợp đồng API + đặc tả** (`prompts/direct-spec-only/`, không cấp mã `subject/`), lọc bốn tầng | LLM tự sinh từ đặc tả, kiểm chứng trên `reference/` | 10 *(tối thiểu 5 nếu giới hạn quota)* | Khởi điểm hộp đen cho thí nghiệm bóc tách ngữ cảnh hạn chế |
| Bóc tách (D-024) | **P4r** | `p4r-coverage-guided` | Khởi đầu từ P3r `run-XX`, lặp phản hồi JaCoCo (`prompts/coverage-guided-reduced/`), **chỉ cấp đoạn mã cục bộ ±2 dòng** quanh điểm chưa phủ + lọc + chọn tham lam lười | LLM tự sinh, kiểm chứng trên `reference/` | 10 *(tối thiểu 5 nếu giới hạn quota)* | Đánh giá riêng hiệu quả phản hồi coverage khi thoát hiệu ứng trần |
| Bóc tách (D-024) | **P4br** | `p4br-mutation-feedback` | Khởi đầu từ bộ cuối P4r `run-XX`, lặp phản hồi mutant còn sống (`prompts/mutation-feedback-reduced/`), **chỉ cấp dòng mã đột biến và mô tả biến đổi** + lọc + chọn tham lam lười | LLM tự sinh, kiểm chứng trên `reference/` | 10 *(tối thiểu 5 nếu giới hạn quota)* | Đánh giá riêng hiệu quả phản hồi mutation khi thoát hiệu ứng trần |

### Lý do bổ sung nhánh thí nghiệm bóc tách "ngữ cảnh hạn chế" (D-024)

Thí nghiệm sơ bộ trên nhánh chính cho thấy **hiệu ứng trần** đã xuất hiện rõ khi LLM có sẵn toàn bộ mã nguồn lẫn đặc tả của module `shop`: bộ sinh trực tiếp một lượt gần như chạm bão hòa ngay từ đầu, khiến vòng lặp phản hồi coverage (P4) và mutation (P4b) không còn đủ nhánh thiếu hoặc mutant sống để thể hiện tác dụng gia tăng cho RQ2 và RQ4 (số liệu chi tiết của cả hai nhánh được báo cáo ở Chương 5). Thay vì hạ cấp mô hình hay chỉnh sửa chương trình đối tượng đã đóng băng ở M1, quyết định **D-024** giữ nguyên thiết kế chính để báo cáo trung thực và bổ sung nhánh **P3r → P4r → P4br**:

- Khởi đầu từ kiểm thử hộp đen thuần đặc tả (`p3r-spec-only`) là kịch bản thực tế trong kiểm thử phần mềm (tương tự quy trình viết P1 không xem mã `subject/`) và nằm hoàn toàn trong phạm vi đề tài.
- Việc chỉ cung cấp đoạn mã cục bộ quanh dòng/nhánh chưa phủ (P4r) hoặc dòng chứa mutant còn sống (P4br) giúp cô lập trực tiếp tác dụng của **tín hiệu phản hồi thực thi** (JaCoCo và PIT/`mutate_custom.py`) so với sinh một lượt không phản hồi, đồng thời hỗ trợ trình diễn trực tiếp các vòng cải thiện coverage và mutation (`--demo`).

### Lý do không thực hiện đối chứng EvoSuite (D-013)

Trong đề xuất ban đầu, **EvoSuite** `[fraser-2011]` được dự kiến là phương pháp đối chứng bổ sung thuộc nhóm SBST nếu tương thích với JDK 17. Tuy nhiên, theo quyết định **D-013**, đối chứng EvoSuite **không được thực hiện** trong đợt phát triển tự động do thao tác tải và chạy tệp `.jar` EvoSuite từ Internet bị chính sách quyền của môi trường điều phối chặn (`docs/orchestration/decisions.md`). Ngoài ra, y văn gần đây cũng ghi nhận EvoSuite thường gặp trục trặc tương thích trên các phiên bản JDK mới `[chen-2024]`. Phù hợp với mục 6 của `docs/thiet-ke-ky-thuat.md`, phạm vi thực nghiệm chính thức không chạy EvoSuite (D-014); trong `experiments/scripts/run_experiment.py`, nếu truyền `--methods evosuite`, trình điều phối ghi rõ bước `[SKIP]` kèm lý do D-013/D-014. Nhóm thực hiện có thể bổ sung chạy thủ công EvoSuite ngoài môi trường hạn chế nếu cần đối chứng mở rộng.

---

## 4.4 Chỉ số đánh giá

### Mười chỉ số đánh giá định lượng

Mỗi lần chạy `run-XX` xuất một tệp kết quả chuẩn hóa `results/comparison/raw/<method>/run-XX.json` được kiểm hợp lệ bằng JSON Schema (`experiments/schema/run-result.schema.json`), bao gồm đầy đủ **8 tiêu chí của đề** và **2 chỉ số chứng minh tính hợp lệ – tính đúng oracle** (`docs/de-xuat-de-tai-1.md` mục 8, `docs/thiet-ke-ky-thuat.md` mục 4.2 & 5.1):

| # | Chỉ số | Trường trong `run-XX.json` | Công thức / Định nghĩa |
|---:|---|---|---|
| 1 | **Statement (Line) Coverage** | `coverage.statement.pct` | Bộ đếm `LINE` của JaCoCo trên package `shop` ($\text{covered} / 235 \times 100\%$). |
| 2 | **Branch Coverage** | `coverage.branch.pct` | Bộ đếm `BRANCH` của JaCoCo trên package `shop` ($\text{covered} / 174 \times 100\%$). |
| 3 | **Function (Method) Coverage** | `coverage.function.pct` | Bộ đếm `METHOD` của JaCoCo trên package `shop` ($\text{covered} / 55 \times 100\%$). |
| 4 | **Mutation Score** | `mutation.score` | $\text{killed} / (\text{total} - \text{equivalent}) \times 100\%$; giữ `NO_COVERAGE` trong `total`; `TIMED_OUT` và `MEMORY_ERROR` tính là `killed` (`tools/svv/pit.py`). |
| 5 | **Số lượng test case** | `tests.generated`, `compiled`, `valid`, `passed` | Số ca kiểm thử sinh ra, biên dịch được, hợp lệ sau bộ lọc bốn tầng và chạy xanh trên `subject/` (ở P4/P4b/P4r/P4br, số ca bị loại do `no_coverage_gain`/`no_mutation_gain` ở bước chọn tham lam được ghi tách tại `generation.json`). |
| 6 | **Thời gian thực thi** | `time.execution_s` | Tổng thời gian chạy Maven test/JaCoCo cộng thời gian chạy PIT và `mutate_custom.py` (khi bật `--mutation`). |
| 7 | **Thời gian sinh test** | `time.generation_s` | Thời gian sinh bộ kiểm thử (P1 không áp dụng tự động; P4/P4b và P4r/P4br tính cộng dồn từ phương pháp cha). |
| 8 | **Chi phí sử dụng LLM** | `llm.calls`, `input_tokens`, `output_tokens`, `cost_usd` | Số lượt gọi và tổng lượng token (P1, P2 để 0; P4/P4b và P4r/P4br cộng dồn từ phương pháp cha). |
| 9 | **Tỷ lệ biên dịch được** *(bổ sung bắt buộc)* | `tests.compile_rate` | $\text{compiled} / \text{generated}$. |
| 10 | **Tỷ lệ oracle đúng** *(bổ sung bắt buộc)* | `tests.oracle_rate` | $\text{passed\_reference} / \text{compiled}$ (chỉ số chứng minh tính đúng oracle theo đặc tả qua `reference/`). |

Ngoài mười chỉ số trên, tệp `run-XX.json` còn lưu danh sách mutant chưa bị diệt (`mutation.alive`) để công cụ `experiments/scripts/analyze.py` có thể tính lại `mutation.score` đồng nhất trong bộ nhớ mỗi khi danh sách mutant tương đương cập nhật mà không cần chạy lại PIT. Đối với chuỗi chính (`p4-coverage-guided`, `p4b-mutation-feedback`), lịch sử `iterations` được chép trực tiếp vào `run-XX.json` bởi `measure.py`; đối với chuỗi hạn chế (`p4r-coverage-guided`, `p4br-mutation-feedback`), `analyze.py` tự động nạp đối chiếu lịch sử `iterations` và `baseline` từ `test-suites/<method>/run-XX/generation.json`.

### Ánh xạ 5 toán tử đột biến và cấu trúc bộ mutant (S2, D-012, D-016)

Kết quả thử nghiệm kỹ thuật Spike S2 (`experiments/spikes/s1-s2/KET-QUA.md`) chứng minh PIT (thao tác trên bytecode JVM) **không hỗ trợ trực tiếp** phép thay toán tử điều kiện ngắn mạch `&&` ↔ `||` và chỉ xóa được một phần câu lệnh (lời gọi `void` và phép gán trường), trong khi mutator `INLINE_CONSTS` của PIT lại sinh các biến đổi hằng nhiễu xuất phát từ phép so sánh. Do đó, theo quyết định **D-012** và **D-016**, đề tài kết hợp **6 mutator PIT chọn lọc** (`experiments/config/default.yaml`) với bộ tạo đột biến mức mã nguồn **`tools/mutate_custom.py`** (`tools/svv/custom_mutation.py`) để phủ đầy đủ 5 toán tử của đề mà không đếm trùng (Bảng 4.4).

| # | Toán tử đột biến của đề | Khóa `by_operator` | Nguồn thực hiện (PIT / `mutate_custom.py`) | Quy mô trên `subject/` (T07) | Ghi chú kỹ thuật |
|---:|---|---|---|---:|---|
| 1 | Thay toán tử so sánh (`>` → `>=`, ...) | `relational` | **PIT:** `CONDITIONALS_BOUNDARY` | 28 | Ánh xạ 1–1 với biên điều kiện so sánh. |
| 2 | Thay toán tử số học (`+` → `-`, ...) | `arithmetic` | **PIT:** `MATH` | 19 | Thay thế các phép toán số học nguyên. |
| 3 | Thay toán tử điều kiện (`&&` ↔ `||`) | `conditional` | **`mutate_custom.py`** (mức mã nguồn) | 31 | Bổ sung khoảng trống bytecode của PIT (S2, D-012). |
| 4 | Thay hằng boolean (`true` ↔ `false`) | `boolean_constant` | **`mutate_custom.py`** (literal `true`↔`false`) **+** **PIT:** `TRUE_RETURNS`, `FALSE_RETURNS` | 2 | **Giới hạn D-016:** `subject/` có 0 literal boolean; cả 2 mutant đến từ đổi giá trị trả về của `Cart.isEmpty()`. Không dùng `INLINE_CONSTS` (D-012). |
| 5 | Xóa câu lệnh | `statement_deletion` | **PIT:** `VOID_METHOD_CALLS`, `EXPERIMENTAL_MEMBER_VARIABLE` **+** **`mutate_custom.py`** (xóa gán biến cục bộ, `++`/`--`, gán phức hợp một dòng) | 41 | `mutate_custom.py` chỉ xóa câu lệnh biến cục bộ để không trùng với lời gọi `void` và gán trường của PIT (D-012). |
| | **Tổng cộng** | | **PIT + `mutate_custom.py`** | **121** | Mutant mã nguồn lỗi biên dịch (`COMPILE_ERROR`) là ứng viên không hợp lệ, không cộng vào `total` (`tools/svv/pit.py`). |

### Quy trình xử lý mutant tương đương

Toàn bộ mutant còn sống sau khi thực nghiệm chạy trên các bộ kiểm thử được rà soát thủ công để nhận diện mutant tương đương và ghi nhận vào tệp `results/mutation/equivalent-mutants.csv` theo lược đồ 8 cột (`source,class,method,line,mutator,index,description,reason`) (`docs/thiet-ke-ky-thuat.md` mục 5.4, `tools/svv/pit.py`). Cột `index` lưu chỉ số chỉ thị bytecode của PIT (hoặc định danh ổn định của mutant mã nguồn) giúp phân biệt chính xác nhiều mutant cùng nằm trên một dòng và cùng mutator; trường hợp cột `index` để trống thì khớp mọi chỉ số cùng bộ định danh. Hàm `merge_mutations()` và `rescore()` trong `tools/svv/pit.py` (được gọi thống nhất từ `measure.py`, `generate_p4.py` và `analyze.py`) đối chiếu khóa 7 thành phần `(source, class, method, line, mutator, index, description)` và loại đồng nhất các mutant tương đương khỏi mẫu số cho **mọi phương pháp**, bảo đảm so sánh công bằng `[CHỜ: chốt danh sách đầy đủ trong results/mutation/equivalent-mutants.csv sau khi hoàn tất rà soát toàn bộ đợt thực nghiệm T14]`.

---

## 4.5 Quy trình thực nghiệm

### Thiết lập lặp, hạt giống và ghép cặp

1. **Số lần chạy:** P1 chạy 1 lần tất định (`run-01`). Bốn phương pháp bất định/ngẫu nhiên của nhánh chính (P2, P3, P4, P4b) chạy **10 lần độc lập** (`run-01` đến `run-10`, khóa `runs: 10` trong `experiments/config/default.yaml`). Theo hướng dẫn của Arcuri và Briand `[arcuri-2011]`, lựa chọn $n = 10$ là sự cân bằng thực tế trước chi phí token và độ trễ của mô hình ngôn ngữ lớn, và sẽ được thảo luận trong mục *Threats to Validity* (mục 5.6). Đối với nhánh bóc tách ngữ cảnh hạn chế (P3r, P4r, P4br — D-024), thiết kế hướng tới cùng 10 lần chạy và quy định ngưỡng tối thiểu **5 lần** trong trường hợp gặp giới hạn hạn mức LLM.
2. **Hạt giống của P2:** `seed = seed_base + run` với `seed_base = 1000` (`seed` từ `1001` đến `1010`), sinh đúng `num_tests = 100` ca kiểm thử/lần. Tính tái lập byte-for-byte của P2 được kiểm soát bằng mã băm SHA-256 trong `generation.json` (`tools/generate_p2.py`) và quy ước xuống dòng LF trong `.gitattributes` (D-017).
3. **Thứ tự ghép cặp trong `run_experiment.py`:** Với mỗi `run-XX`, trình điều phối `experiments/scripts/run_experiment.py` thực thi nghiêm ngặt thứ tự phụ thuộc `P3 → P4 → P4b` (và tương ứng `P3r → P4r → P4br` khi chạy nhánh hạn chế), xen kẽ ngay bước `measure.py --mutation` sau mỗi bước sinh. Trình điều phối ghi nhật ký từng bước vào `results/comparison/run-log.jsonl`; khi gặp lỗi hết hạn mức LLM (mã thoát `2` từ bộ sinh hoặc khớp mẫu lỗi quota), script dừng sạch với mã thoát `3` (`QUOTA_EXIT_CODE`) và in đúng lệnh tiếp tục có cờ `--skip-existing` để bỏ qua các lần chạy đã có `generation.json` hoặc `run-XX.json`.

### Tham số điều khiển thực nghiệm (`experiments/config/default.yaml`)

Bảng 4.5 ghi nhận toàn bộ tham số chính thức đã đồng bộ trong `experiments/config/default.yaml` (áp dụng thống nhất cho cả nhánh chính P3/P4/P4b và nhánh ngữ cảnh hạn chế P3r/P4r/P4br, theo quyết định D-020).

| Khóa cấu hình trong `default.yaml` | Giá trị chính thức | Ý nghĩa |
|---|---|---|
| `runs` | `10` | Số lần chạy lặp mặc định cho các phương pháp có tính bất định |
| `random.seed_base` / `random.num_tests` | `1000` / `100` | Hạt giống nền (`1000 + run`) và số ca kiểm thử mỗi lần của P2 |
| `llm.timeout_s` | `300` s | Thời gian chờ mặc định cho các lời gọi ngắn (như kiểm khói S3) |
| `p3.timeout_s` / `p4.timeout_s` | `1800` s / `1800` s | Thời gian chờ riêng cho mỗi lượt gọi LLM sinh bộ test của P3/P3r và mỗi vòng của P4/P4b/P4r/P4br |
| `p4.max_iterations` | `5` | Số vòng phản hồi coverage tối đa (P4, P4r) |
| `p4.target_branch_coverage` | `100` (%) | Ngưỡng dừng sớm branch coverage (D-020: nâng từ 95% lên 100% vì P1 đã đạt 100%) |
| `p4.token_budget` | `400000` | Ngân sách `input_tokens + output_tokens` riêng cho giai đoạn P4/P4r (D-020: nâng từ 200.000 để bù $\approx$ 22k token nền mỗi lượt gọi CLI) |
| `p4.stop_if_no_gain_iterations` | `2` | Dừng sớm khi 2 vòng liên tiếp không chọn thêm được test tăng LINE hoặc BRANCH |
| `p4.demo_max_iterations` | `2` | Giới hạn số vòng cho chế độ trình diễn `--demo` |
| `p4.p4b.max_iterations` | `3` | Số vòng phản hồi mutation tối đa (P4b, P4br — D-020) |
| `p4.p4b.target_mutation_score` | `100` (%) | Ngưỡng dừng sớm mutation score (D-020) |
| `p4.p4b.token_budget` | `300000` | Ngân sách `input_tokens + output_tokens` riêng cho giai đoạn P4b/P4br (D-020) |
| `p4.p4b.stop_if_no_gain_iterations` | `2` | Dừng sớm khi 2 vòng liên tiếp không chọn thêm được test diệt mutant mới (D-020) |
| `measure.mutation` / `custom_mutation` | `true` / `true` | Bật đo PIT và đo đột biến mức mã nguồn `mutate_custom.py` |
| `measure.timeout_factor` / `custom_workers` / `custom_timeout_constant_s` | `3` / `3` / `10` s | Hệ số timeout PIT, số luồng chạy song song và hằng số timeout của `mutate_custom.py` |

### Phương pháp phân tích thống kê (`experiments/scripts/analyze.py`)

Tuân thủ hướng dẫn của Arcuri và Briand `[arcuri-2011]`, script `experiments/scripts/analyze.py` (sử dụng `tools/svv/stats.py`, `scipy`, `numpy`, `matplotlib`) tổng hợp tự động toàn bộ `results/comparison/raw/<method>/run-XX.json` và thực hiện bốn nhóm phân tích:

- **Thống kê mô tả (`summary.csv`, `summary.md`):** Tính số lượng mẫu $n$, trung bình (mean), độ lệch chuẩn mẫu (SD, `ddof=1`), trung vị (median), khoảng tứ phân vị (IQR), giá trị nhỏ nhất và lớn nhất cho 13 biến số (bao phủ 10 chỉ số mục 4.4), kèm bảng minh bạch các mutant tương đương đã trừ khi tính lại điểm đột biến.
- **Kiểm định giả thuyết phi tham số và độ lớn hiệu ứng (`pairwise.csv`, `pairwise.md`):** Áp dụng **Mann–Whitney U test** (hai phía, mức ý nghĩa $\alpha = 0{,}05$) và hệ số **Vargha–Delaney $\hat{A}_{12}$** ($\hat{A}_{12} = P(A > B) + 0{,}5 \cdot P(A = B)$, ngưỡng diễn giải $|\hat{A}_{12} - 0{,}5| < 0{,}06$ không đáng kể, $\ge 0{,}56$ nhỏ, $\ge 0{,}64$ trung bình, $\ge 0{,}71$ lớn `[arcuri-2011]`), loại trừ các phương pháp có $n < 2$ (như P1) khỏi kiểm định suy luận. Các cặp so sánh cố định gồm:
  - Nhánh chính: **P4 vs P3** (RQ2, RQ4), **P3 vs P2** (RQ3), **P4 vs P2** (RQ3), **P4b vs P4** (đóng góp gia tăng của phản hồi mutation).
  - Nhánh ngữ cảnh hạn chế (khi có dữ liệu): **P4r vs P3r**, **P4br vs P4r**, **P3r vs P3**.
  - Ghi nhận rõ nhu cầu xem xét hiệu chỉnh đa so sánh khi diễn giải nhiều cặp kiểm định đồng thời `[arcuri-2011]`.
- **So sánh theo cặp trước/sau và tiến triển vòng lặp (`coverage-before-after*.csv/.md`, `iterations.csv`):** Tính trực tiếp độ chênh lệch theo từng `run-XX` giữa P3 → P4 → P4b (và P3r → P4r → P4br), đồng thời xuất chuỗi tiến triển theo từng vòng lặp để vẽ biểu đồ hội tụ.
- **Phân tích tương quan cho RQ5 (`rq5-correlation.csv`):** Tính hệ số tương quan hạng **Spearman ($\rho$)** và trị số $p$ cho 8 cặp chỉ số giữa kích thước bộ kiểm thử hợp lệ (`tests_valid`), độ bao phủ (`statement_pct`, `branch_pct`, `function_pct`) và điểm đột biến (`mutation_score`).

### Lệnh tái lập thực nghiệm

Toàn bộ lệnh chạy từ thư mục gốc của kho lưu trữ bằng môi trường Python đã chỉ định (`python`).

```powershell
# 1. Liệt kê tập mutant mã nguồn (D-012) trên subject/
python tools/mutate_custom.py --list

# 2. Kiểm thử khói bộ gọi LLM (Spike S3)
python -m tools.svv.llm_client --smoke --provider codex-cli

# 3. Sinh một lần chạy P2 (seed = 1000 + N, 100 test dùng oracle reference/)
python tools/generate_p2.py --run 1

# 4. Sinh một lần chạy P3 (đủ ngữ cảnh) và P3r (chỉ đặc tả + API); thêm --dry-run để xem prompt không gọi LLM
python tools/generate_p3.py --run 1
python tools/generate_p3.py --run 1 --variant spec-only
python tools/generate_p3.py --run 1 --dry-run

# 5. Sinh P4 (phản hồi coverage) và P4b (phản hồi mutation) ghép cặp từ run-01; hỗ trợ --demo và --dry-run
python tools/generate_p4.py --run 1
python tools/generate_p4.py --run 1 --mutation-feedback
python tools/generate_p4.py --run 1 --demo

# 6. Sinh nhánh bóc tách ngữ cảnh hạn chế P4r và P4br ghép cặp từ P3r run-01
python tools/generate_p4.py --run 1 --variant reduced
python tools/generate_p4.py --run 1 --variant reduced --mutation-feedback

# 7. Đo một bộ kiểm thử đơn lẻ (JUnit 5 + JaCoCo + PIT + mutate_custom) hoặc kiểm chứng trên reference/
python experiments/scripts/measure.py --suite test-suites/p1-manual/run-01 --method p1-manual --run 1 --mutation
python experiments/scripts/measure.py --suite test-suites/p1-manual/run-01 --method p1-manual --run 1 --program reference

# 8. Chạy trọn gói chuỗi chính (P1–P4b, 10 lần) và chuỗi ngữ cảnh hạn chế (P3r–P4br) có hỗ trợ tiếp tục khi hết quota
python experiments/scripts/run_experiment.py --runs 1-10 --skip-existing
python experiments/scripts/run_experiment.py --methods p3r-spec-only,p4r-coverage-guided,p4br-mutation-feedback --runs 1-10 --skip-existing
python experiments/scripts/run_experiment.py --runs 1-10 --dry-run

# 9. Tổng hợp bảng thống kê (CSV/Markdown) và biểu đồ (results/comparison/figures/)
python experiments/scripts/analyze.py
```
