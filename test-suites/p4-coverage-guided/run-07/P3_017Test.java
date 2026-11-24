package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @ParameterizedTest
    @CsvSource({"21,0,0,21", "23,0,0,23", "22,-1,5,28", "22,23,5,4", "22,1,-1,20", "22,1,5,25"})
    void orderConstructorRejectsInconsistentAmounts(long subtotal, long discount, long shipping, long total) {
        Product p = new Product("P", "Name", 11, 1, 2);
        List<CartItem> lines = List.of(new CartItem(p, 2));
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        assertThrows(IllegalArgumentException.class,
                () -> new Order("D", lines, subtotal, discount, shipping, total, time, Region.PROVINCE));
        assertEquals(2, p.getStock());
    }
}
