package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void repeatedOrdersReuseIdCartAndCouponAndComputeShippingAfterDiscount() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product p = new Product("A", "A", 250_000, 500, 6);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 500_000, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order first = service.placeOrder("D", cart, null, Region.INNER_CITY, t);
        assertEquals(500_000L, first.getSubtotal());
        assertEquals(0L, first.getDiscount());
        assertEquals(0L, first.getShipping());
        assertEquals(500_000L, first.getTotal());
        assertEquals(4, p.getStock());

        Order second = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t.plusNanos(1));
        assertEquals(2, p.getStock());
        Order third = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t.plusNanos(2));
        for (Order order : List.of(second, third)) {
            assertEquals("D", order.getId());
            assertEquals(500_000L, order.getSubtotal());
            assertEquals(1L, order.getDiscount());
            assertEquals(15_000L, order.getShipping());
            assertEquals(514_999L, order.getTotal());
        }
        service.pay(second);
        assertEquals(OrderStatus.PENDING, first.getStatus());
        assertEquals(OrderStatus.PAID, second.getStatus());
        assertEquals(OrderStatus.PENDING, third.getStatus());
        assertEquals(t.plusNanos(1), second.getCreatedAt());
        assertEquals(t.plusNanos(2), third.getCreatedAt());
        assertThrows(InsufficientStockException.class,
                () -> service.placeOrder("D", cart, coupon, Region.INNER_CITY, t));
        assertEquals(0, p.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
