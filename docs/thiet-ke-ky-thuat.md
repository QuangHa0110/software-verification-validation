# THIẾT KẾ KỸ THUẬT – Đề tài 1: LLM-based Test Generation

Tài liệu này là **đầu vào để giao cho AI implement**. Nó chốt các quyết định kỹ thuật, quy tắc nghiệp vụ, giao diện giữa các thành phần và tiêu chí "xong" của từng bước. Kế hoạch chia việc cho nhóm nằm ở [ke-hoach-thuc-hien.md](ke-hoach-thuc-hien.md); yêu cầu gốc là đề của thầy.

> **Trạng thái: BẢN NHÁP, chờ nhóm duyệt.** Mục nào ghi **[Quyết định đề xuất]** là lựa chọn của người soạn, chưa ai xác nhận. Mục 3 (quy tắc nghiệp vụ) chỉ trở thành đặc tả chính thức khi G1 chốt ở mốc M1 (25/11).

## 0. Cách dùng tài liệu này với AI

- Giao **từng bước ở mục 8**, mỗi bước một phiên làm việc, kèm đường dẫn tài liệu này và các file spec liên quan.
- Mỗi bước có **lệnh kiểm tra**. Bước chưa qua lệnh kiểm tra thì chưa xong.
- Khi gặp chỗ mơ hồ, AI **không tự đoán**: ghi câu hỏi vào `docs/spec/open-questions.md` và hỏi người giao việc.
- Đọc mục 9 (quy tắc cho AI) trước khi bắt đầu.

---

## 1. Môi trường và quyết định kỹ thuật

### 1.1 Yêu cầu môi trường chung

Mỗi thành viên tự kiểm tra máy của mình ở bước T0 bằng các lệnh bên dưới. Cả nhóm dùng **cùng một bản JDK LTS** để kết quả đo giống nhau.

| Thành phần | Yêu cầu | Lệnh kiểm tra |
|---|---|---|
| JDK | **17 hoặc 21 (LTS)**, đặt `JAVA_HOME`. Tránh bản early-access hoặc quá mới, vì JaCoCo và PIT có thể chưa hỗ trợ | `java -version` |
| Maven | 3.9 trở lên | `mvn -v` (dòng `Java version` phải trùng JDK đã chọn) |
| Python | 3.11 trở lên | `python --version` |
| Thư viện Python | cài từ `requirements.txt` (ghim phiên bản) | `pip install -r requirements.txt` |
| Git | bản hiện hành | `git --version` |

### 1.2 Quyết định

| Hạng mục | Quyết định | Lý do |
|---|---|---|
| Chương trình đối tượng, chương trình tham chiếu | **Java**, `maven.compiler.release=17`, chạy trên JDK 17 hoặc 21 LTS | Đã chốt trong đề xuất; JaCoCo và PIT ổn định trên bản LTS |
| Kiểm thử | JUnit 5 | Đề khuyến nghị |
| Đo coverage | JaCoCo: statement ≈ **LINE**, branch = **BRANCH**, function = **METHOD** | JaCoCo không có "statement" đúng nghĩa, dùng LINE làm đại diện và ghi rõ trong báo cáo |
| Mutation | PIT, kèm mutant tự tạo cho các toán tử PIT không có (xem 5.3) | Đề yêu cầu 5 toán tử cụ thể |
| **[Quyết định đề xuất]** Công cụ điều phối (gọi LLM, vòng lặp P4, script đo, thống kê) | **Python 3.11+** | Xử lý XML/JSON, gọi API và thống kê (`scipy`) gọn hơn nhiều so với Java. Java chỉ dùng cho `subject`, `reference` và bộ sinh ngẫu nhiên |
| **[Quyết định đề xuất]** LLM | API của Anthropic, model `claude-sonnet-5-5`, đổi được qua cấu hình | Chi phí thấp, đủ khả năng viết test. Cần xác nhận bằng bước S3 |
| Thư viện Python | `anthropic` (bản mới nhất khi làm), `pyyaml`, `scipy`, `numpy`, `matplotlib`, `pytest` (để test công cụ); ghim phiên bản trong `requirements.txt` | Tái lập |
| Phiên bản Java library | JUnit, JaCoCo, PIT: ghim **đúng phiên bản** trong `pom.xml` ở bước T0 và ghi vào báo cáo | Tái lập |
| Temperature | Không dùng. Model Claude đời mới không nhận `temperature`; ghi `effort` thay thế và nêu rõ trong báo cáo | Đề yêu cầu ghi cấu hình |
| Khóa API | `ANTHROPIC_API_KEY` trong `.env` ở thư mục gốc (đã bị `.gitignore` chặn) | Không bao giờ commit |

---

## 2. Cấu trúc thư mục và quy ước

Cấu trúc hiện tại trong [README.md](../README.md), cộng thêm:

```
reference/                     # Chương trình tham chiếu (Maven), viết độc lập từ đặc tả
tools/
  random-gen/                  # P2: bộ sinh test ngẫu nhiên (Java, dùng reference để tính expected)
  llm_client.py                # Gọi LLM, ghi log token, chi phí, thời gian
  test_filter.py               # Biên dịch / chạy / kiểm tra oracle / loại trùng
  generate_p3.py               # P3 – Direct LLM
  generate_p4.py               # P4 – Coverage-guided (cờ --mutation-feedback cho P4b)
  mutate_custom.py             # Mutant tự tạo (xem 5.3)
experiments/
  config/default.yaml          # Mọi tham số thí nghiệm (xem 2.3)
  scripts/measure.py           # Đo một bộ test → metrics.json
  scripts/run_experiment.py    # Chạy toàn bộ P1–P4b × số lần
  scripts/analyze.py           # Thống kê, biểu đồ
requirements.txt
.work/                         # Thư mục tạm khi đo (thêm vào .gitignore)
```

### 2.1 Quy ước đặt tên

| Thứ | Quy ước |
|---|---|
| Mã định danh phương pháp | `p1-manual`, `p2-random`, `p3-direct-llm`, `p4-coverage-guided`, `p4b-mutation-feedback`, `evosuite` |
| Một lần chạy | `run-01` … `run-10`. P1 chỉ có `run-01` |
| Bộ test | `test-suites/<phương pháp>/run-XX/*.java`, package `shop`, tên lớp kết thúc bằng `Test` |
| Log LLM | `results/llm-logs/<phương pháp>/run-XX/calls.jsonl` |
| Báo cáo coverage | `results/coverage/<phương pháp>/run-XX/` (JaCoCo XML và HTML) |
| Báo cáo mutation | `results/mutation/<phương pháp>/run-XX/` (PIT và mutant tự tạo) |
| Số liệu tổng hợp của một lần chạy | `results/comparison/raw/<phương pháp>/run-XX.json` (lược đồ ở mục 5.1) |

### 2.2 Cách đo một bộ test (không làm bẩn `subject/`)

`measure.py` sao chép `subject/` (bỏ `target/`) sang `.work/<id>/`, chép bộ test vào `.work/<id>/src/test/java/`, chạy Maven tại đó, thu kết quả rồi xóa thư mục tạm. `subject/src/test/java/` luôn để trống. Nhờ vậy các lần đo không ảnh hưởng nhau và chạy song song được.

### 2.3 Cấu hình `experiments/config/default.yaml`

Mọi tham số nằm ở đây, không viết cứng trong code:

```yaml
runs: 10
llm:
  provider: anthropic
  model: claude-sonnet-5-5      # cần xác nhận ở bước S3
  effort: medium                # thay cho temperature
  max_output_tokens: 16000
  price_per_mtok_input: null    # điền theo bảng giá hiện hành của nhà cung cấp
  price_per_mtok_output: null
random:
  seed_base: 1000               # seed của run-XX = seed_base + XX
  num_tests: 100
p4:
  max_iterations: 5
  target_branch_coverage: 95
  token_budget: 200000          # tổng token cho một lần chạy P4
  stop_if_no_gain_iterations: 2
  demo_max_iterations: 2        # chế độ demo
measure:
  mutation: true
  timeout_factor: 3
```

---

## 3. Chương trình đối tượng: module giỏ hàng và thanh toán

> **Đề xuất cho G1 xác nhận ở M1.** Con số cụ thể (ngưỡng, mức phí) là giả định để có đặc tả chạy được; sửa được, miễn là chốt trước M1 và mọi gói dùng cùng bản.

### 3.1 Nguyên tắc

- Package `shop`. Tiền là `long`, đơn vị VND, **không dùng số thực**. Chia lấy phần nguyên (làm tròn xuống).
- **Không đọc đồng hồ hệ thống**: mọi hàm cần thời gian nhận `LocalDate` / `LocalDateTime` qua tham số, để test xác định được.
- Không dùng database, mạng, hay trạng thái toàn cục. Dữ liệu nằm trong bộ nhớ.
- Ngoại lệ không kiểm tra (unchecked). Dùng `IllegalArgumentException`, `IllegalStateException`, `NoSuchElementException` và 3 ngoại lệ riêng ở dưới.
- **Thứ tự kiểm tra trong mỗi hàm là một phần của đặc tả** (khi vi phạm nhiều điều kiện, ngoại lệ nào được ném trước).

### 3.2 Kiểu dữ liệu

| Kiểu | Nội dung |
|---|---|
| `enum Region` | `INNER_CITY`, `SUBURB`, `PROVINCE`, `REMOTE` |
| `enum CouponType` | `PERCENT`, `FIXED` |
| `enum OrderStatus` | `PENDING`, `PAID`, `SHIPPED`, `CANCELLED` |
| `InsufficientStockException`, `CouponExpiredException`, `CouponNotApplicableException` | kế thừa `RuntimeException` |
| `Product(String id, String name, long price, int weightGrams, int stock)` | `id` không rỗng; `price > 0`; `weightGrams > 0`; `stock >= 0`, nếu không ném `IllegalArgumentException`. Có `reduceStock(int q)` và `restoreStock(int q)` |
| `Coupon(String code, CouponType type, long value, long minOrder, long maxDiscount, LocalDate expiry)` | `code` không rỗng; `type`, `expiry` khác null; `value > 0`; `PERCENT` thì `value <= 100`; `minOrder >= 0`; `maxDiscount >= 0` (0 nghĩa là không giới hạn, chỉ áp cho `PERCENT`), nếu không ném `IllegalArgumentException` |
| `CartItem`, `Order` | Đối tượng dữ liệu; `Order` giữ id, danh sách dòng hàng (bản sao), `subtotal`, `discount`, `shipping`, `total`, `status`, `createdAt`, `region` |

`Product.reduceStock(q)`: `q <= 0` → `IllegalArgumentException`; `q > stock` → `InsufficientStockException`. `Product.restoreStock(q)`: `q <= 0` → `IllegalArgumentException`.

### 3.3 `Cart`

Hằng số `MAX_QTY_PER_ITEM = 50`.

| Hàm | Quy tắc, theo thứ tự kiểm tra |
|---|---|
| `void addItem(Product p, int qty)` | 1. `p == null` hoặc `qty <= 0` → `IllegalArgumentException`. 2. `newQty` = số đang có + `qty`. 3. `newQty > MAX_QTY_PER_ITEM` → `IllegalArgumentException`. 4. `newQty > p.getStock()` → `InsufficientStockException`. 5. Đặt số lượng bằng `newQty` |
| `void updateQuantity(String productId, int qty)` | 1. `qty < 0` → `IllegalArgumentException`. 2. Không có trong giỏ → `NoSuchElementException`. 3. `qty == 0` → xóa dòng. 4. `qty > MAX_QTY_PER_ITEM` → `IllegalArgumentException`. 5. `qty > stock` → `InsufficientStockException`. 6. Đặt số lượng |
| `void removeItem(String productId)` | Không có trong giỏ → `NoSuchElementException`; ngược lại xóa |
| `long getSubtotal()` | Tổng `price × qty` của mọi dòng; giỏ rỗng trả 0 |
| `int getTotalWeightGrams()` | Tổng `weightGrams × qty` |
| `boolean isEmpty()`, `List<CartItem> getItems()` | `getItems` trả danh sách không sửa được |

### 3.4 `PricingService.calculateDiscount(long subtotal, Coupon coupon, LocalDate today)`

1. `subtotal < 0` → `IllegalArgumentException`.
2. `coupon == null` → trả 0.
3. `today` sau `coupon.expiry` → `CouponExpiredException` (ngày hết hạn vẫn còn hiệu lực).
4. `subtotal < coupon.minOrder` → `CouponNotApplicableException`.
5. `PERCENT`: `d = subtotal × value / 100` (làm tròn xuống); nếu `maxDiscount > 0` và `d > maxDiscount` thì `d = maxDiscount`.
6. `FIXED`: `d = min(value, subtotal)`.
7. Trả `d`.

### 3.5 `ShippingCalculator.calculate(Region region, int weightGrams, long orderValue)`

`orderValue` là giá trị hàng **sau khi trừ giảm giá**.

1. `region == null`, `weightGrams <= 0` hoặc `orderValue < 0` → `IllegalArgumentException`.
2. `weightGrams > 20000` → `IllegalArgumentException` (quá khối lượng).
3. Miễn phí vận chuyển: `orderValue >= 500000` **và** `region` là `INNER_CITY` hoặc `SUBURB` → trả 0.
4. Phí cơ bản theo `switch`: `INNER_CITY` 15000, `SUBURB` 25000, `PROVINCE` 35000, `REMOTE` 55000.
5. Phụ phí khối lượng: phần vượt 1000 g, mỗi **500 g bắt đầu** cộng 2000 (cài bằng vòng lặp).
6. Trả phí cơ bản + phụ phí.

### 3.6 `OrderService`

| Hàm | Quy tắc |
|---|---|
| `Order placeOrder(String orderId, Cart cart, Coupon coupon, Region region, LocalDateTime now)` | 1. `orderId` rỗng hoặc `region == null` → `IllegalArgumentException`. 2. `cart == null` hoặc rỗng → `IllegalStateException`. 3. Kiểm tra lại tồn kho từng dòng: thiếu → `InsufficientStockException`. 4. `subtotal`, `discount = calculateDiscount(subtotal, coupon, now.toLocalDate())`, `goods = subtotal − discount`, `shipping = calculate(region, tổng khối lượng, goods)`. 5. `total = goods + shipping`. 6. **Chỉ sau khi mọi bước trên thành công** mới trừ tồn kho (đảm bảo nguyên tử). 7. Trả `Order` ở trạng thái `PENDING` |
| `void pay(Order order)` | `null` → `IllegalArgumentException`; khác `PENDING` → `IllegalStateException`; ngược lại → `PAID` |
| `void ship(Order order)` | `null` → `IllegalArgumentException`; khác `PAID` → `IllegalStateException`; ngược lại → `SHIPPED` |
| `long cancelOrder(Order order, LocalDateTime now)` | 1. `null` → `IllegalArgumentException`. 2. Trạng thái `SHIPPED` hoặc `CANCELLED` → `IllegalStateException`. 3. `now` trước `createdAt` → `IllegalArgumentException`. 4. Hoàn tiền: `PENDING` → 0; `PAID` và `now` không muộn hơn `createdAt + 24 giờ` → `total`; `PAID` và quá 24 giờ → `(subtotal − discount) × 80 / 100` (không hoàn phí vận chuyển). 5. Hoàn lại tồn kho cho mọi dòng. 6. Trạng thái → `CANCELLED`, trả số tiền hoàn |

### 3.7 Nhánh khó phủ có chủ ý

Để P4 có chỗ cải thiện so với P3, các tình huống sau cần có trong chương trình và được test thủ công (P1) phủ:

- Miễn phí vận chuyển dùng giá trị **sau giảm giá**: `subtotal ≥ 500000` nhưng `goods < 500000` thì không được miễn phí.
- Mã `PERCENT` chạm trần `maxDiscount`, và đúng ngưỡng `minOrder`.
- Ngày hết hạn của mã đúng bằng ngày hiện tại.
- Biên khối lượng: 1000, 1001, 1500, 1501, 20000, 20001 g.
- Biên 24 giờ khi hủy đơn: đúng 24 giờ và 24 giờ 1 giây.
- Tính nguyên tử của `placeOrder`: lỗi vận chuyển (quá khối lượng) **không** được làm mất tồn kho.
- Thứ tự kiểm tra trong `addItem` và `updateQuantity`.

Chương trình có khoảng 15 hàm, vượt mức tối thiểu 3–5 hàm của đề, nên bộ test P1 ước tính 50–80 test.

---

## 4. Chương trình tham chiếu và cách chứng minh oracle đúng

Thầy chấm riêng việc **chứng minh tính đúng đắn của test oracle**. Cách làm: một bản cài đặt thứ hai, độc lập, làm chuẩn "đúng theo đặc tả".

### 4.1 `reference/`

- Maven module riêng, **cùng package, cùng tên lớp, cùng chữ ký hàm** như `subject/` (chữ ký đóng băng ở M1 trong `docs/spec/api.md`) để một bộ test biên dịch được trên cả hai.
- **Viết từ đặc tả (`docs/spec/`) bởi người khác hoặc phiên AI khác với bản `subject/`, và không được xem mã của `subject/`.** Nếu cùng một nguồn viết cả hai, một hiểu lầm sẽ lặp lại ở cả hai bản và không ai phát hiện.

### 4.2 Quy tắc "test hợp lệ" (dùng trong `test_filter.py`)

Một test (một hàm `@Test`) được tính là **hợp lệ** khi đồng thời:

1. Biên dịch được với `subject/`.
2. **Chạy xanh trên `reference/`** (đặc tả là chuẩn đúng).
3. Chạy xanh trên `subject/` (PIT yêu cầu bộ test xanh trên bản gốc).
4. Không trùng với test đã giữ (so sánh thân hàm sau khi chuẩn hóa khoảng trắng).

Mỗi test bị loại được ghi lại cùng lý do: `compile_error`, `fails_on_reference` (oracle sai), `fails_on_subject_only` (bất đồng giữa subject và reference, cần xem lại đặc tả), `duplicate`.

Hai chỉ số bắt buộc ghi vào `metrics.json` và báo cáo:

- **Tỷ lệ test biên dịch được** = biên dịch được / tổng số test sinh ra.
- **Tỷ lệ oracle đúng** = chạy xanh trên `reference` / số test biên dịch được.

### 4.3 Kiểm tra `subject` ≡ `reference`

Trước M2 phải chạy bộ P1 trên cả hai bản và xanh hết. Nếu có bất đồng, **sửa đặc tả trước**, rồi sửa bản sai. Không sửa kết quả để cho khớp.

---

## 5. Đo lường

### 5.1 Lược đồ `results/comparison/raw/<phương pháp>/run-XX.json`

```json
{
  "method": "p3-direct-llm",
  "run": 1,
  "tests": { "generated": 0, "compiled": 0, "valid": 0, "passed": 0, "failed": 0,
             "dropped": [ { "name": "", "reason": "compile_error|fails_on_reference|fails_on_subject_only|duplicate" } ] },
  "coverage": {
    "statement": { "covered": 0, "total": 0, "pct": 0.0 },
    "branch":    { "covered": 0, "total": 0, "pct": 0.0 },
    "function":  { "covered": 0, "total": 0, "pct": 0.0 }
  },
  "uncovered": { "lines": [ { "class": "", "method": "", "line": 0 } ],
                 "branches": [ { "class": "", "method": "", "line": 0, "missed": 0, "covered": 0 } ] },
  "mutation": { "total": 0, "killed": 0, "survived": 0, "no_coverage": 0, "timed_out": 0,
                "equivalent": 0, "score": 0.0,
                "by_operator": { "<toán tử>": { "total": 0, "killed": 0 } } },
  "time": { "generation_s": 0.0, "execution_s": 0.0 },
  "llm": { "calls": 0, "input_tokens": 0, "output_tokens": 0, "cost_usd": 0.0, "model": "", "effort": "" },
  "iterations": [ { "i": 0, "statement_pct": 0.0, "branch_pct": 0.0, "kept_tests": 0, "tokens": 0 } ]
}
```

- `iterations` chỉ có ở P4 và P4b (dùng vẽ đường cải thiện coverage).
- `llm` của P1, P2, EvoSuite để `calls = 0`.
- `cost_usd` = (`input_tokens` × giá vào + `output_tokens` × giá ra) / 1.000.000, giá lấy từ cấu hình.
- Số liệu coverage đọc từ `target/site/jacoco/jacoco.xml`; **chỉ tính lớp trong `shop`**, không tính test.

### 5.2 `measure.py`

```bash
python experiments/scripts/measure.py --suite test-suites/p1-manual/run-01 --method p1-manual --run 1 [--mutation]
```

Đọc `generation.json` nằm cạnh bộ test (nếu có) để lấy thời gian sinh và số liệu LLM. Thoát mã 0 khi đo xong, mã khác 0 khi bộ test không xanh trên `subject/`.

### 5.3 Mutation: 5 toán tử của đề

Điều kiện: bộ test xanh trên `subject/`. **Mutant không được test nào chạy tới (`NO_COVERAGE`) vẫn nằm trong mẫu số.**

`Mutation score = killed / (total − equivalent) × 100%`

| Toán tử của đề | Dự kiến trong PIT | Trạng thái |
|---|---|---|
| Thay toán tử so sánh (`>` thành `>=`) | `CONDITIONALS_BOUNDARY` | **Cần xác nhận ở S2** |
| Thay toán tử số học (`+` thành `-`) | `MATH` | **Cần xác nhận ở S2** |
| Thay toán tử điều kiện (`&&` thành `\|\|`) | PIT làm việc trên bytecode, nhiều khả năng **không có** phép thay `&&` bằng `\|\|` trực tiếp | **Dự kiến phải tự tạo** |
| Thay hằng boolean (`true` thành `false`) | Có thể chỉ có đổi giá trị trả về, không đổi hằng | **Dự kiến phải tự tạo** |
| Xóa câu lệnh | Có thể chỉ xóa lời gọi hàm `void` | **Dự kiến phải tự tạo một phần** |

Bảng "Dự kiến" dựa trên hiểu biết chung về PIT, **chưa được chạy kiểm chứng**; spike S2 quyết định.

Nếu S2 xác nhận thiếu, `tools/mutate_custom.py` tạo mutant ở mức mã nguồn (mỗi vị trí một mutant) cho các toán tử còn thiếu, chạy bộ test trên từng mutant, và ghi kết quả cùng khuôn với PIT. Điểm mutation cuối cùng gộp cả hai nguồn và ghi rõ nguồn của từng toán tử trong `by_operator`.

### 5.4 Mutant tương đương

File `results/mutation/equivalent-mutants.csv`, do G1 xem xét thủ công sau khi chạy xong:

```
source,class,method,line,mutator,index,description,reason
```

Mutant đánh dấu tương đương bị loại khỏi mẫu số cho **mọi** phương pháp, để các phương pháp so sánh được với nhau.

---

## 6. Giao diện các phương pháp

Mỗi bộ sinh ghi ra `test-suites/<phương pháp>/run-XX/` và file `generation.json` cùng thư mục:

```json
{ "method": "", "run": 1, "started_at": "", "ended_at": "", "generation_s": 0.0,
  "llm": { "calls": 0, "input_tokens": 0, "output_tokens": 0, "model": "", "effort": "" },
  "iterations": [] }
```

| Phương pháp | Cách sinh | Ghi chú thiết kế |
|---|---|---|
| **P1** `p1-manual` | Viết tay theo đặc tả, **không xem mã nguồn** | Cũng là bộ test chuẩn để đối chiếu |
| **P2** `p2-random` | `tools/random-gen/` (Java). Seed = `seed_base + XX`. Đầu vào ngẫu nhiên có ràng buộc hợp lệ, **expected output tính bằng cách gọi `reference/`**, sinh ra file JUnit với giá trị cụ thể | Không bao giờ lấy expected từ `subject/` |
| **P3** `p3-direct-llm` | `generate_p3.py`: gửi mã nguồn **và** đặc tả cho LLM một lần | Bộ prompt ở `prompts/direct/` |
| **P4** `p4-coverage-guided` | `generate_p4.py`: xem 6.1 | Bộ prompt ở `prompts/coverage-guided/` |
| **P4b** `p4b-mutation-feedback` | `generate_p4.py --mutation-feedback`: P4 cộng phản hồi mutation | Bộ prompt ở `prompts/mutation-feedback/` |
| `evosuite` | Chạy công cụ, nếu tương thích với JDK | Có thể bỏ, ghi lý do vào báo cáo |

### 6.1 Thiết kế P4 và P4b

- **[Quyết định đề xuất] Thiết kế ghép cặp:** `run-XX` của P4 **bắt đầu từ chính bộ test của P3 `run-XX`** thay vì sinh bộ ban đầu mới. Khi đó mọi cải thiện của P4 so với P3 là do vòng phản hồi, và RQ2, RQ4 so sánh theo cặp được. Chi phí và thời gian báo cáo của P4 là **tổng** (P3 cộng phần phản hồi).
- **P4b** bắt đầu từ bộ test cuối của P4 `run-XX` rồi thêm phản hồi mutation. Đây là thí nghiệm bóc tách tác dụng của cải tiến.
- Vòng lặp:
  1. Đo coverage của bộ hiện tại (`measure.py` ở chế độ không mutation).
  2. Dừng nếu `branch ≥ target`, hết `max_iterations`, hết `token_budget`, hoặc `stop_if_no_gain_iterations` vòng liên tiếp không tăng coverage.
  3. Trích các nhánh và dòng chưa phủ cùng **điều kiện nhánh trong mã nguồn** (kèm vài dòng ngữ cảnh) và gửi cho LLM.
  4. LLM trả test bổ sung; `test_filter.py` lọc (mục 4.2).
  5. Chỉ **giữ test làm tăng coverage** (chọn tham lam theo mức đóng góp). Gộp vào bộ, ghi một dòng vào `iterations`.
- P4b thay bước 3 bằng danh sách **mutant còn sống** (vị trí, toán tử, đoạn mã đã bị đổi) và giữ test nào diệt thêm được mutant.
- **Chế độ demo:** `--demo` dùng `demo_max_iterations`, chạy trên bộ P3 đã có sẵn để trình diễn nhanh.

### 6.2 Định dạng test do LLM sinh

LLM trả **từng test là một khối mã riêng** (một hàm `@Test` tự chứa, không dùng hàm phụ). `test_filter.py` bọc mỗi khối thành một lớp `shop.<Tên>_NNNTest` rồi mới biên dịch và chạy. Nhờ vậy lọc được ở mức từng test, và "số test" đếm chính xác bằng số hàm `@Test`.

### 6.3 Ghi log LLM

`llm_client.py` ghi một dòng JSON cho mỗi lần gọi vào `calls.jsonl`: thời điểm, model, effort, prompt đầy đủ, phản hồi đầy đủ, `input_tokens`, `output_tokens`, thời gian. Không ghi khóa API.

---

## 7. Spike cần làm trước (ngày đầu)

| Mã | Câu hỏi | Kết quả cần có |
|---|---|---|
| **S1** | JDK 17/21 + JaCoCo + PIT chạy được trên một dự án mẫu nhỏ chưa? | Lệnh chạy được, ghi phiên bản JDK, JaCoCo, PIT đã dùng |
| **S2** | PIT có đủ 5 toán tử của đề không? | Bảng ánh xạ toán tử ↔ mutator (thay bảng "Dự kiến" ở 5.3); quyết định có cần `mutate_custom.py` không |
| **S3** | Gọi API thành công: model nào dùng được, trường token trả về, `effort` có nhận không, giá hiện hành | 1 lần gọi thật; điền `price_per_mtok_*` vào cấu hình |
| **S4** (tùy chọn) | EvoSuite chạy được với JDK đã chọn không? | Có / không và lý do |

---

## 8. Thứ tự implement và tiêu chí "xong"

| Bước | Gói | Nội dung | Phụ thuộc | Lệnh kiểm tra / điều kiện xong |
|---|---|---|---|---|
| **T0** | G2 | Môi trường: JDK LTS, `requirements.txt`, `.gitignore` thêm `.work/`, spike S1–S3 | – | `java -version` ra 17 hoặc 21; `mvn -q -f subject/pom.xml test` chạy được trên dự án mẫu; 1 lần gọi API thật thành công |
| **T1** | G1 | Đặc tả `docs/spec/` và `docs/spec/api.md`; khung Java (chữ ký, thân hàm chưa cài) | – | `mvn -q -f subject/pom.xml compile` thành công. **Mốc M1 (25/11)** |
| **T2** | G1 | Cài đặt `subject/` theo đặc tả | T1 | Compile; mỗi hàm có ít nhất một test khói chạy xanh |
| **T3** | G3 | Cài đặt `reference/` theo đặc tả (**không xem `subject/`**) | T1 | `mvn -q -f reference/pom.xml compile` thành công |
| **T4** | G2 | `subject/pom.xml`: JaCoCo, PIT, ghim phiên bản | T0, T1 | `mvn test jacoco:report` ra `jacoco.xml`; PIT ra báo cáo |
| **T5** | G3 | Bộ P1 (≥ 50 test, xem 3.7) | T1 | Xanh trên **cả** `subject` và `reference` |
| **T6** | G2 | `measure.py` + lược đồ 5.1 + kiểm tra lược đồ | T4, T5 | Chạy cho P1 ra `run-01.json` đúng lược đồ; **đối chiếu tay** statement/branch với báo cáo HTML của JaCoCo |
| **T7** | G2, G1 | `mutate_custom.py` (nếu S2 yêu cầu), `equivalent-mutants.csv` | S2, T6 | Mutation score của P1 tính được; mỗi toán tử có ít nhất một mutant |
| **T8** | G3 | `tools/random-gen/` (P2) | T3 | 10 lần chạy sinh 10 bộ biên dịch được; cùng seed cho cùng bộ test |
| **T9** | G4 | `llm_client.py` và `test_filter.py` | T0, T3 | Test cho bộ lọc: đưa vào test cố ý hỏng/trùng/sai oracle, mỗi loại bị loại đúng lý do |
| **T10** | G4 | `generate_p3.py` và prompt | T9 | 1 lần chạy: có bộ test, `generation.json`, `calls.jsonl`; tỷ lệ biên dịch và oracle đúng được báo cáo. **Mốc M3** |
| **T11** | G5 | `generate_p4.py` (P4) | T6, T10 | Chạy `--demo` trên bộ P3 có sẵn: ghi `iterations`, coverage không giảm qua các vòng |
| **T12** | G5 | P4b: phản hồi mutation | T7, T11 | Chạy được; ghi được số mutant diệt thêm. **Mốc M4** |
| **T13** | G3 | EvoSuite (nếu S4 đạt) | S4 | Có kết quả hoặc có ghi lý do bỏ |
| **T14** | G2 | `run_experiment.py` và `analyze.py` (Mann–Whitney U, Vargha–Delaney Â12, bảng, biểu đồ) | T8, T11, T12 | Một lệnh chạy lại toàn bộ; bảng có đủ 8 tiêu chí cho mọi phương pháp. **Chốt số liệu M5 (20/12)** |
| **T15** | Cả nhóm | Điền báo cáo từ `results/comparison/` | T14 | Mỗi RQ có bảng/biểu đồ và phân tích |

---

## 9. Quy tắc cho AI

1. **Không sửa `subject/` sau khi đặc tả chốt (M1)** trừ khi đặc tả đổi; khi đó cập nhật cả `reference/`, P1 và `docs/spec/`.
2. **Không bao giờ xem mã của `subject/` khi viết `reference/`**, `p1-manual` hay oracle. Dùng đặc tả.
3. **Không sửa tay test do LLM sinh.** Chỉ lọc, và ghi lý do loại.
4. **Không bịa số liệu**, không điền kết quả giả vào báo cáo, không làm tròn để đẹp. Chạy thật, lưu thật.
5. **Mọi tham số nằm trong `experiments/config/`**, không viết cứng.
6. **Không đưa khóa API vào mã, log, commit hay chat.** Log LLM không chứa khóa.
7. **Mỗi bước kết thúc bằng một commit** theo quy ước trong README, trên nhánh riêng của gói.
8. **Không tự mở rộng phạm vi**: không thêm framework web, database, giao diện, hay chức năng ngoài mục 3.
9. Khi lệnh kiểm tra không qua, **báo đúng kết quả** thay vì sửa tiêu chí cho qua.
10. Công cụ Python có test riêng bằng `pytest` cho phần xử lý quan trọng (bộ lọc, đọc JaCoCo, tính điểm mutation).

---

## 10. Điểm cần nhóm xác nhận

| # | Nội dung | Ghi chú |
|---|---|---|
| 1 | Quy tắc nghiệp vụ ở mục 3 (ngưỡng, mức phí, thời hạn 24 giờ, số lượng tối đa 50) | G1 duyệt hoặc sửa trước 25/11 |
| 2 | Công cụ điều phối bằng **Python** | Đề xuất; đổi sang Java sẽ làm nặng phần thống kê và xử lý XML |
| 3 | Dùng `claude-sonnet-5-5` | Đề xuất; chốt sau S3 và cân nhắc ngân sách |
| 4 | Thiết kế ghép cặp P3 → P4 → P4b | Tiết kiệm chi phí và so sánh được theo cặp, nhưng P4 phụ thuộc P3 |
| 5 | Thêm P4b làm thí nghiệm bóc tách | Tăng số thư mục và thời gian chạy; nếu thiếu thời gian, gộp cải tiến vào P4 và bỏ P4b |
| 6 | Ai viết `reference/` | Phải khác người viết `subject/` |
| 7 | Chọn JDK 17 hay 21 | Cả nhóm dùng cùng một bản LTS |

README cần cập nhật theo mục 2 (thêm `reference/`, `tools/random-gen/`, sơ đồ `results/`) khi nhóm duyệt tài liệu này.
