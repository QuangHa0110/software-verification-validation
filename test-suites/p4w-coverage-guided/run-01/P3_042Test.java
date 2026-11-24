package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_042Test {

    @Test
    void cartUpdateQuantityToZeroRemovesItem() {
        Cart cart = new Cart();
        Product p = new Product("P1", "Prod 1", 10000L, 100, 50);
        cart.addItem(p, 2);
        cart.updateQuantity("P1", 0);
        assertTrue(cart.isEmpty());
    }
}
