package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_025Test {

    @Test
    void repeatedPlacementReusesIdCartAndCouponAndShipsBasedOnDiscountedGoods() {
        Product product = new Product("A", "A", 250000, 500, 6);
        Cart cart = new Cart();
        cart.addItem(product, 2);
        OrderService service = new OrderService();
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 123456789);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 500000, 0, time.toLocalDate());
        Order free = service.placeOrder("D", cart, null, Region.INNER_CITY, time);
        assertEquals(500000L, free.getTotal());
        assertEquals(0L, free.getDiscount());
        assertEquals(0L, free.getShipping());
        assertEquals(4, product.getStock());
        Order first = service.placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        assertEquals(2, product.getStock());
        service.pay(first);
        Order second = service.placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        assertEquals(0, product.getStock());
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.PENDING, second.getStatus());
        assertEquals(OrderStatus.PENDING, free.getStatus());
        for (Order discounted : List.of(first, second)) {
            assertEquals("D", discounted.getId());
            assertEquals(500000L, discounted.getSubtotal());
            assertEquals(1L, discounted.getDiscount());
            assertEquals(15000L, discounted.getShipping());
            assertEquals(514999L, discounted.getTotal());
            assertEquals(time, discounted.getCreatedAt());
        }
        assertEquals(1, cart.getItems().size());
        assertSame(product, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D", cart, coupon, Region.INNER_CITY, time));
        assertEquals(0, product.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(product, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }
}
