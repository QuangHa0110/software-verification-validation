package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_084Test {

    @Test
    void orderServicePlaceOrderNullRegionOrTime() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P1", "Bút", 1000L, 10, 5);
        cart.addItem(p, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(IllegalArgumentException.class, () ->
            service.placeOrder("D1", cart, null, null, now));
        assertThrows(IllegalArgumentException.class, () ->
            service.placeOrder("D1", cart, null, Region.INNER_CITY, null));
        assertEquals(5, p.getStock());
    }
}
