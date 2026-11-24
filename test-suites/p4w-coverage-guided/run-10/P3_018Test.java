package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @Test
    void testProductReduceStockInvalidQuantity() {
        Product p = new Product("P1", "Name", 100L, 10, 7);
        assertThrows(IllegalArgumentException.class, () -> p.reduceStock(0));
        assertThrows(IllegalArgumentException.class, () -> p.reduceStock(-1));
        assertEquals(7, p.getStock());
    }
}
