package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void defaultOrderServiceTestsFreeShippingAgainstGoodsAfterDiscount(boolean useCoupon) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999_999_999);
        Coupon coupon = useCoupon ? new Coupon("F", CouponType.FIXED, 1, 500_000, 0, t.toLocalDate()) : null;
        Product product = new Product("P", "Pen", 250_000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(product, 2);
        Order order = new OrderService().placeOrder(" D ", cart, coupon, Region.INNER_CITY, t);

        assertEquals(" D ", order.getId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(t, order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(useCoupon ? 1L : 0L, order.getDiscount());
        assertEquals(useCoupon ? 15_000L : 0L, order.getShipping());
        assertEquals(useCoupon ? 514_999L : 500_000L, order.getTotal());
        assertEquals(0, product.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(product, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        cart.removeItem("P");
        assertEquals(1, order.getItems().size());
        assertSame(product, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
