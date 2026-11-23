package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void payAndShipAcceptOnlyTheirRespectiveStartingStates(OrderStatus start) {
        Product p = new Product("P", "P", 30, 5, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 9);
        OrderService service = new OrderService();
        Order order = service.placeOrder("O", cart, null, Region.INNER_CITY, t);

        order.setStatus(start);
        if (start == OrderStatus.PENDING) {
            service.pay(order);
            assertEquals(OrderStatus.PAID, order.getStatus());
        } else {
            assertThrows(IllegalStateException.class, () -> service.pay(order));
            assertEquals(start, order.getStatus());
        }
        assertEquals(0, p.getStock());
        order.setStatus(start);
        if (start == OrderStatus.PAID) {
            service.ship(order);
            assertEquals(OrderStatus.SHIPPED, order.getStatus());
        } else {
            assertThrows(IllegalStateException.class, () -> service.ship(order));
            assertEquals(start, order.getStatus());
        }
        assertEquals(0, p.getStock());
        assertEquals(60L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(15_000L, order.getShipping());
        assertEquals(15_060L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());
        assertEquals("O", order.getId());
        assertEquals(1, order.getItems().size());
        assertSame(p, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
