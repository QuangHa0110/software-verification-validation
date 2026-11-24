package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_038Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void cartItemInvalidQuantity(int qty) {
        Product p = new Product("P1", "Bút", 1000L, 100, 10);
        assertThrows(IllegalArgumentException.class, () -> new CartItem(p, qty));
    }
}
