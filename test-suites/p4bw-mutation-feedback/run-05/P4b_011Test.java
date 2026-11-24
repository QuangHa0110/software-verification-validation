package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_011Test {

    @Test
    void testKillOrderConstructorDiscountEqualsSubtotalAllowed() {
        Product p = new Product("P1", "Product 1", 1000L, 100, 10);
        CartItem item = new CartItem(p, 2);
        List<CartItem> items = List.of(item);
        long subtotal = 2000L;
        long discount = 2000L;
        long shipping = 15000L;
        long total = subtotal - discount + shipping;
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Order order = new Order("ORD1", items, subtotal, discount, shipping, total, now, Region.INNER_CITY);
        assertEquals(discount, order.getDiscount());
        assertEquals(total, order.getTotal());
    }
}
