package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_031Test {

    @ParameterizedTest
    @CsvSource({"false,0,0,500000", "true,1,15000,514999"})
    void defaultOrderComputesFreeShippingFromDiscountedGoods(
            boolean useCoupon, long discount, long shipping, long total) {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999999999);
        Product a = new Product("A", "A", 250000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        Coupon coupon = useCoupon
            ? new Coupon("F", CouponType.FIXED, 1, 500000, 0, time.toLocalDate())
            : null;
        Order order = new OrderService().placeOrder(" D ", cart, coupon, Region.INNER_CITY, time);
        assertEquals(" D ", order.getId());
        assertEquals(time, order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(500000L, order.getSubtotal());
        assertEquals(discount, order.getDiscount());
        assertEquals(shipping, order.getShipping());
        assertEquals(total, order.getTotal());
        assertEquals(0, a.getStock());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        cart.updateQuantity("A", 0);
        assertSame(a, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
