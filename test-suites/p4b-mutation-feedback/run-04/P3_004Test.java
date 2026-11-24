package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_004Test {

    @Test
    void stockChangesUseCurrentBalanceAndPreserveProductDetails() {
        Product product = new Product(" P ", " Name ", 1, 1, 0);
        assertEquals(0, product.getStock());
        for (int invalid : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> product.reduceStock(invalid));
            assertEquals(0, product.getStock());
            assertThrows(IllegalArgumentException.class, () -> product.restoreStock(invalid));
            assertEquals(0, product.getStock());
        }
        product.restoreStock(1);
        assertThrows(InsufficientStockException.class, () -> product.reduceStock(2));
        assertEquals(1, product.getStock());
        product.reduceStock(1);
        assertEquals(0, product.getStock());
        product.restoreStock(50);
        product.restoreStock(2);
        assertEquals(52, product.getStock());
        product.reduceStock(3);
        assertEquals(49, product.getStock());
        assertEquals(" P ", product.getId());
        assertEquals(" Name ", product.getName());
        assertEquals(1L, product.getPrice());
        assertEquals(1, product.getWeightGrams());
    }
}
