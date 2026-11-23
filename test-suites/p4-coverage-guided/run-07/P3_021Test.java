package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void injectedCalculatorsReceiveExactArgumentsOnceInOrderBeforeInventoryChanges() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 123456789);
        Product a = new Product("A", "First", 30, 15, 2);
        Product b = new Product("B", "Second", 40, 20, 3);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        Coupon supplied = new Coupon("C", CouponType.FIXED, 1, 0, 0, time.toLocalDate());
        List<String> calls = new ArrayList<>();
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("pricing");
                assertEquals(100L, subtotal);
                assertEquals(supplied, coupon);
                assertEquals(LocalDate.of(2026, 11, 23), today);
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                return 7L;
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weight, long goods) {
                calls.add("shipping");
                assertEquals(Region.REMOTE, region);
                assertEquals(50, weight);
                assertEquals(93L, goods);
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                return 11L;
            }
        };
        OrderService service = new OrderService(pricing, shipping);
        assertTrue(calls.isEmpty());
        Order order = service.placeOrder(" D ", cart, supplied, Region.REMOTE, time);
        assertEquals(List.of("pricing", "shipping"), calls);
        assertEquals(" D ", order.getId());
        assertEquals(100L, order.getSubtotal());
        assertEquals(7L, order.getDiscount());
        assertEquals(11L, order.getShipping());
        assertEquals(104L, order.getTotal());
        assertEquals(time, order.getCreatedAt());
        assertEquals(Region.REMOTE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(0, a.getStock());
        assertEquals(2, b.getStock());
        for (List<CartItem> items : List.of(cart.getItems(), order.getItems())) {
            assertEquals(2, items.size());
            assertSame(a, items.get(0).getProduct());
            assertEquals(2, items.get(0).getQuantity());
            assertSame(b, items.get(1).getProduct());
            assertEquals(1, items.get(1).getQuantity());
        }
    }
}
