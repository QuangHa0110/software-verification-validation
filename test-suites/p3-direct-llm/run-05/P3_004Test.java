package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_004Test {

    @Test
    void productConstructorRejectsEachInvalidFieldIndependently() {
        for (String blank : Arrays.asList(null, "", " \t\n")) {
            assertThrows(IllegalArgumentException.class, () -> new Product(blank, "Book", 1, 1, 0));
            assertThrows(IllegalArgumentException.class, () -> new Product("P", blank, 1, 1, 0));
        }
        for (int invalid : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> new Product("P", "Book", invalid, 1, 0));
            assertThrows(IllegalArgumentException.class, () -> new Product("P", "Book", 1, invalid, 0));
        }
        assertThrows(IllegalArgumentException.class, () -> new Product("P", "Book", 1, 1, -1));
    }
}
