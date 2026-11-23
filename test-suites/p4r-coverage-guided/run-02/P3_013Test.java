package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @Test
    void failedAddsPreserveEveryLineAndCheckCombinedLimitBeforeIncomingStock() {
        Cart cart = new Cart();
        Product a = new Product("A", "original", 9, 1, 60);
        Product b = new Product("B", "second", 11, 2, 2);
        Product scarce = new Product(new String("A"), "replacement", 17, 3, 3);
        Product empty = new Product("C", "empty", 1, 1, 0);
        cart.addItem(a, 49);
        cart.addItem(b, 2);
        List<Runnable> failures = List.of(
            () -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1)),
            () -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, -1)),
            () -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, 0)),
            () -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(scarce, 2)),
            () -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(empty, 51)),
            () -> assertThrows(InsufficientStockException.class, () -> cart.addItem(scarce, 1)),
            () -> assertThrows(InsufficientStockException.class, () -> cart.addItem(empty, 1))
        );
        for (Runnable failure : failures) {
            failure.run();
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(49, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertEquals(60, a.getStock());
            assertEquals(2, b.getStock());
            assertEquals(3, scarce.getStock());
            assertEquals(0, empty.getStock());
        }
    }
}
