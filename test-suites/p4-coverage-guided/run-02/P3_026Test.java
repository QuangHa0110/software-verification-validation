package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @Test
    void paymentAndShipmentAllowOnlyTheirSpecifiedTransitions() {
        OrderService service = new OrderService();
        assertThrows(IllegalArgumentException.class, () -> service.pay(null));
        assertThrows(IllegalArgumentException.class, () -> service.ship(null));
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0, 3, 9);
        for (OrderStatus initial : OrderStatus.values()) {
            Product product = new Product("P", "P", 100, 5, 2);
            Cart cart = new Cart();
            cart.addItem(product, 1);
            Order order = service.placeOrder("D", cart, null, Region.SUBURB, time);
            order.setStatus(initial);
            if (initial == OrderStatus.PENDING) {
                service.pay(order);
                assertEquals(OrderStatus.PAID, order.getStatus());
            } else {
                assertThrows(IllegalStateException.class, () -> service.pay(order));
                assertEquals(initial, order.getStatus());
            }
            assertEquals(1, product.getStock());

            order.setStatus(initial);
            if (initial == OrderStatus.PAID) {
                service.ship(order);
                assertEquals(OrderStatus.SHIPPED, order.getStatus());
            } else {
                assertThrows(IllegalStateException.class, () -> service.ship(order));
                assertEquals(initial, order.getStatus());
            }
            assertEquals(1, product.getStock());
            assertEquals("D", order.getId());
            assertEquals(time, order.getCreatedAt());
            assertEquals(Region.SUBURB, order.getRegion());
            assertEquals(100L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(25_000L, order.getShipping());
            assertEquals(25_100L, order.getTotal());
            assertEquals(1, order.getItems().size());
            assertSame(product, order.getItems().get(0).getProduct());
            assertEquals(1, order.getItems().get(0).getQuantity());
        }
    }
}
