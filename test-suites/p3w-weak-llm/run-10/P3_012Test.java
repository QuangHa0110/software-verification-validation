package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testProductConstructorInvalidId(String invalidId) {
        assertThrows(IllegalArgumentException.class, () -> new Product(invalidId, "Name", 100L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product(null, "Name", 100L, 10, 5));
    }
}
