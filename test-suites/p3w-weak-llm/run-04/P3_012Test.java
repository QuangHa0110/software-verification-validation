package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void cartItemValidCreationAndGetters() {
        Product p = new Product("P1", "Item", 1000000000L, 200, 10);
        CartItem item = new CartItem(p, 3);
        assertSame(p, item.getProduct());
        assertEquals(3, item.getQuantity());
        assertEquals(3000000000L, item.getLineTotal());
    }
}
