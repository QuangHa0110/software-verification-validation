package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @Test
    void cartItemInvalidArguments() {
        Product p = new Product("P1", "Item", 1000L, 200, 10);
        assertThrows(IllegalArgumentException.class, () -> new CartItem(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(p, 0));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(p, -1));
    }
}
