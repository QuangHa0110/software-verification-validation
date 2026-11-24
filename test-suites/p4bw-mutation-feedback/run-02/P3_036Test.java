package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_036Test {

    @Test
    void orderServicePlaceOrderInsufficientStockBeforeCalculations() {
        OrderService service = new OrderService();
        Cart cart = new Cart();
        Product p = new Product("P1", "Sản phẩm", 10000L, 100, 2);
        cart.addItem(p, 2);
        p.reduceStock(1);

        Coupon expiredCoupon = new Coupon("C", CouponType.FIXED, 1000L, 0L, 0L, LocalDate.of(2026, 11, 20));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(InsufficientStockException.class, () -> service.placeOrder("D1", cart, expiredCoupon, Region.INNER_CITY, now));
        assertEquals(1, p.getStock());
    }
}
