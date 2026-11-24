package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void cartItemConstructorValidAndGetters() {
        Product p1 = new Product("P1", "Pen", 1L, 1, 0);
        CartItem item1 = new CartItem(p1, 1);
        assertSame(p1, item1.getProduct());
        assertEquals(1, item1.getQuantity());
        assertEquals(1L, item1.getLineTotal());

        Product p2 = new Product("P2", "Book", 30000L, 500, 7);
        CartItem item2 = new CartItem(p2, 2);
        assertSame(p2, item2.getProduct());
        assertEquals(2, item2.getQuantity());
        assertEquals(60000L, item2.getLineTotal());

        Product p3 = new Product("P3", "Laptop", 1000000000L, 2000, 10);
        CartItem item3 = new CartItem(p3, 3);
        assertEquals(3000000000L, item3.getLineTotal());
    }
}
