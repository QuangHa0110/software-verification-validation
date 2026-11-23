package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_023Test {

    @Test
    void latePaidRefundUsesLongDiscountedGoodsAndExcludesRemoteShipping() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 23, 59);
        Product p = new Product("A", "A", 1_000_000_000L, 501, 3);
        Cart cart = new Cart();
        cart.addItem(p, 3);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 2, 0, 1, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.REMOTE, t);
        assertEquals(3_000_000_000L, order.getSubtotal());
        assertEquals(2L, order.getDiscount());
        assertEquals(59_000L, order.getShipping());
        assertEquals(3_000_058_998L, order.getTotal());
        service.pay(order);
        assertEquals(2_399_999_998L, service.cancelOrder(order, t.plusHours(25)));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(3, p.getStock());
    }
}
