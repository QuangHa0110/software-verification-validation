package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_068Test {

    @Test
    void orderServicePlaceOrderStockDeductionFailsAtomically() {
        OrderService service = new OrderService();
        Product p1 = new Product("P1", "P1", 10000L, 100, 5);
        Product p2 = new Product("P2", "P2", 10000L, 100, 2);
        Cart cart = new Cart();
        cart.addItem(p1, 2);
        cart.addItem(p2, 2);

        p2.reduceStock(1); // p2 stock is now 1, cart needs 2
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(InsufficientStockException.class, () -> service.placeOrder("O1", cart, null, Region.INNER_CITY, now));
        assertEquals(5, p1.getStock());
        assertEquals(1, p2.getStock());
    }
}
