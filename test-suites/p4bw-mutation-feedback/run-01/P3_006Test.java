package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_006Test {

    @Test
    void productConstructorSuccessAndGetters() {
        Product p = new Product(" P1 ", " Bút chì ", 15000L, 200, 10);
        assertEquals(" P1 ", p.getId());
        assertEquals(" Bút chì ", p.getName());
        assertEquals(15000L, p.getPrice());
        assertEquals(200, p.getWeightGrams());
        assertEquals(10, p.getStock());
    }
}
