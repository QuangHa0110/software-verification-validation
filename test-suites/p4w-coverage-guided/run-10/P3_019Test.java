package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @Test
    void testProductReduceStockInsufficientStock() {
        Product p = new Product("P1", "Name", 100L, 10, 7);
        assertThrows(InsufficientStockException.class, () -> p.reduceStock(8));
        assertEquals(7, p.getStock());
    }
}
