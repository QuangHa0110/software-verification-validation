package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void standaloneCartItemValidatesQuantityWithoutApplyingCartOrStockLimits() {
        Product product = new Product("P", "Book", 1_000_000_000L, 10, 0);
        assertThrows(IllegalArgumentException.class, () -> new CartItem(null, 1));
        for (int invalid : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> new CartItem(product, invalid));
        }
        for (int quantity : new int[]{1, 3, 51}) {
            CartItem item = new CartItem(product, quantity);
            assertSame(product, item.getProduct());
            assertEquals(quantity, item.getQuantity());
            assertEquals(1_000_000_000L * quantity, item.getLineTotal());
            assertEquals(0, product.getStock());
        }
    }
}
