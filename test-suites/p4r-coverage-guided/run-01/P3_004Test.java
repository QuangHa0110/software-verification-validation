package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_004Test {

    @Test
    void stockChangesUseCurrentBalanceAndFailuresPreserveProductFields() {
        Product p = new Product(" A ", " Pen ", 37, 19, 7);
        p.reduceStock(1);
        assertEquals(6, p.getStock());
        assertThrows(InsufficientStockException.class, () -> p.reduceStock(7));
        assertEquals(6, p.getStock());
        p.reduceStock(6);
        assertEquals(0, p.getStock());
        for (int q : new int[]{0, -1}) {
            assertThrows(IllegalArgumentException.class, () -> p.reduceStock(q));
            assertEquals(0, p.getStock());
            assertThrows(IllegalArgumentException.class, () -> p.restoreStock(q));
            assertEquals(0, p.getStock());
        }
        p.restoreStock(1);
        p.restoreStock(52);
        assertEquals(53, p.getStock());
        assertEquals(" A ", p.getId());
        assertEquals(" Pen ", p.getName());
        assertEquals(37L, p.getPrice());
        assertEquals(19, p.getWeightGrams());
    }
}
