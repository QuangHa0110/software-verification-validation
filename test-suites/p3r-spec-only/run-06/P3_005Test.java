package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void stockOperationsValidateQuantitiesAndPreserveProductDetails() {
        Product p = new Product(" P ", " Pen ", 1L, 1, 0);
        assertEquals(0, p.getStock());
        for (int invalid : new int[] {-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> p.reduceStock(invalid));
            assertEquals(0, p.getStock());
            assertThrows(IllegalArgumentException.class, () -> p.restoreStock(invalid));
            assertEquals(0, p.getStock());
        }
        assertThrows(InsufficientStockException.class, () -> p.reduceStock(1));
        assertEquals(0, p.getStock());

        p.restoreStock(1);
        p.restoreStock(51);
        assertEquals(52, p.getStock());
        p.reduceStock(1);
        assertEquals(51, p.getStock());
        assertThrows(InsufficientStockException.class, () -> p.reduceStock(52));
        assertEquals(51, p.getStock());
        p.reduceStock(51);
        assertEquals(0, p.getStock());
        assertEquals(" P ", p.getId());
        assertEquals(" Pen ", p.getName());
        assertEquals(1L, p.getPrice());
        assertEquals(1, p.getWeightGrams());
    }
}
