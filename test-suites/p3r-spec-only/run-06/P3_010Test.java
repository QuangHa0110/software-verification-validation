package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_010Test {

    @ParameterizedTest
    @ValueSource(strings = {
        "nullProduct", "negativeAdd", "zeroAdd", "newOverLimit", "newShort",
        "mergedOverLimit", "mergedShort", "negativeUpdate", "missingZero",
        "missingOverLimit", "nullUpdate", "blankUpdate", "existingOverLimit",
        "existingShort", "unchangedButShort", "nullRemove", "blankRemove", "unknownRemove"
    })
    void failedCartMutationPreservesEveryLineAndStock(String scenario) {
        Product a = new Product("A", "original", 10, 2, 49);
        Product b = new Product("B", "second", 20, 3, 2);
        Product replacement = new Product("A", "replacement", 99, 7, 3);
        Product other = new Product("C", "other", 5, 1, 0);
        Cart cart = new Cart();
        cart.addItem(a, 49);
        cart.addItem(b, 2);
        b.reduceStock(1);

        switch (scenario) {
            case "nullProduct" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
            case "negativeAdd" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, -1));
            case "zeroAdd" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, 0));
            case "newOverLimit" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(other, 51));
            case "newShort" -> assertThrows(InsufficientStockException.class, () -> cart.addItem(other, 1));
            case "mergedOverLimit" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(replacement, 2));
            case "mergedShort" -> assertThrows(InsufficientStockException.class, () -> cart.addItem(replacement, 1));
            case "negativeUpdate" -> assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(null, -1));
            case "missingZero" -> assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("a", 0));
            case "missingOverLimit" -> assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(" A ", 51));
            case "nullUpdate" -> assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(null, 1));
            case "blankUpdate" -> assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(" \t", 0));
            case "existingOverLimit" -> assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", 51));
            case "existingShort" -> assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 3));
            case "unchangedButShort" -> assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 2));
            case "nullRemove" -> assertThrows(NoSuchElementException.class, () -> cart.removeItem(null));
            case "blankRemove" -> assertThrows(NoSuchElementException.class, () -> cart.removeItem(" \t"));
            case "unknownRemove" -> assertThrows(NoSuchElementException.class, () -> cart.removeItem("b"));
            default -> throw new AssertionError(scenario);
        }

        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertSame(a, items.get(0).getProduct());
        assertEquals(49, items.get(0).getQuantity());
        assertSame(b, items.get(1).getProduct());
        assertEquals(2, items.get(1).getQuantity());
        assertEquals(530L, cart.getSubtotal());
        assertEquals(104, cart.getTotalWeightGrams());
        assertEquals(49, a.getStock());
        assertEquals(1, b.getStock());
        assertEquals(3, replacement.getStock());
        assertEquals(0, other.getStock());
    }
}
