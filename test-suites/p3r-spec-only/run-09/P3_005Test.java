package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void stockOperationsRespectAvailableStockAndPreserveProductAttributes() {
        Product product = new Product(" P ", " Name ", 1L, 1, 0);
        assertEquals(0, product.getStock());
        for (int invalid : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> product.reduceStock(invalid));
            assertEquals(0, product.getStock());
            assertThrows(IllegalArgumentException.class, () -> product.restoreStock(invalid));
            assertEquals(0, product.getStock());
        }
        assertThrows(InsufficientStockException.class, () -> product.reduceStock(1));
        assertEquals(0, product.getStock());
        product.restoreStock(1);
        product.restoreStock(51);
        assertEquals(52, product.getStock());
        product.reduceStock(1);
        assertEquals(51, product.getStock());
        assertThrows(InsufficientStockException.class, () -> product.reduceStock(52));
        assertEquals(51, product.getStock());
        product.reduceStock(51);
        assertEquals(0, product.getStock());
        assertEquals(" P ", product.getId());
        assertEquals(" Name ", product.getName());
        assertEquals(1L, product.getPrice());
        assertEquals(1, product.getWeightGrams());
    }
}
