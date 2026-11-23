package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_015Test {

    @Test
    void cartAddItemInsufficientStockAndNewObjectPrecedence() {
        Cart cart = new Cart();
        Product pLow = new Product("P1", "Sản phẩm", 1000L, 100, 2);

        assertThrows(InsufficientStockException.class, () -> cart.addItem(pLow, 3));
        assertTrue(cart.isEmpty());

        Product pOld = new Product("P1", "Bản cũ", 9L, 100, 1);
        cart.addItem(pOld, 1);
        assertEquals(1, cart.getItems().get(0).getQuantity());
        assertEquals(9L, cart.getSubtotal());

        Product pNew = new Product("P1", "Bản mới", 12L, 100, 50);
        cart.addItem(pNew, 49);
        assertEquals(50, cart.getItems().get(0).getQuantity());
        assertEquals(600L, cart.getSubtotal());
        assertEquals("Bản mới", cart.getItems().get(0).getProduct().getName());
    }
}
