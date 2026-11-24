package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void paymentAndShipmentEnforceStateAndLeaveStockAndOrderDetailsIntact() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 123);
        Cart cart = new Cart();
        Product p = new Product("P", "Name", 100, 1, 2);
        cart.addItem(p, 2);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, time);
        Runnable verify = () -> {
            assertEquals(0, p.getStock());
            assertEquals("D", order.getId());
            assertEquals(time, order.getCreatedAt());
            assertEquals(Region.SUBURB, order.getRegion());
            assertEquals(200L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(25_000L, order.getShipping());
            assertEquals(25_200L, order.getTotal());
            assertEquals(1, order.getItems().size());
            assertSame(p, order.getItems().get(0).getProduct());
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
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        verify.run();
    }
}
