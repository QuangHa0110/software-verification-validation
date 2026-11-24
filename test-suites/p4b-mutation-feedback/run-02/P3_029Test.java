package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @Test
    void lateCancellationRestoresOriginalProductsToCurrentStockUsingFrozenQuantities() {
        Product original = new Product("A", "Original", 1_000_000_000L, 100, 5);
        Product b = new Product("B", "B", 10, 20, 4);
        Product replacement = new Product(new String("A"), "Replacement", 1, 1, 50);
        Cart cart = new Cart();
        cart.addItem(original, 3);
        cart.addItem(b, 2);
        LocalDateTime created = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 21, 0, 0, created.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.REMOTE, created);
        service.pay(order);
        assertEquals(2, original.getStock());
        assertEquals(2, b.getStock());

        cart.addItem(replacement, 1);
        cart.removeItem("B");
        original.restoreStock(7);
        b.reduceStock(1);
        LocalDateTime late = created.plusDays(2);
        assertEquals(2_399_999_999L, service.cancelOrder(order, late));
        assertEquals(12, original.getStock());
        assertEquals(3, b.getStock());
        assertEquals(50, replacement.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, late));
        assertEquals(12, original.getStock());
        assertEquals(3, b.getStock());
        assertEquals(50, replacement.getStock());

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(3_000_000_020L, order.getSubtotal());
        assertEquals(21L, order.getDiscount());
        assertEquals(55_000L, order.getShipping());
        assertEquals(3_000_054_999L, order.getTotal());
        List<CartItem> ordered = order.getItems();
        assertEquals(2, ordered.size());
        assertSame(original, ordered.get(0).getProduct());
        assertEquals(3, ordered.get(0).getQuantity());
        assertSame(b, ordered.get(1).getProduct());
        assertEquals(2, ordered.get(1).getQuantity());
        List<CartItem> remaining = cart.getItems();
        assertEquals(1, remaining.size());
        assertSame(replacement, remaining.get(0).getProduct());
        assertEquals(4, remaining.get(0).getQuantity());
        assertEquals(4L, cart.getSubtotal());
    }
}
