package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void repeatedPlacementUsesPostDiscountShippingAndCreatesIndependentOrdersWithTheSameId() {
        Product p = new Product("A", "A", 250000, 500, 6);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999999999);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 500000, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order first = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        assertEquals(4, p.getStock());
        Order second = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        assertEquals(2, p.getStock());
        Order third = service.placeOrder("D", cart, null, Region.INNER_CITY, t.plusNanos(1));
        assertEquals(0, p.getStock());
        for (Order discounted : List.of(first, second)) {
            assertEquals(500000L, discounted.getSubtotal());
            assertEquals(1L, discounted.getDiscount());
            assertEquals(15000L, discounted.getShipping());
            assertEquals(514999L, discounted.getTotal());
            assertEquals(t, discounted.getCreatedAt());
        }
        assertEquals(0L, third.getDiscount());
        assertEquals(0L, third.getShipping());
        assertEquals(500000L, third.getTotal());
        service.pay(first);
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.PENDING, second.getStatus());
        assertEquals(OrderStatus.PENDING, third.getStatus());
        assertEquals(1, cart.getItems().size());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertThrows(InsufficientStockException.class,
            () -> service.placeOrder("D", cart, coupon, Region.INNER_CITY, t.plusNanos(1)));
        assertEquals(0, p.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
