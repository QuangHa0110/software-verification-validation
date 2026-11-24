package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_014Test {

    @Test
    void productRestoreStockSuccessfully() {
        Product product = new Product("P01", "Book", 100000L, 250, 5);
        product.restoreStock(3);
        assertEquals(8, product.getStock());
    }
}
