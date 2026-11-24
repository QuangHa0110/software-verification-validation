package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void productConstructorZeroStockAllowed() {
        Product product = new Product("P01", "Book", 100000L, 250, 0);
        assertEquals(0, product.getStock());
    }
}
