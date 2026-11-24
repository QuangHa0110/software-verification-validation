package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void testProductStockOperations() {
        Product p = new Product("P1", "Book", 50000L, 200, 10);
        p.reduceStock(4);
        assertEquals(6, p.getStock());
        p.reduceStock(6);
        assertEquals(0, p.getStock());

        assertThrows(IllegalArgumentException.class, () -> p.reduceStock(0));
        assertThrows(IllegalArgumentException.class, () -> p.reduceStock(-1));
        assertThrows(InsufficientStockException.class, () -> p.reduceStock(1));
        assertEquals(0, p.getStock());

        p.restoreStock(5);
        assertEquals(5, p.getStock());
        p.restoreStock(50);
        assertEquals(55, p.getStock());

        assertThrows(IllegalArgumentException.class, () -> p.restoreStock(0));
        assertThrows(IllegalArgumentException.class, () -> p.restoreStock(-5));
        assertEquals(55, p.getStock());
    }
}
