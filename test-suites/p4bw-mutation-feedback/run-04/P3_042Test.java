package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_042Test {

    @Test
    void orderServicePlaceOrderSuccessDeductsStockAndCreatesOrder() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Item", 250000L, 500, 2);
        Cart cart = new Cart();
        cart.addItem(p, 2);

        Coupon coupon = new Coupon("FIXED1", CouponType.FIXED, 1L, 0L, 0L, LocalDate.of(2026, 11, 24));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Order order = service.placeOrder("D1", cart, coupon, Region.INNER_CITY, now);

        assertEquals("D1", order.getId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(500000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(514999L, order.getTotal());
        assertEquals(0, p.getStock());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
