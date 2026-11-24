package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_041Test {

    @Test
    void orderServicePlaceOrderInvalidInputs() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P1", "Pen", 10000L, 50, 10);
        cart.addItem(p, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        assertThrows(IllegalArgumentException.class, () -> service.placeOrder(null, cart, null, Region.INNER_CITY, now));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("", cart, null, Region.INNER_CITY, now));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("   ", cart, null, Region.INNER_CITY, now));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("O1", cart, null, null, now));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("O1", cart, null, Region.INNER_CITY, null));

        assertThrows(IllegalStateException.class, () -> service.placeOrder("O1", null, null, Region.INNER_CITY, now));
        assertThrows(IllegalStateException.class, () -> service.placeOrder("O1", new Cart(), null, Region.INNER_CITY, now));
    }
}
