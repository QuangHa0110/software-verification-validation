package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_001Test {

    @Test
    void productConstructorValidInitializesFieldsProperly() {
        Product product = new Product("P01", "Book", 100000L, 250, 10);
        assertEquals("P01", product.getId());
        assertEquals("Book", product.getName());
        assertEquals(100000L, product.getPrice());
        assertEquals(250, product.getWeightGrams());
        assertEquals(10, product.getStock());
    }
}
