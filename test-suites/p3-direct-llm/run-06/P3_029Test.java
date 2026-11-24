package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @Test
    void repeatedPlacementReusesCouponAndIdButCreatesIndependentOrders() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 10, 0);
        Cart cart = new Cart();
        Product p = new Product("P", "P", 100, 1, 4);
        cart.addItem(p, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 9, 200, 1, time.toLocalDate());
        OrderService service = new OrderService();
        Order first = service.placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        assertEquals(2, p.getStock());
        Order second = service.placeOrder("D", cart, coupon, Region.SUBURB, time.plusNanos(1));
        assertNotSame(first, second);
        assertEquals(0, p.getStock());
        assertEquals("D", first.getId());
        assertEquals("D", second.getId());
        assertEquals(9L, first.getDiscount());
        assertEquals(9L, second.getDiscount());
        assertEquals(15_191L, first.getTotal());
        assertEquals(25_191L, second.getTotal());
        assertEquals(time, first.getCreatedAt());
        assertEquals(time.plusNanos(1), second.getCreatedAt());
        service.pay(first);
        assertEquals(OrderStatus.PENDING, second.getStatus());
        assertEquals(0L, service.cancelOrder(second, time.plusNanos(1)));
        assertEquals(2, p.getStock());
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.CANCELLED, second.getStatus());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
