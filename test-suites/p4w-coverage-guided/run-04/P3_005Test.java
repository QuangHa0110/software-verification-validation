package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void productConstructorValidAndGettersPreserveWhitespace() {
        Product p = new Product(" P1 ", " Book 1 ", 30000L, 500, 7);
        assertEquals(" P1 ", p.getId());
        assertEquals(" Book 1 ", p.getName());
        assertEquals(30000L, p.getPrice());
        assertEquals(500, p.getWeightGrams());
        assertEquals(7, p.getStock());

        Product boundary = new Product("P0", "ZeroStock", 1L, 1, 0);
        assertEquals(0, boundary.getStock());
        assertEquals(1L, boundary.getPrice());
        assertEquals(1, boundary.getWeightGrams());
    }
}
