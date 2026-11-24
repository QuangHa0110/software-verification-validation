package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void productReduceStockInsufficientStock() {
        Product p = new Product("P1", "Name", 1000L, 100, 5);
        assertThrows(InsufficientStockException.class, () -> p.reduceStock(6));
        assertEquals(5, p.getStock());
    }
}
