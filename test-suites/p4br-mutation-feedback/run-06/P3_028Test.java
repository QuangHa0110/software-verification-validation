package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @Test
    void lateCancellationUsesLongArithmeticAndRestoresOriginalProductsFromOrderSnapshot() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product a = new Product("A", "original", 1_000_000_000L, 100, 4);
        Product b = new Product("B", "second", 9, 80, 5);
        Product replacement = new Product("A", "replacement", 70, 50, 10);
        Cart cart = new Cart();
        cart.addItem(a, 3);
        cart.addItem(b, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.REMOTE, t);
        service.pay(order);
        assertEquals(1, a.getStock());
        assertEquals(3, b.getStock());

        cart.addItem(replacement, 1);
        cart.removeItem("B");
        a.restoreStock(7);
        b.reduceStock(1);
        assertEquals(2_400_000_013L, service.cancelOrder(order, t.plusDays(2)));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(11, a.getStock());
        assertEquals(4, b.getStock());
        assertEquals(10, replacement.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusDays(3)));
        assertEquals(11, a.getStock());
        assertEquals(4, b.getStock());
        assertEquals(10, replacement.getStock());

        List<CartItem> ordered = order.getItems();
        assertEquals(2, ordered.size());
        assertSame(a, ordered.get(0).getProduct());
        assertEquals(3, ordered.get(0).getQuantity());
        assertSame(b, ordered.get(1).getProduct());
        assertEquals(2, ordered.get(1).getQuantity());
        assertEquals(3_000_000_018L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(55_000L, order.getShipping());
        assertEquals(3_000_055_017L, order.getTotal());
        List<CartItem> current = cart.getItems();
        assertEquals(1, current.size());
        assertSame(replacement, current.get(0).getProduct());
        assertEquals(4, current.get(0).getQuantity());
        assertEquals(280L, cart.getSubtotal());
    }
}
