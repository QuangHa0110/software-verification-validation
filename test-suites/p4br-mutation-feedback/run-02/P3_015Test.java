package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @Test
    void updatesCheckCurrentStockEvenForUnchangedQuantityAndAllowZeroAtZeroStock() {
        Cart cart = new Cart();
        Product a = new Product("A", "A", 10, 1, 50);
        Product b = new Product("B", "B", 20, 1, 1);
        cart.addItem(a, 1);
        cart.addItem(b, 1);
        cart.updateQuantity("A", 50);
        assertEquals(50, cart.getItems().get(0).getQuantity());
        a.reduceStock(50);
        List<Runnable> failures = List.of(
            () -> assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("A", -1)),
            () -> assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("A", 51)),
            () -> assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("A", 50)),
            () -> assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("A", 1))
        );
        for (Runnable failure : failures) {
            failure.run();
            List<CartItem> lines = cart.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(50, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
            assertEquals(0, a.getStock());
            assertEquals(1, b.getStock());
        }
        cart.updateQuantity("A", 0);
        assertEquals(1, cart.getItems().size());
        assertSame(b, cart.getItems().get(0).getProduct());
        assertEquals(0, a.getStock());
    }
}
