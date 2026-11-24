package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @Test
    void cartUpdateQuantityNoSuchElement() {
        Cart cart = new Cart();
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("UNKNOWN", 5));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity("", 5));
        assertThrows(NoSuchElementException.class, () -> cart.updateQuantity(null, 5));
    }
}
