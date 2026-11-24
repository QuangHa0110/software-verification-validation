package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_075Test {

    @ParameterizedTest
    @CsvSource({
        "-1, 60000",
        "60001, 60000"
    })
    void orderConstructorDiscountOutOfRangeThrowsException(long discount, long subtotal) {
        Product p = new Product("P01", "Book", 30000L, 500, 10);
        List<CartItem> items = List.of(new CartItem(p, 2));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        long total = subtotal - discount + 15000L;
        assertThrows(IllegalArgumentException.class, () ->
            new Order("ORD1", items, subtotal, discount, 15000L, total, now, Region.INNER_CITY)
        );
    }
}
