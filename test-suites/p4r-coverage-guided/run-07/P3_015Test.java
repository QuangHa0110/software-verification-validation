package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @ParameterizedTest
    @ValueSource(ints = {-1, 2, 3, 51})
    void updateChecksCurrentStockEvenForUnchangedQuantityAndChecksLimitFirst(int requested) {
        Product a = new Product("A", "First", 5, 2, 4);
        Product b = new Product("B", "Second", 9, 3, 2);
        Cart cart = new Cart();
        cart.addItem(a, 1);
        cart.addItem(b, 2);
        b.reduceStock(1);
        if (requested < 0 || requested > 50) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", requested));
        } else {
            assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", requested));
        }
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(1, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(2, lines.get(1).getQuantity());
        assertEquals(4, a.getStock());
        assertEquals(1, b.getStock());
    }
}
