package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

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
    void cancellationRefundUsesExactDeadlineAndRestoresOriginalLinesOnce(
            boolean paid, int hours, long nanos, long expectedRefund) {
        LocalDateTime created = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 876_543_210);
        LocalDateTime cancelled = created.plusHours(hours).plusNanos(nanos);
        Product a = new Product("A", "First", 200_000, 400, 3);
        Product b = new Product("B", "Second", 100_000, 200, 1);
        Product replacement = new Product("A", "Replacement", 1, 2, 10);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 0, 0, created.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, created);
        if (paid) {
            service.pay(order);
        }
        assertEquals(1, a.getStock());
        assertEquals(0, b.getStock());

        cart.addItem(replacement, 1);
        cart.removeItem("B");
        a.restoreStock(4);
        b.restoreStock(3);
        assertEquals(expectedRefund, service.cancelOrder(order, cancelled));
        Runnable verify = () -> {
            assertEquals(OrderStatus.CANCELLED, order.getStatus());
            assertEquals(7, a.getStock());
            assertEquals(4, b.getStock());
            assertEquals(10, replacement.getStock());
            assertEquals(created, order.getCreatedAt());
            assertEquals(500_000L, order.getSubtotal());
            assertEquals(1L, order.getDiscount());
            assertEquals(15_000L, order.getShipping());
            assertEquals(514_999L, order.getTotal());
            List<CartItem> rows = order.getItems();
            assertEquals(2, rows.size());
            assertSame(a, rows.get(0).getProduct());
            assertEquals(2, rows.get(0).getQuantity());
            assertSame(b, rows.get(1).getProduct());
            assertEquals(1, rows.get(1).getQuantity());
            assertEquals(1, cart.getItems().size());
            assertSame(replacement, cart.getItems().get(0).getProduct());
            assertEquals(3, cart.getItems().get(0).getQuantity());
        };
        verify.run();
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, cancelled));
        verify.run();
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        verify.run();
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        verify.run();
    }
}
