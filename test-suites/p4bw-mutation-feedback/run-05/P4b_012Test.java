package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_012Test {

    @Test
    void testKillOrderConstructorShippingZeroAllowed() {
        Product p = new Product("P1", "Product 1", 500000L, 100, 10);
        CartItem item = new CartItem(p, 1);
        List<CartItem> items = List.of(item);
        long subtotal = 500000L;
        long discount = 0L;
        long shipping = 0L;
        long total = subtotal - discount + shipping;
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Order order = new Order("ORD2", items, subtotal, discount, shipping, total, now, Region.INNER_CITY);
        assertEquals(0L, order.getShipping());
        assertEquals(total, order.getTotal());
    }
}
