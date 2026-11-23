package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_041Test {

    @Test
    void testOrderServicePlaceOrderCalculatorExceptionPreservesStock() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P1", "Heavy Item", 10000L, 25000, 5); // exceeds 20000g max shipping
        cart.addItem(p, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("ORD-1", cart, null, Region.INNER_CITY, now));
        assertEquals(5, p.getStock());
    }
}
