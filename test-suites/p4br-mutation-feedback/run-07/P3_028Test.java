package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @ParameterizedTest
    @ValueSource(strings = {"success", "pricingFailure", "shippingFailure"})
    void injectedServicesReceiveExactArgumentsInOrderBeforeAnyStockIsDeducted(String scenario) {
        Product a = new Product("A", "First", 40, 60, 2);
        Product b = new Product("B", "Second", 20, 5, 3);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 123_456_789);
        Coupon offered = new Coupon("F", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        List<String> calls = new ArrayList<>();

        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("discount");
                assertEquals(100L, subtotal);
                assertTrue(coupon == offered);
                assertEquals(LocalDate.of(2026, 11, 23), today);
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                if (scenario.equals("pricingFailure")) {
                    throw new ArithmeticException();
                }
                return 7;
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                calls.add("shipping");
                assertEquals(Region.PROVINCE, region);
                assertEquals(125, grams);
                assertEquals(93L, goods);
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                if (scenario.equals("shippingFailure")) {
                    throw new UnsupportedOperationException();
                }
                return 11;
            }
        };
        OrderService service = new OrderService(pricing, shipping);
        assertTrue(calls.isEmpty());
        if (scenario.equals("success")) {
            Order order = service.placeOrder(" D ", cart, offered, Region.PROVINCE, t);
            assertEquals(" D ", order.getId());
            assertEquals(100L, order.getSubtotal());
            assertEquals(7L, order.getDiscount());
            assertEquals(11L, order.getShipping());
            assertEquals(104L, order.getTotal());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.PROVINCE, order.getRegion());
            assertEquals(OrderStatus.PENDING, order.getStatus());
            assertEquals(0, a.getStock());
            assertEquals(2, b.getStock());
        } else {
            Class<? extends RuntimeException> expected = scenario.equals("pricingFailure")
                ? ArithmeticException.class : UnsupportedOperationException.class;
            assertThrows(expected, () -> service.placeOrder(" D ", cart, offered, Region.PROVINCE, t));
            assertEquals(2, a.getStock());
            assertEquals(3, b.getStock());
        }
        assertEquals(scenario.equals("pricingFailure") ? List.of("discount") : List.of("discount", "shipping"), calls);
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(1, lines.get(1).getQuantity());
    }
}
