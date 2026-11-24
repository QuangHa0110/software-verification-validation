package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_035Test {

    @Test
    void lateCancellationRestoresOriginalProductsFromSnapshotOnTopOfCurrentStocks() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product a = new Product("A", "Original", 1_000_000_000L, 100, 5);
        Product b = new Product("B", "Second", 13, 50, 6);
        Product replacement = new Product("A", "Replacement", 7, 1, 10);
        Cart cart = new Cart();
        cart.addItem(a, 3);
        cart.addItem(b, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 2, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.PROVINCE, t);
        assertEquals(2, a.getStock());
        assertEquals(4, b.getStock());
        cart.addItem(replacement, 1);
        cart.removeItem("B");
        a.restoreStock(7);
        b.reduceStock(1);
        service.pay(order);

        assertEquals(2_400_000_019L, service.cancelOrder(order, t.plusDays(2)));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(12, a.getStock());
        assertEquals(5, b.getStock());
        assertEquals(10, replacement.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, t.plusDays(3)));
        assertEquals(12, a.getStock());
        assertEquals(5, b.getStock());
        assertEquals(10, replacement.getStock());

        List<CartItem> frozen = order.getItems();
        assertEquals(2, frozen.size());
        assertSame(a, frozen.get(0).getProduct());
        assertEquals(3, frozen.get(0).getQuantity());
        assertSame(b, frozen.get(1).getProduct());
        assertEquals(2, frozen.get(1).getQuantity());
        assertEquals(3_000_000_026L, order.getSubtotal());
        assertEquals(2L, order.getDiscount());
        assertEquals(35_000L, order.getShipping());
        assertEquals(3_000_035_024L, order.getTotal());
        assertEquals(1, cart.getItems().size());
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(28L, cart.getSubtotal());
    }
}
