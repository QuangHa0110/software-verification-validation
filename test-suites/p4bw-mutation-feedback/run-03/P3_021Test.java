package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void productReduceStockSuccess() {
        Product p = new Product("P1", "Bút", 1000L, 10, 7);
        p.reduceStock(2);
        assertEquals(5, p.getStock());
        p.reduceStock(5);
        assertEquals(0, p.getStock());
    }
}
