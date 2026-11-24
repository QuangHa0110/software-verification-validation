package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void productReduceStockInvalidQuantity(int q) {
        Product p = new Product("P1", "Bút", 1000L, 10, 7);
        assertThrows(IllegalArgumentException.class, () -> p.reduceStock(q));
        assertEquals(7, p.getStock());
    }
}
