package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_002Test {

    @Test
    void orderConstructorDiscountBoundaries() {
        Product product = new Product("P1", "Product 1", 1000L, 100, 10);
        CartItem item = new CartItem(product, 1);
        List<CartItem> items = List.of(item);
        long subtotal = 1000L;
        long shipping = 15000L;
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        // discount == 0 is valid boundary (discount < 0 condition boundary check)
        Order orderZeroDiscount = new Order("O1", items, subtotal, 0L, shipping, subtotal - 0L + shipping, now, Region.INNER_CITY);
        assertEquals(0L, orderZeroDiscount.getDiscount());

        // discount == subtotal is valid boundary (discount > subtotal condition boundary check)
        Order orderMaxDiscount = new Order("O2", items, subtotal, subtotal, shipping, subtotal - subtotal + shipping, now, Region.INNER_CITY);
        assertEquals(subtotal, orderMaxDiscount.getDiscount());
    }
}
