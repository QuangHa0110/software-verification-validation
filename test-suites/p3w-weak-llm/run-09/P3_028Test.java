package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @Test
    void orderServicePlaceOrderStockCheckAndAtomicity() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p1 = new Product("P1", "A", 10L, 10, 1);
        Product p2 = new Product("P2", "B", 20L, 10, 2);
        cart.addItem(p1, 1);
        cart.addItem(p2, 2);

        p2.reduceStock(1);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D", cart, null, Region.INNER_CITY, time));
        assertEquals(1, p1.getStock());
        assertEquals(1, p2.getStock());
    }
}
