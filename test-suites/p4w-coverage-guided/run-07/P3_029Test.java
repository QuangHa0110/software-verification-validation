package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void cartItemConstructorNonPositiveQuantityThrowsException(int invalidQty) {
        Product product = new Product("P01", "Book", 100000L, 250, 10);
        assertThrows(IllegalArgumentException.class, () -> new CartItem(product, invalidQty));
    }
}
