# Chương 4. Thiết kế thực nghiệm

Chương này mô tả chi tiết đối tượng thực nghiệm, môi trường và phiên bản công cụ, các phương pháp đối chứng, hệ thống chỉ số đánh giá cùng bảng ánh xạ năm toán tử đột biến, và quy trình chạy thực nghiệm – phân tích thống kê nhằm bảo đảm tính khách quan và khả năng tái lập. Mọi thông số cấu trúc và cấu hình đều trích trực tiếp từ mã nguồn và tài liệu trong kho lưu trữ; chương này **không** trình bày số liệu kết quả thực nghiệm (thuộc Chương 5).

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

- **Hiệu ứng trần độ bao phủ (coverage ceiling effect):** Do quy mô module vừa phải (235 dòng, 174 nhánh) và bộ kiểm thử thủ công P1 đã đạt độ bao phủ tối đa **100%** trên cả ba bộ đếm (`LINE` 235/235, `BRANCH` 174/174, `METHOD` 55/55 — `docs/orchestration/progress.md`, D-020), khoảng cách coverage giữa các phương pháp LLM mạnh có thể bị nén gần ngưỡng trần. Khi đó, **mutation score**, **tỷ lệ biên dịch**, **tỷ lệ oracle đúng** và **chi phí token/thời gian** sẽ đóng vai trò thước đo phân biệt then chốt.
- **Không có hằng literal boolean (`true`/`false`) trong mã nguồn `subject/` (D-016):** Kết quả rà soát toàn bộ `subject/src/main/java/shop/*.java` ghi nhận **0** literal `true`/`false`. Do đặc tả và mã nguồn đã đóng băng tại mốc M1 (không chỉnh sửa lại `subject/`, `reference/` và P1), toán tử đột biến thay hằng boolean chỉ tác động được lên giá trị trả về của phương thức boolean duy nhất là `Cart.isEmpty()`, tạo ra đúng **2 mutant** (xem mục 4.4).

---

## 4.2 Môi trường thực nghiệm

Toàn bộ phiên bản công cụ, thư viện và cấu hình LLM được ghim cố định trong kho lưu trữ nhằm bảo đảm khả năng tái lập. Bảng 4.2 tổng hợp chi tiết môi trường thực nghiệm.

| Thành phần | Phiên bản / Giá trị | Nguồn xác minh trong kho lưu trữ |
|---|---|---|
| Hệ điều hành | Windows 11 x64 | `experiments/spikes/s1-s2/KET-QUA.md` |
| JDK | Oracle JDK **17.0.12 LTS** (`maven.compiler.release=17`) | D-004, `subject/pom.xml`, `experiments/spikes/s1-s2/KET-QUA.md` |
| Build tool | Apache Maven **3.9.15** | D-004, `experiments/spikes/s1-s2/KET-QUA.md` |
| Maven Plugins | `maven-compiler-plugin` **3.14.1**, `maven-surefire-plugin` **3.5.4**, `build-helper-maven-plugin` **3.6.1** | `subject/pom.xml`, `reference/pom.xml` |
| Framework kiểm thử | JUnit Jupiter **5.13.4** | `subject/pom.xml` |
| Công cụ đo coverage | JaCoCo (`jacoco-maven-plugin`) **0.8.14** | `subject/pom.xml` |
| Công cụ mutation | PIT (`pitest-maven`) **1.20.3** + `pitest-junit5-plugin` **1.2.3** | `subject/pom.xml` |
| Python | Python **3.12.16** (môi trường conda `svv`) | D-004 |
| Thư viện Python | `anthropic==2.12.0`, `pyyaml==6.0.4`, `scipy==1.18.1`, `numpy==2.5.5`, `matplotlib==3.11.2`, `pytest==9.2.0`, `jsonschema==4.26.0` | `requirements.txt` |
| LLM chính & CLI | Provider `codex-cli` (`codex-cli` **0.171.0**), model `gpt-6.2-astra`, `effort = medium` (thay cho `temperature`), `max_output_tokens = 16000` | `experiments/config/default.yaml`, D-001, D-005 |
| LLM dự phòng & CLI | Provider `agy-cli` (Antigravity CLI **1.5.14**), mức suy luận nằm trong tên model (kiểm khói S3 trên `gemini-3.8-flash-low`) | D-001, D-015, `experiments/spikes/s3/KET-QUA.md` |
| Provider API tùy chọn | `anthropic` (đọc `ANTHROPIC_API_KEY` từ `.env`, không dùng mặc định) | D-005, `tools/svv/llm_client.py` |
| Đơn giá quy đổi USD | `price_per_mtok_input: null`, `price_per_mtok_output: null` | `experiments/config/default.yaml` `[CHỜ: cập nhật đơn giá quy đổi nếu áp dụng]` |
| Phần cứng máy chạy | `[CHỜ: cấu hình CPU/RAM máy chạy đợt thực nghiệm chính thức T14]` | — |

**Hạn chế môi trường đã biết:** Như đã phân tích tại mục 3.5, việc gọi LLM thông qua CLI (`codex-cli`, `agy-cli`) thay vì API trực tiếp (D-005) kéo theo lượng **token nền cố định** trong mỗi lời gọi ($\approx$ 22.000 token ở `codex-cli` và $\approx$ 15.500 token ở `agy-cli`) cũng như ràng buộc hạn mức theo cửa sổ thời gian (D-015). Vì `price_per_mtok_*` đang để `null` khi chạy bằng gói quota CLI, chi phí LLM được đánh giá chủ yếu qua tổng số lượt gọi (`calls`), `input_tokens`, `output_tokens` và thời gian sinh (`generation_s`); trường `cost_usd` sẽ được tính bổ sung nếu áp bảng giá tham chiếu công khai ở T14.

---

## 4.3 Phương pháp đối chứng

Thực nghiệm so sánh **năm phương pháp** trên cùng chương trình đối tượng `subject/` và cùng quy trình đo (`experiments/scripts/measure.py`), gồm hai phương pháp đối chứng truyền thống/thủ công và ba chiến lược LLM (D-014):

| Mã | Định danh hệ thống | Cách sinh test | Nguồn oracle | Số lần chạy | Vai trò |
|---|---|---|---|---:|---|
| **P1** | `p1-manual` | Viết tay theo đặc tả `docs/spec/`, **không xem mã `subject/`** (D-006); truy vết 32/32 nhóm tình huống trong `branches.md` (181 ca: 54 `@Test` + 127 ca tham số) | Con người suy từ đặc tả | 1 (`run-01`) | Đối chứng hộp đen chuẩn & bộ kiểm chứng `subject ≡ reference` |
| **P2** | `p2-random` | Bộ sinh Java `tools/random-gen/` (`p2.Generator`) điều phối qua `tools/generate_p2.py`; sinh 100 ca kiểm thử/lần từ seed cố định `1000 + run` | Thực thi kịch bản trực tiếp trên `reference/` để phát sinh `assertEquals`/`assertThrows` cụ thể | 10 (`run-01`..`run-10`) | Đối chứng tự động truyền thống (kiểm thử ngẫu nhiên có oracle chuẩn) |
| **P3** | `p3-direct-llm` | Gọi LLM một lượt từ đặc tả + mã nguồn, lọc bốn tầng qua `test_filter.py` | LLM tự sinh, kiểm chứng trên `reference/` | 10 (`run-01`..`run-10`) | Đối chứng LLM cơ sở (không phản hồi) |
| **P4** | `p4-coverage-guided` | Khởi đầu từ P3 `run-XX`, lặp phản hồi nhánh/dòng chưa phủ từ JaCoCo + lọc + chọn tham lam | LLM tự sinh, kiểm chứng trên `reference/` | 10 (`run-01`..`run-10`) | Phương pháp đề xuất (phản hồi coverage) |
| **P4b** | `p4b-mutation-feedback` | Khởi đầu từ bộ cuối P4 `run-XX`, lặp phản hồi mutant còn sống từ PIT + `mutate_custom.py` + lọc + chọn tham lam | LLM tự sinh, kiểm chứng trên `reference/` | 10 (`run-01`..`run-10`) | Phương pháp cải tiến (thí nghiệm bóc tách phản hồi mutation) |

### Lý do không thực hiện đối chứng EvoSuite (D-013)

Trong đề xuất ban đầu, **EvoSuite** `[fraser-2011]` được dự kiến là phương pháp đối chứng bổ sung thuộc nhóm SBST nếu tương thích với JDK 17. Tuy nhiên, theo quyết định **D-013**, đối chứng EvoSuite **không được thực hiện** trong đợt phát triển tự động do thao tác tải và chạy tệp `.jar` EvoSuite từ Internet bị chính sách quyền của môi trường điều phối chặn (`docs/orchestration/decisions.md`). Ngoài ra, y văn gần đây cũng ghi nhận EvoSuite thường gặp trục trặc tương thích trên các phiên bản JDK mới `[chen-2024]`. Phù hợp với mục 6 của `docs/thiet-ke-ky-thuat.md`, phạm vi thực nghiệm chính thức gồm năm phương pháp P1, P2, P3, P4 và P4b (D-014); nhóm thực hiện có thể bổ sung chạy thủ công EvoSuite ngoài môi trường hạn chế nếu cần đối chứng mở rộng.

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
| 5 | **Số lượng test case** | `tests.generated`, `compiled`, `valid`, `passed` | Số ca kiểm thử sinh ra, biên dịch được, hợp lệ sau lọc và chạy xanh trên `subject/`. |
| 6 | **Thời gian thực thi** | `time.execution_s` | Tổng thời gian chạy Maven test/JaCoCo cộng thời gian chạy PIT và `mutate_custom.py` (khi bật `--mutation`). |
| 7 | **Thời gian sinh test** | `time.generation_s` | Thời gian sinh bộ kiểm thử (P1 không áp dụng tự động; P4/P4b tính cộng dồn từ P3). |
| 8 | **Chi phí sử dụng LLM** | `llm.calls`, `input_tokens`, `output_tokens`, `cost_usd` | Số lượt gọi và tổng lượng token (P1, P2 để 0; P4/P4b cộng dồn từ P3). |
| 9 | **Tỷ lệ biên dịch được** *(bổ sung bắt buộc)* | `tests.compile_rate` | $\text{compiled} / \text{generated}$. |
| 10 | **Tỷ lệ oracle đúng** *(bổ sung bắt buộc)* | `tests.oracle_rate` | $\text{passed\_reference} / \text{compiled}$ (chỉ số chứng minh tính đúng oracle theo đặc tả qua `reference/`). |

### Ánh xạ 5 toán tử đột biến và cấu trúc bộ mutant (S2, D-012, D-016)

Kết quả thử nghiệm kỹ thuật Spike S2 (`experiments/spikes/s1-s2/KET-QUA.md`) chứng minh PIT (thao tác trên bytecode JVM) **không hỗ trợ trực tiếp** phép thay toán tử điều kiện ngắn mạch `&&` ↔ `||` và chỉ xóa được một phần câu lệnh (lời gọi `void` và phép gán trường), trong khi mutator `INLINE_CONSTS` của PIT lại sinh các biến đổi hằng nhiễu xuất phát từ phép so sánh. Do đó, theo quyết định **D-012** và **D-016**, đề tài kết hợp **6 mutator PIT chọn lọc** (`experiments/config/default.yaml`) với bộ tạo đột biến mức mã nguồn **`tools/mutate_custom.py`** (`tools/svv/custom_mutation.py`) để phủ đầy đủ 5 toán tử của đề mà không đếm trùng (Bảng 4.3).

| # | Toán tử đột biến của đề | Khóa `by_operator` | Nguồn thực hiện (PIT / `mutate_custom.py`) | Quy mô trên `subject/` (T07) | Ghi chú kỹ thuật |
|---:|---|---|---|---:|---|
| 1 | Thay toán tử so sánh (`>` → `>=`, ...) | `relational` | **PIT:** `CONDITIONALS_BOUNDARY` | 28 | Ánh xạ 1–1 với biên điều kiện so sánh. |
| 2 | Thay toán tử số học (`+` → `-`, ...) | `arithmetic` | **PIT:** `MATH` | 19 | Thay thế các phép toán số học nguyên. |
| 3 | Thay toán tử điều kiện (`&&` ↔ `||`) | `conditional` | **`mutate_custom.py`** (mức mã nguồn) | 31 | Bổ sung khoảng trống bytecode của PIT (S2, D-012). |
| 4 | Thay hằng boolean (`true` ↔ `false`) | `boolean_constant` | **`mutate_custom.py`** (literal `true`↔`false`) **+** **PIT:** `TRUE_RETURNS`, `FALSE_RETURNS` | 2 | **Giới hạn D-016:** `subject/` có 0 literal boolean; cả 2 mutant đến từ đổi giá trị trả về của `Cart.isEmpty()`. Không dùng `INLINE_CONSTS` (D-012). |
| 5 | Xóa câu lệnh | `statement_deletion` | **PIT:** `VOID_METHOD_CALLS`, `EXPERIMENTAL_MEMBER_VARIABLE` **+** **`mutate_custom.py`** (xóa gán biến cục bộ, `++`/`--`, gán phức hợp một dòng) | 41 | `mutate_custom.py` chỉ xóa câu lệnh biến cục bộ để không trùng với lời gọi `void` và gán trường của PIT (D-012). |
| | **Tổng cộng** | | **PIT + `mutate_custom.py`** | **121** | Mutant mã nguồn lỗi biên dịch (`COMPILE_ERROR`) là ứng viên không hợp lệ, không cộng vào `total` (`tools/svv/pit.py`). |

### Quy trình xử lý mutant tương đương

Sau khi hoàn tất chạy thực nghiệm trên các bộ kiểm thử, toàn bộ mutant còn sống được rà soát thủ công để nhận diện mutant tương đương và ghi nhận vào tệp `results/mutation/equivalent-mutants.csv` theo đúng lược đồ 7 cột (`source,class,method,line,mutator,description,reason`) (`docs/thiet-ke-ky-thuat.md` mục 5.4). Hàm `merge_mutations()` trong `tools/svv/pit.py` đối chiếu đúng bộ định danh `(source, class, method, line, mutator, description)` và loại đồng nhất các mutant tương đương khỏi mẫu số cho **mọi phương pháp**, bảo đảm so sánh công bằng `[CHỜ: results/mutation/equivalent-mutants.csv sau khi rà soát thủ công ở T14]`.

---

## 4.5 Quy trình thực nghiệm

### Thiết lập lặp, hạt giống và ghép cặp

1. **Số lần chạy:** P1 chạy 1 lần tất định (`run-01`). Bốn phương pháp có yếu tố ngẫu nhiên hoặc bất định (P2, P3, P4, P4b) chạy **10 lần độc lập** (`run-01` đến `run-10`, khóa `runs: 10` trong `experiments/config/default.yaml`). Theo hướng dẫn của Arcuri và Briand `[arcuri-2011]`, lựa chọn $n = 10$ là sự cân bằng thực tế trước chi phí token và độ trễ của mô hình ngôn ngữ lớn, và sẽ được thảo luận trong mục *Threats to Validity* (mục 5.6).
2. **Hạt giống của P2:** `seed = seed_base + run` với `seed_base = 1000` (`seed` từ `1001` đến `1010`), sinh đúng `num_tests = 100` ca kiểm thử/lần. Tính tái lập byte-for-byte của P2 được kiểm soát bằng mã băm SHA-256 trong `generation.json` (`tools/generate_p2.py`) và quy ước xuống dòng LF trong `.gitattributes` (D-017).
3. **Ghép cặp P3 → P4 → P4b:** Với mỗi `run-XX`, thực hiện tuần tự P3 → P4 (kế thừa bộ giữ lại của P3) → P4b (kế thừa bộ cuối của P4), sau đó đo thống nhất bằng `measure.py --mutation`.

### Tham số dừng của P4 và P4b

Bảng 4.4 ghi nhận các tham số điều khiển vòng lặp của P4 và P4b. Lưu ý có sự cập nhật đã được chốt tại quyết định **D-020** (sau khi xác nhận P1 đạt 100% branch coverage và đo token nền CLI ở S3) so với giá trị khởi tạo ban đầu trong `experiments/config/default.yaml` trước khi hợp nhất gói T11/T12.

| Tham số | Giá trị hiện trong `default.yaml` | Giá trị chính thức đã chốt (D-020 cho T11/T12) | Ý nghĩa |
|---|---|---|---|
| `p4.max_iterations` | 5 | 5 | Số vòng phản hồi coverage tối đa |
| `p4.target_branch_coverage` | 95 | **100** | Ngưỡng branch coverage dừng sớm (nâng lên 100% để tránh dừng ngay vòng 0 khi P3 đạt >95%) |
| `p4.token_budget` | 200.000 | **400.000** | Ngân sách `input_tokens + output_tokens` (nâng để bù $\approx$ 22k token nền mỗi lời gọi CLI) |
| `p4.stop_if_no_gain_iterations` | 2 | 2 | Dừng khi 2 vòng liên tiếp không tăng LINE hoặc BRANCH |
| `p4.demo_max_iterations` | 2 | 2 | Số vòng cho chế độ trình diễn `--demo` |
| `p4b.max_iterations` | `[CHỜ: T11/T12]` | **3** | Số vòng phản hồi mutation tối đa |
| `p4b.target_mutation_score` | `[CHỜ: T11/T12]` | **100** | Ngưỡng mutation score dừng sớm |
| `p4b.token_budget` | `[CHỜ: T11/T12]` | **300.000** | Ngân sách `input_tokens + output_tokens` riêng cho P4b |
| `p4b.stop_if_no_gain_iterations` | `[CHỜ: T11/T12]` | **2** | Dừng khi 2 vòng liên tiếp không diệt thêm mutant |
| `measure.timeout_factor` / `custom_workers` / `custom_timeout_constant_s` | 3 / 3 / 10 s | 3 / 3 / 10 s | Hệ số timeout PIT và cấu hình chạy song song `mutate_custom.py` |

*(Trạng thái đồng bộ cấu hình sẽ được xác nhận lại sau khi hợp nhất T11/T12 `[CHỜ: experiments/config/default.yaml sau T11/T12]`.)*

### Phương pháp phân tích thống kê

Tuân thủ hướng dẫn của Arcuri và Briand `[arcuri-2011]`, dữ liệu 10 lần chạy của các phương pháp được tổng hợp và kiểm định bằng `scipy` / `numpy` như sau:

- **Thống kê mô tả:** Báo cáo trung bình (mean) và độ lệch chuẩn (standard deviation — SD), kèm trung vị (median) và khoảng tứ phân vị (IQR) cho từng chỉ số.
- **Kiểm định giả thuyết phi tham số:** Sử dụng **Mann–Whitney U test** (hai phía, mức ý nghĩa $\alpha = 0{,}05$) cho các phép so sánh giữa các cặp phương pháp (ví dụ P3 vs P2, P4 vs P3, P4b vs P4, P4b vs P2), vì dữ liệu độ bao phủ và điểm đột biến thường lệch gần trần 100% và không tuân theo phân phối chuẩn. Ghi nhận rõ nhu cầu xem xét hiệu chỉnh đa so sánh khi diễn giải nhiều cặp kiểm định đồng thời `[arcuri-2011]`.
- **Độ lớn hiệu ứng (Effect Size):** Tính hệ số **Vargha–Delaney $\hat{A}_{12}$** để lượng hóa xác suất một lần chạy của phương pháp A vượt trội phương pháp B, diễn giải theo các ngưỡng chuẩn: không đáng kể ($|\hat{A}_{12} - 0{,}5| < 0{,}06$), nhỏ ($\ge 0{,}56$), trung bình ($\ge 0{,}64$) và lớn ($\ge 0{,}71$) `[arcuri-2011]`.

### Lệnh tái lập thực nghiệm

Toàn bộ lệnh chạy từ thư mục gốc của kho lưu trữ bằng môi trường Python đã chỉ định (`python`). Các lệnh hạ tầng đã sẵn sàng trong kho lưu trữ hiện tại:

```powershell
# 1. Liệt kê tập mutant mã nguồn (D-012) trên subject/
python tools/mutate_custom.py --list

# 2. Sinh một lần chạy P2 (seed = 1000 + N, 100 test dùng oracle reference/)
python tools/generate_p2.py --run 1

# 3. Đo một bộ kiểm thử (JUnit 5 + JaCoCo + PIT + mutate_custom) ra JSON chuẩn hóa
python experiments/scripts/measure.py --suite test-suites/p1-manual/run-01 --method p1-manual --run 1 --mutation

# 4. Kiểm chứng bộ kiểm thử trên chương trình tham chiếu độc lập
python experiments/scripts/measure.py --suite test-suites/p1-manual/run-01 --method p1-manual --run 1 --program reference

# 5. Kiểm thử khói bộ gọi LLM (Spike S3)
python -m tools.svv.llm_client --smoke --provider codex-cli
```

Các lệnh sinh bộ kiểm thử LLM (P3, P4, P4b), chạy tự động toàn bộ thực nghiệm 10 lần và tổng hợp thống kê/biểu đồ sẽ được cập nhật nguyên văn ngay sau khi các gói tương ứng được hợp nhất:

- `[CHỜ: tools/generate_p3.py — lệnh sinh P3 run-01..run-10]`
- `[CHỜ: tools/generate_p4.py — lệnh sinh P4 và P4b (--mutation-feedback) và chế độ --demo]`
- `[CHỜ: experiments/scripts/run_experiment.py và experiments/scripts/analyze.py — lệnh chạy trọn gói và tạo bảng/biểu đồ thống kê]`
