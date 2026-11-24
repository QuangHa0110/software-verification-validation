package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @ParameterizedTest
    @ValueSource(ints = {-1, -10})
    void testProductConstructorInvalidStock(int stock) {
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Pen", 1000L, 10, stock));
    }
}
