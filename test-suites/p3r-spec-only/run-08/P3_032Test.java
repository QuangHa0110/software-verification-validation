package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_032Test {

    @Test
    void cancellingOneOfTwoOrdersRestoresOnlyItsFrozenLinesToTheirOriginalProducts() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product original = new Product("A", "Original", 1_000_000_000L, 11, 9);
        Product b = new Product("B", "Second", 7, 13, 6);
        Product replacement = new Product("A", "Replacement", 2, 17, 20);
        Cart cart = new Cart();
        cart.addItem(original, 3);
        cart.addItem(b, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order first = service.placeOrder("D", cart, coupon, Region.REMOTE, t);
        Order second = service.placeOrder("D", cart, coupon, Region.REMOTE, t);
        assertEquals(3, original.getStock());
        assertEquals(2, b.getStock());
        service.pay(first);

        cart.addItem(replacement, 1);
        cart.removeItem("B");
        original.restoreStock(10);
        b.reduceStock(1);
        assertEquals(2_400_000_010L, service.cancelOrder(first, t.plusHours(25)));
        assertEquals(16, original.getStock());
        assertEquals(3, b.getStock());
        assertEquals(20, replacement.getStock());
        assertEquals(OrderStatus.CANCELLED, first.getStatus());
        assertEquals(OrderStatus.PENDING, second.getStatus());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(first, t));
        assertEquals(16, original.getStock());
        assertEquals(3, b.getStock());
        assertEquals(20, replacement.getStock());

        for (Order order : List.of(first, second)) {
            List<CartItem> lines = order.getItems();
            assertEquals(2, lines.size());
            assertSame(original, lines.get(0).getProduct());
            assertEquals(3, lines.get(0).getQuantity());
            assertSame(b, lines.get(1).getProduct());
            assertEquals(2, lines.get(1).getQuantity());
            assertEquals(3_000_000_014L, order.getSubtotal());
            assertEquals(1L, order.getDiscount());
            assertEquals(55_000L, order.getShipping());
            assertEquals(3_000_055_013L, order.getTotal());
        }
        assertEquals(0L, service.cancelOrder(second, t));
        assertEquals(19, original.getStock());
        assertEquals(5, b.getStock());
        assertEquals(20, replacement.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(8L, cart.getSubtotal());
    }
}
