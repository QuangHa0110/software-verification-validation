package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_008Test {

    @ParameterizedTest
    @ValueSource(ints = {-1, -10})
    void productConstructorNegativeStockThrowsException(int negativeStock) {
        assertThrows(IllegalArgumentException.class, () -> new Product("P01", "Book", 100000L, 250, negativeStock));
    }
}
