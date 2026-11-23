package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_030Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void paymentAndShipmentAllowOnlyTheirRespectiveStartingStates(OrderStatus start) {
        Product p = new Product("P", "P", 25, 10, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.INNER_CITY, t);
        order.setStatus(start);
        if (start == OrderStatus.PENDING) {
            service.pay(order);
            assertEquals(OrderStatus.PAID, order.getStatus());
        } else {
            assertThrows(IllegalStateException.class, () -> service.pay(order));
            assertEquals(start, order.getStatus());
        }
        assertEquals(0, p.getStock());
        assertEquals(15_050L, order.getTotal());

        order.setStatus(start);
        if (start == OrderStatus.PAID) {
            service.ship(order);
            assertEquals(OrderStatus.SHIPPED, order.getStatus());
        } else {
            assertThrows(IllegalStateException.class, () -> service.ship(order));
            assertEquals(start, order.getStatus());
        }
        assertEquals(0, p.getStock());
        assertEquals(50L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(15_000L, order.getShipping());
        assertEquals(15_050L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
        assertSame(p, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
    }
}
