package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void productRestoreStockSuccess() {
        Product p = new Product("P1", "Bút", 1000L, 10, 50);
        p.restoreStock(2);
        assertEquals(52, p.getStock());
    }
}
