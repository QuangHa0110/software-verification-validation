package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @Test
    void lateCancellationUsesLongArithmeticAndRestoresOriginalProductsAfterCartReplacementAndRemoval() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 765432100);
        Product original = new Product("A", "Original", 1_000_000_000L, 2, 4);
        Product second = new Product("B", "Second", 7, 3, 5);
        Product replacement = new Product("A", "Replacement", 13, 11, 8);
        Cart cart = new Cart();
        cart.addItem(original, 3);
        cart.addItem(second, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 3, 0, 1, time.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.PROVINCE, time);
        service.pay(order);
        assertEquals(1, original.getStock());
        assertEquals(3, second.getStock());

        cart.addItem(replacement, 1);
        cart.removeItem("B");
        original.restoreStock(6);
        second.reduceStock(1);
        assertEquals(2_400_000_008L, service.cancelOrder(order, time.plusDays(2)));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time.plusDays(3)));
        assertEquals(10, original.getStock());
        assertEquals(4, second.getStock());
        assertEquals(8, replacement.getStock());

        List<CartItem> ordered = order.getItems();
        assertEquals(2, ordered.size());
        assertSame(original, ordered.get(0).getProduct());
        assertEquals(3, ordered.get(0).getQuantity());
        assertSame(second, ordered.get(1).getProduct());
        assertEquals(2, ordered.get(1).getQuantity());
        assertEquals(3_000_000_014L, order.getSubtotal());
        assertEquals(3L, order.getDiscount());
        assertEquals(35_000L, order.getShipping());
        assertEquals(3_000_035_011L, order.getTotal());
        List<CartItem> current = cart.getItems();
        assertEquals(1, current.size());
        assertSame(replacement, current.get(0).getProduct());
        assertEquals(4, current.get(0).getQuantity());
        assertEquals(52L, cart.getSubtotal());
    }
}
