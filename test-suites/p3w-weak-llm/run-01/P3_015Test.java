package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void productReduceStockInvalidQuantity(int q) {
        Product p = new Product("P1", "Name", 1000L, 100, 10);
        assertThrows(IllegalArgumentException.class, () -> p.reduceStock(q));
        assertEquals(10, p.getStock());
    }
}
