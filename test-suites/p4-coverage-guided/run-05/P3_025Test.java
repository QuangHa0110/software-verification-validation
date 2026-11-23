package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void paymentAndShipmentAllowOnlyTheirSpecifiedStartingStates() {
        Product product = new Product("P", "Book", 1, 1, 1);
        Cart cart = new Cart();
        cart.addItem(product, 1);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.INNER_CITY, time);

        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(0, product.getStock());
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(1L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(15_000L, order.getShipping());
        assertEquals(15_001L, order.getTotal());
        assertEquals(time, order.getCreatedAt());
        assertSame(product, order.getItems().get(0).getProduct());
        assertEquals(1, order.getItems().get(0).getQuantity());
        assertEquals(0, product.getStock());
    }
}
