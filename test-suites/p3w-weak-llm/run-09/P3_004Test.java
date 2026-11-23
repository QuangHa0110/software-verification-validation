package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_004Test {

    @Test
    void productConstructorInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Product(null, "Name", 100L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("", "Name", 100L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("   ", "Name", 100L, 10, 5));

        assertThrows(IllegalArgumentException.class, () -> new Product("P1", null, 100L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "", 100L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "  \t ", 100L, 10, 5));

        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 0L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", -1L, 10, 5));

        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 100L, 0, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 100L, -1, 5));

        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 100L, 10, -1));
    }
}
