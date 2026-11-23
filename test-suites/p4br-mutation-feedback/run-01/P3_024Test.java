package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void defaultOrderServiceUsesDiscountedGoodsForFreeShipping(boolean useCoupon) {
        Product p = new Product("A", "A", 250000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 123456789);
        Coupon coupon = useCoupon ? new Coupon("F", CouponType.FIXED, 1, 500000, 0, t.toLocalDate()) : null;
        Order order = new OrderService().placeOrder(" D1 ", cart, coupon, Region.INNER_CITY, t);
        assertEquals(" D1 ", order.getId());
        assertEquals(500000L, order.getSubtotal());
        assertEquals(useCoupon ? 1L : 0L, order.getDiscount());
        assertEquals(useCoupon ? 15000L : 0L, order.getShipping());
        assertEquals(useCoupon ? 514999L : 500000L, order.getTotal());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(t, order.getCreatedAt());
        assertEquals(0, p.getStock());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        cart.removeItem("A");
        assertEquals(1, order.getItems().size());
        assertSame(p, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
