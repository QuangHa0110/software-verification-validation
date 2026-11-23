package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_047Test {

    @Test
    void orderServicePlaceOrderInsufficientStock() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product pA = new Product("A", "Prod A", 1000L, 10, 5);
        Product pB = new Product("B", "Prod B", 2000L, 20, 5);
        cart.addItem(pA, 2);
        cart.addItem(pB, 3);

        pB.reduceStock(3);
        assertEquals(2, pB.getStock());

        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D1", cart, null, Region.INNER_CITY, LocalDateTime.of(2026, 11, 23, 8, 0)));
        assertEquals(5, pA.getStock());
        assertEquals(2, pB.getStock());
    }
}
