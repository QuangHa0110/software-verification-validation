package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -50})
    void testProductConstructorInvalidWeight(int invalidWeight) {
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 100L, invalidWeight, 5));
    }
}
