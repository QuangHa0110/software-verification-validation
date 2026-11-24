package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_006Test {

    @Test
    void productConstructorValidAndGettersPreserveWhitespace() {
        Product pA = new Product("P1", "Bút 1", 1L, 1, 0);
        assertEquals("P1", pA.getId());
        assertEquals("Bút 1", pA.getName());
        assertEquals(1L, pA.getPrice());
        assertEquals(1, pA.getWeightGrams());
        assertEquals(0, pA.getStock());

        Product pB = new Product(" P2 ", "Vở 2", 30000L, 500, 7);
        assertEquals(" P2 ", pB.getId());
        assertEquals("Vở 2", pB.getName());
        assertEquals(30000L, pB.getPrice());
        assertEquals(500, pB.getWeightGrams());
        assertEquals(7, pB.getStock());
    }
}
