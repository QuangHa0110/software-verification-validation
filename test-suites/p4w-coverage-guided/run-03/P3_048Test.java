package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_048Test {

    @Test
    void cartUpdateQuantityNegativeThrowsIllegalArgumentBeforeLookup() {
        Cart cart = new Cart();
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity("NON_EXISTING", -1));
        assertThrows(IllegalArgumentException.class, () -> cart.updateQuantity(null, -1));
    }
}
