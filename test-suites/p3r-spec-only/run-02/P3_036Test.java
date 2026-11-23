package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_036Test {

    @Test
    void lateCancellationUsesLongArithmeticAndRestoresOriginalProductsAfterCartReplacement() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product original = new Product("A", "original", 1_000_000_000L, 100, 5);
        Product b = new Product("B", "B", 7, 150, 4);
        Product replacement = new Product(new String("A"), "replacement", 2, 10, 10);
        Cart cart = new Cart();
        cart.addItem(original, 3);
        cart.addItem(b, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, time.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.REMOTE, time);
        service.pay(order);
        assertEquals(2, original.getStock());
        assertEquals(2, b.getStock());

        cart.addItem(replacement, 1);
        cart.removeItem("B");
        original.restoreStock(5);
        b.reduceStock(1);
        assertEquals(2_400_000_010L, service.cancelOrder(order, time.plusDays(2)));
        assertEquals(10, original.getStock());
        assertEquals(3, b.getStock());
        assertEquals(10, replacement.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(3_000_000_014L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(55000L, order.getShipping());
        assertEquals(3_000_055_013L, order.getTotal());

        List<CartItem> ordered = order.getItems();
        assertEquals(2, ordered.size());
        assertSame(original, ordered.get(0).getProduct());
        assertEquals(3, ordered.get(0).getQuantity());
        assertSame(b, ordered.get(1).getProduct());
        assertEquals(2, ordered.get(1).getQuantity());
        List<CartItem> current = cart.getItems();
        assertEquals(1, current.size());
        assertSame(replacement, current.get(0).getProduct());
        assertEquals(4, current.get(0).getQuantity());
        assertEquals(8L, cart.getSubtotal());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time.plusDays(3)));
        assertEquals(10, original.getStock());
        assertEquals(3, b.getStock());
        assertEquals(10, replacement.getStock());
    }
}
