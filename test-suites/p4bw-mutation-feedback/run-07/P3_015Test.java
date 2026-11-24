package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void productRestoreStockNonPositiveThrowsException(int invalidQty) {
        Product product = new Product("P01", "Book", 100000L, 250, 5);
        assertThrows(IllegalArgumentException.class, () -> product.restoreStock(invalidQty));
        assertEquals(5, product.getStock());
    }
}
