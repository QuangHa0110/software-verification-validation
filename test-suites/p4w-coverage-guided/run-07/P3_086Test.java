package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_086Test {

    @Test
    void orderServicePlaceOrderNullOrderIdThrowsException() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P01", "Book", 250000L, 500, 5);
        cart.addItem(p, 2);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class, () ->
            service.placeOrder(null, cart, null, Region.INNER_CITY, now)
        );
    }
}
