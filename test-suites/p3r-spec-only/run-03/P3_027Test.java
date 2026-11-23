package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_027Test {

    @Test
    void duplicateOrderIdAndReusableCouponProduceIndependentOrdersWhileStockLasts() {
        LocalDateTime t = LocalDateTime.of(2026, 11, 23, 8, 0);
        Product p = new Product("P", "Pair", 250_000, 500, 6);
        Coupon coupon = new Coupon("ONE", CouponType.FIXED, 1, 500_000, 0, t.toLocalDate());
        Cart cart = new Cart();
        cart.addItem(p, 2);
        OrderService service = new OrderService();

        Order first = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        assertEquals(4, p.getStock());
        service.pay(first);
        Order second = service.placeOrder("D", cart, coupon, Region.INNER_CITY, t);
        assertEquals(2, p.getStock());
        Order third = service.placeOrder("D", cart, null, Region.INNER_CITY, t);
        assertEquals(0, p.getStock());
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.PENDING, second.getStatus());
        assertEquals(OrderStatus.PENDING, third.getStatus());
        for (Order discounted : List.of(first, second)) {
            assertEquals("D", discounted.getId());
            assertEquals(500_000L, discounted.getSubtotal());
            assertEquals(1L, discounted.getDiscount());
            assertEquals(15_000L, discounted.getShipping());
            assertEquals(514_999L, discounted.getTotal());
        }
        assertEquals(500_000L, third.getSubtotal());
        assertEquals(0L, third.getDiscount());
        assertEquals(0L, third.getShipping());
        assertEquals(500_000L, third.getTotal());
        assertThrows(InsufficientStockException.class,
            () -> service.placeOrder("D", cart, coupon, Region.INNER_CITY, t));
        assertEquals(0, p.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(p, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());

        cart.removeItem("P");
        for (Order order : List.of(first, second, third)) {
            assertEquals(1, order.getItems().size());
            assertSame(p, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
        }
    }
}
