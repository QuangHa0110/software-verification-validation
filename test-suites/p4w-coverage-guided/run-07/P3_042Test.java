package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_042Test {

    @Test
    void cartUpdateQuantityExceedsMax50ThrowsIllegalArgumentException() {
        Cart cart = new Cart();
        Product p1 = new Product("P01", "Book", 100000L, 200, 60);
        cart.addItem(p1, 10);
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("P01", 51));
    }
}
