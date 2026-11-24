package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_013Test {

    @Test
    void missingCartIdsAreRejectedBeforeZeroRemovalOrUpperLimitChecks() {
        Cart cart = new Cart();
        Product product = new Product(" A ", "A", 1, 2, 3);
        cart.addItem(product, 2);
        for (String id : Arrays.asList(null, "", "\t ", "A", " a ", "missing")) {
            assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(id, -1));
            for (int quantity : new int[]{0, 1, 51}) {
                assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(id, quantity));
                assertEquals(1, cart.getItems().size());
                assertSame(product, cart.getItems().get(0).getProduct());
                assertEquals(2, cart.getItems().get(0).getQuantity());
            }
            assertThrows(NoSuchElementException.class, () -> cart.removeItem(id));
            assertEquals(1, cart.getItems().size());
            assertSame(product, cart.getItems().get(0).getProduct());
            assertEquals(2, cart.getItems().get(0).getQuantity());
            assertEquals(3, product.getStock());
        }
    }
}
