package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_053Test {

    @Test
    void testPlaceOrderStockCheckAtomicBeforeDiscounts() {
        OrderService service = new OrderService();
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        Product p1 = new Product("P1", "Pen", 10L, 100, 1);
        Product p2 = new Product("P2", "Book", 20L, 100, 2);
        Cart cart = new Cart();
        cart.addItem(p1, 1);
        cart.addItem(p2, 2);

        p2.reduceStock(1); // p2 stock is now 1, but cart has 2
        Coupon expiredCoupon = new Coupon("C", CouponType.FIXED, 5L, 0L, 0L, LocalDate.of(2026, 11, 20));

        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D1", cart, expiredCoupon, Region.INNER_CITY, now));
        assertEquals(1, p1.getStock());
        assertEquals(1, p2.getStock());
    }
}
