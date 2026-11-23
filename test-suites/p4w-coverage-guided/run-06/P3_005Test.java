package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void productConstructorValidAndGettersPreserved() {
        Product p = new Product(" P1 ", " Bút Bi ", 15000L, 50, 100);
        assertEquals(" P1 ", p.getId());
        assertEquals(" Bút Bi ", p.getName());
        assertEquals(15000L, p.getPrice());
        assertEquals(50, p.getWeightGrams());
        assertEquals(100, p.getStock());

        Product boundary = new Product("ID", "Name", 1L, 1, 0);
        assertEquals(1L, boundary.getPrice());
        assertEquals(1, boundary.getWeightGrams());
        assertEquals(0, boundary.getStock());
    }
}
