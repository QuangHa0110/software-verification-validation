package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_003Test {

    @Test
    void productRejectsInvalidIdentifiersNamesPricesWeightsAndStock() {
        for (String text : Arrays.asList(null, "", " \t\n")) {
            assertThrows(IllegalArgumentException.class, () -> new Product(text, "Name", 1, 1, 0));
            assertThrows(IllegalArgumentException.class, () -> new Product("P", text, 1, 1, 0));
        }
        for (int invalid : new int[]{-1, 0}) {
            assertThrows(IllegalArgumentException.class, () -> new Product("P", "Name", invalid, 1, 0));
            assertThrows(IllegalArgumentException.class, () -> new Product("P", "Name", 1, invalid, 0));
        }
        assertThrows(IllegalArgumentException.class, () -> new Product("P", "Name", 1, 1, -1));
    }
}
