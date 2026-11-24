package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_007Test {

    @Test
    void productConstructorValidationOrder() {
        assertThrows(IllegalArgumentException.class, () -> new Product(null, "Tên", 100L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("", "Tên", 100L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("   ", "Tên", 100L, 10, 5));

        assertThrows(IllegalArgumentException.class, () -> new Product("P1", null, 100L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "", 100L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "  ", 100L, 10, 5));

        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Tên", 0L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Tên", -1L, 10, 5));

        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Tên", 100L, 0, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Tên", 100L, -1, 5));

        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Tên", 100L, 10, -1));
    }
}
