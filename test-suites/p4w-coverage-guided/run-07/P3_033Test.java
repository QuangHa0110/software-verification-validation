package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_033Test {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void cartAddItemNonPositiveQuantityThrowsException(int invalidQty) {
        Cart cart = new Cart();
        Product p = new Product("P01", "Book", 100000L, 200, 10);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(p, invalidQty));
    }
}
