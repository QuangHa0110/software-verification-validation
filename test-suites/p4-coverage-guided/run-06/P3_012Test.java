package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @ParameterizedTest
    @ValueSource(strings = {
        "nullProduct", "negativeAdd", "zeroAdd", "newOverLimit", "newShortage",
        "mergedOverLimit", "mergedShortage", "negativeMissing", "negativeExisting",
        "updateOverLimit", "updateShortage", "unchangedShortage"
    })
    void failedCartChangePreservesEveryLineAndStock(String operation) {
        Cart cart = new Cart();
        Product first = new Product("A", "A", 9, 7, 49);
        Product second = new Product("B", "B", 5, 11, 2);
        Product incoming = new Product("A", "replacement", 19, 13, 3);
        Product absent = new Product("C", "C", 23, 17, 0);
        cart.addItem(first, 49);
        cart.addItem(second, 2);
        second.reduceStock(1);

        switch (operation) {
            case "nullProduct" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
            case "negativeAdd" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(first, -1));
            case "zeroAdd" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(first, 0));
            case "newOverLimit" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(absent, 51));
            case "newShortage" -> assertThrows(InsufficientStockException.class, () -> cart.addItem(absent, 1));
            case "mergedOverLimit" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(incoming, 2));
            case "mergedShortage" -> assertThrows(InsufficientStockException.class, () -> cart.addItem(incoming, 1));
            case "negativeMissing" -> assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(null, -1));
            case "negativeExisting" -> assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("A", -1));
            case "updateOverLimit" -> assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("B", 51));
            case "updateShortage" -> assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("A", 50));
            case "unchangedShortage" -> assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 2));
            default -> fail(operation);
        }

        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertSame(first, items.get(0).getProduct());
        assertEquals(49, items.get(0).getQuantity());
        assertSame(second, items.get(1).getProduct());
        assertEquals(2, items.get(1).getQuantity());
        assertEquals(49, first.getStock());
        assertEquals(1, second.getStock());
        assertEquals(3, incoming.getStock());
        assertEquals(0, absent.getStock());
    }
}
