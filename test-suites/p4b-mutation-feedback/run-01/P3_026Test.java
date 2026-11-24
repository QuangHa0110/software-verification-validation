package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_026Test {

    @Test
    void repeatedIdAndCouponProduceIndependentOrdersAndCancellationUsesOriginalProducts() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 123456789);
        Product a = new Product("A", "First", 20, 5, 4);
        Product b = new Product("B", "Second", 3, 1, 7);
        Product replacement = new Product("A", "Replacement", 99, 12, 20);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        cart.addItem(b, 3);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 49, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order first = service.placeOrder(" D ", cart, coupon, Region.SUBURB, t);
        assertEquals(2, a.getStock());
        assertEquals(4, b.getStock());
        Order second = service.placeOrder(" D ", cart, coupon, Region.SUBURB, t.plusNanos(1));
        assertNotSame(first, second);
        assertEquals(" D ", second.getId());
        assertEquals(t.plusNanos(1), second.getCreatedAt());
        assertEquals(1L, second.getDiscount());
        assertEquals(25_048L, second.getTotal());
        assertEquals(0, a.getStock());
        assertEquals(1, b.getStock());

        cart.addItem(replacement, 1);
        cart.removeItem("B");
        a.restoreStock(6);
        b.reduceStock(1);
        assertEquals(0L, service.cancelOrder(first, t.plusDays(3)));
        assertEquals(OrderStatus.CANCELLED, first.getStatus());
        assertEquals(OrderStatus.PENDING, second.getStatus());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(first, t));
        assertEquals(8, a.getStock());
        assertEquals(3, b.getStock());
        assertEquals(20, replacement.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(3, cart.getItems().get(0).getQuantity());
        assertEquals(List.of(a, b), first.getItems().stream().map(CartItem::getProduct).toList());
        assertEquals(List.of(2, 3), first.getItems().stream().map(CartItem::getQuantity).toList());
        assertEquals(49L, first.getSubtotal());
        assertEquals(1L, first.getDiscount());
        assertEquals(25_000L, first.getShipping());
        assertEquals(25_048L, first.getTotal());
        assertEquals(t, first.getCreatedAt());
        assertEquals(Region.SUBURB, first.getRegion());
    }
}
