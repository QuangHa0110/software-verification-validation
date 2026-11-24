package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @Test
    void productReduceStockExceedsStockThrowsInsufficientStockException() {
        Product product = new Product("P01", "Book", 100000L, 250, 5);
        assertThrows(InsufficientStockException.class, () -> product.reduceStock(6));
        assertEquals(5, product.getStock());
    }
}
