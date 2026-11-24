package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_030Test {

    @Test
    void lateCancellationUsesLongMoneyAndRestoresOriginalProductsAfterCartReplacement() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product a = new Product("A", "original", 1_000_000_000L, 100, 4);
        Product b = new Product("B", "second", 5, 100, 5);
        Product replacement = new Product(new String("A"), "replacement", 2, 1, 20);
        Cart cart = new Cart();
        cart.addItem(a, 3);
        cart.addItem(b, 2);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 11, 0, 1, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.REMOTE, t);
        assertEquals(1, a.getStock());
        assertEquals(3, b.getStock());
        service.pay(order);
        cart.addItem(replacement, 1);
        cart.removeItem("B");
        a.restoreStock(7);
        b.reduceStock(1);

        assertEquals(2_399_999_999L, service.cancelOrder(order, t.plusDays(2)));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusDays(3)));
        assertEquals(11, a.getStock());
        assertEquals(4, b.getStock());
        assertEquals(20, replacement.getStock());
        List<CartItem> ordered = order.getItems();
        assertEquals(2, ordered.size());
        assertSame(a, ordered.get(0).getProduct());
        assertEquals(3, ordered.get(0).getQuantity());
        assertSame(b, ordered.get(1).getProduct());
        assertEquals(2, ordered.get(1).getQuantity());
        assertEquals(3_000_000_010L, order.getSubtotal());
        assertEquals(11L, order.getDiscount());
        assertEquals(55_000L, order.getShipping());
        assertEquals(3_000_054_999L, order.getTotal());
        List<CartItem> remaining = cart.getItems();
        assertEquals(1, remaining.size());
        assertSame(replacement, remaining.get(0).getProduct());
        assertEquals(4, remaining.get(0).getQuantity());
        assertEquals(8L, cart.getSubtotal());
    }
}
