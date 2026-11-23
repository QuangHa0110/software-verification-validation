package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @Test
    void latePaidRefundUsesLongNetGoodsAndExcludesRemoteDeliveryFee() {
        Product product = new Product("P", "Book", 1_000_000_000L, 500, 3);
        Cart cart = new Cart();
        cart.addItem(product, 3);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 12, 30);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, time.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.REMOTE, time);
        assertEquals(3_000_000_000L, order.getSubtotal());
        assertEquals(57_000L, order.getShipping());
        assertEquals(3_000_056_999L, order.getTotal());
        service.pay(order);
        assertEquals(2_399_999_999L, service.cancelOrder(order, time.plusDays(2)));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(3, product.getStock());
    }
}
