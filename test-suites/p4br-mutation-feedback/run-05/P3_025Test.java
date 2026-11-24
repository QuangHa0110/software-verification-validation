package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void placementUsesInjectedCalculatorsExactlyOnceInOrderWithOriginalStockAndCorrectInputs() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 987_654_321);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        Product a = new Product("A", "A", 30, 12, 2);
        Product b = new Product("B", "B", 40, 5, 3);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        List<String> calls = new ArrayList<>();
        OrderService service = new OrderService(new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon supplied, LocalDate today) {
                calls.add("discount");
                assertEquals(100L, subtotal);
                assertEquals(coupon, supplied);
                assertEquals(LocalDate.of(2026, 11, 23), today);
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                return 7;
            }
        }, new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                calls.add("shipping");
                assertEquals(Region.PROVINCE, region);
                assertEquals(29, weightGrams);
                assertEquals(93L, orderValue);
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                return 11;
            }
        });
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
        assertEquals(0, a.getStock());
        assertEquals(2, b.getStock());
        for (List<CartItem> rows : List.of(cart.getItems(), order.getItems())) {
            assertEquals(2, rows.size());
            assertSame(a, rows.get(0).getProduct());
            assertEquals(2, rows.get(0).getQuantity());
            assertSame(b, rows.get(1).getProduct());
            assertEquals(1, rows.get(1).getQuantity());
        }
    }
}
