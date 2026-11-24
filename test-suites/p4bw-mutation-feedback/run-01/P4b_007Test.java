package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_007Test {

    @Test
    void killOrderServicePlaceOrderItemQuantityEqualsStockBoundary() {
        OrderService orderService = new OrderService();
        Cart cart = new Cart();
        Product product = new Product("P1", "Product 1", 50_000L, 300, 5);
        cart.addItem(product, 5); // Quantity equals stock exactly (5 == 5)
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        Order order = orderService.placeOrder("ORD-STOCK-EQUAL", cart, null, Region.INNER_CITY, now);
        assertNotNull(order);
        assertEquals(0, product.getStock());
    }
}
