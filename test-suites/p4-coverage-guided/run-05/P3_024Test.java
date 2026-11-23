package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_024Test {

    @Test
    void repeatedOrderIdAndCouponCreateIndependentOrdersWhileEnoughStockRemains() {
        Product product = new Product("P", "Book", 250_000, 500, 6);
        Cart cart = new Cart();
        cart.addItem(product, 2);
        LocalDateTime time = LocalDateTime.of(2026, 11, 23, 23, 59, 59, 999_999_999);
        Coupon coupon = new Coupon("F", CouponType.FIXED, 1, 500_000, 0, time.toLocalDate());
        OrderService service = new OrderService();

        Order first = service.placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        assertEquals(4, product.getStock());
        Order second = service.placeOrder("D", cart, coupon, Region.INNER_CITY, time);
        assertEquals(2, product.getStock());
        Order third = service.placeOrder("D", cart, null, Region.INNER_CITY, time);
        assertEquals(0, product.getStock());
        for (Order discounted : List.of(first, second)) {
            assertEquals(500_000L, discounted.getSubtotal());
            assertEquals(1L, discounted.getDiscount());
            assertEquals(15_000L, discounted.getShipping());
            assertEquals(514_999L, discounted.getTotal());
        }
        assertEquals(0L, third.getDiscount());
        assertEquals(0L, third.getShipping());
        assertEquals(500_000L, third.getTotal());
        service.pay(first);
        assertEquals(OrderStatus.PAID, first.getStatus());
        assertEquals(OrderStatus.PENDING, second.getStatus());
        assertEquals(OrderStatus.PENDING, third.getStatus());
        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D", cart, coupon, Region.INNER_CITY, time));
        assertEquals(0, product.getStock());
        assertEquals(1, cart.getItems().size());
        assertSame(product, cart.getItems().get(0).getProduct());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        cart.removeItem("P");
        for (Order order : List.of(first, second, third)) {
            assertEquals("D", order.getId());
            assertEquals(time, order.getCreatedAt());
            assertEquals(1, order.getItems().size());
            assertSame(product, order.getItems().get(0).getProduct());
            assertEquals(2, order.getItems().get(0).getQuantity());
        }
    }
}
