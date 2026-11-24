package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_031Test {

    @Test
    void duplicateOrderIdsAndReusableCouponProduceIndependentOrdersUntilStockRunsOut() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product p = new Product("A", "First", 100, 1, 4);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 7, 200, 1, t.toLocalDate());
        OrderService service = new OrderService();
        Order first = service.placeOrder("D", cart, coupon, Region.SUBURB, t);
        assertEquals(2, p.getStock());
        Order second = service.placeOrder("D", cart, coupon, Region.SUBURB, t.plusNanos(1));
        assertEquals(0, p.getStock());
        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D", cart, coupon, Region.SUBURB, t));
        assertEquals(0, p.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        for (Order order : List.of(first, second)) {
            assertEquals("D", order.getId());
            assertEquals(7L, order.getDiscount());
            assertEquals(25_193L, order.getTotal());
            assertEquals(OrderStatus.PENDING, order.getStatus());
        }
        assertEquals(t, first.getCreatedAt());
        assertEquals(t.plusNanos(1), second.getCreatedAt());
        service.pay(first);
        assertEquals(OrderStatus.PENDING, second.getStatus());
        assertEquals(0L, service.cancelOrder(second, t.plusNanos(1)));
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.CANCELLED, second.getStatus());
        assertEquals(2, p.getStock());
    }
}
