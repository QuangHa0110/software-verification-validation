package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_030Test {

    @Test
    void lateRefundUsesLongNetGoodsAndExcludesShippingOnLargeOrders() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product p = new Product("P", "P", 1_000_000_000L, 500, 3);
        Cart cart = new Cart();
        cart.addItem(p, 3);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.PROVINCE, t);
        service.pay(order);
        assertEquals(3_000_000_000L, order.getSubtotal());
        assertEquals(37_000L, order.getShipping());
        assertEquals(3_000_036_999L, order.getTotal());
        assertEquals(2_399_999_999L, service.cancelOrder(order, t.plusDays(2)));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(3, p.getStock());
    }
}
