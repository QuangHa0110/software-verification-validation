package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P4b_003Test {

    @Test
    void orderConstructorDiscountNegativeFailsEvenWhenSubtotalMatches() {
        Product product = new Product("P1", "Product 1", 1000, 100, 10);
        CartItem item = new CartItem(product, 1);
        List<CartItem> items = List.of(item);
        assertThrows(IllegalArgumentException.class, () ->
            new Order("O1", items, 1000, -1, 15000, 16001, LocalDateTime.of(2026, 11, 23, 8, 0), Region.INNER_CITY)
        );
    }
}
