package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @Test
    void productReduceStockSuccessAndValidation() {
        Product product = new Product("P1", "Name", 1000L, 10, 7);
        assertThrows(IllegalArgumentException.class, () -> product.reduceStock(0));
        assertThrows(IllegalArgumentException.class, () -> product.reduceStock(-1));
        assertEquals(7, product.getStock());

        assertThrows(InsufficientStockException.class, () -> product.reduceStock(8));
        assertEquals(7, product.getStock());

        product.reduceStock(2);
        assertEquals(5, product.getStock());
        product.reduceStock(5);
        assertEquals(0, product.getStock());
        assertThrows(InsufficientStockException.class, () -> product.reduceStock(1));
    }
}
