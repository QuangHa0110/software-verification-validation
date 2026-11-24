package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_006Test {

    @Test
    void stockChangesUseCurrentBalanceAndPreserveProductDetails() {
        Product product = new Product(" P ", " Name ", 30000, 500, 7);
        product.reduceStock(1);
        assertEquals(6, product.getStock());
        assertThrows(InsufficientStockException.class, () -> product.reduceStock(7));
        assertEquals(6, product.getStock());
        product.reduceStock(6);
        assertEquals(0, product.getStock());

        for (int invalid : new int[] {-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> product.reduceStock(invalid));
            assertEquals(0, product.getStock());
            assertThrows(IllegalArgumentException.class, () -> product.restoreStock(invalid));
            assertEquals(0, product.getStock());
        }

        product.restoreStock(1);
        product.restoreStock(51);
        assertEquals(52, product.getStock());
        assertEquals(" P ", product.getId());
        assertEquals(" Name ", product.getName());
        assertEquals(30000L, product.getPrice());
        assertEquals(500, product.getWeightGrams());
    }
}
