package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_006Test {

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testProductConstructorInvalidId(String id) {
        assertThrows(IllegalArgumentException.class, () -> new Product(id, "Pen", 1000L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product(null, "Pen", 1000L, 10, 5));
    }
}
