package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @ParameterizedTest
    @CsvSource({
        "false,0,0,0",
        "false,25,0,0",
        "true,0,0,514999",
        "true,24,-1,514999",
        "true,24,0,514999",
        "true,24,1,399999",
        "true,24,1000000000,399999"
    })
    void cancellationRefundRespectsPaymentAndTheExactNanosecondDeadline(
            boolean paid, long hours, long nanos, long expectedRefund) {
        LocalDateTime created = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 987_654_321);
        Product product = new Product("A", "A", 250_000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(product, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, created.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, created);
        if (paid) {
            service.pay(order);
        }
        assertEquals(0, product.getStock());
        LocalDateTime cancelled = created.plusHours(hours).plusNanos(nanos);
        assertEquals(expectedRefund, service.cancelOrder(order, cancelled));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(2, product.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, cancelled));
        assertEquals(2, product.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(created, order.getCreatedAt());
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15_000L, order.getShipping());
        assertEquals(514_999L, order.getTotal());
        assertSame(product, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
