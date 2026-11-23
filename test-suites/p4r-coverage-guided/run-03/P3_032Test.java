package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_032Test {

    @Test
    void lateCancellationRestoresOriginalProductsFromSnapshotAndUsesLongRefundArithmetic() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product a = new Product("A", "Original", 1_000_000_000L, 100, 7);
        Product b = new Product("B", "Second", 19, 200, 8);
        Product replacement = new Product("A", "Replacement", 2, 3, 20);
        Cart cart = new Cart();
        cart.addItem(a, 3);
        cart.addItem(b, 2);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 5, 0, 1, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.REMOTE, t);
        assertEquals(4, a.getStock());
        assertEquals(6, b.getStock());
        service.pay(order);

        cart.addItem(replacement, 1);
        cart.removeItem("B");
        a.restoreStock(9);
        b.reduceStock(1);
        assertEquals(13, a.getStock());
        assertEquals(5, b.getStock());

        assertEquals(2_400_000_026L, service.cancelOrder(order, t.plusHours(25)));
        Runnable verify = () -> {
            assertEquals(OrderStatus.CANCELLED, order.getStatus());
            assertEquals(16, a.getStock());
            assertEquals(7, b.getStock());
            assertEquals(20, replacement.getStock());
            assertEquals(3_000_000_038L, order.getSubtotal());
            assertEquals(5L, order.getDiscount());
            assertEquals(55_000L, order.getShipping());
            assertEquals(3_000_055_033L, order.getTotal());
            List<CartItem> lines = order.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(3, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertEquals(1, cart.getItems().size());
            assertSame(replacement, cart.getItems().get(0).getProduct());
            assertEquals(4, cart.getItems().get(0).getQuantity());
            assertEquals(8L, cart.getSubtotal());
            assertEquals(12, cart.getTotalWeightGrams());
        };
        verify.run();
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusHours(26)));
        verify.run();
    }
}
