package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_049Test {

    @Test
    void cartUpdateQuantityMissingOrEmptyIdThrowsNoSuchElement() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Bút", 1000L, 100, 10);
        cart.addItem(p, 2);

        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("P2", 5));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(null, 5));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("  ", 5));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("P2", 0));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("P2", 51));
    }
}
