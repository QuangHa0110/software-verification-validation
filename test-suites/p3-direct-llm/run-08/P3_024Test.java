package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void latePaidRefundUsesLongArithmeticAndExcludesRemoteShipping() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product p = new Product("P", "P", 1_000_000_000L, 500, 3);
        Cart cart = new Cart();
        cart.addItem(p, 3);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("O", cart, coupon, Region.REMOTE, t);
        assertEquals(3_000_000_000L, order.getSubtotal());
        assertEquals(57_000L, order.getShipping());
        assertEquals(3_000_056_999L, order.getTotal());
        service.pay(order);
        assertEquals(2_399_999_999L, service.cancelOrder(order, t.plusDays(2)));
        assertEquals(3, p.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
