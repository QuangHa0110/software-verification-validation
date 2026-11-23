package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @Test
    void injectedCalculatorsReceiveExactInputsInOrderOnEverySuccessfulPlacement() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 987654321);
        Product a = new Product("A", "A", 30, 3, 4);
        Product b = new Product("B", "B", 40, 5, 2);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 0, 0, time.toLocalDate());
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        List<String> calls = new ArrayList<>();
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon received, LocalDate day) {
                calls.add("pricing");
                assertEquals(100L, subtotal);
                assertEquals(coupon, received);
                assertEquals(LocalDate.of(2026, 11, 23), day);
                return 7L;
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                calls.add("shipping");
                assertEquals(Region.PROVINCE, region);
                assertEquals(11, grams);
                assertEquals(93L, goods);
                return 11L;
            }
        };
        OrderService service = new OrderService(pricing, shipping);
        assertTrue(calls.isEmpty());
        Order first = service.placeOrder(" D ", cart, coupon, Region.PROVINCE, time);
        assertEquals(List.of("pricing", "shipping"), calls);
        assertEquals(2, a.getStock());
        assertEquals(1, b.getStock());
        service.pay(first);
        Order second = service.placeOrder(" D ", cart, coupon, Region.PROVINCE, time);
        assertEquals(List.of("pricing", "shipping", "pricing", "shipping"), calls);
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.PENDING, second.getStatus());
        for (Order order : List.of(first, second)) {
            assertEquals(" D ", order.getId());
            assertEquals(time, order.getCreatedAt());
            assertEquals(Region.PROVINCE, order.getRegion());
            assertEquals(100L, order.getSubtotal());
            assertEquals(7L, order.getDiscount());
            assertEquals(11L, order.getShipping());
            assertEquals(104L, order.getTotal());
        }
        for (List<CartItem> lines : List.of(cart.getItems(), first.getItems(), second.getItems())) {
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
