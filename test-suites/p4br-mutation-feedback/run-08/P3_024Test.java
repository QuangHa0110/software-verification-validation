package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void injectedServicesReceiveExactInputsInOrderOncePerPlacement() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999_999_999);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        Cart cart = new Cart();
        Product a = new Product("A", "First", 40, 17, 4);
        Product b = new Product("B", "Second", 20, 3, 2);
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        List<String> calls = new ArrayList<>();
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon input, LocalDate day) {
                calls.add("pricing");
                assertEquals(100L, subtotal);
                assertTrue(input == coupon);
                assertEquals(t.toLocalDate(), day);
                return 7;
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                calls.add("shipping");
                assertEquals(Region.PROVINCE, region);
                assertEquals(37, grams);
                assertEquals(93L, goods);
                return 11;
            }
        };
        OrderService service = new OrderService(pricing, shipping);
        assertTrue(calls.isEmpty());
        Order first = service.placeOrder(" D ", cart, coupon, Region.PROVINCE, t);
        assertEquals(List.of("pricing", "shipping"), calls);
        assertEquals(2, a.getStock());
        assertEquals(1, b.getStock());
        Order second = service.placeOrder(" D ", cart, coupon, Region.PROVINCE, t);
        assertEquals(List.of("pricing", "shipping", "pricing", "shipping"), calls);
        assertNotSame(first, second);
        for (Order order : List.of(first, second)) {
            assertEquals(" D ", order.getId());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.PROVINCE, order.getRegion());
            assertEquals(OrderStatus.PENDING, order.getStatus());
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
        service.pay(first);
        assertEquals(OrderStatus.PENDING, second.getStatus());
        assertEquals(0, a.getStock());
        assertEquals(0, b.getStock());
    }
}
