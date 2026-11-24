package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_002Test {

    @Test
    void killOrderConstructorDiscountBoundaryMutant() {
        Product p = new Product("P1", "Product 1", 1000, 100, 10);
        CartItem item = new CartItem(p, 1);
        List<CartItem> items = List.of(item);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        // subtotal = 1000, discount = 0 (boundary discount == 0)
        assertDoesNotThrow(() -> new Order("O1", items, 1000, 0, 15000, 16000, now, Region.INNER_CITY));

        // subtotal = 1000, discount = 1000 (boundary discount == subtotal)
        assertDoesNotThrow(() -> new Order("O2", items, 1000, 1000, 15000, 15000, now, Region.INNER_CITY));
    }
}
