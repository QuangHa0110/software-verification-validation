package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @Test
    void productBoundaryValues() {
        Product p = new Product("P1", "Bút 1", 1L, 1, 0);
        assertEquals("P1", p.getId());
        assertEquals("Bút 1", p.getName());
        assertEquals(1L, p.getPrice());
        assertEquals(1, p.getWeightGrams());
        assertEquals(0, p.getStock());
    }
}
