package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_014Test {

    @Test
    void productRestoreStockSuccessAndValidation() {
        Product product = new Product("P1", "Name", 1000L, 10, 50);
        assertThrows(IllegalArgumentException.class, () -> product.restoreStock(0));
        assertThrows(IllegalArgumentException.class, () -> product.restoreStock(-3));
        assertEquals(50, product.getStock());

        product.restoreStock(2);
        assertEquals(52, product.getStock());
    }
}
