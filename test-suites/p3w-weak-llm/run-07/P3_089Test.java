package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_089Test {

    @Test
    void orderServicePlaceOrderStockDecreasedBeforePlaceThrowsInsufficientStockException() {
        OrderService service = new OrderService();
        Product p1 = new Product("P01", "Book", 10000L, 500, 5);
        Product p2 = new Product("P02", "Pen", 20000L, 100, 2);
        Cart cart = new Cart();
        cart.addItem(p1, 2);
        cart.addItem(p2, 2);

        p2.reduceStock(1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(InsufficientStockException.class, () ->
            service.placeOrder("ORD1", cart, null, Region.INNER_CITY, now)
        );
        assertEquals(5, p1.getStock());
        assertEquals(1, p2.getStock());
    }
}
