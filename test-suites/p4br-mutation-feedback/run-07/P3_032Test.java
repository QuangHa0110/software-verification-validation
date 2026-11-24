package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_032Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void payAndShipEnforceTheirOwnStartingStatesAndPreserveMoneyLinesAndStock(OrderStatus initial) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 876_543_210);
        OrderService service = new OrderService();
        for (boolean paying : new boolean[] {true, false}) {
            Cart cart = new Cart();
            Product p = new Product("P", "Product", 123, 50, 2);
            cart.addItem(p, 2);
            Order order = service.placeOrder("D", cart, null, Region.INNER_CITY, t);
            order.setStatus(initial);
            OrderStatus expected = initial;
            if (paying && initial == OrderStatus.PENDING) {
                service.pay(order);
                expected = OrderStatus.PAID;
            } else if (!paying && initial == OrderStatus.PAID) {
                service.ship(order);
                expected = OrderStatus.SHIPPED;
            } else if (paying) {
                assertThrows(IllegalStateException.class, () -> service.pay(order));
            } else {
                assertThrows(IllegalStateException.class, () -> service.ship(order));
            }
            assertEquals(expected, order.getStatus());
            assertEquals("D", order.getId());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.INNER_CITY, order.getRegion());
            assertEquals(246L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(15_000L, order.getShipping());
            assertEquals(15_246L, order.getTotal());
            assertEquals(1, order.getItems().size());
            assertSame(p, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
            assertEquals(0, p.getStock());
        }
    }
}
