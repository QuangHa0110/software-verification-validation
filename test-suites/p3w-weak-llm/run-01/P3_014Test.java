package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_014Test {

    @Test
    void productReduceStockSuccess() {
        Product p = new Product("P1", "Name", 1000L, 100, 10);
        p.reduceStock(4);
        assertEquals(6, p.getStock());
        p.reduceStock(6);
        assertEquals(0, p.getStock());
    }
}
