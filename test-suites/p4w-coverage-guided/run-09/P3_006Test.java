package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_006Test {

    @Test
    void productRestoreStockSuccessAndFailures() {
        Product p = new Product("P1", "Name", 100L, 10, 50);
        p.restoreStock(2);
        assertEquals(52, p.getStock());

        assertThrows(IllegalArgumentException.class, () -> p.restoreStock(0));
        assertThrows(IllegalArgumentException.class, () -> p.restoreStock(-5));
        assertEquals(52, p.getStock());
    }
}
