package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_054Test {

    @Test
    void cartRemoveAndReAddPlacesItemAtEnd() {
        Cart cart = new Cart();
        Product p1 = new Product("P1", "Bút", 1000L, 100, 10);
        Product p2 = new Product("P2", "Vở", 2000L, 200, 10);
        cart.addItem(p1, 2);
        cart.addItem(p2, 3);

        cart.removeItem("P1");
        cart.addItem(p1, 1);

        List<CartItem> items = cart.getItems();
        assertEquals("P2", items.get(0).getProduct().getId());
        assertEquals("P1", items.get(1).getProduct().getId());
    }
}
