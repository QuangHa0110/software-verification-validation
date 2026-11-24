package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_069Test {

    @Test
    void orderServicePaySuccessAndInvalidStates() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Prod 1", 50000L, 200, 10);
        Cart cart = new Cart();
        cart.addItem(p, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("O1", cart, null, Region.INNER_CITY, now);

        assertThrows(IllegalArgumentException.class, () -> service.pay(null));

        service.pay(order);
        assertEquals(OrderStatus.PAID, order.getStatus());

        // paying already paid order throws
        assertThrows(IllegalStateException.class, () -> service.pay(order));
    }
}
