package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void productReduceStockInsufficientStock() {
        Product p = new Product("P1", "Bút", 1000L, 10, 7);
        assertThrows(InsufficientStockException.class, () -> p.reduceStock(8));
        assertEquals(7, p.getStock());
    }
}
