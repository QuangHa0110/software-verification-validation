package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_029Test {

    @Test
    void reusedOrderIdAndCouponCreateIndependentOrdersAndCancellationUsesOriginalProductRows() {
        Product a = new Product("A", "Original", 10, 2, 4);
        Product b = new Product("B", "Second", 7, 3, 6);
        Product replacement = new Product("A", "New", 90, 8, 20);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 345);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order first = service.placeOrder(" D ", cart, coupon, Region.SUBURB, t);
        assertEquals(2, a.getStock());
        assertEquals(3, b.getStock());
        Order second = service.placeOrder(" D ", cart, coupon, Region.SUBURB, t.plusNanos(1));
        assertEquals(0, a.getStock());
        assertEquals(0, b.getStock());
        service.pay(second);
        assertEquals(OrderStatus.PENDING, first.getStatus());
        assertEquals(t, first.getCreatedAt());
        assertEquals(t.plusNanos(1), second.getCreatedAt());

        cart.addItem(replacement, 1);
        cart.removeItem("B");
        a.restoreStock(8);
        b.restoreStock(1);
        assertEquals(0L, service.cancelOrder(first, t.plusDays(2)));
        assertEquals(OrderStatus.CANCELLED, first.getStatus());
        assertEquals(OrderStatus.PAID, second.getStatus());
        assertEquals(10, a.getStock());
        assertEquals(4, b.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(first, t.plusDays(3)));
        assertEquals(10, a.getStock());
        assertEquals(4, b.getStock());
        assertEquals(20, replacement.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(3, cart.getItems().get(0).getQuantity());

        for (Order order : List.of(first, second)) {
            assertEquals(" D ", order.getId());
            assertEquals(Region.SUBURB, order.getRegion());
            assertEquals(41L, order.getSubtotal());
            assertEquals(1L, order.getDiscount());
            assertEquals(25_000L, order.getShipping());
            assertEquals(25_040L, order.getTotal());
            List<CartItem> rows = order.getItems();
            assertEquals(2, rows.size());
            assertSame(a, rows.get(0).getProduct());
            assertEquals(2, rows.get(0).getQuantity());
            assertSame(b, rows.get(1).getProduct());
            assertEquals(3, rows.get(1).getQuantity());
        }
    }
}
