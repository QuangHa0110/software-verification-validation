package shop;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;
import java.util.*;

public class P3_040Test {

    @Test
    void orderServicePlaceOrderCouponFailurePreventsDeduction() {
        OrderService service = new OrderService();
        Product p = new Product("P1", "Item", 10000L, 100, 5);
        Cart cart = new Cart();
        cart.addItem(p, 1);

        Coupon expired = new Coupon("EXP", CouponType.PERCENT, 10L, 0L, 0L, LocalDate.of(2026, 11, 22));
        LocalDateTime now = LocalDateTime.of(2026, 11, 23, 8, 0);

        assertThrows(CouponExpiredException.class, () -> service.placeOrder("D1", cart, expired, Region.INNER_CITY, now));
        assertEquals(5, p.getStock());

        Coupon highMin = new Coupon("MIN", CouponType.PERCENT, 10L, 50000L, 0L, LocalDate.of(2026, 11, 24));
        assertThrows(CouponNotApplicableException.class, () -> service.placeOrder("D2", cart, highMin, Region.INNER_CITY, now));
        assertEquals(5, p.getStock());
    }
}
