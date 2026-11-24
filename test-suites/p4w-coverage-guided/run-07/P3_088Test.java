package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_088Test {

    @Test
    void orderServicePlaceOrderNullOrEmptyCartThrowsIllegalStateException() {
        OrderService service = new OrderService();
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalStateException.class, () ->
            service.placeOrder("ORD1", null, null, Region.INNER_CITY, now)
        );
        Cart emptyCart = new Cart();
        assertThrows(IllegalStateException.class, () ->
            service.placeOrder("ORD1", emptyCart, null, Region.INNER_CITY, now)
        );
    }
}
