package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @Test
    void latePaidCancellationUsesLongArithmeticAndRestoresOnlyOriginalSnapshotProducts() {
        Product a = new Product("A", "Original", 1_000_000_000L, 300, 5);
        Product b = new Product("B", "Second", 7, 400, 4);
        Product replacement = new Product(new String("A"), "New A", 9, 1, 20);
        Cart cart = new Cart();
        cart.addItem(a, 3);
        cart.addItem(b, 2);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 3, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.REMOTE, t);
        service.pay(order);
        assertEquals(2, a.getStock());
        assertEquals(2, b.getStock());
        cart.removeItem("B");
        cart.addItem(replacement, 1);
        a.restoreStock(8);
        b.reduceStock(1);

        assertEquals(2_400_000_008L, service.cancelOrder(order, t.plusDays(2)));
        Runnable verify = () -> {
            assertEquals(13, a.getStock());
            assertEquals(3, b.getStock());
            assertEquals(20, replacement.getStock());
            assertEquals(OrderStatus.CANCELLED, order.getStatus());
            assertEquals(3_000_000_014L, order.getSubtotal());
            assertEquals(3L, order.getDiscount());
            assertEquals(59000L, order.getShipping());
            assertEquals(3_000_059_011L, order.getTotal());
            List<CartItem> ordered = order.getItems();
            assertEquals(2, ordered.size());
            assertSame(a, ordered.get(0).getProduct());
            assertEquals(3, ordered.get(0).getQuantity());
            assertSame(b, ordered.get(1).getProduct());
            assertEquals(2, ordered.get(1).getQuantity());
            List<CartItem> current = cart.getItems();
            assertEquals(1, current.size());
            assertSame(replacement, current.get(0).getProduct());
            assertEquals(4, current.get(0).getQuantity());
            assertEquals(36L, cart.getSubtotal());
        };
        verify.run();
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusDays(3)));
        verify.run();
    }
}
