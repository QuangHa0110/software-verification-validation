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
        "false,48,0,0",
        "true,0,0,514999",
        "true,24,-1,514999",
        "true,24,0,514999",
        "true,24,1,399999",
        "true,24,1000000000,399999"
    })
    void cancellationRefundUsesPaymentStatusAndExactTwentyFourHourBoundary(
            boolean paid, int hours, long nanos, long expected) {
        Product product = new Product("A", "A", 250000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(product, 2);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0, 12, 345678901);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, time.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        if (paid) {
            service.pay(order);
        }
        assertEquals(0, product.getStock());
        product.restoreStock(3);
        LocalDateTime cancellation = time.plusHours(hours).plusNanos(nanos);
        assertEquals(expected, service.cancelOrder(order, cancellation));
        assertEquals(5, product.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, cancellation));
        assertEquals(5, product.getStock());
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, product.getStock());
        assertEquals(500000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(514999L, order.getTotal());
        assertEquals(time, order.getCreatedAt());
        for (List<CartItem> lines : List.of(order.getItems(), cart.getItems())) {
            assertEquals(1, lines.size());
            assertSame(product, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
        }
    }
}
