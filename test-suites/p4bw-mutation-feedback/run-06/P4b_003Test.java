package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_003Test {

    @Test
    void orderConstructorWithZeroShippingFee() {
        Product product = new Product("P1", "Item 1", 1000, 100, 10);
        CartItem item = new CartItem(product, 1);
        List<CartItem> items = List.of(item);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Order orderZeroShipping = new Order("ORD1", items, 1000, 0, 0, 1000, now, Region.INNER_CITY);
        assertEquals(0, orderZeroShipping.getShipping());
        assertEquals(1000, orderZeroShipping.getTotal());
    }
}
