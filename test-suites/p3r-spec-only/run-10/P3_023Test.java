package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @ParameterizedTest
    @ValueSource(strings = {"success", "pricing", "shipping"})
    void injectedServicesRunOnceInOrderBeforeEitherStockIsReduced(String outcome) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 0, 0, 0, 1);
        Coupon supplied = new Coupon(" C ", CouponType.FIXED, 2, 0, 0, t.toLocalDate());
        Product a = new Product("A", "first", 40, 3, 2);
        Product b = new Product("B", "second", 20, 5, 3);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        List<String> calls = new ArrayList<>();
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("pricing");
                assertEquals(100L, subtotal);
                assertSame(supplied, coupon);
                assertEquals(t.toLocalDate(), today);
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                if (outcome.equals("pricing")) {
                    throw new ArithmeticException();
                }
                return 7;
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                calls.add("shipping");
                assertEquals(Region.PROVINCE, region);
                assertEquals(11, weight);
                assertEquals(93L, goods);
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                if (outcome.equals("shipping")) {
                    throw new UnsupportedOperationException();
                }
                return 11;
            }
        };
        OrderService service = new OrderService(pricing, shipping);
        assertTrue(calls.isEmpty());
        if (outcome.equals("success")) {
            Order order = service.placeOrder(" D ", cart, supplied, Region.PROVINCE, t);
            assertEquals(" D ", order.getId());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.PROVINCE, order.getRegion());
            assertEquals(OrderStatus.PENDING, order.getStatus());
            assertEquals(100L, order.getSubtotal());
            assertEquals(7L, order.getDiscount());
            assertEquals(11L, order.getShipping());
            assertEquals(104L, order.getTotal());
            assertEquals(0, a.getStock());
            assertEquals(2, b.getStock());
        } else {
            Class<? extends RuntimeException> expected = outcome.equals("pricing")
                    ? ArithmeticException.class : UnsupportedOperationException.class;
            assertThrows(expected, () -> service.placeOrder(" D ", cart, supplied, Region.PROVINCE, t));
            assertEquals(2, a.getStock());
            assertEquals(3, b.getStock());
        }
        assertEquals(outcome.equals("pricing") ? List.of("pricing") : List.of("pricing", "shipping"), calls);
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(1, lines.get(1).getQuantity());
    }
}
