package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_022Test {

    @Test
    void repeatedOrderIdAndCouponProduceSeparateOrdersAndCancellationRestoresOriginalProductsOnce() {
        Product a = new Product("A", "Original", 100, 4, 5);
        Product b = new Product("B", "B", 30, 2, 2);
        Product replacement = new Product("A", "Replacement", 999, 7, 10);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 1);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 7, 230, 1, t.toLocalDate());
        OrderService service = new OrderService();
        Order first = service.placeOrder("D", cart, coupon, Region.SUBURB, t);
        assertEquals(3, a.getStock());
        assertEquals(1, b.getStock());
        Order second = service.placeOrder("D", cart, coupon, Region.SUBURB, t.plusNanos(1));
        assertNotSame(first, second);
        assertEquals(1, a.getStock());
        assertEquals(0, b.getStock());
        service.pay(second);

        cart.addItem(replacement, 1);
        cart.removeItem("B");
        a.restoreStock(4);
        b.restoreStock(6);
        assertEquals(0L, service.cancelOrder(first, t.plusDays(10)));
        assertEquals(OrderStatus.CANCELLED, first.getStatus());
        assertEquals(OrderStatus.PAID, second.getStatus());
        assertEquals(7, a.getStock());
        assertEquals(7, b.getStock());
        assertEquals(10, replacement.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(first, t.plusDays(11)));
        assertEquals(7, a.getStock());
        assertEquals(7, b.getStock());
        assertEquals(10, replacement.getStock());
        assertEquals(25_223L, service.cancelOrder(second, t.plusHours(1)));
        assertEquals(9, a.getStock());
        assertEquals(8, b.getStock());
        assertEquals(10, replacement.getStock());

        for (Order order : List.of(first, second)) {
            List<CartItem> lines = order.getItems();
            assertEquals(2, lines.size());
            assertSame(a, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(1, lines.get(1).getQuantity());
            assertEquals("D", order.getId());
            assertEquals(230L, order.getSubtotal());
            assertEquals(7L, order.getDiscount());
            assertEquals(25_000L, order.getShipping());
            assertEquals(25_223L, order.getTotal());
            assertEquals(Region.SUBURB, order.getRegion());
            assertEquals(OrderStatus.CANCELLED, order.getStatus());
        }
        assertEquals(t, first.getCreatedAt());
        assertEquals(t.plusNanos(1), second.getCreatedAt());
        assertEquals(1, cart.getItems().size());
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(3, cart.getItems().get(0).getQuantity());
    }
}
