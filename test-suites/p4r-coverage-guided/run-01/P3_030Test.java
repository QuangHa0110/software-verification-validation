package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_030Test {

    @Test
    void lateCancellationRestoresOriginalProductsAndQuantitiesAfterCartReplacementAndRemoval() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product original = new Product("A", "old", 1_000_000_000L, 100, 5);
        Product b = new Product("B", "B", 4, 20, 3);
        Product replacement = new Product("A", "new", 7, 10, 50);
        Cart cart = new Cart();
        cart.addItem(original, 3);
        cart.addItem(b, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 9, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.PROVINCE, t);
        service.pay(order);
        assertEquals(2, original.getStock());
        assertEquals(1, b.getStock());
        cart.addItem(replacement, 1);
        cart.removeItem("B");
        original.restoreStock(7);
        b.reduceStock(1);
        assertEquals(2_399_999_999L, service.cancelOrder(order, t.plusDays(2)));
        assertEquals(12, original.getStock());
        assertEquals(2, b.getStock());
        assertEquals(50, replacement.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusDays(3)));
        assertEquals(12, original.getStock());
        assertEquals(2, b.getStock());
        assertEquals(50, replacement.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(3_000_000_008L, order.getSubtotal());
        assertEquals(9L, order.getDiscount());
        assertEquals(35000L, order.getShipping());
        assertEquals(3_000_034_999L, order.getTotal());
        List<CartItem> purchased = order.getItems();
        assertEquals(2, purchased.size());
        assertSame(original, purchased.get(0).getProduct());
        assertEquals(3, purchased.get(0).getQuantity());
        assertSame(b, purchased.get(1).getProduct());
        assertEquals(2, purchased.get(1).getQuantity());
        List<CartItem> remaining = cart.getItems();
        assertEquals(1, remaining.size());
        assertSame(replacement, remaining.get(0).getProduct());
        assertEquals(4, remaining.get(0).getQuantity());
    }
}
