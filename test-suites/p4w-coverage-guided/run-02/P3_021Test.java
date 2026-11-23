package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_021Test {

    @Test
    void cartLargeAmountNoIntegerOverflow() {
        Cart cart = new Cart();
        Product p = new Product("P", "Đắt", 1000000000L, 10, 10);
        cart.addItem(p, 3);
        assertEquals(3000000000L, cart.getSubtotal());
    }
}
