package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_007Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -50})
    void productConstructorInvalidWeightThrowsException(int invalidWeight) {
        assertThrows(IllegalArgumentException.class, () -> new Product("P01", "Book", 100000L, invalidWeight, 10));
    }
}
