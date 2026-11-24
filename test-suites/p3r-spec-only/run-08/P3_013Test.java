package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @Test
    void failedRemovalByBlankUnknownOrMismatchedIdPreservesAllLines() {
        Product a = new Product(" A ", "First", 5, 2, 2);
        Product b = new Product("B", "Second", 7, 3, 4);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        for (String missing : Arrays.asList(null, "", " \t", "C", " A", "A", "b")) {
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(missing));
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(3, lines.get(1).getQuantity());
            assertEquals(2, a.getStock());
            assertEquals(4, b.getStock());
        }
    }
}
