package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_003Test {

    @Test
    void killOrderConstructorShippingBoundaryMutant() {
        Product p = new Product("P1", "Product 1", 1000, 100, 10);
        CartItem item = new CartItem(p, 1);
        List<CartItem> items = List.of(item);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0, 0);

        // shipping = 0 (boundary shipping == 0)
        assertDoesNotThrow(() -> new Order("O1", items, 1000, 0, 0, 1000, now, Region.INNER_CITY));
    }
}
