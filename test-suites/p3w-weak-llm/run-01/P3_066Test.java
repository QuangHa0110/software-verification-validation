package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_066Test {

    @Test
    void orderServicePlaceOrderSuccessReducesStock() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Prod 1", 50000L, 200, 10);
        Cart cart = new Cart();
        cart.addItem(p, 2);

        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("ORD-001", cart, null, Region.INNER_CITY, now);

        assertNotNull(order);
        assertEquals("ORD-001", order.getId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(100000L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(115000L, order.getTotal());
        assertEquals(8, p.getStock());
        assertEquals(1, cart.getItems().size());
    }
}
