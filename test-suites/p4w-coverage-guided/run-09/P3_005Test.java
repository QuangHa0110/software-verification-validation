package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void productReduceStockSuccessAndFailures() {
        Product p = new Product("P1", "Name", 100L, 10, 7);
        p.reduceStock(2);
        assertEquals(5, p.getStock());
        p.reduceStock(5);
        assertEquals(0, p.getStock());

        assertThrows(IllegalArgumentException.class, () -> p.reduceStock(0));
        assertThrows(IllegalArgumentException.class, () -> p.reduceStock(-1));

        assertThrows(InsufficientStockException.class, () -> p.reduceStock(1));
        assertEquals(0, p.getStock());

        Product p2 = new Product("P2", "Name2", 50L, 10, 1);
        assertThrows(InsufficientStockException.class, () -> p2.reduceStock(2));
        assertEquals(1, p2.getStock());
    }
}
