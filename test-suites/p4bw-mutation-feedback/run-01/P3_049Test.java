package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_049Test {

    @Test
    void cartGetItemsReturnsUnmodifiableListSnapshot() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Prod 1", 10000L, 100, 50);
        cart.addItem(p, 2);
        List<CartItem> items = cart.getItems();
        assertThrows(UnsupportedOperationException.class, () -> items.clear());
        assertThrows(UnsupportedOperationException.class, () -> items.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> items.add(new CartItem(p, 1)));
    }
}
