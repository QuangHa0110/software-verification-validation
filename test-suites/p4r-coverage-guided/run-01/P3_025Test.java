package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void repeatedPlacementWithSameIdCartAndCouponCreatesIndependentOrders() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product p = new Product("P", "P", 30, 1, 4);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 5, 60, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order first = service.placeOrder("D", cart, coupon, Region.SUBURB, t);
        assertEquals(2, p.getStock());
        Order second = service.placeOrder("D", cart, coupon, Region.SUBURB, t.plusNanos(1));
        assertNotSame(first, second);
        assertEquals(0, p.getStock());
        for (Order order : List.of(first, second)) {
            assertEquals("D", order.getId());
            assertEquals(5L, order.getDiscount());
            assertEquals(25055L, order.getTotal());
            assertSame(p, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
        }
        assertEquals(t, first.getCreatedAt());
        assertEquals(t.plusNanos(1), second.getCreatedAt());
        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D", cart, coupon, Region.SUBURB, t));
        assertEquals(0, p.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        service.pay(first);
        assertEquals(OrderStatus.PENDING, second.getStatus());
        assertEquals(0L, service.cancelOrder(second, second.getCreatedAt()));
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.CANCELLED, second.getStatus());
        assertEquals(2, p.getStock());
    }
}
