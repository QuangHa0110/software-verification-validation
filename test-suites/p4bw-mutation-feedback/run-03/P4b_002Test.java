package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_002Test {

    @Test
    void orderConstructorDiscountEqualsSubtotalSucceeds() {
        Product product = new Product("P1", "Product 1", 1000, 100, 10);
        CartItem item = new CartItem(product, 1);
        List<CartItem> items = List.of(item);
        Order order = new Order("O1", items, 1000, 1000, 15000, 15000, LocalDateTime.of(2026, 11, 23, 8, 0), Region.INNER_CITY);
        assertEquals(1000, order.getDiscount());
        assertEquals(15000, order.getTotal());
    }
}
