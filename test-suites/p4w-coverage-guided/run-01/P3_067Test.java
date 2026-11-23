package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_067Test {

    @Test
    void orderServicePlaceOrderValidationErrors() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        // invalid orderId
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("", cart, null, Region.INNER_CITY, now));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder(null, cart, null, Region.INNER_CITY, now));

        // null region
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("O1", cart, null, null, now));

        // null now
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("O1", cart, null, Region.INNER_CITY, null));

        // empty or null cart
        assertThrows(IllegalStateException.class, () -> service.placeOrder("O1", null, null, Region.INNER_CITY, now));
        assertThrows(IllegalStateException.class, () -> service.placeOrder("O1", cart, null, Region.INNER_CITY, now));
    }
}
