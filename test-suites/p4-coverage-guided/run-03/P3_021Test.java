package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @ParameterizedTest
    @CsvSource({"false,0,0,500000", "true,1,15000,514999"})
    void defaultOrderAppliesFreeShippingThresholdToDiscountedGoods(
            boolean useCoupon, long discount, long shipping, long total) {
        Product a = new Product("A", "A", 250_000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999_999_999);
        Coupon coupon = useCoupon ? new Coupon("F", CouponType.FIXED, 1, 500_000, 0, t.toLocalDate()) : null;
        Order order = new OrderService().placeOrder(" D1 ", cart, coupon, Region.INNER_CITY, t);
        assertEquals(" D1 ", order.getId());
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(discount, order.getDiscount());
        assertEquals(shipping, order.getShipping());
        assertEquals(total, order.getTotal());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(t, order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(0, a.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(a, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        cart.updateQuantity("A", 0);
        assertTrue(cart.isEmpty());
        assertEquals(1, order.getItems().size());
        assertSame(a, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
