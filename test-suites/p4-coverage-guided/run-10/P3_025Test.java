package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void repeatedPlacementsReuseIdAndCouponAndPriceShippingAfterDiscount() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999999999);
        Coupon oneOff = new Coupon("ONE", CouponType.FIXED, 1, 500_000, 0, t.toLocalDate());
        Product p = new Product("A", "A", 250_000, 500, 6);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        OrderService service = new OrderService();
        Order first = service.placeOrder("D", cart, oneOff, Region.INNER_CITY, t);
        assertEquals(4, p.getStock());
        service.pay(first);
        Order second = service.placeOrder("D", cart, oneOff, Region.INNER_CITY, t);
        assertEquals(2, p.getStock());
        Order third = service.placeOrder("D", cart, null, Region.INNER_CITY, t);
        assertEquals(0, p.getStock());

        for (Order discounted : List.of(first, second)) {
            assertEquals(500_000L, discounted.getSubtotal());
            assertEquals(1L, discounted.getDiscount());
            assertEquals(15_000L, discounted.getShipping());
            assertEquals(514_999L, discounted.getTotal());
        }
        assertEquals(0L, third.getDiscount());
        assertEquals(0L, third.getShipping());
        assertEquals(500_000L, third.getTotal());
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.PENDING, second.getStatus());
        assertEquals(OrderStatus.PENDING, third.getStatus());
        assertEquals(1, cart.getItems().size());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());

        cart.removeItem("A");
        assertEquals(0L, service.cancelOrder(second, t));
        assertEquals(2, p.getStock());
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.PENDING, third.getStatus());
        for (Order order : List.of(first, second, third)) {
            assertEquals("D", order.getId());
            assertEquals(t, order.getCreatedAt());
            assertEquals(1, order.getItems().size());
            assertSame(p, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
        }
    }
}
