package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_031Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void cartItemConstructorInvalidQuantityThrows(int q) {
        Product p = new Product("P1", "Product 1", 25000L, 100, 10);
        assertThrows(IllegalArgumentException.class, () -> new CartItem(p, q));
    }
}
