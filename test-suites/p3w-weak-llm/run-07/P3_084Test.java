package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_084Test {

    @Test
    void orderServicePlaceOrderSuccessfullyDeductsStock() {
        OrderService service = new OrderService();
        Product p = new Product("P01", "Book", 250000L, 500, 5);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon coupon = new Coupon("FIXED1", CouponType.FIXED, 1L, 0L, 0L, now.toLocalDate());

        Order order = service.placeOrder("ORD1", cart, coupon, Region.INNER_CITY, now);

        assertEquals(3, p.getStock());
        assertEquals("ORD1", order.getId());
        assertEquals(500000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(514999L, order.getTotal());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(now, order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
    }
}
