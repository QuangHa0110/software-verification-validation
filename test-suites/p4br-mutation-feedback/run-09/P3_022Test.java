package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @Test
    void injectedServicesRunOnceInOrderWithCartTotalsDateAndDiscountedValue() {
        Product a = new Product("A", "A", 30, 20, 2);
        Product b = new Product("B", "B", 40, 7, 3);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999999999);
        Coupon supplied = new Coupon(" X ", CouponType.FIXED, 3, 1, 2, time.toLocalDate());
        List<String> calls = new ArrayList<>();
        PricingService pricing = new PricingService() {
            @Override
            public long calculateDiscount(long subtotal, Coupon coupon, LocalDate today) {
                calls.add("discount");
                assertEquals(100L, subtotal);
                assertEquals(time.toLocalDate(), today);
                assertEquals(" X ", coupon.getCode());
                assertEquals(CouponType.FIXED, coupon.getType());
                assertEquals(3L, coupon.getValue());
                assertEquals(1L, coupon.getMinOrder());
                assertEquals(2L, coupon.getMaxDiscount());
                assertEquals(today, coupon.getExpiry());
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                return 7;
            }
        };
        ShippingCalculator shipping = new ShippingCalculator() {
            @Override
            public long calculate(Region region, int weightGrams, long orderValue) {
                calls.add("shipping");
                assertEquals(Region.PROVINCE, region);
                assertEquals(47, weightGrams);
                assertEquals(93L, orderValue);
                assertEquals(2, a.getStock());
                assertEquals(3, b.getStock());
                return 11;
            }
        };
        OrderService service = new OrderService(pricing, shipping);
        assertTrue(calls.isEmpty());
        Order order = service.placeOrder(" D ", cart, supplied, Region.PROVINCE, time);
        assertEquals(List.of("discount", "shipping"), calls);
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
        assertEquals(2, b.getStock());
    }
}
