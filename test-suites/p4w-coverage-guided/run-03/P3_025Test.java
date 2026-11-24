package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void productRestoreStockInvalidQuantity(int q) {
        Product p = new Product("P1", "Bút", 1000L, 10, 5);
        assertThrows(IllegalArgumentException.class, () -> p.restoreStock(q));
        assertEquals(5, p.getStock());
    }
}
