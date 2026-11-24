package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @Test
    void productRestoreStockSuccess() {
        Product p = new Product("P1", "Name", 1000L, 100, 0);
        p.restoreStock(5);
        assertEquals(5, p.getStock());
        p.restoreStock(10);
        assertEquals(15, p.getStock());
    }
}
