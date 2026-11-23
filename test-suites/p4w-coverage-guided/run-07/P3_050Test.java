package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_050Test {

    @Test
    void cartCalculatesSubtotalAndWeightForMultipleItems() {
        Cart cart = new Cart();
        Product p1 = new Product("P01", "Item1", 12000L, 200, 10);
        Product p2 = new Product("P02", "Item2", 7000L, 150, 10);
        cart.addItem(p1, 2);
        cart.addItem(p2, 3);
        assertEquals(45000L, cart.getSubtotal());
        assertEquals(850, cart.getTotalWeightGrams());
    }
}
