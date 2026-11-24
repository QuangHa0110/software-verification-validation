package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @ParameterizedTest
    @ValueSource(strings = {"nullProduct", "negative", "zero", "limit", "replacementStock", "newStock"})
    void rejectedAdditionPreservesEveryExistingLineAndStock(String scenario) {
        Product a = new Product("A", "Original", 5, 3, 50);
        Product b = new Product("B", "Second", 8, 7, 4);
        Product replacement = new Product("A", "Replacement", 90, 19, 3);
        Product absent = new Product("C", "Absent", 4, 11, 1);
        Cart cart = new Cart();
        cart.addItem(a, 49);
        cart.addItem(b, 2);

        switch (scenario) {
            case "nullProduct" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
            case "negative" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(b, -1));
            case "zero" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(b, 0));
            case "limit" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(replacement, 2));
            case "replacementStock" -> assertThrows(InsufficientStockException.class,
                () -> cart.addItem(replacement, 1));
            case "newStock" -> assertThrows(InsufficientStockException.class, () -> cart.addItem(absent, 2));
            default -> fail();
        }

        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(49, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(2, lines.get(1).getQuantity());
        assertEquals(50, a.getStock());
        assertEquals(4, b.getStock());
        assertEquals(3, replacement.getStock());
        assertEquals(1, absent.getStock());
    }
}
