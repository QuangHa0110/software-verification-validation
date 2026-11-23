package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_009Test {

    @Test
    void productRestoreStock() {
        Product p = new Product("P1", "Sản phẩm 1", 1000L, 100, 50);

        assertThrows(IllegalArgumentException.class, () -> p.restoreStock(0));
        assertThrows(IllegalArgumentException.class, () -> p.restoreStock(-2));
        assertEquals(50, p.getStock());

        p.restoreStock(2);
        assertEquals(52, p.getStock());

        Product pZero = new Product("P2", "Sản phẩm 2", 500L, 50, 0);
        pZero.restoreStock(10);
        assertEquals(10, pZero.getStock());
    }
}
