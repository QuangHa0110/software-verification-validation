package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @ParameterizedTest
    @ValueSource(ints = {-1, -10})
    void productConstructorInvalidStock(int invalidStock) {
        assertThrows(IllegalArgumentException.class, () -> new Product("P1", "Product", 1000L, 100, invalidStock));
    }
}
