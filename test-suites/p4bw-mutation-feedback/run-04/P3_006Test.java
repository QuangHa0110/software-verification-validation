package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_006Test {

    @Test
    void productConstructorInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Product(null, "Name", 1000L, 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new Product("", "Name", 1000L, 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new Product("   ", "Name", 1000L, 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", null, 1000L, 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "", 1000L, 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "   ", 1000L, 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 0L, 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", -1L, 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 1000L, 0, 10));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 1000L, -1, 10));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 1000L, 100, -1));
    }
}
