package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_083Test {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "\t\n"})
    void orderServicePlaceOrderInvalidOrderId(String orderId) {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P1", "Bút", 1000L, 10, 5);
        cart.addItem(p, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () ->
            service.placeOrder(orderId, cart, null, Region.INNER_CITY, now));
        assertEquals(5, p.getStock());
    }
}
