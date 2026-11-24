package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @Test
    void paymentAndShippingOnlyAdvanceFromTheirRequiredStates() {
        Product product = new Product("P", "Pen", 10, 1, 4);
        Cart cart = new Cart();
        cart.addItem(product, 3);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.PROVINCE, t);
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, product.getStock());
        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(1, product.getStock());
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.PAID, order.getStatus());
        service.ship(order);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertEquals(1, product.getStock());
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.SHIPPED, order.getStatus());

        Order cancelled = service.placeOrder("E", new Cart() {{
            addItem(product, 1);
        }}, null, Region.PROVINCE, t);
        assertEquals(0L, service.cancelOrder(cancelled, t));
        assertThrows(IllegalStateException.class, () -> service.pay(cancelled));
        assertEquals(OrderStatus.CANCELLED, cancelled.getStatus());
        assertThrows(IllegalStateException.class, () -> service.ship(cancelled));
        assertEquals(OrderStatus.CANCELLED, cancelled.getStatus());
        assertEquals("D", order.getId());
        assertEquals(t, order.getCreatedAt());
        assertEquals(Region.PROVINCE, order.getRegion());
        assertEquals(30L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(35_000L, order.getShipping());
        assertEquals(35_030L, order.getTotal());
        assertEquals(1, order.getItems().size());
        assertSame(product, order.getItems().get(0).getProduct());
        assertEquals(3, order.getItems().get(0).getQuantity());
        assertEquals(1, product.getStock());
    }
}
