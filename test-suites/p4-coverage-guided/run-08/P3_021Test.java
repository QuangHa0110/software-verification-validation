package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void payAndShipAllowOnlyTheirSpecifiedTransitionsAndLeaveStockAndMoneyUntouched() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product p = new Product("P", "P", 20, 1, 3);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        OrderService service = new OrderService();
        Order order = service.placeOrder("O", cart, null, Region.PROVINCE, t);
        Runnable verify = () -> {
            assertEquals(1, p.getStock());
            assertEquals(40L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(35_000L, order.getShipping());
            assertEquals(35_040L, order.getTotal());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.PROVINCE, order.getRegion());
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
        for (OrderStatus forbidden : List.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED)) {
            order.setStatus(forbidden);
            assertThrows(IllegalStateException.class, () -> service.pay(order));
            assertEquals(forbidden, order.getStatus());
            verify.run();
            assertThrows(IllegalStateException.class, () -> service.ship(order));
            assertEquals(forbidden, order.getStatus());
            verify.run();
        }
    }
}
