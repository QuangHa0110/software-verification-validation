package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @ParameterizedTest
    @CsvSource({
        "false,0,0,0", "false,25,0,0",
        "true,0,0,514999", "true,24,-1,514999", "true,24,0,514999",
        "true,24,1,399999", "true,24,1000000000,399999"
    })
    void cancellationUsesNanosecondDeadlineAndRestoresStockExactlyOnce(boolean paid, int hours,
                                                                       long nanos, long expectedRefund) {
        Product p = new Product("A", "A", 250000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 123456789);
        Coupon coupon = new Coupon("C", CouponType.FIXED, 1, 0, 0, t.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        if (paid) {
            service.pay(order);
        }
        p.restoreStock(3);
        LocalDateTime when = t.plusHours(hours).plusNanos(nanos);
        assertEquals(expectedRefund, service.cancelOrder(order, when));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, p.getStock());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, when));
        assertEquals(5, p.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(500000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(514999L, order.getTotal());
        assertEquals(t, order.getCreatedAt());
        for (List<CartItem> lines : List.of(cart.getItems(), order.getItems())) {
            assertEquals(1, lines.size());
            assertSame(p, lines.get(0).getProduct());
            assertEquals(2, lines.get(0).getQuantity());
        }
    }
}
