package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_011Test {

    @ParameterizedTest
    @ValueSource(strings = {"nullProduct", "zero", "negative", "newShort", "newOverLimit",
                            "mergedShort", "mergedOverLimit"})
    void failedAddPreservesEveryExistingLineAndProduct(String scenario) {
        Cart cart = new Cart();
        Product a = new Product("A", "Old", 9, 2, 50);
        Product b = new Product("B", "Second", 3, 4, 4);
        Product incoming = new Product("A", "New", 12, 5, 3);
        Product fresh = new Product("C", "New ID", 1, 1, 0);
        cart.addItem(a, 49);
        cart.addItem(b, 2);

        switch (scenario) {
            case "nullProduct" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
            case "zero" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, 0));
            case "negative" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, -1));
            case "newShort" -> assertThrows(InsufficientStockException.class, () -> cart.addItem(fresh, 1));
            case "newOverLimit" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(fresh, 51));
            case "mergedShort" -> assertThrows(InsufficientStockException.class, () -> cart.addItem(incoming, 1));
            case "mergedOverLimit" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(incoming, 2));
            default -> fail(scenario);
        }

        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(49, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(2, lines.get(1).getQuantity());
        assertEquals(50, a.getStock());
        assertEquals(4, b.getStock());
        assertEquals(3, incoming.getStock());
        assertEquals(0, fresh.getStock());
    }
}
