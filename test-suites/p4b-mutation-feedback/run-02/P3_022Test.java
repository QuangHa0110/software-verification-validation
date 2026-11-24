package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @Test
    void injectedServicesReceiveExactInputsInOrderBeforeAnyStockIsDeducted() {
        Product a = new Product("A", "A", 40, 200, 2);
        Product b = new Product("B", "B", 20, 101, 1);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 123_456_789);
        Coupon supplied = new Coupon(" C ", CouponType.FIXED, 1, 0, 0, time.toLocalDate());
        List<String> calls = new ArrayList<>();
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("pricing");
                assertEquals(100L, subtotal);
                assertSame(supplied, coupon);
                assertEquals(LocalDate.of(2026, 11, 23), today);
                assertEquals(2, a.getStock());
                assertEquals(1, b.getStock());
                return 7;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                calls.add("shipping");
                assertEquals(Region.PROVINCE, region);
                assertEquals(501, weight);
                assertEquals(93L, goods);
                assertEquals(2, a.getStock());
                assertEquals(1, b.getStock());
                return 11;
            }
        });
        assertTrue(calls.isEmpty());

        Order order = service.placeOrder(" D ", cart, supplied, Region.PROVINCE, time);
        assertEquals(List.of("pricing", "shipping"), calls);
        assertEquals(" D ", order.getId());
        assertEquals(time, order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(100L, order.getSubtotal());
        assertEquals(7L, order.getDiscount());
        assertEquals(11L, order.getShipping());
        assertEquals(104L, order.getTotal());
        for (List<CartItem> lines : List.of(cart.getItems(), order.getItems())) {
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
        }
        assertEquals(0, a.getStock());
        assertEquals(0, b.getStock());
    }
}
