package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_055Test {

    @Test
    void testPlaceOrderPropagatesPricingAndShippingExceptionsAtomically() {
        OrderService service = new OrderService();
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Product p1 = new Product("A", "Prod1", 10L, 10000, 1);
        Product p2 = new Product("B", "Prod2", 20L, 10001, 2);
        Cart cart = new Cart();
        cart.addItem(p1, 1);
        cart.addItem(p2, 1);

        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D3", cart, null, Region.REMOTE, now));
        assertEquals(1, p1.getStock());
        assertEquals(2, p2.getStock());
    }
}
