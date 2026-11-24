package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_033Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void cancellationValidatesNullTimeThenStatusThenChronologyWithoutChangingEitherStock(OrderStatus status) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 1);
        Product a = new Product("A", "First", 11, 3, 3);
        Product b = new Product("B", "Second", 17, 7, 5);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, null, Region.SUBURB, t);
        order.setStatus(status);
        for (LocalDateTime when : new LocalDateTime[] {null, t.minusNanos(1)}) {
            Class<? extends RuntimeException> expected = when != null &&
                (status == OrderStatus.SHIPPED || status == OrderStatus.CANCELLED)
                ? IllegalStateException.class : IllegalArgumentException.class;
            assertThrows(expected, () -> service.cancelOrder(order, when));
            assertEquals(status, order.getStatus());
            assertEquals(1, a.getStock());
            assertEquals(2, b.getStock());
            assertEquals(73L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(25_000L, order.getShipping());
            assertEquals(25_073L, order.getTotal());
            assertEquals(List.of(2, 3), order.getItems().stream().map(CartItem::getQuantity).toList());
        }
    }
}
