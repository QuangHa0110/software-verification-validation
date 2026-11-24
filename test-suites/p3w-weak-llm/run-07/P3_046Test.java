package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_046Test {

    @Test
    void cartRemoveItemSuccessfully() {
        Cart cart = new Cart();
        Product p1 = new Product("P01", "Book", 100000L, 200, 30);
        Product p2 = new Product("P02", "Pen", 20000L, 50, 30);
        cart.addItem(p1, 2);
        cart.addItem(p2, 5);
        cart.removeItem("P01");
        assertEquals(1, cart.getItems().size());
        assertEquals("P02", cart.getItems().get(0).getProduct().getId());
    }
}
