package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_030Test {

    @Test
    void cartItemConstructorNullProductThrows() {
        assertThrows(IllegalArgumentException.class, () -> new CartItem(null, 2));
    }
}
