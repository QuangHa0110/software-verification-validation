package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void injectedServicesReceiveExactArgumentsOnceInOrderBeforeStockChanges() {
        Product a = new Product("A", "First", 40, 8, 2);
        Product b = new Product("B", "Second", 20, 3, 4);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999_999_999);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 2, 0, 0, time.toLocalDate());
        List<String> calls = new ArrayList<>();
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon supplied, LocalDate date) {
                assertTrue(calls.isEmpty());
                assertEquals(100L, subtotal);
                assertSame(coupon, supplied);
                assertEquals(LocalDate.of(2026, 11, 23), date);
                assertEquals(2, a.getStock());
                assertEquals(4, b.getStock());
                calls.add("pricing");
                return 7;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                assertEquals(List.of("pricing"), calls);
                assertEquals(Region.REMOTE, region);
                assertEquals(19, weight);
                assertEquals(93L, goods);
                assertEquals(2, a.getStock());
                assertEquals(4, b.getStock());
                calls.add("shipping");
                return 11;
            }
        });
        assertTrue(calls.isEmpty());
        Order order = service.placeOrder(" D ", cart, coupon, Region.REMOTE, time);
        assertEquals(List.of("pricing", "shipping"), calls);
        assertEquals(" D ", order.getId());
        assertEquals(time, order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(100L, order.getSubtotal());
        assertEquals(7L, order.getDiscount());
        assertEquals(11L, order.getShipping());
        assertEquals(104L, order.getTotal());
        assertEquals(0, a.getStock());
        assertEquals(3, b.getStock());
        for (List<CartItem> lines : List.of(cart.getItems(), order.getItems())) {
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
        }
    }
}
