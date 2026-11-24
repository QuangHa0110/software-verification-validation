package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_005Test {

    @Test
    void testProductConstructorValid() {
        Product p = new Product(" P1 ", " Bút bi ", 15000L, 50, 10);
        assertEquals(" P1 ", p.getId());
        assertEquals(" Bút bi ", p.getName());
        assertEquals(15000L, p.getPrice());
        assertEquals(50, p.getWeightGrams());
        assertEquals(10, p.getStock());
    }
}
