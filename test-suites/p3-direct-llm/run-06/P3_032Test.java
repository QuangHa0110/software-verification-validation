package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_032Test {

    @Test
    void lateCancellationUsesLongNetGoodsAndRestoresOriginalProductsAfterCartReplacement() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product a = new Product("A", "original", 1_000_000_000L, 200, 5);
        Product b = new Product("B", "original", 11, 100, 2);
        Product replacement = new Product("A", "replacement", 7, 50, 10);
        Cart cart = new Cart();
        cart.addItem(a, 3);
        cart.addItem(b, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 3, 0, 0, time.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.PROVINCE, time);
        service.pay(order);
        assertEquals(2, a.getStock());
        assertEquals(0, b.getStock());

        cart.addItem(replacement, 1);
        cart.removeItem("B");
        a.restoreStock(4);
        b.restoreStock(7);
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, null));
        assertThrows(IllegalArgumentException.class, () -> service.cancelOrder(order, time.minusNanos(1)));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(6, a.getStock());
        assertEquals(7, b.getStock());
        assertEquals(10, replacement.getStock());

        assertEquals(2_400_000_015L, service.cancelOrder(order, time.plusDays(2)));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, time.plusDays(3)));
        assertEquals(9, a.getStock());
        assertEquals(9, b.getStock());
        assertEquals(10, replacement.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(replacement, cart.getItems().get(0).getProduct());
        assertEquals(4, cart.getItems().get(0).getQuantity());
        assertEquals(2, order.getItems().size());
        assertSame(a, order.getItems().get(0).getProduct());
        assertEquals(3, order.getItems().get(0).getQuantity());
        assertSame(b, order.getItems().get(1).getProduct());
        assertEquals(2, order.getItems().get(1).getQuantity());
        assertEquals(3_000_000_022L, order.getSubtotal());
        assertEquals(3L, order.getDiscount());
        assertEquals(35_000L, order.getShipping());
        assertEquals(3_000_035_019L, order.getTotal());
    }
}
