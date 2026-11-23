package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void defaultOrderServiceDeterminesFreeShippingFromDiscountedGoods(boolean applyCoupon) {
        Product p = new Product("P", "P", 250_000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999_999_999);
        Coupon coupon = applyCoupon ? new Coupon("C", CouponType.FIXED, 1, 500_000, 0, t.toLocalDate()) : null;
        Order order = new OrderService().placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(applyCoupon ? 1L : 0L, order.getDiscount());
        assertEquals(applyCoupon ? 15_000L : 0L, order.getShipping());
        assertEquals(applyCoupon ? 514_999L : 500_000L, order.getTotal());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(t, order.getCreatedAt());
        assertEquals(0, p.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        cart.removeItem("P");
        assertSame(p, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
