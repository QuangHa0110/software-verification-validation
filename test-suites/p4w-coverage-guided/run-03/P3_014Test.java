package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_014Test {

    @Test
    void productValidCreationAndGettersPreserveWhitespace() {
        Product p = new Product(" P2 ", " Vở 2 ", 30000L, 500, 7);
        assertEquals(" P2 ", p.getId());
        assertEquals(" Vở 2 ", p.getName());
        assertEquals(30000L, p.getPrice());
        assertEquals(500, p.getWeightGrams());
        assertEquals(7, p.getStock());
    }
}
