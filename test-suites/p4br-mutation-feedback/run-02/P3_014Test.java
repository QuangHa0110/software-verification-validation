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
    @ValueSource(strings = {" \t ", "a", " A ", "missing"})
    void updateAndRemoveDistinguishInvalidQuantityFromMissingExactId(String missing) {
        Product a = new Product("A", "A", 10, 2, 3);
        Product b = new Product("B", "B", 20, 3, 4);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        List<Runnable> failures = List.of(
            () -> assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(missing, -1)),
            () -> assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(missing, 0)),
            () -> assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(missing, 51)),
            () -> assertThrows(NoSuchElementException.class, () -> cart.removeItem(missing))
        );
        for (Runnable failure : failures) {
            failure.run();
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(3, lines.get(1).getQuantity());
            assertEquals(3, a.getStock());
            assertEquals(4, b.getStock());
        }
    }
}
