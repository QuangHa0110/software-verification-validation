package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_038Test {

    @Test
    void orderServicePlaceOrderSuccessDeductsStockAndCreatesPendingOrder() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P", "Sản phẩm", 250000L, 500, 2);
        cart.addItem(p, 2);

        Coupon coupon = new Coupon("C", CouponType.FIXED, 1L, 0L, 0L, LocalDate.of(2026, 11, 25));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Order order = service.placeOrder("D1", cart, coupon, Region.INNER_CITY, now);

        assertEquals(0, p.getStock());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(500000L, order.getSubtotal());
        assertEquals(1L, order.getDiscount());
        assertEquals(15000L, order.getShipping());
        assertEquals(514999L, order.getTotal());
        assertEquals(now, order.getCreatedAt());
        assertEquals(Region.INNER_CITY, order.getRegion());

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
