package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_007Test {

    @Test
    void standaloneCartItemRequiresPositiveQuantityButDoesNotEnforceCartOrStockLimits() {
        Product p = new Product("P", "Product", 1_000_000_000L, 2, 0);
        assertThrows(IllegalArgumentException.class, () -> new CartItem(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(p, -1));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(p, 0));
        for (int quantity : new int[]{1, 3, 51}) {
            CartItem item = new CartItem(p, quantity);
            assertSame(p, item.getProduct());
            assertEquals(quantity, item.getQuantity());
            assertEquals(1_000_000_000L * quantity, item.getLineTotal());
            assertEquals(0, p.getStock());
        }
    }
}
