package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void productRestoreStockSuccessAndExceptions() {
        Product p = new Product("P1", "Book", 1000L, 100, 5);
        p.restoreStock(3);
        assertEquals(8, p.getStock());
        p.restoreStock(100);
        assertEquals(108, p.getStock());

        assertThrows(IllegalArgumentException.class, () -> p.restoreStock(0));
        assertThrows(IllegalArgumentException.class, () -> p.restoreStock(-2));
        assertEquals(108, p.getStock());
    }
}
