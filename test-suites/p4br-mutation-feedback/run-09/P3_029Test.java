package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @Test
    void lateCancellationRestoresOriginalProductsAndQuantitiesAfterCartReplacement() {
        Product a = new Product("A", "Original", 1_000_000_000L, 100, 3);
        Product b = new Product("B", "Second", 9, 200, 4);
        Product replacement = new Product("A", "Replacement", 20, 1, 10);
        Cart cart = new Cart();
        cart.addItem(a, 3);
        cart.addItem(b, 2);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 18, 30);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, time.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.REMOTE, time);
        assertEquals(0, a.getStock());
        assertEquals(2, b.getStock());
        service.pay(order);
        cart.addItem(replacement, 1);
        cart.updateQuantity("B", 1);
        a.restoreStock(7);
        b.reduceStock(1);

        assertEquals(2_400_000_013L, service.cancelOrder(order, time.plusDays(2)));
        Runnable verify = () -> {
            assertEquals(OrderStatus.CANCELLED, order.getStatus());
            assertEquals(10, a.getStock());
            assertEquals(3, b.getStock());
            assertEquals(10, replacement.getStock());
            List<CartItem> frozen = order.getItems();
            assertEquals(2, frozen.size());
            assertSame(a, frozen.get(0).getProduct());
            assertEquals(3, frozen.get(0).getQuantity());
            assertSame(b, frozen.get(1).getProduct());
            assertEquals(2, frozen.get(1).getQuantity());
            assertEquals(3_000_000_018L, order.getSubtotal());
            assertEquals(1L, order.getDiscount());
            assertEquals(55000L, order.getShipping());
            assertEquals(3_000_055_017L, order.getTotal());
            List<CartItem> current = cart.getItems();
            assertEquals(2, current.size());
            assertSame(replacement, current.get(0).getProduct());
            assertEquals(4, current.get(0).getQuantity());
            assertSame(b, current.get(1).getProduct());
            assertEquals(1, current.get(1).getQuantity());
            assertEquals(89L, cart.getSubtotal());
        };
        verify.run();
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time.minusNanos(1)));
        verify.run();
    }
}
