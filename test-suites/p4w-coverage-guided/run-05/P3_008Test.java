package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L})
    void testProductConstructorInvalidPrice(long price) {
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Pen", price, 10, 5));
    }
}
