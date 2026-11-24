package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void payAndShipEnforceTheirOwnStartingStatesAndPreserveOrderDetails(OrderStatus initial) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 91);
        Product p = new Product("P", "N", 13, 5, 3);
        List<CartItem> lines = List.of(new CartItem(p, 2));
        OrderService service = new OrderService();
        Order toPay = new Order("P", lines, 26, 7, 11, 30, t, Region.SUBURB);
        Order toShip = new Order("S", lines, 26, 7, 11, 30, t, Region.SUBURB);
        toPay.setStatus(initial);
        toShip.setStatus(initial);

        if (initial == OrderStatus.PENDING) {
            service.pay(toPay);
            assertEquals(OrderStatus.PAID, toPay.getStatus());
        } else {
            assertThrows(IllegalStateException.class, () -> service.pay(toPay));
            assertEquals(initial, toPay.getStatus());
        }
        assertEquals(3, p.getStock());
        if (initial == OrderStatus.PAID) {
            service.ship(toShip);
            assertEquals(OrderStatus.SHIPPED, toShip.getStatus());
        } else {
            assertThrows(IllegalStateException.class, () -> service.ship(toShip));
            assertEquals(initial, toShip.getStatus());
        }
        assertEquals(3, p.getStock());
        assertEquals("P", toPay.getId());
        assertEquals("S", toShip.getId());
        for (Order order : List.of(toPay, toShip)) {
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.SUBURB, order.getRegion());
            assertEquals(26L, order.getSubtotal());
            assertEquals(7L, order.getDiscount());
            assertEquals(11L, order.getShipping());
            assertEquals(30L, order.getTotal());
            assertEquals(1, order.getItems().size());
            assertSame(p, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
        }
    }
}
