package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void injectedServicesReceiveExactArgumentsOnceInOrderBeforeStockIsDeducted() {
        Product a = new Product("A", "A", 40, 12, 2);
        Product b = new Product("B", "B", 20, 7, 4);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999999999);
        Coupon coupon = new Coupon("X", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        List<String> calls = new ArrayList<>();
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon supplied, LocalDate today) {
                calls.add("pricing");
                assertEquals(100L, subtotal);
                assertSame(coupon, supplied);
                assertEquals(LocalDate.of(2026, 11, 23), today);
                assertEquals(2, a.getStock());
                assertEquals(4, b.getStock());
                return 7L;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                calls.add("shipping");
                assertEquals(Region.PROVINCE, region);
                assertEquals(31, weightGrams);
                assertEquals(93L, orderValue);
                assertEquals(2, a.getStock());
                assertEquals(4, b.getStock());
                return 11L;
            }
        });
        assertTrue(calls.isEmpty());
        Order order = service.placeOrder(" D ", cart, coupon, Region.PROVINCE, t);
        assertEquals(List.of("pricing", "shipping"), calls);
        assertEquals(" D ", order.getId());
        assertEquals(100L, order.getSubtotal());
        assertEquals(7L, order.getDiscount());
        assertEquals(11L, order.getShipping());
        assertEquals(104L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        for (List<CartItem> lines : List.of(cart.getItems(), order.getItems())) {
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
        }
        assertEquals(0, a.getStock());
        assertEquals(3, b.getStock());
    }
}
