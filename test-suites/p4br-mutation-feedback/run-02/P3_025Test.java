package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void packageStatusSetterAcceptsEveryTransitionAndRejectsNullWithoutSideEffects() {
        Product product = new Product("P", "P", 10, 1, 4);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = new Order("D", List.of(new CartItem(product, 1)), 10, 10, 0, 0,
            time, Region.SUBURB);
        for (OrderStatus from : OrderStatus.values()) {
            for (OrderStatus to : OrderStatus.values()) {
                order.setStatus(from);
                order.setStatus(to);
                assertEquals(to, order.getStatus());
                assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
                assertEquals(to, order.getStatus());
                assertEquals(0L, order.getTotal());
                assertEquals(4, product.getStock());
            }
        }
    }
}
