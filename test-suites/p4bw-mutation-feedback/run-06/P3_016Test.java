package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_016Test {

    @Test
    void cartItemConstructorValidAndGetters() {
        Product p = new Product("P1", "Pen", 10000L, 20, 10);
        CartItem item = new CartItem(p, 3);
        assertSame(p, item.getProduct());
        assertEquals(3, item.getQuantity());
        assertEquals(30000L, item.getLineTotal());

        Product bigProduct = new Product("P2", "Gold", 1_000_000_000L, 100, 10);
        CartItem bigItem = new CartItem(bigProduct, 3);
        assertEquals(3_000_000_000L, bigItem.getLineTotal());
    }
}
