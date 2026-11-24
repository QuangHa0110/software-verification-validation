package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_037Test {

    @Test
    void cartAddItemCumulativeQuantityExceedsUpdatedStockThrowsException() {
        Cart cart = new Cart();
        Product p1 = new Product("P01", "Book", 100000L, 200, 30);
        cart.addItem(p1, 20);
        Product p1UpdatedStock = new Product("P01", "Book", 100000L, 200, 25);
        assertThrows(InsufficientStockException.class, () -> cart.addItem(p1UpdatedStock, 10));
        assertEquals(20, cart.getItems().get(0).getQuantity());
    }
}
