package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @Test
    void orderServicePlaceOrderArgumentValidation() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P1", "Name", 100L, 10, 5);
        cart.addItem(p, 1);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () -> service.placeOrder(null, cart, null, Region.INNER_CITY, time));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("", cart, null, Region.INNER_CITY, time));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("   ", cart, null, Region.INNER_CITY, time));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, null, time));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D", cart, null, Region.INNER_CITY, null));

        assertThrows(IllegalStateException.class, () -> service.placeOrder("D", null, null, Region.INNER_CITY, time));
        assertThrows(IllegalStateException.class, () -> service.placeOrder("D", new Cart(), null, Region.INNER_CITY, time));
    }
}
