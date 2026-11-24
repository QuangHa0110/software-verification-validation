package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_052Test {

    @Test
    void testPlaceOrderValidationOrderPrecedesCart() {
        OrderService service = new OrderService();
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Cart emptyCart = new Cart();

        assertThrows(IllegalArgumentException.class, () -> service.placeOrder(null, emptyCart, null, Region.INNER_CITY, now));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("", emptyCart, null, Region.INNER_CITY, now));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("  ", emptyCart, null, Region.INNER_CITY, now));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D1", emptyCart, null, null, now));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("D1", emptyCart, null, Region.INNER_CITY, null));

        assertThrows(IllegalStateException.class, () -> service.placeOrder("D1", null, null, Region.INNER_CITY, now));
        assertThrows(IllegalStateException.class, () -> service.placeOrder("D1", emptyCart, null, Region.INNER_CITY, now));
    }
}
