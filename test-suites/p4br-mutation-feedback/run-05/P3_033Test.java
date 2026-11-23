package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_033Test {

    @Test
    void latePaidRefundUsesLongArithmeticOnDiscountedGoodsAndExcludesShipping() {
        Product p = new Product("P", "P", 1_000_000_000L, 1, 3);
        Cart cart = new Cart();
        cart.addItem(p, 3);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.REMOTE, t);
        service.pay(order);
        assertEquals(3_000_054_999L, order.getTotal());
        assertEquals(2_399_999_999L, service.cancelOrder(order, t.plusDays(2)));
        assertEquals(3, p.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
