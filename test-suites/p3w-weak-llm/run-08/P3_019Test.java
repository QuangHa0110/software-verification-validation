package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_019Test {

    @Test
    void cartItemConstructorSuccessAndGetters() {
        Product p1 = new Product("P1", "Bút 1", 1L, 1, 0);
        CartItem itemA = new CartItem(p1, 1);
        assertSame(p1, itemA.getProduct());
        assertEquals(1, itemA.getQuantity());
        assertEquals(1L, itemA.getLineTotal());

        Product p2 = new Product("P2", "Vở 2", 30000L, 500, 7);
        CartItem itemB = new CartItem(p2, 2);
        assertSame(p2, itemB.getProduct());
        assertEquals(2, itemB.getQuantity());
        assertEquals(60000L, itemB.getLineTotal());

        Product pLarge = new Product("P3", "Laptop", 1000000000L, 2000, 10);
        CartItem itemLarge = new CartItem(pLarge, 3);
        assertEquals(3000000000L, itemLarge.getLineTotal());
    }
}
