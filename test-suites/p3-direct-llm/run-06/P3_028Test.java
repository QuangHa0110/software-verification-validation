package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @ParameterizedTest
    @CsvSource({"false,0,0,500000", "true,1,15000,514999"})
    void standardPlacementUsesDiscountedGoodsForFreeShipping(
            boolean useCoupon, long discount, long shipping, long total) {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999999999);
        Product product = new Product("P", "P", 250_000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(product, 2);
        Coupon coupon = useCoupon
            ? new Coupon("F", CouponType.FIXED, 1, 500_000, 0, time.toLocalDate())
            : null;
        Order order = new OrderService().placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(discount, order.getDiscount());
        assertEquals(shipping, order.getShipping());
        assertEquals(total, order.getTotal());
        assertEquals(time, order.getCreatedAt());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(0, product.getStock());
        assertSame(product, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        cart.updateQuantity("P", 0);
        assertTrue(cart.isEmpty());
        assertSame(product, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
