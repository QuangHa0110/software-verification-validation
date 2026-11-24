package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @Test
    void payAndShipAllowOnlyTheirSingleForwardTransition() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product product = new Product("P", "P", 17, 1, 2);
        Cart cart = new Cart();
        cart.addItem(product, 2);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, time);
        Runnable verify = () -> {
            assertEquals(0, product.getStock());
            assertEquals(34L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(25000L, order.getShipping());
            assertEquals(25034L, order.getTotal());
            assertEquals(time, order.getCreatedAt());
            assertEquals(1, order.getItems().size());
            assertSame(product, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
        };
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        verify.run();
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        verify.run();
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        verify.run();
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        verify.run();
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        verify.run();
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        verify.run();
    }
}
