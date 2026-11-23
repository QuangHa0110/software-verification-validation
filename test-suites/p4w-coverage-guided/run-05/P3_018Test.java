package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void testCartItemValidAndGetters() {
        Product p = new Product("P1", "Pen", 10000L, 50, 20);
        CartItem item = new CartItem(p, 3);
        assertSame(p, item.getProduct());
        assertEquals(3, item.getQuantity());
        assertEquals(30000L, item.getLineTotal());
    }
}
