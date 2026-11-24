package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @Test
    void productReduceStock() {
        Product p = new Product("P1", "Sản phẩm 1", 1000L, 100, 7);

        assertThrows(IllegalArgumentException.class, () -> p.reduceStock(0));
        assertThrows(IllegalArgumentException.class, () -> p.reduceStock(-1));
        assertEquals(7, p.getStock());

        assertThrows(InsufficientStockException.class, () -> p.reduceStock(8));
        assertEquals(7, p.getStock());

        p.reduceStock(2);
        assertEquals(5, p.getStock());

        p.reduceStock(5);
        assertEquals(0, p.getStock());

        assertThrows(InsufficientStockException.class, () -> p.reduceStock(1));
        assertEquals(0, p.getStock());
    }
}
