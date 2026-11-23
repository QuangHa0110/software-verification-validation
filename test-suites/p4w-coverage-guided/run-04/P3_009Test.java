package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void productRestoreStockValidAndInvalid() {
        Product p = new Product("P1", "Item", 1000L, 100, 5);
        p.restoreStock(5);
        assertEquals(10, p.getStock());
        p.restoreStock(50);
        assertEquals(60, p.getStock());
        assertThrows(IllegalArgumentException.class, () -> p.restoreStock(0));
        assertThrows(IllegalArgumentException.class, () -> p.restoreStock(-2));
        assertEquals(60, p.getStock());
    }
}
