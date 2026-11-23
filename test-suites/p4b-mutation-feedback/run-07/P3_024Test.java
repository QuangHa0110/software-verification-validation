package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void repeatedCheckoutReusesIdCartAndCouponWhileApplyingShippingThresholdAfterDiscount() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 12, 30, 40, 999999999);
        Product p = new Product("A", "First", 250_000, 500, 6);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        OrderService service = new OrderService();
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 500_000, 0, time.toLocalDate());
        Order first = service.placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        assertEquals(4, p.getStock());
        Order second = service.placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        assertEquals(2, p.getStock());
        Order third = service.placeOrder("D", cart, null, Region.INNER_CITY, time);
        assertEquals(0, p.getStock());
        for (Order order : List.of(first, second)) {
            assertEquals(500_000L, order.getSubtotal());
            assertEquals(1L, order.getDiscount());
            assertEquals(15_000L, order.getShipping());
            assertEquals(514_999L, order.getTotal());
        }
        assertEquals(500_000L, third.getSubtotal());
        assertEquals(0L, third.getDiscount());
        assertEquals(0L, third.getShipping());
        assertEquals(500_000L, third.getTotal());
        assertEquals(1, cart.getItems().size());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        service.pay(first);
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.PENDING, second.getStatus());
        assertEquals(OrderStatus.PENDING, third.getStatus());
        assertEquals(0L, service.cancelOrder(second, time));
        assertEquals(OrderStatus.PENDING, third.getStatus());
        assertEquals(2, p.getStock());
    }
}
