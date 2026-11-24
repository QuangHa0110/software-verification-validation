package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_005Test {

    @Test
    void placeOrderWithQuantityExactlyEqualToStockSucceeds() {
        OrderService orderService = new OrderService();
        Cart cart = new Cart();
        Product product = new Product("P1", "Item 1", 1000, 100, 5);
        cart.addItem(product, 5);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Order order = orderService.placeOrder("ORD1", cart, null, Region.INNER_CITY, now);
        assertNotNull(order);
        assertEquals(0, product.getStock());
    }
}
