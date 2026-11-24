package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_041Test {

    @Test
    void orderServicePlaceOrderWeightOverloadPreventsDeduction() {
        OrderService service = new OrderService();
        Product p1 = new Product("P1", "Heavy 1", 100L, 10000, 5);
        Product p2 = new Product("P2", "Heavy 2", 200L, 10001, 5);
        Cart cart = new Cart();
        cart.addItem(p1, 1);
        cart.addItem(p2, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D1", cart, null, Region.REMOTE, now));
        assertEquals(5, p1.getStock());
        assertEquals(5, p2.getStock());
    }
}
