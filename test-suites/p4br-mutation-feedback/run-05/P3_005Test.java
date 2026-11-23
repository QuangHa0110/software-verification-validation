package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void stockOperationsCheckQuantityAndPreserveAllOtherProductFields() {
        Product product = new Product(" P ", " Pen ", 1, 1, 0);
        for (int invalid : new int[] {0, -1}) {
            assertThrows(IllegalArgumentException.class, () -> product.reduceStock(invalid));
            assertThrows(IllegalArgumentException.class, () -> product.restoreStock(invalid));
            assertEquals(0, product.getStock());
        }
        product.restoreStock(1);
        assertThrows(InsufficientStockException.class, () -> product.reduceStock(2));
        assertEquals(1, product.getStock());
        product.reduceStock(1);
        assertEquals(0, product.getStock());
        product.restoreStock(50);
        product.restoreStock(3);
        product.reduceStock(2);
        assertEquals(51, product.getStock());
        assertEquals(" P ", product.getId());
        assertEquals(" Pen ", product.getName());
        assertEquals(1L, product.getPrice());
        assertEquals(1, product.getWeightGrams());
    }
}
