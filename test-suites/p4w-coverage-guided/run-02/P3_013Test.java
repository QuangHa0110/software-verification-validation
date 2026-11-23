package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @Test
    void cartItemConstructorValidation() {
        Product p = new Product("P1", "Sản phẩm", 1000L, 100, 5);

        assertThrows(IllegalArgumentException.class, () -> new CartItem(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(p, 0));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(p, -1));
    }
}
