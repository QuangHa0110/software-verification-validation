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
        "false,25,0,0",
        "true,0,0,514999",
        "true,24,-1,514999",
        "true,24,0,514999",
        "true,24,1,399999",
        "true,24,1000000000,399999"
    })
    void cancellationUsesExactTimeBoundaryAndRestoresSnapshotToCurrentStocks(
            boolean paid, int hours, long nanos, long refund) {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 123_456_789);
        Product a = new Product("A", "First", 200_000, 250, 3);
        Product b = new Product("B", "Second", 100_000, 500, 1);
        Product replacement = new Product("A", "Replacement", 1, 1, 8);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, time.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        if (paid) {
            service.pay(order);
        }
        assertEquals(1, a.getStock());
        assertEquals(0, b.getStock());
        a.restoreStock(3);
        cart.addItem(replacement, 1);
        cart.removeItem("B");

        LocalDateTime cancellation = time.plusHours(hours).plusNanos(nanos);
        assertEquals(refund, service.cancelOrder(order, cancellation));
        Runnable verify = () -> {
            assertEquals(OrderStatus.CANCELLED, order.getStatus());
            assertEquals(6, a.getStock());
            assertEquals(1, b.getStock());
            assertEquals(8, replacement.getStock());
            assertEquals(1, cart.getItems().size());
            assertSame(replacement, cart.getItems().get(0).getProduct());
            assertEquals(3, cart.getItems().get(0).getQuantity());
            List<CartItem> lines = order.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
            assertEquals(500_000L, order.getSubtotal());
            assertEquals(1L, order.getDiscount());
            assertEquals(15_000L, order.getShipping());
            assertEquals(514_999L, order.getTotal());
            assertEquals(time, order.getCreatedAt());
        };
        verify.run();
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, cancellation));
        verify.run();
        assertThrows(IllegalStateException.class, () -> service.pay(order));
        verify.run();
        assertThrows(IllegalStateException.class, () -> service.ship(order));
        verify.run();
    }
}
