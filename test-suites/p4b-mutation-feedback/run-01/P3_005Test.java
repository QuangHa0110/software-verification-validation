package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void stockChangesAreAdditiveAndLeaveProductMetadataIntact() {
        Product p = new Product(" P ", " Name ", 1, 1, 0);
        for (int invalid : new int[] {-1, 0}) {
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
        p.restoreStock(2);
        assertEquals(52, p.getStock());
        p.reduceStock(3);
        assertEquals(49, p.getStock());
        assertEquals(" P ", p.getId());
        assertEquals(" Name ", p.getName());
        assertEquals(1L, p.getPrice());
        assertEquals(1, p.getWeightGrams());
    }
}
