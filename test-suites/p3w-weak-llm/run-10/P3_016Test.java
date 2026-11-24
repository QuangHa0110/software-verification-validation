package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @ParameterizedTest
    @ValueSource(ints = {-1, -10})
    void testProductConstructorInvalidStock(int invalidStock) {
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Name", 100L, 10, invalidStock));
    }
}
