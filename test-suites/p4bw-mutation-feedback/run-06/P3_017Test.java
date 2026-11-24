package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_017Test {

    @Test
    void cartItemConstructorInvalid() {
        Product p = new Product("P1", "Pen", 10000L, 20, 10);
        assertThrows(IllegalArgumentException.class, () -> new CartItem(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(p, 0));
        assertThrows(IllegalArgumentException.class, () -> new CartItem(p, -1));
    }
}
