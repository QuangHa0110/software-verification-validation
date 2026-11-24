package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_037Test {

    @Test
    void orderServicePlaceOrderQuantityExactlyEqualsStockSucceeds() {
        OrderService orderService = new OrderService();
        Product product = new Product("P1", "Product 1", 20_000L, 500, 5);
        Cart cart = new Cart();
        cart.addItem(product, 5);
        LocalDateTime now = LocalDateTime.of(2026, 11, 24, 10, 0);

        Order order = orderService.placeOrder("ORD-01", cart, null, Region.INNER_CITY, now);
        assertNotNull(order);
        assertEquals(0, product.getStock());
    }
}
