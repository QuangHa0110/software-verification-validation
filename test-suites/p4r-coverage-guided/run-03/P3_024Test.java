package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void injectedCalculatorsReceiveExactArgumentsOnceInOrderBeforeStockChanges() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 987_654_321);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 42, 0, 0, t.toLocalDate());
        Product a = new Product("A", "First", 40, 3, 4);
        Product b = new Product("B", "Second", 20, 7, 1);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        List<String> calls = new ArrayList<>();

        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon received, LocalDate today) {
                calls.add("discount");
                assertEquals(100L, subtotal);
                assertTrue(received == coupon);
                assertEquals(t.toLocalDate(), today);
                assertEquals(4, a.getStock());
                assertEquals(1, b.getStock());
                return 7;
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                calls.add("shipping");
                assertEquals(Region.PROVINCE, region);
                assertEquals(13, grams);
                assertEquals(93L, goods);
                assertEquals(4, a.getStock());
                assertEquals(1, b.getStock());
                return 11;
            }
        };
        OrderService service = new OrderService(pricing, shipping);
        assertTrue(calls.isEmpty());
        Order order = service.placeOrder(" D ", cart, coupon, Region.PROVINCE, t);
        assertEquals(List.of("discount", "shipping"), calls);
        assertEquals(" D ", order.getId());
        assertEquals(t, order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(100L, order.getSubtotal());
        assertEquals(7L, order.getDiscount());
        assertEquals(11L, order.getShipping());
        assertEquals(104L, order.getTotal());
        assertEquals(2, a.getStock());
        assertEquals(0, b.getStock());
        for (List<CartItem> lines : List.of(cart.getItems(), order.getItems())) {
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
        }
    }
}
