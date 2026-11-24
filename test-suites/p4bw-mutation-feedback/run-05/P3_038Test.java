package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_038Test {

    @Test
    void testOrderServicePlaceOrderSuccessAndStockDeduction() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p1 = new Product("P1", "Pen", 20000L, 200, 10);
        Product p2 = new Product("P2", "Book", 50000L, 300, 5);
        cart.addItem(p1, 2);
        cart.addItem(p2, 1);

        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon coupon = new Coupon("DISC10", CouponType.PERCENT, 10L, 50000L, 20000L, now.toLocalDate());

        Order order = service.placeOrder("ORD-001", cart, coupon, Region.INNER_CITY, now);

        assertNotNull(order);
        assertEquals("ORD-001", order.getId());
        assertEquals(90000L, order.getSubtotal());
        assertEquals(9000L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(96000L, order.getTotal());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(8, p1.getStock());
        assertEquals(4, p2.getStock());
        assertEquals(2, cart.getItems().size());
    }
}
