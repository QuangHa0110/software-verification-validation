package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @Test
    void productReduceStockSuccessfully() {
        Product product = new Product("P01", "Book", 100000L, 250, 10);
        product.reduceStock(4);
        assertEquals(6, product.getStock());
        product.reduceStock(6);
        assertEquals(0, product.getStock());
    }
}
