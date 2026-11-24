package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void repeatedOrderIdAndCouponCreateIndependentOrdersUntilStockRunsOut() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 7, 100, 1, t.toLocalDate());
        Product product = new Product("P", "Pen", 50, 1, 4);
        Cart cart = new Cart();
        cart.addItem(product, 2);
        OrderService service = new OrderService();
        Order first = service.placeOrder("D", cart, coupon, Region.SUBURB, t);
        assertEquals(2, product.getStock());
        service.pay(first);
        Order second = service.placeOrder("D", cart, coupon, Region.SUBURB, t.plusNanos(1));
        assertEquals(0, product.getStock());
        assertEquals("D", first.getId());
        assertEquals("D", second.getId());
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.PENDING, second.getStatus());
        assertEquals(t, first.getCreatedAt());
        assertEquals(t.plusNanos(1), second.getCreatedAt());
        for (Order order : List.of(first, second)) {
            assertEquals(7L, order.getDiscount());
            assertEquals(25_093L, order.getTotal());
            assertEquals(1, order.getItems().size());
            assertSame(product, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
        }
        assertThrows(InsufficientStockException.class,
                () -> service.placeOrder("D", cart, coupon, Region.SUBURB, t.plusNanos(2)));
        assertEquals(0, product.getStock());
        assertEquals(0L, service.cancelOrder(second, t.plusNanos(1)));
        assertEquals(2, product.getStock());
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(1, cart.getItems().size());
        assertSame(product, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
