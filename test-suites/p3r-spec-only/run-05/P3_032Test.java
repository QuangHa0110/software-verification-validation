package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_032Test {

    @ParameterizedTest
    @CsvSource({
        "false,0,0,0",
        "false,24,1,0",
        "true,0,0,514999",
        "true,24,-1,514999",
        "true,24,0,514999",
        "true,24,1,399999",
        "true,24,1000000000,399999"
    })
    void cancellationRefundUsesExactInclusiveDeadlineAndRestoresCurrentStockOnlyOnce(
            boolean paid, long hours, long nanos, long expectedRefund) {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 654_321_987);
        Product p = new Product("P", "P", 250_000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        if (paid) service.pay(order);
        assertEquals(0, p.getStock());
        p.restoreStock(3);
        LocalDateTime cancelledAt = t.plusHours(hours).plusNanos(nanos);
        assertEquals(expectedRefund, service.cancelOrder(order, cancelledAt));
        assertEquals(5, p.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, cancelledAt));
        assertEquals(5, p.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(500_000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15_000L, order.getShipping());
        assertEquals(514_999L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
        for (List<CartItem> rows : List.of(cart.getItems(), order.getItems())) {
            assertEquals(1, rows.size());
            assertSame(p, rows.get(0).getProduct());
            assertEquals(2, rows.get(0).getQuantity());
        }
    }
}
