package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @ParameterizedTest
    @CsvSource(value = {
        "NULL,-1,argument", "'   ',-1,argument", "missing,-1,argument", "A,-1,argument",
        "NULL,0,missing", "'',0,missing", "'   ',51,missing", "missing,0,missing",
        "missing,51,missing", "a,1,missing", "' A ',1,missing",
        "A,51,argument", "A,3,stock", "A,2,stock"
    }, nullValues = "NULL")
    void failedQuantityUpdateChecksErrorsInOrderAndPreservesCart(String id, int qty, String error) {
        Product a = new Product("A", "First", 5, 2, 2);
        Product b = new Product("B", "Second", 7, 3, 4);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        a.reduceStock(1);

        Class<? extends RuntimeException> expected = switch (error) {
            case "argument" -> IllegalArgumentException.class;
            case "missing" -> NoSuchElementException.class;
            case "stock" -> InsufficientStockException.class;
            default -> throw new AssertionError(error);
        };
        assertThrows(expected, () -> cart.updateQuantity(id, qty));
        List<CartItem> lines = cart.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(2, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(3, lines.get(1).getQuantity());
        assertEquals(1, a.getStock());
        assertEquals(4, b.getStock());
    }
}
