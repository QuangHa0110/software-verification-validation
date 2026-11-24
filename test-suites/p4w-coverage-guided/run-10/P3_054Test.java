package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_054Test {

    @Test
    void testPlaceOrderSuccessAndReducesStock() {
        OrderService service = new OrderService();
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Product p = new Product("A", "Prod", 250000L, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);

        Coupon coupon = new Coupon("C1", CouponType.FIXED, 1L, 0L, 0L, LocalDate.of(2026, 11, 23));
        Order order = service.placeOrder("D1", cart, coupon, Region.INNER_CITY, now);

        assertEquals(0, p.getStock());
        assertEquals("D1", order.getId());
        assertEquals(500000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(514999L, order.getTotal());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(now, order.getCreatedAt());

        assertEquals(1, cart.getItems().size());
    }
}
