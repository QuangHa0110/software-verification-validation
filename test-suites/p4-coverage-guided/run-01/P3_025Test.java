package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @ParameterizedTest
    @CsvSource({"true,1,15000,514999", "false,0,0,500000"})
    void checkoutTestsFreeShippingThresholdAfterDiscount(
            boolean useCoupon, long discount, long shipping, long total) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product p = new Product("P", "P", 250_000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Coupon coupon = useCoupon ? new Coupon("F", CouponType.FIXED, 1, 500_000, 0, t.toLocalDate()) : null;
        Order order = new OrderService().placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(discount, order.getDiscount());
        assertEquals(shipping, order.getShipping());
        assertEquals(total, order.getTotal());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(0, p.getStock());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        cart.updateQuantity("P", 0);
        assertTrue(cart.isEmpty());
        assertSame(p, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
