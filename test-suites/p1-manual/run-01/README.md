# P1 · Bộ test thủ công, run-01

Bộ test gồm **181 ca JUnit 5** trong package `shop`. Oracle lấy từ đặc tả M1. Các ca dùng ngày 23/11/2026 và thời điểm đặt có phần nano giây cố định để kiểm tra đúng mốc hủy. Mỗi lần chạy một bộ tham số dùng dữ liệu nghiệp vụ riêng.

| Lớp | `@Test` | Bộ tham số | Tổng ca |
|---|---:|---:|---:|
| CartTest | 14 | 19 | 33 |
| DomainTest | 7 | 33 | 40 |
| OrderLifecycleTest | 8 | 18 | 26 |
| OrderTest | 8 | 12 | 20 |
| PlaceOrderTest | 10 | 11 | 21 |
| PricingTest | 7 | 10 | 17 |
| ShippingTest | 0 | 24 | 24 |
| **Tổng** | **54** | **127** | **181** |

127 bộ tham số thuộc 30 phương thức `@ParameterizedTest`. Mỗi hàng `@CsvSource` hoặc phần tử `@ValueSource` tính là một ca. Các thao tác liên tiếp và các assert cùng kiểm tra một tình huống chỉ tính một lần.

## Bảng truy vết

32 dòng dưới đây tương ứng với toàn bộ 32 dòng tình huống của [branches.md](../../../docs/spec/branches.md). Phân hoạch đầu vào nằm ở các ca constructor và ngoại lệ. Bảng quyết định kết hợp vùng với ngưỡng phí, thứ tự lỗi với dữ liệu giỏ, trạng thái nguồn với thao tác đơn. Các biên và kết quả quan sát được ghi ở cột cuối.

| Dòng branches.md | Mục đặc tả | Lớp và phương thức test | Biên hoặc kết quả quan sát |
|---:|---|---|---|
| [9](../../../docs/spec/branches.md#L9) | [Enum](../../../docs/spec/domain.md) | [DomainTest](DomainTest.java) · `regionEnumeration`, `couponEnumeration`, `statusEnumeration` | Thứ tự, bản sao mảng, tra tên đúng/sai/null cho cả ba enum. |
| [10](../../../docs/spec/branches.md#L10) | [Ngoại lệ riêng](../../../docs/spec/domain.md) | [DomainTest](DomainTest.java) · `domainExceptionsAcceptMessages` | Từng constructor với chuỗi thường, rỗng và null. |
| [11](../../../docs/spec/branches.md#L11) | [Product(...)](../../../docs/spec/domain.md) | [DomainTest](DomainTest.java) · `invalidProduct`, `productKeepsFieldsAtLowerBounds` | Từng trường sai riêng biệt, kèm bộ hợp lệ tại các cận 1/1/0. |
| [12](../../../docs/spec/branches.md#L12) | [Product · getter](../../../docs/spec/domain.md) | [DomainTest](DomainTest.java) · `productKeepsFieldsAtLowerBounds`, `stockCanReachZeroAndExceedOriginalStock` | Giữ nguyên khoảng trắng và trường ban đầu, đọc lại tồn sau giảm và hoàn. |
| [13](../../../docs/spec/branches.md#L13) | [reduceStock / restoreStock](../../../docs/spec/domain.md) | [DomainTest](DomainTest.java) · `stockRejectsNonpositiveChanges`, `stockCanReachZeroAndExceedOriginalStock` | −1, 0, 1, vừa hết, thiếu 1, hoàn lên 51. |
| [14](../../../docs/spec/branches.md#L14) | [Coupon](../../../docs/spec/domain.md) | [DomainTest](DomainTest.java) · `invalidCoupon`, `couponRequiresExpiry`, `couponKeepsAllSixFields` | Bảy bước từ chối, biên PERCENT 0/1/100/101, FIXED 101 với trần âm/0/dương. |
| [15](../../../docs/spec/branches.md#L15) | [CartItem](../../../docs/spec/domain.md) | [DomainTest](DomainTest.java) · `lineRequiresProduct`, `lineRequiresPositiveQuantity`, `lineTotalUsesLongAndIgnoresStock` | Lượng âm/0/1, tồn 0, lượng 51 và 3 × 10⁹ VND. |
| [16](../../../docs/spec/branches.md#L16) | [Cart() và các phép đọc](../../../docs/spec/cart.md) | [CartTest](CartTest.java) · `totalsFollowAddedAndRemovedLines`, `subtotalExceedsIntegerRange` | Giỏ mới, một dòng, hai dòng, xóa hết và tạm tính vượt int. |
| [17](../../../docs/spec/branches.md#L17) | [addItem](../../../docs/spec/cart.md) | [CartTest](CartTest.java) · `addRequiresProduct`, `addRequiresPositiveQuantity`, `addSameObjectAccumulatesInPlace`, `addUsesNewProductForStockPriceAndWeight`, `addInsufficientReplacementStockKeepsOldProduct`, `addNewIdShortByOneLeavesEmptyCart` | Thêm mới, cộng cùng ID, thay đối tượng, kiểm tra tồn mới, vừa đủ tại 50. |
| [18](../../../docs/spec/branches.md#L18) | [addItem · bước 3–4](../../../docs/spec/cart.md) | [CartTest](CartTest.java) · `addLimitPrecedesNewProductStock` | 49 + 2 = 51, tồn mới 3. Kiểm tra loại lỗi, cả hai dòng cũ và mọi tồn. |
| [19](../../../docs/spec/branches.md#L19) | [updateQuantity](../../../docs/spec/cart.md) | [CartTest](CartTest.java) · `updateChecksSignThenIdBeforeZeroOrLimit`, `updateLimitPrecedesStock`, `updateShortByOne`, `updateRechecksUnchangedQuantityAgainstCurrentStock`, `updateReplacesInsteadOfAdding`, `updateZeroRemovesLastLineEvenAfterStockChange` | Ưu tiên dấu → ID → xóa → giới hạn → tồn. Kiểm tra lượng cũ sau khi tồn giảm. |
| [20](../../../docs/spec/branches.md#L20) | [removeItem / getItems](../../../docs/spec/cart.md) | [CartTest](CartTest.java) · `removeRequiresExactId`, `removalAndReinsertionPreserveRelativeOrder`, `itemsAreAnImmutableSnapshot` | ID null/trắng/lạ hoặc khác chữ hoa, xóa giữa rồi thêm cuối, bản chụp và iterator. |
| [26](../../../docs/spec/branches.md#L26) | [PricingService() / calculateDiscount · bước 1–2](../../../docs/spec/pricing.md) | [PricingTest](PricingTest.java) · `negativeSubtotalPrecedesMissingCoupon`, `dateRequiredEvenWithoutCoupon`, `noCouponDiscountsNothing` | Âm hoặc thiếu ngày bị từ chối trước khi xét mã null. Ngày hợp lệ, không mã thì giảm 0. |
| [27](../../../docs/spec/branches.md#L27) | [calculateDiscount · bước 3–4](../../../docs/spec/pricing.md) | [PricingTest](PricingTest.java) · `expiryPrecedesMinimum`, `liveCouponOneBelowMinimum`, `dayBeforeExpiryAndAboveMinimum`, `percentCapAtMinimumOnExpiry` | Trước/đúng/sau ngày hết hạn. Đơn dưới/bằng/trên ngưỡng, kể cả sai cả hạn lẫn ngưỡng. |
| [28](../../../docs/spec/branches.md#L28) | [calculateDiscount · PERCENT](../../../docs/spec/pricing.md) | [PricingTest](PricingTest.java) · `percentCapAtMinimumOnExpiry`, `percentMultiplicationUsesLong`, `percentCanRoundDownToZero` | Tại 99999, 15% lấy 14999. Thử trần 0/14999/10000/15000, phép nhân long và làm tròn. |
| [29](../../../docs/spec/branches.md#L29) | [calculateDiscount · FIXED](../../../docs/spec/pricing.md) | [PricingTest](PricingTest.java) · `fixedIgnoresCapButCannotExceedSubtotal` | Mã 400, bỏ trần 1 tại các tạm tính 0/399/400/401. |
| [30](../../../docs/spec/branches.md#L30) | [ShippingCalculator() / calculate · bước 1–2](../../../docs/spec/shipping.md) | [ShippingTest](ShippingTest.java) · `invalidInputBeforeFreeShipping` | Thiếu vùng, cân nặng −1/0/20001 hoặc tiền âm. Quá cân tại cả hai phía ngưỡng. |
| [31](../../../docs/spec/branches.md#L31) | [calculate · bước 3–4](../../../docs/spec/shipping.md) | [ShippingTest](ShippingTest.java) · `freeShippingDecisionTable`, `heavyShipmentAboveThreshold` | Bốn vùng × 499999/500000 tại 1000 g, rồi 500001 tại 20000 g. |
| [32](../../../docs/spec/branches.md#L32) | [calculate · bước 2 và 5–6](../../../docs/spec/shipping.md) | [ShippingTest](ShippingTest.java) · `startedWeightBlocks`, `invalidInputBeforeFreeShipping` | 1000/1001/1500/1501/20000 g tương ứng 0/1/1/2/38 khối. 20001 g bị từ chối. |
| [38](../../../docs/spec/branches.md#L38) | [Order(...) và getter](../../../docs/spec/domain.md) | [OrderTest](OrderTest.java) · `constructorSnapshotsSourceAndExposesNineGetters`, `constructorRequiresId`, `constructorRejectsNullLines`, `constructorRejectsEmptyLines`, `constructorRejectsNullElementAfterValidLine`, `constructorValidatesMoney`, `constructorRequiresTime`, `constructorRequiresRegion`, `constructorAllowsFullDiscountAndZeroTotal` | Sai từng điều kiện của bước 1–6, biên tiền hợp lệ, bản chụp, trạng thái đầu và chín getter. |
| [39](../../../docs/spec/branches.md#L39) | [Order.setStatus](../../../docs/spec/domain.md) | [OrderTest](OrderTest.java) · `setterRejectsNull`, `setterOnlyChangesStatus` | Null giữ nguyên đơn và tồn. Cả bốn giá trị enum chỉ đổi trạng thái. |
| [40](../../../docs/spec/branches.md#L40) | [Hai constructor của OrderService](../../../docs/spec/order.md) | [PlaceOrderTest](PlaceOrderTest.java) · `serviceRequiresPricing`, `serviceRequiresShipping`, `injectedServicesReceiveArgumentsOnceInOrder`, `freeShippingUsesDiscountedGoods` | Mặc định và tiêm. Hai phụ thuộc bắt buộc, constructor gọi bộ tính 0 lần. |
| [41](../../../docs/spec/branches.md#L41) | [placeOrder · bước 1–2](../../../docs/spec/order.md) | [PlaceOrderTest](PlaceOrderTest.java) · `invalidHeaderPrecedesNullOrEmptyCart`, `validHeaderRequiresPopulatedCart` | ID/vùng/thời điểm sai trước giỏ null/rỗng. Tổng số lần gọi bộ tính bằng 0. |
| [42](../../../docs/spec/branches.md#L42) | [placeOrder · bước 3](../../../docs/spec/order.md) | [PlaceOrderTest](PlaceOrderTest.java) · `stockOfLaterLineCheckedBeforeExpiredCoupon`, `freeShippingUsesDiscountedGoods` | Dòng đầu đủ, dòng sau thiếu với mã hết hạn. Trường hợp thành công còn đúng 0 ở dòng vừa đủ. |
| [43](../../../docs/spec/branches.md#L43) | [placeOrder · bước 4 và 6](../../../docs/spec/order.md) | [PlaceOrderTest](PlaceOrderTest.java) · `expiredCouponStopsOrder`, `minimumShortfallStopsOrder`, `injectedPriceFailureStopsBeforeShipping`, `injectedShippingFailureLeavesAllLinesUntouched`, `overweightAfterCheckingBothStocksIsAtomic` | Lỗi mã, lỗi bộ tính được tiêm, tổng 20001 g. So từng dòng và tồn, đối chiếu chính ngoại lệ được tiêm. |
| [44](../../../docs/spec/branches.md#L44) | [placeOrder · goods và total](../../../docs/spec/order.md) | [PlaceOrderTest](PlaceOrderTest.java) · `freeShippingUsesDiscountedGoods` | Nội thành, 1000 g. Tạm tính 500000, FIXED 1 dẫn đến tổng 514999. Không mã dẫn đến tổng 500000. |
| [45](../../../docs/spec/branches.md#L45) | [placeOrder · bước 5–6](../../../docs/spec/order.md) | [PlaceOrderTest](PlaceOrderTest.java) · `repeatIdCreatesIndependentOrdersAndSnapshots` | Hai đơn cùng ID trên một giỏ, sau đó trả tiền đơn sau, thay A cùng ID và xóa B. Đối chiếu từng bản chụp. |
| [46](../../../docs/spec/branches.md#L46) | [pay](../../../docs/spec/order.md) | [OrderLifecycleTest](OrderLifecycleTest.java) · `payRequiresOrder`, `payMovesPendingToPaid`, `payRejectsOtherStates` | Null và cả bốn trạng thái nguồn. Chỉ PENDING chuyển thành PAID. |
| [47](../../../docs/spec/branches.md#L47) | [ship](../../../docs/spec/order.md) | [OrderLifecycleTest](OrderLifecycleTest.java) · `shipRequiresOrder`, `shipMovesPaidToShipped`, `shipRejectsOtherStates` | Null và cả bốn trạng thái nguồn. Chỉ PAID chuyển thành SHIPPED. |
| [48](../../../docs/spec/branches.md#L48) | [cancelOrder · bước 1–3](../../../docs/spec/order.md) | [OrderLifecycleTest](OrderLifecycleTest.java) · `cancelRequiresOrder`, `cancelRequiresTimeBeforeCheckingState`, `cancelChecksForbiddenStateBeforeEarlyTime`, `cancelRejectsTimeBeforeCreation` | Thiếu tham số trước trạng thái cấm, trạng thái cấm trước thời điểm quá sớm. |
| [49](../../../docs/spec/branches.md#L49) | [cancelOrder · bước 4](../../../docs/spec/order.md) | [OrderLifecycleTest](OrderLifecycleTest.java) · `refundAtCreationAndAround24Hours`, `paidOneNanosecondPastDeadlineGetsGoodsOnly`, `lateRefundUsesLongArithmetic` | PENDING/PAID ở 0, 86400, 86401 giây. Thêm biên nano giây và tiền hoàn vượt int. |
| [50](../../../docs/spec/branches.md#L50) | [cancelOrder · bước 5–6](../../../docs/spec/order.md) | [OrderLifecycleTest](OrderLifecycleTest.java) · `repeatedCancellationUsesOriginalProductsAndRestoresOnce` | Nhập thêm cả hai kho, thay đối tượng A, xóa B. Hoàn đúng vào hai đối tượng gốc, hủy lần hai giữ nguyên. |

Bảy yêu cầu tại [thiết kế §3.7](../../../docs/thiet-ke-ky-thuat.md) đối chiếu lần lượt với dòng 44 (tiền sau giảm), 28 (trần và minOrder), 27–28 (ngày hết hạn), 30–32 (gam), 49 (24 giờ), 43 (lỗi vận chuyển không trừ kho) và 18–19 (thứ tự kiểm tra giỏ).

## Kiểm tra biên dịch

Chạy PowerShell từ gốc worktree với JDK 17 và các JAR trong Maven repository do `MAVEN_OPTS` chỉ định. Thư mục `javac-t05-check` dùng tạm cho kết quả biên dịch.

```powershell
$ErrorActionPreference = 'Stop'
$repo = $env:MAVEN_OPTS -replace '^-Dmaven.repo.local=', ''
$jars = @(
    'org/junit/jupiter/junit-jupiter-api/5.13.4/junit-jupiter-api-5.13.4.jar'
    'org/junit/jupiter/junit-jupiter-params/5.13.4/junit-jupiter-params-5.13.4.jar'
    'org/junit/platform/junit-platform-commons/1.13.4/junit-platform-commons-1.13.4.jar'
    'org/apiguardian/apiguardian-api/1.1.2/apiguardian-api-1.1.2.jar'
    'org/opentest4j/opentest4j/1.3.0/opentest4j-1.3.0.jar'
)
$cp = ($jars | ForEach-Object { (Get-Item -LiteralPath (Join-Path $repo $_)).FullName }) -join [IO.Path]::PathSeparator
$stubs = (Get-ChildItem -Path subject/src/main/java/shop/*.java).FullName
$tests = (Get-ChildItem -Path test-suites/p1-manual/run-01/*Test.java).FullName
$suite = (Resolve-Path -LiteralPath 'test-suites/p1-manual/run-01').Path
$out = Join-Path $suite 'javac-t05-check'
New-Item -ItemType Directory -Path $out -ErrorAction Stop | Out-Null
javac --release 17 -encoding UTF-8 -cp $cp -d $out @stubs @tests
$code = $LASTEXITCODE
Write-Output "javac exit $code ($($stubs.Count) stub sources, $($tests.Count) test sources)"
exit $code
```

`javac` không in chẩn đoán. Dòng kết quả do lệnh PowerShell phía sau ghi ra, mã thoát của tiến trình là 0.

```text
javac exit 0 (14 stub sources, 7 test sources)
```

Đã dọn các tệp `.class` tạm sau kiểm tra. Bước T05 chỉ xác minh biên dịch với khung M1. Cả 181 ca chờ chạy trên bản cài đặt.

<details>
<summary>Lệnh PowerShell đếm ca từ mã test</summary>

```powershell
$suite = 'test-suites/p1-manual/run-01'
$plainTotal = 0
$dataTotal = 0
$methods = 0
Get-ChildItem -Path "$suite/*Test.java" | ForEach-Object {
    $source = Get-Content -Raw -Encoding UTF8 -LiteralPath $_.FullName
    $plain = [regex]::Matches($source, '(?m)^\s*@Test\s*$').Count
    $data = 0
    $tables = [regex]::Matches($source, '(?s)@(?:ValueSource|CsvSource)\([^{}]*\{([^{}]*)\}')
    foreach ($table in $tables) {
        $entries = [regex]::Matches($table.Groups[1].Value, '"[^"]*"|-?\d+|true|false')
        $data += $entries.Count
    }
    $plainTotal += $plain
    $dataTotal += $data
    $methods += $tables.Count
    '{0}: {1} @Test + {2} cases ({3} parameterized methods) = {4}' -f $_.BaseName,$plain,$data,$tables.Count,($plain+$data)
}
"Total: $plainTotal @Test + $dataTotal cases ($methods parameterized methods) = $($plainTotal+$dataTotal)"
```

```text
Total: 54 @Test + 127 cases (30 parameterized methods) = 181
```

</details>
