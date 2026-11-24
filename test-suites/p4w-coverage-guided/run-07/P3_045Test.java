package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_045Test {

    @Test
    void cartUpdateQuantityNullIdThrowsNoSuchElementException() {
        Cart cart = new Cart();
        Product p1 = new Product("P01", "Book", 100000L, 200, 30);
        cart.addItem(p1, 10);
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(null, 5));
    }
}
