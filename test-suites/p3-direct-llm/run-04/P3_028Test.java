package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @Test
    void latePaidRefundUsesLongArithmeticAndExcludesRemoteShipping() {
        Product product = new Product("P", "Name", 1_000_000_000L, 1, 3);
        Cart cart = new Cart();
        cart.addItem(product, 3);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, time.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.REMOTE, time);
        assertEquals(3_000_000_000L, order.getSubtotal());
        assertEquals(3_000_054_999L, order.getTotal());
        service.pay(order);
        assertEquals(2_399_999_999L, service.cancelOrder(order, time.plusDays(2)));
        assertEquals(3, product.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
