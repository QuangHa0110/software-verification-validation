package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void stockChangesAreExactAndFailuresPreserveAllProductFields() {
        Product p = new Product(" P ", " Vở ", 30_000, 500, 2);
        p.reduceStock(1);
        assertEquals(1, p.getStock());
        assertThrows(InsufficientStockException.class, () -> p.reduceStock(2));
        assertEquals(1, p.getStock());
        p.reduceStock(1);
        assertEquals(0, p.getStock());

        for (int invalid : new int[]{0, -1}) {
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
        assertEquals(" P ", p.getId());
        assertEquals(" Vở ", p.getName());
        assertEquals(30_000L, p.getPrice());
        assertEquals(500, p.getWeightGrams());
    }
}
