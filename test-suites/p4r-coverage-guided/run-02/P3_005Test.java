package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void productRequiresPositivePriceAndWeightAndNonnegativeStock() {
        for (int invalid : new int[] {-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> new Product("A", "Item", invalid, 1, 0));
            assertThrows(IllegalArgumentException.class, () -> new Product("A", "Item", 1, invalid, 0));
        }
        assertThrows(IllegalArgumentException.class, () -> new Product("A", "Item", 1, 1, -1));
        Product smallest = new Product("A", "Item", 1, 1, 0);
        assertEquals(1L, smallest.getPrice());
        assertEquals(1, smallest.getWeightGrams());
        assertEquals(0, smallest.getStock());
    }
}
