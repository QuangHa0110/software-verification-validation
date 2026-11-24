package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_035Test {

    @Test
    void orderConstructorDiscountBoundariesZeroAndFullSubtotalSucceed() {
        Product product = new Product("P1", "Product 1", 50_000L, 100, 10);
        CartItem item = new CartItem(product, 2);
        List<CartItem> items = List.of(item);
        long subtotal = 100_000L;
        LocalDateTime now = LocalDateTime.of(2026, 11, 24, 10, 0);

        Order orderZeroDiscount = new Order("ORD-01", items, subtotal, 0L, 15_000L, 115_000L, now, Region.INNER_CITY);
        assertEquals(0L, orderZeroDiscount.getDiscount());
        assertEquals(115_000L, orderZeroDiscount.getTotal());

        Order orderFullDiscount = new Order("ORD-02", items, subtotal, subtotal, 15_000L, 15_000L, now, Region.INNER_CITY);
        assertEquals(subtotal, orderFullDiscount.getDiscount());
        assertEquals(15_000L, orderFullDiscount.getTotal());
    }
}
