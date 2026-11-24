package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void payAndShipPermitOnlyTheirRespectivePredecessorsAndTerminalStatePrecedesTimeCheck() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product a = new Product("A", "First", 12, 3, 1);
        Product b = new Product("B", "Second", 19, 7, 3);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, time);
        Runnable checkDetails = () -> {
            assertEquals(0, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(50L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(25_000L, order.getShipping());
            assertEquals(25_050L, order.getTotal());
            assertEquals(time, order.getCreatedAt());
            assertEquals(Region.SUBURB, order.getRegion());
            List<CartItem> lines = order.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(1, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
        };

        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, time.minusNanos(1)));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        checkDetails.run();
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        checkDetails.run();
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, time.minusNanos(1)));
        assertEquals(OrderStatus.PAID, order.getStatus());
        checkDetails.run();
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        checkDetails.run();
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time.minusNanos(1)));
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        checkDetails.run();
    }
}
