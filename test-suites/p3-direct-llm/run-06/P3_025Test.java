package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void injectedCalculatorsReceiveExactInputsInOrderBeforeAnyStockDeduction() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 876543210);
        Coupon coupon = new Coupon(" C ", CouponType.FIXED, 17, 5, 1, time.toLocalDate());
        Product a = new Product("A", "A", 35, 4, 2);
        Product b = new Product("B", "B", 30, 5, 3);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        List<String> calls = new ArrayList<>();

        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon supplied, LocalDate today) {
                calls.add("pricing");
                assertEquals(100L, subtotal);
                assertSame(coupon, supplied);
                assertEquals(LocalDate.of(2026, 11, 23), today);
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                return 7;
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long goods) {
                calls.add("shipping");
                assertEquals(Region.REMOTE, region);
                assertEquals(13, weightGrams);
                assertEquals(93L, goods);
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                return 11;
            }
        };
        OrderService service = new OrderService(pricing, shipping);
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
        assertEquals(2, b.getStock());
        for (List<CartItem> lines : List.of(cart.getItems(), order.getItems())) {
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
        }
    }
}
