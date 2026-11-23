package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_044Test {

    @Test
    void cartUpdateQuantityNotFoundThrows() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Prod 1", 10000L, 100, 50);
        cart.addItem(p, 2);
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("NON_EXISTENT", 5));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(null, 5));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("", 5));
    }
}
