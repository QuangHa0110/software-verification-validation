package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_073Test {

    @Test
    void orderServiceCancelOrderPaidAfter24HoursPartialRefundWithoutShipping() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Prod 1", 100000L, 200, 10);
        Cart cart = new Cart();
        cart.addItem(p, 1);
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);
        Order order = service.placeOrder("O1", cart, null, Region.INNER_CITY, now);
        service.pay(order);

        // subtotal = 100000, discount = 0, goods = 100000, 80% = 80000
        long refund = service.cancelOrder(order, now.plusHours(25));
        assertEquals(80000L, refund);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(10, p.getStock());
    }
}
