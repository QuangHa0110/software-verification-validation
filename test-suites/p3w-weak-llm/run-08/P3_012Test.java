package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void productConstructorInvalidPriceWeightStock() {
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 0L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", -1L, 10, 5));

        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 1000L, 0, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 1000L, -5, 5));

        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 1000L, 10, -1));
    }
}
