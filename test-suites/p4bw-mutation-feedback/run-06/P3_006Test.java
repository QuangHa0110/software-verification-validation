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
    void productConstructorInvalidId(String id) {
        assertThrows(IllegalArgumentException.class, () -> new Product(id, "Book", 1000L, 100, 10));
        assertThrows(IllegalArgumentException.class, () -> new Product(null, "Book", 1000L, 100, 10));
    }
}
