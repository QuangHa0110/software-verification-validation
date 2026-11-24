package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_089Test {

    @Test
    void orderServicePlaceOrderFreeShippingThresholdExact() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P1", "Bút", 250000L, 500, 2);
        cart.addItem(p, 2);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Order order = service.placeOrder("D2", cart, null, Region.SUBURB, now);
        assertEquals(500000L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(500000L, order.getTotal());
        assertEquals(0, p.getStock());
    }
}
