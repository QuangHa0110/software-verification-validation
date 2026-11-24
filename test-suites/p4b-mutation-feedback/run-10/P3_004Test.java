package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_004Test {

    @Test
    void productRejectsEachInvalidConstructorArgument() {
        for (String blank : Arrays.asList(null, "", " \t\r\n")) {
            assertThrows(IllegalArgumentException.class, () -> new Product(blank, "Name", 1, 1, 0));
            assertThrows(IllegalArgumentException.class, () -> new Product("ID", blank, 1, 1, 0));
        }
        for (int invalid : List.of(-1, 0)) {
            assertThrows(IllegalArgumentException.class, () -> new Product("ID", "Name", invalid, 1, 0));
            assertThrows(IllegalArgumentException.class, () -> new Product("ID", "Name", 1, invalid, 0));
        }
        assertThrows(IllegalArgumentException.class, () -> new Product("ID", "Name", 1, 1, -1));
    }
}
