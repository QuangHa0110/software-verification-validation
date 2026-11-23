package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_039Test {

    @Test
    void cartItemIndependentFromProductStock() {
        Product p = new Product("P1", "Bút", 1000L, 100, 0);
        CartItem item = new CartItem(p, 5);
        assertEquals(5, item.getQuantity());
        assertEquals(0, p.getStock());
    }
}
