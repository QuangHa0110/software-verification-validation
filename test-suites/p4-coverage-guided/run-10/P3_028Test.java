package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_028Test {

    @Test
    void lateCancellationUsesLongMoneyAndRestoresOriginalProductsAfterCartReplacement() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product a = new Product("A", "Original", 1_000_000_000L, 100, 5);
        Product b = new Product("B", "Second", 7, 200, 8);
        Product replacement = new Product("A", "Replacement", 1, 1, 10);
        Cart cart = new Cart();
        cart.addItem(a, 3);
        cart.addItem(b, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.PROVINCE, t);
        service.pay(order);
        assertEquals(2, a.getStock());
        assertEquals(6, b.getStock());
        cart.addItem(replacement, 1);
        cart.removeItem("B");
        a.restoreStock(7);
        b.reduceStock(1);

        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, t.minusNanos(1)));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(9, a.getStock());
        assertEquals(5, b.getStock());
        assertEquals(10, replacement.getStock());

        assertEquals(2_400_000_010L, service.cancelOrder(order, t.plusDays(2)));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(12, a.getStock());
        assertEquals(7, b.getStock());
        assertEquals(10, replacement.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusDays(3)));
        assertEquals(12, a.getStock());
        assertEquals(7, b.getStock());
        assertEquals(10, replacement.getStock());

        List<CartItem> lines = order.getItems();
        assertEquals(2, lines.size());
        assertSame(a, lines.get(0).getProduct());
        assertEquals(3, lines.get(0).getQuantity());
        assertSame(b, lines.get(1).getProduct());
        assertEquals(2, lines.get(1).getQuantity());
        assertEquals(3_000_000_014L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(35_000L, order.getShipping());
        assertEquals(3_000_035_013L, order.getTotal());
        assertEquals(1, cart.getItems().size());
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(4L, cart.getSubtotal());
    }
}
