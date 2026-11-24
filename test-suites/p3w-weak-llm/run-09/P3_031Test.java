package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_031Test {

    @Test
    void orderServicePlaceOrderFreeShippingThreshold() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P", "Item", 250000L, 500, 5);
        cart.addItem(p, 2);

        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("D2", cart, null, Region.SUBURB, time);

        assertEquals(500000L, order.getSubtotal());
        assertEquals(0L, order.getDiscount());
        assertEquals(0L, order.getShipping());
        assertEquals(500000L, order.getTotal());
        assertEquals(3, p.getStock());
    }
}
