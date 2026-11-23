package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_004Test {

    @Test
    void stockOperationsUseCurrentStockAndPreserveProductAttributes() {
        Product p = new Product(" P ", " Name ", 1, 1, 0);
        assertEquals(0, p.getStock());
        for (int invalid : new int[]{0, -1}) {
            assertThrows(IllegalArgumentException.class, () -> p.reduceStock(invalid));
            assertEquals(0, p.getStock());
            assertThrows(IllegalArgumentException.class, () -> p.restoreStock(invalid));
            assertEquals(0, p.getStock());
        }
        p.restoreStock(1);
        assertThrows(InsufficientStockException.class, () -> p.reduceStock(2));
        assertEquals(1, p.getStock());
        p.reduceStock(1);
        assertEquals(0, p.getStock());
        p.restoreStock(50);
        p.restoreStock(3);
        assertEquals(53, p.getStock());
        p.reduceStock(2);
        assertEquals(51, p.getStock());
        assertEquals(" P ", p.getId());
        assertEquals(" Name ", p.getName());
        assertEquals(1L, p.getPrice());
        assertEquals(1, p.getWeightGrams());
    }
}
