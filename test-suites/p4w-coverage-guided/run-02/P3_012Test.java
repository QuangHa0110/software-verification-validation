package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_012Test {

    @Test
    void cartItemConstructorValidAndGetters() {
        Product p = new Product("P1", "Sản phẩm", 30000L, 200, 10);
        CartItem item = new CartItem(p, 2);

        assertSame(p, item.getProduct());
        assertEquals(2, item.getQuantity());
        assertEquals(60000L, item.getLineTotal());
        assertEquals(10, p.getStock());
    }
}
