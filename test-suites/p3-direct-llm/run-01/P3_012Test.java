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
        "nullProduct", "negativeAdd", "zeroAdd", "newShort", "newOverLimit",
        "replacementShort", "combinedOverLimit", "largeAdd", "negativeUpdate",
        "missingUpdateZero", "missingUpdateLarge", "blankUpdate", "nullUpdate",
        "caseUpdate", "overLimitUpdate", "shortUpdate", "unchangedShortUpdate",
        "nullRemove", "emptyRemove", "blankRemove", "missingRemove", "trimmedRemove"
    })
    void rejectedCartMutationPreservesEveryLineAndProduct(String operation) {
        Cart cart = new Cart();
        Product a = new Product(" A ", "A", 9, 4, 50);
        Product b = new Product("B", "B", 7, 3, 2);
        Product replacement = new Product(" A ", "New A", 12, 8, 3);
        Product absent = new Product("C", "C", 1, 1, 0);
        cart.addItem(a, 49);
        cart.addItem(b, 2);
        a.reduceStock(2);

        switch (operation) {
            case "nullProduct" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
            case "negativeAdd" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, -1));
            case "zeroAdd" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, 0));
            case "newShort" -> assertThrows(InsufficientStockException.class, () -> cart.addItem(absent, 1));
            case "newOverLimit" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(absent, 51));
            case "replacementShort" -> assertThrows(InsufficientStockException.class, () -> cart.addItem(replacement, 1));
            case "combinedOverLimit" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(replacement, 2));
            case "largeAdd" -> assertThrows(IllegalArgumentException.class, () -> cart.addItem(a, Integer.MAX_VALUE));
            case "negativeUpdate" -> assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(null, -1));
            case "missingUpdateZero" -> assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("C", 0));
            case "missingUpdateLarge" -> assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("C", 51));
            case "blankUpdate" -> assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(" \t", 51));
            case "nullUpdate" -> assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(null, 0));
            case "caseUpdate" -> assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(" a ", 1));
            case "overLimitUpdate" -> assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(" A ", 51));
            case "shortUpdate" -> assertThrows(InsufficientStockException.class, () -> cart.updateQuantity("B", 3));
            case "unchangedShortUpdate" -> assertThrows(InsufficientStockException.class, () -> cart.updateQuantity(" A ", 49));
            case "nullRemove" -> assertThrows(NoSuchElementException.class, () -> cart.removeItem(null));
            case "emptyRemove" -> assertThrows(NoSuchElementException.class, () -> cart.removeItem(""));
            case "blankRemove" -> assertThrows(NoSuchElementException.class, () -> cart.removeItem("\t "));
            case "missingRemove" -> assertThrows(NoSuchElementException.class, () -> cart.removeItem("C"));
            case "trimmedRemove" -> assertThrows(NoSuchElementException.class, () -> cart.removeItem("A"));
            default -> fail(operation);
        }

        List<CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        assertSame(a, items.get(0).getProduct());
        assertEquals(49, items.get(0).getQuantity());
        assertSame(b, items.get(1).getProduct());
        assertEquals(2, items.get(1).getQuantity());
        assertEquals(48, a.getStock());
        assertEquals(2, b.getStock());
        assertEquals(3, replacement.getStock());
        assertEquals(0, absent.getStock());
    }
}
