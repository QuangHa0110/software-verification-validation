package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_035Test {

    @ParameterizedTest
    @CsvSource({
        "false,0,0,0",
        "false,25,0,0",
        "true,0,0,514999",
        "true,24,-1,514999",
        "true,24,0,514999",
        "true,24,1,399999",
        "true,24,1000000000,399999"
    })
    void cancellationRefundUsesExactNanosecondDeadlineAndPaidGoods(
            boolean paid, int hours, long nanos, long expectedRefund) {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 8, 0, 0, 123456789);
        Product a = new Product("A", "A", 250000, 500, 2);
        Cart cart = new Cart();
        cart.addItem(a, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 0, 0, time.toLocalDate());
        OrderService service = new OrderService();
        Order order = service.placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        if (paid) {
            service.pay(order);
        }
        assertEquals(0, a.getStock());
        a.restoreStock(3);
        LocalDateTime at = time.plusHours(hours).plusNanos(nanos);
        assertEquals(expectedRefund, service.cancelOrder(order, at));
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(5, a.getStock());
        assertEquals(500000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(514999L, order.getTotal());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertThrows(IllegalStateException.class, () -> service.cancelOrder(order, at));
        assertEquals(5, a.getStock());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
