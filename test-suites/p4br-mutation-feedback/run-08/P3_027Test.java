package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @ParameterizedTest
    @CsvSource({"false,0,0,500000", "true,1,15000,514999"})
    void standardPlacementUsesDiscountedGoodsForFreeShipping(
            boolean useCoupon, long discount, long shipping, long total) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 123_456_789);
        Product a = new Product("A", "First", 250_000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        Coupon coupon = useCoupon ? new Coupon("C", CouponType.FIXED, 1, 500_000, 0, t.toLocalDate()) : null;
        Order order = new OrderService().placeOrder(" D ", cart, coupon, Region.INNER_CITY, t);
        assertEquals(" D ", order.getId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(t, order.getCreatedAt());
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(discount, order.getDiscount());
        assertEquals(shipping, order.getShipping());
        assertEquals(total, order.getTotal());
        assertEquals(0, a.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(a, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        cart.updateQuantity("A", 0);
        assertEquals(1, order.getItems().size());
        assertSame(a, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
