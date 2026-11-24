package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_035Test {

    @Test
    void cartAddItemExceedsStockThrowsInsufficientStockException() {
        Cart cart = new Cart();
        Product p = new Product("P01", "Book", 100000L, 200, 5);
        assertThrows(InsufficientStockException.class, () -> cart.addItem(p, 6));
    }
}
