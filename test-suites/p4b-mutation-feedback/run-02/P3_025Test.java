package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void repeatedCheckoutReusesIdCartAndCouponAndUsesPostDiscountShippingThreshold() {
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 9, 30);
        Product product = new Product("A", "A", 250_000, 500, 6);
        Cart cart = new Cart();
        cart.addItem(product, 2);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 500_000, 0, time.toLocalDate());
        OrderService service = new OrderService();

        Order freeShipping = service.placeOrder("D", cart, null, Region.INNER_CITY, time);
        assertEquals(4, product.getStock());
        assertEquals(500_000L, freeShipping.getSubtotal());
        assertEquals(0L, freeShipping.getDiscount());
        assertEquals(0L, freeShipping.getShipping());
        assertEquals(500_000L, freeShipping.getTotal());

        Order firstDiscounted = service.placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        assertEquals(2, product.getStock());
        service.pay(firstDiscounted);
        Order secondDiscounted = service.placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        assertEquals(0, product.getStock());
        assertEquals(OrderStatus.PENDING, freeShipping.getStatus());
        assertEquals(OrderStatus.PAID, firstDiscounted.getStatus());
        assertEquals(OrderStatus.PENDING, secondDiscounted.getStatus());
        for (Order order : List.of(firstDiscounted, secondDiscounted)) {
            assertEquals("D", order.getId());
            assertEquals(500_000L, order.getSubtotal());
            assertEquals(1L, order.getDiscount());
            assertEquals(15_000L, order.getShipping());
            assertEquals(514_999L, order.getTotal());
            assertEquals(1, order.getItems().size());
            assertSame(product, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
        }
        assertThrows(InsufficientStockException.class,
            () -> service.placeOrder("D", cart, coupon, Region.INNER_CITY, time));
        assertEquals(0, product.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(product, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
