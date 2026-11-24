package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_014Test {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t ", "missing", "a", " A "})
    void missingCartIdIsCheckedAfterNegativeQuantityAndBeforeZeroOrLimit(String missing) {
        Product a = new Product("A", "First", 4, 5, 6);
        Product b = new Product("B", "Second", 7, 8, 9);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        for (int quantity : new int[] {-1, 0, 51, 1}) {
            if (quantity < 0) {
                assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(missing, quantity));
            } else {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(missing, quantity));
            }
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(3, lines.get(1).getQuantity());
        }
        assertThrows(NoSuchElementException.class, () -> cart.removeItem(missing));
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(3, lines.get(1).getQuantity());
        assertEquals(6, a.getStock());
        assertEquals(9, b.getStock());
    }
}
