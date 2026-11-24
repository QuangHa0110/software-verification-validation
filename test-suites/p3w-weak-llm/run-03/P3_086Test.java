package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_086Test {

    @Test
    void orderServicePlaceOrderInsufficientStockCheckedBeforePricing() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p1 = new Product("P1", "Bút", 1000L, 10, 1);
        Product p2 = new Product("P2", "Vở", 2000L, 10, 2);
        cart.addItem(p1, 1);
        cart.addItem(p2, 2);

        p2.reduceStock(1);
        Coupon expiredCoupon = new Coupon("C1", CouponType.PERCENT, 10L, 0L, 0L, LocalDate.of(2020, 1, 1));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(InsufficientStockException.class, () ->
            service.placeOrder("D1", cart, expiredCoupon, Region.INNER_CITY, now));
        assertEquals(1, p1.getStock());
        assertEquals(1, p2.getStock());
    }
}
