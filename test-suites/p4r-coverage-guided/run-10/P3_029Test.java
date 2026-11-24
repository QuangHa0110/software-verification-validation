package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @ParameterizedTest
    @CsvSource({
        "false,0,0,0", "false,24,1,0", "false,240,0,0",
        "true,0,0,514999", "true,24,-1,514999", "true,24,0,514999",
        "true,24,1,399999", "true,24,1000000000,399999"
    })
    void cancellationRefundUsesExactElapsedTimeAndRestocksOnlyOnce(
            boolean paid, int hours, long nanos, long refund) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 17, 43, 765_432_100);
        Product product = new Product("P", "Pen", 250_000, 500, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 500_000, 0, t.toLocalDate());
        Cart cart = new Cart();
        cart.addItem(product, 2);
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        if (paid) {
            service.pay(order);
        }
        product.restoreStock(3);
        LocalDateTime cancellationTime = t.plusHours(hours).plusNanos(nanos);
        assertEquals(refund, service.cancelOrder(order, cancellationTime));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, product.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, cancellationTime));
        assertEquals(5, product.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15_000L, order.getShipping());
        assertEquals(514_999L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
        assertSame(product, order.getItems().get(0).getProduct());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertSame(product, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
