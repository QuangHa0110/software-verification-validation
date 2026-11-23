package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_018Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void productRestoreStockInvalidQuantity(int q) {
        Product p = new Product("P1", "Name", 1000L, 100, 5);
        assertThrows(IllegalArgumentException.class, () -> p.restoreStock(q));
        assertEquals(5, p.getStock());
    }
}
