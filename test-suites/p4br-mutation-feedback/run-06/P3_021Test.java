package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void injectedServicesReceiveExactInputsInOrderOnEveryPlacement() {
        Product a = new Product("A", "A", 30, 100, 5);
        Product b = new Product("B", "B", 40, 7, 2);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 987_654_321);
        Coupon coupon = new Coupon(" C ", CouponType.PERCENT, 9, 0, 0, t.toLocalDate());
        List<String> calls = new ArrayList<>();
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon supplied, LocalDate day) {
                calls.add("pricing");
                assertEquals(100L, subtotal);
                assertTrue(supplied == coupon);
                assertEquals(t.toLocalDate(), day);
                return 7;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int grams, long goods) {
                calls.add("shipping");
                assertEquals(Region.SUBURB, region);
                assertEquals(207, grams);
                assertEquals(93L, goods);
                return 11;
            }
        });
        assertTrue(calls.isEmpty());

        Order first = service.placeOrder(" D ", cart, coupon, Region.SUBURB, t);
        assertEquals(List.of("pricing", "shipping"), calls);
        assertEquals(3, a.getStock());
        assertEquals(1, b.getStock());
        Order second = service.placeOrder(" D ", cart, coupon, Region.SUBURB, t);
        assertEquals(List.of("pricing", "shipping", "pricing", "shipping"), calls);
        assertEquals(1, a.getStock());
        assertEquals(0, b.getStock());

        for (Order order : List.of(first, second)) {
            assertEquals(" D ", order.getId());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.SUBURB, order.getRegion());
            assertEquals(100L, order.getSubtotal());
            assertEquals(7L, order.getDiscount());
            assertEquals(11L, order.getShipping());
            assertEquals(104L, order.getTotal());
            assertEquals(OrderStatus.PENDING, order.getStatus());
        }
        for (List<CartItem> lines : List.of(cart.getItems(), first.getItems(), second.getItems())) {
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
        }
        service.pay(first);
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.PENDING, second.getStatus());
    }
}
