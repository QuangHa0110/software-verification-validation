package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_053Test {

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "NON_EXISTENT"})
    void cartRemoveItemMissingIdThrowsNoSuchElement(String id) {
        Cart cart = new Cart();
        Product p = new Product("P1", "Bút", 1000L, 100, 10);
        cart.addItem(p, 2);

        assertThrows(NoSuchElementException.class, () -> cart.removeItem(id));
        assertEquals(1, cart.getItems().size());
    }
}
