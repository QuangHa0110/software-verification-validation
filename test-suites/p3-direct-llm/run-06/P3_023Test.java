package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void packageStatusSetterAcceptsArbitraryTransitionsAndRejectsNull() {
        Product product = new Product("P", "P", 11, 1, 4);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = new Order("D", List.of(new CartItem(product, 2)), 22, 3, 7, 26, time, Region.SUBURB);

        for (OrderStatus status : List.of(OrderStatus.SHIPPED, OrderStatus.PENDING, OrderStatus.CANCELLED,
                                          OrderStatus.PAID, OrderStatus.PAID)) {
            order.setStatus(status);
            assertEquals(status, order.getStatus());
            assertThrows(IllegalArgumentException.class, () -> order.setStatus(null));
            assertEquals(status, order.getStatus());
            assertEquals(26L, order.getTotal());
            assertEquals(4, product.getStock());
            assertSame(product, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
        }
    }
}
