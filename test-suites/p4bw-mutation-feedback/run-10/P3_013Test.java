package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void testProductConstructorInvalidName(String invalidName) {
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", invalidName, 100L, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", null, 100L, 10, 5));
    }
}
