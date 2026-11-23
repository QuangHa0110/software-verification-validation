package shop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static shop.CartTest.assertPair;
import static shop.DomainTest.DAY;
import static shop.DomainTest.expectError;
import static shop.OrderTest.PLACED;
import static shop.OrderTest.assertHeader;

class PlaceOrderTest {
    private final Product a = new Product("A", "A", 150_000, 300, 4);
    private final Product b = new Product("B", "B", 200_000, 400, 3);
    private final Cart cart = new Cart();
    private final Coupon oneOff = new Coupon("F", CouponType.FIXED, 1, 500_000, 0, DAY);
    private final List<String> calls = new ArrayList<>();

    private void fill() {
        cart.addItem(a, 2);
        cart.addItem(b, 1);
    }

    private void checkCartAndStock(int stockA, int stockB) {
        // Đặt đơn giữ nguyên giỏ [A × 2, B × 1], dù kết quả thành công hay lỗi.
        assertPair(cart.getItems(), a, 2, b, 1);
        // Đối chiếu tồn từng sản phẩm với kết quả tính tay của ca gọi.
        assertAll(() -> assertEquals(stockA, a.getStock()), () -> assertEquals(stockB, b.getStock()));
    }

    private OrderService recordingService(RuntimeException priceError, RuntimeException shippingError) {
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("giảm");
                // 150000 × 2 + 200000 = 500000; chuyển đúng mã và ngày của thời điểm đặt.
                assertAll(() -> assertEquals(500_000L, subtotal), () -> assertSame(oneOff, coupon),
                        () -> assertEquals(DAY, today));
                if (priceError != null) {
                    throw priceError;
                }
                return 7;
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                calls.add("phí");
                // 300 × 2 + 400 = 1000 g; dùng 500000 - 7 = 499993 VND sau bộ giảm được tiêm.
                assertAll(() -> assertSame(Region.REMOTE, region), () -> assertEquals(1000, grams),
                        () -> assertEquals(499_993L, goods));
                if (shippingError != null) {
                    throw shippingError;
                }
                return 11;
            }
        };
        return new OrderService(pricing, shipping);
    }

    @Test
    void serviceRequiresPricing() {
        ShippingCalculator shipping = new ShippingCalculator();
        // Thiếu bộ giảm, dù bộ tính phí hợp lệ.
        expectError(IllegalArgumentException.class, () -> new OrderService(null, shipping));
    }

    @Test
    void serviceRequiresShipping() {
        PricingService pricing = new PricingService();
        // Thiếu bộ tính phí, dù bộ giảm hợp lệ.
        expectError(IllegalArgumentException.class, () -> new OrderService(pricing, null));
    }

    @Test
    void injectedServicesReceiveArgumentsOnceInOrder() {
        fill();
        OrderService service = recordingService(null, null);
        // Constructor chỉ giữ phụ thuộc, số lần tính là 0.
        assertEquals(List.of(), calls);
        Order order = service.placeOrder(" D ", cart, oneOff, Region.REMOTE, PLACED);
        // Mỗi bộ tính được gọi đúng một lần, giảm trước phí.
        assertEquals(List.of("giảm", "phí"), calls);
        // 500000 - 7 + 11 = 500004 VND.
        assertHeader(order, " D ", PLACED, Region.REMOTE, OrderStatus.PENDING, 500_000, 7, 11, 500_004);
        assertPair(order.getItems(), a, 2, b, 1);
        // A còn 4 - 2 = 2, B còn 3 - 1 = 2.
        checkCartAndStock(2, 2);
    }

    @ParameterizedTest
    @CsvSource(value = {"NULL,true,true,true", "'',true,true,false", "'  ',true,true,true",
                        "D,false,true,true", "D,false,true,false", "D,true,false,true", "D,true,false,false"},
               nullValues = "NULL")
    void invalidHeaderPrecedesNullOrEmptyCart(String id, boolean hasRegion, boolean hasTime, boolean nullCart) {
        OrderService service = recordingService(null, null);
        Region region = hasRegion ? Region.REMOTE : null;
        LocalDateTime time = hasTime ? PLACED : null;
        Cart input = nullCart ? null : cart;
        // ID rỗng, thiếu vùng hoặc thiếu thời điểm đều dừng ở bước 1, dù giỏ cũng sai.
        expectError(IllegalArgumentException.class, () -> service.placeOrder(id, input, oneOff, region, time));
        // Giỏ rỗng vẫn có 0 dòng và không bộ tính nào được gọi.
        assertAll(() -> assertEquals(List.of(), cart.getItems()), () -> assertEquals(List.of(), calls));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void validHeaderRequiresPopulatedCart(boolean nullCart) {
        OrderService service = recordingService(null, null);
        Cart input = nullCart ? null : cart;
        // Đầu đơn hợp lệ, giỏ null hoặc không có dòng dừng ở bước 2.
        expectError(IllegalStateException.class, () -> service.placeOrder("D", input, oneOff, Region.REMOTE, PLACED));
        // Không tính giảm hay phí và không ghi vào giỏ.
        assertAll(() -> assertEquals(List.of(), calls), () -> assertEquals(List.of(), cart.getItems()));
    }

    @Test
    void stockOfLaterLineCheckedBeforeExpiredCoupon() {
        fill();
        b.reduceStock(3);
        OrderService service = new OrderService();
        Coupon expired = new Coupon("E", CouponType.FIXED, 1, 0, 0, DAY.minusDays(1));
        // A đủ 2, B cần 1 nhưng còn 0. Thiếu kho dừng trước mã đã hết hạn.
        expectError(InsufficientStockException.class,
                () -> service.placeOrder("D", cart, expired, Region.INNER_CITY, PLACED));
        // A chưa bị trừ, B vẫn 3 - 3 = 0.
        checkCartAndStock(4, 0);
    }

    @Test
    void expiredCouponStopsOrder() {
        fill();
        OrderService service = new OrderService();
        Coupon expired = new Coupon("E", CouponType.PERCENT, 15, 0, 0, DAY.minusDays(1));
        // Tồn đủ nhưng đã qua hạn 1 ngày.
        expectError(CouponExpiredException.class, () -> service.placeOrder("D", cart, expired, Region.SUBURB, PLACED));
        checkCartAndStock(4, 3);
    }

    @Test
    void minimumShortfallStopsOrder() {
        fill();
        OrderService service = new OrderService();
        Coupon costly = new Coupon("M", CouponType.FIXED, 1, 500_001, 0, DAY);
        // 500000 < 500001 đúng 1 VND, ngày bằng hạn vẫn hợp lệ.
        expectError(CouponNotApplicableException.class, () -> service.placeOrder("D", cart, costly, Region.SUBURB, PLACED));
        checkCartAndStock(4, 3);
    }

    @Test
    void injectedPriceFailureStopsBeforeShipping() {
        fill();
        ArithmeticException failure = new ArithmeticException();
        OrderService service = recordingService(failure, null);
        // Lỗi gốc ngay lần tính giảm đầu tiên phải tới người gọi.
        var thrown = expectError(ArithmeticException.class,
                () -> service.placeOrder("D", cart, oneOff, Region.REMOTE, PLACED));
        // Cùng đối tượng lỗi, chưa gọi tính phí và chưa trừ sản phẩm nào.
        assertAll(() -> assertSame(failure, thrown), () -> assertEquals(List.of("giảm"), calls));
        checkCartAndStock(4, 3);
    }

    @Test
    void injectedShippingFailureLeavesAllLinesUntouched() {
        fill();
        ArithmeticException failure = new ArithmeticException();
        OrderService service = recordingService(null, failure);
        // Tính giảm được 7, đến bộ tính phí thì nhận lỗi gốc.
        var thrown = expectError(ArithmeticException.class,
                () -> service.placeOrder("D", cart, oneOff, Region.REMOTE, PLACED));
        // Cùng đối tượng lỗi, hai lần gọi theo thứ tự, không ghi kho.
        assertAll(() -> assertSame(failure, thrown), () -> assertEquals(List.of("giảm", "phí"), calls));
        checkCartAndStock(4, 3);
    }

    @Test
    void overweightAfterCheckingBothStocksIsAtomic() {
        Product first = new Product("H1", "H1", 250_000, 10_000, 1);
        Product second = new Product("H2", "H2", 250_000, 10_001, 2);
        cart.addItem(first, 1);
        cart.addItem(second, 1);
        OrderService service = new OrderService();
        // 10000 + 10001 = 20001 g. Đủ tồn, đủ 500000 nhưng quá nặng, lỗi ở vận chuyển.
        expectError(IllegalArgumentException.class,
                () -> service.placeOrder("D", cart, null, Region.INNER_CITY, PLACED));
        // Cả hai dòng nguyên vẹn; chưa trừ 1 ở dòng đầu hay dòng sau.
        assertPair(cart.getItems(), first, 1, second, 1);
        assertAll(() -> assertEquals(1, first.getStock()), () -> assertEquals(2, second.getStock()));
    }

    @ParameterizedTest
    @CsvSource({"true,1,15000,514999", "false,0,0,500000"})
    void freeShippingUsesDiscountedGoods(boolean hasCoupon, long discount, long fee, long total) {
        fill();
        a.reduceStock(2);
        Order order = new OrderService().placeOrder("D", cart, hasCoupon ? oneOff : null, Region.INNER_CITY, PLACED);
        // 500000 - 1 = 499999 → cộng 15000 = 514999; 500000 - 0 → miễn phí = 500000.
        assertHeader(order, "D", PLACED, Region.INNER_CITY, OrderStatus.PENDING, 500_000, discount, fee, total);
        assertPair(order.getItems(), a, 2, b, 1);
        // A vừa đủ: 2 - 2 = 0, B còn 3 - 1 = 2.
        checkCartAndStock(0, 2);
    }

    @Test
    void repeatIdCreatesIndependentOrdersAndSnapshots() {
        fill();
        OrderService service = new OrderService();
        Order first = service.placeOrder("D", cart, oneOff, Region.INNER_CITY, PLACED);
        // Sau đơn đầu, A còn 4 - 2 = 2 và B còn 3 - 1 = 2.
        checkCartAndStock(2, 2);
        Order second = service.placeOrder("D", cart, oneOff, Region.SUBURB, PLACED.plusMinutes(1));
        // Sau hai đơn cùng ID, A còn 2 - 2 = 0 và B còn 2 - 1 = 1.
        checkCartAndStock(0, 1);
        service.pay(second);
        Product replacement = new Product("A", "replacement", 5, 7, 3);
        cart.addItem(replacement, 1);
        cart.removeItem("B");
        // Giỏ hiện tại chỉ còn A mới × 3.
        assertEquals(1, cart.getItems().size());
        CartTest.assertLine(cart.getItems().get(0), replacement, 3);
        // Đơn đầu vẫn PENDING giá 499999 + 15000 = 514999; đơn hai PAID giá 499999 + 25000 = 524999.
        assertNotSame(first, second);
        assertHeader(first, "D", PLACED, Region.INNER_CITY, OrderStatus.PENDING, 500_000, 1, 15_000, 514_999);
        assertHeader(second, "D", PLACED.plusMinutes(1), Region.SUBURB, OrderStatus.PAID, 500_000, 1, 25_000, 524_999);
        assertPair(first.getItems(), a, 2, b, 1);
        assertPair(second.getItems(), a, 2, b, 1);
    }
}
