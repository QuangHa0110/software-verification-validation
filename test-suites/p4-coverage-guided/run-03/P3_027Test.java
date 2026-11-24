package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @Test
    void latePaidRefundUsesLongGoodsValueAndExcludesRemoteShipping() {
        Product p = new Product("P", "P", 1_000_000_000L, 400, 4);
        Cart cart = new Cart();
        cart.addItem(p, 3);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.REMOTE, t);
        assertEquals(3_000_000_000L, order.getSubtotal());
        assertEquals(57_000L, order.getShipping());
        assertEquals(3_000_056_999L, order.getTotal());
        service.pay(order);
        assertEquals(1, p.getStock());
        assertEquals(2_399_999_999L, service.cancelOrder(order, t.plusDays(2)));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(4, p.getStock());
    }
}
