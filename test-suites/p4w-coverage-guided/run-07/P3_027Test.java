package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @Test
    void cartItemConstructorValidComputesLineTotal() {
        Product product = new Product("P01", "Book", 100000L, 250, 10);
        CartItem item = new CartItem(product, 3);
        assertSame(product, item.getProduct());
        assertEquals(3, item.getQuantity());
        assertEquals(300000L, item.getLineTotal());
    }
}
