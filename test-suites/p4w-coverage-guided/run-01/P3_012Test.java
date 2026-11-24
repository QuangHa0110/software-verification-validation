package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -50})
    void productConstructorInvalidWeight(int invalidWeight) {
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Product", 1000L, invalidWeight, 5));
    }
}
