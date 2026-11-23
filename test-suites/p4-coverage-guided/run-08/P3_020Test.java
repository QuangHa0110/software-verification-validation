package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_020Test {

    @Test
    void repeatedPlacementsUsePostDiscountShippingAndCreateIndependentOrdersWithTheSameId() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 123);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 500_000, 0, t.toLocalDate());
        Product p = new Product("P", "P", 250_000, 500, 6);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        OrderService service = new OrderService();
        Order first = service.placeOrder(" O ", cart, coupon, Region.INNER_CITY, t);
        assertEquals(4, p.getStock());
        Order second = service.placeOrder(" O ", cart, null, Region.INNER_CITY, t.plusNanos(1));
        assertEquals(2, p.getStock());
        Order third = service.placeOrder(" O ", cart, coupon, Region.INNER_CITY, t.plusNanos(2));
        assertEquals(0, p.getStock());
        assertEquals(t, first.getCreatedAt());
        assertEquals(t.plusNanos(1), second.getCreatedAt());
        assertEquals(t.plusNanos(2), third.getCreatedAt());
        assertEquals(0L, second.getDiscount());
        assertEquals(0L, second.getShipping());
        assertEquals(500_000L, second.getTotal());
        for (Order discounted : List.of(first, third)) {
            assertEquals(1L, discounted.getDiscount());
            assertEquals(15_000L, discounted.getShipping());
            assertEquals(514_999L, discounted.getTotal());
        }
        assertEquals(1, cart.getItems().size());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        cart.removeItem("P");
        for (Order order : List.of(first, second, third)) {
            assertEquals(" O ", order.getId());
            assertEquals(Region.INNER_CITY, order.getRegion());
            assertEquals(500_000L, order.getSubtotal());
            assertEquals(OrderStatus.PENDING, order.getStatus());
            assertEquals(1, order.getItems().size());
            assertSame(p, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
        }
        service.pay(first);
        assertEquals(0L, service.cancelOrder(second, t.plusHours(1)));
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.CANCELLED, second.getStatus());
        assertEquals(OrderStatus.PENDING, third.getStatus());
        assertEquals(2, p.getStock());
    }
}
