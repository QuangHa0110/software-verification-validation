package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_039Test {

    @Test
    void orderServicePlaceOrderStockShortagePreventsDeduction() {
        OrderService service = new OrderService();
        Product p1 = new Product("P1", "Item 1", 1000L, 100, 2);
        Product p2 = new Product("P2", "Item 2", 2000L, 100, 2);

        Cart cart = new Cart();
        cart.addItem(p1, 2);
        cart.addItem(p2, 2);

        p2.reduceStock(1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D1", cart, null, Region.INNER_CITY, now));
        assertEquals(2, p1.getStock());
        assertEquals(1, p2.getStock());
    }
}
