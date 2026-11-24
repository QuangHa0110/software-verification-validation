package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void productReduceStockInvalidOrInsufficient() {
        Product p = new Product("P1", "Item", 1000L, 100, 5);
        assertThrows(IllegalArgumentException.class, () -> p.reduceStock(0));
        assertThrows(IllegalArgumentException.class, () -> p.reduceStock(-1));
        assertEquals(5, p.getStock());
        assertThrows(InsufficientStockException.class, () -> p.reduceStock(6));
        assertEquals(5, p.getStock());
    }
}
