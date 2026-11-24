package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_004Test {

    @Test
    void productConstructorValidatesEachFieldAtItsBoundary() {
        for (String invalid : Arrays.asList(null, "", " \t\n")) {
            assertThrows(IllegalArgumentException.class, () -> new Product(invalid, "N", 1, 1, 0));
            assertThrows(IllegalArgumentException.class, () -> new Product("P", invalid, 1, 1, 0));
        }
        for (int invalid : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> new Product("P", "N", invalid, 1, 0));
            assertThrows(IllegalArgumentException.class, () -> new Product("P", "N", 1, invalid, 0));
        }
        assertThrows(IllegalArgumentException.class, () -> new Product("P", "N", 1, 1, -1));

        Product minimum = new Product("P", "N", 1, 1, 0);
        assertEquals(1L, minimum.getPrice());
        assertEquals(1, minimum.getWeightGrams());
        assertEquals(0, minimum.getStock());
    }
}
