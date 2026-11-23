package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void paymentAndShippingPermitOnlyTheirSpecifiedStartingStates(OrderStatus initial) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        OrderService service = new OrderService();
        for (boolean pay : new boolean[] {true, false}) {
            Product p = new Product("P", "P", 35, 10, 3);
            Cart cart = new Cart();
            cart.addItem(p, 2);
            Order order = service.placeOrder("D", cart, null, Region.INNER_CITY, t);
            order.setStatus(initial);
            if (pay && initial == OrderStatus.PENDING) {
                service.pay(order);
                assertEquals(OrderStatus.PAID, order.getStatus());
            } else if (!pay && initial == OrderStatus.PAID) {
                service.ship(order);
                assertEquals(OrderStatus.SHIPPED, order.getStatus());
            } else {
                assertThrows(IllegalStateException.class, () -> {
                    if (pay) {
                        service.pay(order);
                    } else {
                        service.ship(order);
                    }
                });
                assertEquals(initial, order.getStatus());
            }
            assertEquals(1, p.getStock());
            assertEquals(70L, order.getSubtotal());
            assertEquals(0L, order.getDiscount());
            assertEquals(15_000L, order.getShipping());
            assertEquals(15_070L, order.getTotal());
            assertEquals(t, order.getCreatedAt());
            assertEquals(Region.INNER_CITY, order.getRegion());
            assertEquals(1, order.getItems().size());
            assertSame(p, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
        }
    }
}
