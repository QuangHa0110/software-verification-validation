package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_002Test {

    @Test
    void testKillCartPutItemMaxQtyBoundary50Allowed() {
        Cart cart = new Cart();
        Product product = new Product("P1", "Product 1", 1000, 100, 100);
        assertDoesNotThrow(() -> cart.addItem(product, Cart.MAX_QTY_PER_ITEM));
        assertEquals(50, cart.getItems().get(0).getQuantity());
    }
}
