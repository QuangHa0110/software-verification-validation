package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_033Test {

    @Test
    void cartPutItemBoundaryQuantityFiftySucceeds() {
        Product product = new Product("P1", "Product 1", 10_000L, 200, 100);
        Cart cart = new Cart();
        cart.addItem(product, Cart.MAX_QTY_PER_ITEM);

        assertEquals(50, cart.getItems().get(0).getQuantity());
        assertEquals(500_000L, cart.getSubtotal());
    }
}
