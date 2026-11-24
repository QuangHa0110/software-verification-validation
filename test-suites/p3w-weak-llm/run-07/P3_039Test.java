package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_039Test {

    @Test
    void cartUpdateQuantitySuccessfully() {
        Cart cart = new Cart();
        Product p1 = new Product("P01", "Book", 100000L, 200, 30);
        cart.addItem(p1, 10);
        cart.updateQuantity("P01", 15);
        assertEquals(15, cart.getItems().get(0).getQuantity());
        assertEquals(1500000L, cart.getSubtotal());
    }
}
